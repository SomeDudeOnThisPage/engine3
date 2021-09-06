package engine3.entity.api;

import engine3.event.EventBindings;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public interface IEntityCollection extends EventBindings {
  static List<Class<? extends IEntityComponent>> bag(Class<? extends IEntityComponent>... components) {
    return Arrays.asList(components);
  }

  default void onEntityAdded(IEntity entity) { };
  default void onEntityRemoved(IEntity entity) { };

  <T extends IEntityComponent> Class<T>[] components();

  void addEntity(IEntity entity);
  void removeEntity(IEntity entity);
  Collection<IEntity> getEntities();
}
