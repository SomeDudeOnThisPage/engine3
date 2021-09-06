package engine3.render;

import engine3.api.IApplication;
import engine3.entity.api.ISceneGraphNode;
import engine3.gfx.texture.Texture2D;
import engine3.render.entity.ICamera;
import engine3.scene.SceneEntity;
import org.joml.Vector2i;

public interface IRenderer2 {
  final class RenderQueue {
    // sort by material archetype (shader) // TODO
      // operation: bind shader
    // sort by material instance (uniforms, textures)
      // operation: set uniforms, bind textures

    // sort by mesh (vertex array)
      // bind vertex array (construct instanced rendering data if multiple meshes)

    // render! glDrawElements (Instanced)!

    public final void constructRenderQueueFrom(ISceneGraphNode<?> node) {

    }

  }

  final class RenderTask {
    public String material;
    public String mesh;

    public RenderTask(String material, String mesh) {
      this.material = material;
      this.mesh = mesh;
    }
  }

  void initialize(IApplication application);
  void render(SceneEntity root, ICamera camera, Vector2i viewport);

  // indev
  default void render() {}

  /**
   * The renderer should write its' final image data to a texture, which can be a renderbuffer.
   * The lifecycle of the texture itself should be managed by the renderer, and not be its' own asset.
   * @return Texture2D
   */
  default Texture2D getRenderbuffer() {
    return null;
  }
}
