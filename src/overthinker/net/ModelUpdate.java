package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by Peter on 11/13/2014.
 */

@Serializable
public class ModelUpdate extends AbstractMessage {
    public long version;
    private HashMap<Integer, Vector3f> playerLocations;
    private HashMap<Integer, Boolean> playerAlive;

    public HashMap<Integer, Vector3f> getPlayerLocations() {
        return playerLocations;
    }

    public void setPlayerLocations(HashMap<Integer, Vector3f> playerLocations) {
        this.playerLocations = playerLocations;
    }

    public HashMap<Integer, Boolean> getPlayerAlive() {
        return playerAlive;
    }

    public void setPlayerAlive(HashMap<Integer, Boolean> playerAlive) {
        this.playerAlive = playerAlive;
    }
}
