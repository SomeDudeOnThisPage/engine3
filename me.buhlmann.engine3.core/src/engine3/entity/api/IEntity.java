package engine3.entity.api;

import engine3.core.IFlags;
import engine3.entity.EntityComponent;

public interface IEntity extends IFlags<IEntity.Flags, IEntity>, ISceneGraphNode<IEntity> {
  enum Flags {
    COMPONENT_CHANGE
  }

  String getIdentifier();

  <T extends EntityComponent> IEntity addComponent(T component);

  <T extends EntityComponent> boolean hasComponent(Class<T> component);
  <T extends EntityComponent> T getComponent(Class<T> component);

  <T extends EntityComponent> IEntity detachComponent(Class<T> component);
  <T extends EntityComponent> IEntity detachComponent(T component);
}
