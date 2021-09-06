package engine3.asset;

import engine3.asset.api.IAsset;
import engine3.asset.api.IDestructible;

public abstract class Asset implements IAsset {
  private String key;

  @Override
  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public String getKey() {
    return this.key;
  }
}
