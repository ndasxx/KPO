package pos.application.port;

import pos.domain.rubric.Criterion;
import pos.domain.student.Student;
import java.util.List;

public record ImportedSheet(
        List<Criterion> criteria,
        List<Student> students,
        List<ImportedGrade> grades
) {
}
