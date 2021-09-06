package editor.imgui;

import engine3.entity.EntityCollection;
import engine3.entity.api.IEntity;
import engine3.entity.api.IEntityComponent;
import engine3.entity.component.TransformComponent;
import engine3.gfx.texture.Texture2D;
import engine3.render.IRenderer;
import engine3.render.IRenderer2;
import engine3.render.deferred.DeferredRenderer;
import engine3.render.entity.ICamera;
import engine3.render.renderer2.ForwardSceneRenderer;
import engine3.scene.SceneEntity;
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

public class SceneViewportGUI extends ImGUI {
  private final EntityCollection transforms = new EntityCollection() {
    @Override
    public <T extends IEntityComponent> Class<T>[] components() {
      return new Class[] {
          TransformComponent.class
      };
    }
  };

  private Vector2i size;

  public Vector2i getSize() {
    return this.size;
  }

  private boolean focused;

  public boolean isFocused() {
    return this.focused;
  }

  @Override
  public void onInit() {
    // Engine3.APPLICATION.getEntityComponentSystem().add(this.transforms);
    this.size = new Vector2i(500, 500);
  }

  public void render2(SceneEntity scene, IRenderer2 renderer2) {
    ForwardSceneRenderer renderer = (ForwardSceneRenderer) renderer2;
    Texture2D texture = renderer.getGBuffer().getColorTexture(0);

    ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
    ImGui.setNextWindowBgAlpha(1.0f);
    int flags = ImGuiWindowFlags.NoCollapse |
        ImGuiWindowFlags.NoScrollWithMouse |
        ImGuiWindowFlags.NoScrollbar;

    float fx = Math.max(texture.getSize().x, 800.0f);
    float fy = Math.max(texture.getSize().y, 600.0f);

    ImGui.setNextWindowSize(fx, fy, ImGuiCond.Always);
    ImGui.begin("Scene " + scene.getIdentifier(), flags);

    float x = ImGui.getWindowWidth();
    float sx = Math.min(texture.getSize().x, x);
    float sy = texture.getSize().y;

    ImGui.beginChild("s" + scene.getIdentifier(), sx, sy);

    this.focused = ImGui.isWindowFocused() | ImGui.isWindowHovered();
    ImGuizmo.setOrthographic(false);
    ImGuizmo.setEnabled(this.focused);
    ImGuizmo.setDrawList();
    ImGui.image(texture.id(), sx, sy, /* flip vertical uv coordinates */ 0, 1, 1, 0);

    float windowWidth = ImGui.getWindowWidth();
    float windowHeight = ImGui.getWindowHeight();
    ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), windowWidth, windowHeight);

    ICamera camera = scene.getViewportCamera();
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

        ImGuizmo.manipulate(
            view,
            projection,
            model,
            Operation.TRANSLATE | Operation.ROTATE,
            Mode.LOCAL
        );

        // update matrix
        entity.getComponent(TransformComponent.class).setTransformMatrix(new Matrix4f().set(model));
      }
    }

    ImGui.setNextWindowBgAlpha(1.0f);
    ImGui.setCursorPos(0, 0);
    ImGui.beginChildFrame(123123213, 100, 100);
    ImGui.endChild();

    ImGui.endChild();
    //}

    // resize to fit window
    this.size.set((int) ImGui.getWindowSizeX(), (int) ImGui.getWindowSizeY());
    ImGui.end();
    ImGui.popStyleVar(1);
  }

  @Override
  public void render(SceneTree scene, IRenderer rendererr) {
    // render framebuffer texture
    DeferredRenderer renderer = (DeferredRenderer) rendererr;
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
    ImGui.begin("Scene " + scene, flags);

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

    ICamera camera = scene.getViewportCamera();
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

        ImGuizmo.manipulate(
            view,
            projection,
            model,
            Operation.TRANSLATE | Operation.ROTATE,
            Mode.LOCAL
        );

        // update matrix
        entity.getComponent(TransformComponent.class).setTransformMatrix(new Matrix4f().set(model));
      }
    }

    ImGui.setNextWindowBgAlpha(1.0f);
    ImGui.setCursorPos(0, 0);
    ImGui.beginChildFrame(123123213, 100, 100);
    ImGui.text("ms/frame: " + ((renderer.qstop - renderer.qstart) / 1000000.0));
    ImGui.endChild();

    ImGui.endChild();
    //}

    // resize to fit window
    this.size.set((int) ImGui.getWindowSizeX(), (int) ImGui.getWindowSizeY());
    ImGui.end();
    ImGui.popStyleVar(1);
  }
}
