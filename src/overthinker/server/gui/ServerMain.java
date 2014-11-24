package overthinker.server.gui;


import com.jme3.network.HostedConnection;
import overthinker.Util;
import overthinker.levels.maze1.Maze1;
import overthinker.net.message.ModelChangeRequest;
import overthinker.net.message.ModelUpdate;
import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import overthinker.net.message.NewClientRequest;
import overthinker.server.ServerNetListener;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerMain extends SimpleApplication {
    private Server netServer;
    private Util.Model activeModel;
    private long modelVersion;
    private ArrayList<HostedConnection> clients = new ArrayList<HostedConnection>();
    private HashMap<HostedConnection, Point> clientLocations = new HashMap<HostedConnection, Point>();
    private HashMap<HostedConnection, Float> versions = new HashMap<HostedConnection, Float>();

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
        initModel();
        initNetServer();
        netServer.start();
    }

    private void initModel() {
        modelVersion = 0L;
        activeModel = new Util.Model(new Maze1());
    }

    public ArrayList<HostedConnection> getClients()
    {
        return clients;
    }

    private void initNetServer() {
        ServerNetListener listener = new ServerNetListener(this);

        Serializer.registerClass(ModelChangeRequest.class);
        Serializer.registerClass(ModelUpdate.class);
        Serializer.registerClass(NewClientRequest.class);

        netServer.addMessageListener(listener, NewClientRequest.class);
        netServer.addMessageListener(listener, ModelChangeRequest.class);
    }

    public HashMap<HostedConnection, Float> getVersions() {
        return versions;
    }

    public Server getNetServer(){
        return netServer;
    }

    public Util.Model getActiveModel() {
        return activeModel;
    }

    public long getModelVersion() {
        return modelVersion;
    }
}
