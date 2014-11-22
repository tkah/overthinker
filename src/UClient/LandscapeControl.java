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
      final PlayerNode node = (PlayerNode) event.getNodeA();
      System.out.println("player is A");
      node.setIsOnGround(true);
      //node.setUserData("onGround", true);
    }
    else if (event.getNodeB().getName().equals("player"))
    {
      final PlayerNode node = (PlayerNode) event.getNodeB();
      System.out.println("player is B");
      node.setIsOnGround(true);
      //node.setUserData("onGround", true);
    }
  }
}
