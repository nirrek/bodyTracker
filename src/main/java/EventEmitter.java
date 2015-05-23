import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An EventEmitter is responsible for managing a set of listeners and publishing
 * events to those listeners when it is informed that the particular event
 * has occurred. The publication works in a simple multicast fashion.
 */
public class EventEmitter {
    private Map<String, List<Consumer<Payload>>> _listeners = new HashMap<>();

    /**
     * Adds a new listener to be invoked whenever an event of the specified
     * is emitted.
     * @param eventType - The name of the event to listen to
     * @param listener - Function to invoke when the eventType is emitted.
     */
    public ListenerSubscription addListener(String eventType, Consumer<Payload> listener) {
        if (_listeners.get(eventType) == null) {
            _listeners.put(eventType, new ArrayList<>());
        }

        int key = _listeners.get(eventType).size();
        _listeners.get(eventType).add(listener);

        return new ListenerSubscription(this, eventType, key);
    }

    /**
     * Removes all listeners of a given type.
     * @param eventType The name of the event to remove all listeners of.
     */
    public void removeAllListeners(String eventType) {
        List<Consumer<Payload>> listeners = _listeners.get(eventType);
        if (listeners == null) return;
        listeners.clear();
    }

    /**
     * Emits an event of the given type. All registered handlers for that type
     * will be invoked.
     *
     * Currently the payload is empty as I am not sure how to send arbitrary
     * data payloads in Java.
     * @param eventType The name of the event to emit
     */
    public void emit(String eventType) {
        List<Consumer<Payload>> listeners = _listeners.get(eventType);
        if (listeners == null) return;

        for (Consumer<Payload> listener : listeners) {
            if (listener != null) {
                listener.accept(new Payload());
            }
        }
    }

    /**
     * Returns a list of listeners currently registered for the eventType.
     * @param eventType Name of the event to query
     * @return List of listeners. If no listeners, returns an empty list.
     */
    public List<Consumer<Payload>> listeners(String eventType) {
        if (_listeners.get(eventType) == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(_listeners.get(eventType));
    }

    /**
     * Removes a specific subscription. Don't invoke this directly, it should
     * be invoked by the ListenerSubscription's remove() method instead.
     * @param subscription A subscription returned from a listener registration
     *                     method that is used to identify the subscription.
     */
    public void removeSubscription(ListenerSubscription subscription) {
        String eventType = subscription.eventType;
        int key = subscription.key;

        if (_listeners.get(eventType) == null) return;
        _listeners.get(eventType).set(key, null);
    }
}

/**
 * Event payload
 */
class Payload {}

/**
 * A subscription token for identifying a particular listener subscription.
 */
class ListenerSubscription {
    private EventEmitter emitter;
    public String eventType;
    public int key;

    public ListenerSubscription(EventEmitter emitter, String eventType, int key) {
        this.emitter = emitter;
        this.eventType = eventType;
        this.key = key;
    }

    public void remove() {
        this.emitter.removeSubscription(this);
    }
}
