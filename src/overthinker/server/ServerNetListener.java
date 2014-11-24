package overthinker.server;

import com.jme3.network.*;
import overthinker.client.Globals;
import overthinker.net.message.ModelChangeRequest;
import overthinker.net.message.NewClientRequest;
import overthinker.net.message.NewClientResponse;
import overthinker.server.gui.ServerMain;

import java.awt.*;

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

            server.getClients().add(source);
            server.getVersions().put(source, 0f);

            NewClientResponse response = new NewClientResponse();
            response.setSpawnPoint(getRandomSpawnPoint());
            server.getNetServer().broadcast(Filters.in(source),server.getActiveModel().toModelUpdate());

        } else if (message instanceof ModelChangeRequest) {

        }
    }

    private Point getRandomSpawnPoint() {
        return null;
    }

}