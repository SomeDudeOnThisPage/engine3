package engine3.asset.api;

import engine3.asset.Asset;
import engine3.asset.AssetReference;

public interface IAssetFactory<T extends IAsset> {
  /**
   * Should return the XML-Tag this factory should handle.
   *
   * @return .
   */
  String tag();

  /**
   * Should return a typed asset reference instanced with the default asset!
   * @return .
   */
  AssetReference<T> createReference(boolean counted);

  /**
   * Should return the class that the XML-Data should be mapped to.
   *
   * @return .
   */
  Class<?> getXMLDataClass();

  /**
   * Should load an {@link Asset} <strong>asynchronously</strong>, meaning no context-specific operations, like
   * OpenGL calls should be performed.
   * If an asset needs to be instanced in a context environment, it should implement {@link ISyncedInitialization}.
   * The {@link ISyncedInitialization#initialize()} method is called on the main thread which manages the render thread.
   *
   * @param data Mapped XML-Data-Object of type retrieved from {@link IAssetFactory#getXMLDataClass()}
   * @return Asset (which may be not yet initialized via context)
   */
  T load(Object data);
}
