package pos.domain.service;

import pos.domain.evaluation.Evaluation;
import pos.domain.rubric.Criterion;
import pos.domain.rubric.Rubric;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * итог по весам: 10 * sum(score / max * вес) / sum(веса)
 * критерий без оценки считается нулём
 */
public class WeightedTotalPolicy implements ITotalPolicy {

    @Override
    public String name() {
        return "Взвешенный итог (0-10)";
    }

    @Override
    public double total(Rubric rubric, List<Evaluation> evaluations) {
        Map<String, Evaluation> byCriterion = evaluations.stream()
                .collect(Collectors.toMap(Evaluation::criterionName, e -> e, (a, b) -> b));

        double weightedSum = 0.0;
        double weightSum = 0.0;
        for (Criterion criterion : rubric.all()) {
            weightSum += criterion.weight();
            Evaluation evaluation = byCriterion.get(criterion.name());
            if (evaluation != null) {
                weightedSum += ((double) evaluation.score().value()
                        / criterion.maxScore()) * criterion.weight();
            }
        }
        if (weightSum == 0.0) {
            return 0.0;
        }
        return 10.0 * weightedSum / weightSum;
    }
}
