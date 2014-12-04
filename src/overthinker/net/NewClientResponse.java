package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import overthinker.levels.LevelType;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by Peter on 11/24/2014.
 */
@Serializable
public class NewClientResponse extends AbstractMessage {
    private Vector3f spawnLocation = null;
    private HashMap<Integer, Vector3f> playerLocations;
    private long version;
    private boolean connected;
    private int clientIndex;

    public NewClientResponse() {}

    public Vector3f getSpawnLocation()
    {
        return spawnLocation;
    }

    public void setSpawnLocation(Vector3f spawnLocation){
        this.spawnLocation = spawnLocation;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public HashMap<Integer, Vector3f> getPlayerLocations() {
        return playerLocations;
    }

    public void setPlayerLocations(HashMap<Integer, Vector3f> playerLocations) {
        this.playerLocations = playerLocations;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public int getClientIndex() {
        return clientIndex;
    }

    public void setClientIndex(int clientIndex) {
        this.clientIndex = clientIndex;
    }
}
