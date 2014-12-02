package UClient; /**
 * Created by Torran on 11/9/14.
 */

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.Listener;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.light.Light;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FadeFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.water.WaterFilter;
import jme3utilities.Misc;
import jme3utilities.sky.SkyControl;

import java.util.Calendar;
import java.util.ArrayList;

public class GamePlayAppState extends AbstractAppState
  implements ActionListener, AnalogListener
{
  private static final int SPHERE_RESOURCE_COUNT = 250;
  private static final float SPHERE_RESOURCE_RADIUS = 1.0f;

  private SimpleApplication app;
  private Camera cam;
  private Node rootNode;
  private Node localRootNode;
  private AssetManager assetManager;
  private InputManager inputManager;
  private FlyByCamera flyCam;
  private ViewPort viewPort;
  private Listener listener;
  private SkyControl sc;

  private Vector3f lightDir = new Vector3f(4.1f, -3.2f, 0.1f);
  private AmbientLight ambientLight = null;
  private DirectionalLight mainLight = null;

  private Node keysNode;
  private Node collidableNode;
  private Node resources;
  private PlayerNode playerNode;
  private int playerType = 1;

  private float waterHeight = 20.0f;
  private float waterHeightRate = 0.05f;

  private BulletAppState bulletAppState;
  private LandscapeControl landscape;

  private FadeFilter fade;
  private FilterPostProcessor fpp;
  private WaterFilter water;
  private TerrainQuad terrain;
  private Material mat_terrain;
  private ParticleEmitter sparkEmitter;

  private ArrayList<SphereResource> sphereResourceArrayList = new ArrayList<SphereResource>();
  private ArrayList<SphereResource> sphereResourcesToShrink = new ArrayList<SphereResource>();
  private Vector3f[] keyLocArray = {new Vector3f(60, 65, -330), new Vector3f(35, 65, 345), new Vector3f(115, 65, -353)};
  private Vector3f[] doorLocArray = {new Vector3f(-293, 98, 143), new Vector3f(330, 98, -68), new Vector3f(-236, 98, 208)};
  private float[] doorSizeXArray = {15f, 15f, 17f};
  private float[] doorRotationArray = {-55f, 100f, -43f};
  private ArrayList<Key> keys = new ArrayList<Key>();
  private ArrayList<Door> doors = new ArrayList<Door>();

  /** Create AudioNodes **/
  private AudioNode audio_ocean;
  private AudioNode audio_collect;

  /**
   * Class initialization
   */
  public void initialize(AppStateManager stateManager, Application app)
  {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    this.cam = this.app.getCamera();
    this.rootNode = this.app.getRootNode();
    this.assetManager = this.app.getAssetManager();
    this.listener = this.app.getListener();
    viewPort = this.app.getViewPort();
    inputManager = this.app.getInputManager();
    flyCam = this.app.getFlyByCamera();
    sparkEmitter = new ParticleEmitter("spark emitter", ParticleMesh.Type.Triangle, 60);

    /** Set up Physics */
    bulletAppState = new BulletAppState();
    stateManager.attach(bulletAppState);
    resources = new Node("Resources");
    localRootNode = new Node("LocalRoot");
    localRootNode.attachChild(resources);
    collidableNode = new Node ("CollidableNode");
    keysNode = new Node ("KeyNode");

    flyCam.setMoveSpeed(100);

    createSphereResources();
    createDoorsAndKeys();
    setUpLandscape();
    setUpLight();
    setUpPlayer();

    fade = new FadeFilter(2); // 2 seconds
    fpp.addFilter(fade);

    viewPort.addProcessor(fpp);
    localRootNode.attachChild(collidableNode);
    rootNode.attachChild(localRootNode);

    sc.setEnabled(true);

    Globals.setUpTimer();
    Globals.startTimer();
    initAudio();

    inputManager.setCursorVisible(false);
  }

  /**
   * Custom actions for mouse actions
   * @param binding - name of key binding
   * @param value   - movement value
   * @param tpf     - time per frame
   */
  public void onAnalog(String binding, float value, float tpf)
  {
    playerNode.onAnalog(binding, value, tpf);
  }

  /**
   * Custom actions for key bindings
   * @param binding   - name of key binding
   * @param isPressed - is the binding active
   * @param tpf       - time per frame
   */
  public void onAction(String binding, boolean isPressed, float tpf)
  {
    playerNode.onAction(binding, isPressed, tpf);
  }

  /**
   * Main event loop where most game actions occur and
   *   non-action related collision detection with player handled
   * @param tpf - timer per frame
   */
  @Override
  public void update(float tpf)
  {
    // Raise Water Level, to be controlled by EEG
    if (!playerNode.isSlowWater()) water.setWaterHeight(water.getWaterHeight() + Globals.WATER_HEIGHT_DEFAULT_RATE);
    else water.setWaterHeight(water.getWaterHeight() + Globals.WATER_HEIGHT_PLAYER_RATE);
    //water.setWaterHeight(water.getWaterHeight() + waterHeightRate);

    // Player died
    if (playerType == 1 && playerNode.getHeight() < .3f && !playerNode.isDead())
    {
      fade.fadeOut();
      playerNode.setDead(true);
    }

    playerNode.update(tpf);
    for (Key k : keys) k.update(tpf);

    if (playerType == 1)
    {
      if ((playerNode.getGeometry().getWorldTranslation().getY() <= water.getWaterHeight()) || playerNode.getShrink())
      {
        playerNode.scalePlayerDown(tpf);
      }

      CollisionResults results = new CollisionResults();
      resources.collideWith(playerNode.getGeometry().getWorldBound(), results);

      if (results.size() > 0)
      {
        audio_collect.play();
        CollisionResult closest = results.getClosestCollision();
        System.out.println("What was hit? " + closest.getGeometry().getName());

        boolean isHit = closest.getGeometry().getUserData("isHit");
        if (!isHit)
        {
          int sResId = closest.getGeometry().getUserData("id");
          closest.getGeometry().setUserData("isHit", true);
          SphereResource s = sphereResourceArrayList.get(sResId);
          s.setShrink(true);
          sphereResourcesToShrink.add(s);
          playerNode.setScaleStartTime(Globals.getTotSecs());
          playerNode.setPlayerNeedsScaling(true);
          if (playerNode.getHeight() < Globals.MAX_PLAYER_SIZE) playerNode.scalePlayerUp();
        }
      }

      CollisionResults keyResults = new CollisionResults();
      keysNode.collideWith(playerNode.getGeometry().getWorldBound(), keyResults);

      if (keyResults.size() > 0)
      {
        CollisionResult closest = keyResults.getClosestCollision();
        System.out.println("What was hit? " + closest.getGeometry().getName());

        int id = closest.getGeometry().getUserData("id");
        for (int i = 0; i < keys.size(); i++)
        {
          if (id == i)
          {
            Key k = keys.get(i);
            k.removeFromParent();
            Door d = doors.get(i);
            d.removeFromParent();
          }
        }
      }
    }


    ArrayList<SphereResource> toRemove = new ArrayList<SphereResource>();
    for (SphereResource s : sphereResourcesToShrink)
    {
      if (s.getShrink()) s.setSphereToDisappear();
      else if (!s.getShrink() && Globals.getTotSecs() - s.getStartShrinkTime() > 30)
      {
        s.getGeometry().setLocalTranslation(new Vector3f(s.getX(), 200, s.getZ()));
        s.setSphereBack();
        resources.attachChild(s.getGeometry());
        toRemove.add(s);
      }
      else
      {
        s.setShrink(false);
        s.getGeometry().setUserData("isHit", false);
        s.getGeometry().removeFromParent();
      }
    }
    for (SphereResource s : toRemove) sphereResourcesToShrink.remove(s);

    //move the audio with the camera
    listener.setLocation(cam.getLocation());
    listener.setRotation(cam.getRotation());
  }

  /** ---Initialization methods--- **/

  private void setUpLight()
  {
    mainLight = new DirectionalLight();
    mainLight.setName("main");
    mainLight.setColor(ColorRGBA.White.clone().multLocal(1.1f));
    mainLight.setDirection(lightDir);
    ambientLight = new AmbientLight();
    //ambientLight.setColor(ColorRGBA.White.mult(1.2f));
    ambientLight.setName("ambient");

    DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
    dlsr.setLight(mainLight);
    viewPort.addProcessor(dlsr);

    sc = new SkyControl(assetManager, cam, 0.9f, true, true);
    sc.getSunAndStars().setHour(12f);
    sc.getSunAndStars().setObserverLatitude(37.4046f * FastMath.DEG_TO_RAD);
    sc.getSunAndStars().setSolarLongitude(Calendar.FEBRUARY, 10);
    sc.setCloudiness(0.3f);
    for (Light light : rootNode.getLocalLightList())
    {
      if (light.getName().equals("ambient")) sc.getUpdater().setAmbientLight((AmbientLight) light);
      else if (light.getName().equals("main")) sc.getUpdater().setMainLight((DirectionalLight) light);
    }

    localRootNode.addLight(mainLight);
    localRootNode.addLight(ambientLight);

    localRootNode.addControl(sc);
    setUpWater();
    BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
    bloom.setBlurScale(2.5f);
    bloom.setExposurePower(1f);
    Misc.getFpp(viewPort, assetManager).addFilter(bloom);
    sc.getUpdater().addBloomFilter(bloom);
  }

  private void setUpPlayer()
  {
    if (playerType == 0)
    {
      playerNode = new OverNode("OverThinker");
      cam.setLocation(new Vector3f(0,250,0));
      cam.lookAtDirection(new Vector3f(0,-1,0), Vector3f.UNIT_Y);
    }
    else
    {
      playerNode = new UnderNode("player", cam, terrain, assetManager, bulletAppState);
      flyCam.setEnabled(false);
    }
    playerNode.setUpPlayer();
    ArrayList<String> actionStrings = playerNode.setUpControls(inputManager);
    for (String s : actionStrings)
    {
      inputManager.addListener(this, s);
    }
    playerNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    collidableNode.attachChild(playerNode);
    bulletAppState.getPhysicsSpace().addAll(playerNode);
  }

  private void setUpLandscape()
  {
    /** Create terrain material and load four textures into it. */
    mat_terrain = new Material(assetManager,
      "Common/MatDefs/Terrain/TerrainLighting.j3md");
    mat_terrain.setBoolean("useTriPlanarMapping", false);
    mat_terrain.setFloat("Shininess", 0.0f);
    mat_terrain.setFloat("Ambient", 10.0f);

    /** Add ALPHA map (for red-blue-green coded splat textures) */
    mat_terrain.setTexture("AlphaMap", assetManager.loadTexture(
      "assets/terrains/tieredmaze1color.png"));
    //mat_terrain.setTexture("AlphaMap_1", assetManager.loadTexture(
    //  "assets/terrains/tieredmaze1color2.png"));

    /** Add GRASS texture into the red layer*/
    Texture grass = assetManager.loadTexture(
      "Textures/Terrain/splat/grass.jpg");
    grass.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap", grass);
    mat_terrain.setFloat("DiffuseMap_0_scale", 64f);

    /** Add DIRT texture into the green layer*/
    Texture dirt = assetManager.loadTexture(
      "Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_1", dirt);
    mat_terrain.setFloat("DiffuseMap_1_scale", 32f);

    /** Add ROAD texture into the blue layer */
    Texture rock = assetManager.loadTexture(
      "Textures/Terrain/splat/road.jpg");
    rock.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_2", rock);
    mat_terrain.setFloat("DiffuseMap_2_scale", 128f);

    /** Add Lava Rocks into alpha layer**/
    Texture lava = assetManager.loadTexture(
          "Textures/lava_texture-sm.jpg");
    lava.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_3", lava);
    mat_terrain.setFloat("DiffuseMap_3_scale", 128f);

    /** Create the height map */
    Texture heightMapImage;

    if (playerType == 0) heightMapImage = assetManager.loadTexture("assets/terrains/tieredmaze1_nowalls.png");
    else heightMapImage = assetManager.loadTexture("assets/terrains/tieredmaze1.png");

    AbstractHeightMap heightMap = null;

    try
    {
      heightMap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.8f);
      heightMap.load();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    int patchSize = 65;
    terrain = new TerrainQuad("my terrain", patchSize, 513, heightMap.getHeightMap());

    /** We give the terrain its material, position & scale it, and attach it. */
    mat_terrain.setReceivesShadows(true);
    mat_terrain.setFloat("Ambient", 5.0f);
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, 0, 0);
    terrain.setLocalScale(2f, 1f, 2f);

    terrain.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

    /** The LOD (level of detail) depends on were the camera is: */
    TerrainLodControl control = new TerrainLodControl(terrain, app.getCamera());
    terrain.addControl(control);

    // We set up collision detection for the scene by creating a
    // compound collision shape and a static RigidBodyControl with mass zero.
    CollisionShape sceneShape =
      CollisionShapeFactory.createMeshShape(terrain);
    landscape = new LandscapeControl(sceneShape, 0, bulletAppState.getPhysicsSpace());
    terrain.addControl(landscape);
    bulletAppState.getPhysicsSpace().add(landscape);
    collidableNode.attachChild(terrain);
  }

  private void setUpWater()
  {
    fpp = new FilterPostProcessor(assetManager);
    water = new WaterFilter(rootNode, mainLight.getDirection());
    water.setWaterHeight(waterHeight);
    water.setDeepWaterColor(new ColorRGBA(0.0f, 0.5f, 0.5f, 1.0f));
    fpp.addFilter(water);
  }

  /**
   * Setter for water height. Water level always increasing.
   * Value comes from EEG to determine just how fast.
   * Range: .001 - .005
   *
   * @param val - water height value from EEG
   */
  public void setWaterHeight (float val)
  {
    waterHeightRate = val;
  }

  public void setPlayerType(int type)
  {
    playerType = type;
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

  private void createDoorsAndKeys()
  {
    for (int i = 0; i < doorLocArray.length; i++)
    {
      Door door = new Door("Door_" + i);
      door.createDoor(assetManager, doorSizeXArray[i], doorRotationArray[i], doorLocArray[i]);
      collidableNode.attachChild(door);
      bulletAppState.getPhysicsSpace().add(door.getPhy());
      doors.add(door);
    }

    for (int i = 0; i < keyLocArray.length; i++)
    {
      Key key = new Key("Key_" + i);
      key.createKey(assetManager, keyLocArray[i]);
      bulletAppState.getPhysicsSpace().add(key.getPhy());
      keys.add(key);
      keysNode.attachChild(key);
    }

    collidableNode.attachChild(keysNode);
  }

  private void initAudio()
  {

    //collect object
    audio_collect = new AudioNode(assetManager, "assets/sounds/collect.ogg",false);
    audio_collect.setPositional(false);
    audio_collect.setVolume(2);
    localRootNode.attachChild(audio_collect);

    if (playerNode.getAudio().size() > 0)
    {
      for (AudioNode a : (ArrayList<AudioNode>) playerNode.getAudio())
      {
        localRootNode.attachChild(a);
      }
    }

    //ambient map sounds
    audio_ocean = new AudioNode(assetManager,"assets/sounds/wavesLoop.ogg",true);
    audio_ocean.setLooping(true);
    audio_ocean.setPositional(true);
    audio_ocean.setVolume(1);
    localRootNode.attachChild(audio_ocean);
    audio_ocean.play();
  }

  @Override
  public void cleanup()
  {
    super.cleanup();
    rootNode.detachChild(localRootNode);
  }
}