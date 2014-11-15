package overthinker.net.message;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import overthinker.client.ClientGameData;

/**
 * Created by Peter on 11/12/2014.
 */
@Serializable
public class NewClientResponseMessage extends AbstractMessage {
    public boolean clientConnection;

    public float lightDirX;
    public float lightDirY;
    public float lightDirZ;

    public float waterHeight;

    public NewClientResponseMessage() {
    }
}
