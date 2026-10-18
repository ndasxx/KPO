package vshkat.domain;

import org.junit.jupiter.api.Test;
import vshkat.domain.vehicle.Bicycle;
import vshkat.domain.vehicle.ElectricBike;
import vshkat.domain.vehicle.Scooter;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void scooter_negativeEnergy_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Scooter("SC-1", "Самокат", -1.0, 5));
    }

    @Test
    void scooter_friendlinessOutOfRange_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Scooter("SC-1", "Самокат", 1.0, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new Scooter("SC-1", "Самокат", 1.0, 11));
    }

    @Test
    void bicycle_friendlinessOutOfRange_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bicycle("BK-1", "Велосипед", 0));
    }

    @Test
    void electricBike_negativeEnergy_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ElectricBike("EB-1", "Электровелосипед", -1.0, 5));
    }

    @Test
    void scooter_blankInventoryNumber_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Scooter("", "X", 1.0, 8));
    }

    @Test
    void scooter_nullInventoryNumber_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Scooter(null, "X", 1.0, 8));
    }

    @Test
    void bicycle_zeroEnergy_returnsZero() {
        Bicycle bike = new Bicycle("BK-1", "Велосипед", 5);
        assertEquals(0.0, bike.dailyEnergyKwh());
    }
}
