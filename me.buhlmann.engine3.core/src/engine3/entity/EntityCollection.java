package engine3.entity;

import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityCollection;

import java.util.Collection;
import java.util.HashSet;

public abstract class EntityCollection extends AbstractEventBinding implements IEntityCollection {

  protected final HashSet<IEntity> entities;

  @Override
  public void addEntity(IEntity entity) {
    if (this.entities.contains(entity)) {
      return;
      // throw new UnsupportedOperationException("cannot add entity " + entity.getIdentifier() + " to collection " + this + " - collection already contains entity");
    }

    this.entities.add(entity);
  }

  @Override
  public void removeEntity(IEntity entity) {
    if (!this.entities.contains(entity)) {
      return;
      // throw new UnsupportedOperationException("cannot remove entity " + entity.getIdentifier() + " from collection " + this + " - collection does not contain entity");
    }

    this.entities.remove(entity);
  }

  @Override
  public Collection<IEntity> getEntities() {
    return this.entities;
  }

  protected EntityCollection() {
    this.entities = new HashSet<>();
  }
}
