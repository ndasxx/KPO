package pos.application.port;

public record ImportedGrade(
        String studentId,
        String criterionName,
        int score
) {
}
