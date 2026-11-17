package pos.domain;

import org.junit.jupiter.api.Test;
import pos.domain.evaluation.Evaluation;
import pos.domain.evaluation.EvaluationFactory;
import pos.domain.rubric.Criterion;
import pos.domain.rubric.Rubric;

import static org.junit.jupiter.api.Assertions.*;

class EvaluationFactoryTest {

    private final EvaluationFactory factory = new EvaluationFactory();

    private Rubric rubric() {
        Rubric rubric = new Rubric();
        rubric.add(new Criterion("ДЗ", 10, 2));
        return rubric;
    }

    @Test
    void create_validEvaluation_takesMaxFromCriterion() {
        Evaluation evaluation = factory.create("S-1", "ДЗ", 8, "ок", rubric());

        assertEquals(8, evaluation.score().value());
        assertEquals(10, evaluation.score().maxValue());
        assertEquals("ДЗ", evaluation.criterionName());
    }

    @Test
    void create_criterionNotInRubric_rejected() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> factory.create("S-1", "Экзамен", 8, "", rubric()));
        assertTrue(ex.getMessage().contains("нет в рубрикаторе"));
    }

    @Test
    void create_scoreAboveCriterionMax_rejected() {
        assertThrows(IllegalArgumentException.class,
                () -> factory.create("S-1", "ДЗ", 12, "", rubric()));
    }
}
