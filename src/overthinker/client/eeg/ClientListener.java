package overthinker.client.eeg;

import overthinker.net.HelloMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 * Created by Peter on 11/11/2014.
 */
public class ClientListener implements MessageListener<Client> {
    public void messageReceived(Client source, Message message) {
        if (message instanceof HelloMessage) {
            // do something with the message
            HelloMessage helloMessage = (HelloMessage) message;
            System.out.println("Client #" + source.getId() + " received: '" + helloMessage.getMessage() + "'");
        } // else...
    }
}