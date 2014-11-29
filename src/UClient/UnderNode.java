package UClient;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
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
  private Node pivot;
  private CameraNode camNode;
  private Camera cam;

  // Possibly move to abstract class
  private TerrainQuad terrain;
  private AssetManager assetManager;
  private BulletAppState bulletAppState;
  private boolean dead = false;

  private float verticalAngle = 30 * FastMath.DEG_TO_RAD;
  private float maxVerticalAngle = 85 * FastMath.DEG_TO_RAD;
  private float minVerticalAngle = -15 * FastMath.DEG_TO_RAD;
  private float rotation;
  private float rotSpeed = 300f;
  private float moveSpeed = 20f;
  private int scaleUpStartTime;

  private boolean playerNeedsScalingUp = false;
  private boolean onGround = false;
  private boolean shrink = false;
  private boolean left = false, right = false, up = false, down = false, slowWater = false, jump = false;

  private Vector3f walkDirection = new Vector3f();

  public UnderNode(String name, Camera cam, TerrainQuad terrain, AssetManager assetManager, BulletAppState bulletAppState)
  {
    super(name);
    this.cam = cam;
    this.terrain = terrain;
    this.assetManager = assetManager;
    this.bulletAppState = bulletAppState;
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
      camNode.setLocalTranslation(new Vector3f(0, camNode.getLocalTranslation().getY() - .22f, camNode.getLocalTranslation().getZ() + 1));
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
    onGround = playerControl.checkGravity(onGround, getLocalTranslation(), terrain);

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

    playerControl.setWalkDirection(walkDirection);

    // Collision Scaling
    if (playerNeedsScalingUp && playerControl.getHeight() < Globals.MAX_PLAYER_SIZE) scalePlayerUp();
  }

  private void moveBall(float x, float z)
  {
    Quaternion ballRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotation, new Vector3f(x, 0, z));
    playerG.setLocalRotation(ballRotate);
  }

  @Override
  public void scalePlayerDown()
  {
    rotSpeed += 2f;
    moveSpeed += .01f;
    scale(.99f);
    playerControl.setScale(.99f);
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

  /** ---Init Methods--- **/
  public void setUpPlayer()
  {
    setUpCamera(cam);
    playerSphere = new Sphere(32, 32, Globals.PLAYER_SPHERE_START_RADIUS);

    // Tutorial pond ball
    playerG = new Geometry("Shiny rock", playerSphere);
    playerSphere.setTextureMode(Sphere.TextureMode.Projected);
    TangentBinormalGenerator.generate(playerSphere);
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
    mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
    mat.setBoolean("UseMaterialColors", true);
    mat.setColor("Diffuse", ColorRGBA.White.clone());
    mat.setColor("Ambient", ColorRGBA.White.mult(0.5f));
    //mat.setFloat("Shininess", 64f);
    playerG.setMaterial(mat);
    playerG.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    attachChild(playerG);

    //BetterCharacteControl moves, but bounces and falls through ground
    playerControl = new PlayerControl(Globals.PLAYER_SPHERE_START_RADIUS, Globals.PLAYER_SPHERE_START_RADIUS, 10f, assetManager);
    playerControl.setJumpForce(new Vector3f(0,300,0));
    playerControl.setGravity(new Vector3f(0, -10, 0));
    setLocalTranslation(new Vector3f(-340, 80, -400));
    addControl(playerControl);
    bulletAppState.getPhysicsSpace().add(playerControl);
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

    // For third person cam
    // pivot node allows for mouse tracking of player character


    camNode = new CameraNode("Camera Node", cam);
    camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
    camNode.setLocalTranslation(new Vector3f(0, 4, -18));
    pivot.attachChild(camNode);
    Quaternion quat = new Quaternion();
    quat.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
    attachChild(pivot);
    camNode.setEnabled(true);
    pivot.getLocalRotation().fromAngleAxis(verticalAngle, Vector3f.UNIT_X);
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
  public boolean getShrink()
  {
    return shrink;
  }

  public ArrayList getAudio()
  {
    return playerControl.getAudio();
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
