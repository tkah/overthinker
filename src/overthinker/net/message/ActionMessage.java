package overthinker.net.message;

import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 11/12/2014.
 */
@Serializable
public class ActionMessage extends AbstractMessage {
    private ActionType type;
    private Client client;

    public enum ActionType {
        FORWARD,BACKWARD,LEFT,RIGHT,JUMP
    };

    public ActionMessage() {
        type = null;
        client = null;
    }
    public ActionMessage(ActionType type, Client client)
    {
        this.type = type;
        this.client = client;
    }
    public ActionType getType(){
        return type;
    }
    public Client getClient(){
        return client;
    }
}
