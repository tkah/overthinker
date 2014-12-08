package overthinker.client;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.input.InputManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import java.util.ArrayList;

/**
 * Abstract class extended by UnderNode and OverNode
 * Used to reference player object in GamePlayAppState
 * Saves code and improves readability
 *
 * Created by Torran, Peter, Derek, Sid, Josh on 11/26/14.
 */
abstract class PlayerNode extends Node
{
  ArrayList<String> actionStrings = new ArrayList<>();

  /**
   * Class constructor
   * @param name - name of node
   */
  public PlayerNode(String name)
  {
    super(name);
  }

  /**
   * Sets up player object
   */
  abstract void setUpPlayer();

  /**
   * Sets up player control
   * @param inputManager - program's input manager
   * @return list of controls
   */
  abstract ArrayList setUpControls(InputManager inputManager);

  /**
   * Action to take on key change
   * @param binding   - name of key change
   * @param isPressed - status of key changed
   * @param tpf       - frame rate
   */
  abstract void onAction(String binding, boolean isPressed, float tpf);

  /**
   * Node's update method
   * @param tpf - frame rate
   */
  abstract void update(float tpf);

  /**
   * Getter for player audio
   * @return list of player's audio
   */
  abstract ArrayList getAudio();

  /**
   * Action take on analog action
   * Overridden by UnderNode
   * @param binding - name of analog action
   * @param value   - value of action
   * @param tpf     - frame rate
   */
  public void onAnalog(String binding, float value, float tpf)
  {

  }

  /**
   * Getter for player geometry
   * Overridden by UnderNode
   * @return player geometry
   */
  public Geometry getGeometry()
  {
    return null;
  }

  /**
   * Getter for player status - alive or dead
   * Overridden by UnderNode
   * @return player living status
   */
  public boolean isDead()
  {
    return false;
  }

  /**
   * Setter for player status
   * Overridden by UnderNode
   * @param val - alive = false, dead = true
   */
  public void setDead(boolean val)
  {
  }

  /**
   * Is the water rate moving slowly
   * @return water rate status
   */
  public boolean isSlowWater()
  {
    return false;
  }

  /**
   * Getter for player height
   * Overridden by UnderNode
   * @return player's height
   */
  public float getHeight()
  {
    return 0;
  }

  /**
   * Sets start time for player scaling transformation
   * Used to compare with current time in order to find when to stop
   * Overridden by UnderNode
   * @param time scale start time
   */
  public void setScaleStartTime(int time)
  {

  }

  /**
   * Sets whether or not the player needs to be scaled
   * Overridden by UnderNode
   * @param val - true = player needs scaling, false = player doesn't need scaling
   */
  public void setPlayerNeedsScaling(boolean val)
  {

  }

  /**
   * Scales player object up
   * Overridden by UnderNode
   */
  public void scalePlayerUp()
  {

  }

  /**
   * Scales player down
   * Overridden by UnderNode
   * @param tpf - frame rate
   */
  public void scalePlayerDown(float tpf)
  {

  }

  /**
   * Getter for player right dust emitter
   * Overridden by UnderNode
   * @return player right dust emitter
   */
  public ParticleEmitter getDustEmitterRight()
  {
    return null;
  }

  /**
   * Getter for player left dust emitter
   * Overridden by UnderNode
   * @return player left dust emitter
   */
  public ParticleEmitter getDustEmitterLeft(){return null;}

  /**
   * Getter for whether or not to scale player down
   * Overridden by UnderNode
   * @return true = scale down, false = don't scale down
   */
  public boolean getShrink()
  {
    return false;
  }

  public PlayerControl getBCCControl()
  {
    return null;
  }

  /**
   * Getter for player cam node
   * Overridden by UnderNode
   * @return camera node
   */
  public Node getCamNode()
  {
    return null;
  }

  /**
   * Getter for water rate
   * Overridden by OverNode
   * @return water rate
   */
  public float getWaterRate() { return 0; }

  /**
   * Getter for forward gravity
   * Overridden by OverNode
   * @return forward gravity
   */
  public boolean getForwardGrav() {
    return false;
  }

  /**
   * Getter for back gravity
   * Overridden by OverNode
   * @return back gravity
   */
  public boolean getBackwardGrav() {
    return false;
  }

  /**
   * Getter for left gravity
   * Overridden by OverNode
   * @return left gravity
   */
  public boolean getLeftGrav() {
    return false;
  }

  /**
   * Getter for right gravity
   * Overridden by OverNode
   * @return right gravity
   */
  public boolean getRightGrav() {
    return false;
  }

  /**
   * Getter for normal gravity
   * Overridden by OverNode
   * @return normal gravity
   */
  public boolean getNormalGrav() {
    return false;
  }

  /**
   * Set player physics height
   * @param height - height to set
   */
  public void setHeight(float height)
  {
  }

}
