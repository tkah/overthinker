/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * @author jdrid_000
 */
public class InteractionManager extends AbstractAppState implements ActionListener
{

  public static final String LEFT = "Left";
  public static final String RIGHT = "Right";
  public static final String UP = "Up";
  public static final String DOWN = "Down";
  private boolean left = false, right = false, up = false, down = false, click = false;
  private Vector3f walkDirection = new Vector3f();
  private Vector3f camDir = new Vector3f();
  private Vector3f camLeft = new Vector3f();
  private AppStateManager stateManager;
  private InputManager inputManager;
  private SimpleApplication app;
  private Player player;
  private Quaternion rotate;
  private float rotateVal;
  private AudioNode audioFootstep;

  @Override
  public void initialize(AppStateManager stateManager, Application app)
  {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    this.stateManager = this.app.getStateManager();
    this.inputManager = this.app.getInputManager();
    this.player = this.stateManager.getState(PlayerManager.class).player;
    this.rotateVal = 0f;
    setUpKeys();
    setUpAudio();
  }

  public void onAction(String name, boolean isPressed, float tpf)
  {
    if (name.equals(LEFT))
    {
      left = isPressed;
    }
    else if (name.equals(RIGHT))
    {
      right = isPressed;
    }
    else if (name.equals(UP))
    {
      up = isPressed;
    }
    else if (name.equals(DOWN))
    {
      down = isPressed;
    }
  }

  @Override
  public void update(float tpf)
  {
    camDir.set(this.app.getCamera().getDirection()).multLocal(10f, 0, 10f);
    camLeft.set(this.app.getCamera().getLeft()).multLocal(10.0f);
    walkDirection.set(0, 0, 0);
    if (left)
    {
      audioFootstep.play();
      walkDirection.addLocal(camLeft);
      rotatePlayerModel(0, -1);
    }
    if (right)
    {
      audioFootstep.play();
      walkDirection.addLocal(camLeft.negate());
      rotatePlayerModel(0, 1);
    }
    if (up)
    {
      audioFootstep.play();
      walkDirection.addLocal(camDir);
      rotatePlayerModel(1, 0);
    }
    if (down)
    {
      audioFootstep.play();
      walkDirection.addLocal(camDir.negate());
      rotatePlayerModel(-1, 0);
    }

    player.playerPhys.setWalkDirection(walkDirection.multLocal(1));
    player.playerPhys.setViewDirection(camDir);
  }

  public void rotatePlayerModel(float x, float z)
  {
    rotateVal += 2f;
    rotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotateVal, new Vector3f(x, 0, z));
    player.model.setLocalRotation(rotate);
  }

  private void setUpAudio()
  {
    audioFootstep = new AudioNode(app.getAssetManager(), "assets/sounds/footsteps.ogg", false);
    audioFootstep.setPositional(false);
    audioFootstep.setLooping(false);
    audioFootstep.setVolume(1);
    app.getRootNode().attachChild(audioFootstep);
  }

  private void setUpKeys()
  {
    // Basic character movement
    inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping(UP, new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping(DOWN, new KeyTrigger(KeyInput.KEY_S));
    inputManager.addListener(this, LEFT);
    inputManager.addListener(this, RIGHT);
    inputManager.addListener(this, UP);
    inputManager.addListener(this, DOWN);
  }
}