package overthinker.client;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.util.TangentBinormalGenerator;

import java.util.ArrayList;

/**
 * Created by Torran on 11/21/14.
 */
public class UnderNode extends PlayerNode
{


  private PlayerControl playerControl;
  private Sphere playerSphere;
  private Geometry playerG;
  private Material playerMat;
  private Node pivot;
  private CameraNode camNode;
  private Camera cam;
  private GhostControl camGhost;
  private ParticleEmitter dustEmitterRight;
  private ParticleEmitter dustEmitterLeft;
  private static AudioNode warning_sound;

  // Possibly move to abstract class
  private TerrainQuad terrain;
  private Node collidableNode;
  private AssetManager assetManager;
  private BulletAppState bulletAppState;
  private boolean dead = false;
  public boolean pushOff = false;

  private float verticalAngle = 30 * FastMath.DEG_TO_RAD;
  private float maxVerticalAngle = 85 * FastMath.DEG_TO_RAD;
  private float minVerticalAngle = -15 * FastMath.DEG_TO_RAD;
  private float rotation;
  private float rotSpeed = 300f;
  private float moveSpeed = 20f;
  private int scaleUpStartTime;
  private int flashStartTime;
  private int lastFlashDiff = 0;

  private boolean doFlash = false;
  private boolean lastFlashWasRed = false;
  private boolean playerNeedsScalingUp = false;
  private boolean onGround = false;
  private boolean shrink = false;
  private boolean left = false, right = false, up = false, down = false, slowWater = false, jump = false;

  int id;

  private Vector3f walkDirection = new Vector3f();

  public UnderNode(String name, Camera cam, TerrainQuad terrain, AssetManager assetManager, BulletAppState bulletAppState, Node colNode, int id)
  {
    super(name);
    this.cam = cam;
    this.terrain = terrain;
    this.assetManager = assetManager;
    this.bulletAppState = bulletAppState;
    this.id = id;
    collidableNode = colNode;
    initWarningSound();

    pivot = new Node("Pivot");
  }

  public void onAction(String binding, boolean isPressed, float tpf)
  {
    if (binding.equals("MapTiltBack") || binding.equals("MapTiltForward") ||
        binding.equals("MapTiltLeft") || binding.equals("MapTiltRight"))
    {
      playerControl.onAction(binding, isPressed, tpf);
    }
    else if (binding.equals("Left")) left = isPressed;
    else if (binding.equals("Right")) right = isPressed;
    else if (binding.equals("Up")) up = isPressed;
    else if (binding.equals("Down")) down = isPressed;
    else if (binding.equals("SlowWater")) slowWater = isPressed;
    else if (binding.equals("Shrink")) shrink = isPressed;
    else if (binding.equals("Jump"))
    {
      if (isPressed)
      {
        if (isOnGround()) playerControl.jump();
      }
    }
  }

  public void onAnalog(String binding, float value, float tpf)
  {
    if (binding.equals("TurnLeft")) playerControl.turn(FastMath.PI * value);
    else if (binding.equals("TurnRight")) playerControl.turn(-FastMath.PI * value);
    else if (binding.equals("MouseDown")) checkVertAngle(value);
    else if (binding.equals("MouseUp")) checkVertAngle(-value);
    else if (binding.equals("ZoomIn"))
    {
      if (!(camNode.getLocalTranslation().getZ() + 1 > 20))
      {
        camNode.setLocalTranslation(new Vector3f(0, camNode.getLocalTranslation().getY() - .22f, camNode.getLocalTranslation().getZ() + 1 ));
      }

    }
    else if (binding.equals("ZoomOut"))
    {
      camNode.setLocalTranslation(new Vector3f(0, camNode.getLocalTranslation().getY() + .22f, camNode.getLocalTranslation().getZ() - 1));
    }

    pivot.getLocalRotation().fromAngleAxis(verticalAngle, Vector3f.UNIT_X);
  }

  public boolean isOnGround()
  {
    return onGround;
  }

  public void setIsOnGround (boolean status)
  {
    onGround = status;
  }

