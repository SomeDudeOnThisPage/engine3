package engine3.event.api;

import engine3.core.IFlags;

public interface IEvent extends IFlags<IEvent.Flags, IEvent> {
  enum Flags {
    DEFERRED,
    CANCELLED
  }

  String getName();
  boolean isDeferred();
  boolean isCancelled();
  void cancel();
}
