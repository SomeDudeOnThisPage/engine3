package engine3.util;

public final class Utils {
  public static void assertRTE(Boolean condition, String message) {
    if (condition) {
      throw new RuntimeException(message);
    }
  }
}
