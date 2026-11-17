package pos.domain.repository;

import pos.domain.rubric.Rubric;

/**
 * порт: хранение рубрикатора
 */
public interface RubricRepository {

    Rubric load();

    void save(Rubric rubric);
}
