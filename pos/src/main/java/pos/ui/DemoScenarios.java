package pos.ui;

import pos.application.PosFacade;
import pos.domain.rubric.Criterion;
import pos.domain.student.Student;

import java.io.IOException;
import java.nio.file.Path;

public class DemoScenarios {

    private final PosFacade pos;

    public DemoScenarios(PosFacade pos) {
        this.pos = pos;
    }

    public void run() {
        System.out.println("======== ДЕМОНСТРАЦИЯ СЦЕНАРИЕВ «ПОС» ========");
        System.out.println();
        scenarioStudents();
        scenarioRubric();
        scenarioGrading();
        scenarioInvalidGrades();
        scenarioTotals();
        scenarioFixAndCancel();
        scenarioExport();
    }

    private void scenarioStudents() {
        System.out.println("--- Сценарий 1: добавляем студентов ---");
        addStudent("БПИ-01", "Бусыгина Ксения");
        addStudent("БПИ-02", "Батышкина Алёна");
        addStudent("БПИ-03", "Жаркова Татьяна");
        System.out.printf("  Студентов на курсе: %d%n", pos.students().size());
        System.out.println();
    }

    private void scenarioRubric() {
        System.out.println("--- Сценарий 2: рубрикатор курса ---");
        addCriterion("Аура", 52, 1);
        addCriterion("Свага", 67, 2);
        addCriterion("Вайб", 100, 3);
        for (Criterion c : pos.rubric().all()) {
            System.out.printf("  %-20s max %2d, вес %.0f%n",
                    c.name(), c.maxScore(), c.weight());
        }
        System.out.println();
    }

    private void scenarioGrading() {
        System.out.println("--- Сценарий 3: выставление оценок ---");
        grade("БПИ-01", "Аура", 52, "мега аура");
        grade("БПИ-01", "Свага", 67, " тут всё понятно");
        grade("БПИ-01", "Вайб", 100, "жоский +вайб");
        grade("БПИ-02", "Аура", 35 , "");
        grade("БПИ-02", "Свага", 52, "");
        grade("БПИ-03", "Вайб", 67, "");
        System.out.println();
    }

    private void scenarioInvalidGrades() {
        System.out.println("--- Сценарий 4: инварианты домена ---");
        System.out.println("  Попытка поставить 67 из 52:");
        grade("БПИ-01", "Аура", 67, "попытка накрутки");
        System.out.println("  Попытка оценить критерий не из рубрикатора:");
        grade("БПИ-02", "Харизма", 10, "такого критерия нет");
        System.out.println("  Попытка оценить несуществующего студента:");
        grade("ПМИ-01", "Вайб", 10, "-вайб");
        System.out.println();
    }

    private void scenarioTotals() {
        System.out.println("--- Сценарий 5: итоги (" + pos.policyName() + ") ---");
        for (Student s : pos.students()) {
            System.out.printf("  %-6s %-16s итог: %.2f%n",
                    s.studentId(), s.name(), pos.totalOf(s.studentId()));
        }
        System.out.println();
    }

    private void scenarioFixAndCancel() {
        System.out.println("--- Сценарий 6: исправление и отмена оценки ---");
        System.out.println("  Исправляем ауру БПИ-02: 35 -> 50");
        grade("БПИ-02", "Аура", 50, "поднялась");
        System.out.println("  Отменяем оценку БПИ-03 по вайбу:");
        String reason = pos.attemptRemoveGrade("БПИ-03", "Вайб");
        System.out.println(reason == null
                ? "  [OK]  Оценка отменена" : "  [FAIL] " + reason);
        System.out.printf("  Итог БПИ-02 после исправления: %.2f%n", pos.totalOf("БПИ-02"));
        System.out.printf("  Итог БПИ-03 после отмены: %.2f%n", pos.totalOf("БПИ-03"));
        System.out.println();
    }

    private void scenarioExport() {
        System.out.println("--- Сценарий 7: экспорт итоговой ведомости ---");
        for (String format : new String[]{"csv", "json"}) {
            try {
                Path file = pos.exportGradeSheet(format, Path.of("exports"));
                System.out.printf("  [OK]  %s -> %s%n", format.toUpperCase(),
                        file.toAbsolutePath());
            } catch (IOException | IllegalArgumentException e) {
                System.out.printf("  [FAIL] %s: %s%n", format.toUpperCase(), e.getMessage());
            }
        }
        System.out.println();
    }

    private void addStudent(String id, String name) {
        String reason = pos.attemptAddStudent(id, name);
        System.out.println(reason == null
                ? "  [OK]  " + id + " " + name
                : "  [FAIL] " + id + " - " + reason);
    }

    private void addCriterion(String name, int maxScore, double weight) {
        String reason = pos.attemptAddCriterion(name, maxScore, weight);
        if (reason != null) {
            System.out.println("  [FAIL] " + name + " - " + reason);
        }
    }

    private void grade(String studentId, String criterion, int score, String comment) {
        String reason = pos.attemptGrade(studentId, criterion, score, comment);
        System.out.println(reason == null
                ? "  [OK]  " + studentId + " / " + criterion + " = " + score
                : "  [FAIL] " + studentId + " / " + criterion + " - " + reason);
    }
}
