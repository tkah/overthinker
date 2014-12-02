package overthinker.server;

import overthinker.net.HelloMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerListener implements MessageListener<HostedConnection> {
    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof HelloMessage) {
            // do something with the message
            HelloMessage helloMessage = (HelloMessage) message;
            System.out.println("Server received '" + helloMessage.getMessage() + "' from overthinker.client #" + source.getId());
        } // else....
    }
}