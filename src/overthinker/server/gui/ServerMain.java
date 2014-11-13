package overthinker.server.gui;

import overthinker.net.message.ActionMessage;
import overthinker.net.message.PingMessage;
import com.jme3.app.SimpleApplication;
import com.jme3.network.Message;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import overthinker.server.ServerListener;

import java.io.IOException;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerMain extends SimpleApplication {
    private Server myServer = null;
    private static final boolean DEBUG = true;

    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless); // headless type for servers!
    }
    @Override
    public void simpleInitApp() {
        try {
            myServer = Network.createServer(6143);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initNetServer();
        myServer.start();

        startPing();
    }

    private void startPing() {

        while(true)
        {
            Message message = new PingMessage("Hey!");
            myServer.broadcast(message);

            if(DEBUG)System.out.println("Broadcasting ping");

            try {
                Thread.sleep(2000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void initNetServer() {
        ServerListener listener = new ServerListener();

        Serializer.registerClass(PingMessage.class);
        Serializer.registerClass(ActionMessage.class);

        myServer.addMessageListener(listener, PingMessage.class);
        myServer.addMessageListener(listener, ActionMessage.class);
    }
}
