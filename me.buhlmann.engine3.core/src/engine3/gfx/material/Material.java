package engine3.gfx.material;

import engine3.Engine3;
import engine3.asset.AssetBindable;
import engine3.asset.api.IAssetReference;
import engine3.gfx.OpenGL;
import engine3.gfx.shader.ShaderProgram;

public abstract class Material extends AssetBindable {

  protected IAssetReference<ShaderProgram> program;

  public abstract void setUniforms();

  @Override
  public void destroy() {
    Engine3.ASSET_MANAGER.release(this.program);
  }

  public final ShaderProgram getProgram() {
    if (this.program.isLoaded()) {
      return this.program.get();
    }
    return null;
  }

  public void bind() {
    if (this.program.isLoaded()) {
      OpenGL.context().shader(this.program.get());
    }
  }

  protected Material(String program) {
    this.program = Engine3.ASSET_MANAGER.request(ShaderProgram.class, program);
  }
}