  public void update(float tpf)
  {
    if (getHeight() < .6f)
    {
      flash(tpf);
    }
    else if (playerMat.getParam("Diffuse").getValue() !=  ColorRGBA.White) playerMat.setColor("Diffuse", ColorRGBA.White);

    onGround = playerControl.checkGravity(onGround, getLocalTranslation(), collidableNode, pivot, camNode);
    dustEmitterRight.setLowLife(0);
    dustEmitterRight.setHighLife(0);
    dustEmitterLeft.setLowLife(0);
    dustEmitterLeft.setHighLife(0);

    if (left || right || up || down) rotation += tpf*rotSpeed;

    if (left) moveBall(0, -1.0f);
    if (right) moveBall(0, 1.0f);
    if (up) moveBall(1.0f, 0);
    if (down) moveBall(-1.0f, 0);
    if (up && right) moveBall(1.0f, 1.0f);
    if (up && left) moveBall(1.0f, -1.0f);
    if (down && right) moveBall(-1.0f, 1.0f);
    if (down && left) moveBall(-1.0f, -1.0f);

    Vector3f modelForwardDir = getWorldRotation().mult(Vector3f.UNIT_Z);
    Vector3f modelLeftDir = getWorldRotation().mult(Vector3f.UNIT_X);
    walkDirection.set(0,0,0);
    if (up) walkDirection.addLocal(modelForwardDir.mult(moveSpeed));
    else if (down) walkDirection.addLocal(modelForwardDir.mult(moveSpeed).negate());
    if (left) walkDirection.addLocal(modelLeftDir.mult(moveSpeed));
    else if (right) walkDirection.addLocal(modelLeftDir.mult(moveSpeed).negate());

    playerControl.addMovementSound((up || down || left || right));

    if (onGround || pushOff) playerControl.setWalkDirection(walkDirection);
    pushOff = false;
    //else playerControl.setWalkDirection(walkDirection.set(0,0,0));

    // Collision Scaling
    if (playerNeedsScalingUp && playerControl.getHeight() < Globals.MAX_PLAYER_SIZE) scalePlayerUp();
  }

