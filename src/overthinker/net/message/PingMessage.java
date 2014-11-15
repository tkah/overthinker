package overthinker.net.message;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 11/11/2014.
 */
@Serializable
public class PingMessage extends AbstractMessage {
    private String hello;       // custom message data
    public PingMessage() {}    // empty constructor
    public PingMessage(String s) { hello = s; } // custom constructor
    public String getMessage(){
        return hello;
    }
}