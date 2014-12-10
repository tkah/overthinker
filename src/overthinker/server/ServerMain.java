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
 * Used as the main game server. Holds the current model and client information needed for indexing in the model.
 * Handles model changes, new clients and sends model updates to connected clients.
 *
 * @author Peter, Torran, Josh, Derek and Sid
 */
public class ServerMain extends SimpleApplication {
    private Server netServer;
    private ServerModel model;
    private ConcurrentHashMap<HostedConnection, Integer> clientIndex = new ConcurrentHashMap<>();
    private static final int PLAYER_CAP = 4;
    private static final boolean DEBUG = true;

    /**
     * Main entry point for the server. Initializes the server application.
     *
     * @param args - none needed.
     */
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
            if(DEBUG) System.out.println("Disconnecting " + clientIndex.get(client));
            model.getPlayerAlive().remove(clientIndex.get(client));
            model.getPlayerLocations().remove(clientIndex.get(client));
            model.setVersion(model.getVersion() + 1);
            clientIndex.remove(client);
            broadcastModelUpdate();
        });
    }

    /**
     * Used to send the current model all connected clients.
     */
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


    /**
     * Handles the request for a new client to join the server.
     *
     * @param source - HostedConnection of the new client.
     * @param message - NewClientRequest of the new client.
     */
    public void initClient(HostedConnection source, NewClientRequest message)
    {
        if(DEBUG)System.out.println("New client request from: " + source.getAddress());

        NewClientResponse response = new NewClientResponse();

        if(message.isEEG())
        {
            if(model.getPlayerLocations().get(PLAYER_CAP-1) == null)
            {
                model.getPlayerLocations().put(PLAYER_CAP-1, new Vector3f(0,0,0));
                model.getPlayerAlive().put(PLAYER_CAP-1, true);
                model.setVersion(model.getVersion() + 1);
                clientIndex.put(source, PLAYER_CAP-1);
                response.setConnected(true);
                response.setClientIndex(PLAYER_CAP-1);
                broadcastModelUpdate();
            }
            else response.setConnected(false);

        }
        else
        {
            boolean clientConnected = false;
            for(int i = 0; i < PLAYER_CAP-1; i++)
            {
                if(model.getPlayerLocations().get(i) == null)
                {
                    clientConnected = true;
                    model.getPlayerLocations().put(i, new Vector3f(0,0,0));
                    model.getPlayerAlive().put(i, true);
                    model.setVersion(model.getVersion() + 1);
                    clientIndex.put(source, i);
                    response.setConnected(true);
                    response.setClientIndex(i);
                    broadcastModelUpdate();
                    break;
                }
            }
            if(!clientConnected) response.setConnected(false);
        }

        // Send response
        response.setSpawnLocation(new Vector3f(0,0,0));
        response.setVersion(model.getVersion());
        response.setPlayerLocations(model.getPlayerLocations());
        netServer.broadcast(Filters.in(source), response);

    }

    /**
     * Returns the current server model
     *
     * @return - the current server model
     */
    public ServerModel getModel() {
        return model;
    }

    /**
     * Handles a new location request from a given client.
     *
     * @param source - Requesting client.
     * @param playerLocation - New location of the requesting client.
     */
    public void updatePlayerLocation(HostedConnection source, Vector3f playerLocation) {
        model.getPlayerLocations().replace(clientIndex.get(source), playerLocation);
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
    }

    /**
     * Handles a death request from a given client.
     *
     * @param source - Requesting client.
     */
    public void handlePlayerDeath(HostedConnection source)
    {
        model.getPlayerAlive().replace(clientIndex.get(source), false);
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
    }

    /**
     * Handles a map tilt request from the overthinker.
     *
     * @param message ChangeMapTiltRequest from the overthinker.
     */
    public void updateMapTilt(ChangeMapTiltRequest message) {
        model.setGravityRight(message.isRight());
        model.setGravityLeft(message.isLeft());
        model.setGravityForward(message.isForward());
        model.setGravityBack(message.isBack());
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
    }

    /**
     * Handles a water rate change request.
     *
     * @param message - ChangeWaterRateRequest
     */
    public void updateWaterRate(ChangeWaterRateRequest message) {
        model.setWaterRate(message.getWaterRate());
        model.setVersion(model.getVersion() + 1);
        broadcastModelUpdate();
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
}
