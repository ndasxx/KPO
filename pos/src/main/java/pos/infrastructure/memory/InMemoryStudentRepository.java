package pos.infrastructure.memory;

import pos.domain.repository.StudentRepository;
import pos.domain.student.Student;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryStudentRepository implements StudentRepository {

    private final Map<String, Student> storage = new LinkedHashMap<>();

    @Override
    public void save(Student student) {
        storage.put(student.studentId(), student);
    }

    @Override
    public Optional<Student> findById(String studentId) {
        return Optional.ofNullable(storage.get(studentId));
    }

    @Override
    public List<Student> findAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
