package overthinker.client;

import com.jme3.ai.navmesh.NavMesh;
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
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FadeFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
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
import overthinker.net.*;
import overthinker.server.ServerModel;
import overthinker.ai.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Class controls and initializes main client game play logic
 *
 * Created by Torran, Josh, Peter, Derek, Sid
 */

public class GamePlayAppState extends AbstractAppState
      implements ActionListener, AnalogListener
{
  public NavMesh navMesh;
  public BulletAppState bulletAppState;
  private static final int SPHERE_RESOURCE_COUNT = 100;
  private static final float SPHERE_RESOURCE_RADIUS = 1.5f;
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
  private Node platformsNode;
  private Node keysNode;
  private Node collidableNode;
  private Node resources;
  private Node exitNode;
  private Door exitDoor;
  private PlayerNode playerNode;
  private int playerType = 1;
  private int fadeStart = 0;
  private final String levelName = "pentamaze";
  private String lvlNoWallsName = "_height-nowalls.png";
  private String lvlHeightName = "_height.png";
  private String lvlColorName = "_color-noalpha.png";
  private float lvlTime;
  private Vector3f lightDir;
  private ColorRGBA lightIntensity;
  private boolean needsExitDoor = true;
  private AmbientLight ambientLight = null;
  private DirectionalLight mainLight = null;
  private final float waterHeight = 20.0f;
  private float waterHeightRate = 0.00f;
  private LandscapeControl landscape;
  private int playersDead = 0;

  private FadeFilter fade;
  private FilterPostProcessor fpp;
  private WaterFilter water;
  private FogFilter fogFilter;
  private TerrainQuad terrain;
  private Material mat_terrain;

  private final ArrayList<SphereResource> sphereResourceArrayList = new ArrayList<>();
  private final ArrayList<SphereResource> sphereResourcesToShrink = new ArrayList<>();
  private final boolean[] isDead = new boolean[]{false, false, false, false};
  private Vector3f exitLocation;
  private Vector3f[] keyLocArray = null;
  private Vector3f[] keyDoorLocArray = null;
  private Vector3f[] platLocArray = null;
  private Vector3f[] platDoorLocArray = null;
  private float[] keyDoorSizeXArray;
  private float[] keyDoorRotationArray;
  private float[] platDoorSizeXArray;
  private float[] platDoorRotationArray;
  private Vector3f[] playerSpawnPts;
  private final float fogDensity = 0.0f;// 0, 1.0, 2.0
  private final ArrayList<Key> keys = new ArrayList<>();
  private final ArrayList<Door> keyDoors = new ArrayList<>();
  private final ArrayList<Door> platDoors = new ArrayList<>();
  private final ArrayList<Platform> platforms = new ArrayList<>();

  /**
   * Create AudioNodes *
   */
  private AudioNode audio_ocean;
  private AudioNode audio_collect;

  /**
   * Networking *
   */
  private Client netClient;
  private ServerModel model;
  private Vector3f spawnLocation;
  private long activeVersion;
  private int clientIndex;
  private final int playerCount = 4;
  private final HashMap<Integer, OtherPlayer> otherPlayers = new HashMap<>();

  /**
   * Class initialization
   * Initialization order is important
   *
   * @param stateManager - game state manager
   * @param app          - the application
   */
  public void initialize(AppStateManager stateManager, Application app)
  {
    initNetClient();
    stateManager.attach(new AiManager());
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    this.cam = this.app.getCamera();
    this.rootNode = this.app.getRootNode();
    this.assetManager = this.app.getAssetManager();
    this.listener = this.app.getListener();
    viewPort = this.app.getViewPort();
    inputManager = this.app.getInputManager();
    flyCam = this.app.getFlyByCamera();

    /** Set up Physics */
    bulletAppState = new BulletAppState();
    bulletAppState.setDebugEnabled(true);
    stateManager.attach(bulletAppState);
    resources = new Node("Resources");
    localRootNode = new Node("LocalRoot");
    localRootNode.attachChild(resources);
    collidableNode = new Node("CollidableNode");
    keysNode = new Node("KeyNode");
    platformsNode = new Node("PlatformsNode");
    exitNode = new Node("ExitNode");

    flyCam.setMoveSpeed(100);

    setUpLevel();
    createOtherPlayers();
    createDoorsAndKeys();
    createPlatformsAndDoors();
    setUpLandscape();
    setUpLight();
    setUpPlayer();

    fade = new FadeFilter(2);//  2 seconds
    fpp.addFilter(fade);
    fogFilter = new FogFilter();
    fogFilter.setFogDistance(155);
    fogFilter.setFogDensity(fogDensity);
    fpp.addFilter(fogFilter);

    setUpExit();

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
   *
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
   *
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
   * non-action related collision detection with player handled
   *
   * @param tpf - timer per frame
   */
  @Override
  public void update(float tpf)
  {
    //System.out.println(cam.getLocation());

    //Network update
    sendPlayerLocation();
    updatePlayers();
    updateGravity();

    /* Testing */
    //Water level from stress
    //TODO: water height rate from excitement
    //water.setWaterHeight(water.getWaterHeight() + model.getWaterRate());
    if(model.getPlayerLocations().get(3) != null) {
      water.setWaterHeight(water.getWaterHeight() + model.getWaterRate());
    }

    if (playersDead == 1) fogFilter.setFogDensity(1.0f);
    if (playersDead == 2) fogFilter.setFogDensity(2);

    if (playerNode.isDead() && Globals.getTotSecs() - fadeStart > fade.getDuration())
    {
      fade.fadeIn();
      if (fade.getValue() == 0)
      {
        cam.setLocation(new Vector3f(exitLocation.getX(), exitLocation.getY() + 150, exitLocation.getZ()));
        cam.lookAtDirection(new Vector3f(0, -1, 0), new Vector3f(0, -1, 0));
        flyCam.setEnabled(true);
        flyCam.setMoveSpeed(0);
      }
    }

    if (playerType == 1 && playerNode.getHeight() < .3f && !playerNode.isDead())
    {
      netClient.send(new PlayerDeathRequest());
      fade.fadeOut();
      playerNode.setDead(true);
      bulletAppState.getPhysicsSpace().remove(playerNode);
      playerNode.removeFromParent();
      fadeStart = Globals.getTotSecs();
      UnderNode.stopWarningSound();
    }

    playerNode.update(tpf);
    for (Key k : keys)
    {
      k.update(tpf);
    }
    if (playerType == 1)
    {
      testCollisions(tpf);
    }

    //move the audio with the camera
    listener.setLocation(cam.getLocation());
    listener.setRotation(cam.getRotation());
  }

  /**
   * Setter for water height. Water level always increasing.
   * Value comes from EEG to determine just how fast.
   * Range: .001 - .005
   *
   * @param val - water height value from EEG
   */
  public void setWaterHeight(float val)
  {
    waterHeightRate = val;
  }

  /**
   * Sets the player type
   * 0 = overthinker, 1 = underthinker
   *
   * @param type - player type
   */
  public void setPlayerType(int type)
  {
    playerType = type;
  }

  /**
   * Set up level variables depending on level name
   */
  void setUpLevel()
  {
    lvlColorName = "overthinker/assets/terrains/" + levelName + lvlColorName;
    lvlHeightName = "overthinker/assets/terrains/" + levelName + lvlHeightName;
    lvlNoWallsName = "overthinker/assets/terrains/" + levelName + lvlNoWallsName;

    switch (levelName)
    {
      case "circlemaze":
        exitLocation = new Vector3f(7, 122, -7);
        playerSpawnPts = new Vector3f[]{new Vector3f(-344, 80, -380), new Vector3f(289, 80, 414.7f), new Vector3f(381,80, -387.5f)};
        keyLocArray = new Vector3f[]{
              new Vector3f(60, 65, -330), new Vector3f(35, 65, 345), new Vector3f(115, 65, -353)
        };
        keyDoorLocArray = new Vector3f[]{
              new Vector3f(-293, 98, 143), new Vector3f(330, 98, -68), new Vector3f(-236, 98, 208)
        };
        platLocArray = new Vector3f[]{
              new Vector3f(55, 81.65f, -295), new Vector3f(245, 81.65f, 120), new Vector3f(195, 102.45f, 75)
        };
        platDoorLocArray = new Vector3f[]{
              new Vector3f(252, 133, -87), new Vector3f(202, 133, 139), new Vector3f(67, 133, -245)
        };
        keyDoorSizeXArray = new float[]{15f, 15f, 17f};
        keyDoorRotationArray = new float[]{-55f, 100f, -43f};
        platDoorSizeXArray = new float[]{16f, 17f, 21f};
        platDoorRotationArray = new float[]{-70, 49, -10};
        lvlTime = 12;
        lightDir = new Vector3f(4.1f, -3.2f, 0.1f);
        lightIntensity = ColorRGBA.White.clone().multLocal(1.1f);
        break;
      case "pentamaze":
        exitLocation = new Vector3f(-5.5f, 102, -6);
        playerSpawnPts = new Vector3f[]{new Vector3f(-183, 80, -431), new Vector3f(360, 59, 450), new Vector3f(345,80,-284)};
        keyLocArray = new Vector3f[]{
              new Vector3f(-298.9f, 45, 360), new Vector3f(408.7f, 45, 15), new Vector3f(80, 45, -421)
        };
        keyDoorLocArray = new Vector3f[]{
              new Vector3f(-238f, 100, -224.9f), new Vector3f(369, 100, 56.2f), new Vector3f(98, 100, -333.57f)
        };
        platLocArray = new Vector3f[]{
              new Vector3f(-84, 81.65f, 273), new Vector3f(201, 81.65f, 122), new Vector3f(203, 102.45f, -18)
        };
        platDoorLocArray = new Vector3f[]{
              new Vector3f(-156, 134, 188), new Vector3f(-50, 134, 247), new Vector3f(201, 134, 26)
        };
        keyDoorSizeXArray = new float[]{18f, 15f, 17f};
        keyDoorRotationArray = new float[]{32, 68, -34};
        platDoorSizeXArray = new float[]{16f, 16f, 17};
        platDoorRotationArray = new float[]{-78, 0, 70};
        lvlTime = 0;
        lightDir = new Vector3f(.5f, -1, 0);
        lightIntensity = ColorRGBA.White.clone().multLocal(.5f);
        break;
      case "radiomaze":
        exitLocation = new Vector3f(.36f, 80.3f, -30);
        playerSpawnPts = new Vector3f[]{new Vector3f(-260, 110, -390), new Vector3f(-170, 110, 350), new Vector3f(260, 110, -390)};
        platDoorLocArray = new Vector3f[]{
              new Vector3f(-123, 124.8f, -216), new Vector3f(-89, 124.8f, 167), new Vector3f(125, 124.8f, -214)
        };
        platDoorSizeXArray = new float[]{16f, 18f, 16};
        platDoorRotationArray = new float[]{36, -18, -30};
        lvlTime = 15;
        lightDir = new Vector3f(6.3f, -2.0f, 6.9f);
        lightIntensity = ColorRGBA.White.clone().multLocal(1.3f);
        waterHeightRate = .003f;
        needsExitDoor = false;
        break;
    }
  }

  /**
   * Override app state cleanup to clear after detaching
   */
  @Override
  public void cleanup()
  {
    super.cleanup();
    rootNode.detachChild(localRootNode);
    System.exit(0);
  }

  /**
   * Net client getter
   * @return the net client
   */
  public Client getNetClient()
  {
    return netClient;
  }

  /**
   * Connect new client to server
   * @param message - message from server
   */
  public void handleNewClientResponse(NewClientResponse message)
  {
    System.out.println("Connected to server");
    model = new ServerModel();
    model.setPlayerLocations(message.getPlayerLocations());
    model.setVersion(message.getVersion());
    spawnLocation = message.getSpawnLocation();
    clientIndex = message.getClientIndex();
  }

  /**
   * Updates model instance with information from server
   * @param message - message from server
   */
  public void updateModel(ModelUpdate message)
  {
    if (model != null)
    {
      model.setPlayerLocations(message.getPlayerLocations());
      model.setPlayerAlive(message.getPlayerAlive());
      model.setGravityRight(message.isGravityRight());
      model.setGravityLeft(message.isGravityLeft());
      model.setGravityForward(message.isGravityForward());
      model.setGravityBack(message.isGravityBack());
      model.setWaterRate(message.getWaterRate());
      model.setVersion(message.version);
    }
  }

  /**
   * Gett for localRootNode
   * @return localRootNode
   */
  public Node getLocalRootNode()
  {
    return localRootNode;
  }

  /**
   * Getter for player node
   * @return playerNode
   */
  public PlayerNode getPlayerNode()
  {
    return playerNode;
  }

  private void createOtherPlayers()
  {
    for (int i = 0; i < playerCount; i++)
    {
      if (i != 3)
      {
        System.out.println("Creating new Player Objects");
        OtherPlayer otherPlayer = new OtherPlayer(Globals.PLAYER_SPHERE_START_RADIUS, i,
                spawnLocation, assetManager);
        bulletAppState.getPhysicsSpace().add(otherPlayer.getSphereResourcePhy());
        otherPlayers.put(i, otherPlayer);
        resources.attachChild(otherPlayer.getGeometry());
        localRootNode.attachChild(otherPlayer.getGeometry());
      }
    }
  }

  private void initNetClient()
  {
    try
    {
      netClient = Network.connectToServer("localhost", 6143);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    ClientNetListener listener = new ClientNetListener(this);

    Serializer.registerClass(ChangePlayerLocationRequest.class);
    Serializer.registerClass(ChangeMapTiltRequest.class);
    Serializer.registerClass(ChangeWaterRateRequest.class);
    Serializer.registerClass(PlayerDeathRequest.class);
    Serializer.registerClass(ModelUpdate.class);
    Serializer.registerClass(NewClientRequest.class);
    Serializer.registerClass(NewClientResponse.class);

    netClient.addMessageListener(listener, ModelUpdate.class);
    netClient.addMessageListener(listener, NewClientResponse.class);

    netClient.start();

    while (model == null)
    {
      NewClientRequest newClientRequest = new NewClientRequest();
      if(playerType == 0) newClientRequest.setEEG(true);
      netClient.send(newClientRequest);
      System.out.println("Waiting For Model Data...");
      try
      {
        Thread.sleep(1000);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }
  }

  private void testCollisions(float tpf)
  {
    //Scale player down if at same point as water level
    if ((playerNode.getGeometry().getWorldTranslation().getY() <= water.getWaterHeight()) || playerNode.getShrink())
    {
      playerNode.scalePlayerDown(tpf);
    }

    //Detect collisions with resource spheres
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
        if (playerNode.getHeight() < Globals.MAX_PLAYER_SIZE)
        {
          playerNode.scalePlayerUp();
        }
      }
    }

    //Detect collisions with keys
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
          Door d = keyDoors.get(i);
          d.removeFromParent();
        }
      }
    }

    //Detect collisions with platforms
    CollisionResults platResults = new CollisionResults();
    platformsNode.collideWith(playerNode.getGeometry().getWorldBound(), platResults);
    for (Platform p : platforms)
    {
      p.moveUp();
    }
    if (platResults.size() > 0)
    {
      boolean onePressed = false;
      boolean twoPressed = false;
      boolean threePressed = false;
      for (int i = 0; i < platResults.size(); i++)
      {
        int id = platResults.getCollision(i).getGeometry().getUserData("id");
        Platform p = platforms.get(id);
        p.pressDown();
        if (id == 0)
        {
          onePressed = true;
        }
        if (id == 1)
        {
          twoPressed = true;
        }
        if (id == 2)
        {
          threePressed = true;
        }
      }

      if (onePressed && twoPressed)
      {
        Door d = platDoors.get(0);
        d.removeFromParent();
      }
      else if (threePressed)
      {
        Door d2 = platDoors.get(1);
        Door d3 = platDoors.get(2);
        d2.removeFromParent();
        d3.removeFromParent();
        exitDoor.removeFromParent();
        localRootNode.attachChild(exitNode);
      }
    }

    //Detect collisions with exit
    CollisionResults exitResults = new CollisionResults();
    exitNode.collideWith(playerNode.getGeometry().getWorldBound(), exitResults);
    if (localRootNode.hasChild(exitNode) && exitResults.size() > 0)
    {
      fade.fadeOut();
      bulletAppState.getPhysicsSpace().remove(playerNode);
      playerNode.removeFromParent();

    }

    ArrayList<SphereResource> toRemove = new ArrayList<>();
    for (SphereResource s : sphereResourcesToShrink)
    {
      if (s.getShrink())
      {
        s.setSphereToDisappear();
      }
      else if (Globals.getTotSecs() - s.getStartShrinkTime() > 30)
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
    toRemove.forEach(sphereResourcesToShrink::remove);
  }

  private void updateGravity()
  {
    if (playerType == 1)
    {
      playerNode.getBCCControl().setGravityBack(model.isGravityBack());
      playerNode.getBCCControl().setGravityForward(model.isGravityForward());
      playerNode.getBCCControl().setGravityLeft(model.isGravityLeft());
      playerNode.getBCCControl().setGravityRight(model.isGravityRight());

      if (!model.isGravityBack() && !model.isGravityForward() && !model.isGravityRight() && !model.isGravityLeft())
      {

      }
    }
  }

  private void updatePlayers()
  {
    if (activeVersion < model.getVersion())
    {
      for (int i = 0; i < playerCount; i++)
      {
        if (i != clientIndex && i != 3)
        {
          if (model.getPlayerLocations().get(i) != null)
          {
            otherPlayers.get(i).move(model.getPlayerLocations().get(i));
          }
          if (model.getPlayerAlive().get(i) != null && !model.getPlayerAlive().get(i))
          {
            if (!isDead[i])
            {
              isDead[i] = true;
              playersDead++;
            }
            otherPlayers.get(i).getGeometry().removeFromParent();
          }
        }
      }
      activeVersion = model.getVersion();
    }
  }

  private void sendPlayerLocation()
  {
    ChangePlayerLocationRequest playerLocationChangeRequest = new ChangePlayerLocationRequest();
    playerLocationChangeRequest.setPlayerLocation(playerNode.getLocalTranslation());
    netClient.send(playerLocationChangeRequest);
  }

  /**
   * ---Initialization methods--- *
   */
  private void setUpLight()
  {
    mainLight = new DirectionalLight();
    mainLight.setName("main");
    mainLight.setColor(lightIntensity);
    mainLight.setDirection(lightDir);
    ambientLight = new AmbientLight();
    ambientLight.setColor(ColorRGBA.White.mult(1.2f));
    ambientLight.setName("ambient");

    DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
    dlsr.setLight(mainLight);
    viewPort.addProcessor(dlsr);

    sc = new SkyControl(assetManager, cam, 0.9f, true, true);
    sc.getSunAndStars().setHour(lvlTime);
    sc.getSunAndStars().setObserverLatitude(37.4046f * FastMath.DEG_TO_RAD);
    sc.getSunAndStars().setSolarLongitude(Calendar.FEBRUARY, 10);
    sc.setCloudiness(0.3f);
    for (Light light : rootNode.getLocalLightList())
    {
      if (light.getName().equals("ambient"))
      {
        sc.getUpdater().setAmbientLight((AmbientLight) light);
      }
      else if (light.getName().equals("main"))
      {
        sc.getUpdater().setMainLight((DirectionalLight) light);
      }
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
      playerNode = new OverNode("OverThinker", netClient);
      cam.setLocation(new Vector3f(0, 250, 0));
      cam.lookAtDirection(new Vector3f(0, -1, 0), Vector3f.UNIT_Y);
    }
    else
    {
      playerNode = new UnderNode("player", cam, terrain, assetManager, bulletAppState, collidableNode, clientIndex);
      playerNode.setLocalTranslation(playerSpawnPts[clientIndex]);
      flyCam.setEnabled(false);
      createSphereResources();
    }
    playerNode.setUpPlayer();
    ArrayList<String> actionStrings = playerNode.setUpControls(inputManager);
    for (String s : actionStrings)
    {
      inputManager.addListener(this, s);
    }
    playerNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    localRootNode.attachChild(playerNode);
    bulletAppState.getPhysicsSpace().addAll(playerNode);
  }

  private void setUpExit()
  {
    Sphere exitS = new Sphere(32, 32, 12);
    Geometry geoS = new Geometry("ExitSphere", exitS);

    Material exitMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    exitMat.setColor("Color", ColorRGBA.Orange);
    exitMat.setColor("GlowColor", ColorRGBA.Orange);
    geoS.setMaterial(exitMat);
    geoS.setLocalTranslation(exitLocation.getX(), exitLocation.getY() - 5, exitLocation.getZ());
    geoS.setShadowMode(RenderQueue.ShadowMode.Cast);

    Material sparkMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    sparkMat.setTexture("Texture", assetManager.loadTexture("overthinker/assets/effects/flash.png"));
    ParticleEmitter flashEmitter = new ParticleEmitter("flash emitter", ParticleMesh.Type.Triangle, 100);
    flashEmitter.setLocalTranslation(new Vector3f(exitLocation.getX(), exitLocation.getY(), exitLocation.getZ()));
    flashEmitter.setMaterial(sparkMat);
    flashEmitter.setImagesX(2);
    flashEmitter.setImagesY(2);
    flashEmitter.setStartColor(ColorRGBA.Orange);
    flashEmitter.setEndColor(ColorRGBA.Yellow);
    flashEmitter.setFacingVelocity(true);
    flashEmitter.setStartSize(.5f);
    flashEmitter.setEndSize(.5f);
    flashEmitter.setLowLife(1.9f);
    flashEmitter.setHighLife(2.1f);
    flashEmitter.setRotateSpeed(4);
    flashEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 20, 0));
    flashEmitter.setSelectRandomImage(true);
    flashEmitter.setRandomAngle(true);
    flashEmitter.getParticleInfluencer().setVelocityVariation(1.0f);
    exitNode.attachChild(flashEmitter);

    BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
    bloom.setBlurScale(2.5f);
    bloom.setExposurePower(1f);
    fpp.addFilter(bloom);
    exitNode.attachChild(geoS);

    if (needsExitDoor)
    {
      exitDoor = new Door("ExitDoor");
      exitDoor.createDoor(assetManager, 15, 1, 15, 90, exitLocation);
      collidableNode.attachChild(exitDoor);
      bulletAppState.getPhysicsSpace().add(exitDoor.getPhy());
    }
    else
    {
      localRootNode.attachChild(exitNode);
    }
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
          lvlColorName));

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
          "overthinker/assets/textures/lava_texture-sm.jpg");
    lava.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_3", lava);
    mat_terrain.setFloat("DiffuseMap_3_scale", 128f);


    Node terrain1 = (Node) assetManager.loadModel("overthinker/assets/terrains/tieredmaze.j3o");
    terrain1.setLocalTranslation(0, 0, 0);
    Geometry navGeom = (Geometry) terrain1.getChild("NavMesh");
    navGeom.setMaterial(new Material(assetManager, "/Common/MatDefs/Misc/Unshaded.j3md"));
    navGeom.setLocalTranslation(0, 1, 0);
    localRootNode.attachChild(navGeom);
    navMesh = new NavMesh(navGeom.getMesh());

    /** Create the height map */
    Texture heightMapImage;

    if (playerType == 0)
    {
      heightMapImage = assetManager.loadTexture(lvlNoWallsName);
    }
    else
    {
      heightMapImage = assetManager.loadTexture(lvlHeightName);
    }


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
    assert heightMap != null;
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

     //We set up collision detection for the scene by creating a
     //compound collision shape and a static RigidBodyControl with mass zero.
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
    if (keyDoorLocArray != null)
    {
      for (int i = 0; i < keyDoorLocArray.length; i++)
      {
        Door door = new Door("Door_" + i);
        door.createDoor(assetManager, keyDoorSizeXArray[i], 40, 1, keyDoorRotationArray[i], keyDoorLocArray[i]);
        collidableNode.attachChild(door);
        bulletAppState.getPhysicsSpace().add(door.getPhy());
        keyDoors.add(door);

        Key key = new Key("Key_" + i);
        key.createKey(assetManager, keyLocArray[i]);
        bulletAppState.getPhysicsSpace().add(key.getPhy());
        keys.add(key);
        keysNode.attachChild(key);
      }
      collidableNode.attachChild(keysNode);
    }
  }

  private void createPlatformsAndDoors()
  {
    if (platLocArray != null)
    {
      for (int i = 0; i < platLocArray.length; i++)
      {
        Platform plat = new Platform("Platform_" + i);
        plat.createPlatform(assetManager, platLocArray[i], 2);
        platformsNode.attachChild(plat);
        bulletAppState.getPhysicsSpace().add(plat.getPhy());
        platforms.add(plat);
      }
      collidableNode.attachChild(platformsNode);
    }

    if (platDoorLocArray != null)
    {
      for (int i = 0; i < platDoorLocArray.length; i++)
      {
        Door door = new Door("Door_" + i);
        door.createDoor(assetManager, platDoorSizeXArray[i], 40, 1, platDoorRotationArray[i], platDoorLocArray[i]);
        collidableNode.attachChild(door);
        bulletAppState.getPhysicsSpace().add(door.getPhy());
        platDoors.add(door);
      }
    }
  }

  private void initAudio()
  {

    //collect object
    audio_collect = new AudioNode(assetManager, "overthinker/assets/sounds/collect.ogg", false);
    audio_collect.setPositional(false);
    audio_collect.setVolume(2);
    localRootNode.attachChild(audio_collect);

    if (playerNode.getAudio().size() > 0)
    {
      ((ArrayList<AudioNode>) playerNode.getAudio()).forEach(localRootNode::attachChild);
    }

    //ambient map sounds
    audio_ocean = new AudioNode(assetManager, "overthinker/assets/sounds/wavesLoop.ogg", false);
    audio_ocean.setLooping(true);
    audio_ocean.setPositional(true);
    audio_ocean.setVolume(1);
    localRootNode.attachChild(audio_ocean);
    audio_ocean.play();
  }
}