package overthinker.client;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 * Created by jdrid_000 on 12/8/2014.
 */
public class Resource extends Geometry
{
  private static String COLLECTED = "collected";
  private static String ID = "id";


  /**
   * Creates a resource sphere.
   * @param id ID of the sphere.
   * @param x X position of the sphere.
   * @param y Y position of the sphere.
   * @param z Z position of the sphere.
   * @param mat Material of the sphere.
   */
  public Resource(int id, int x, int y, int z, Material mat)
  {
    Sphere sphere = new Sphere(32, 32, Globals.SPHERE_RESOURCE_RADIUS);
    RigidBodyControl rigidBodyControl = new RigidBodyControl(0);
    setUserData(COLLECTED, false);
    setUserData(ID, id);
    setMaterial(mat);
    setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    setMesh(sphere);
    // Translate the Y direction by radius of the sphere * 2
    // This is so the sphere isn't clipping with the ground.
    setLocalTranslation(x, y + 3, z);
    rigidBodyControl.setSpatial(this);
    rigidBodyControl.setEnabled(true);
    addControl(rigidBodyControl);
  }

  public int getId()
  {
    return getUserData(ID);
  }

  public boolean isCollected()
  {
    return getUserData(COLLECTED);
  }

  public void setCollected(boolean collected)
  {
    setUserData(COLLECTED, collected);
  }
}
