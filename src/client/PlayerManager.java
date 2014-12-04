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
import game.Level.LevelManager;

/**
 *
 * @author jdrid_000
 */
@SuppressWarnings("FieldCanBeLocal")
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
        this.physics = this.stateManager.getState(LevelManager.class).getPhysics();
        this.player = new Player();
        setUpModel();
        setUpPhysics();
        warpPlayer();
        attachPlayerNodes();
    }

    private void warpPlayer()
    {
        player.playerPhys.warp(new Vector3f(60,65,-330));
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
        player.model.setLocalTranslation(0f, 2f, 0f);
    }
    
    private void setUpPhysics()
    {
        player.playerPhys = new BetterCharacterControl(2f, 4f, 1f);
        player.playerPhys.setJumpForce(Vector3f.UNIT_Y.mult(10f));
        player.playerPhys.setGravity(Vector3f.UNIT_Y.mult(-50f));
        physics.getPhysicsSpace().add(player.playerPhys);
        player.addControl(player.playerPhys);
    }
    
    private void attachPlayerNodes()
    {
        player.attachChild(player.model);
        rootNode.attachChild(player);
    }
}
