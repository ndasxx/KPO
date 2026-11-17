package pos.domain.repository;

import pos.domain.student.Student;

import java.util.List;
import java.util.Optional;

/**
 * порт: хранение студентов
 */
public interface StudentRepository {

    void save(Student student);

    Optional<Student> findById(String studentId);

    List<Student> findAll();

    void clear();
}
