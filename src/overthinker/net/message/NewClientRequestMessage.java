package overthinker.net.message;

import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 11/12/2014.
 */
@Serializable
public class NewClientRequestMessage extends AbstractMessage {

    public NewClientRequestMessage()
    {
    }

}
