/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author jdrid_000
 */
public class PlayerManager extends AbstractAppState {

    private Node rootNode;
    private SimpleApplication app;
    private AppStateManager stateManager;
    private BulletAppState physics;
    private AssetManager assetManager;
    public Player player;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.physics = this.stateManager.getState(WorldManager.class).physics;
        initPlayer();
    }

    //Creates the player
    private void initPlayer() {
        player = new Player();
        setUpModel();
        setUpPivot();
        attachPlayerNodes();
    }

    private void setUpModel() {
        player.model = new Node("Player");
        Sphere shape = new Sphere(32, 32, 2f);
        Geometry sphere = new Geometry("Sphere", shape);
        shape.setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 64f);
        sphere.setMaterial(mat);
        player.model.attachChild(sphere);
    }
    
    private void setUpPivot()
    {
        player.pivot = new Node("Pivot");
        player.playerPhys = new BetterCharacterControl(2f, 4f, 1f);
        player.playerPhys.setGravity(new Vector3f(0, -50, 0));
        player.model.setLocalTranslation(0f, 2f, 0f);
        physics.getPhysicsSpace().add(player.playerPhys);
        player.addControl(player.playerPhys);
    }
    
    private void attachPlayerNodes()
    {
        player.attachChild(player.model);
        player.attachChild(player.pivot);
        rootNode.attachChild(player);
    }
}