  private void moveBall(float x, float z)
  {
    //TODO: move quaternion to server so that other players can use for rotation
    Quaternion ballRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotation, new Vector3f(x, 0, z));
    playerG.setLocalRotation(ballRotate);
    dustEmitterRight.setLowLife(.1f);
    dustEmitterRight.setHighLife(1f);
    dustEmitterLeft.setLowLife(.1f);
    dustEmitterLeft.setHighLife(1f);
  }

  @Override
  public void scalePlayerDown(float tpf)
  {
    rotSpeed += 2f;
    moveSpeed += .01f;
    scale(.99f);
    playerControl.setScale(.99f);
    walkDirection.addLocal(new Vector3f(0, 0, .5f));
    playerControl.setWalkDirection(walkDirection);
  }

  @Override
  public void scalePlayerUp()
  {
    int curTime = Globals.getTotSecs();
    int duration;

    rotSpeed -= 1f;
    moveSpeed -= .01f;
    scale(Globals.SCALE_BY);
    playerControl.setScale(Globals.SCALE_BY);
    duration = curTime - scaleUpStartTime;
    if (duration >= Globals.SCALE_ANIM_TIME) playerNeedsScalingUp = false;
  }

  private void checkVertAngle(float value)
  {
    float angle = FastMath.PI * value;
    verticalAngle += angle;
    if (verticalAngle > maxVerticalAngle) verticalAngle = maxVerticalAngle;
    else if (verticalAngle < minVerticalAngle) verticalAngle = minVerticalAngle;
  }

  public void initWarningSound(){
    warning_sound= new AudioNode(assetManager, "overthinker/assets/sounds/warning.ogg",false);
    warning_sound.setPositional(false);
    warning_sound.setLooping(true);
    warning_sound.setVolume(2);
  }

  public static void playWarningSound(){
    warning_sound.play();
  }

  public static void stopWarningSound(){
    warning_sound.stop();
  }

  public void flash(float tpf)
  {

    if (!doFlash)
    {
      doFlash = true;
      lastFlashWasRed = true;
      flashStartTime = Globals.getTotSecs();
      playerMat.setColor("Diffuse", ColorRGBA.Red);
      lastFlashDiff = Globals.getTotSecs() - flashStartTime;
      playWarningSound();
    }
    else if (Globals.getTotSecs() - flashStartTime >  lastFlashDiff)
    {
      if (lastFlashWasRed)
      {
        playerMat.setColor("Diffuse", ColorRGBA.White);
        lastFlashWasRed = false;
      }
      else
      {
        playerMat.setColor("Diffuse", ColorRGBA.Red);
        lastFlashWasRed = true;
      }
      lastFlashDiff = Globals.getTotSecs() - flashStartTime;
    }

  }

  /** ---Init Methods--- **/
  public void setUpPlayer()
  {
    setUpCamera(cam);
    playerSphere = new Sphere(32, 32, Globals.PLAYER_SPHERE_START_RADIUS);

    playerG = new Geometry("Shiny rock", playerSphere);
    playerSphere.setTextureMode(Sphere.TextureMode.Projected);
    TangentBinormalGenerator.generate(playerSphere);
    playerMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    if (id == 0) playerMat.setTexture("DiffuseMap", assetManager.loadTexture("overthinker/assets/textures/striated_rock_texture.JPG"));
    else if (id == 1) playerMat.setTexture("DiffuseMap", assetManager.loadTexture("overthinker/assets/textures/stone_texture.JPG"));
    else if (id == 2) playerMat.setTexture("DiffuseMap", assetManager.loadTexture("overthinker/assets/textures/barnacles_texture.JPG"));
    else System.out.println("UnderNode: setUpPlayer - id error");
    playerMat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
    playerMat.setBoolean("UseMaterialColors", true);
    playerMat.setColor("Diffuse", ColorRGBA.White.clone());
    playerMat.setColor("Ambient", ColorRGBA.White.mult(0.5f));
    playerG.setMaterial(playerMat);
    playerG.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    attachChild(playerG);

    //BetterCharacteControl moves, but bounces and falls through ground
    playerControl = new PlayerControl(Globals.PLAYER_SPHERE_START_RADIUS, Globals.PLAYER_SPHERE_START_RADIUS, 10f, assetManager);
    playerControl.setJumpForce(new Vector3f(0,250,0));
    playerControl.setGravity(new Vector3f(0, -10, 0));
    //setLocalTranslation(new Vector3f(-183, 80, -431));
    // Player Locs: 1,2,3 - Going counterclockwise from top-left
    //Circle Start 1: -344, 80, -380
    //Circle Start 2: 289, 80, 414.7f
    //Circle Start 3: 381,80, -387.5f
    //Radio Start 1: -260, 110, -390
    //Radio Start 2: -170, 110, 350
    //Radio Start 3: 260, 110, -390
    //Penta Start 1: -183, 80, -431
    //Penta Start 2: 360, 59, 450
    //Penta Start 3: 345,80,-284
    addControl(playerControl);
    bulletAppState.getPhysicsSpace().add(playerControl);

    dustEmitterRight = new ParticleEmitter("dust emitter right", ParticleMesh.Type.Triangle, 100);
    dustEmitterLeft = new ParticleEmitter("dust emitter left", ParticleMesh.Type.Triangle, 100);

    Material dustMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    dustMat.setTexture("Texture", assetManager.loadTexture("overthinker/assets/effects/smoke.png"));

    dustEmitterRight.setMaterial(dustMat);
    dustEmitterLeft.setMaterial(dustMat);

    dustEmitterRight.setImagesX(2);
    dustEmitterRight.setImagesY(2);
    dustEmitterRight.setSelectRandomImage(true);
    dustEmitterRight.setRandomAngle(true);
    dustEmitterRight.setStartColor(new ColorRGBA(ColorRGBA.Brown));
    dustEmitterRight.setEndColor(new ColorRGBA(ColorRGBA.Brown));
    dustEmitterRight.getParticleInfluencer().setVelocityVariation(1f);

    dustEmitterLeft.setImagesX(2);
    dustEmitterLeft.setImagesY(2);
    dustEmitterLeft.setSelectRandomImage(true);
    dustEmitterLeft.setRandomAngle(true);
    dustEmitterLeft.setStartColor(new ColorRGBA(ColorRGBA.Brown));
    dustEmitterLeft.setEndColor(new ColorRGBA(ColorRGBA.Brown));
    dustEmitterLeft.getParticleInfluencer().setVelocityVariation(1f);

    dustEmitterRight.setLocalTranslation(-.3f, -1.7f, 0);
    dustEmitterLeft.setLocalTranslation(.3f, -1.7f, 0);

    attachChild(dustEmitterRight);
    attachChild(dustEmitterLeft);


  }



  public ArrayList setUpControls(InputManager inputManager)
  {
    // Mouse pivoting for 3rd person cam
    inputManager.addMapping("TurnLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
    inputManager.addMapping("TurnRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
    inputManager.addMapping("MouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
    inputManager.addMapping("MouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
    inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
    inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
    actionStrings.add("TurnLeft");
    actionStrings.add("TurnRight");
    actionStrings.add("TurnDown");
    actionStrings.add("MouseUp");
    actionStrings.add("MouseDown");
    actionStrings.add("ZoomIn");
    actionStrings.add("ZoomOut");

    // Basic character movement
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("Shrink", new KeyTrigger(KeyInput.KEY_E));
    actionStrings.add("Left");
    actionStrings.add("Right");
    actionStrings.add("Up");
    actionStrings.add("Down");
    actionStrings.add("Jump");
    actionStrings.add("Shrink");
    inputManager.addListener(jumpActionListener,"Jump");

    // Tilting map, to be replaced by headset commands
    inputManager.addMapping("MapTiltForward", new KeyTrigger(KeyInput.KEY_I));
    inputManager.addMapping("MapTiltLeft", new KeyTrigger(KeyInput.KEY_J));
    inputManager.addMapping("MapTiltRight", new KeyTrigger(KeyInput.KEY_L));
    inputManager.addMapping("MapTiltBack", new KeyTrigger(KeyInput.KEY_K));
    actionStrings.add("MapTiltBack");
    actionStrings.add("MapTiltLeft");
    actionStrings.add("MapTiltRight");
    actionStrings.add("MapTiltForward");

    // Lower water level, to be replaced by headset commands
    inputManager.addMapping("SlowWater", new KeyTrigger(KeyInput.KEY_LSHIFT));
    actionStrings.add("SlowWater");

    return actionStrings;
  }

  private void setUpCamera(Camera cam)
  {
    camNode = new CameraNode("CameraNode", cam);
    camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
    camNode.setLocalTranslation(new Vector3f(0, 4, -18));
    pivot.attachChild(camNode);
    attachChild(pivot);
    camNode.setEnabled(true);
    pivot.getLocalRotation().fromAngleAxis(verticalAngle, Vector3f.UNIT_X);

    // Add ghostCam to detect collisions with LandscapeControl, not enough time to fully flesh out before due date
    //camGhost = new GhostControl(new SphereCollisionShape(0.7f));
    //camNode.addControl(camGhost);
    //bulletAppState.getPhysicsSpace().add(camGhost);
  }

  public PlayerControl getPlayerControl()
  {
    return playerControl;
  }

  @Override
  public Geometry getGeometry()
  {
    return playerG;
  }

  @Override
  public void setDead(boolean val)
  {
    dead = val;
  }

  @Override
  public boolean isDead()
  {
    return dead;
  }

  @Override
  public float getHeight()
  {
    return playerControl.getHeight();
  }

  @Override
  public Node getCamNode()
  {
    return camNode;
  }

  @Override
  public void setScaleStartTime(int time)
  {
    scaleUpStartTime = time;
  }

  @Override
  public void setPlayerNeedsScaling (boolean val)
  {
    playerNeedsScalingUp = val;
  }

  @Override
  public ParticleEmitter getDustEmitterRight()
  {
   return dustEmitterRight;
  }

  @Override
  public ParticleEmitter getDustEmitterLeft()
  {
    return dustEmitterLeft;
  }

  @Override
  public boolean getShrink()
  {
    return shrink;
  }

  public ArrayList getAudio()
  {
    return playerControl.getAudio();
  }

  public float getVerticleAngle()
  {
    return verticalAngle;
  }

  public BetterCharacterControl getBCControl()
  {
    return playerControl;
  }

  private ActionListener jumpActionListener = new ActionListener() {
    @Override
    public void onAction(String name, boolean keyPressed, float v) {
      if (name.equals("Jump") && keyPressed)
        playerControl.playJump();
    }
  };
}
