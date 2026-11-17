package pos;

import pos.application.PosFacade;
import pos.di.Container;
import pos.domain.evaluation.EvaluationFactory;
import pos.domain.repository.EvaluationRepository;
import pos.domain.repository.RubricRepository;
import pos.domain.repository.StudentRepository;
import pos.domain.service.ITotalPolicy;
import pos.domain.service.TotalCalculator;
import pos.domain.service.WeightedTotalPolicy;
import pos.infrastructure.exporter.CsvGradeSheetExporter;
import pos.infrastructure.exporter.JsonGradeSheetExporter;
import pos.infrastructure.importer.CsvGradeSheetImporter;
import pos.infrastructure.importer.JsonGradeSheetImporter;
import pos.infrastructure.memory.InMemoryEvaluationRepository;
import pos.infrastructure.memory.InMemoryRubricRepository;
import pos.infrastructure.memory.InMemoryStudentRepository;
import pos.ui.ConsoleApp;
import pos.ui.DemoScenarios;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Container container = new Container();

        // domain
        container.register(ITotalPolicy.class, new WeightedTotalPolicy());
        container.register(EvaluationFactory.class, new EvaluationFactory());
        container.registerFactory(TotalCalculator.class,
                () -> new TotalCalculator(container.resolve(ITotalPolicy.class)));

        // infrastructure (адаптеры портов)
        container.register(StudentRepository.class, new InMemoryStudentRepository());
        container.register(RubricRepository.class, new InMemoryRubricRepository());
        container.register(EvaluationRepository.class, new InMemoryEvaluationRepository());

        // application (фасад сценариев)
        container.registerFactory(PosFacade.class, () -> new PosFacade(
                container.resolve(StudentRepository.class),
                container.resolve(RubricRepository.class),
                container.resolve(EvaluationRepository.class),
                container.resolve(EvaluationFactory.class),
                container.resolve(TotalCalculator.class),
                List.of(new CsvGradeSheetExporter(), new JsonGradeSheetExporter()),
                List.of(new CsvGradeSheetImporter(), new JsonGradeSheetImporter())));

        // ui
        container.registerFactory(DemoScenarios.class,
                () -> new DemoScenarios(container.resolve(PosFacade.class)));
        container.registerFactory(ConsoleApp.class,
                () -> new ConsoleApp(
                        container.resolve(PosFacade.class),
                        container.resolve(DemoScenarios.class)));

        container.resolve(ConsoleApp.class).run();
    }
}
