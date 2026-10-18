package vshkat.service;

import vshkat.domain.IVehicle;

public class ServiceCenter implements ITechnicalInspection {

    private static final double MAX_DAILY_ENERGY_KWH = 100.0;

    @Override
    public boolean inspect(IVehicle vehicle) {
        if (vehicle.name() == null || vehicle.name().isBlank()) {
            return false;
        }
        if (vehicle.dailyEnergyKwh() > MAX_DAILY_ENERGY_KWH) {
            return false;
        }
        if (vehicle.beginnerFriendliness() < 2) {
            return false;
        }
        return true;
    }
}