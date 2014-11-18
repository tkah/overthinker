package UClient; /**
 * Created by Torran on 11/9/14.
 */

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.WaterFilter;

import java.util.ArrayList;

public class UClient extends SimpleApplication
  implements ActionListener, AnalogListener
{
  private static final int SPHERE_RESOURCE_COUNT = 100;
  private static final float SPHERE_RESOURCE_RADIUS = 1.0f;
  private static final float PLAYER_SPHERE_START_RADIUS = 2.0f;
  private static final float MAP_TILT_RATE = 0.008f;
  private static final float WATER_HEIGHT_DEFAULT_RATE = 0.005f;
  private static final float WATER_HEIGHT_PLAYER_RATE = 0.001f; // Should be somewhat lower than the DEFAULT_RATE,
                                                                // but water height should continue increase no matter what

  private float waterHeight = 20.0f;
  private float verticalAngle = 30 * FastMath.DEG_TO_RAD;
  private float maxVerticalAngle = 85 * FastMath.DEG_TO_RAD;
  private float minVerticalAngle = -85 * FastMath.DEG_TO_RAD;

  private Vector3f lightDir = new Vector3f(-4.9f, -2.3f, 5.9f);
  private Vector3f walkDirection = new Vector3f();
  private Vector3f camDir = new Vector3f();
  private Vector3f camLeft = new Vector3f();

  private Node resources;
  private Node playerNode;
  private Node pivot;
  private CameraNode camNode;

  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;
  private CharacterControl playerControl;
  //private BetterCharacterControl playerControl;


  private SphereCollisionShape sphereShape;
  private Sphere playerSphere;
  private Geometry playerG;

  private FilterPostProcessor fpp;
  private WaterFilter water;
  private TerrainQuad terrain;
  private Material mat_terrain;

  private boolean playerNeedsScaling;
  private int scaleStartTime;

  private boolean left = false, right = false, up = false, down = false, slowWater = false;
  private boolean mapTiltLeft = false, mapTiltRight = false, mapTiltForward = false, mapTiltBack = false;
  private ArrayList<SphereResource> sphereResourceArrayList = new ArrayList<SphereResource>();
  private ArrayList<SphereResource> sphereResourcesToShrink = new ArrayList<SphereResource>();

  private Quaternion mapTilt = new Quaternion();
  float rotation; // Save rotation levels for each direction
  float tiltRotationBack, tiltRotationForward, tiltRotationLeft, tiltRotationRight;
  float tiltMapX, tiltMapY = 0;

  /** Server Communcation - Not yet implemented **/
  private Vector3f myLoc = new Vector3f(); // Might replace with 'walkDirection' from above
  private ArrayList<Vector3f> playerLocs = new ArrayList<Vector3f>();
  Integer distortionVal;  //Might consider waterHeight as a possible "distortion"

  /** Create AudioNodes **/
  private AudioNode audio_ocean;
  private AudioNode audio_footsteps;
  private AudioNode audio_jump;
  private AudioNode audio_collect;

  /**
   * Class entry point
   * @param args - command line arguments
   */
  public static void main(String[] args)
  {
    UClient app = new UClient();
    app.start();
  }

  /**
   * Overrides SimpleApplication initialization
   */
  @Override
  public void simpleInitApp()
  {
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    resources = new Node("Resources");
    rootNode.attachChild(resources);
    playerNode = new Node("player");
    pivot = new Node("Pivot");

    viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

    setUpKeys();
    setUpLight();
    setUpLandscape();
    setUpWater();
    setUpPlayer();
    setUpCamera();
    createSphereResources();


    Globals.setUpTimer();
    Globals.startTimer();
    initAudio();
  }

  /**
   * Custom actions for mouse actions
   * @param binding - name of key binding
   * @param value   - movement value
   * @param tpf     - time per frame
   */
  public void onAnalog(String binding, float value, float tpf)
  {
    if (binding.equals("TurnLeft"))
    {
      Quaternion turn = new Quaternion();
      turn.fromAngleAxis(FastMath.PI * value, Vector3f.UNIT_Y);
      playerControl.setViewDirection(turn.mult(playerControl.getViewDirection()));
    }
    else if (binding.equals("TurnRight"))
    {
      Quaternion turn = new Quaternion();
      turn.fromAngleAxis(-FastMath.PI * value, Vector3f.UNIT_Y);
      playerControl.setViewDirection(turn.mult(playerControl.getViewDirection()));
    }
    else if (binding.equals("MouseDown")) checkVertAngle(value);
    else if (binding.equals("MouseUp")) checkVertAngle(-value);

    pivot.getLocalRotation().fromAngleAxis(verticalAngle, Vector3f.UNIT_X);
  }

  private void checkVertAngle(float value)
  {
    float angle = FastMath.PI * value;
    verticalAngle += angle;
    if (verticalAngle > maxVerticalAngle) verticalAngle = maxVerticalAngle;
    else if (verticalAngle < minVerticalAngle) verticalAngle = minVerticalAngle;
  }

  /**
   * Custom actions for key bindings
   * @param binding   - name of key binding
   * @param isPressed - is the binding active
   * @param tpf       - time per frame
   */
  public void onAction(String binding, boolean isPressed, float tpf)
  {
    if (binding.equals("Left")) left = isPressed;
    else if (binding.equals("Right")) right = isPressed;
    else if (binding.equals("Up")) up = isPressed;
    else if (binding.equals("Down")) down = isPressed;
    else if (binding.equals("SlowWater")) slowWater = isPressed;
    else if (binding.equals("MapTiltBack")) mapTiltBack = isPressed;
    else if (binding.equals("MapTiltForward")) mapTiltForward = isPressed;
    else if (binding.equals("MapTiltLeft")) mapTiltLeft = isPressed;
    else if (binding.equals("MapTiltRight")) mapTiltRight = isPressed;
    else if (binding.equals("Jump"))
    {
      if (isPressed) playerControl.jump();
    }
  }

  /**
   * Main event loop where most game actions occur and
   *   non-action related collision detection with player handled
   * @param tpf - timer per frame
   */
  @Override
  public void simpleUpdate(float tpf)
  {
    // Raise Water Level, to be controlled by EEG
    if (!slowWater) water.setWaterHeight(water.getWaterHeight() + WATER_HEIGHT_DEFAULT_RATE);
    else water.setWaterHeight(water.getWaterHeight() + WATER_HEIGHT_PLAYER_RATE);

    // Tilt Map, to be controlled by EEG gyroscope
    if (!mapTiltLeft && !mapTiltRight && !mapTiltBack && !mapTiltForward) // EEG not rotating, move back to normal
    {
      if (tiltMapX < 0 && tiltMapY > 0)
      {
        tiltMapX += MAP_TILT_RATE;
        tiltMapY -= MAP_TILT_RATE;
      }
      if (tiltMapX < 0 && tiltMapY < 0)
      {
        tiltMapX += MAP_TILT_RATE;
        tiltMapY += MAP_TILT_RATE;
      }
      else if (tiltMapX > 0 && tiltMapY < 0)
      {
        tiltMapX -= MAP_TILT_RATE;
        tiltMapY += MAP_TILT_RATE;
      }
      else if (tiltMapX > 0 && tiltMapY > 0)
      {
        tiltMapX -= MAP_TILT_RATE;
        tiltMapY -= MAP_TILT_RATE;
      }
      else if (tiltMapX < 0) tiltMapX += MAP_TILT_RATE;
      else if (tiltMapX > 0) tiltMapX -= MAP_TILT_RATE;
      else if (tiltMapY > 0) tiltMapY -= MAP_TILT_RATE;
      else if (tiltMapY < 0) tiltMapY += MAP_TILT_RATE;
    }

    if (mapTiltForward&&mapTiltLeft)
    {
      tiltMapY += MAP_TILT_RATE;
      tiltMapX += MAP_TILT_RATE;
    }
    else if (mapTiltForward&&mapTiltRight)
    {
      tiltMapY += MAP_TILT_RATE;
      tiltMapX -= MAP_TILT_RATE;
    }
    else if (mapTiltBack&&mapTiltLeft)
    {
      tiltMapY -= MAP_TILT_RATE;
      tiltMapX += MAP_TILT_RATE;
    }
    else if (mapTiltBack&&mapTiltRight)
    {
      tiltMapY -= MAP_TILT_RATE;
      tiltMapX -= MAP_TILT_RATE;
    }
    else if (mapTiltLeft) tiltMapX += MAP_TILT_RATE;
    else if (mapTiltRight) tiltMapX -= MAP_TILT_RATE;
    else if (mapTiltForward) tiltMapY += MAP_TILT_RATE;
    else if (mapTiltBack) tiltMapY -= MAP_TILT_RATE;
    tiltMap();

    // Control Movement and Player Rotation
    camDir.set(cam.getDirection()).multLocal(0.6f); //20f for BetterCharacterControl
    camDir.setY(0); // Keep from flying into space when camera angle looking skyward
    camLeft.set(cam.getLeft()).multLocal(0.4f); //20f for BetterCharacterControl
    walkDirection.set(0, 0, 0);

    if (left || right || up || down) rotation += 3;
    if (left) moveBall(0, -1.0f, camLeft);
    if (right) moveBall(0, 1.0f, camLeft.negate());
    if (up) moveBall(1.0f, 0, camDir);
    if (down) moveBall(-1.0f,0, camDir.negate());
    if (up&&right) moveBall(1.0f, 1.0f, null);
    if (up&&left) moveBall(1.0f, -1.0f, null);
    if (down&&right) moveBall(-1.0f, 1.0f, null);
    if (down&&left) moveBall(-1.0f, -1.0f, null);

    addMovementSound((up || down || left || right ));

    playerControl.setWalkDirection(walkDirection);

    // Collision Scaling
    if (playerNeedsScaling) scalePlayer();
    boolean clear = true;
    for (SphereResource s : sphereResourcesToShrink)
    {
      clear = false;
      if (s.getShrink()) s.setSphereToDisappear();
      else s.getGeometry().removeFromParent();
    }
    if (clear) sphereResourcesToShrink.clear();

    CollisionResults results = new CollisionResults();
    resources.collideWith(playerG.getWorldBound(), results);

    if (results.size() > 0)
    {
      CollisionResult closest = results.getClosestCollision();
      System.out.println("What was hit? " + closest.getGeometry().getName());

      boolean isHit = closest.getGeometry().getUserData("isHit");
      if (!isHit)
      {
        //audio_collect.play();
        int sResId = closest.getGeometry().getUserData("id");
        closest.getGeometry().setUserData("isHit", true);
        SphereResource s = sphereResourceArrayList.get(sResId);
        s.setShrink(true);
        sphereResourcesToShrink.add(s);
        scaleStartTime = Globals.getTotSecs();
        playerNeedsScaling = true;
        scalePlayer();

      }
    }
    //move the audio with the camera
    listener.setLocation(cam.getLocation());
    listener.setRotation(cam.getRotation());
  }

  private void tiltMap ()
  {
    mapTilt = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * tiltMapX, new Vector3f(0, 0, 1.0f));
    Quaternion q = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * tiltMapY, new Vector3f(1.0f, 0, 0));
    Quaternion m = mapTilt.mult(q);
    terrain.setLocalRotation(m);
    landscape.setPhysicsRotation(m);
  }

  private void moveBall(float x, float z, Vector3f c)
  {
    Quaternion ballRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotation, new Vector3f(x, 0,z));
    playerG.setLocalRotation(ballRotate);
    if (c != null) walkDirection.addLocal(c);
  }

  private void scalePlayer()
  {
    int curTime = Globals.getTotSecs();
    int duration;

    playerNode.scale(Globals.SCALE_BY);
    sphereShape.setScale(playerNode.getWorldScale());
    duration = curTime - scaleStartTime;
    if (duration >= Globals.SCALE_ANIM_TIME) playerNeedsScaling = false;
  }

  /** ---Initialization methods--- **/

  private void setUpCamera()
  {
    flyCam.setMoveSpeed(100);

    // For third person cam
    // pivot node allows for mouse tracking of player character
    mouseInput.setCursorVisible(false);
    camNode = new CameraNode("Camera Node", cam);
    camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
    camNode.setLocalTranslation(new Vector3f(0, 4, -18));
    pivot.attachChild(camNode);
    Quaternion quat = new Quaternion();
    quat.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
    playerNode.attachChild(pivot);
    camNode.setEnabled(true);
    flyCam.setEnabled(false);
    pivot.getLocalRotation().fromAngleAxis(verticalAngle, Vector3f.UNIT_X);

    // For first person camera
    //camNode.lookAt(playerNode.getLocalTranslation(), Vector3f.UNIT_Y);
  }

  private void setUpPlayer()
  {
    playerSphere = new Sphere(32, 32, PLAYER_SPHERE_START_RADIUS);

    // Tutorial pond ball
    playerG = new Geometry("Shiny rock", playerSphere);
    playerSphere.setTextureMode(Sphere.TextureMode.Projected);
    TangentBinormalGenerator.generate(playerSphere);
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
    mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
    mat.setBoolean("UseMaterialColors", true);
    mat.setColor("Diffuse", ColorRGBA.White);
    mat.setColor("Specular", ColorRGBA.White);
    mat.setFloat("Shininess", 64f);
    playerG.setMaterial(mat);
    playerNode.attachChild(playerG);


    /* Red Balls
    playerG = new Geometry("Sphere", playerSphere);
    Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setBoolean("UseMaterialColors", true);
    mat.setColor("Diffuse", ColorRGBA.Red);
    mat.setColor("Specular", ColorRGBA.White);
    mat.setFloat("Shininess", 64f);
    playerG.setMaterial(mat);
    playerNode.attachChild(playerG);
    */

    sphereShape = new SphereCollisionShape(PLAYER_SPHERE_START_RADIUS);

    // BetterCharacteControl moves, but bounces and falls through ground
    //playerControl = new BetterCharacterControl(PLAYER_SPHERE_START_RADIUS, PLAYER_SPHERE_START_RADIUS, 1.0f);
    //playerControl.setJumpForce(new Vector3f(0,0,0));
    //playerControl.setGravity(new Vector3f(0,-10,0));
    //playerControl.setApplyPhysicsLocal(true);
    //playerControl.setSpatial(playerG);
    playerControl = new CharacterControl(sphereShape, 0.05f);
    playerControl.setJumpSpeed(20);
    playerControl.setFallSpeed(30);
    playerControl.setGravity(30);
    playerControl.setPhysicsLocation(new Vector3f(-340, 80, -400));
    playerNode.setLocalTranslation(new Vector3f(-340, 80, -400));
    playerNode.addControl(playerControl);
    rootNode.attachChild(playerNode);
    bulletAppState.getPhysicsSpace().add(playerControl);
  }

  private void setUpLandscape()
  {
    /** Create terrain material and load four textures into it. */
    mat_terrain = new Material(assetManager,
      "Common/MatDefs/Terrain/Terrain.j3md");

    /** Add ALPHA map (for red-blue-green coded splat textures) */
    mat_terrain.setTexture("Alpha", assetManager.loadTexture(
      "assets/terrains/tieredmaze1color.png"));

    /** Add GRASS texture into the red layer (Tex1). */
    Texture grass = assetManager.loadTexture(
      "Textures/Terrain/splat/grass.jpg");
    grass.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex1", grass);
    mat_terrain.setFloat("Tex1Scale", 64f);

    /** Add DIRT texture into the green layer (Tex2) */
    Texture dirt = assetManager.loadTexture(
      "Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex2", dirt);
    mat_terrain.setFloat("Tex2Scale", 32f);

    /** Add ROAD texture into the blue layer (Tex3) */
    Texture rock = assetManager.loadTexture(
      "Textures/Terrain/splat/road.jpg");
    rock.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("Tex3", rock);
    mat_terrain.setFloat("Tex3Scale", 128f);

    /** Create the height map */
    AbstractHeightMap heightmap = null;
    Texture heightMapImage = assetManager.loadTexture(
      "assets/terrains/tieredmaze1.png");
    heightmap = new ImageBasedHeightMap(heightMapImage.getImage());

    // Height Map Randomization
        /*HillHeightMap heightmap = null;
        HillHeightMap.NORMALIZE_RANGE = 100; // optional
        try {
            heightmap = new HillHeightMap(513, 1000, 5, 1000, (byte) 3); // byte 3 is a random seed
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

    heightmap.load();

    /** We have prepared material and heightmap.
     * Now we create the actual terrain:
     * -Create a TerrainQuad and name it "my terrain".
     * -A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
     * -We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
     * -As LOD step scale we supply Vector3f(1,1,1).
     * -We supply the prepared heightmap itself.
     */
    int patchSize = 65;
    terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());

    /** We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, 0, 0);
    terrain.setLocalScale(2f, 1f, 2f);
    rootNode.attachChild(terrain);

    /** The LOD (level of detail) depends on were the camera is: */
    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
    terrain.addControl(control);

    // We set up collision detection for the scene by creating a
    // compound collision shape and a static RigidBodyControl with mass zero.
    CollisionShape sceneShape =
      CollisionShapeFactory.createMeshShape(terrain);
    landscape = new RigidBodyControl(sceneShape, 0);
    terrain.addControl(landscape);
    bulletAppState.getPhysicsSpace().add(landscape);
  }

  private void setUpWater()
  {
    fpp = new FilterPostProcessor(assetManager);
    water = new WaterFilter(rootNode, lightDir);
    water.setWaterHeight(waterHeight);
    water.setDeepWaterColor(new ColorRGBA(0.0f, 0.5f, 0.5f, 1.0f));
    fpp.addFilter(water);
    viewPort.addProcessor(fpp);
  }

  private void setUpLight()
  {
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.3f));
    rootNode.addLight(al);

    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    //dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
    dl.setDirection(lightDir.normalizeLocal());
    rootNode.addLight(dl);
  }

  private void setUpKeys()
  {
    // Mouse pivoting for 3rd person cam
    inputManager.addMapping("TurnLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
    inputManager.addMapping("TurnRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
    inputManager.addMapping("MouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
    inputManager.addMapping("MouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
    inputManager.addListener(this, "TurnLeft");
    inputManager.addListener(this, "TurnRight");
    inputManager.addListener(this, "MouseDown");
    inputManager.addListener(this, "MouseUp");

    // Basic character movement
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    inputManager.addListener(jumpActionListener,"Jump");


    // Tilting map, to be replaced by headset commands
    inputManager.addMapping("MapTiltBack", new KeyTrigger(KeyInput.KEY_I));
    inputManager.addMapping("MapTiltLeft", new KeyTrigger(KeyInput.KEY_J));
    inputManager.addMapping("MapTiltRight", new KeyTrigger(KeyInput.KEY_L));
    inputManager.addMapping("MapTiltForward", new KeyTrigger(KeyInput.KEY_K));
    inputManager.addListener(this, "MapTiltBack");
    inputManager.addListener(this, "MapTiltLeft");
    inputManager.addListener(this, "MapTiltRight");
    inputManager.addListener(this, "MapTiltForward");

    // Lower water level, to be replaced by headset commands
    inputManager.addMapping("SlowWater", new KeyTrigger(KeyInput.KEY_LSHIFT));
    inputManager.addListener(this, "SlowWater");
  }

  private void createSphereResources()
  {
    for (int i = 0; i < SPHERE_RESOURCE_COUNT; i++)
    {
      int x = Globals.getRandInt(Globals.MAP_WIDTH * 2) - Globals.MAP_WIDTH;
      int z = Globals.getRandInt(Globals.MAP_HEIGHT * 2) - Globals.MAP_HEIGHT;
      SphereResource sRes = new SphereResource(SPHERE_RESOURCE_RADIUS, x, z, i, assetManager);
      bulletAppState.getPhysicsSpace().add(sRes.getSphereResourcePhy());
      sphereResourceArrayList.add(sRes);
      resources.attachChild(sRes.getGeometry());
    }
  }


  private void initAudio(){

    //collect object
    audio_collect = new AudioNode(assetManager, "assets/sounds/collect.ogg",false);
    audio_collect.setPositional(false);
    audio_collect.setLooping(false);
    audio_collect.setVolume(2);
    rootNode.attachChild(audio_collect);

    //walking sounds
    audio_footsteps = new AudioNode(assetManager, "assets/sounds/footsteps.ogg",true);
    audio_footsteps.setPositional(false);
    audio_footsteps.setLooping(true);
    audio_footsteps.setVolume(2);
    rootNode.attachChild(audio_footsteps);


    //jumping sound
    audio_jump = new AudioNode(assetManager, "assets/sounds/pop.ogg",false);
    audio_jump.setPositional(false);
    audio_jump.setLooping(false);
    audio_jump.setVolume(2);
    rootNode.attachChild(audio_jump);

    //ambient map sounds
    audio_ocean = new AudioNode(assetManager,"assets/sounds/wavesLoop.ogg",true);
    audio_ocean.setLooping(true);
    audio_ocean.setPositional(true);
    audio_ocean.setVolume(1);
    rootNode.attachChild(audio_ocean);
    audio_ocean.play();
  }
  /** Method to add sounds when buttons are pressed **/
  private void addMovementSound(boolean emmit){
    if(emmit){
      audio_footsteps.play();
    }else{
      audio_footsteps.stop();
    }
  }

  private ActionListener jumpActionListener = new ActionListener() {
    @Override
    public void onAction(String name, boolean keyPressed, float v) {
      if (name.equals("Jump") && keyPressed)
        audio_jump.playInstance();

    }
  };
}