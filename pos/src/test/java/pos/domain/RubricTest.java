package pos.domain;

import org.junit.jupiter.api.Test;
import pos.domain.rubric.Criterion;
import pos.domain.rubric.Rubric;

import static org.junit.jupiter.api.Assertions.*;

class RubricTest {

    @Test
    void addCriterion_addedToRubric() {
        Rubric rubric = new Rubric();
        rubric.add(new Criterion("ДЗ", 10, 2));

        assertTrue(rubric.find("ДЗ").isPresent());
        assertEquals(1, rubric.all().size());
    }

    @Test
    void addCriterion_duplicateName_rejected() {
        Rubric rubric = new Rubric();
        rubric.add(new Criterion("ДЗ", 10, 2));

        assertThrows(IllegalArgumentException.class,
                () -> rubric.add(new Criterion("ДЗ", 5, 1)));
    }

    @Test
    void criterion_invalidParams_rejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new Criterion("ДЗ", 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Criterion("ДЗ", 10, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Criterion(" ", 10, 1));
    }
}
