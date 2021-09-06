package engine3.gfx.texture;

public class TextureWrap {
  private final int s;
  private final int t;

  public int s() {
    return this.s;
  }

  public int t() {
    return this.t;
  }

  public TextureWrap(int both) {
    this.s = both;
    this.t = both;
  }

  public TextureWrap(int s, int t) {
    this.s = s;
    this.t = t;
  }
}
