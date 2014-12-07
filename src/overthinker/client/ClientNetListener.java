package overthinker.client;

import com.jme3.network.MessageListener;
import overthinker.net.ModelUpdate;
import com.jme3.network.Client;
import com.jme3.network.Message;
import overthinker.net.NewClientResponse;

public class ClientNetListener implements MessageListener<Client> {
    private GamePlayAppState clientMain;

    public ClientNetListener(GamePlayAppState client)
    {
        this.clientMain = client;
    }

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