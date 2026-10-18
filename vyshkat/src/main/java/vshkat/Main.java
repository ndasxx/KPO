package vshkat;

import vshkat.di.Container;
import vshkat.service.Fleet;
import vshkat.service.ITechnicalInspection;
import vshkat.service.ServiceCenter;
import vshkat.ui.ConsoleApp;
import vshkat.ui.DemoScenarios;

public class Main {
    public static void main(String[] args) {
        Container container = new Container();

        container.register(ITechnicalInspection.class, new ServiceCenter());

        container.registerFactory(Fleet.class,
                () -> new Fleet(container.resolve(ITechnicalInspection.class)));

        container.registerFactory(DemoScenarios.class,
                () -> new DemoScenarios(container.resolve(Fleet.class)));

        container.registerFactory(ConsoleApp.class,
                () -> new ConsoleApp(
                        container.resolve(Fleet.class),
                        container.resolve(DemoScenarios.class)));

        container.resolve(ConsoleApp.class).run();
    }
}