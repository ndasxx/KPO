package vshkat.ui;

import vshkat.domain.IInventoryItem;
import vshkat.domain.IThing;
import vshkat.domain.IVehicle;
import vshkat.domain.thing.ChargingStation;
import vshkat.domain.thing.DockingStation;
import vshkat.domain.thing.Helmet;
import vshkat.domain.vehicle.Bicycle;
import vshkat.domain.vehicle.ElectricBike;
import vshkat.domain.vehicle.Scooter;
import vshkat.service.Fleet;

import java.util.Scanner;

public class ConsoleApp {

    private static final int VEHICLE_TYPE_SCOOTER        = 1;
    private static final int VEHICLE_TYPE_BICYCLE        = 2;
    private static final int VEHICLE_TYPE_ELECTRIC_BIKE  = 3;

    private static final int THING_TYPE_HELMET            = 1;
    private static final int THING_TYPE_CHARGING_STATION  = 2;
    private static final int THING_TYPE_DOCKING_STATION   = 3;

    private final Fleet fleet;
    private final DemoScenarios demo;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(Fleet fleet, DemoScenarios demo) {
        this.fleet = fleet;
        this.demo = demo;
    }

    public void run() {
        demo.run();
        fleet.clear();
        printDemoFinishedBanner();
        interactiveLoop();
    }

    private void printDemoFinishedBanner() {
        System.out.println("""
        ========================================
          Парк очищен — начните с нуля.
        ========================================
        """);
    }

