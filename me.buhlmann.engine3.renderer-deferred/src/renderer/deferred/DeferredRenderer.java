package renderer.deferred;

import engine3.entity.EntityComponentSystem;
import engine3.gfx.framebuffer.FrameBuffer;
import engine3.render.IRenderer;
import engine3.render.entity.ICamera;

@SuppressWarnings("unused")
public class DeferredRenderer implements IRenderer {

  @Override
  public FrameBuffer getFramebuffer() {
    return null;
  }

  @Override
  public void render(ICamera camera) {

  }

  @Override
  public void initialize(EntityComponentSystem ecs) {

  }

  @Override
  public void destroy(EntityComponentSystem ecs) {

  }

}
