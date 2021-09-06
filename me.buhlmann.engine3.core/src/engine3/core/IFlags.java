package engine3.core;

public interface IFlags<T, R> {
  boolean isFlagged(T flag);
  R setFlag(T flag);
  R unsetFlag(T flag);
}
