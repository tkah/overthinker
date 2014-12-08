package overthinker.client;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * Abstract class for use by nonplayable objects with physics
 *
 * Created by Torran, Derek, Peter, Josh, Sid on 11/30/14.
 */
abstract class NonPlayableObjectNode extends Node
{
  protected Geometry geo;
  protected RigidBodyControl phy;
  protected Vector3f loc;

  /**
   * Class contructor
   * @param name - node name
   */
  public NonPlayableObjectNode(String name)
  {
    super(name);
  }

  /**
   * Getter for object geometry
   * @return object geometry
   */
  public Geometry getGeometry()
  {
    return geo;
  }

  /**
   * Getter for object physics
   * @return object physics
   */
  public RigidBodyControl getPhy()
  {
    return phy;
  }
}
