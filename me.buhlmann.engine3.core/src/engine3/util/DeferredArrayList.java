package engine3.util;

import java.util.ArrayList;
import java.util.List;

public class DeferredArrayList<T> extends ArrayList<T> {
  private final List<Object> tbr;
  private final List<T> tba;

  @Override
  public boolean add(T e) {
    return this.tba.add(e);
  }

  @Override
  public boolean remove(Object e) {
    return this.tbr.add(e);
  }

  public void update() {
    for (Object element : this.tbr) {
      super.remove(element);
    }

    for (T element : this.tba) {
      super.add(element);
    }
  }

  public DeferredArrayList() {
    super();

    this.tbr = new ArrayList<>();
    this.tba = new ArrayList<>();
  }
}
