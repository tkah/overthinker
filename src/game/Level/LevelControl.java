package game.Level;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FadeFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import overthinker.client.*;

import java.util.ArrayList;

/**
 * Created by jdriden on 12/4/2014.
 */
public class LevelControl extends AbstractAppState
{
  private static final int SPHERE_RESOURCE_COUNT = 250;
  private static final float SPHERE_RESOURCE_RADIUS = 1f;
  private ArrayList<SphereResource> sphereResourceArrayList = new ArrayList<SphereResource>();
  private ArrayList<SphereResource> sphereResourcesToShrink = new ArrayList<SphereResource>();
  private Vector3f exitLocation = new Vector3f(7, 122, -7);
  private Vector3f[] keyLocArray = {new Vector3f(60, 65, -330), new Vector3f(35, 65, 345), new Vector3f(115, 65, -353)};
  private Vector3f[] keyDoorLocArray = {
        new Vector3f(-293, 98, 143), new Vector3f(330, 98, -68), new Vector3f(-236, 98, 208)
  };
  private Vector3f[] platLocArray = {
        new Vector3f(55, 81.65f, -295), new Vector3f(245, 81.65f, 120), new Vector3f(195, 102.45f, 75)
  };
  private Vector3f[] platDoorLocArray = {
        new Vector3f(252, 133, -87), new Vector3f(202, 133, 139), new Vector3f(67, 133, -245)
  };
  private float[] keyDoorSizeXArray = {15f, 15f, 17f};
  private float[] keyDoorRotationArray = {-55f, 100f, -43f};
  private float[] platDoorSizeXArray = {16f, 17f, 21f};
  private float[] platDoorRotationArray = {-70, 49, -10};
  private float fogDensity = 2.0f; //0, 1.0, 1.5, 2.0
  private ArrayList<Key> keys = new ArrayList<Key>();
  private ArrayList<Door> keyDoors = new ArrayList<Door>();
  private ArrayList<Door> platDoors = new ArrayList<Door>();
  private ArrayList<Platform> platforms = new ArrayList<Platform>();

  /**
   * Create AudioNodes *
   */
  private AudioNode audio_ocean;
  private AudioNode audio_collect;

  private Node worldRoot;
  private Node platformsNode;
  private Node keysNode;
  private Node collidableNode;
  private Node resources;
  private Node exitNode;
  private Door exitDoor;

  private AssetManager assetManager;
  private BulletAppState bulletAppState;
  private FilterPostProcessor fpp;
  private FadeFilter fade;
  private FogFilter fogFilter;


  @Override
  public void initialize(AppStateManager stateManager, Application app)
  {
    super.initialize(stateManager, app);
    this.assetManager = app.getAssetManager();
    this.worldRoot = stateManager.getState(LevelManager.class).getWorldNode();
    this.bulletAppState = stateManager.getState(LevelManager.class).getPhysics();
    this.fpp = stateManager.getState(LevelManager.class).getFpp();
    setUpNodes();
    setUpSphereResources();
    setUpDoorsAndKeys();
    setUpPlatformsAndDoors();
    setUpAudio();
    setUpFog();
    setUpExit();
  }


  private void setUpExit()
  {
    exitDoor = new Door("ExitDoor");
    exitDoor.createDoor(assetManager, 15, 1, 15, 90, exitLocation);
    collidableNode.attachChild(exitDoor);
    bulletAppState.getPhysicsSpace().add(exitDoor.getPhy());

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
    flashEmitter.setStartColor(ColorRGBA.Yellow);
    flashEmitter.setEndColor(ColorRGBA.White);
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
  }

  private void setUpSphereResources()
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


  private void setUpFog()
  {
    fade = new FadeFilter(2);
    fpp.addFilter(fade);
    fogFilter = new FogFilter();
    fogFilter.setFogDensity(155);
    fogFilter.setFogDensity(fogDensity);
    fpp.addFilter(fogFilter);
  }


  private void setUpAudio()
  {
    //ambient map sounds
    audio_ocean = new AudioNode(assetManager, "assets/sounds/wavesLoop.ogg", true);
    audio_ocean.setLooping(true);
    audio_ocean.setPositional(true);
    audio_ocean.setVolume(1);
    worldRoot.attachChild(audio_ocean);
    audio_ocean.play();
  }

  private void setUpNodes()
  {
    resources = new Node("Resources");
    collidableNode = new Node("CollidableNode");
    keysNode = new Node("KeyNode");
    platformsNode = new Node("PlatformsNode");
    exitNode = new Node("ExitNode");
    worldRoot.attachChild(resources);
    worldRoot.attachChild(collidableNode);
    worldRoot.attachChild(keysNode);
    worldRoot.attachChild(platformsNode);
    worldRoot.attachChild(exitNode);
  }


  private void setUpPlatformsAndDoors()
  {
    for (int i = 0; i < platLocArray.length; i++)
    {
      Platform plat = new Platform("Platform_" + i);
      plat.createPlatform(assetManager, platLocArray[i], 2);
      platformsNode.attachChild(plat);
      bulletAppState.getPhysicsSpace().add(plat.getPhy());
      platforms.add(plat);
      Door door = new Door("Door_" + i);
      door.createDoor(assetManager, platDoorSizeXArray[i], 40, 1, platDoorRotationArray[i], platDoorLocArray[i]);
      collidableNode.attachChild(door);
      bulletAppState.getPhysicsSpace().add(door.getPhy());
      platDoors.add(door);
    }
    collidableNode.attachChild(platformsNode);
  }


  private void setUpDoorsAndKeys()
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
