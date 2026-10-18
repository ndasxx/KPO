package vshkat.service;

import org.junit.jupiter.api.Test;
import vshkat.domain.vehicle.Bicycle;
import vshkat.domain.vehicle.Scooter;

import static org.junit.jupiter.api.Assertions.*;

class ServiceCenterTest {

    private final ServiceCenter center = new ServiceCenter();

    @Test
    void acceptsValidScooter() {
        assertTrue(center.inspect(new Scooter("SC-1", "X", 1.0, 8)));
    }

    @Test
    void rejectsScooterWithBlankName() {
        assertFalse(center.inspect(new Scooter("SC-1", "", 1.0, 8)));
    }

    @Test
    void rejectsScooterWithNullName() {
        assertFalse(center.inspect(new Scooter("SC-1", null, 1.0, 8)));
    }

    @Test
    void rejectsScooterWithTooHighEnergy() {
        assertFalse(center.inspect(new Scooter("SC-1", "X", 1000.0, 8)));
    }

    @Test
    void rejectsScooterWithTooLowFriendliness() {
        assertFalse(center.inspect(new Scooter("SC-1", "X", 1.0, 1)));
    }

    @Test
    void acceptsBicycleWithZeroEnergy() {
        assertTrue(center.inspect(new Bicycle("BK-1", "City", 7)));
    }
}