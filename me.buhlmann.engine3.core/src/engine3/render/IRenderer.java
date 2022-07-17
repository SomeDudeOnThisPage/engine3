package engine3.render;

import engine3.entity.EntityComponentSystem;
import engine3.gfx.framebuffer.FrameBuffer;
import engine3.render.entity.ICamera;
import org.joml.Vector2i;

/**
 * Base interface all custom renderers must inherit.
 * The {@link IRenderer} renders data to a Framebuffer, which is subsequently displayed
 * by the application, either in the main window or a textured sub-window, for instance
 * provided by ImGUI.
 */
public interface IRenderer {
  /**
   * Retrieves the main scene {@link FrameBuffer} the data has been rendered to. The output texture must be rendered
   * to the first attached color texture. The texture may be a renderbuffer.
   * @return The main {@link FrameBuffer}.
   */
  FrameBuffer getFramebuffer();

  /**
   * Performs a full render of the {@link engine3.scene.IScene}.
   * This render may encompass several sub-render passes and operations.
   * This method is always called by the main {@link Thread} managing the
   * {@link engine3.gfx.OpenGL} context.
   * @param camera The {@link ICamera} the scene should be rendered from.
   */
  void render(final ICamera camera);

  /**
   * Utility method to register all required {@link engine3.entity.api.IEntityCollection EntityCollections} to the
   * {@link EntityComponentSystem}, that this renderer may need.
   * @param ecs The {@link EntityComponentSystem} of the {@link engine3.scene.IScene Scene} this {@link IRenderer} is
   *            attached to.
   */
  void initialize(final EntityComponentSystem ecs);

  /**
   * Utility method to unregister all required {@link engine3.entity.api.IEntityCollection EntityCollections} to the
   * {@link EntityComponentSystem}, that this renderer may need. This method should also be used to destroy any
   * {@link engine3.asset.api.IAsset Assets} that this {@link IRenderer} uses, like the {@link FrameBuffer} used
   * to render the {@link engine3.scene.IScene Scene}.
   * @param ecs The {@link EntityComponentSystem} of the {@link engine3.scene.IScene Scene} this {@link IRenderer} is
   *            attached to.
   */
  void destroy(final EntityComponentSystem ecs);
}
