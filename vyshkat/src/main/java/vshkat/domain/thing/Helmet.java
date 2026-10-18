package vshkat.domain.thing;

import vshkat.domain.IThing;

public record Helmet(
        String inventoryNumber,
        String name
) implements IThing {

    public Helmet {
        if (inventoryNumber == null || inventoryNumber.isBlank()) {
            throw new IllegalArgumentException("Инвентарный номер обязателен");
        }
    }

    @Override
    public String type() {
        return "Шлем";
    }
}
