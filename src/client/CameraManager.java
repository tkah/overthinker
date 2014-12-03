/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.control.CameraControl;

/**
 *
 * @author jdrid_000
 */
public class CameraManager extends AbstractAppState {

    private SimpleApplication app;
    private Player player;
    public ChaseCamera cam;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.player = this.app.getStateManager().getState(PlayerManager.class).player;
        initCamera();
    }

    //Creates camera
    public void initCamera() {
        //Creates a new chase cam and attached it to the player.model for the game
        cam = new ChaseCamera(this.app.getCamera(), player, this.app.getInputManager());
        cam.setMinDistance(10f);
        cam.setMaxDistance(15f);
        cam.setSmoothMotion(true);
        cam.setInvertVerticalAxis(true);
        cam.setDragToRotate(false);
    }
}
