package pos.infrastructure.importer;

import pos.application.port.ImportedGrade;
import pos.application.port.ImportedSheet;
import pos.domain.rubric.Criterion;
import pos.domain.student.Student;
import pos.infrastructure.json.SimpleJson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonGradeSheetImporter extends AbstractGradeSheetImporter {

    @Override
    public String format() {
        return "json";
    }

    @Override
    protected ImportedSheet parse(String content) {
        Object root = SimpleJson.parse(content);
        if (!(root instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("Ожидался JSON-объект ведомости");
        }

        List<Criterion> criteria = new ArrayList<>();
        if (map.get("criteria") instanceof List<?> criteriaList) {
            for (Object item : criteriaList) {
                if (item instanceof Map<?, ?> criterionMap) {
                    criteria.add(new Criterion(
                            String.valueOf(criterionMap.get("name")),
                            ((Number) criterionMap.get("maxScore")).intValue(),
                            ((Number) criterionMap.get("weight")).doubleValue()));
                }
            }
        }

        Object rows = map.get("rows");
        if (!(rows instanceof List<?> list)) {
            throw new IllegalArgumentException("В JSON нет массива rows");
        }

        List<Student> students = new ArrayList<>();
        List<ImportedGrade> grades = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> row)) {
                continue;
            }
            Object studentId = row.get("studentId");
            if (studentId == null) {
                continue;
            }
            Object studentName = row.get("studentName");
            students.add(new Student(String.valueOf(studentId),
                    studentName == null ? String.valueOf(studentId)
                            : String.valueOf(studentName)));

            if (!(row.get("scores") instanceof Map<?, ?> scoresMap)) {
                continue;
            }
            for (Map.Entry<?, ?> entry : scoresMap.entrySet()) {
                if (!(entry.getValue() instanceof Number number)) {
                    throw new IllegalArgumentException(
                            "Некорректный балл у студента " + studentId);
                }
                grades.add(new ImportedGrade(String.valueOf(studentId),
                        String.valueOf(entry.getKey()), number.intValue()));
            }
        }
        return new ImportedSheet(criteria, students, grades);
    }
}
