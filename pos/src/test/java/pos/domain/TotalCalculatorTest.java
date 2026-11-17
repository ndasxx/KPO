package pos.domain;

import org.junit.jupiter.api.Test;
import pos.domain.evaluation.Evaluation;
import pos.domain.evaluation.EvaluationFactory;
import pos.domain.rubric.Criterion;
import pos.domain.rubric.Rubric;
import pos.domain.service.SimpleSumTotalPolicy;
import pos.domain.service.TotalCalculator;
import pos.domain.service.WeightedTotalPolicy;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TotalCalculatorTest {

    private final EvaluationFactory factory = new EvaluationFactory();

    private Rubric rubric() {
        Rubric rubric = new Rubric();
        rubric.add(new Criterion("Посещаемость", 10, 1));
        rubric.add(new Criterion("ДЗ", 10, 2));
        rubric.add(new Criterion("Проект", 20, 3));
        return rubric;
    }

    @Test
    void weightedTotal_countsUngradedCriterionAsZero() {
        Rubric rubric = rubric();
        List<Evaluation> evaluations = List.of(
                factory.create("S-1", "ДЗ", 10, "", rubric),
                factory.create("S-1", "Проект", 10, "", rubric));
        TotalCalculator calculator = new TotalCalculator(new WeightedTotalPolicy());

        assertEquals(5.8333, calculator.totalFor("S-1", rubric, evaluations), 0.001);
    }

    @Test
    void weightedTotal_fullMarks_givesTen() {
        Rubric rubric = rubric();
        List<Evaluation> evaluations = List.of(
                factory.create("S-1", "Посещаемость", 10, "", rubric),
                factory.create("S-1", "ДЗ", 10, "", rubric),
                factory.create("S-1", "Проект", 20, "", rubric));
        TotalCalculator calculator = new TotalCalculator(new WeightedTotalPolicy());

        assertEquals(10.0, calculator.totalFor("S-1", rubric, evaluations), 0.001);
    }

    @Test
    void strategySwap_changesResult() {
        Rubric rubric = rubric();
        List<Evaluation> evaluations = List.of(
                factory.create("S-1", "ДЗ", 10, "", rubric),
                factory.create("S-1", "Проект", 10, "", rubric));

        double weighted = new TotalCalculator(new WeightedTotalPolicy())
                .totalFor("S-1", rubric, evaluations);
        double simpleSum = new TotalCalculator(new SimpleSumTotalPolicy())
                .totalFor("S-1", rubric, evaluations);

        assertEquals(5.8333, weighted, 0.001);
        assertEquals(20.0, simpleSum, 0.001);
    }

    @Test
    void totalFor_ignoresOtherStudents() {
        Rubric rubric = rubric();
        List<Evaluation> evaluations = List.of(
                factory.create("S-1", "ДЗ", 10, "", rubric),
                factory.create("S-2", "ДЗ", 2, "", rubric));
        TotalCalculator calculator = new TotalCalculator(new SimpleSumTotalPolicy());

        assertEquals(10.0, calculator.totalFor("S-1", rubric, evaluations), 0.001);
        assertEquals(2.0, calculator.totalFor("S-2", rubric, evaluations), 0.001);
    }
}
