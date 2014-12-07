package overthinker.ai;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Created by jdrid_000 on 12/7/2014.
 */
class AiNode extends Node
{
  public final BetterCharacterControl phys;
  private final Node pivot;
  private final int id;
  private final AssetManager assetManager;

  public AiNode(int id, AssetManager assetManager)
  {
    super("AI");
    this.assetManager = assetManager;
    this.id = id;
    pivot = new Node("pivot");
    phys = new BetterCharacterControl(2f, 4f, 1f);
    phys.setGravity(Vector3f.UNIT_Y.mult(-50));
    attachChild(pivot);
    addControl(phys);
    setUpModel();
  }

  private void setUpModel()
  {
    Sphere sphere = new Sphere(32, 32, 2);
    Geometry geom = new Geometry("AI_" + id, sphere);
    geom.setUserData("id", id);
    TangentBinormalGenerator.generate(geom);

    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
    mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
    mat.setBoolean("UseMaterialColors", true);
    mat.setColor("Diffuse", ColorRGBA.White);
    mat.setColor("Specular", ColorRGBA.White);
    mat.setFloat("Shininess", 64f);
    geom.setMaterial(mat);
    setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.CastAndReceive);
    attachChild(geom);
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.Green);
    addLight(al);
  }

}
