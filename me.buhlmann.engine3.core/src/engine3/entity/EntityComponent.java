package engine3.entity;

import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityComponent;

public abstract class EntityComponent extends AbstractEventBinding implements IEntityComponent {
  @Override
  public void onComponentAttached(IEntity entity) { }

  @Override
  public void onComponentDetached(IEntity entity) { }
}
