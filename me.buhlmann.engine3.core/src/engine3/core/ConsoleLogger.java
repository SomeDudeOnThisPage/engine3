package engine3.core;

public final class ConsoleLogger implements ILogger {
  private ILogger.LEVEL level;

  @Override
  public void trace(String... messages) {
    if (this.level.getValue() < ILogger.LEVEL.TRACE.getValue()) {
      return;
    }

    if (messages.length == 1) {
      System.out.println("[TRACE] " + messages[0]);
    } else {
      System.out.println("[TRACE]");
      for (String message : messages) {
        System.out.println("\t" + message);
      }
    }
  }

  @Override
  public void info(String... messages) {
    if (this.level.getValue() < ILogger.LEVEL.INFO.getValue()) {
      return;
    }

    if (messages.length == 1) {
      System.out.println("[INFO] " + messages[0]);
    } else {
      System.out.println("[INFO]");
      for (String message : messages) {
        System.out.println("\t" + message);
      }
    }
  }

  @Override
  public void warning(String... messages) {
    if (this.level.getValue() < ILogger.LEVEL.WARNING.getValue()) {
      return;
    }

    if (messages.length == 1) {
      System.out.println("[WARNING] " + messages[0]);
    } else {
      System.out.println("[WARNING]");
      for (String message : messages) {
        System.out.println("\t" + message);
      }
    }
  }

  @Override
  public void error(String... messages) {
    if (this.level.getValue() < ILogger.LEVEL.ERROR.getValue()) {
      return;
    }

    if (messages.length == 1) {
      System.err.println("[ERROR] " + messages[0]);
    } else {
      System.err.println("[ERROR]");
      for (String message : messages) {
        System.err.println("\t" + message);
      }
    }
  }
  public void initialize(ILogger.LEVEL level) {
    this.level = level;
  }
}
