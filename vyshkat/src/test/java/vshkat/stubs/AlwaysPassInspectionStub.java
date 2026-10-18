package vshkat.stubs;

import vshkat.domain.IVehicle;
import vshkat.service.ITechnicalInspection;

public class AlwaysPassInspectionStub implements ITechnicalInspection {
    @Override
    public boolean inspect(IVehicle vehicle) {
        return true;
    }
}
