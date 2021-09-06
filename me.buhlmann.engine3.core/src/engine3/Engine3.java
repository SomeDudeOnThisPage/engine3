package engine3;

import engine3.asset.AssetManager;
import engine3.config.EngineConfig;
import engine3.config.InstanceConfig;
import engine3.event.EventBus;
import engine3.gfx.OpenGL;
import engine3.input.IInputManager;
import engine3.input.Input;
import engine3.platform.GLFWWindow;
import engine3.util.Arguments;
import engine3.util.ConfigUtils;
import org.joml.Vector2i;

import java.lang.reflect.InvocationTargetException;

public class Engine3 {
  public static Application APPLICATION;
  public static EngineConfig CONFIGURATION;
  public static InstanceConfig INSTANCE_CONFIGURATION;
  public static EventBus EVENT_BUS;
  public static AssetManager ASSET_MANAGER;

  public static GLFWWindow DISPLAY;

  public static Input INPUT;

  public static IInputManager input() {
    return Engine3.APPLICATION.getInput();
  }

  public static void run(Class<? extends Application> application, String[] args) {
    final Arguments arguments = new Arguments();
    arguments.parse(args);

    // load engine level config
    Engine3.CONFIGURATION = ConfigUtils.loadTomlConfig(arguments.platform + "/engine.toml", EngineConfig.class);
    System.out.println(Engine3.CONFIGURATION + " " + arguments.platform + "/engine.toml");
    Engine3.DISPLAY = new GLFWWindow(new Vector2i(800, 600));

    // load instance level config
    Engine3.INSTANCE_CONFIGURATION = ConfigUtils.loadTomlConfig(arguments.game + "/instance.toml", InstanceConfig.class);
    OpenGL.initialize();

    Engine3.EVENT_BUS = new EventBus();
    Engine3.ASSET_MANAGER = new AssetManager();

    Engine3.INPUT = new Input();
    Engine3.INPUT.initialize(Engine3.DISPLAY);

    try {
      Application app = application.getConstructor().newInstance();
      Engine3.APPLICATION = app;
      app.onInit();
      Engine3.ASSET_MANAGER.update(); // update asset manager once after init
      app.run();
      app.destroy();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      e.printStackTrace();
    } finally {
      Engine3.ASSET_MANAGER.terminate();
      Engine3.DISPLAY.terminate();
    }
  }
}
