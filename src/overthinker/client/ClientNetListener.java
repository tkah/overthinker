package overthinker.client;

import overthinker.Globals;
import overthinker.Model;
import overthinker.net.message.ModelUpdate;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 * Created by Peter on 11/11/2014.
 */
public class ClientNetListener implements MessageListener<Client> {
    private ClientMain clientMain;

    public ClientNetListener(ClientMain client)
    {
        this.clientMain = client;
    }

    public void messageReceived(Client source, Message message) {
        if (message instanceof ModelUpdate){
            if(Globals.DEBUG)System.out.println("Received new model");
            if(clientMain.getLocalModel() == null)clientMain.setLocalModel(new Model());
            clientMain.getLocalModel().update((ModelUpdate)message);
        }
    }
}