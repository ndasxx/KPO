package pos.infrastructure.exporter;

import pos.application.port.GradeSheet;
import pos.application.port.GradeSheetRow;
import pos.application.port.IGradeSheetExporter;
import pos.domain.rubric.Criterion;
import pos.infrastructure.json.SimpleJson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonGradeSheetExporter implements IGradeSheetExporter {

    @Override
    public String format() {
        return "json";
    }

    @Override
    public Path export(GradeSheet sheet, Path directory) throws IOException {
        Files.createDirectories(directory);
        Path file = directory.resolve("gradesheet.json");

        List<Object> criteria = new ArrayList<>();
        for (Criterion criterion : sheet.criteria()) {
            Map<String, Object> obj = new LinkedHashMap<>();
            obj.put("name", criterion.name());
            obj.put("maxScore", criterion.maxScore());
            obj.put("weight", criterion.weight());
            criteria.add(obj);
        }

        List<Object> rows = new ArrayList<>();
        for (GradeSheetRow row : sheet.rows()) {
            Map<String, Object> obj = new LinkedHashMap<>();
            obj.put("studentId", row.studentId());
            obj.put("studentName", row.studentName());
            obj.put("scores", new LinkedHashMap<>(row.scores()));
            obj.put("total", row.total());
            rows.add(obj);
        }

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("criteria", criteria);
        root.put("rows", rows);

        Files.writeString(file, SimpleJson.write(root), StandardCharsets.UTF_8);
        return file;
    }
}
