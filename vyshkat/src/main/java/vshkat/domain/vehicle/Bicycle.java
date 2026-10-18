package vshkat.domain.vehicle;

import vshkat.domain.IVehicle;

public record Bicycle(
        String inventoryNumber,
        String name,
        int beginnerFriendliness
) implements IVehicle {

    public Bicycle {
        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            throw new IllegalArgumentException("Инвентарный номер обязателен");
        }
        if (beginnerFriendliness < 1 || beginnerFriendliness > 10) {
            throw new IllegalArgumentException("Простота для новичка должна быть 1..10");
        }
    }

    @Override
    public double dailyEnergyKwh() {
        return 0.0;
    }

    @Override
    public String type() {
        return "Велосипед";
    }
}