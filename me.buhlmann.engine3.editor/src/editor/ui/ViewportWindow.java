package editor.ui;

import engine3.entity.EntityCollection;
import engine3.entity.EntityComponentSystem;
import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityComponent;
import engine3.entity.component.TransformComponent;
import engine3.gfx.texture.Texture2D;
import engine3.render.IRenderer;
import engine3.render.deferred.DeferredRenderer;
import engine3.render.entity.ICamera;
import engine3.scene.SceneTree;
import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Matrix4f;
import org.joml.Vector2i;

public class ViewportWindow extends EditorUI {
  private final EntityCollection transforms = new EntityCollection() {
    @Override
    public <T extends IEntityComponent> Class<T>[] components() {
      return new Class[] {
          TransformComponent.class
      };
    }
  };

  private final DeferredRenderer renderer;
  private Vector2i size;
  private boolean focused;
  private SceneTree scene;

  public boolean isFocused() {
    return this.focused;
  }

  public Vector2i getSize() {
    return this.size;
  }

  public ViewportWindow(SceneTree scene, IRenderer renderer, ChildPosition position, EntityComponentSystem ecs) {
    super("Scene", ImGuiWindowFlags.None, position);
    this.renderer = (DeferredRenderer) renderer;
    this.scene = scene;
    this.size = new Vector2i(100, 100);

    ecs.add(this.transforms);
  }

  private int guizmoMode = 0;

  @Override
  public void render() {
    // render framebuffer texture
    Texture2D texture = renderer.getGBuffer().getColorTexture(0);

    if (texture == null) return;

    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
    ImGui.setNextWindowBgAlpha(1.0f);
    int flags = ImGuiWindowFlags.NoCollapse |
        ImGuiWindowFlags.NoScrollWithMouse |
        ImGuiWindowFlags.NoScrollbar;

    float fx = Math.max(texture.getSize().x, 800.0f);
    float fy = Math.max(texture.getSize().y, 600.0f);

    ImGui.setNextWindowSize(fx, fy, ImGuiCond.Always);
    ImGui.begin(this.title, flags);

    float x = ImGui.getWindowWidth();
    float sx = Math.min(texture.getSize().x, x);
    float sy = texture.getSize().y;

    ImGui.beginChild("s", sx, sy);

    this.focused = ImGui.isWindowFocused() | ImGui.isWindowHovered();
    ImGuizmo.setOrthographic(false);
    ImGuizmo.setEnabled(this.focused);
    ImGuizmo.setDrawList();
    ImGui.image(texture.id(), sx, sy, /* flip vertical uv coordinates */ 0, 1, 1, 0);

    float windowWidth = ImGui.getWindowWidth();
    float windowHeight = ImGui.getWindowHeight();
    ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), windowWidth, windowHeight);

    ICamera camera = this.scene.getViewportCamera();
    float[] view = camera.getTransform().getTransformMatrix().get(new float[16]);
    float[] projection = camera.getProjection().getProjectionMatrix().get(new float[16]);
    float[] identity = new Matrix4f().identity().get(new float[16]);

    ImGuizmo.drawGrid(view, projection, identity, 100);
    ImGuizmo.setId(0);

    float viewManipulateRight = ImGui.getWindowPosX() + windowWidth;
    float viewManipulateTop = ImGui.getWindowPosY();
    ImGuizmo.viewManipulate(view, 8, new float[]{viewManipulateRight - sx / 8.0f, viewManipulateTop}, new float[]{sx / 8.0f, sx / 8.0f}, 0x10101010);
    camera.getTransform().setTransformMatrix(new Matrix4f().set(view));

    for (IEntity entity : this.transforms.getEntities()) {
      if (entity.getIdentifier().equals("cube")) {
        float[] model = entity.getComponent(TransformComponent.class).getTransformMatrix().get(new float[16]);
        ImGuizmo.setId(1);

        int mode = 0;
        if (this.guizmoMode == 0) {
          mode |= Operation.TRANSLATE;
        } else if (this.guizmoMode == 1) {
          mode |= Operation.ROTATE;
        } else if (this.guizmoMode == 2) {
          mode |= Operation.SCALE;
        } else if (this.guizmoMode == 3) {
          mode |= Operation.TRANSLATE | Operation.ROTATE;
        }

        ImGuizmo.manipulate(
            view,
            projection,
            model,
            mode,
            Mode.WORLD
        );

        // update matrix
        entity.getComponent(TransformComponent.class).setTransformMatrix(new Matrix4f().set(model));
      }
    }

    ImGui.setNextWindowBgAlpha(1.0f);
    ImGui.setCursorPos(0, 0);
    if (ImGui.button("Guizmo Mode Switch")) {
      this.guizmoMode = (this.guizmoMode + 1) % 4;
    }

    ImGui.endChild();
    //}

    // resize to fit window
    this.size.set((int) ImGui.getWindowSizeX(), (int) ImGui.getWindowSizeY());
    ImGui.end();
    ImGui.popStyleVar(1);
  }
}
