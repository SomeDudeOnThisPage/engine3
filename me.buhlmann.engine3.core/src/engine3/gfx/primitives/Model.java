package engine3.gfx.primitives;

import engine3.Engine4;
import engine3.asset.Asset;
import engine3.asset.api.IAssetReference;

import java.util.ArrayList;
import java.util.List;

public class Model extends Asset {
  public List<IAssetReference<Mesh>> meshes;

  @Override
  public void destroy() {
    for (IAssetReference<Mesh> mesh : this.meshes) {
      Engine4.getAssetManager().release(mesh);
    }
  }

  public Model(String... meshes) {
    this.meshes = new ArrayList<>();

    for (String mesh : meshes) {
      this.meshes.add(Engine4.getAssetManager().request(Mesh.class, mesh));
    }
  }
}
