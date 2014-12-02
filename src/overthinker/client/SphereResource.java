package overthinker.client;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 * Created by Torran on 11/10/14.
 */
public class SphereResource
{
  private float radius;
  private int z;
  private int x;
  private int id;
  private boolean shrink = false;
  private Sphere sphere;
  private Geometry geom;
  private int startShrinkTime;
  private RigidBodyControl sphereResource_phy;
  private AssetManager assetManager;


  public SphereResource(float radius, int x, int z, int id, AssetManager assetManager)
  {
    this.x = x;
    this.z = z;
    this.id = id;
    this.radius = radius;
    this.assetManager = assetManager;

    sphere = new Sphere(32, 32, radius);
    geom = new Geometry("Sphere_" + id, sphere);
    geom.setLocalTranslation(new Vector3f(x, 250, z));
    geom.setUserData("id", id);
    geom.setUserData("isHit", false);
    geom.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setBoolean("UseMaterialColors", true);
    mat.setColor("Diffuse", ColorRGBA.Red);
    mat.setColor("Specular", ColorRGBA.White);
    mat.setFloat("Shininess", 64f);
    geom.setMaterial(mat);
    sphereResource_phy = new RigidBodyControl(1f);
    //sphereResource_phy.getCollisionShape().setScale(new Vector3f(radius,radius,radius)); //!!!
    sphereResource_phy.setSpatial(geom);
    sphereResource_phy.setApplyPhysicsLocal(true);
    sphereResource_phy.setEnabled(true);
    geom.addControl(sphereResource_phy);
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
    return sphereResource_phy;
  }

  public boolean getShrink()
  {
    return shrink;
  }

  public void setShrink(boolean s)
  {
    if (s == true) startShrinkTime = Globals.getTotSecs();
    shrink = s;
  }

  public void setSphereToDisappear()
  {
    geom.scale(.9f);
    sphere.updateBound();
    sphereResource_phy.getCollisionShape().setScale(new Vector3f(.9f,.9f,.9f));
    if(geom.getLocalScale().getY() <= .01) shrink = false;
  }

  public void setSphereBack()
  {
    geom.scale(100f);
    sphere.updateBound();
    sphereResource_phy.getCollisionShape().setScale(new Vector3f(100,100,100));
  }

  public int getStartShrinkTime()
  {
    return startShrinkTime;
  }

  public int getX()
  {
    return x;
  }

  public int getZ()
  {
    return z;
  }
}
