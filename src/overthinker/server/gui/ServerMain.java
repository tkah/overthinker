package overthinker.server.gui;

import overthinker.net.HelloMessage;
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
class ServerMain extends SimpleApplication {
    private Server myServer = null;
    private static final boolean DEBUG = true;

    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless); // headless type for servers!
    }

    private void startEngine() {

        while(true)
        {
            Message message = new HelloMessage("Hey!");
            myServer.broadcast(message);
            if(DEBUG)System.out.println("Broadcasting message");

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
            myServer = Network.createServer(6143);
        } catch (IOException e) {
            e.printStackTrace();
        }

        myServer.start();
        Serializer.registerClass(HelloMessage.class);
        myServer.addMessageListener(new ServerListener(), HelloMessage.class);
        startEngine();

    }
}
