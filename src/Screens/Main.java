package Screens;

/**
 * Created by Derek on 11/24/2014.
 */

import com.jme3.app.SimpleApplication;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.system.AppSettings;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends SimpleApplication {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

   public static void main(String[] args){
       Logger.getLogger("").setLevel(Level.WARNING);
       AppSettings settings = new AppSettings(true);
       GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
       DisplayMode[] modes = device.getDisplayModes();

       int camIndex = 0;
       for(int j=0; j < (modes.length-1); j++) {
           if(modes[j].getHeight() >= 576) {
               // Found first camera settings thats over 576 in height.
               camIndex = j;
               break;
           }
       }

       settings.setResolution(modes[camIndex].getWidth(),modes[camIndex].getHeight());
       settings.setFrequency(modes[camIndex].getRefreshRate());
       settings.setBitsPerPixel(modes[camIndex].getBitDepth());

       settings.setTitle("Overthinker!");


       settings.setVSync(true);

       Main app = new Main();
       app.setSettings(settings);
       app.setShowSettings(false);
       app.start();

   }


    @Override
    public void simpleInitApp(){
        setDisplayStatView(false);
        setDisplayFps(false);

        MainMenuController mmc = new MainMenuController();
        stateManager.attach(mmc);

        JmeCursor jc = (JmeCursor) assetManager.loadAsset("assets/interface/cursorPointing.cur");
        inputManager.setCursorVisible(true);
        inputManager.setMouseCursor(jc);
    }


}
