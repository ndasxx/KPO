package pos.domain.student;

public record Student(
        String studentId,
        String name
) {

    public Student {
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("ID студента обязателен");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя студента обязательно");
        }
    }
}
