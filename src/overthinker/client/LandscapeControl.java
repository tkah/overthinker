package overthinker.client;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

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
    if (event.getNodeA().getName().equals("player") || event.getNodeB().getName().equals("player")
          && !event.getNodeA().getName().equals("AI") && !event.getNodeB().getName().equals("AI"))
    {
      final UnderNode node = (UnderNode) event.getNodeA();
      if (!node.isOnGround() && node.getPlayerControl().getVelocity().getZ() < 1f && node.getPlayerControl()
            .getVelocity().getZ() > 0)
      {
        node.getPlayerControl().setWalkDirection(new Vector3f(0, -5, -5));
        node.pushOff = true;
      }
    }
    /* To detect camera collisions with landscape, difficulties with fast moving cameras not repositioning correctly,
       not enough time to fully flesh out before due date
    else if (event.getNodeA().getName().equals("CameraNode"))
    {
      final CameraNode node = (CameraNode) event.getNodeA();
      final UnderNode uNode = (UnderNode) node.getParent().getParent();
      node.setLocalTranslation(new Vector3f(0, node.getLocalTranslation().getY() - .22f, 4));
      System.out.println("cam hit in a");
    }
    else if (event.getNodeB().getName().equals("CameraNode"))
    {
      System.out.println("cam hit in b");
    }*/
  }
}
