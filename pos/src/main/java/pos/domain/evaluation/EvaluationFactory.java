package pos.domain.evaluation;

import pos.domain.rubric.Criterion;
import pos.domain.rubric.Rubric;
import java.time.LocalDate;

public class EvaluationFactory {

    public Evaluation create(String studentId, String criterionName,
                             int rawScore, String comment, Rubric rubric) {
        Criterion criterion = rubric.find(criterionName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Критерия '" + criterionName + "' нет в рубрикаторе"));
        Score score = new Score(rawScore, criterion.maxScore());
        return new Evaluation(studentId, criterionName, score, LocalDate.now(), comment);
    }
}
