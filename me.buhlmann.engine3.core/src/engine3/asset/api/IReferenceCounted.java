package engine3.asset.api;

public interface IReferenceCounted {
  boolean isReferenceCounted();
  int getReferences();
  void addReferences(int references);
}
