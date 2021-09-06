package engine3.events;

public final class CursorMovementInputEvent extends InputEvent {
  public int x;
  public int y;

  public int lx;
  public int ly;

  public CursorMovementInputEvent(int x, int y, int lx, int ly) {
    super("CursorMovement");
    this.x = x;
    this.y = y;

    this.lx = lx;
    this.ly = ly;
  }
}
