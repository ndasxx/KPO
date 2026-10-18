package vshkat.di;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContainerTest {

    @Test
    void resolve_unregisteredType_throwsException() {
        Container container = new Container();
        assertThrows(IllegalStateException.class,
                () -> container.resolve(String.class));
    }

    @Test
    void resolve_registeredInstance_returnsInstance() {
        Container container = new Container();
        String instance = "test";
        container.register(String.class, instance);

        assertSame(instance, container.resolve(String.class));
    }
}
