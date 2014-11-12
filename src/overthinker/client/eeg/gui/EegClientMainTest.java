package overthinker.client.eeg.gui;

import overthinker.client.eeg.ClientListener;
import overthinker.net.HelloMessage;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.AppSettings;
import overthinker.net.Util;

import java.io.IOException;

/**
 * Created by Peter on 11/11/2014.
 */
public class EegClientMainTest extends SimpleApplication implements ActionListener {


    private boolean left = false, right = false, up = false, down = false;
    private CharacterControl player;

    private Client myClient = null;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setFrameRate(0);
        settings.setTitle("Overthinker");
        Util.registerSerializers();
        EegClientMainTest app = new EegClientMainTest();
        app.setSettings(settings);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    private void startEngine() {
        while(true)
        {
            Message message = new HelloMessage("Hey!");
            myClient.send(message);
            try {
                Thread.sleep(5000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void simpleInitApp() {
        try {
            myClient = Network.connectToServer("localhost", 6143);
        } catch (IOException e) {
            e.printStackTrace();
        }

        myClient.start();
        Serializer.registerClass(HelloMessage.class);
        myClient.addMessageListener(new ClientListener(), HelloMessage.class);
        startEngine();
    }

    @Override
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) {
            left = isPressed;
        } else if (binding.equals("Right")) {
            right = isPressed;
        } else if (binding.equals("Up")) {
            up = isPressed;
        } else if (binding.equals("Down")) {
            down = isPressed;
        } else if (binding.equals("Jump")) {
            if (isPressed) {
                player.jump();
            }
        }
    }
}
