package engine3.entity.component;

import engine3.Engine3;
import engine3.asset.api.IAssetReference;
import engine3.entity.EntityComponent;
import engine3.entity.api.IEntity;

import engine3.gfx.primitives.Model;

public final class GeometryComponent extends EntityComponent {
  public IAssetReference<Model> model;

  @Override
  public void onComponentDetached(IEntity entity) {
    Engine3.ASSET_MANAGER.release(this.model);
  }

  public GeometryComponent(String model) {
    this.model = Engine3.ASSET_MANAGER.request(Model.class, model);
  }
}
