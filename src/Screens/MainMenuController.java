package Screens;

import UClient.GamePlayAppState;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import com.jme3.scene.Node;

/**
 * Created by Derek on 11/24/2014.
 */
public class MainMenuController extends AbstractAppState implements ScreenController {

    private SimpleApplication app;
    private Camera cam;
    private Node rootNode;
    private AssetManager assetManager;
    private InputManager inputManager;
    private ViewPort guiViewPort;
    private AudioRenderer audioRenderer;
    private AppStateManager stateManager;
    private Node sceneNode;
    private Nifty nifty;
    private NiftyJmeDisplay nDisplay;
    private BulletAppState bulletAppState;



  @Override
    public void initialize(AppStateManager stateManager, Application app){
      super.initialize(stateManager, app);
      this.stateManager = stateManager;
      this.app = (SimpleApplication) app;
      this.cam = this.app.getCamera();
      this.rootNode = this.app.getRootNode();
      this.assetManager = this.app.getAssetManager();
      this.inputManager = this.app.getInputManager();
      this.guiViewPort = this.app.getGuiViewPort();
      this.audioRenderer = this.app.getAudioRenderer();

      JmeCursor jc = (JmeCursor) assetManager.loadAsset("assets/interface/cursorPointing.cur");
      inputManager.setCursorVisible(true);
      inputManager.setMouseCursor(jc);

      initNifty();
  }

    private void initNifty()
    {
        nDisplay = new NiftyJmeDisplay(assetManager,inputManager,audioRenderer,guiViewPort);
        nifty = nDisplay.getNifty();
        nifty.fromXml("assets/interface/MainMenuLayout.xml", "start", this);
        guiViewPort.addProcessor(nDisplay);
    }

    public void menuStartUnderthinker()
    {
      GamePlayAppState gamePlay = new GamePlayAppState();
      stateManager.detach(this);
      nifty.exit();
      gamePlay.setPlayerType(1);
      stateManager.attach(gamePlay);
    }

    public void menuStartOverthinker()
    {
      GamePlayAppState gamePlay = new GamePlayAppState();
      stateManager.detach(this);
      nifty.exit();
      gamePlay.setPlayerType(0);
      stateManager.attach(gamePlay);
    }

    public void menuQuitGame(){
        app.stop();
    }

    public void methodToBeCalledWhenEffectStarted() {
        System.out.println("Hover start or stop?");
    }

    public void setStartScreen(){
        nifty.gotoScreen("start");
    }

    public void setLoadingScreen(){
        nifty.gotoScreen("loading");
    }

    public void setLoadedScreen() {nifty.gotoScreen("loaded");}

    public void bind(Nifty nifty, Screen screen){
        System.out.println("bind(" + screen.getScreenId() + ") ");
    }

    public void onStartScreen(){

    }

    public void onEndScreen(){

    }
}

