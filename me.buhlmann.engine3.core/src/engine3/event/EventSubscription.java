package engine3.event;

import engine3.event.api.IEvent;

@FunctionalInterface
public interface EventSubscription<T extends IEvent> {
  void onEvent(T event);
}
