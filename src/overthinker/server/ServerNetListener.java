package overthinker.server;

import com.jme3.network.*;
import overthinker.net.*;

/**
 * Used by the server to listen on the network for client communication.
 */
public class ServerNetListener implements MessageListener<HostedConnection> {
    private ServerMain server;

    /**
     * Constructs a network listener tied to the given server.
     *
     * @param server - linked server.
     */
    public ServerNetListener(ServerMain server){
        this.server = server;
    }

    /**
     * Used to filter client to server communication and send messages to specific method on the server.
     *
     * @param source - Client sending a message.
     * @param message - Message the client send.
     */
    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof NewClientRequest) {
            server.initClient(source, (NewClientRequest) message);
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