package engine3.entity.api;

public interface IEntitySystem extends IEntityCollection {
  enum SystemPriority {
    SYNC_PHYSICS_PRE,
    LEVEL_0,
    LEVEL_1,
    LEVEL_2,
    LEVEL_3,
    LEVEL_4,
    SYNC_PHYSICS_POST
  }

  default SystemPriority priority() {
    return SystemPriority.LEVEL_2;
  }
  void update(float dt);
}
