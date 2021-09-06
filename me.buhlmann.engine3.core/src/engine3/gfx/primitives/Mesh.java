package engine3.gfx.primitives;

import engine3.Engine3;
import engine3.asset.Asset;
import engine3.asset.api.IAssetReference;
import engine3.gfx.buffer.VertexArray;
import engine3.gfx.material.Material;

public class Mesh extends Asset {
  public IAssetReference<VertexArray> vao;
  public IAssetReference<Material> material;

  @Override
  public void destroy() {
    Engine3.ASSET_MANAGER.release(this.vao);
    Engine3.ASSET_MANAGER.release(this.material);
  }

  public Mesh(String vao, String material) {
    this.vao = Engine3.ASSET_MANAGER.request(VertexArray.class, vao);
    this.material = Engine3.ASSET_MANAGER.request(Material.class, material);
  }
}
