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

public class DemoScenarios {

    private final Fleet fleet;

    public DemoScenarios(Fleet fleet) {
        this.fleet = fleet;
    }

    public void run() {
        System.out.println("""
        
        ========================================
          ДЕМОНСТРАЦИЯ ВСЕХ СЦЕНАРИЕВ РАБОТЫ
        ========================================
        
        """);

        scenarioAddVehicles();
        scenarioAddThings();
        scenarioInspectionAccept();
        scenarioInspectionReject();
        scenarioTotalEnergy();
        scenarioBeginners();
        scenarioInventory();
        scenarioRemove();
        scenarioDuplicateRejection();

        System.out.println("""
        ========================================
          ДЕМОНСТРАЦИЯ ЗАВЕРШЕНА
        ========================================
        
        """);
    }

    private void scenarioAddVehicles() {
        System.out.println("--- Сценарий 1а: добавление транспорта в парк ---");
        acceptVehicle(new Scooter("SC-001", "Самокат X1", 1.2, 8));
        acceptVehicle(new Bicycle("BK-001", "Городской велосипед", 7));
        acceptVehicle(new ElectricBike("EB-001", "Электровелосипед V1", 2.4, 9));
        System.out.println();
    }

    private void scenarioAddThings() {
        System.out.println("--- Сценарий 1б: добавление вещей на баланс ---");
        acceptThing(new Helmet("HL-001", "Шлем чёрный"));
        acceptThing(new ChargingStation("CH-001", "Зарядная станция", 5.0));
        acceptThing(new DockingStation("DK-001", "Док-станция у корпуса", 1.5));
        System.out.printf("  Вещей в парке: %d%n", fleet.things().size());
        System.out.println();
    }

    private void scenarioInspectionAccept() {
        System.out.println("--- Сценарий 2a: техосмотр — принять ---");
        acceptVehicle(new Scooter("SC-002", "Самокат X2", 1.1, 4));
        System.out.println();
    }

    private void scenarioInspectionReject() {
        System.out.println("--- Сценарий 2b: техосмотр — отклонить ---");
        IVehicle bad = new Scooter("BAD-001", "Гоночный самокат", 1.0, 1);

        String reason = fleet.attemptAddVehicle(bad);
        if (reason == null) {
            System.out.println("  [ОШИБКА] ожидался отказ, но транспорт принят");
        } else {
            System.out.printf("  [REJECT] %s — %s%n", bad.name(), reason);
        }
        System.out.println();
    }

    private void scenarioTotalEnergy() {
        System.out.println("--- Сценарий 3: суммарное суточное потребление ---");
        System.out.println("Транспорт: " + fleet.vehicles().size() + " ед.");
        System.out.println("Вещи:      " + fleet.things().size() + " ед.");
        System.out.printf("Всего единиц: %d%n",
                fleet.vehicles().size() + fleet.things().size());
        System.out.printf("Суммарное потребление: %.2f кВт·ч/сутки%n",
                fleet.totalDailyEnergyKwh());
        System.out.println();
    }

    private void scenarioBeginners() {
        System.out.println("--- Сценарий 4: техника для новичков (простота ≥ 6) ---");
        var list = fleet.vehiclesForBeginners();
        if (list.isEmpty()) {
            System.out.println("  нет подходящей техники");
        } else {
            for (IVehicle v : list) {
                System.out.printf("  [%s] %s (%s) — простота %d, %.2f кВт·ч%n",
                        v.type(), v.name(), v.inventoryNumber(),
                        v.beginnerFriendliness(), v.dailyEnergyKwh());
            }
        }
        System.out.println();
    }

    private void scenarioInventory() {
        System.out.println("--- Сценарий 5: все инвентарные номера (техника + вещи) ---");
        for (IInventoryItem item : fleet.allInventoryItems()) {
            System.out.printf("  %-20s %s — %s%n",
                    "[" + item.type() + "]",
                    item.inventoryNumber(),
                    item.name());
        }
        System.out.println();
    }

    private void scenarioRemove() {
        System.out.println("--- Сценарий 6: удаление транспорта из парка ---");
        IVehicle temp = new Scooter("TEMP-001", "Временный самокат", 0.5, 5);
        acceptVehicle(temp);
        System.out.printf("  Транспорта в парке: %d%n", fleet.vehicles().size());

        boolean removed = fleet.removeVehicle("TEMP-001");
        System.out.printf("  Удаление TEMP-001: %s%n",
                removed ? "успешно" : "не найдено");
        System.out.printf("  Транспорта после удаления: %d%n",
                fleet.vehicles().size());
        System.out.println();
    }

    private void scenarioDuplicateRejection() {
        System.out.println("--- Сценарий 7: отказ из-за дубликата номера ---");

        System.out.println("  Попытка добавить транспорт с номером SC-001:");
        acceptVehicle(new Scooter("SC-001", "Дубликат самоката", 1.0, 8));

        System.out.println("  Попытка добавить вещь с номером SC-001:");
        acceptThing(new Helmet("SC-001", "Шлем с чужим номером"));

        System.out.println("  Попытка добавить вещь с номером HL-001 (уже есть):");
        acceptThing(new Helmet("HL-001", "Второй шлем"));

        System.out.printf("  Итог: транспорта %d, вещей %d (не изменилось)%n",
                fleet.vehicles().size(), fleet.things().size());
        System.out.println();
    }

    private void acceptVehicle(IVehicle v) {
        String reason = fleet.attemptAddVehicle(v);
        if (reason == null) {
            System.out.printf("  [OK]  [%s] %s (%s)%n",
                    v.type(), v.name(), v.inventoryNumber());
        } else {
            System.out.printf("  [FAIL] [%s] %s — %s%n",
                    v.type(), v.name(), reason);
        }
    }

    private void acceptThing(IThing t) {
        String reason = fleet.attemptAddThing(t);
        if (reason == null) {
            System.out.printf("  [OK]  [%s] %s (%s)%n",
                    t.type(), t.name(), t.inventoryNumber());
        } else {
            System.out.printf("  [FAIL] [%s] %s — %s%n",
                    t.type(), t.name(), reason);
        }
    }
}
