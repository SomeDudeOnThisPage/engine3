package engine3.entity;

import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityScript;

public abstract class EntityScript extends EntityComponent implements IEntityScript {
  protected IEntity entity;

  @Override
  public void setEntity(IEntity entity) {
    this.entity = entity;
  }

  @Override
  public IEntity getEntity() {
    return this.entity;
  }
}
