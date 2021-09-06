package engine3.entity.events;

import engine3.entity.Entity;
import engine3.entity.EntityComponent;
import engine3.event.Event;

public class ComponentOperationEvent extends Event {
  public enum Type {
    ADD,
    REMOVE
  }

  public Type type;
  public Entity entity;
  public Class<? extends EntityComponent> clazz;
  public EntityComponent component;

  public ComponentOperationEvent(Type type, Entity entity, Class<? extends EntityComponent> component) {
    super("ComponentOperation", Flags.DEFERRED);
    this.type = type;
    this.entity = entity;
    this.clazz = component;
  }

  public ComponentOperationEvent(Type type, Entity entity, EntityComponent component) {
    super("ComponentOperation", Flags.DEFERRED);
    this.type = type;
    this.entity = entity;
    this.component = component;
  }
}
