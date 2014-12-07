package overthinker.server;

import com.jme3.network.*;
import overthinker.client.Globals;
import overthinker.net.*;

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
        } else if (message instanceof ChangePlayerLocationRequest) {
            server.updatePlayerLocation(source, ((ChangePlayerLocationRequest) message).getPlayerLocation());
        } else if (message instanceof PlayerDeathRequest) {
            server.handlePlayerDeath(source);
        } else if (message instanceof ChangeMapTiltRequest) {
            server.updateMapTilt((ChangeMapTiltRequest) message);
        } else if (message instanceof ChangeWaterRateRequest) {
            server.updateWaterRate((ChangeWaterRateRequest) message);
        }
    }
}