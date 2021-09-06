package engine3.entity;

import engine3.entity.api.IEntity;
import engine3.scene.SceneEntity;

import java.util.*;

public abstract class SceneLockedEntityCollection extends EntityCollection {
  private final Map<String, Set<IEntity>> entities = new HashMap<>();

  private IEntity searchParentScene(IEntity entity) {
    // find parent scene
    IEntity parent = entity.getParent();
    while (!(parent instanceof SceneEntity) && parent != null) {
      parent = parent.getParent();
    }

    if (parent == null) {
      System.out.println("could not find parent");
      return null;
    }

    if (!this.entities.containsKey(parent.getIdentifier())) {
      this.entities.put(parent.getIdentifier(), new HashSet<>());
    }

    return parent;
  }

  @Override
  public void addEntity(IEntity entity) {
    final IEntity scene = this.searchParentScene(entity);
    System.out.println("adding entity to scene");
    if (scene != null) {
      System.out.println("added entity to scene " + scene.getIdentifier());
      this.entities.get(scene.getIdentifier()).add(entity);
    }
  }

  @Override
  public void removeEntity(IEntity entity) {
    final IEntity scene = this.searchParentScene(entity);
    if (scene != null) {
      this.entities.get(scene.getIdentifier()).remove(entity);

      if (this.entities.get(scene.getIdentifier()).size() <= 0) {
        this.entities.remove(scene.getIdentifier());
      }
    }
  }

  public Collection<IEntity> getEntities(String scene) {
    return this.entities.get(scene);
  }

  @Override
  public Collection<IEntity> getEntities() {
    return null;
  }
}
