package engine3.gfx.buffer;

import engine3.asset.api.IBindable;

public interface Buffer extends IBindable {
  int size();
  int getTarget();
}
