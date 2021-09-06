package engine3.util;

import com.moandjiezana.toml.Toml;

import java.io.File;

public final class ConfigUtils {
  public static <T> T loadTomlConfig(String path, Class<T> config) {
    File file = new File(path);

    if (!file.exists()) {
      throw new RuntimeException(String.format("could not load configuration '%s' - file '%s' does not exist",
          config.getSimpleName(),
          file.getPath()
      ));
    }

    return new Toml().read(file).to(config);
  }
}
