package pos.infrastructure.importer;

import pos.application.port.ImportedGrade;
import pos.application.port.ImportedSheet;
import pos.domain.rubric.Criterion;
import pos.domain.student.Student;

import java.util.ArrayList;
import java.util.List;

public class CsvGradeSheetImporter extends AbstractGradeSheetImporter {

    @Override
    public String format() {
        return "csv";
    }

    @Override
    protected ImportedSheet parse(String content) {
        List<Criterion> criteria = new ArrayList<>();
        List<Student> students = new ArrayList<>();
        List<ImportedGrade> grades = new ArrayList<>();

        String[] lines = content.split("\\R");
        int headerIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("#criterion;")) {
                String[] parts = line.split(";", -1);
                if (parts.length >= 4) {
                    try {
                        criteria.add(new Criterion(parts[1].trim(),
                                Integer.parseInt(parts[2].trim()),
                                Double.parseDouble(parts[3].trim().replace(',', '.'))));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                "Некорректные параметры критерия в строке " + (i + 1));
                    }
                }
            } else if (line.startsWith("studentId;")) {
                headerIndex = i;
                break;
            }
        }
        if (headerIndex < 0) {
            throw new IllegalArgumentException("В CSV нет строки заголовка studentId;...");
        }

        String[] header = lines[headerIndex].split(";", -1);
        for (int i = headerIndex + 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] cells = line.split(";", -1);
            String studentId = cells[0].trim();
            String studentName = cells.length > 1 ? cells[1].trim() : studentId;
            students.add(new Student(studentId, studentName));
            for (int col = 2; col < header.length - 1 && col < cells.length; col++) {
                String value = cells[col].trim();
                if (value.isEmpty()) {
                    continue;
                }
                try {
                    grades.add(new ImportedGrade(
                            studentId, header[col].trim(), Integer.parseInt(value)));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Некорректный балл '" + value + "' в строке " + (i + 1));
                }
            }
        }
        return new ImportedSheet(criteria, students, grades);
    }
}
