package engine3.scene;

import engine3.entity.EntityComponentSystem;
import engine3.entity.api.ISceneGraphNode;
import engine3.render.IRenderer;
import engine3.render.entity.ICamera;

import java.util.ArrayList;
import java.util.List;

public abstract class SceneTree implements ISceneGraphNode<SceneTree> {
  private final EntityComponentSystem ecs;

  private SceneTree parent;
  private final List<SceneTree> children;

  public abstract ICamera getViewportCamera();

  public abstract void onInit();
  public abstract void onRender(float dt);
  public abstract void onUpdate(float dt);

  public final void update(float dt) {
    this.onUpdate(dt);
    this.ecs.update(dt);

    for (SceneTree scene : this.children) {
      scene.update(dt);
    }
  }

  public final <T extends IRenderer> void render(float dt, IRenderer.Stage stage, T renderer) {
    this.onRender(dt);
    this.ecs.updateRenderStage(stage, renderer);

    // also execute all render-systems for the child scenes of this context
    for (SceneTree scene : this.children) {
      scene.render(dt, stage, renderer);
    }
  }

  public final EntityComponentSystem getEntityComponentSystem() {
    return this.ecs;
  }

  @Override
  public final SceneTree getParent() {
    return this.parent;
  }

  @Override
  public final void setParent(SceneTree parent) {
    this.parent = parent;
  }

  @Override
  public List<SceneTree> getChildren() {
    return this.children;
  }

  @Override
  public void addChild(SceneTree child) {
    this.children.add(child);
  }

  @Override
  public void removeChild(SceneTree child) {
    this.children.remove(child);
  }

  public <T extends IRenderer> SceneTree(T renderer) {
    this.ecs = new EntityComponentSystem();
    this.children = new ArrayList<>();

    renderer.registerRenderSystems(this.ecs);
  }
}
