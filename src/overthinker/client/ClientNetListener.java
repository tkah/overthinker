package overthinker.client;

import com.jme3.network.MessageListener;
import overthinker.net.ModelUpdate;
import com.jme3.network.Client;
import com.jme3.network.Message;
import overthinker.net.NewClientResponse;

/**
 * This class is the network listener for a given client. This listener looks for communications from the server as
 * well as sorts the message types from the sever.
 *
 * @author Peter, Derek, Sid, Josh and Torran
 */
public class ClientNetListener implements MessageListener<Client> {
    private GamePlayAppState clientMain;

    /**
     * Constructs a new network listener, attaching itself to the given client.
     * @param client
     */
    public ClientNetListener(GamePlayAppState client)
    {
        this.clientMain = client;
    }

    /**
     * Landing point of messages from the server.
     *
     * @param source - The server
     * @param message - The message from the server.
     */
    public void messageReceived(Client source, Message message) {
        if (message instanceof ModelUpdate) {
            clientMain.updateModel((ModelUpdate) message);
        } else if (message instanceof NewClientResponse) {
            if(Globals.DEBUG)System.out.println("Received new client response");
            if(((NewClientResponse) message).isConnected()) {
                clientMain.handleNewClientResponse((NewClientResponse) message);
            }
        }
    }
}