package overthinker.client;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 * Class creates other player representations for any given client
 *
 * Created by Torran, Peter, Sid, Derek, Josh on 11/25/14.
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

  /**
   * Class constructor
   * @param radius         - radius of player sphere
   * @param id             - player id
   * @param playerLocation - starting location of player
   * @param assetManager   - program's asset manager
   */
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
    if (id == 0) mat.setTexture("DiffuseMap", assetManager.loadTexture("overthinker/assets/textures/striated_rock_texture.JPG"));
    else if (id == 1) mat.setTexture("DiffuseMap", assetManager.loadTexture("overthinker/assets/textures/stone_texture.JPG"));
    else if (id == 2) mat.setTexture("DiffuseMap", assetManager.loadTexture("overthinker/assets/textures/barnacles_texture.JPG"));
    else System.out.println("OtherPlayer: constructor - id error");
    mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
    mat.setBoolean("UseMaterialColors", true);
    mat.setColor("Diffuse", ColorRGBA.White);
    mat.setColor("Ambient", ColorRGBA.White.mult(0.5f));
    //mat.setColor("Specular", ColorRGBA.White);
    //mat.setFloat("Shininess", 64f);
    geom.setMaterial(mat);
    sphere_phy = new RigidBodyControl(0f);
    geom.addControl(sphere_phy);
  }

  /**
   * Getter for sphere object
   * @return sphere object
   */
  public Sphere getSphere()
  {
    return sphere;
  }

  /**
   * Getter for sphere geometry
   * @return sphere geometry
   */
  public Geometry getGeometry()
  {
    return geom;
  }

  /**
   * Getter for sphere physics
   * @return sphere physics
   */
  public RigidBodyControl getSphereResourcePhy()
  {
    return sphere_phy;
  }

  /**
   * Scales sphere
   * @param scale - amount to scale object by
   */
  public void scale(float scale)
  {
    geom.scale(scale);
    sphere.updateBound();
    sphere_phy.getCollisionShape().setScale(new Vector3f(scale,scale,scale));
  }

  /**
   * Moves player object around screen
   * @param move - vector location
   */
  public void move(Vector3f move)
  {
    //TODO: Rotation conditional on movement direction and size
    sphere_phy.setPhysicsLocation(move);
  }


}
