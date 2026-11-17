package pos.domain.rubric;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Rubric {

    private final Map<String, Criterion> criteria = new LinkedHashMap<>();

    public void add(Criterion criterion) {
        if (criteria.containsKey(criterion.name())) {
            throw new IllegalArgumentException(
                    "Критерий '" + criterion.name() + "' уже есть в рубрикаторе");
        }
        criteria.put(criterion.name(), criterion);
    }

    public Optional<Criterion> find(String name) {
        return Optional.ofNullable(criteria.get(name));
    }

    public List<Criterion> all() {
        return List.copyOf(criteria.values());
    }

    public boolean isEmpty() {
        return criteria.isEmpty();
    }

    public void clear() {
        criteria.clear();
    }
}
