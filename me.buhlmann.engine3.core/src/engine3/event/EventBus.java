package engine3.event;

import engine3.Engine4;
import engine3.event.api.IEvent;
import engine3.events.GLDebugEvent;

import java.util.*;

public final class EventBus {
  private final Queue<IEvent> bus;
  private final HashMap<Class<? extends IEvent>, List<EventSubscription<? extends IEvent>>> subscriptions;

  /**
   * Immediately fires all event subscribers until the event is cancelled
   * @param event event to be fired
   * @param <T> type
   */
  @SuppressWarnings("unchecked")
  private <T extends IEvent> void fire(T event) {
    if (event.isCancelled()) {
      return;
    }

    for (EventSubscription<? extends IEvent> subscription : this.subscriptions.get(event.getClass())) {
      if (!event.isCancelled()) { // check if the event was cancelled by the last subscriber
        ((EventSubscription<T>) subscription).onEvent(event);
      } else {
        break;
      }
    }
  }

  public void registerEvent(Class<? extends IEvent> event) {
    this.subscriptions.put(event, new ArrayList<>());
  }

  public <T extends IEvent> EventSubscription<T> subscribe(Class<T> event, EventSubscription<T> subscription) {
    if (Engine4.getConfiguration().unsafe_event_registry && this.subscriptions.get(event) == null) {
      this.registerEvent(event);
    }

    if (this.subscriptions.get(event) == null) {
      throw new UnsupportedOperationException(
          String.format("failed to subscribe to event of type '%s' - event type is not registered", event));
    }

    this.subscriptions.get(event).add(subscription);

    return subscription;
  }

  public void unsubscribe(Class<? extends IEvent> event, EventSubscription<? extends IEvent> subscription) {
    this.subscriptions.get(event).remove(subscription);
  }

  public void publish(IEvent event) {
    if (Engine4.getConfiguration().unsafe_event_registry && this.subscriptions.get(event.getClass()) == null) {
      this.registerEvent(event.getClass());
    }

    if (this.subscriptions.get(event.getClass()) == null) {
      throw new UnsupportedOperationException(
          String.format("failed to publish event of type '%s' - event type is not registered", event.getClass()));
    }

    if (!event.isCancelled()) {
      if (event.isDeferred()) {
        this.bus.add(event);
      } else {
        this.fire(event);
      }
    }
  }

  // todo: threading
  public <T extends IEvent> void update() {
    while (this.bus.size() > 0) { // okay so this is very bad design
      this.fire(this.bus.poll());
    }
  }

  public EventBus() {
    this.bus = new LinkedList<>();
    this.subscriptions = new HashMap<>();
  }
}
