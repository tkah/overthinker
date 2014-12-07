package overthinker.client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.InputManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import java.util.concurrent.ExecutionException;

/**
 * Created by Derek on 11/24/2014.
 */
public class MainMenuController extends AbstractAppState implements ScreenController
{

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
  private AudioNode menu_music;


  @Override
  public void initialize(AppStateManager stateManager, Application app)
  {
    super.initialize(stateManager, app);
    this.stateManager = stateManager;
    this.app = (SimpleApplication) app;
    this.cam = this.app.getCamera();
    this.rootNode = this.app.getRootNode();
    this.assetManager = this.app.getAssetManager();
    this.inputManager = this.app.getInputManager();
    this.guiViewPort = this.app.getGuiViewPort();
    this.audioRenderer = this.app.getAudioRenderer();

    JmeCursor jc = (JmeCursor) assetManager.loadAsset("overthinker/assets/interface/cursorPointing.cur");
    inputManager.setCursorVisible(true);
    inputManager.setMouseCursor(jc);

    initNifty();
  }

  public void menuStartUnderthinker()
  {
    new Thread(() -> {
      try
      {
        app.enqueue(() -> {
          nifty.gotoScreen("loading");
          menu_music.stop();
          return null;
        }).get();
        inputManager.setCursorVisible(true);
        GamePlayAppState gamePlay = new GamePlayAppState();
        gamePlay.setPlayerType(1);
        stateManager.attach(gamePlay);
        nifty.gotoScreen("loaded");
      }
      catch (InterruptedException | ExecutionException e)
      {
        e.printStackTrace();
      }
    }).start();
  }

  public void menuStartOverthinker()
  {
    new Thread(() -> {
      try
      {
        app.enqueue(() -> {
          nifty.gotoScreen("loading");
          menu_music.stop();
          return null;
        }).get();
        inputManager.setCursorVisible(true);
        GamePlayAppState gamePlay = new GamePlayAppState();
        gamePlay.setPlayerType(0);
        stateManager.attach(gamePlay);
        nifty.gotoScreen("loaded");
      }
      catch (InterruptedException | ExecutionException e)
      {
        e.printStackTrace();
      }
    }).start();
  }

  public void menuQuitGame()
  {
    app.stop();
  }

  public void setStartScreen()
  {
    nifty.gotoScreen("start");
  }

  public void setLoadingScreen()
  {
    nifty.gotoScreen("loading");
  }

  public void setLoadedScreen()
  {
    nifty.gotoScreen("loaded");
  }

  public void bind(Nifty nifty, Screen screen)
  {
    System.out.println("bind(" + screen.getScreenId() + ") ");
  }

  public void onStartScreen()
  {

  }

  public void onEndScreen()
  {

  }

  private void initNifty()
  {
    nDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
    nifty = nDisplay.getNifty();
    nifty.fromXml("overthinker/assets/interface/MainMenuLayout.xml", "start", this);
    guiViewPort.addProcessor(nDisplay);
    menu_music = new AudioNode(assetManager, "overthinker/assets/sounds/menuMusic.ogg", false);
    menu_music.setPositional(false);
    menu_music.setVolume(.3f);
    menu_music.attachChild(menu_music);
    menu_music.play();
  }
}

