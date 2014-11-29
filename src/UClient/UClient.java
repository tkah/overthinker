package UClient; /**
 * Created by Torran on 11/9/14.
 */

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
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
import com.jme3.light.Light;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FadeFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.WaterFilter;
import jme3utilities.Misc;
import jme3utilities.sky.SkyControl;

import com.jme3.scene.shape.Line;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.ArrayList;

public class UClient extends SimpleApplication
  implements ActionListener, AnalogListener
{
  private static final int SPHERE_RESOURCE_COUNT = 250;
  private static final float SPHERE_RESOURCE_RADIUS = 1.0f;

  private Vector3f lightDir = new Vector3f(4.1f, -3.2f, 0.1f);
  private AmbientLight ambientLight = null;
  private DirectionalLight mainLight = null;
  private LightScatteringFilter sunLightFilter;

  private Node lightNode;
  private Node collidableNode;
  private Node resources;
  private PlayerNode playerNode;
  private Node lvl5Node;
  private int playerType = 1;

  private float waterHeight = 20.0f;
  private float waterHeightRate = 0.005f;

  private BulletAppState bulletAppState;
  private LandscapeControl landscape;

  private FadeFilter fade;
  private FilterPostProcessor fpp;
  private WaterFilter water;
  private TerrainQuad terrain;
  private Material mat_terrain;

  private ArrayList<SphereResource> sphereResourceArrayList = new ArrayList<SphereResource>();
  private ArrayList<SphereResource> sphereResourcesToShrink = new ArrayList<SphereResource>();


  /** Server Communcation - Not yet implemented **/
  private Vector3f myLoc = new Vector3f(); // Might replace with 'walkDirection' from above
  private ArrayList<Vector3f> playerLocs = new ArrayList<Vector3f>();

  /** Create AudioNodes **/
  private AudioNode audio_ocean;
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

    lvl5Node = new Node ("SceneNode");
    collidableNode = new Node ("CollidableNode");
    lightNode = new Node("LightNode");
    //setUpLight();

    flyCam.setMoveSpeed(100);
    mouseInput.setCursorVisible(false);
    createSphereResources();

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

    SkyControl sc = new SkyControl(assetManager, cam, 0.9f, true, true);
    sc.getSunAndStars().setHour(12f);
    sc.getSunAndStars().setObserverLatitude(37.4046f * FastMath.DEG_TO_RAD);
    sc.getSunAndStars().setSolarLongitude(Calendar.FEBRUARY, 10);
    sc.setCloudiness(0.3f);
    for (Light light : rootNode.getLocalLightList())
    {
      if (light.getName().equals("ambient")) sc.getUpdater().setAmbientLight((AmbientLight) light);
      else if (light.getName().equals("main")) sc.getUpdater().setMainLight((DirectionalLight) light);
    }

    //lightNode = new LightNode(mainLight);
    rootNode.addLight(mainLight);
    rootNode.addLight(ambientLight);

    rootNode.addControl(sc);
    setUpWater();
    // Sunray effect great looking, but doesn't shut off at the right time
    sunLightFilter = new LightScatteringFilter(lightDir.mult(-3000));
    //fpp.addFilter(sunLightFilter);
    BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
    bloom.setBlurScale(2.5f);
    bloom.setExposurePower(1f);
    Misc.getFpp(viewPort, assetManager).addFilter(bloom);
    sc.getUpdater().addBloomFilter(bloom);

    setUpLandscape();

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

    fade = new FadeFilter(2); // e.g. 2 seconds
    fpp.addFilter(fade);

    viewPort.addProcessor(fpp);
    rootNode.attachChild(collidableNode);

    sc.setEnabled(true);

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
  public void simpleUpdate(float tpf)
  {
    // Raise Water Level, to be controlled by EEG
    if (!playerNode.isSlowWater()) water.setWaterHeight(water.getWaterHeight() + Globals.WATER_HEIGHT_DEFAULT_RATE);
    else water.setWaterHeight(water.getWaterHeight() + Globals.WATER_HEIGHT_PLAYER_RATE);
    //water.setWaterHeight(water.getWaterHeight() + waterHeightRate);

    // Player died
    if (playerType == 1 && playerNode.getHeight() < .1f && !playerNode.isDead())
    {
      fade.fadeOut();
      playerNode.setDead(true);
    }

    playerNode.update(tpf);

    if (playerType == 1)
    {
      if ((playerNode.getGeometry().getWorldTranslation().getY() <= water.getWaterHeight()) || playerNode.getShrink())
      {
        playerNode.scalePlayerDown();
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
      "assets/textures/lava_texture-sm.jpg");
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
      //heightMap.smooth(0.9f, 1);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    // Height Map Randomization
        /*HillHeightMap heightmap = null;
        HillHeightMap.NORMALIZE_RANGE = 100; // optional
        try {
            heightmap = new HillHeightMap(513, 1000, 5, 1000, (byte) 3); // byte 3 is a random seed
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

    /** We have prepared material and heightmap.
     * Now we create the actual terrain:
     * -Create a TerrainQuad and name it "my terrain".
     * -A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
     * -We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
     * -As LOD step scale we supply Vector3f(1,1,1).
     * -We supply the prepared heightmap itself.
     */
    int patchSize = 65;
    terrain = new TerrainQuad("my terrain", patchSize, 513, heightMap.getHeightMap());

    /** We give the terrain its material, position & scale it, and attach it. */
    mat_terrain.setReceivesShadows(true);
    mat_terrain.setFloat("Ambient", 5.0f);
    terrain.setMaterial(mat_terrain);
    terrain.setLocalTranslation(0, 0, 0);
    terrain.setLocalScale(2f, 1f, 2f);

    terrain.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    //lvl5Node.attachChild(terrain);
    //rootNode.attachChild(lvl5Node);

    /** The LOD (level of detail) depends on were the camera is: */
    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
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

  private void setUpLight()
  {
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.3f));
    rootNode.addLight(al);

    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    //dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
    //dl.setDirection(lightDir.normalizeLocal());
    rootNode.addLight(dl);
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


  private void initAudio()
  {

    //collect object
    audio_collect = new AudioNode(assetManager, "assets/sounds/collect.ogg",false);
    audio_collect.setPositional(false);
    audio_collect.setVolume(2);
    rootNode.attachChild(audio_collect);

    if (playerNode.getAudio().size() > 0)
    {
      for (AudioNode a : (ArrayList<AudioNode>) playerNode.getAudio())
      {
        rootNode.attachChild(a);
      }
    }

    //ambient map sounds
    audio_ocean = new AudioNode(assetManager,"assets/sounds/wavesLoop.ogg",true);
    audio_ocean.setLooping(true);
    audio_ocean.setPositional(true);
    audio_ocean.setVolume(1);
    rootNode.attachChild(audio_ocean);
    audio_ocean.play();
  }
}