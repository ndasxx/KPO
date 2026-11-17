package pos.domain.rubric;

public record Criterion(
        String name,
        int maxScore,
        double weight
) {

    public Criterion {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название критерия обязательно");
        }
        if (maxScore < 1) {
            throw new IllegalArgumentException("Максимальный балл должен быть >= 1");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Вес критерия должен быть положительным");
        }
    }
}
