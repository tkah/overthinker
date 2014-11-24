package UClient;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import org.lwjgl.Sys;

/**
 * Created by Torran on 11/21/14.
 */
public class PlayerControl extends BetterCharacterControl
{
  public PlayerControl(float radius, float height, float mass)
  {
    super(radius, height, mass);
  }

  @Override
  public void jump()
  {
    super.jump();
    System.out.println(getGravity());
    //setGravity(getGravity().negate());
  }
}
