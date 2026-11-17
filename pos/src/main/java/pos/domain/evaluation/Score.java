package pos.domain.evaluation;

public record Score(
        int value,
        int maxValue
) {

    public Score {
        if (maxValue < 1) {
            throw new IllegalArgumentException("Максимальный балл должен быть >= 1");
        }
        if (value < 0 || value > maxValue) {
            throw new IllegalArgumentException(
                    "Балл " + value + " вне диапазона критерия [0, " + maxValue + "]");
        }
    }
}
