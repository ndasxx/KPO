package vshkat.stubs;

import vshkat.domain.IVehicle;
import vshkat.service.ITechnicalInspection;

public class AlwaysFailInspectionStub implements ITechnicalInspection {
    @Override
    public boolean inspect(IVehicle vehicle) {
        return false;
    }
}
