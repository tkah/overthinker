package overthinker.client;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import overthinker.net.message.ActionMessage;
import overthinker.net.message.PingMessage;

import java.io.IOException;

/**
 * Created by Peter on 11/12/2014.
 */
public class ClientMain extends SimpleApplication {
    private Client netClient = null;
    private GameData gameData = new GameData();
    private ClientInputListener inputListener = new ClientInputListener(gameData);

    public static void main(String[] args) {
        ClientMain app = new ClientMain();
        app.start(JmeContext.Type.Display); // standard display type
    }

    public void simpleInitApp() {
        try {
            netClient = Network.connectToServer("localhost", 6143);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initNetClient();
        netClient.start();
        startPing();

        //TODO: Need to wait for server initial package here
        gameData.setBulletAppState(new BulletAppState());
        stateManager.attach(gameData.getBulletAppState());
        viewPort.setBackgroundColor(gameData.getBackgroundColor());
        setUpKeys();
        setUpLight();
    }

    private void setUpLight() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }

    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));


        inputManager.addListener(inputListener, "Left");
        inputManager.addListener(inputListener, "Right");
        inputManager.addListener(inputListener, "Up");
        inputManager.addListener(inputListener, "Down");
        inputManager.addListener(inputListener, "Jump");

    }

    private void startPing() {

        while(true)
        {
            netClient.send(new PingMessage("Hey!"));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initNetClient() {
        ClientNetListener listener = new ClientNetListener();

        Serializer.registerClass(PingMessage.class);
        Serializer.registerClass(ActionMessage.class);

        netClient.addMessageListener(listener, PingMessage.class);
        netClient.addMessageListener(listener, ActionMessage.class);
    }

}
