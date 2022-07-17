package engine3.gfx.shader;

import engine3.asset.AssetReference;
import engine3.asset.api.IAssetFactory;
import engine3.asset.api.IAssetReference;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

public class ShaderProgramFactory implements IAssetFactory<ShaderProgram, ShaderProgramFactory.Meta> {
  private static final String TAG = "program";

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = ShaderProgramFactory.TAG)
  public static final class Meta extends MetaData {
    @XmlElement public String vs;
    @XmlElement public String fs;

    @XmlAccessorType(XmlAccessType.FIELD)
    private static final class Output {
      @XmlAttribute public String binding;
      @XmlAttribute public int buffer;
    }

    @XmlElement public List<Output> output;

    @Override
    public void getAssociatedFiles(List<String> files) {
      files.add(this.vs);
      files.add(this.fs);
    }
  }

  public String tag() {
    return ShaderProgramFactory.TAG;
  }

  @Override
  public AssetReference<ShaderProgram> createReference(boolean counted) {
    return new AssetReference<>(ShaderProgram.class, counted);
  }

  public Class<Meta> getXMLDataClass() {
    return Meta.class;
  }

  public ShaderProgram loadAssetSynchronous(Object obj) {
    Meta meta = (Meta) obj;

    // todo: why the fuck is this here and not somewhere in the global config???
    ShaderUtil.ShaderConfig config = new ShaderUtil.ShaderConfig();
    config.GLSL_VERSION = 420;
    config.GLSL_VERSION_CORE = true;
    config.SHADER_LIB_ROOT = Shader.dir;
    config.COMMENT_INCLUDE = true;

    final List<Shader> shaders = new ArrayList<>();
    if (meta.vs != null) {
      shaders.add(new Shader(Shader.ShaderType.VERTEX_SHADER, ShaderUtil.buildShaderSource(meta.vs, config)));
    }

    if (meta.fs != null) {
      shaders.add(new Shader(Shader.ShaderType.FRAGMENT_SHADER, ShaderUtil.buildShaderSource(meta.fs, config)));
    }

    if (meta.output != null) {
      final ShaderProgram.ShaderIO[] io = new ShaderProgram.ShaderIO[meta.output.size()];
      for (int i = 0; i < meta.output.size(); i++) {
        Meta.Output output = meta.output.get(i);
        io[i] = new ShaderProgram.FragmentShaderOutput(output.buffer, output.binding);
      }
      return new ShaderProgram(shaders, io);
    } else {
      return new ShaderProgram(shaders);
    }
  }

  @Override
  public void load(Meta meta, IAssetReference<ShaderProgram> reference) {

  }
}
