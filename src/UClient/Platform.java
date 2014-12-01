package UClient;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

/**
 * Created by Torran on 12/1/14.
 */
public class Platform extends NonPlayableObjectNode
{
  public Platform(String name)
  {
    super(name);

    Cylinder platform = new Cylinder(20,50,2,1,true);
    geo = new Geometry("Platform", platform);
    CylinderCollisionShape pCol = new CylinderCollisionShape(new Vector3f(2,0,2));
    phy = new RigidBodyControl(pCol, 0f);

    int id = Integer.parseInt(name.split("_")[1]);
    geo.setUserData("id", id);
  }

  public void createPlatform(AssetManager assetManager, Vector3f loc)
  {
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

  public void pressDown()
  {
    Vector3f down = new Vector3f(loc.getX(), loc.getY() - .45f, loc.getZ());
    Vector3f phyDown = new Vector3f(loc.getX(), loc.getY() - 10f, loc.getZ());
    geo.setLocalTranslation(down);
    phy.setPhysicsLocation(phyDown);
  }

  public void moveUp()
  {
    Vector3f up = new Vector3f(loc.getX(), loc.getY(), loc.getZ());
    geo.setLocalTranslation(up);
    phy.setPhysicsLocation(up);
  }
}
