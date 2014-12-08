package overthinker.client;

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
 * Class creates door node objects for use as maze obstacles
 *
 * Created by Torran, Josh, Peter, Derek, Sid on 11/30/14.
 */
public class Door extends NonPlayableObjectNode
{
  /**
   * Class constructor
   * @param name - name of door node
   */
  public Door(String name)
  {
    super(name);
    phy = new RigidBodyControl(0f);
  }

  /**
   * Set up door object, called from GamePlayAppState
   * @param assetManager - game's asset manager
   * @param sizeX        - width of door
   * @param sizeY        - height of door
   * @param sizeZ        - depth of door
   * @param rotate       - rotation of door
   * @param loc          - door location
   */
  public void createDoor(AssetManager assetManager, float sizeX, float sizeY, float sizeZ, float rotate, Vector3f loc)
  {
    this.loc = loc;
    Box door = new Box(sizeX, sizeY, sizeZ);
    geo = new Geometry(name, door);

    Material wood = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    wood.setTexture("ColorMap", assetManager.loadTexture("overthinker/assets/textures/wood_texture.jpg"));
    geo.setMaterial(wood);
    geo.setLocalTranslation(loc);
    geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    Quaternion doorRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotate, new Vector3f(0, 1, 0));
    geo.setLocalRotation(doorRotate);
    phy.setSpatial(geo);
    phy.setApplyPhysicsLocal(true);
    phy.setEnabled(true);
    geo.addControl(phy);
    attachChild(geo);
  }
}
