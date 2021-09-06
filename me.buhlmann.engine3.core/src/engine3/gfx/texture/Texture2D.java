package engine3.gfx.texture;

import engine3.asset.AssetBindable;
import engine3.asset.api.ISyncedInitialization;
import engine3.asset.loading.STBLoader;
import engine3.gfx.OpenGL;
import engine3.gfx.texture.filter.TextureFilter;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL45C;

import java.nio.ByteBuffer;

public class Texture2D extends AssetBindable implements ISyncedInitialization {

  private final int target;

  private final TextureFormat format;
  private final TextureFilter filter;
  private final TextureWrap wrap;

  private final Vector2i size;

  private STBLoader loader;

  public Vector2i getSize() {
    return this.size;
  }

  @Override
  public void destroy() {
    System.out.println("Destroyed Texture2D with ID '" + this.id + "'");
    GL45C.glDeleteTextures(this.id);
  }

  public int getTarget() {
    return this.target;
  }

  // empty texture initialization
  public Texture2D(Vector2i size, TextureFormat format, TextureFilter filter, TextureWrap wrap) {
    this.target = GL45C.GL_TEXTURE_2D;
    this.format = format;
    this.filter = filter;
    this.wrap = wrap;
    this.size = size;
    this.loader = null;
  }

  // loaded texture initialization
  public Texture2D(STBLoader loader, TextureFormat format, TextureFilter filter, TextureWrap wrap) {
    this(new Vector2i(loader.width(), loader.height()), format, filter, wrap);
    this.loader = loader;
  }

  @Override
  public boolean initialize() {
    this.id = GL45C.glGenTextures(); // create texture
    OpenGL.context().texture2d(this, 0); // bind texture

    if (this.loader != null) { // load texture data to gpu
      GL45C.glTexImage2D(
          this.target,
          0,
          this.format.internal(),
          this.loader.width(),
          this.loader.height(),
          0,
          this.format.type(),
          this.format.data(),
          this.loader.data()
      );
      this.loader.free();
      this.loader = null;
    } else { // load empty texture to gpu
      GL45C.glTexImage2D(
          this.target,
          0,
          this.format.internal(),
          this.size.x,
          this.size.y,
          0,
          this.format.type(),
          this.format.data(),
          (ByteBuffer) null
      );
    }

    this.filter.apply(GL45C.GL_TEXTURE_2D); // don't forget to apply the filter

    OpenGL.context().texture2d(null, 0); // unbind texture again
    return true; // tell asset manager that we have succeeded
  }
}
