package engine3.thread;

public enum Engine3Thread {
  /**
   * Main thread used for game logic and rendering.
   * This thread contains the opengl / vulkan context. Thus, assets that require initialization via these libraries
   * are instanced on this thread as well.
   */
  MAIN,
  ASSET,
  AUDIO
}
