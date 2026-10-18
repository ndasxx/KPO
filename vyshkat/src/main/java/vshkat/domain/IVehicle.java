package vshkat.domain;

public interface IVehicle extends IInventoryItem, IEnergyConsumer {
    int beginnerFriendliness();

    default boolean isSuitableForBeginners() {
        return beginnerFriendliness() >= 6;
    }
}
