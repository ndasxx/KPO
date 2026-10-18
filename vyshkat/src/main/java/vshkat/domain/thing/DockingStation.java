package vshkat.domain.thing;

import vshkat.domain.IEnergyConsumer;
import vshkat.domain.IThing;

public record DockingStation(
        String inventoryNumber,
        String name,
        double dailyEnergyKwh
) implements IThing, IEnergyConsumer {

    public DockingStation {
        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            throw new IllegalArgumentException("Инвентарный номер обязателен");
        }
        if (dailyEnergyKwh < 0) {
            throw new IllegalArgumentException("Энергия не может быть отрицательной");
        }
    }

    @Override
    public String type() {
        return "Док-станция";
    }
}
