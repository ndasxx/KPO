package pos.domain.evaluation;

import java.time.LocalDate;

public record Evaluation(
        String studentId,
        String criterionName,
        Score score,
        LocalDate date,
        String comment
) {

    public Evaluation {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("ID студента обязателен");
        }
        if (criterionName == null || criterionName.isBlank()) {
            throw new IllegalArgumentException("Критерий обязателен");
        }
        if (score == null) {
            throw new IllegalArgumentException("Балл обязателен");
        }
        if (date == null) {
            date = LocalDate.now();
        }
        if (comment == null) {
            comment = "";
        }
    }
}
