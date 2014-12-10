package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * This class is used to request a new client server connection.
 *
 * @author Peter, Josh, Sid, Derek, and Torran.
 *
 */
@Serializable
public class NewClientRequest extends AbstractMessage {

    private boolean isEEG = false;

    /**
     * Empty constructor used by Serializable.
     */
    public NewClientRequest() {}

    /**
     * Sets if the requesting client is a Overthinker.
     *
     * @return - is client Overthinker.
     */
    public boolean isEEG() {
        return isEEG;
    }

    /**
     * Sets if the requesting client is a Overthinker.
     *
     * @param isEEG - if client is a Overthinker.
     */
    public void setEEG(boolean isEEG) {
        this.isEEG = isEEG;
    }
}