package pos.infrastructure.memory;

import pos.domain.evaluation.Evaluation;
import pos.domain.repository.EvaluationRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryEvaluationRepository implements EvaluationRepository {

    private final Map<String, Evaluation> storage = new LinkedHashMap<>();

    private static String key(String studentId, String criterionName) {
        return studentId + "|" + criterionName;
    }

    @Override
    public void save(Evaluation evaluation) {
        storage.put(key(evaluation.studentId(), evaluation.criterionName()), evaluation);
    }

    @Override
    public Optional<Evaluation> find(String studentId, String criterionName) {
        return Optional.ofNullable(storage.get(key(studentId, criterionName)));
    }

    @Override
    public List<Evaluation> findByStudent(String studentId) {
        return storage.values().stream()
                .filter(e -> e.studentId().equals(studentId))
                .toList();
    }

    @Override
    public List<Evaluation> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public boolean delete(String studentId, String criterionName) {
        return storage.remove(key(studentId, criterionName)) != null;
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
