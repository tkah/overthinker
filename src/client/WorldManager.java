/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;

/**
 * @author jdrid_000
 */
public class WorldManager extends AbstractAppState
{
  private SimpleApplication app;
  private Node rootNode;
  private Node worldRoot;
  private DirectionalLightShadowRenderer dlsr;
  private DirectionalLightShadowFilter dlsf;
  public Node scene;
  public BulletAppState physics;

  @Override
  public void initialize(AppStateManager stateManager, final Application app)
  {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    this.rootNode = this.app.getRootNode();
    this.physics = new BulletAppState();

    stateManager.attach(physics);
    physics.setDebugEnabled(true);

    loadLevel("assets/scenes/myTerrain.j3o");
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
/*
        dlsr = new DirectionalLightShadowRenderer(app.getAssetManager(), 1024, 3);
        dlsr.setLight(dl);
        dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.6f);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        app.getViewPort().addProcessor(dlsr);

        dlsf = new DirectionalLightShadowFilter(app.getAssetManager(), 1024, 3);
        dlsf.setLight(dl);
        dlsf.setLambda(0.55f);
        dlsf.setShadowIntensity(0.6f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        dlsf.setEnabled(false);
        
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
        fpp.addFilter(dlsf);
        
        app.getViewPort().addProcessor(fpp);
        */
  }
}
