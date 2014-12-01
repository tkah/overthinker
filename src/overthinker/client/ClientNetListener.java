package overthinker.client;

import com.jme3.math.Vector3f;
import com.jme3.network.MessageListener;
import overthinker.levels.maze1.Maze1;
import overthinker.net.ModelUpdate;
import com.jme3.network.Client;
import com.jme3.network.Message;
import overthinker.net.NewClientResponse;

import javax.print.attribute.standard.MediaSize;
import java.util.HashMap;

public class ClientNetListener implements MessageListener<Client> {
    private ClientMain clientMain;

    public ClientNetListener(ClientMain client)
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