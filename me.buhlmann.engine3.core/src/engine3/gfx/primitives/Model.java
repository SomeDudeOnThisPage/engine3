package engine3.gfx.primitives;

import engine3.Engine3;
import engine3.asset.Asset;
import engine3.asset.api.IAssetReference;

import java.util.ArrayList;
import java.util.List;

public class Model extends Asset {
  public List<IAssetReference<Mesh>> meshes;

  @Override
  public void destroy() {
    for (IAssetReference<Mesh> mesh : this.meshes) {
      Engine3.ASSET_MANAGER.release(mesh);
    }
  }

  public Model(String... meshes) {
    this.meshes = new ArrayList<>();

    for (String mesh : meshes) {
      this.meshes.add(Engine3.ASSET_MANAGER.request(Mesh.class, mesh));
    }
  }
}
