package vshkat.domain;

import org.junit.jupiter.api.Test;
import vshkat.domain.thing.ChargingStation;
import vshkat.domain.thing.DockingStation;
import vshkat.domain.thing.Helmet;

import static org.junit.jupiter.api.Assertions.*;

class ThingTest {

    @Test
    void chargingStation_negativeEnergy_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ChargingStation("CH-1", "Зарядка", -1.0));
    }

    @Test
    void dockingStation_negativeEnergy_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new DockingStation("DK-1", "Док", -1.0));
    }

    @Test
    void helmet_validData_createdSuccessfully() {
        assertDoesNotThrow(() -> new Helmet("HL-1", "Шлем"));
    }
}