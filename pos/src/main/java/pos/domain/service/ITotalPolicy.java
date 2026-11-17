package pos.domain.service;

import pos.domain.evaluation.Evaluation;
import pos.domain.rubric.Rubric;
import java.util.List;

public interface ITotalPolicy {

    String name();

    double total(Rubric rubric, List<Evaluation> evaluations);
}
