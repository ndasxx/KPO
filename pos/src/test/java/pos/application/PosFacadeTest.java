package pos.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pos.domain.evaluation.EvaluationFactory;
import pos.domain.service.SimpleSumTotalPolicy;
import pos.domain.service.TotalCalculator;
import pos.domain.service.WeightedTotalPolicy;
import pos.infrastructure.memory.InMemoryEvaluationRepository;
import pos.infrastructure.memory.InMemoryRubricRepository;
import pos.infrastructure.memory.InMemoryStudentRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PosFacadeTest {

    private PosFacade facade;

    @BeforeEach
    void setUp() {
        facade = facadeWith(new TotalCalculator(new WeightedTotalPolicy()));
    }

    private PosFacade facadeWith(TotalCalculator calculator) {
        return new PosFacade(
                new InMemoryStudentRepository(),
                new InMemoryRubricRepository(),
                new InMemoryEvaluationRepository(),
                new EvaluationFactory(),
                calculator,
                List.of(), List.of());
    }

    private void seedStudentAndCriterion() {
        assertNull(facade.attemptAddStudent("S-1", "Иван Петров"));
        assertNull(facade.attemptAddCriterion("ДЗ", 10, 2));
    }

    @Test
    void addStudent_duplicate_rejected() {
        seedStudentAndCriterion();

        String reason = facade.attemptAddStudent("S-1", "Другой Иван");

        assertNotNull(reason);
        assertEquals(1, facade.students().size());
    }

    @Test
    void grade_unknownStudent_rejected() {
        assertNull(facade.attemptAddCriterion("ДЗ", 10, 2));

        String reason = facade.attemptGrade("S-999", "ДЗ", 5, "");

        assertNotNull(reason);
        assertTrue(reason.contains("нет в списке"));
    }

    @Test
    void grade_scoreAboveMax_rejected() {
        seedStudentAndCriterion();

        String reason = facade.attemptGrade("S-1", "ДЗ", 12, "накрутка");

        assertNotNull(reason);
        assertTrue(facade.evaluationsOf("S-1").isEmpty());
    }

    @Test
    void grade_unknownCriterion_rejected() {
        seedStudentAndCriterion();

        String reason = facade.attemptGrade("S-1", "Харизма", 5, "");

        assertNotNull(reason);
        assertTrue(facade.evaluationsOf("S-1").isEmpty());
    }

    @Test
    void grade_fix_overwritesPreviousScore() {
        seedStudentAndCriterion();
        assertNull(facade.attemptGrade("S-1", "ДЗ", 5, "черновик"));

        assertNull(facade.attemptGrade("S-1", "ДЗ", 9, "исправлено"));

        var evaluations = facade.evaluationsOf("S-1");
        assertEquals(1, evaluations.size());
        assertEquals(9, evaluations.get(0).score().value());
    }

    @Test
    void removeGrade_removesExistingEvaluation() {
        seedStudentAndCriterion();
        assertNull(facade.attemptGrade("S-1", "ДЗ", 7, ""));

        assertNull(facade.attemptRemoveGrade("S-1", "ДЗ"));

        assertTrue(facade.evaluationsOf("S-1").isEmpty());
    }

    @Test
    void removeGrade_missingEvaluation_returnsReason() {
        seedStudentAndCriterion();

        assertNotNull(facade.attemptRemoveGrade("S-1", "ДЗ"));
    }

    @Test
    void total_consistentWithCurrentEvaluations() {
        seedStudentAndCriterion();
        assertNull(facade.attemptAddCriterion("Проект", 20, 3));
        assertNull(facade.attemptGrade("S-1", "ДЗ", 10, ""));

        // (10/10*2 + 0/20*3) / 5 * 10 = 4.0
        assertEquals(4.0, facade.totalOf("S-1"), 0.001);

        // итог пересчитывается после исправления, а не хранится отдельно
        assertNull(facade.attemptGrade("S-1", "Проект", 20, ""));
        assertEquals(10.0, facade.totalOf("S-1"), 0.001);
    }

    @Test
    void policySwapViaConstructor_changesTotal() {
        // одинаковые данные в обоих фасадах, разная стратегия итога
        PosFacade simpleFacade = facadeWith(new TotalCalculator(new SimpleSumTotalPolicy()));
        for (PosFacade f : new PosFacade[]{facade, simpleFacade}) {
            assertNull(f.attemptAddStudent("S-1", "Иван Петров"));
            assertNull(f.attemptAddCriterion("ДЗ", 10, 2));
            assertNull(f.attemptAddCriterion("Проект", 20, 3));
            assertNull(f.attemptGrade("S-1", "ДЗ", 10, ""));
        }

        // взвешенная оценка: (10/10*2 + 0/20*3) / 5 * 10 = 4.0; сумма: 10
        assertEquals(4.0, facade.totalOf("S-1"), 0.001);
        assertEquals(10.0, simpleFacade.totalOf("S-1"), 0.001);
        assertEquals("Сумма баллов", simpleFacade.policyName());
    }
}
