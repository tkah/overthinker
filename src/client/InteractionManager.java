/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
  public static final String JUMP = "Jump";
  private boolean left = false;
  private boolean right = false;
  private boolean up = false;
  private boolean down = false;
  private Vector3f walkDirection = new Vector3f();
  private Vector3f camDir = new Vector3f();
  private Vector3f camLeft = new Vector3f();
  private InputManager inputManager;
  private AssetManager assetManager;
  private SimpleApplication app;
  private Player player;
  private float rotateVal;
  private AudioNode audioFootstep;
  private Vector3f rotateDirection = Vector3f.ZERO;
  private ParticleEmitter dustEmitter;

  @Override
  public void initialize(AppStateManager stateManager, Application app)
  {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    this.assetManager = this.app.getAssetManager();
    this.inputManager = this.app.getInputManager();
    this.player = this.app.getStateManager().getState(PlayerManager.class).player;
    this.rotateVal = 0f;
    setUpKeys();
    setUpAudio();
    setUpParticleEmitter();
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
    else if (name.equals(JUMP) && player.playerPhys.isOnGround())
    {
      player.playerPhys.jump();
    }
  }

  private void setUpParticleEmitter()
  {
    this.dustEmitter = new ParticleEmitter("DustEmitter", ParticleMesh.Type.Triangle, 100);
    Material dustMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    dustMat.setTexture("Texture", assetManager.loadTexture("assets/effects/smoke.png"));
    dustEmitter.setLocalTranslation(new Vector3f(0, -2, 0));
    dustEmitter.setMaterial(dustMat);
    dustEmitter.setImagesX(2);
    dustEmitter.setImagesY(2);
    dustEmitter.setStartColor(new ColorRGBA(.254f, .1568f, 0.098f, 1));   // brown
    dustEmitter.setEndColor(new ColorRGBA(1f, 1f, 1f, 0.5f)); // white
    dustEmitter.setFacingVelocity(true);
    dustEmitter.setStartSize(.5f);
    dustEmitter.setEndSize(.5f);
    dustEmitter.setLowLife(.9f);
    dustEmitter.setHighLife(1.1f);
    dustEmitter.setRotateSpeed(4);
    dustEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 10, 0));
    dustEmitter.setSelectRandomImage(true);
    dustEmitter.setRandomAngle(true);
    dustEmitter.getParticleInfluencer().setVelocityVariation(1.0f);
    player.attachChild(dustEmitter);
  }

  @Override
  public void update(float tpf)
  {
    camDir.set(this.app.getCamera().getDirection()).multLocal(20f, 0, 20f);
    camLeft.set(this.app.getCamera().getLeft()).multLocal(20.0f);
    walkDirection.set(0, 0, 0);
    rotateDirection.set(0, 0, 0);
    dustEmitter.setParticlesPerSec(0);
    if (left)
    {
      audioFootstep.play();
      walkDirection.addLocal(camLeft);
      rotateDirection.set(0, 0, -1);
    }
    if (right)
    {
      audioFootstep.play();
      walkDirection.addLocal(camLeft.negate());
      rotateDirection.set(0, 0, 1);
    }
    if (up)
    {
      audioFootstep.play();
      walkDirection.addLocal(camDir);
      rotateDirection.set(1, 0, 0);
    }
    if (down)
    {
      audioFootstep.play();
      walkDirection.addLocal(camDir.negate());
      rotateDirection.set(-1, 0, 0);
    }

    rotateDirection.normalizeLocal();
    rotatePlayerModel(rotateDirection);

    player.playerPhys.setWalkDirection(walkDirection);
    player.playerPhys.setViewDirection(camDir);
  }

  public void rotatePlayerModel(Vector3f rotateDirection)
  {
    rotateVal += 2f;
    Quaternion rotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotateVal, rotateDirection);
    player.model.setLocalRotation(rotate);
    dustEmitter.setParticlesPerSec(100);
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
    inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, LEFT);
    inputManager.addListener(this, RIGHT);
    inputManager.addListener(this, UP);
    inputManager.addListener(this, DOWN);
    inputManager.addListener(this, JUMP);
  }
}
