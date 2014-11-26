package overthinker.client;

import com.jme3.network.MessageListener;
import overthinker.levels.maze1.Maze1;
import overthinker.net.ModelUpdate;
import com.jme3.network.Client;
import com.jme3.network.Message;
import overthinker.net.NewClientResponse;

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
            System.out.println(((ModelUpdate) message).getPlayerLocations());
        } else if (message instanceof NewClientResponse) {
            if(Globals.DEBUG)System.out.println("Received new client response");
            switch (((NewClientResponse) message).getLevelType())
            {
                case MAZE1:
                    clientMain.setLevel(new Maze1());
            }
            clientMain.getLevel().setSpawnX(((NewClientResponse) message).getSpawnX());
            clientMain.getLevel().setSpawnY(((NewClientResponse) message).getSpawnY());
            clientMain.getLevel().setSpawnZ(((NewClientResponse) message).getSpawnZ());
        }
    }
}