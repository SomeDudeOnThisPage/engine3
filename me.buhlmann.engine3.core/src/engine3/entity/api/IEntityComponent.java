package engine3.entity.api;

public interface IEntityComponent {
  void onComponentAttached(IEntity entity);
  void onComponentDetached(IEntity entity);
}
