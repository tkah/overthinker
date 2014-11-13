package overthinker.client;

import overthinker.net.message.PingMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 * Created by Peter on 11/11/2014.
 */
public class ClientNetListener implements MessageListener<Client> {
    public void messageReceived(Client source, Message message) {
        if (message instanceof PingMessage) {
            // do something with the message
            PingMessage pingMessage = (PingMessage) message;
            System.out.println("Client #" + source.getId() + " received: '" + pingMessage.getMessage() + "'");
        } // else...
    }
}