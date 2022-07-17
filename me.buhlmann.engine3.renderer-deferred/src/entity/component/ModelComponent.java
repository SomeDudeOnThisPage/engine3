package entity.component;

import engine3.asset.api.IAssetReference;
import engine3.entity.EntityComponent;
import engine3.gfx.material.Material;
import engine3.gfx.primitives.Mesh;

import java.util.List;

public class ModelComponent extends EntityComponent {
  public IAssetReference<Mesh> mesh;
  public List<IAssetReference<Material>> material;
}
