package engine3.asset;

import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetReference;
import engine3.asset.api.IDestructible;

import java.util.HashMap;

public class AssetFamily<T extends IAsset> implements IDestructible {
  private final HashMap<String, IAssetReference<T>> assets;

  public T get(String asset) {
    if (this.assets.containsKey(asset)) {
      return this.assets.get(asset).get();
    }
    throw new RuntimeException("asset with key '" + asset + "' not found");
  }

  public IAssetReference<T> getReference(String key) {
    return this.assets.get(key);
  }

  public void addReference(IAssetReference<T> reference) {
    this.assets.put(reference.getKey(), reference);
  }

  public void put(String key, IAssetReference<T> reference) {
    this.assets.put(key, reference);
  }

  @SuppressWarnings("unchecked")
  public void load(String key, IAsset asset) {
    this.assets.get(key).set((T) asset);
  }

  public AssetFamily() {
    this.assets = new HashMap<>();
  }

  @Override
  public void destroy() {
    // destroy all assets
    for (IAssetReference<T> reference : this.assets.values()) {
      reference.get().destroy();
    }
  }
}
