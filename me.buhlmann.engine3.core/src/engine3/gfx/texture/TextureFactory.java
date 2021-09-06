package engine3.gfx.texture;

import engine3.asset.AssetReference;
import engine3.asset.api.IAssetFactory;
import engine3.asset.loading.STBLoader;
import engine3.gfx.texture.filter.TextureFilter;
import engine3.gfx.texture.filter.TextureFilterLinear;
import engine3.util.Utils;
import org.joml.Vector2i;

import javax.xml.bind.annotation.*;
import java.lang.reflect.InvocationTargetException;

public class TextureFactory implements IAssetFactory<Texture2D>  {
  private static final String TAG = "texture";

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = TextureFactory.TAG)
  public static final class Meta {
    @XmlAttribute public String id;
    @XmlElement public String source;
    @XmlElement public Size size;
    @XmlElement public Format format;
    @XmlElement public Filter filter;
    @XmlElement public Wrap wrap;

    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Size {
      @XmlAttribute
      public String x;
      @XmlAttribute
      public String y;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Format {
      @XmlAttribute
      public String internal;
      @XmlAttribute
      public String format;
      @XmlAttribute
      public String type;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Filter {
      @XmlAttribute
      public String type;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Wrap {
      @XmlAttribute
      public String wrap;
    }
  }

  @Override
  public String tag() {
    return TextureFactory.TAG;
  }

  @Override
  public AssetReference<Texture2D> createReference(boolean counted) {
    return new AssetReference<>(Texture2D.class, counted);
  }

  @Override
  public Class<?> getXMLDataClass() {
    return TextureFactory.Meta.class;
  }

  private TextureFilter createTextureFilter(String string) {
    try {
      Class<? extends TextureFilter> clazz = TextureUtils.gl_filter.get(string);
      return clazz.getConstructor().newInstance();

    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    }

    return new TextureFilterLinear();
  }

  @Override
  public Texture2D load(Object data) {
    TextureFactory.Meta meta = (TextureFactory.Meta) data;

    System.out.println("ID = " + meta.id);
    System.out.println("SOURCE = " + meta.source);
    System.out.println("FILTER = " + meta.filter.type);
    System.out.println("FORMAT = " + meta.format.format);
    System.out.println("INTERNAL FORMAT = " + meta.format.internal);
    System.out.println("FORMAT TYPE = " + meta.format.type);
    System.out.println("WRAP = " + meta.wrap.wrap);

    Utils.assertRTE(!TextureUtils.gl_formatInternal.containsKey(meta.format.internal),
        "unknown internal format definition");
    Utils.assertRTE(!TextureUtils.gl_type.containsKey(meta.format.type), "unknown format type definition");
    Utils.assertRTE(!TextureUtils.gl_format.containsKey(meta.format.format), "unknown format definition");
    Utils.assertRTE(!TextureUtils.gl_wrap.containsKey(meta.wrap.wrap), "unknown texture wrap definition");

    final TextureWrap wrap = new TextureWrap(TextureUtils.gl_wrap.get(meta.wrap.wrap));
    final TextureFilter filter = this.createTextureFilter(meta.filter.type);
    final TextureFormat format = new TextureFormat(
      TextureUtils.gl_formatInternal.get(meta.format.internal),
      TextureUtils.gl_format.get(meta.format.format),
      TextureUtils.gl_type.get(meta.format.type)
    );


    if (meta.source.isEmpty()) {
      // load empty texture with size definition of size if source is not set
      Vector2i size = new Vector2i(Integer.parseInt(meta.size.x), Integer.parseInt(meta.size.y));
      return new Texture2D(size, format, filter, wrap);
    } else {
      // load texture data if source is set (STBI)
      STBLoader loader = new STBLoader();
      loader.load(meta.source, format.components());
      return new Texture2D(loader, format, filter, wrap);
    }
  }
}
