package game.Level;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * Created by Torran on 11/30/14.
 */
abstract class NonPlayableObjectNode extends Node
{
  protected Geometry geo;
  protected RigidBodyControl phy;
  protected Vector3f loc;

  public NonPlayableObjectNode(String name)
  {
    super(name);
  }

  public Geometry getGeometry()
  {
    return geo;
  }

  public RigidBodyControl getPhy()
  {
    return phy;
  }
}
