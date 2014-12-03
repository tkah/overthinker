package client;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.water.WaterFilter;
import jme3tools.optimize.GeometryBatchFactory;
import jme3utilities.Misc;
import jme3utilities.sky.SkyControl;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jdrid_000 on 12/2/2014.
 */
public class LevelManager extends AbstractAppState
{
  private SimpleApplication app;
  private Node rootNode;
  private Node worldNode;
  private BulletAppState physics;
  private AssetManager assetManager;
  private DirectionalLight mainLight;
  private NavMesh navMesh;
  private NavMeshGenerator navMeshGenerator;

  public BulletAppState getPhysics()
  {
    return physics;
  }

  @Override
  public void initialize(AppStateManager stateManager, Application application)
  {
    super.initialize(stateManager, application);
    this.app = (SimpleApplication) application;
    this.rootNode = this.app.getRootNode();
    this.physics = new BulletAppState();
    this.assetManager = this.app.getAssetManager();
    this.worldNode = new Node();

    stateManager.attach(physics);
    physics.setDebugEnabled(true);

    setUpLandscape(1);
    setUpLight();
    setUpNavMesh();
  }

  public void setUpNavMesh()
  {
    Mesh mesh = new Mesh();
    navMesh = new NavMesh();
    navMeshGenerator = new NavMeshGenerator();
    navMeshGenerator.setCellSize(1f);
    navMeshGenerator.setCellHeight(2f);

    GeometryBatchFactory.mergeGeometries(findGeometries(worldNode, new LinkedList<>()), mesh);
    Mesh optiMesh = navMeshGenerator.optimize(mesh);

    navMesh.loadFromMesh(optiMesh);

    Geometry navGeom = new Geometry("NavMesh");
    navGeom.setMesh(optiMesh);
    Material green = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    green.setColor("Color", ColorRGBA.Green);
    green.getAdditionalRenderState().setWireframe(true);
    navGeom.setMaterial(green);

    worldNode.attachChild(navGeom);
  }

  private List<Geometry> findGeometries(Node node, List<Geometry> geoms)
  {
    for (Iterator<Spatial> it = node.getChildren().iterator(); it.hasNext(); )
    {
      Spatial spatial = it.next();
      if (spatial instanceof Geometry)
      {
        geoms.add((Geometry) spatial);
      }else if (spatial instanceof Node)
      {
        findGeometries((Node) spatial, geoms);
      }
    }
    return geoms;
  }

  private void setUpLight()
  {
    Vector3f lightDir = new Vector3f(4.1f, -3.2f, 0.1f);
    mainLight = new DirectionalLight();
    mainLight.setName("main");
    mainLight.setColor(ColorRGBA.White.clone().multLocal(1.1f));
    mainLight.setDirection(lightDir);

    DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
    dlsr.setLight(mainLight);
    app.getViewPort().addProcessor(dlsr);
/*
    SkyControl sc = new SkyControl(assetManager, app.getCamera(), 0.9f, true, true);
    sc.getSunAndStars().setHour(12f);
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
    */

    rootNode.addLight(mainLight);

    //worldNode.addControl(sc);
    setUpWater();
    BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
    bloom.setBlurScale(2.5f);
    bloom.setExposurePower(1f);
    Misc.getFpp(app.getViewPort(), assetManager).addFilter(bloom);
    //sc.getUpdater().addBloomFilter(bloom);
  }

  private void setUpWater()
  {
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    WaterFilter water = new WaterFilter(rootNode, mainLight.getDirection());
    water.setWaterHeight(20f);
    water.setDeepWaterColor(new ColorRGBA(0.0f, 0.5f, 0.5f, 1.0f));
    fpp.addFilter(water);
  }

  private void setUpLandscape(int playerType)
  {
    /** Create terrain material and load four textures into it. */
    Material mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
    mat_terrain.setBoolean("useTriPlanarMapping", false);
    mat_terrain.setFloat("Shininess", 0.0f);
    mat_terrain.setFloat("Ambient", 10.0f);

    /** Add ALPHA map (for red-blue-green coded splat textures) */
    mat_terrain.setTexture("AlphaMap", assetManager.loadTexture("assets/terrains/tieredmaze1color.png"));

    /** Add GRASS texture into the red layer*/
    Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
    grass.setWrap(Texture.WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap", grass);
    mat_terrain.setFloat("DiffuseMap_0_scale", 64f);

    /** Add DIRT texture into the green layer*/
    Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(Texture.WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_1", dirt);
    mat_terrain.setFloat("DiffuseMap_1_scale", 32f);

    /** Add ROAD texture into the blue layer */
    Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
    rock.setWrap(Texture.WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_2", rock);
    mat_terrain.setFloat("DiffuseMap_2_scale", 128f);

    /** Add Lava Rocks into alpha layer**/
    Texture lava = assetManager.loadTexture("Textures/lava_texture-sm.jpg");
    lava.setWrap(Texture.WrapMode.Repeat);
    mat_terrain.setTexture("DiffuseMap_3", lava);
    mat_terrain.setFloat("DiffuseMap_3_scale", 128f);

    /** Create the height map */
    Texture heightMapImage;

    if (playerType == 0)
    {
      heightMapImage = assetManager.loadTexture("assets/terrains/tieredmaze1_nowalls.png");
    }
    else
    {
      heightMapImage = assetManager.loadTexture("assets/terrains/tieredmaze1.png");
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
    CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(terrain);
    RigidBodyControl rigidBodyControl = new RigidBodyControl(sceneShape, 0);
    terrain.addControl(rigidBodyControl);
    physics.getPhysicsSpace().add(terrain);
    worldNode.attachChild(terrain);
    rootNode.attachChild(worldNode);
  }


}
