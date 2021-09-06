package engine3.events;

import engine3.entity.api.IEntity;
import engine3.event.Event;
import engine3.event.api.IEvent;

public class SceneGraphModificationEvent extends Event {
  public final IEntity child;
  public final IEntity parent;

  public SceneGraphModificationEvent(IEntity child, IEntity parent) {
    super("SceneGraphModificationEvent", IEvent.Flags.DEFERRED);
    this.child = child;
    this.parent = parent;
  }
}
