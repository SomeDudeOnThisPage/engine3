package engine3.asset.manifest;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of all asset IDs a scene requires.
 * These will be loaded from the {@link AssetManifest AssetManifests}.
 */
public class SceneManifest {
  private final List<String> assets;

  public List<String> getAssets() {
    return this.assets;
  }

  public SceneManifest() {
    this.assets = new ArrayList<>();
  }
}
