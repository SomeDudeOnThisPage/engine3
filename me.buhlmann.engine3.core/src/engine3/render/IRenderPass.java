package engine3.render;

import engine3.entity.EntityComponentSystem;
import engine3.render.entity.ICamera;

public interface IRenderPass {
   void initialize(final EntityComponentSystem ecs);
   void destroy(final EntityComponentSystem ecs);

  void render(final ICamera camera);
}
