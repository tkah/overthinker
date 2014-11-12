package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 11/11/2014.
 */
@Serializable
public class HelloMessage extends AbstractMessage {
    private String hello;       // custom message data
    public HelloMessage() {}    // empty constructor
    public HelloMessage(String s) { hello = s; } // custom constructor
    public String getMessage(){
        return hello;
    }
}