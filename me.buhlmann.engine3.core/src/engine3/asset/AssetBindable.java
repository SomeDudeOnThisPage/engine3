package engine3.asset;

import engine3.asset.api.IBindable;

public abstract class AssetBindable extends Asset implements IBindable {
  protected int id;

  public int id() {
    return this.id;
  }
}
