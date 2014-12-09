package overthinker.server;

import com.jme3.network.*;
import overthinker.net.*;

/**
 * This class is used by the server to listen for requests from clients on the network.
 *
 * @author Peter, Torran, Derek, Sid, and Josh.
 */
public class ServerNetListener implements MessageListener<HostedConnection> {
    private ServerMain server;

    /**
     * Constructs a new networking listener and attaches it to a given server.
     *
     * @param server - game server using this networking listener.
     */
    public ServerNetListener(ServerMain server){
        this.server = server;
    }

    /**
     * Landing point of client communications through the networking. Used to determine what type of request has been
     * given, then passes the request to the appropriate server method.
     *
     * @param source - Client giving a new request.
     * @param message - Message of the client giving the request.
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