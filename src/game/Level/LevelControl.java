package game.Level;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import overthinker.client.Door;
import overthinker.client.Key;
import overthinker.client.Platform;
import overthinker.client.SphereResource;

import java.util.ArrayList;

/**
 * Created by jdriden on 12/4/2014.
 */
public class LevelControl extends AbstractAppState
{
  private ArrayList<SphereResource> sphereResourceArrayList = new ArrayList<SphereResource>();
  private ArrayList<SphereResource> sphereResourcesToShrink = new ArrayList<SphereResource>();
  private Vector3f exitLocation = new Vector3f(7,122,-7);
  private Vector3f[] keyLocArray = {new Vector3f(60, 65, -330), new Vector3f(35, 65, 345), new Vector3f(115, 65, -353)};
  private Vector3f[] keyDoorLocArray = {new Vector3f(-293, 98, 143), new Vector3f(330, 98, -68), new Vector3f(-236, 98, 208)};
  private Vector3f[] platLocArray = {new Vector3f(55, 81.65f, -295), new Vector3f(245, 81.65f, 120), new Vector3f(195, 102.45f, 75)};
  private Vector3f[] platDoorLocArray = {new Vector3f(252, 133, -87), new Vector3f(202,133,139), new Vector3f(67,133,-245)};
  private float[] keyDoorSizeXArray = {15f, 15f, 17f};
  private float[] keyDoorRotationArray = {-55f, 100f, -43f};
  private float[] platDoorSizeXArray = {16f, 17f, 21f};
  private float[] platDoorRotationArray = {-70, 49, -10};
  private float fogDensity = 2.0f; //0, 1.0, 1.5, 2.0
  private ArrayList<Key> keys = new ArrayList<Key>();
  private ArrayList<Door> keyDoors = new ArrayList<Door>();
  private ArrayList<Door> platDoors = new ArrayList<Door>();
  private ArrayList<Platform> platforms = new ArrayList<Platform>();

  /** Create AudioNodes **/
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

  @Override
  public void initialize(AppStateManager stateManager, Application app)
  {
    super.initialize(stateManager, app);
    this.assetManager = app.getAssetManager();
    this.worldRoot = stateManager.getState(LevelManager.class).getWorldNode();
    this.bulletAppState = stateManager.getState(LevelManager.class).getPhysics();
    setUpNodes();
    createDoorsAndKeys();
    createPlatformsAndDoors();
    setUpAudio();
  }

  private void setUpAudio()
  {
    //ambient map sounds
    audio_ocean = new AudioNode(assetManager,"assets/sounds/wavesLoop.ogg",true);
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
  }

  private void createPlatformsAndDoors()
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

  private void createDoorsAndKeys()
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
