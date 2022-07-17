package engine3.asset;

import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetFactory;
import engine3.asset.api.IAssetReference;

public interface ISyncedAssetFactory<T extends IAsset, M extends IAssetFactory.MetaData> extends IAssetFactory<T, M> {
  /**
   * Populate asset data asynchronously.
   */
  void instantiate(M meta, IAssetReference<T> reference);
}
