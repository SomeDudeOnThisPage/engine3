package engine3.gfx.material;

import org.joml.Vector3f;

public class FlatColorPBRMaterial extends Material {

  private Vector3f color;
  private float roughness;
  private float metallic;
  private float ao;
  private float emissive;

  @Override
  public void setUniforms() {
    if (this.program.isLoaded()) {
      this.program.get().setUniform("u_material.diffuse", this.color);
      this.program.get().setUniform("u_material.roughness", this.roughness);
      this.program.get().setUniform("u_material.metallic", this.metallic);
      this.program.get().setUniform("u_material.ao", this.ao);
      this.program.get().setUniform("u_material.emissive", this.emissive);

    }
  }

  public FlatColorPBRMaterial(Vector3f color, float roughness, float metallic, float ao, float emissive) {
    super("shader.test");
    this.color = color;
    this.roughness = roughness;
    this.metallic = metallic;
    this.ao = ao;
    this.emissive = emissive;
  }
}
