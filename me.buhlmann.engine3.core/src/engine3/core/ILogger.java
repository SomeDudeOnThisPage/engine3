package engine3.core;

public interface ILogger {
  enum LEVEL {
    TRACE(3),
    INFO(2),
    WARNING(1),
    ERROR(0);

    private final int value;

    int getValue() {
      return this.value;
    }

    LEVEL(int value) {
      this.value = value;
    }
  }

  void trace(String... messages);
  void info(String... messages);
  void warning(String... messages);
  void error(String... messages);
}
