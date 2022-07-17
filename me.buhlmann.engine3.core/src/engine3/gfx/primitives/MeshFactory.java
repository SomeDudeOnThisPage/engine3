package engine3.gfx.primitives;

import engine3.asset.AssetReference;
import engine3.asset.api.IAssetFactory;
import engine3.asset.api.IAssetReference;
import engine3.asset.loading.Assimp;
import org.lwjgl.assimp.AIMesh;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

public class MeshFactory implements IAssetFactory<Mesh, MeshFactory.Meta> {
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = "mesh")
  public static final class Meta extends MetaData {
    @XmlElement public String file;

    @Override
    public void getAssociatedFiles(List<String> files) {
      files.add(this.file);
    }
  }

  @Override
  public String tag() {
    return "mesh";
  }

  @Override
  public AssetReference<Mesh> createReference(boolean counted) {
    return new AssetReference<>(Mesh.class, counted);
  }

  @Override
  public Class<?> getXMLDataClass() {
    return MeshFactory.Meta.class;
  }

  @Override
  public Mesh loadAssetSynchronous(Object data) {
    MeshFactory.Meta meta = (MeshFactory.Meta) data;
    final List<AIMesh> meshes = Assimp.load(meta.file);
    return new Mesh(meshes);
  }

  @Override
  public void load(Meta meta, IAssetReference<Mesh> reference) {

  }
}
