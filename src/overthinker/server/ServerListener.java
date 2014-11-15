package overthinker.server;

import com.jme3.math.Vector3f;
import com.jme3.network.*;
import overthinker.net.message.NewClientRequestMessage;
import overthinker.net.message.NewClientResponseMessage;
import overthinker.server.gui.ServerMain;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerListener implements MessageListener<HostedConnection> {
    private ServerMain server;

    public ServerListener(ServerMain server){
        this.server = server;
    }

    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof NewClientRequestMessage) {
            sendNewClientResponse(source);
        }
    }

    private void sendNewClientResponse(HostedConnection source) {
        NewClientResponseMessage responseMessage = new NewClientResponseMessage();

        responseMessage.lightDirX = -4.9f;
        responseMessage.lightDirY = -1.3f;
        responseMessage.lightDirZ = 5.9f;
        responseMessage.waterHeight = 20.0f;
        server.getNetServer().broadcast(Filters.in(source), new NewClientResponseMessage());
    }

}