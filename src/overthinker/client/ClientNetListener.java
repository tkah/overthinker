package overthinker.client;

import com.jme3.math.Vector3f;
import overthinker.net.message.NewClientResponseMessage;
import overthinker.net.message.PingMessage;
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
        if (message instanceof PingMessage) {
            // do something with the message
            PingMessage pingMessage = (PingMessage) message;
            System.out.println("Client #" + source.getId() + " received: '" + pingMessage.getMessage() + "'");
        } else if( message instanceof NewClientResponseMessage){
            System.out.println("Received opening response");
            NewClientResponseMessage responseMessage = (NewClientResponseMessage) message;
            ClientGameData clientGameData = new ClientGameData();
            clientGameData.setLightDir(new Vector3f(responseMessage.lightDirX, responseMessage.lightDirY,
                    responseMessage.lightDirZ));
            clientGameData.setWaterHeight(responseMessage.waterHeight);
            clientMain.setClientGameData(clientGameData);
        }
    }
}