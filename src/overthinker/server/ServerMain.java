package overthinker.server;


import com.jme3.math.Vector3f;
import com.jme3.network.*;
import overthinker.net.*;
import com.jme3.app.SimpleApplication;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerMain extends SimpleApplication {
    private Server netServer;
    private ServerModel model;
    private int clientCount = 0;
    private static final int PLAYER_CAP = 4;
    private static final Vector3f SPAWN_LOCATION = new Vector3f(340, 80, -400);
    private ConcurrentHashMap<HostedConnection, Integer> clientIndex = new ConcurrentHashMap<>();

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

        clientIndex.keySet().stream().filter(client -> !client.getServer().hasConnections()).forEach(client -> {
            clientIndex.remove(client);
            clientCount--;
        });
    }
    public void broadcastModelUpdate()
    {
        ModelUpdate modelUpdate = new ModelUpdate();
        modelUpdate.setPlayerLocations(model.getPlayerLocations());
        modelUpdate.setPlayerAlive(model.getPlayerAlive());
        modelUpdate.version = model.getVersion();
        modelUpdate.setGravityRight(model.isGravityRight());
        modelUpdate.setGravityLeft(model.isGravityLeft());
        modelUpdate.setGravityForward(model.isGravityForward());
        modelUpdate.setGravityBack(model.isGravityBack());
        modelUpdate.setWaterRate(model.getWaterRate());
        netServer.broadcast(modelUpdate);
    }

    private void initModel() {
        model = new ServerModel();
    }

    private void initNetServer() {
        ServerNetListener listener = new ServerNetListener(this);

        Serializer.registerClass(ChangePlayerLocationRequest.class);
        Serializer.registerClass(ChangeMapTiltRequest.class);
        Serializer.registerClass(ChangeWaterRateRequest.class);
        Serializer.registerClass(PlayerDeathRequest.class);
        Serializer.registerClass(ModelUpdate.class);
        Serializer.registerClass(NewClientRequest.class);
        Serializer.registerClass(NewClientResponse.class);

        netServer.addMessageListener(listener, NewClientRequest.class);
        netServer.addMessageListener(listener, ChangePlayerLocationRequest.class);
        netServer.addMessageListener(listener, ChangeMapTiltRequest.class);
        netServer.addMessageListener(listener, ChangeWaterRateRequest.class);
        netServer.addMessageListener(listener, PlayerDeathRequest.class);
    }

    public void initClient(HostedConnection source)
    {
        NewClientResponse response = new NewClientResponse();

        // Check if there is room on the current level
        if(clientIndex.values().size() < PLAYER_CAP)
        {
            model.getPlayerLocations().put(clientCount, SPAWN_LOCATION);
            model.getPlayerAlive().put(clientCount, true);
            clientIndex.put(source, clientCount);
            response.setConnected(true);
            response.setClientIndex(clientCount);
            clientCount++;
            model.setVersion(model.getVersion() + 1);
            broadcastModelUpdate();
        }
        else response.setConnected(false);

        // Send response
        response.setSpawnLocation(SPAWN_LOCATION);
        response.setVersion(model.getVersion());
        response.setPlayerLocations(model.getPlayerLocations());
        netServer.broadcast(Filters.in(source), response);

    }

    public ServerModel getModel() {
        return model;
    }

    public void updatePlayerLocation(HostedConnection source, Vector3f playerLocation) {
        model.getPlayerLocations().replace(clientIndex.get(source), playerLocation);
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
    }

    public void handlePlayerDeath(HostedConnection source)
    {
        model.getPlayerAlive().replace(clientIndex.get(source), false);
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
    }

    public void updateMapTilt(ChangeMapTiltRequest message) {
        model.setGravityRight(message.isRight());
        model.setGravityLeft(message.isLeft());
        model.setGravityForward(message.isForward());
        model.setGravityBack(message.isBack());
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
    }


    public void updateWaterRate(ChangeWaterRateRequest message) {
        model.setWaterRate(message.getWaterRate());
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
    }
}
