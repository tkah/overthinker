package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 11/11/2014.
 */
@Serializable
public class NewClientRequest extends AbstractMessage {

    public NewClientRequest() {}
}