package overthinker.server;

import com.jme3.network.*;
import overthinker.client.Globals;
import overthinker.net.PlayerLocationChangeRequest;
import overthinker.net.NewClientRequest;

/**
 * Created by Peter on 11/11/2014.
 */
public class ServerNetListener implements MessageListener<HostedConnection> {
    private ServerMain server;

    public ServerNetListener(ServerMain server){
        this.server = server;
    }

    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof NewClientRequest) {
            if(Globals.DEBUG)System.out.println("New client request from: " + source.getAddress());
            server.initClient(source);
        } else if (message instanceof PlayerLocationChangeRequest) {
            server.updateModel(source, ((PlayerLocationChangeRequest) message).getPlayerLocation());
        }
    }
}