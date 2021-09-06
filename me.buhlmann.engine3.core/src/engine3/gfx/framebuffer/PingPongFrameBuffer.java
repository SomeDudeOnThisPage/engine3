package engine3.gfx.framebuffer;

import org.joml.Vector2i;

public class PingPongFrameBuffer extends FrameBuffer {


  public PingPongFrameBuffer(Vector2i size, FrameBufferSpecification specification, Flags... flags) {
    super(size, specification, flags);
  }
}
