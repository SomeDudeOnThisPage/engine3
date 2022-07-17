package engine3;

import engine3.api.IApplication;
import engine3.asset.AssetManager;
import engine3.config.EngineConfiguration;
import engine3.config.InstanceConfiguration;
import engine3.core.ConsoleLogger;
import engine3.core.ILogger;
import engine3.event.EventBus;
import engine3.gfx.OpenGL;
import engine3.input.IInputManager;
import engine3.platform.GLFWWindow;
import engine3.util.Arguments;
import engine3.util.ConfigUtils;
import org.joml.Vector2i;

public class Engine4<T extends IApplication> {
  private static Engine4<? extends IApplication> INSTANCE;

  // CUSTOM ENGINE APPLICATION
  private T application;

  // CORE ENGINE SYSTEMS
  private GLFWWindow display;
  private IInputManager input;
  private EngineConfiguration configuration;
  private InstanceConfiguration instance;
  private EventBus events;
  private AssetManager assets;
  private ILogger logger;

  public static Engine4<? extends IApplication> getInstance() {
    if (Engine4.INSTANCE == null) {
      throw new RuntimeException("Engine4 not initialized or started");
    }
    return Engine4.INSTANCE;
  }

  public static ILogger getLogger() {
    return Engine4.getInstance().logger;
  }

  public static AssetManager getAssetManager() {
    return Engine4.getInstance().assets;
  }

  public static EventBus getEventBus() {
    return Engine4.getInstance().events;
  }

  public static EngineConfiguration getConfiguration() {
    return Engine4.getInstance().configuration;
  }

  public static GLFWWindow getDisplay() {
    return Engine4.getInstance().display;
  }

  public static IInputManager getInputManager() {
    return Engine4.getInstance().getApplication().getInput();
  }

  public boolean initialize(String[] args) {
    Engine4.INSTANCE = this;

    // INITIALIZE CONFIGURATION
    final Arguments arguments = new Arguments();
    arguments.parse(args);
    this.configuration = ConfigUtils.loadTomlConfig(arguments.platform + "/engine.toml", EngineConfiguration.class);
    if (this.configuration != null) {
      Engine4.getLogger().info("[CONFIGURATION] loaded engine configuration file " + arguments.platform + "/engine.toml");
    } else {
      Engine4.getLogger().error("[CONFIGURATION] failed to load engine configuration file " + arguments.platform + "/engine.toml");
      return false;
    }

    // INITIALIZE WINDOW
    this.display = new GLFWWindow(new Vector2i(800, 600));

    // INITIALIZE RENDERING BACKEND // TODO: dynamic? vk support?
    OpenGL.initialize();

    // INITIALIZE MAIN EVENT BUS
    this.events = new EventBus();

    // INITIALIZE CORE ASSET MANAGER
    this.assets = new AssetManager();

    return true;
  }

  public T getApplication() {
    return this.application;
  }

  public void setApplication(final T application) {
    if (this.application != null) {
      throw new RuntimeException("cannot set application if initial application has already been set");
    }
    this.application = application;

    // TODO: initialize instance configuration
  }

  public void start() {
    if (this.application == null) {
      throw new RuntimeException("no application set"); // TODO: error management
    }

    try {
      this.application.onInit();
      this.assets.update();
      this.application.run();
      this.application.onDestroy();
    } catch(Exception e) {
      e.printStackTrace(); // TODO: error management
    } finally {
      Engine4.getAssetManager().terminate();
      Engine4.getDisplay().terminate();
    }
  }

  public Engine4() {
    final ConsoleLogger logger = new ConsoleLogger();
    logger.initialize(ILogger.LEVEL.TRACE);
    this.logger = logger;
  }
}
