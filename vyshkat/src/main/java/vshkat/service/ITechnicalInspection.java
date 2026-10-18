package vshkat.service;

import vshkat.domain.IVehicle;

public interface ITechnicalInspection {
    boolean inspect(IVehicle vehicle);
}