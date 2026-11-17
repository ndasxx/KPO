package pos.domain.service;

import pos.domain.evaluation.Evaluation;
import pos.domain.rubric.Rubric;

import java.util.List;

public class TotalCalculator {

    private final ITotalPolicy policy;

    public TotalCalculator(ITotalPolicy policy) {
        this.policy = policy;
    }

    public String policyName() {
        return policy.name();
    }

    public double totalFor(String studentId, Rubric rubric, List<Evaluation> allEvaluations) {
        List<Evaluation> own = allEvaluations.stream()
                .filter(e -> e.studentId().equals(studentId))
                .toList();
        return policy.total(rubric, own);
    }
}
