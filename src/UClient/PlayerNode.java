package UClient;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.scene.Node;

/**
 * Created by Torran on 11/21/14.
 */
public class PlayerNode extends Node implements PhysicsTickListener
{
  private boolean onGround = false;

  public PlayerNode(String name)
  {
    super(name);
  }

  @Override
  public void prePhysicsTick(PhysicsSpace space, float tpf){
    System.out.println("PlayerNode: prePhysicsTick() - not on ground");
    onGround = false;
  }

  @Override
  public void physicsTick(PhysicsSpace space, float tpf){
    System.out.println("PlayerNode: physicsTick() - not on ground");
    // poll game state ...
  }

  public boolean isOnGround()
  {
    return onGround;
  }

  public void setIsOnGround (boolean status)
  {
    onGround = status;
  }
}
