package vshkat.di;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Container {

    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> factories = new HashMap<>();

    public <T> void register(Class<T> type, T instance) {
        instances.put(type, instance);
    }

    public <T> void registerFactory(Class<T> type, Supplier<T> factory) {
        factories.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        Object existing = instances.get(type);
        if (existing != null) {
            return (T) existing;
        }
        Supplier<?> factory = factories.get(type);
        if (factory != null) {
            T created = (T) factory.get();
            instances.put(type, created);
            return created;
        }
        throw new IllegalStateException("Не зарегистрирован: " + type.getName());
    }
}
