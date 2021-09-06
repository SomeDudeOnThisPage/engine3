package engine3.event;

import engine3.Engine3;
import engine3.event.api.IEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Base-Interface for classes which are intended to use the event-subscriptions via the {@link EventListener} annotation.
 * This isn't strictly needed, this interface simply removes the need to worry about removing and adding event subscriptions
 * manually via reflection.
 */
public interface EventBindings {
  /**
   * Combination storing the event class and event subscription. This is needed as event subscriptions created by
   * annotations need the event class stored to be removed.
   */
  final class CompositeEventSubscription {
    public Class<?> clazz; // event class
    public EventSubscription<? extends IEvent> subscription;

    public CompositeEventSubscription(Class<? extends IEvent> clazz, EventSubscription<? extends IEvent> subscription) {
      this.clazz = clazz;
      this.subscription = subscription;
    }
  }

  /**
   * Removes <b>all</b> {@link EventSubscription EventSubscriptions} created by annotations of an object from the event
   * bus.
   * @param object The object which' {@link EventSubscription EventSubscriptions} are to be removed.
   */
  @SuppressWarnings("unchecked")
  static void unregister(EventBindings object) {
    final List<CompositeEventSubscription> subscriptions = object.getSubscriptions();
    for (CompositeEventSubscription subscription : subscriptions) {
      Engine3.EVENT_BUS.unsubscribe((Class<? extends IEvent>) subscription.clazz, subscription.subscription);
    }
  }

  /**
   * Creates <b>all</b> {@link EventSubscription EventSubscriptions} created by annotations of an object, and adds them
   * to the subscriptions event bus.
   * @param object The object which's {@link EventSubscription EventSubscriptions} are to be created.
   */
  static void register(EventBindings object) {
    final Class<?> clazz = object.getClass();

    // iterate all methods of the class, and check for event listeners
    for (Method method : clazz.getMethods()) {
      if (Engine3.CONFIGURATION.unsafe_event_listening) {
        method.setAccessible(true);
      }

      if (method.isAnnotationPresent(EventListener.class)) {
        EventListener listener = method.getAnnotation(EventListener.class);
        Class<? extends IEvent> event = listener.value();

        // create event subscription that calls the annotated method
        final EventSubscription<? extends IEvent> subscription = Engine3.EVENT_BUS.subscribe(event, (e) -> {
          try {
            method.invoke(object, e);
          } catch (IllegalAccessException exception) {
            System.err.println("attempted to access event listener with non-public modifier - make sure both" +
                "the class and the event callback are public.");
            // todo: log error, event not registered
          } catch (InvocationTargetException exception) {
            // todo: crash, handler nonexistant
            exception.printStackTrace();
          }
        });

        object.bindEventSubscription(new CompositeEventSubscription(event, subscription));
      }
    }
  }

  /** Should return a list of CompositeEventSubscriptions of this object. The object needs to manage this list manually. */
  List<CompositeEventSubscription> getSubscriptions();

  /** Should add the subscription to the list of CompositeEventSubscription of this object. */
  void bindEventSubscription(CompositeEventSubscription subscription);
}
