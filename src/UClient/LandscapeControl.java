package UClient;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

/**
 * Created by Torran on 11/20/14.
 */
public class LandscapeControl extends RigidBodyControl implements PhysicsCollisionListener
{

  public LandscapeControl(CollisionShape shape, float mass, PhysicsSpace phy)
  {
    super(shape, mass);
    phy.addCollisionListener(this);
  }

  public void collision(PhysicsCollisionEvent event)
  {
    if (event.getNodeA().getName().equals("player"))
    {
      final UnderNode node = (UnderNode) event.getNodeA();
      node.setIsOnGround(true);
    }
    else if (event.getNodeB().getName().equals("player"))
    {
      final UnderNode node = (UnderNode) event.getNodeB();
      node.setIsOnGround(true);
    }
  }
}
