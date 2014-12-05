package overthinker.client;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 * Created by Torran on 11/25/14.
 */
public class OtherPlayer
{
  private float radius;
  private Vector3f playerLocation;
  private int id;
  private Sphere sphere;
  private Geometry geom;
  private RigidBodyControl sphere_phy;
  private AssetManager assetManager;


  public OtherPlayer(float radius, int id, Vector3f playerLocation, AssetManager assetManager)
  {
    this.playerLocation = playerLocation;
    this.id = id;
    this.radius = radius;
    this.assetManager = assetManager;

    sphere = new Sphere(32, 32, radius);
    geom = new Geometry("Player_" + id, sphere);
    geom.setLocalTranslation(playerLocation);
    geom.setUserData("id", id);
    //geom.setUserData("isHit", false);
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
    mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
    mat.setBoolean("UseMaterialColors", true);
    mat.setColor("Diffuse", ColorRGBA.White);
    mat.setColor("Specular", ColorRGBA.White);
    mat.setFloat("Shininess", 64f);
    geom.setMaterial(mat);
    sphere_phy = new RigidBodyControl(0f);
    geom.addControl(sphere_phy);
  }

  public Sphere getSphere()
  {
    return sphere;
  }

  public Geometry getGeometry()
  {
    return geom;
  }

  public RigidBodyControl getSphereResourcePhy()
  {
    return sphere_phy;
  }

  public void scale(float scale)
  {
    geom.scale(scale);
    sphere.updateBound();
    sphere_phy.getCollisionShape().setScale(new Vector3f(scale,scale,scale));
  }

  public void move(Vector3f move)
  {
    //TODO: Rotation conditional on movement direction and size
    sphere_phy.setPhysicsLocation(move);
  }


}
