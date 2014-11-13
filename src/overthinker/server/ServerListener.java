package overthinker.server;

import overthinker.net.message.ActionMessage;
import overthinker.net.message.PingMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerListener implements MessageListener<HostedConnection> {
    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof PingMessage) {
            // do something with the message
            PingMessage pingMessage = (PingMessage) message;
            System.out.println("Server received '" + pingMessage.getMessage() + "' from overthinker.client #" + source.getId());
        } else if (message instanceof ActionMessage) {
            // Handle an action!
            ActionMessage actionMessage = (ActionMessage) message;

        }
    }


    private void handleAction(ActionMessage actionMessage){
        System.out.println("Received action " + actionMessage.getType() + " From " + actionMessage.getClient());
    }

}