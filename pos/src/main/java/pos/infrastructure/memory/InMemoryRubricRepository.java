package pos.infrastructure.memory;

import pos.domain.repository.RubricRepository;
import pos.domain.rubric.Rubric;

public class InMemoryRubricRepository implements RubricRepository {

    private Rubric rubric = new Rubric();

    @Override
    public Rubric load() {
        return rubric;
    }

    @Override
    public void save(Rubric rubric) {
        this.rubric = rubric;
    }
}
