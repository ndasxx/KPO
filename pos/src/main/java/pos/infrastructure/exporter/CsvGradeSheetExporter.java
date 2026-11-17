package pos.infrastructure.exporter;

import pos.application.port.GradeSheet;
import pos.application.port.GradeSheetRow;
import pos.application.port.IGradeSheetExporter;
import pos.domain.rubric.Criterion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.StringJoiner;


public class CsvGradeSheetExporter implements IGradeSheetExporter {

    @Override
    public String format() {
        return "csv";
    }

    @Override
    public Path export(GradeSheet sheet, Path directory) throws IOException {
        Files.createDirectories(directory);
        Path file = directory.resolve("gradesheet.csv");

        StringBuilder sb = new StringBuilder();
        for (Criterion criterion : sheet.criteria()) {
            sb.append("#criterion;").append(criterion.name())
                    .append(";").append(criterion.maxScore())
                    .append(";").append(formatWeight(criterion.weight()))
                    .append("\n");
        }

        StringJoiner header = new StringJoiner(";");
        header.add("studentId").add("studentName");
        sheet.criteria().forEach(c -> header.add(c.name()));
        header.add("total");
        sb.append(header).append("\n");

        for (GradeSheetRow row : sheet.rows()) {
            StringJoiner line = new StringJoiner(";");
            line.add(row.studentId()).add(row.studentName());
            for (Criterion criterion : sheet.criteria()) {
                Integer score = row.scores().get(criterion.name());
                line.add(score == null ? " " : score.toString());
            }
            line.add(String.format(Locale.ROOT, "%.2f", row.total()));
            sb.append(line).append("\n");
        }

        Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
        return file;
    }

    private static String formatWeight(double weight) {
        return weight == Math.floor(weight)
                ? String.valueOf((long) weight) : String.valueOf(weight);
    }
}
