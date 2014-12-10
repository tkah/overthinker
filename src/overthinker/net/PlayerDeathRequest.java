package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * This class is used to send a client death notification over the network.
 *
 * @author Peter, Torran, Derek, Josh and Sid.
 */
@Serializable
public class PlayerDeathRequest extends AbstractMessage {
    /**
     * Empty constructor used by Serializable.
     */
    public PlayerDeathRequest() {}
}
