package vshkat.service;

import vshkat.domain.*;

import java.util.ArrayList;
import java.util.List;

public class Fleet {

    private final ITechnicalInspection inspection;
    private final List<IVehicle> vehicles = new ArrayList<>();
    private final List<IThing> things = new ArrayList<>();

    public Fleet(ITechnicalInspection inspection) {
        this.inspection = inspection;
    }

    public String attemptAddVehicle(IVehicle v) {
        if (!inspection.inspect(v)) {
            return "Техосмотр не пройден";
        }
        String duplicate = checkDuplicateInventoryNumber(v.inventoryNumber());
        if (duplicate != null) {
            return duplicate;
        }
        vehicles.add(v);
        return null;
    }

    public String attemptAddThing(IThing t) {
        String duplicate = checkDuplicateInventoryNumber(t.inventoryNumber());
        if (duplicate != null) {
            return duplicate;
        }
        things.add(t);
        return null;
    }

    public void addVehicle(IVehicle v) {
        String reason = attemptAddVehicle(v);
        if (reason != null) {
            throw new IllegalArgumentException(reason);
        }
    }

    public void addThing(IThing t) {
        String reason = attemptAddThing(t);
        if (reason != null) {
            throw new IllegalArgumentException(reason);
        }
    }

    public boolean removeVehicle(String inventoryNumber) {
        if (inventoryNumber == null || inventoryNumber.isBlank()) return false;
        return vehicles.removeIf(v -> inventoryNumber.equals(v.inventoryNumber()));
    }

    public boolean removeThing(String inventoryNumber) {
        if (inventoryNumber == null || inventoryNumber.isBlank()) return false;
        return things.removeIf(t -> inventoryNumber.equals(t.inventoryNumber()));
    }

    public void clear() {
        vehicles.clear();
        things.clear();
    }


    public List<IVehicle> vehicles() { return List.copyOf(vehicles); }
    public List<IThing> things()     { return List.copyOf(things); }

    public double totalDailyEnergyKwh() {
        double sum = vehicles.stream()
                .mapToDouble(IEnergyConsumer::dailyEnergyKwh)
                .sum();
        for (IThing thing : things) {
            if (thing instanceof IEnergyConsumer ins) {
                sum += ins.dailyEnergyKwh();
            }
        }
        return sum;
    }

    public List<IVehicle> vehiclesForBeginners() {
        return vehicles.stream()
                .filter(IVehicle::isSuitableForBeginners)
                .toList();
    }

    public List<IInventoryItem> allInventoryItems() {
        List<IInventoryItem> all = new ArrayList<>();
        all.addAll(vehicles);
        all.addAll(things);
        return all;
    }

    private String checkDuplicateInventoryNumber(String inventoryNumber) {
        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            return "Инвентарный номер обязателен";
        }
        boolean taken = vehicles.stream()
                .anyMatch(x -> x.inventoryNumber().equals(inventoryNumber))
                || things.stream()
                .anyMatch(x -> x.inventoryNumber().equals(inventoryNumber));
        if (taken) {
            return "Инвентарный номер " + inventoryNumber + " уже используется";
        }
        return null;
    }
}