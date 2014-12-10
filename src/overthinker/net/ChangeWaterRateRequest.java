package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * This class is used to send a water rate update over the network. Only used by the Otherthinker.
 *
 * @author Peter, Josh, Sid, Derek, and Torran.
 */
@Serializable
public class ChangeWaterRateRequest extends AbstractMessage {
    private float waterRate;

    /**
     * Empty constructor required by Serializable.
     */
    public ChangeWaterRateRequest() {}

    /**
     * Gets the new water rate.
     *
     * @return - water rate.
     */
    public float getWaterRate() {
        return waterRate;
    }

    /**
     * Sets the new water rate.
     *
     * @param waterRate - new water rate.
     */
    public void setWaterRate(float waterRate) {
        this.waterRate = waterRate;
    }
}
