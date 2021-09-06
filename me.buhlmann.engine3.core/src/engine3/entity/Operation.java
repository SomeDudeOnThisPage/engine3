package engine3.entity;

import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityCollection;

public abstract class Operation {
  public enum Type {
    ADD,
    REMOVE,
    MAP
  }

  public static final class OnEntity extends Operation {
    public IEntity entity;

    public OnEntity(IEntity entity, Type type) {
      super(type);
      this.entity = entity;
    }
  }

  public static final class OnCollection extends Operation {
    public IEntityCollection collection;

    public OnCollection(IEntityCollection collection, Type type) {
      super(type);
      this.collection = collection;
    }
  }

  public Type type;

  public Operation(Type type) {
    this.type = type;
  }
}
