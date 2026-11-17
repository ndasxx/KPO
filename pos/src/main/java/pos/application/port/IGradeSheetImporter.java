package pos.application.port;

import java.io.IOException;
import java.nio.file.Path;

/**
 * порт: импорт ведомости
 */
public interface IGradeSheetImporter {

    String format();

    ImportedSheet importFrom(Path file) throws IOException;
}
