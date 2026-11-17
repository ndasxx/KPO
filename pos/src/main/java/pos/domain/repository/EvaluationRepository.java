package pos.domain.repository;

import pos.domain.evaluation.Evaluation;

import java.util.List;
import java.util.Optional;

/**
 * порт: хранение оценок
 */
public interface EvaluationRepository {

    void save(Evaluation evaluation);

    Optional<Evaluation> find(String studentId, String criterionName);

    List<Evaluation> findByStudent(String studentId);

    List<Evaluation> findAll();

    boolean delete(String studentId, String criterionName);

    void clear();
}
