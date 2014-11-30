package overthinker.client;

import com.jme3.math.Vector3f;
import com.jme3.network.MessageListener;
import overthinker.levels.maze1.Maze1;
import overthinker.net.ModelUpdate;
import com.jme3.network.Client;
import com.jme3.network.Message;
import overthinker.net.NewClientResponse;

import javax.print.attribute.standard.MediaSize;
import java.util.HashMap;

public class ClientNetListener implements MessageListener<Client> {
    private ClientMain clientMain;

    public ClientNetListener(ClientMain client)
    {
        this.clientMain = client;
    }

    public void messageReceived(Client source, Message message) {
        if (message instanceof ModelUpdate) {
            System.out.println(((ModelUpdate) message).getPlayerLocations());

//            HashMap<Integer, Vector3f> playerLocationMap = ((ModelUpdate) message).getPlayerLocations();
//            for(Integer playerIndex : playerLocationMap.keySet())
//            {
//                clientMain.getLevel().getOtherPlayers().get(playerIndex).move(playerLocationMap.get(playerIndex));
//            }
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