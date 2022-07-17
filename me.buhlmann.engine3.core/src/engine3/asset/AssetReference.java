package engine3.asset;

import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetFactory;
import engine3.asset.api.IAssetReference;

public class AssetReference<T extends IAsset> implements IAssetReference<T> {
  private T asset;
  private String key;
  private LoadingStage stage;
  private final Class<T> type;

  private IAssetFactory.MetaData meta;

  private final boolean counted;
  private int references;

  public static <T extends IAsset> IAssetReference<T> wrap(String key, T asset, Class<T> type) {
    AssetReference<T> reference = new AssetReference<>(type, false);
    reference.set(asset);
    reference.setKey(key);
    reference.setLoadingStage(LoadingStage.LOADED);
    return reference;
  }

  @SuppressWarnings("unchecked")
  public void set(IAsset asset) {
    this.asset = (T) asset;
  }

  public T get() {
    return this.asset;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getKey() {
    return this.key;
  }

  @Override
  public IAssetFactory.MetaData getMetaData() {
    return this.meta;
  }

  @Override
  public void setMetaData(IAssetFactory.MetaData meta) {
    this.meta = meta;
  }

  public Class<T> getType() {
    return this.type;
  }

  @Override
  public void setLoadingStage(IAssetReference.LoadingStage stage) {
    if (stage.ordinal() > this.stage.ordinal()) {
      this.stage = stage;
    }
  }

  @Override
  public IAssetReference.LoadingStage getLoadingStage() {
    return this.stage;
  }

  public boolean isLoaded() {
    return this.stage.equals(IAssetReference.LoadingStage.LOADED);
  }

  @Override
  public boolean isReferenceCounted() {
    return this.counted;
  }

  @Override
  public int getReferences() {
    return this.references;
  }

  @Override
  public void addReferences(int references) {
    this.references += references;
  }

  public AssetReference(Class<T> type, boolean counted) {
    this.type = type;
    this.stage = IAssetReference.LoadingStage.UNLOADED;
    this.references = 0;
    this.counted = counted;
  }
}
