package engine3.asset.api;

public interface IAssetReference<T extends IAsset> extends IReferenceCounted {
  enum LoadingStage {
    UNLOADED,
    LOADING_ASYNCHRONOUS,
    LOADED_ASYNCHRONOUS,
    // INITIALIZING_SYNCHRONOUS // no reason for this to exist, as other threads do not interact with the asset
                                // at this point anymore
    INITIALIZED_SYNCHRONOUS,
    LOADED
  }

  void set(IAsset asset);
  T get();

  boolean isLoaded();
  void setLoadingStage(LoadingStage stage);
  LoadingStage getLoadingStage();

  void setKey(String key);
  String getKey();

  IAssetFactory.MetaData getMetaData();
  void setMetaData(IAssetFactory.MetaData meta);

  /**
   * Returns the {@link Class<T> type} of the contained {@link IAsset Asset}.
   * @return The {@link Class<T> type} of the contained {@link IAsset Asset}.
   */
  Class<T> getType();
}
