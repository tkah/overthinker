/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import UClient.LandscapeControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

/**
 * @author jdrid_000
 */
public class WorldManager extends AbstractAppState
{
  private SimpleApplication app;
  private Node rootNode;
  private Node worldRoot;
  public Node scene;
  public BulletAppState physics;
  private AssetManager assetManager;

  @Override
  public void initialize(AppStateManager stateManager, final Application app)
  {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    this.rootNode = this.app.getRootNode();
    this.physics = new BulletAppState();
    assetManager = app.getAssetManager();

    stateManager.attach(physics);
    physics.setDebugEnabled(true);

    loadLevel("assets/scenes/myTerrain.j3o");
    setUpLandscape();
    //attachLevel();
    setUpAmbientSound("assets/sounds/waves.ogg", 0.2f);
    setUpLights();
  }


  private void setUpLandscape()
  {
    worldRoot = new Node("LevelMaze");
    /** Create terrain material and load four textures into it. */
    Material mat_terrain = new Material(assetManager,
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
    grass.setWrap(Texture.WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap", grass);
    mat_terrain.setFloat("DiffuseMap_0_scale", 64f);

    /** Add DIRT texture into the green layer*/
    Texture dirt = assetManager.loadTexture(
      "Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(Texture.WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_1", dirt);
    mat_terrain.setFloat("DiffuseMap_1_scale", 32f);

    /** Add ROAD texture into the blue layer */
    Texture rock = assetManager.loadTexture(
      "Textures/Terrain/splat/road.jpg");
    rock.setWrap(Texture.WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_2", rock);
    mat_terrain.setFloat("DiffuseMap_2_scale", 128f);

    /** Add Lava Rocks into alpha layer**/
    Texture lava = assetManager.loadTexture(
          "Textures/lava_texture-sm.jpg");
    lava.setWrap(Texture.WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_3", lava);
    mat_terrain.setFloat("DiffuseMap_3_scale", 128f);

    /** Create the height map */
    Texture heightMapImage;

    int playerType = 0;
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
    assert heightMap != null;
    TerrainQuad terrain = new TerrainQuad("my terrain", patchSize, 513, heightMap.getHeightMap());

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
    LandscapeControl landscape = new LandscapeControl(sceneShape, 0, physics.getPhysicsSpace());
    terrain.addControl(landscape);
    worldRoot.attachChild(terrain);
    rootNode.attachChild(worldRoot);
  }


  public void loadLevel(String name)
  {
    worldRoot = (Node) app.getAssetManager().loadModel(name);
  }

  public void attachLevel()
  {
    rootNode.attachChild(worldRoot);
    physics.getPhysicsSpace().add(worldRoot);
  }

  public void setUpAmbientSound(String name, float gain)
  {
    AudioNode audioNode = new AudioNode(app.getAssetManager(), name, false);
    audioNode.setVolume(gain);
    audioNode.setLooping(true);
    audioNode.setPositional(false);
    rootNode.attachChild(audioNode);
    audioNode.play();
  }

  public void setUpLights()
  {
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(1.3f));
    rootNode.addLight(al);

    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    dl.setDirection(new Vector3f(1f, -1f, -1f).normalize());
    rootNode.addLight(dl);
  }
}
