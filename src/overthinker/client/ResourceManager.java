package overthinker.client;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;

import java.util.Arrays;

/**
 * Created by jdrid_000 on 12/8/2014.
 */
public class ResourceManager extends AbstractAppState
{
  private AssetManager assetManager;
  private Resource[] resources;
  private static int id = 0;
  private Material mat;
  private Node worldNode;
  private Node resourceNode;
  private Terrain terrain;
  private PlayerNode playerNode;
  private AudioNode audio_collect;
  private float time;

  @Override
  public void initialize(AppStateManager stateManager, Application app)
  {
    super.initialize(stateManager, app);
    this.worldNode = stateManager.getState(GamePlayAppState.class).getLocalRootNode();
    this.terrain = stateManager.getState(GamePlayAppState.class).terrain;
    this.playerNode = stateManager.getState(GamePlayAppState.class).getPlayerNode();
    this.assetManager = app.getAssetManager();
    this.resources = new Resource[Globals.SPHERE_RESOURCE_COUNT];
    this.resourceNode = new Node();
    this.audio_collect = new AudioNode(assetManager, "overthinker/assets/sounds/collect.ogg", false);
    this.audio_collect.setPositional(false);
    this.audio_collect.setVolume(2);
    setUpMaterial();
    createResources();
  }

  /**
   * Creates a random distribution of resources around the map.
   */
  public void createResources()
  {
    for (int i = 0; i < Globals.SPHERE_RESOURCE_COUNT; i++)
    {
      int x = randomRange(Globals.MAP_WIDTH);
      int z = randomRange(Globals.MAP_HEIGHT);
      int y = (int) terrain.getHeight(new Vector2f(x, z));
      Resource res = new Resource(getNewId(), x, y, z, mat);
      resourceNode.attachChild(res);
      resources[i] = res;
    }
    worldNode.attachChild(resourceNode);
  }

  /**
   * Updates as the game loops.
   * @param tpf time per frame
   */
  @Override
  public void update(float tpf)
  {
    super.update(tpf);
    handleCollision();
    handleRespawn(tpf);
  }

  private void handleRespawn(float tpf)
  {
    time += tpf;
    if (time > Globals.SPHERE_RESPAWN_RATE)
    {
      time = 0;
      Arrays.stream(resources).filter(Resource::isCollected).forEach(r -> {
        r.setCollected(false);
        resourceNode.attachChild(r);
      });
    }
  }

  private void handleCollision() {
    CollisionResults results = new CollisionResults();
    if (playerNode instanceof UnderNode) {
      resourceNode.collideWith(playerNode.getGeometry().getWorldBound(), results);
      if (results.size() > 0) {
        Resource closest = (Resource) results.getClosestCollision().getGeometry();
        if (!closest.isCollected()) {
          audio_collect.playInstance();
          closest.setCollected(true);
          resourceNode.detachChild(closest);
          playerNode.setScaleStartTime(Globals.getTotSecs());
          playerNode.setPlayerNeedsScaling(true);
          if (playerNode.getHeight() < Globals.MAX_PLAYER_SIZE) {
            playerNode.scalePlayerUp();
          }
        }
      }
    }
  }

  private void setUpMaterial()
  {
    mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    mat.setBoolean("UseMaterialColors", true);
    mat.setColor("Diffuse", ColorRGBA.Red);
    mat.setColor("Specular", ColorRGBA.White);
    mat.setFloat("Shininess", 64f);
  }

  private static int randomRange(int range)
  {
    return Globals.getRandInt(range * 2) - range;
  }

  private static int getNewId()
  {
    return id++;
  }

  @Override
  public void cleanup()
  {
    worldNode.detachAllChildren();
    super.cleanup();
  }
}
