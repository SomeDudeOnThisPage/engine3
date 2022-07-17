package engine3.entity;

import engine3.Engine4;
import engine3.entity.api.IEntity;
import engine3.entity.events.ComponentOperationEvent;
import engine3.events.SceneGraphModificationEvent;
import engine3.util.DeferredArrayList;
import engine3.util.DeferredHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class Entity extends AbstractEventBinding implements IEntity {
  // IEntity
  protected String identifier;
  private final DeferredHashMap<Class<? extends EntityComponent>, EntityComponent> components = new DeferredHashMap<>();

  // IFlags
  private final EnumSet<IEntity.Flags> flags;

  // ISceneGraphNode
  private IEntity parent;
  private final List<IEntity> children = new DeferredArrayList<>();

  @Override
  public boolean isFlagged(IEntity.Flags flag) {
    return this.flags.contains(flag);
  }

  @Override
  public IEntity setFlag(IEntity.Flags flag) {
    this.flags.add(flag);
    return this;
  }

  @Override
  public IEntity unsetFlag(IEntity.Flags flag) {
    this.flags.remove(flag);
    return this;
  }

  public List<IEntity> getChildren() {
    return this.children;
  }

  @Override
  public void addChild(IEntity entity) {
    // re-set parent of child if the entity to be added already has a parent
    if (entity.getParent() != null && !this.children.contains(entity)) {
      entity.getParent().getChildren().remove(entity);
      ((DeferredArrayList<IEntity>) entity.getParent().getChildren()).update();

      entity.setParent(this);
      this.children.add(entity);
    }

    // if parent of entity is null just add it as child
    if (entity.getParent() == null && !this.children.contains(entity)) {
      entity.setParent(this);
      this.children.add(entity);
    }

    Engine4.getEventBus().publish(new SceneGraphModificationEvent(entity, entity.getParent()));
    ((DeferredArrayList<IEntity>) this.children).update();
  }

  @Override
  public void removeChild(IEntity entity) {
    this.children.remove(entity);
  }

  @Override
  public void setParent(IEntity entity) {
    this.parent = entity;
  }

  @Override
  public IEntity getParent() {
    return this.parent;
  }

  @Override
  public String toString() {
    return this.identifier;
  }

  @Override
  public String getIdentifier() {
    return this.identifier;
  }

  @Override
  public <T extends EntityComponent> Entity addComponent(T component) {
    this.components.put(component.getClass(), component);
    component.onComponentAttached(this);
    this.setFlag(Flags.COMPONENT_CHANGE);

    Engine4.getEventBus().publish(new ComponentOperationEvent(ComponentOperationEvent.Type.ADD, this, component));

    return this;
  }

  @Override
  public <T extends EntityComponent> boolean hasComponent(Class<T> component) {
    return this.components.containsKey(component);
  }

  @Override
  public <T extends EntityComponent> T getComponent(Class<T> component) {
    if (!this.hasComponent(component)) {
      throw new UnsupportedOperationException("attempted to retrieve non-existent component of type '"
          + component.getSimpleName() + "' from entity '" + this + "'");
    }

    return component.cast(this.components.get(component));
  }

  @Override
  public <T extends EntityComponent> IEntity detachComponent(Class<T> component) {
    if (this.hasComponent(component)) {
      this.components.remove(component);
      this.setFlag(Flags.COMPONENT_CHANGE);

      Engine4.getEventBus().publish(new ComponentOperationEvent(ComponentOperationEvent.Type.REMOVE, this, component));
    }

    return this;
  }

  @Override
  public <T extends EntityComponent> IEntity detachComponent(T component) {
    return this.detachComponent(component.getClass());
  }

  public void update() {
    this.components.update();
  }

  public Entity() {
    this(UUID.randomUUID().toString());
  }

  public Entity(@NotNull String identifier) {
    this.identifier = identifier.toLowerCase();
    this.flags = EnumSet.noneOf(IEntity.Flags.class);
  }
}
