package pos.application.port;

import pos.domain.rubric.Criterion;
import java.util.List;

public record GradeSheet(
        List<Criterion> criteria,
        List<GradeSheetRow> rows
) {
}
