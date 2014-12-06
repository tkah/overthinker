package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 12/5/2014.
 */
@Serializable
public class ChangeWaterRateRequest extends AbstractMessage {
    private float waterRate;

    public ChangeWaterRateRequest() {}

    public float getWaterRate() {
        return waterRate;
    }

    public void setWaterRate(float waterRate) {
        this.waterRate = waterRate;
    }
}
