package engine3.asset;

import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetReference;
import org.w3c.dom.Node;

public class AssetLoadTask<T extends IAsset> {
  private final Object data;
  private final IAssetReference<T> reference;

  public Object getData() {
    return this.data;
  }

  public IAssetReference<T> getReference() {
    return this.reference;
  }

  public AssetLoadTask(Object data, IAssetReference<T> reference) {
    this.data = data;
    this.reference = reference;
  }
}
