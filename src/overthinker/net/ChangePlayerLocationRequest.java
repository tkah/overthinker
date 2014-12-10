package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * This class is used to send a player location update over the networking to the server.
 *
 * @author Peter, Derek, Sid, Josh, and Torran.
 */
@Serializable
public class ChangePlayerLocationRequest extends AbstractMessage{
    private Vector3f playerLocation;

    /**
     * Empty constructor required by Serializable.
     */
    public ChangePlayerLocationRequest() {}

    /**
     * Gets the new client location.
     *
     * @return - new client location.
     */
    public Vector3f getPlayerLocation() {
        return playerLocation;
    }

    /**
     * Sets the new client location.
     *
     * @param playerLocation - new client location.
     */
    public void setPlayerLocation(Vector3f playerLocation) {
        this.playerLocation = playerLocation;
    }
}
