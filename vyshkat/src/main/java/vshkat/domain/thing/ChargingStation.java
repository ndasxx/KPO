package vshkat.domain.thing;

import vshkat.domain.IEnergyConsumer;
import vshkat.domain.IThing;

public record ChargingStation(
        String inventoryNumber,
        String name,
        double dailyEnergyKwh
) implements IThing, IEnergyConsumer {

    public ChargingStation {
        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            throw new IllegalArgumentException("Инвентарный номер обязателен");
        }
        if (dailyEnergyKwh < 0) {
            throw new IllegalArgumentException("Энергия не может быть отрицательной");
        }
    }

    @Override
    public String type() {
        return "Зарядная станция";
    }
}
