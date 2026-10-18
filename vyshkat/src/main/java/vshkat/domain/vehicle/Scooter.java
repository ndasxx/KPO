package vshkat.domain.vehicle;

import vshkat.domain.IVehicle;

public record Scooter(
        String inventoryNumber,
        String name,
        double dailyEnergyKwh,
        int beginnerFriendliness
) implements IVehicle {

    public Scooter {
        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            throw new IllegalArgumentException("Инвентарный номер обязателен");
        }
        if (beginnerFriendliness < 1 || beginnerFriendliness > 10) {
            throw new IllegalArgumentException("Простота для новичка должна быть 1..10");
        }
        if (dailyEnergyKwh < 0) {
            throw new IllegalArgumentException("Энергия не может быть отрицательной");
        }
    }

    @Override
    public String type() {
        return "Самокат";
    }
}