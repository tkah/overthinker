package UClient;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import org.lwjgl.Sys;

/**
 * Created by Torran on 11/21/14.
 */
public class PlayerControl extends BetterCharacterControl
{
  /**
   * Class constructor
   * @param radius - radius of player
   * @param height - height of player
   * @param mass   - mass of player
   */
  public PlayerControl(float radius, float height, float mass)
  {
    super(radius, height, mass);
  }

  public void setScale (float size)
  {
    height *= size;
    radius *= size;
    rigidBody.setCollisionShape(getShape());
  }

  public float getHeight()
  {
    return height;
  }

  /**
   * Override BetterCharacterControl jump method
   * Necessary to bypass the parent class's onGround
   * check. This check is done in the simpleUpdate()
   * method of UClient using the playerNode's onGround
   * value.
   */
  @Override
  public void jump()
  {
    jump = true;
  }
}
