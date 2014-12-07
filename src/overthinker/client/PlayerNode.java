package overthinker.client;

import com.jme3.effect.ParticleEmitter;
import com.jme3.input.InputManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import java.util.ArrayList;

/**
 * Created by Torran on 11/26/14.
 */
abstract class PlayerNode extends Node
{
  ArrayList<String> actionStrings = new ArrayList<>();

  public PlayerNode(String name)
  {
    super(name);
  }

  abstract void setUpPlayer();

  abstract ArrayList setUpControls(InputManager inputManager);

  abstract void onAction(String binding, boolean isPressed, float tpf);

  abstract void onAnalog(String binding, float value, float tpf);

  abstract void update(float tpf);

  abstract ArrayList getAudio();

  public Geometry getGeometry()
  {
    return null;
  }

  public boolean isDead()
  {
    return false;
  }

  public void setDead(boolean val)
  {
  }

  public boolean isSlowWater()
  {
    return false;
  }

  public float getHeight()
  {
    return 0;
  }

  public void setScaleStartTime(int time)
  {

  }

  public void setPlayerNeedsScaling(boolean val)
  {

  }

  public void scalePlayerUp()
  {

  }

  public void scalePlayerDown(float tpf)
  {

  }

  public ParticleEmitter getDustEmitterRight()
  {
    return null;
  }
  public ParticleEmitter getDustEmitterLeft(){return null;}

  public boolean getShrink()
  {
    return false;
  }

  public Node getCamNode()
  {
    return null;
  }

  public float getWaterRate() { return 0; }

  public boolean getForwardGrav() {
    return false;
  }
  public boolean getBackwardGrav() {
    return false;
  }
  public boolean getLeftGrav() {
    return false;
  }
  public boolean getRightGrav() {
    return false;
  }
  public boolean getNormalGrav() {
    return false;
  }

}
