package pos.application;

import pos.application.port.GradeSheet;
import pos.application.port.GradeSheetRow;
import pos.application.port.IGradeSheetExporter;
import pos.application.port.IGradeSheetImporter;
import pos.application.port.ImportedGrade;
import pos.application.port.ImportedSheet;
import pos.domain.evaluation.Evaluation;
import pos.domain.evaluation.EvaluationFactory;
import pos.domain.repository.EvaluationRepository;
import pos.domain.repository.RubricRepository;
import pos.domain.repository.StudentRepository;
import pos.domain.rubric.Criterion;
import pos.domain.rubric.Rubric;
import pos.domain.service.TotalCalculator;
import pos.domain.student.Student;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PosFacade {

    private final StudentRepository students;
    private final RubricRepository rubrics;
    private final EvaluationRepository evaluations;
    private final EvaluationFactory evaluationFactory;
    private final TotalCalculator totalCalculator;
    private final Map<String, IGradeSheetExporter> exporters = new LinkedHashMap<>();
    private final Map<String, IGradeSheetImporter> importers = new LinkedHashMap<>();

    public PosFacade(StudentRepository students,
                     RubricRepository rubrics,
                     EvaluationRepository evaluations,
                     EvaluationFactory evaluationFactory,
                     TotalCalculator totalCalculator,
                     List<IGradeSheetExporter> exporters,
                     List<IGradeSheetImporter> importers) {
        this.students = students;
        this.rubrics = rubrics;
        this.evaluations = evaluations;
        this.evaluationFactory = evaluationFactory;
        this.totalCalculator = totalCalculator;
        exporters.forEach(e -> this.exporters.put(e.format(), e));
        importers.forEach(i -> this.importers.put(i.format(), i));
    }

    public String attemptAddStudent(String studentId, String name) {
        try {
            Student student = new Student(studentId, name);
            if (students.findById(student.studentId()).isPresent()) {
                return "Студент с ID " + student.studentId() + " уже есть";
            }
            students.save(student);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String attemptAddCriterion(String name, int maxScore, double weight) {
        try {
            Rubric rubric = rubrics.load();
            rubric.add(new Criterion(name, maxScore, weight));
            rubrics.save(rubric);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String attemptGrade(String studentId, String criterionName,
                               int score, String comment) {
        if (students.findById(studentId).isEmpty()) {
            return "Студента " + studentId + " нет в списке";
        }
        try {
            Evaluation evaluation = evaluationFactory.create(
                    studentId, criterionName, score, comment, rubrics.load());
            evaluations.save(evaluation);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String attemptRemoveGrade(String studentId, String criterionName) {
        boolean removed = evaluations.delete(studentId, criterionName);
        return removed ? null
                : "Оценки студента " + studentId + " по критерию '" + criterionName + "' нет";
    }

    public List<Student> students() {
        return students.findAll();
    }

    public Rubric rubric() {
        return rubrics.load();
    }

    public List<Evaluation> evaluationsOf(String studentId) {
        return evaluations.findByStudent(studentId);
    }

    public String policyName() {
        return totalCalculator.policyName();
    }

    public double totalOf(String studentId) {
        return totalCalculator.totalFor(studentId, rubrics.load(), evaluations.findAll());
    }

    public Path exportGradeSheet(String format, Path directory) throws IOException {
        IGradeSheetExporter exporter = exporters.get(format);
        if (exporter == null) {
            throw new IllegalArgumentException(
                    "Неизвестный формат: " + format + " (есть: " + exporters.keySet() + ")");
        }
        return exporter.export(buildGradeSheet(), directory);
    }

    public String importGradeSheet(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        IGradeSheetImporter importer = null;
        for (var entry : importers.entrySet()) {
            if (fileName.endsWith("." + entry.getKey())) {
                importer = entry.getValue();
                break;
            }
        }
        if (importer == null) {
            return "Поддерживаются только форматы: " + importers.keySet();
        }

        ImportedSheet sheet;
        try {
            sheet = importer.importFrom(file);
        } catch (IOException | IllegalArgumentException e) {
            return "Не удалось прочитать файл: " + e.getMessage();
        }

        List<String> added = new ArrayList<>();
        for (Criterion criterion : sheet.criteria()) {
            if (rubric().find(criterion.name()).isEmpty()
                    && attemptAddCriterion(criterion.name(),
                            criterion.maxScore(), criterion.weight()) == null) {
                added.add("критерий '" + criterion.name() + "' (max "
                        + criterion.maxScore() + ", вес " + criterion.weight() + ")");
            }
        }
        for (Student student : sheet.students()) {
            if (students.findById(student.studentId()).isEmpty()
                    && attemptAddStudent(student.studentId(), student.name()) == null) {
                added.add("студент " + student.studentId() + " " + student.name());
            }
        }
        for (ImportedGrade grade : sheet.grades()) {
            if (rubric().find(grade.criterionName()).isEmpty()
                    && attemptAddCriterion(grade.criterionName(), 10, 1) == null) {
                added.add("критерий '" + grade.criterionName()
                        + "' (по умолчанию: max 10, вес 1)");
            }
        }

        int applied = 0;
        List<String> errors = new ArrayList<>();
        for (ImportedGrade grade : sheet.grades()) {
            String reason = attemptGrade(
                    grade.studentId(), grade.criterionName(), grade.score(), "импорт");
            if (reason == null) {
                applied++;
            } else {
                errors.add(grade.studentId() + "/" + grade.criterionName() + ": " + reason);
            }
        }

        String summary = "Импортировано " + applied + " из " + sheet.grades().size() + " оценок";
        if (!added.isEmpty()) {
            summary += ". Добавлено: " + String.join("; ", added);
        }
        return errors.isEmpty() ? summary : summary + ". Отклонено: " + String.join("; ", errors);
    }

    public void clearAll() {
        students.clear();
        evaluations.clear();
        Rubric rubric = rubrics.load();
        rubric.clear();
        rubrics.save(rubric);
    }

    private GradeSheet buildGradeSheet() {
        Rubric rubric = rubrics.load();
        List<Criterion> criteria = rubric.all();

        List<GradeSheetRow> rows = new ArrayList<>();
        for (Student student : students.findAll()) {
            Map<String, Integer> scores = new LinkedHashMap<>();
            for (Evaluation e : evaluations.findByStudent(student.studentId())) {
                scores.put(e.criterionName(), e.score().value());
            }
            double total = totalOf(student.studentId());
            rows.add(new GradeSheetRow(student.studentId(), student.name(),
                    scores, Math.round(total * 100.0) / 100.0));
        }
        return new GradeSheet(criteria, rows);
    }
}
