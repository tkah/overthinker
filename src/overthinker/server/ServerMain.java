package overthinker.server;


import com.jme3.math.Vector3f;
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
import java.util.HashMap;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerMain extends SimpleApplication {
    private Server netServer;
    private ServerModel model;
    private Level level;
    private HashMap<HostedConnection, Integer> clientIndex = new HashMap<HostedConnection, Integer>();
    private HashMap<HostedConnection, Long> clientModelVersions = new HashMap<HostedConnection, Long>();

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

    public void broadcastModelUpdate()
    {
        ModelUpdate modelUpdate = new ModelUpdate();
        modelUpdate.setPlayerLocations(model.getPlayerLocations());
        modelUpdate.version = model.version;
        netServer.broadcast(modelUpdate);
    }

    private void initModel() {
        model = new ServerModel();
        level = new Maze1();
        for(int i = 0; i < level.getPlayerCount(); i++)
        {
            model.getPlayerLocations().put(i, null);
        }
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


    public HashMap<HostedConnection, Long> getClientModelVersions() {
        return clientModelVersions;
    }

    public Server getNetServer(){
        return netServer;
    }

    public NewClientResponse addClient(HostedConnection sources)
    {
        Vector3f spawnLocation = null;
        for(int i = 0; i < level.getPlayerCount(); i++)
        {
            if(model.getPlayerLocations().get(i) == null)
            {
                spawnLocation = level.getRandomSpawnLocation();
                model.getPlayerLocations().put(i, spawnLocation);
                clientIndex.put(sources, i);
                clientModelVersions.put(sources, model.version);
            }
        }

        NewClientResponse response = new NewClientResponse();
        if(spawnLocation != null)
        {
            response.setLevelType(level.getLevelType());
            response.setSpawnLocation(spawnLocation);
            response.setConnected(true);
        }
        else response.setConnected(false);
        return response;
    }

    public ServerModel getModel() {
        return model;
    }

    public synchronized void updateModel(HostedConnection source, Vector3f playerLocation) {
        model.getPlayerLocations().replace(clientIndex.get(source), playerLocation);
        model.version += 1;
        broadcastModelUpdate();
    }
}
