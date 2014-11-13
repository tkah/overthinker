package overthinker.server.gui;


import overthinker.net.message.NewClientRequestMessage;
import overthinker.net.message.NewClientResponseMessage;
import overthinker.net.message.PingMessage;
import com.jme3.app.SimpleApplication;
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
    private Server netServer = null;
    private static final boolean DEBUG = true;

    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless); // headless type for servers!
    }
    @Override
    public void simpleInitApp() {
        try {
            netServer = Network.createServer(6143);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initNetServer();
        netServer.start();
    }

    public Server getNetServer()
    {
        return netServer;
    }

    private void initNetServer() {
        ServerListener listener = new ServerListener(this);

        Serializer.registerClass(NewClientRequestMessage.class);
        Serializer.registerClass(NewClientResponseMessage.class);

        netServer.addMessageListener(listener, NewClientRequestMessage.class);
    }
}
