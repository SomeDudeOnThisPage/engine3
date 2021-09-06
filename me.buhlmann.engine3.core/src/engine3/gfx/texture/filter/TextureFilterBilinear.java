package engine3.gfx.texture.filter;

import org.lwjgl.opengl.GL45C;

public class TextureFilterBilinear extends TextureFilter {
  @Override
  public void apply(int target) {
    GL45C.glTexParameteri(target, GL45C.GL_TEXTURE_MIN_FILTER, GL45C.GL_LINEAR);
    GL45C.glTexParameteri(target, GL45C.GL_TEXTURE_MAG_FILTER, GL45C.GL_LINEAR);
  }
}
