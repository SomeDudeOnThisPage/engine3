package engine3.entity.api;

import java.util.List;

public interface ISceneGraphNode<T> {

  T getParent();
  void setParent(T parent);

  List<T> getChildren();
  void addChild(T child);
  void removeChild(T child);
}
