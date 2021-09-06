package engine3.event;

import engine3.event.api.IEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Example Usage:<br>
 * class MyClass {<br>
 *   {@code @EventListener(MyEvent.class)}<br>
 *   public void myCallback(MyEvent event) {<br>
 *   // stuff<br>
 *   }<br>
 * }<br>
 *
 * Note that the event listeners still need to be registered. Either extend {@link EventBindings} and use
 * {@link EventBindings#register(EventBindings)} or do the reflection yourself.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventListener {
  Class<? extends IEvent> value();
}
