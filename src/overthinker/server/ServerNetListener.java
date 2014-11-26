package overthinker.server;

import com.jme3.math.Vector3f;
import com.jme3.network.*;
import overthinker.client.Globals;
import overthinker.levels.LevelType;
import overthinker.net.ModelChangeRequest;
import overthinker.net.NewClientRequest;
import overthinker.net.NewClientResponse;

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
            server.addClient(source);
            server.getClientModelVersions().put(source, 0f);

            initClient(source);

        } else if (message instanceof ModelChangeRequest) {
            server.updateModel(source, ((ModelChangeRequest) message).getPlayerLocation());
        }
    }

    private void initClient(HostedConnection source) {
        NewClientResponse response = new NewClientResponse();
        response.setLevelType(LevelType.MAZE1);
        response.setSpawnPoint(-340, 80, -400);
        server.getNetServer().broadcast(Filters.in(source), response);
        server.getModel().version += 1;
        server.broadcastModelUpdate();
    }


}