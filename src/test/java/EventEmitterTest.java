import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.Assert.*;

public class EventEmitterTest {
    private static EventEmitter emitter;

    @Before
    public void beforeEach() {
        emitter = new EventEmitter();
    }

// An event emitter...
    // should allow a single listener to be added
    @Test
    public void shouldAddListener() {
        emitter.addListener("coolEvent", payload -> {
            // Logic would go here
        });

        assertEquals(1, emitter.listeners("coolEvent").size());
    }

    // should allow multiple listeners for a single event
    @Test
    public void shouldAllowMultipleListeners() {
        emitter.addListener("e", p -> {});
        emitter.addListener("e", p -> {});
        emitter.addListener("e", p -> {});

        assertEquals(3, emitter.listeners("e").size());
    }

    // should allow all listeners of a particular event to be removed
    @Test
    public void shouldRemoveListeners() {
        emitter.addListener("e", payload -> {});
        emitter.addListener("e", payload -> {});
        emitter.removeAllListeners("e");

        assertEquals(0, emitter.listeners("e").size());
    }

    // should invoke all registered listeners
    @Test
    public void shouldInvokeRegisteredListeners() {
        ArrayList<Integer> mutableList = new ArrayList<>();
        emitter.addListener("e", p -> { mutableList.add(1); });
        emitter.addListener("e", p -> { mutableList.add(2); });

        emitter.emit("e");
        assertEquals(2, mutableList.size());
    }

    // should allow specific listeners to be removed.
    @Test
    public void allowSpecificListenerRemoval() {
        ListenerSubscription sub1 =  emitter.addListener("e", p -> {});
        emitter.addListener("e", p -> {});
        emitter.addListener("e", p -> {});

        sub1.remove();
        long count = predicateCount(emitter.listeners("e"), cb -> cb != null);
        assertEquals(2, count);
    }

    // Get the number of items in the list that match the predicate
    private static long predicateCount(List<Consumer<Payload>> list,
                                       Predicate<Consumer<Payload>> p) {
        return list
                .stream()
                .filter(p::test)
                .count();
    }
}