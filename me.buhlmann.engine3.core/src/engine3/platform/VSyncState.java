package engine3.platform;

public enum VSyncState {
  ENABLED(1),
  DISABLED(0);

  private int value;

  public int getValue() {
    return this.value;
  }

  VSyncState(int value) {
    this.value = value;
  }
}
