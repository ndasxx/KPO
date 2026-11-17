package pos.domain;

import org.junit.jupiter.api.Test;
import pos.domain.evaluation.Score;

import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    @Test
    void score_withinRange_created() {
        Score score = new Score(7, 10);

        assertEquals(7, score.value());
        assertEquals(10, score.maxValue());
    }

    @Test
    void score_aboveMax_rejected() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> new Score(12, 10));
        assertTrue(ex.getMessage().contains("вне диапазона"));
    }

    @Test
    void score_negative_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new Score(-1, 10));
    }

    @Test
    void score_boundaries_allowed() {
        assertDoesNotThrow(() -> new Score(0, 10));
        assertDoesNotThrow(() -> new Score(10, 10));
    }
}
