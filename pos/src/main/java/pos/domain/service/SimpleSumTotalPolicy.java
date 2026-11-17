package pos.domain.service;

import pos.domain.evaluation.Evaluation;
import pos.domain.rubric.Rubric;

import java.util.List;

/**
 * сумма баллов без весов
 */
public class SimpleSumTotalPolicy implements ITotalPolicy {

    @Override
    public String name() {
        return "Сумма баллов";
    }

    @Override
    public double total(Rubric rubric, List<Evaluation> evaluations) {
        return evaluations.stream()
                .filter(e -> rubric.find(e.criterionName()).isPresent())
                .mapToInt(e -> e.score().value())
                .sum();
    }
}
