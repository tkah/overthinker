package UClient;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Created by Torran on 11/30/14.
 */
public class Door extends NonPlayableObjectNode
{
  public Door(String name)
  {
    super(name);
    phy = new RigidBodyControl(0f);
  }

  public void createDoor(AssetManager assetManager, float sizeX, float rotate, Vector3f loc)
  {
    Box door = new Box(sizeX, 40.0f, 1.0f);
    geo = new Geometry(name, door);

    Material wood = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    wood.setTexture("ColorMap", assetManager.loadTexture("assets/textures/wood_texture.jpg"));
    geo.setMaterial(wood);
    geo.setLocalTranslation(loc);
    geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    Quaternion doorRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotate, new Vector3f(0, 1, 0));
    geo.setLocalRotation(doorRotate);
    phy.setSpatial(geo);
    phy.setApplyPhysicsLocal(true);
    phy.setEnabled(true);
    attachChild(geo);
  }
}
