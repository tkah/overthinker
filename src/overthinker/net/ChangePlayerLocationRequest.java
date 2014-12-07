package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 11/13/2014.
 */
@Serializable
public class ChangePlayerLocationRequest extends AbstractMessage{
    private Vector3f playerLocation;

    public ChangePlayerLocationRequest() {}

    public Vector3f getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(Vector3f playerLocation) {
        this.playerLocation = playerLocation;
    }
}