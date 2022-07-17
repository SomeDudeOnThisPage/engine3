package engine3.entity.component;

import engine3.Engine4;
import engine3.asset.api.IAssetReference;
import engine3.entity.EntityComponent;
import engine3.entity.api.IEntity;

import engine3.gfx.primitives.Model;

public final class GeometryComponent extends EntityComponent {
  public IAssetReference<Model> model;

  @Override
  public void onComponentDetached(IEntity entity) {
    Engine4.getAssetManager().release(this.model);
  }

  public GeometryComponent(String model) {
    this.model = Engine4.getAssetManager().request(Model.class, model);
  }
}
