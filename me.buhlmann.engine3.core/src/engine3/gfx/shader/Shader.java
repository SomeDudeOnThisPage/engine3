package engine3.gfx.shader;

import engine3.asset.AssetBindable;
import engine3.asset.api.ISyncedInitialization;

import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER;

public class Shader extends AssetBindable implements ISyncedInitialization {
  public enum ShaderType {
    VERTEX_SHADER,
    FRAGMENT_SHADER,
    GEOMETRY_SHADER,
    UNDEFINED
  }

  public static final String dir = "platform\\resources\\shaders\\";
  private boolean timed = false;

  private String source;
  private final int type;

  public String getSource() {
    return this.source;
  }

  @Override
  public void destroy() {
    if (glIsShader(this.id)) {
      glDeleteShader(this.id);
    }
  }

  @Override
  public boolean initialize() {
    this.id = glCreateShader(this.type);

    glShaderSource(this.id, this.source /* Append EOF */ + "\0");
    glCompileShader(this.id);

    final boolean compiled = glGetShaderi(this.id, GL_COMPILE_STATUS) == GL_TRUE;

    if (!compiled) {
      System.err.println("could not compile shader:\n\t" + glGetShaderInfoLog(this.id));
    }

    return compiled;
  }

  public Shader(ShaderType type, String source) {
    switch (type) {
      case VERTEX_SHADER -> this.type = GL_VERTEX_SHADER;
      case GEOMETRY_SHADER -> this.type = GL_GEOMETRY_SHADER;
      case FRAGMENT_SHADER -> this.type = GL_FRAGMENT_SHADER;
      default -> throw new RuntimeException("aaarg");
    }

    this.source = source;
  }
}
