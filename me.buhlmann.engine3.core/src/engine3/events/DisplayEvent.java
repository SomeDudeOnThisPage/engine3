package engine3.events;

import engine3.event.Event;

public final class DisplayEvent extends Event {
  public DisplayEvent() {
    super("Display", Event.Flags.DEFERRED);
  }
}
