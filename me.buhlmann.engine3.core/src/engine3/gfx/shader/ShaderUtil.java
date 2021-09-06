package engine3.gfx.shader;

import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShaderUtil {
  public static final class ShaderConfig {
    public String SHADER_LIB_ROOT;
    public short GLSL_VERSION;
    public boolean GLSL_VERSION_CORE;
    public boolean COMMENT_INCLUDE;
  }

  private static final Pattern PATTERN_INCLUDE = Pattern.compile("\\s*#include\\s+[\",<](.*)[\",>]");
  private static final Pattern PATTERN_VERSION = Pattern.compile("\\s*#version\\s+.*\\n");

  private static String loadResourceToString(final String fp) {
    final File file = new File(fp);
    try {
      return Files.readString(file.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("fucko bucko");
  }

  /**
   * Builds a shader source recursively, excluding any already included shader sources.
   *
   * @param file     The path to the current file to be included.
   * @param config   {@link ShaderConfig Configuration} struct holding information about the way shaders are loaded.
   * @param included {@link List} of already included shader sources.
   * @return shader code string
   */
  private static String buildShaderSource(final String file, final ShaderConfig config, final List<String> included) {

    // Allow dot notation. This means that, for instance, all of the following include statements are valid and equal:
    //  #include "common/shared.glsl"
    //  #include <common/shared.glsl>
    //  #include "common.shared.glsl"
    //  #include <common.shared.glsl>
    String path = file.replaceAll("\\.(?=.*\\.)", "\\" + File.separator);
    String source = ShaderUtil.loadResourceToString(config.SHADER_LIB_ROOT + "\\" + path);

    // match include preprocessor directives
    final Matcher matcher = PATTERN_INCLUDE.matcher(source);

    // only care about first
    while (matcher.find()) {
      final String includeShaderPath = matcher.group(1);
      String includeShaderString = (config.COMMENT_INCLUDE) ? "// FILE " + includeShaderPath + "\n" : "";

      // load included if it's not included yet
      if (!included.contains(includeShaderPath)) {
        included.add(includeShaderPath);

        // replace include-directive with included shader string
        includeShaderString += ShaderUtil.buildShaderSource(includeShaderPath, config, included);

        if (config.COMMENT_INCLUDE) {
          includeShaderString += "\n// EOF \"" + includeShaderPath + "\"";
        }
      }

      source = source.replaceFirst(matcher.group(0), includeShaderString + "\n");
    }

    return source;
  }

  public static String buildShaderSource(final String file, final ShaderConfig config) {
    final String source = ShaderUtil.buildShaderSource(file, config, new ArrayList<>());

    final StringBuilder string = new StringBuilder();

    // append custom version
    string.append("#version ");
    string.append(config.GLSL_VERSION);
    string.append(' ');
    if (config.GLSL_VERSION_CORE) {
      string.append("core");
    }
    string.append('\n');

    // append shader source with version directives removed (just in case there are some...)
    string.append(PATTERN_VERSION.matcher(source).replaceAll(""));
    return string.toString();
  }
}
