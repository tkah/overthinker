package client;

import com.jme3.app.SimpleApplication;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import java.util.concurrent.ExecutionException;

public class ClientMain extends SimpleApplication implements ScreenController
{
  private Nifty nifty;
  private static ClientMain app;
  private LevelManager levelManager;
  private PlayerManager playerManager;
  private InteractionManager interactionManager;
  private CameraManager cameraManager;
  private NiftyJmeDisplay niftyDisplay;

  @Override
  public void bind(Nifty nifty, Screen screen)
  {

  }

  @Override
  public void onStartScreen()
  {

  }

  @Override
  public void onEndScreen()
  {

  }

  @Override
  public void destroy()
  {
    if(stateManager.hasState(levelManager)) stateManager.detach(levelManager);
    if(stateManager.hasState(playerManager)) stateManager.detach(playerManager);
    if(stateManager.hasState(interactionManager)) stateManager.detach(interactionManager);
    if(stateManager.hasState(cameraManager)) stateManager.detach(cameraManager);
    super.destroy();
  }

  @Override
  public void simpleInitApp()
  {
    setDisplayStatView(false);
    setDisplayFps(false);

    JmeCursor jc = (JmeCursor) assetManager.loadAsset("assets/interface/cursorPointing.cur");
    inputManager.setCursorVisible(true);
    inputManager.setMouseCursor(jc);

    startNifty();
  }

  public void startGame()
  {
    new Thread(() -> {
      try
      {
        enqueue(() -> {
          nifty.gotoScreen("loading_screen");
          return null;
        }).get();
        getFlyByCamera().setEnabled(false);
        inputManager.setCursorVisible(true);
        stateManager.attach((levelManager = new LevelManager()));
        stateManager.attach((playerManager = new PlayerManager()));
        stateManager.attach((interactionManager = new InteractionManager()));
        stateManager.attach((cameraManager = new CameraManager()));
        nifty.gotoScreen("loaded_screen");
      }
      catch (InterruptedException | ExecutionException e)
      {
        e.printStackTrace();
      }
    }).start();
  }

  private void startNifty()
  {
    flyCam.setDragToRotate(true);
    guiNode.detachAllChildren();
    niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
    nifty = niftyDisplay.getNifty();
    try
    {
      nifty.fromXml("assets/interface/StartMenu.xml", "start_screen", this);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    guiViewPort.addProcessor(niftyDisplay);
  }

  public static void main(String[] args)
  {
    app = new ClientMain();
    app.start();
  }
}
