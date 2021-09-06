package engine3.gfx.texture;

import engine3.gfx.texture.filter.TextureFilter;
import engine3.gfx.texture.filter.TextureFilterBilinear;
import engine3.gfx.texture.filter.TextureFilterLinear;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL45C.*;

public final class TextureUtils {
  public static final Map<String, Integer> gl_formatInternal = new HashMap<>() {{
    // XXXX16F
    put("R16F",     GL_R16F     );
    put("RG16F",    GL_RG16F    );
    put("RGB16F",   GL_RGB16F   );
    put("RGBA16F",  GL_RGBA16F  );

    // XXXX32F
    put("R32F",     GL_R32F         );
    put("RG32F",    GL_RG32F        );
    put("RGB32F",   GL_RGB32F       );
    put("RGBA32F",  GL_RGBA32F      );
    put("SRGB8",    GL_SRGB8        );
    put("SRGB8A8",  GL_SRGB8_ALPHA8 );

    // simple
    put("RED",  GL_RED );
    put("RG",   GL_RG  );
    put("RGB",  GL_RGB );
    put("RGBA", GL_RGBA);
    put("SRGB", GL_SRGB);
  }};

  public static final Map<String, Integer> gl_format = new HashMap<>() {{
    put("RED",  GL_RED );
    put("RG",   GL_RG  );
    put("RGB",  GL_RGB );
    put("RGBA", GL_RGBA);
    put("SRGB", GL_SRGB);
  }};

  public static final Map<String, Integer> gl_type = new HashMap<>() {{
    put("float",  GL_FLOAT        );
    put("int",    GL_INT          );
    put("uint",   GL_UNSIGNED_INT );
    put("byte",   GL_BYTE         );
    put("ubyte",  GL_UNSIGNED_BYTE);
  }};

  public static final Map<String, Integer> gl_wrap = new HashMap<>() {{
    put("border", GL_CLAMP_TO_BORDER);
    put("edge",   GL_CLAMP_TO_EDGE);
    put("repeat", GL_REPEAT);
  }};

  public static final Map<String, Class<? extends TextureFilter>> gl_filter = new HashMap<>() {{
    put("linear", TextureFilterLinear.class);
    put("bilinear", TextureFilterBilinear.class);
  }};
}
