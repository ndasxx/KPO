package pos.application.port;

import java.util.Map;

public record GradeSheetRow(
        String studentId,
        String studentName,
        Map<String, Integer> scores,
        double total
) {
}
