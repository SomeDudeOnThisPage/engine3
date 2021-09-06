package engine3.asset;

import engine3.asset.api.IAsset;
import org.w3c.dom.Node;

public class AssetLoadTask<T extends IAsset> {
  private final Node data;
  private final AssetReference<T> reference;

  public Node getData() {
    return this.data;
  }

  public AssetReference<T> getReference() {
    return this.reference;
  }

  public AssetLoadTask(Node data, AssetReference<T> reference) {
    this.data = data;
    this.reference = reference;
  }
}
