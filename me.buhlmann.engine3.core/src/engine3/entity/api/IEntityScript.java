package engine3.entity.api;

public interface IEntityScript extends IEntityComponent {
  IEntity getEntity();
  void setEntity(IEntity entity);

  void update(float dt);
}
