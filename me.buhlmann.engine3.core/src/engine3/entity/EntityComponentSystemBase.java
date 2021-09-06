package engine3.entity;

import engine3.event.EventBindings;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityComponentSystemBase implements EventBindings {
  private final List<CompositeEventSubscription> subscriptions = new ArrayList<>();

  @Override
  public final List<CompositeEventSubscription> getSubscriptions() {
    return this.subscriptions;
  }

  @Override
  public final void bindEventSubscription(CompositeEventSubscription subscription) {
    this.subscriptions.add(subscription);
  }
}
