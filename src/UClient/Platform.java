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
  private float updateHeight;
  private Vector3f loc;
  private boolean moveUp = false;

  public Platform(String name)
  {
    super(name);

    Cylinder platform = new Cylinder(20,50,2,1,true);
    geo = new Geometry("Platform", platform);
    CylinderCollisionShape pCol = new CylinderCollisionShape(new Vector3f(2,1,2));
    phy = new RigidBodyControl(pCol, 0f);

    int id = Integer.parseInt(name.split("_")[1]);
    geo.setUserData("id", id);
  }

  public void createPlatform(AssetManager assetManager, Vector3f loc)
  {
    Material platMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    platMat.setColor("Color", ColorRGBA.Orange);
    geo.setMaterial(platMat);
    geo.setLocalTranslation(new Vector3f(-330, 40.8f, -400));
    geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

    Quaternion platRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * -90, new Vector3f(1, 0, 0));
    geo.setLocalRotation(platRotate);

    phy.setSpatial(geo);
    phy.setApplyPhysicsLocal(true);
    phy.setEnabled(true);
  }

  public void update(float tpf)
  {
    geo.setLocalTranslation(loc.getX(), updateHeight, loc.getZ());
  }
}
