package engine3.asset;

import engine3.Engine4;
import engine3.asset.api.IAsset;
import engine3.asset.api.IAssetFactory;
import engine3.asset.api.IAssetReference;
import engine3.asset.api.ISyncedInitialization;
import engine3.asset.manifest.AssetManifest;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public final class AssetManager {
  private final AssetLoadThread thread;

  private final Map<String, AssetManifest> manifests;

  private final HashMap<Class<? extends IAsset>, AssetFamily<? extends IAsset>> families;
  private final HashMap<Class<? extends IAsset>, IAssetFactory<? extends IAsset, ? extends IAssetFactory.MetaData>> factories;

  public final Queue<AssetLoadTask<? extends IAsset>> queue;
  public final List<IAssetReference<? extends IAsset>> loaded;

  public void addAssetManifest(final String path) {
    final AssetManifest manifest = new AssetManifest(path);
    this.manifests.put(path, manifest);
  }

  public void loadManifestAssets(final String key) {
    final AssetManifest manifest = this.manifests.get(key);
    if (manifest != null) {
      for (IAssetReference<? extends IAsset> reference : manifest.getAssetReferences()) {
        AssetDefinitionFile.load(reference, this);
      }
    }
  }

  public void saveManifestAssets(final String key) {
    final AssetManifest manifest = this.manifests.get(key);
    if (manifest != null) {
      manifest.write("soos");
    }
  }

  @SuppressWarnings("unchecked")
  public <T extends IAsset> void addAssetReference(IAssetReference<T> reference) {
    System.out.println("ADDED ASSET REFERENCE " + reference.getKey());
    AssetFamily<T> family = (AssetFamily<T>) this.families.get(reference.getType());
    family.addReference(reference);
  }

  public void loadSynchronous(IAssetReference<? extends IAsset> reference) {
    this.loaded.add(reference);
  }

  @SuppressWarnings("unchecked")
  public <T extends IAsset> IAssetReference<T> request(Class<T> family, String key) {
    AssetFamily<? extends IAsset> assetFamily = this.families.get(family);
    if (assetFamily == null) throw new RuntimeException("TODO: Make this a special exception with more info");

    IAssetReference<T> reference = (IAssetReference<T>) assetFamily.getReference(key);
    if (reference == null) {
      Engine4.getLogger().info("[ASSET] could not find asset reference with key " + key + " in family " + family + " - attempting to load from manifest");

      for (final AssetManifest manifest : this.manifests.values()) {
        if (manifest.getAssetReference(key) != null) {
          AssetDefinitionFile.load(manifest.getAssetReference(key), this);
        }
      }
      throw new RuntimeException("TODO: Make this a special exception with more info " + key);
    }

    reference.addReferences(1);
    return reference;
  }

  public <T extends IAsset> void release(IAssetReference<T> reference) {
    reference.addReferences(-1);
    // todo: check if asset needs to be removed
    if (reference.isReferenceCounted() && reference.getReferences() <= 0) {
      Engine4.getLogger().info("[ASSET] [TODO] unload asset with reference count <= 0");
    }
  }

  @SuppressWarnings({"unchecked", "unused"})
  public <T extends IAsset> void release(Class<T> family, T asset) {
    AssetFamily<? extends IAsset> assetFamily = this.families.get(family);
    if (family != null) {
      IAssetReference<T> reference = (IAssetReference<T>) assetFamily.get(asset.getKey());
      if (reference != null) {
        this.release(reference);
      }
    }
  }

  public <T extends IAsset> void registerAssetType(Class<T> key) {
    Engine4.getLogger().warning("[ASSET] registered asset type '" + key + "' without a factory");
    this.registerAssetFamily(key, new AssetFamily<>());
  }

  public <T extends IAsset, M extends IAssetFactory.MetaData> void registerAssetType(Class<T> key, IAssetFactory<T, M> factory) {
    this.registerAssetFamily(key, new AssetFamily<>());
    this.registerAssetFactory(key, factory);
  }

  public <T extends IAsset> void registerAssetFamily(Class<T> key, AssetFamily<T> family) {
    this.families.put(key, family);
  }

  public <T extends IAsset, M extends IAssetFactory.MetaData> void registerAssetFactory(Class<T> family, IAssetFactory<T, M> factory) {
    this.factories.put(family, factory);
  }

  public IAssetFactory<? extends IAsset, ? extends IAssetFactory.MetaData> getAssetFactory(String tag) {
    for (IAssetFactory<? extends IAsset, ? extends IAssetFactory.MetaData> factory : this.factories.values()) {
      if (factory.tag().equals(tag)) {
        return factory;
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public <T extends IAsset, M extends IAssetFactory.MetaData> IAssetFactory<T, M> getAssetFactory(Class<T> family) {
    return (IAssetFactory<T, M>) this.factories.get(family);
  }

  /**
   * Check if <strong>any</strong> {@link IAsset Asset} is currently loading, either asynchronously, or synchronously.
   * @return Whether <strong>any</strong> {@link IAsset Asset} is currently loading.
   */
  public boolean isLoading() {
    return this.queue.size() > 0 || this.loaded.size() > 0;
  }

  //public void load(String pack) {
  //  AssetDefinitionFile.load(pack, this);
  //}

  /**
   * Loads an {@link IAsset Asset} of a given type to the asset manager, using the given {@link String} key.
   * @param key The {@link String} key of this {@link IAsset Asset}.
   * @param asset The {@link IAsset Asset} - the type of this {@link IAsset Asset} must match the parameter {@code type}.
   * @param type The {@link Class<T> type} of this {@link IAsset Asset}.
   * @param <T> Bounded type parameter, extends {@link IAsset}.
   */
  @SuppressWarnings("unchecked")
  public <T extends IAsset> void load(String key, T asset, Class<T> type, boolean counted) {
    final AssetReference<T> reference = this.getAssetFactory(type).createReference(counted);
    reference.setKey(key);
    reference.set(asset);

    final AssetFamily<T> family = (AssetFamily<T>) this.families.get(type);
    family.addReference(reference);
  }

  @SuppressWarnings("unchecked")
  public <T extends IAsset> AssetFamily<T> getAssetFamily(Class<T> type) {
    if (!this.families.containsKey(type) && Engine4.getConfiguration().unsafe_asset_family_registry) {
      this.registerAssetFamily(type, new AssetFamily<>());
    } else if (!this.families.containsKey(type) && !Engine4.getConfiguration().unsafe_asset_family_registry) {
      throw new UnsupportedOperationException("cannot find asset family of type '" + type + "'");
    }

    return (AssetFamily<T>) this.families.get(type);
  }

  public <T extends IAsset> void load(IAssetReference<T> reference) {
    final AssetFamily<T> family = this.getAssetFamily(reference.getType());
    family.addReference(reference);
  }

  public synchronized <T extends IAsset> void initializeAsset(IAssetReference<T> reference) {
    if (ISyncedInitialization.class.isAssignableFrom(reference.getType())) {
      ((ISyncedInitialization) reference.get()).initialize();
      reference.setLoadingStage(IAssetReference.LoadingStage.LOADED);
      Engine4.getLogger().trace("[ASSET] loaded asset " + reference.getKey());
    }
  }

  public synchronized void update() {
    // add all pre-initialized assets to the asset pool
    this.loaded.forEach((reference) -> {
      this.initializeAsset(reference);
      this.loaded.remove(reference);
    });
  }

  public synchronized void terminate() {
    for (AssetFamily<? extends IAsset> family : this.families.values()) {
      family.destroy();
    }
    this.thread.terminate();
  }

  public AssetManager() {
    this.factories = new HashMap<>();
    this.families = new HashMap<>();

    this.queue = new LinkedBlockingQueue<>();
    this.loaded = new CopyOnWriteArrayList<>();

    this.manifests = new HashMap<>();

    // start loading thread
    this.thread = new AssetLoadThread();
    this.thread.start();
  }
}
