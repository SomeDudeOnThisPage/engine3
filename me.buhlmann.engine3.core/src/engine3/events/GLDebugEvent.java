package engine3.events;

import engine3.event.Event;

import java.util.Arrays;

public final class GLDebugEvent extends Event {
  private final String message;

  public String getMessage() {
    return this.message;
  }

  public GLDebugEvent(String method, String... arguments) {
    this("", method, arguments);
  }

  public GLDebugEvent(String message, String method, String... arguments) {
    super("OpenGL Debug Message", Flags.DEFERRED);

    this.message = "\tmethod:  " + method + "\n" +
                   "\targs:    " + Arrays.toString(arguments) + (message.isBlank() ? "" : "\n") +
                   (message.isBlank() ? "" : "\tmessage: " + message);
  }
}
