package pos.ui;

import pos.application.PosFacade;
import pos.domain.evaluation.Evaluation;
import pos.domain.rubric.Criterion;
import pos.domain.student.Student;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private final PosFacade pos;
    private final DemoScenarios demo;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(PosFacade pos, DemoScenarios demo) {
        this.pos = pos;
        this.demo = demo;
    }

    public void run() {
        demo.run();
        pos.clearAll();
        printDemoFinishedBanner();
        interactiveLoop();
    }

    private void printDemoFinishedBanner() {
        System.out.println("""
        ========================================
          Демо завершено, данные очищены - начните с нуля.
        ========================================
        """);
    }

    private void interactiveLoop() {
        while (true) {
            printMenu();
            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> addStudentInteractive();
                    case 2 -> addCriterionInteractive();
                    case 3 -> showStudents();
                    case 4 -> showRubric();
                    case 5 -> gradeInteractive();
                    case 6 -> removeGradeInteractive();
                    case 7 -> showStudentEvaluations();
                    case 8 -> showTotals();
                    case 9 -> exportInteractive();
                    case 10 -> importInteractive();
                    case 0 -> {
                        System.out.println("До встречи!");
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.print("""

        === ПОС ===
        1. Добавить студента
        2. Добавить критерий в рубрикатор
        3. Список студентов
        4. Рубрикатор
        5. Выставить/исправить оценку
        6. Отменить оценку
        7. Оценки студента
        8. Итоги по всем студентам
        9. Экспорт ведомости (csv/json)
        10. Импорт ведомости (csv/json)
        0. Выход
        > """);
    }

    private void addStudentInteractive() {
        System.out.print("ID студента: ");
        String id = scanner.nextLine().trim();
        System.out.print("Имя студента: ");
        String name = scanner.nextLine().trim();

        String reason = pos.attemptAddStudent(id, name);
        System.out.println(reason == null
                ? "[OK] Студент добавлен." : "[FAIL] " + reason);
    }

    private void addCriterionInteractive() {
        System.out.print("Название критерия: ");
        String name = scanner.nextLine().trim();
        int maxScore = readPositiveInt("Максимальный балл: ");
        double weight = readPositiveDouble("Вес критерия: ");

        String reason = pos.attemptAddCriterion(name, maxScore, weight);
        System.out.println(reason == null
                ? "[OK] Критерий добавлен." : "[FAIL] " + reason);
    }

    private void showStudents() {
        List<Student> students = pos.students();
        if (students.isEmpty()) {
            System.out.println("Студентов пока нет.");
            return;
        }
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.printf("  %d. %-8s %s%n", i + 1, s.studentId(), s.name());
        }
    }

    private void showRubric() {
        List<Criterion> criteria = pos.rubric().all();
        if (criteria.isEmpty()) {
            System.out.println("Рубрикатор пуст.");
            return;
        }
        for (int i = 0; i < criteria.size(); i++) {
            Criterion c = criteria.get(i);
            System.out.printf("  %d. %-20s max %d, вес %s%n",
                    i + 1, c.name(), c.maxScore(), formatWeight(c.weight()));
        }
    }

    private String askCriterion() {
        List<Criterion> criteria = pos.rubric().all();
        if (criteria.isEmpty()) {
            System.out.println("Рубрикатор пуст - сначала добавьте критерии.");
            return null;
        }
        showRubric();
        int choice = readIntInRange("Номер критерия: ", 1, criteria.size());
        return criteria.get(choice - 1).name();
    }

    private String askStudentId() {
        List<Student> students = pos.students();
        if (students.isEmpty()) {
            System.out.println("Студентов пока нет - сначала добавьте студентов.");
            return null;
        }
        showStudents();
        int choice = readIntInRange("Номер студента: ", 1, students.size());
        return students.get(choice - 1).studentId();
    }

    private void gradeInteractive() {
        String studentId = askStudentId();
        if (studentId == null) {
            return;
        }
        String criterion = askCriterion();
        if (criterion == null) {
            return;
        }
        int score = readInt("Балл: ");
        System.out.print("Комментарий (можно пусто): ");
        String comment = scanner.nextLine().trim();

        String reason = pos.attemptGrade(studentId, criterion, score, comment);
        System.out.println(reason == null
                ? "[OK] Оценка выставлена." : "[FAIL] " + reason);
    }

    private void removeGradeInteractive() {
        String studentId = askStudentId();
        if (studentId == null) {
            return;
        }
        String criterion = askCriterion();
        if (criterion == null) {
            return;
        }

        String reason = pos.attemptRemoveGrade(studentId, criterion);
        System.out.println(reason == null
                ? "[OK] Оценка отменена." : "[FAIL] " + reason);
    }

    private void showStudentEvaluations() {
        String studentId = askStudentId();
        if (studentId == null) {
            return;
        }

        List<Evaluation> evaluations = pos.evaluationsOf(studentId);
        if (evaluations.isEmpty()) {
            System.out.println("Оценок нет.");
            return;
        }
        for (Evaluation e : evaluations) {
            System.out.printf("  %-20s %d/%d  (%s)%s%n",
                    e.criterionName(), e.score().value(), e.score().maxValue(),
                    e.date(),
                    e.comment().isBlank() ? "" : " — " + e.comment());
        }
        System.out.printf("Итог (%s): %.2f%n", pos.policyName(), pos.totalOf(studentId));
    }

    private void showTotals() {
        List<Student> students = pos.students();
        if (students.isEmpty()) {
            System.out.println("Студентов пока нет.");
            return;
        }
        System.out.println("Правило итога: " + pos.policyName());
        for (Student s : students) {
            System.out.printf("  %-8s %-20s итог: %.2f%n",
                    s.studentId(), s.name(), pos.totalOf(s.studentId()));
        }
    }

    private void exportInteractive() {
        System.out.print("Формат (csv/json): ");
        String format = scanner.nextLine().trim().toLowerCase();
        try {
            Path file = pos.exportGradeSheet(format, Path.of("exports"));
            System.out.println("[OK] Ведомость выгружена: " + file.toAbsolutePath());
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("[FAIL] " + e.getMessage());
        }
    }

    private void importInteractive() {
        System.out.print("Путь к файлу ведомости: ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            System.out.println("Путь не указан.");
            return;
        }
        System.out.println(pos.importGradeSheet(Path.of(path)));
    }

    private int readInt() {
        String line = scanner.nextLine().trim();
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число.");
            }
        }
    }

    private int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Введите число от " + min + " до " + max + ".");
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value >= 1) {
                return value;
            }
            System.out.println("Число должно быть >= 1.");
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
                if (value > 0) {
                    return value;
                }
                System.out.println("Число должно быть положительным.");
            } catch (NumberFormatException e) {
                System.out.println("Введите число.");
            }
        }
    }

    private static String formatWeight(double weight) {
        return weight == Math.floor(weight)
                ? String.valueOf((long) weight) : String.valueOf(weight);
    }
}
