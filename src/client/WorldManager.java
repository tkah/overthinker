/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import UClient.LandscapeControl;
import com.jme3.ai.navmesh.NavMesh;
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
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import jme3tools.optimize.GeometryBatchFactory;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
  private NavMesh navMesh;
  private NavMeshGenerator navMeshGenerator;

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

    loadLevel("assets/terrains/tieredmaze.j3o");
    attachLevel();
    setUpAmbientSound("assets/sounds/waves.ogg", 0.2f);
    setUpLights();
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
