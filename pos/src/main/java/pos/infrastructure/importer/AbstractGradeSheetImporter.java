package pos.infrastructure.importer;

import pos.application.port.IGradeSheetImporter;
import pos.application.port.ImportedSheet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractGradeSheetImporter implements IGradeSheetImporter {

    @Override
    public final ImportedSheet importFrom(Path file) throws IOException {
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Файл не найден: " + file);
        }
        String content = Files.readString(file, StandardCharsets.UTF_8);
        ImportedSheet sheet = parse(content);
        if (sheet.grades().isEmpty()) {
            throw new IllegalArgumentException("В файле нет ни одной оценки");
        }
        return sheet;
    }

    protected abstract ImportedSheet parse(String content);
}