    private void interactiveLoop() {
        while (true) {
            printMenu();
            int choice = readInt();
            try {
                switch (choice) {
                    case 1 -> addVehicleInteractive();
                    case 2 -> addThingInteractive();
                    case 3 -> showFleetReport();
                    case 4 -> showBeginners();
                    case 5 -> showInventory();
                    case 6 -> removeVehicleInteractive();
                    case 7 -> removeThingInteractive();
                    case 0 -> {
                        System.out.println("До встречи!");
                        return;
                    }
                    default -> System.out.println("Неизвестная команда.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.print("""
        
        === ВышКат ===
        1. Принять транспорт в парк
        2. Принять вещь на баланс
        3. Отчёт: число единиц и суммарная энергия
        4. Список техники для новичков
        5. Все инвентарные номера (техника + вещи)
        6. Удалить транспорт по инвентарному номеру
        7. Удалить вещь по инвентарному номеру
        0. Выход
        > """);
    }

    private void addVehicleInteractive() {
        System.out.println("Тип транспорта: " + describeVehicleTypes());
        int type = readInt();
        if (type < VEHICLE_TYPE_SCOOTER || type > VEHICLE_TYPE_ELECTRIC_BIKE) {
            System.out.println("Неизвестный тип транспорта.");
            return;
        }

        System.out.print("Инвентарный номер: ");
        String inv = scanner.nextLine().trim();

        System.out.print("Наименование: ");
        String name = scanner.nextLine().trim();

        int friendliness = readIntInRange("Простота для новичка (1..10): ", 1, 10);

        double kwh = 0.0;
        if (vehicleNeedsEnergy(type)) {
            kwh = readNonNegativeDouble("Суточный расход (кВт·ч): ");
        }

        try {
            IVehicle vehicle = switch (type) {
                case VEHICLE_TYPE_SCOOTER ->
                        new Scooter(inv, name, kwh, friendliness);
                case VEHICLE_TYPE_BICYCLE ->
                        new Bicycle(inv, name, friendliness);
                case VEHICLE_TYPE_ELECTRIC_BIKE ->
                        new ElectricBike(inv, name, kwh, friendliness);
                default -> throw new IllegalArgumentException("Неизвестный тип: " + type);
            };
            String reason = fleet.attemptAddVehicle(vehicle);
            if (reason == null) {
                System.out.println("[OK] Транспорт принят в парк.");
            } else {
                System.out.println("[FAIL] Отклонено: " + reason);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Не удалось создать транспорт: " + e.getMessage());
        }
    }

    private void addThingInteractive() {
        System.out.println("Тип вещи: " + describeThingTypes());
        int type = readInt();
        if (type < THING_TYPE_HELMET || type > THING_TYPE_DOCKING_STATION) {
            System.out.println("Неизвестный тип вещи.");
            return;
        }

        System.out.print("Инвентарный номер: ");
        String inv = scanner.nextLine().trim();

        System.out.print("Наименование: ");
        String name = scanner.nextLine().trim();

        double kwh = 0.0;
        if (thingNeedsEnergy(type)) {
            kwh = readNonNegativeDouble("Суточный расход (кВт·ч): ");
        }

        try {
            IThing thing = switch (type) {
                case THING_TYPE_HELMET ->
                        new Helmet(inv, name);
                case THING_TYPE_CHARGING_STATION ->
                        new ChargingStation(inv, name, kwh);
                case THING_TYPE_DOCKING_STATION ->
                        new DockingStation(inv, name, kwh);
                default -> throw new IllegalArgumentException("Неизвестный тип: " + type);
            };
            String reason = fleet.attemptAddThing(thing);
            if (reason == null) {
                System.out.println("[OK] Вещь принята на баланс.");
            } else {
                System.out.println("[FAIL] Отклонено: " + reason);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Не удалось создать вещь: " + e.getMessage());
        }
    }

    private void removeVehicleInteractive() {
        System.out.print("Инвентарный номер транспорта для удаления: ");
        String inv = scanner.nextLine().trim();

        boolean removed = fleet.removeVehicle(inv);
        if (removed) {
            System.out.println("[OK] Транспорт " + inv + " удалён из парка.");
        } else {
            System.out.println("[FAIL] Транспорт с номером \"" + inv + "\" не найден.");
        }
    }

    private void removeThingInteractive() {
        System.out.print("Инвентарный номер вещи для удаления: ");
        String inv = scanner.nextLine().trim();

        boolean removed = fleet.removeThing(inv);
        if (removed) {
            System.out.println("[OK] Вещь " + inv + " удалена с баланса.");
        } else {
            System.out.println("[FAIL] Вещь с номером \"" + inv + "\" не найдена.");
        }
    }

    private void showFleetReport() {
        System.out.println("--- Отчёт по парку ---");
        System.out.println("Транспорт: " + fleet.vehicles().size() + " ед.");
        System.out.println("Вещи:      " + fleet.things().size() + " ед.");
        System.out.printf ("Всего единиц: %d%n",
                fleet.vehicles().size() + fleet.things().size());
        System.out.printf ("Суммарное суточное потребление: %.2f кВт·ч%n",
                fleet.totalDailyEnergyKwh());
    }

    private void showBeginners() {
        var list = fleet.vehiclesForBeginners();
        System.out.println("--- Техника, подходящая новичкам (простота ≥ 6) ---");
        if (list.isEmpty()) {
            System.out.println("Нет подходящей техники.");
            return;
        }
        for (IVehicle v : list) {
            System.out.printf("  [%s] %s (%s) — простота %d, %.2f кВт·ч%n",
                    v.type(), v.name(), v.inventoryNumber(),
                    v.beginnerFriendliness(), v.dailyEnergyKwh());
        }
    }

    private void showInventory() {
        System.out.println("--- Все инвентарные номера (техника и вещи) ---");
        for (IInventoryItem item : fleet.allInventoryItems()) {
            System.out.printf("  %-20s %s — %s%n",
                    "[" + item.type() + "]",
                    item.inventoryNumber(),
                    item.name());
        }
    }

    private String describeVehicleTypes() {
        return "1 — самокат, 2 — велосипед, 3 — электровелосипед";
    }

    private String describeThingTypes() {
        return "1 — шлем, 2 — зарядная станция, 3 — док-станция";
    }

    private boolean vehicleNeedsEnergy(int type) {
        return type == VEHICLE_TYPE_SCOOTER || type == VEHICLE_TYPE_ELECTRIC_BIKE;
    }

    private boolean thingNeedsEnergy(int type) {
        return type == THING_TYPE_CHARGING_STATION || type == THING_TYPE_DOCKING_STATION;
    }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Введите число: ");
        }
        int v = scanner.nextInt();
        scanner.nextLine();
        return v;
    }

    private int readIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            int v = readInt();
            if (v >= min && v <= max) return v;
            System.out.println("Значение должно быть в диапазоне " + min + ".." + max);
        }
    }

    private double readDouble(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print("Это не число, попробуйте ещё раз: ");
        }
        double v = scanner.nextDouble();
        scanner.nextLine();
        return v;
    }

    private double readNonNegativeDouble(String prompt) {
        while (true) {
            double v = readDouble(prompt);
            if (v >= 0) return v;
            System.out.println("Ошибка: значение не может быть отрицательным. Повторите ввод.");
        }
    }
}
