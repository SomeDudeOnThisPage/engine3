package engine3.asset.loading;

import engine3.gfx.buffer.IndexBuffer;
import engine3.gfx.buffer.VertexArray;
import engine3.gfx.buffer.VertexBuffer;
import engine3.gfx.buffer.VertexBufferLayout;
import engine3.gfx.enums.VertexDataType;
import engine3.util.ArrayUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL45C.*;

public final class Assimp {

  private static List<Float> parseAIVector3(final AIVector3D.Buffer buffer) {
    // try (MemoryStack stack = MemoryStack.stackPush()) {
      final List<Float> data = new LinkedList<>();

      if (buffer == null) return data;

      while (buffer.remaining() > 0) {
        AIVector3D vertex = buffer.get();
        data.add(vertex.x());
        data.add(vertex.y());
        data.add(vertex.z());
      }

      return data;
    // }
  }

  private static List<Float> parseUVCoordinates(final AIMesh aimesh) {
    final AIVector3D.Buffer aiTextures = aimesh.mTextureCoords(0);
    final List<Float> data = new LinkedList<>();

    if (aiTextures == null) return data;

    while (aiTextures.remaining() > 0) {
      AIVector3D aiTexture = aiTextures.get();
      data.add(aiTexture.x());
      data.add(1 - aiTexture.y()); // invert texture y coordinate
    }

    return data;
  }

  private static List<Integer> parseIndices(final AIMesh aimesh) {
    final List<Integer> data = new LinkedList<>();
    final AIFace.Buffer aiFaces = aimesh.mFaces();

    for (int i = 0; i < aimesh.mNumFaces(); i++) {
      final AIFace aiFace = aiFaces.get();
      final IntBuffer buffer = aiFace.mIndices();
      while (buffer.remaining() > 0) {
        data.add(buffer.get());
      }
    }

    return data;
  }

  /**
   * Creates a {@link VertexArray} from an AIMesh.
   * @param aimesh The mesh to be processed.
   * @return A {@link VertexArray} with one packed {@link VertexBuffer} and an {@link IndexBuffer}, if applicable.
   */
  public static VertexArray processMesh(AIMesh aimesh) {
    final List<Float> data = new LinkedList<>();

    final List<Float> vertices = Assimp.parseAIVector3(aimesh.mVertices());
    final List<Float> normals = Assimp.parseAIVector3(aimesh.mNormals());
    final List<Float> uv = Assimp.parseUVCoordinates(aimesh);
    final List<Float> tangents = Assimp.parseAIVector3(aimesh.mTangents());
    final List<Float> bitangents = Assimp.parseAIVector3(aimesh.mBitangents());
    final List<Integer> indices = Assimp.parseIndices(aimesh);

    System.err.println("UV-SIZE " + uv.size());

    // construct vertex buffer layout according to order of vertex attributes
    VertexBufferLayout layout = new VertexBufferLayout();
    if (vertices.size() > 0) {
      System.out.println("HAS V_POSITION");
      layout.addElement(new VertexBufferLayout.BufferElement("v_position", VertexDataType.FLOAT3));
    }

    if (uv.size() > 0) {
      System.out.println("HAS V_TEXTURE");
      layout.addElement(new VertexBufferLayout.BufferElement("v_texture",  VertexDataType.FLOAT2));
    }

    if (normals.size() > 0) {
      System.out.println("HAS V_NORMAL");
      layout.addElement(new VertexBufferLayout.BufferElement("v_normal",   VertexDataType.FLOAT3));
    }

    /*if (tangents.size() > 0) {
      System.out.println("HAS V_TANGENT");
      layout.addElement(new VertexBufferLayout.BufferElement("v_tangent", VertexDataType.FLOAT3));
    }

    if (bitangents.size() > 0) {
      System.out.println("HAS V_BITANGENT");
      layout.addElement(new VertexBufferLayout.BufferElement("v_bitangent", VertexDataType.FLOAT3));
    }*/

    // interleave vertex data into data array according to the layout
    int counter2 = 0; // counter for uv coordinates (if needed)
    for (int i = 0; i < vertices.size(); i += 3) {
      // add vertex position
      data.add(vertices.get(i));
      data.add(vertices.get(i + 1));
      data.add(vertices.get(i + 2));

      // add uv coordinates (if applicable)
      if (uv.size() > 0) {
        data.add(uv.get(counter2));
        data.add(uv.get(counter2 + 1));
        counter2 += 2;
      }

      // add normals
      data.add(normals.get(i));
      data.add(normals.get(i + 1));
      data.add(normals.get(i + 2));

      // add tangents (if applicable)
      /*if (tangents.size() > 0) {
        data.add(tangents.get(i));
        data.add(tangents.get(i + 1));
        data.add(tangents.get(i + 2));
      }

      // add bitangents (if applicable)
      if (bitangents.size() > 0) {
        data.add(bitangents.get(i));
        data.add(bitangents.get(i + 1));
        data.add(bitangents.get(i + 2));
      }*/
    }

    // construct vertex array with buffers
    final VertexArray vao = new VertexArray();
    final VertexBuffer vbo = new VertexBuffer(GL_ARRAY_BUFFER, GL_FLOAT, GL_STATIC_DRAW, ArrayUtils.toBoxedArrayF(data));
    vbo.layout(layout);
    vao.addVertexBuffer(vbo);

    if (indices.size() > 0) {
      final IndexBuffer ibo = new IndexBuffer(GL_STATIC_DRAW, ArrayUtils.toBoxedArrayI(indices));
      vao.setIndexBuffer(ibo);
    }

    return vao;
  }

  public static VertexArray load(String path) {
    AIScene scene = aiImportFile(path, aiProcess_ImproveCacheLocality |
        aiProcess_JoinIdenticalVertices |
        aiProcess_Triangulate |
        aiProcess_CalcTangentSpace);

    if (scene == null) {
      throw new UnsupportedOperationException("scene is null");
    }

    int numMeshes = scene.mNumMeshes();
    PointerBuffer aiMeshes = scene.mMeshes();

    for (int i = 0; i < numMeshes; i++) {
      assert aiMeshes != null;

      AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
      VertexArray mesh = processMesh(aiMesh);

      AIString aiName = aiMesh.mName();
      aiMesh.mName(aiName);

      byte[] s = new byte[(int) aiName.length()];
      aiName.data().get(s);

      // todo: more meshes
      return mesh;
    }
    return null;
  }
}
