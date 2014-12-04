package overthinker.server;


import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import overthinker.client.Globals;
import overthinker.levels.Level;
import overthinker.levels.maze1.Maze1;
import overthinker.net.ModelChangeRequest;
import overthinker.net.ModelUpdate;
import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import overthinker.net.NewClientRequest;
import overthinker.net.NewClientResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerMain extends SimpleApplication {
    private Server netServer;
    private ServerModel model;
    private Level level;
    private int clientCount = 0;
    private ConcurrentHashMap<HostedConnection, Integer> clientIndex = new ConcurrentHashMap<HostedConnection, Integer>();

    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
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

    @Override
    public void simpleUpdate(float tpf) {

        for(HostedConnection client : clientIndex.keySet())
        {
            if (!client.getServer().hasConnections())
            {
                clientIndex.remove(client);
                clientCount--;
            }
        }
    }
    public void broadcastModelUpdate()
    {
        ModelUpdate modelUpdate = new ModelUpdate();
        modelUpdate.setPlayerLocations(model.getPlayerLocations());
        modelUpdate.version = model.getVersion();
        netServer.broadcast(modelUpdate);
    }

    private void initModel() {
        model = new ServerModel();
        level = new Maze1();
    }

    private void initNetServer() {
        ServerNetListener listener = new ServerNetListener(this);

        Serializer.registerClass(ModelChangeRequest.class);
        Serializer.registerClass(ModelUpdate.class);
        Serializer.registerClass(NewClientRequest.class);
        Serializer.registerClass(NewClientResponse.class);

        netServer.addMessageListener(listener, NewClientRequest.class);
        netServer.addMessageListener(listener, ModelChangeRequest.class);
    }

    public void initClient(HostedConnection source)
    {
        Vector3f spawnLocation = level.getRandomSpawnLocation();
        NewClientResponse response = new NewClientResponse();

        // Check if there is room on the current level
        if(clientIndex.values().size() < level.getPlayerCount())
        {
            model.getPlayerLocations().put(clientCount, spawnLocation);
            clientIndex.put(source, clientCount);
            response.setConnected(true);
            response.setClientIndex(clientCount);
            clientCount++;
            model.setVersion(model.getVersion() + 1);
            broadcastModelUpdate();
        }
        else response.setConnected(false);

        // Send response
//        response.setLevelType(level.getLevelType());
        response.setSpawnLocation(spawnLocation);
        response.setVersion(model.getVersion());
        response.setPlayerLocations(model.getPlayerLocations());
        netServer.broadcast(Filters.in(source), response);

    }

    public ServerModel getModel() {
        return model;
    }

    public synchronized void updateModel(HostedConnection source, Vector3f playerLocation) {
        model.getPlayerLocations().replace(clientIndex.get(source), playerLocation);
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
    }
}
