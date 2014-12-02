package client;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Created by jdriden on 12/1/2014.
 */
public class ClientMain extends SimpleApplication implements ScreenController
{
  private Nifty nifty;
  private static ClientMain app;
  private WorldManager worldManager;
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
    stateManager.detach(worldManager);
    stateManager.detach(playerManager);
    stateManager.detach(interactionManager);
    stateManager.detach(cameraManager);
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

  public void attachServer()
  {
    attachClient();
  }


  public void attachClient()
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
        stateManager.attach((worldManager = new WorldManager()));
        stateManager.attach((playerManager = new PlayerManager()));
        stateManager.attach((interactionManager = new InteractionManager()));
        stateManager.attach((cameraManager = new CameraManager()));
        nifty.gotoScreen("loaded");
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
      nifty.fromXml("assets/interface/StartMenu.xml", "start_menu", this);
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
