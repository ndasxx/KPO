package vshkat.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vshkat.domain.IVehicle;
import vshkat.domain.thing.ChargingStation;
import vshkat.domain.thing.Helmet;
import vshkat.domain.vehicle.Bicycle;
import vshkat.domain.vehicle.Scooter;
import vshkat.stubs.AlwaysFailInspectionStub;
import vshkat.stubs.AlwaysPassInspectionStub;

import static org.junit.jupiter.api.Assertions.*;

class FleetTest {

    private Fleet fleet;

    @BeforeEach
    void setUp() {
        fleet = new Fleet(new AlwaysPassInspectionStub());
    }

    @Test
    void addVehicle_passesInspection_addedToFleet() {
        IVehicle scooter = new Scooter("SC-1", "Самокат", 1.0, 5);
        String result = fleet.attemptAddVehicle(scooter);

        assertNull(result);
        assertEquals(1, fleet.vehicles().size());
    }

    @Test
    void addVehicle_failsInspection_notAdded() {
        Fleet strictFleet = new Fleet(new AlwaysFailInspectionStub());
        IVehicle scooter = new Scooter("SC-1", "Самокат", 1.0, 5);

        String result = strictFleet.attemptAddVehicle(scooter);

        assertNotNull(result);
        assertTrue(strictFleet.vehicles().isEmpty());
    }

    @Test
    void addThing_addedWithoutInspection() {
        Helmet helmet = new Helmet("HL-1", "Шлем");
        String result = fleet.attemptAddThing(helmet);

        assertNull(result);
        assertEquals(1, fleet.things().size());
    }

    @Test
    void totalDailyEnergyKwh_sumsVehiclesAndEnergyConsumingThings() {
        fleet.attemptAddVehicle(new Scooter("SC-1", "Самокат", 1.5, 5));
        fleet.attemptAddVehicle(new Bicycle("BK-1", "Велосипед", 7));
        fleet.attemptAddThing(new ChargingStation("CH-1", "Зарядка", 2.5));
        fleet.attemptAddThing(new Helmet("HL-1", "Шлем"));

        assertEquals(4.0, fleet.totalDailyEnergyKwh(), 0.001);
    }

    @Test
    void vehiclesForBeginners_returnsOnlySuitableVehicles() {
        fleet.attemptAddVehicle(new Scooter("SC-1", "Простой", 1.0, 8));
        fleet.attemptAddVehicle(new Scooter("SC-2", "Сложный", 1.0, 4));
        fleet.attemptAddVehicle(new Bicycle("BK-1", "Средний", 6));

        var beginners = fleet.vehiclesForBeginners();
        assertEquals(2, beginners.size());
        assertTrue(beginners.stream().allMatch(IVehicle::isSuitableForBeginners));
    }

    @Test
    void removeVehicle_successfullyRemoves() {
        fleet.attemptAddVehicle(new Scooter("SC-1", "Самокат", 1.0, 5));
        boolean removed = fleet.removeVehicle("SC-1");

        assertTrue(removed);
        assertTrue(fleet.vehicles().isEmpty());
    }

    @Test
    void duplicateInventoryNumber_rejected() {
        fleet.attemptAddVehicle(new Scooter("SC-1", "Самокат 1", 1.0, 5));
        String result = fleet.attemptAddVehicle(new Scooter("SC-1", "Самокат 2", 1.0, 5));

        assertNotNull(result);
        assertEquals(1, fleet.vehicles().size());
    }
}