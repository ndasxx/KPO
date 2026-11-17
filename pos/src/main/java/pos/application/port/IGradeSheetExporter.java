package pos.application.port;

import java.io.IOException;
import java.nio.file.Path;

/**
 * порт: выгрузка ведомости
 */
public interface IGradeSheetExporter {

    String format();

    Path export(GradeSheet sheet, Path directory) throws IOException;
}
