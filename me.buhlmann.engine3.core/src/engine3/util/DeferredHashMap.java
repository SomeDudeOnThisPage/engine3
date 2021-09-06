package engine3.util;

import javax.annotation.Nullable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * HashMap that only updates its' contents when the update-method is called.
 * Updates are buffered in internal lists.
 *
 * @param <K>
 * @param <V>
 */
public class DeferredHashMap<K, V> extends HashMap<K, V> {
  public final HashSet<Object> tbr;
  public final ArrayList<SimpleEntry<K, V>> tba;

  @Override
  @Nullable
  public V remove(Object e) {
    if (this.containsKey(e)) {
      this.tbr.add(e);
      return this.get(e);
    }
    return null;
  }

  @Override
  public V put(K key, V value) {
    this.tba.add(new AbstractMap.SimpleEntry<K, V>(key, value));
    return value;
  }

  public void update() {
    for (Object element : this.tbr) {
      super.remove(element);
    }

    for (AbstractMap.SimpleEntry<K, V> element : this.tba) {
      super.put(element.getKey(), element.getValue());
    }
  }

  public DeferredHashMap() {
    this.tbr = new HashSet<>();
    this.tba = new ArrayList<>();
  }
}
