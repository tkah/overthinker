package overthinker.client;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

/**
 * Class creates a platform node for use as an obstacle
 *
 * Created by Torran, Peter, Josh, Derek, Sid on 12/1/14.
 */
public class Platform extends NonPlayableObjectNode
{
  private Cylinder platform;
  private CylinderCollisionShape pCol;

  /**
   * Class constructor
   * @param name - name of node
   */
  public Platform(String name)
  {
    super(name);
    platform = new Cylinder(20,50,2,1,true);
    geo = new Geometry("Platform", platform);
    pCol = new CylinderCollisionShape(new Vector3f(14,20,14));
    phy = new RigidBodyControl(pCol, 0f);

    int id = Integer.parseInt(name.split("_")[1]);
    geo.setUserData("id", id);
  }

  /**
   * Creates platform
   * @param assetManager - program's asset manager
   * @param loc          - location of platform
   * @param width        - width of platform
   */
  public void createPlatform(AssetManager assetManager, Vector3f loc, float width)
  {
    platform.updateGeometry(platform.getAxisSamples(), platform.getRadialSamples(), width, width, platform.getHeight(), true, false);
    //pCol.setScale(new Vector3f(4, 6f, 4));

    Material platMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    platMat.setColor("Color", ColorRGBA.Orange);
    this.loc = loc;
    geo.setMaterial(platMat);
    geo.setLocalTranslation(loc);
    geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

    Quaternion platRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * -90, new Vector3f(1, 0, 0));
    geo.setLocalRotation(platRotate);

    phy.setSpatial(geo);
    phy.setApplyPhysicsLocal(true);
    phy.setEnabled(true);

    attachChild(geo);
  }

  /**
   * Move platform down as if it were pressed
   * Called when player hits platform
   */
  public void pressDown()
  {
    Vector3f down = new Vector3f(loc.getX(), loc.getY() - .45f, loc.getZ());
    Vector3f phyDown = new Vector3f(loc.getX(), loc.getY() - 10f, loc.getZ());
    geo.setLocalTranslation(down);
    phy.setPhysicsLocation(phyDown);
  }

  /**
   * Move platform up as if it were released
   * Called when player is no longer touching platform
   */
  public void moveUp()
  {
    Vector3f up = new Vector3f(loc.getX(), loc.getY(), loc.getZ());
    geo.setLocalTranslation(up);
    phy.setPhysicsLocation(up);
  }
}
