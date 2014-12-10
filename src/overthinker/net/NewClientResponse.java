package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import java.util.HashMap;

/**
 * This class is a response given by the server on receiving a new client request.
 *
 * @author Peter, Derek, Torran, Sid, and Josh.
 */
@Serializable
public class NewClientResponse extends AbstractMessage {
    private Vector3f spawnLocation = null;
    private HashMap<Integer, Vector3f> playerLocations;
    private long version;
    private boolean connected;
    private int clientIndex;

    /**
     * Empty constructor used by Serializable.
     */
    public NewClientResponse() {}

    /**
     * Gets the spawn location of the new client
     *
     * @return - spawn location.
     */
    public Vector3f getSpawnLocation()
    {
        return spawnLocation;
    }

    /**
     * Sets the spawn location of the new client.
     *
     * @param spawnLocation - spawn location.
     */
    public void setSpawnLocation(Vector3f spawnLocation){
        this.spawnLocation = spawnLocation;
    }

    /**
     * Gets if the new client successfully connected to the server
     *
     * @return - connection state.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Sets if the new client connected to the server.
     *
     * @param connected - connection state.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Gets the initial locations of all connected clients.
     *
     * @return - HashMap of client index to locations.
     */
    public HashMap<Integer, Vector3f> getPlayerLocations() {
        return playerLocations;
    }

    /**
     * Sets the initial locations of all connected clients.
     *
     * @param playerLocations - new HashMap of client index to locations.
     */
    public void setPlayerLocations(HashMap<Integer, Vector3f> playerLocations) {
        this.playerLocations = playerLocations;
    }

    /**
     * Gets the current server model version.
     *
     * @return - server model version.
     */
    public long getVersion() {
        return version;
    }

    /**
     * Sets the current server model version
     *
     * @param version - current server model version.
     */
    public void setVersion(long version) {
        this.version = version;
    }

    /**
     * Gets newly connected clients index on the server.
     *
     * @return - newly connected client index
     */
    public int getClientIndex() {
        return clientIndex;
    }

    /**
     * Sets the newly connected client index on the server.
     *
     * @param clientIndex - newly connected client index.
     */
    public void setClientIndex(int clientIndex) {
        this.clientIndex = clientIndex;
    }
}
