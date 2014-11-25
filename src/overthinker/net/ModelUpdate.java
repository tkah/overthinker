package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 11/13/2014.
 */

@Serializable
public class ModelUpdate extends AbstractMessage {
    public long version;
    private Vector3f[] playerLocations;

    public Vector3f[] getPlayerLocations() {
        return playerLocations;
    }

    public void setPlayerLocations(Vector3f[] playerLocations) {
        this.playerLocations = playerLocations;
    }
}
