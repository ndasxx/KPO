package pos.infrastructure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pos.application.PosFacade;
import pos.application.port.GradeSheet;
import pos.application.port.GradeSheetRow;
import pos.application.port.ImportedGrade;
import pos.application.port.ImportedSheet;
import pos.domain.evaluation.EvaluationFactory;
import pos.domain.rubric.Criterion;
import pos.domain.service.TotalCalculator;
import pos.domain.service.WeightedTotalPolicy;
import pos.domain.student.Student;
import pos.infrastructure.exporter.CsvGradeSheetExporter;
import pos.infrastructure.exporter.JsonGradeSheetExporter;
import pos.infrastructure.importer.CsvGradeSheetImporter;
import pos.infrastructure.importer.JsonGradeSheetImporter;
import pos.infrastructure.memory.InMemoryEvaluationRepository;
import pos.infrastructure.memory.InMemoryRubricRepository;
import pos.infrastructure.memory.InMemoryStudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GradeSheetImporterTest {

    @TempDir
    Path dir;

    private GradeSheet sampleSheet() {
        Map<String, Integer> scores = new LinkedHashMap<>();
        scores.put("ДЗ", 9);
        scores.put("Проект", 18);
        return new GradeSheet(
                List.of(new Criterion("ДЗ", 10, 2), new Criterion("Проект", 20, 3)),
                List.of(new GradeSheetRow("S-1", "Иван Петров", scores, 7.33),
                        new GradeSheetRow("S-2", "Мария Сидорова", Map.of(), 0.0)));
    }

    @Test
    void csvAndJson_importSameSheet_fromExportedFile() throws IOException {
        GradeSheet sheet = sampleSheet();
        Path csv = new CsvGradeSheetExporter().export(sheet, dir);
        Path json = new JsonGradeSheetExporter().export(sheet, dir);

        ImportedSheet fromCsv = new CsvGradeSheetImporter().importFrom(csv);
        ImportedSheet fromJson = new JsonGradeSheetImporter().importFrom(json);

        assertEquals(fromCsv, fromJson);
        assertEquals(List.of(new Criterion("ДЗ", 10, 2), new Criterion("Проект", 20, 3)),
                fromCsv.criteria());
        assertEquals(List.of(new Student("S-1", "Иван Петров"),
                        new Student("S-2", "Мария Сидорова")),
                fromCsv.students());
        assertEquals(List.of(
                new ImportedGrade("S-1", "ДЗ", 9),
                new ImportedGrade("S-1", "Проект", 18)), fromCsv.grades());
    }

    @Test
    void import_missingFile_rejected() {
        var importer = new CsvGradeSheetImporter();

        assertThrows(IllegalArgumentException.class,
                () -> importer.importFrom(dir.resolve("missing.csv")));
    }

    @Test
    void import_emptyFile_rejected() throws IOException {
        Path empty = dir.resolve("empty.csv");
        Files.writeString(empty, "");

        var importer = new CsvGradeSheetImporter();
        assertThrows(IllegalArgumentException.class, () -> importer.importFrom(empty));
    }

    @Test
    void import_brokenJson_rejected() throws IOException {
        Path broken = dir.resolve("broken.json");
        Files.writeString(broken, "{ not json");

        var importer = new JsonGradeSheetImporter();
        assertThrows(IllegalArgumentException.class, () -> importer.importFrom(broken));
    }

    @Test
    void import_recreatesStudentsAndCriteria_inEmptyApp() throws IOException {
        // заполняем приложение и выгружаем ведомость
        PosFacade source = newFacade();
        assertNull(source.attemptAddStudent("S-1", "Иван Петров"));
        assertNull(source.attemptAddCriterion("ДЗ", 10, 2));
        assertNull(source.attemptAddCriterion("Проект", 20, 3));
        assertNull(source.attemptGrade("S-1", "ДЗ", 9, ""));
        assertNull(source.attemptGrade("S-1", "Проект", 18, ""));
        double expectedTotal = source.totalOf("S-1");
        Path file = source.exportGradeSheet("csv", dir);

        // импортируем в пустое приложение: студент и критерии досоздаются сами
        PosFacade target = newFacade();
        String report = target.importGradeSheet(file);

        assertTrue(report.startsWith("Импортировано 2 из 2"), report);
        assertTrue(report.contains("студент S-1"), report);
        assertTrue(report.contains("критерий 'Проект' (max 20, вес 3.0)"), report);
        assertEquals(1, target.students().size());
        assertEquals(2, target.rubric().all().size());
        assertEquals(expectedTotal, target.totalOf("S-1"), 0.001);
    }

    private PosFacade newFacade() {
        return new PosFacade(
                new InMemoryStudentRepository(),
                new InMemoryRubricRepository(),
                new InMemoryEvaluationRepository(),
                new EvaluationFactory(),
                new TotalCalculator(new WeightedTotalPolicy()),
                List.of(new CsvGradeSheetExporter(), new JsonGradeSheetExporter()),
                List.of(new CsvGradeSheetImporter(), new JsonGradeSheetImporter()));
    }
}
