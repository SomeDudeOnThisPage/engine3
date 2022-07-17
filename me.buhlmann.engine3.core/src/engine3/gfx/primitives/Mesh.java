package engine3.gfx.primitives;

import engine3.asset.Asset;
import engine3.asset.api.ISyncedInitialization;
import engine3.asset.loading.Assimp;
import engine3.gfx.buffer.VertexArray;
import org.lwjgl.assimp.AIMesh;

import java.util.ArrayList;
import java.util.List;

public class Mesh extends Asset implements ISyncedInitialization {
  public List<VertexArray> vao;
  private List<AIMesh> meshdata;

  @Override
  public void destroy() {
    // Engine4.getAssetManager().release(this.vao);
    // Engine4.getAssetManager().release(this.material);
  }

  public Mesh(List<AIMesh> meshdata) {
    this.vao = new ArrayList<>();
    this.meshdata = meshdata;
  }

  @Override
  public boolean initialize() {
    for (AIMesh meshdata : this.meshdata) {
      VertexArray vao = Assimp.processMesh(meshdata);
      this.vao.add(vao);
      meshdata.free();
    }
    this.meshdata = null;
    return true;
  }
}
