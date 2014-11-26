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
            HashMap<Integer, Vector3f> playerLocationMap = ((ModelUpdate) message).getPlayerLocations();
            for(Integer playerIndex : playerLocationMap.keySet())
            {
                if(clientMain.getLevel().getOtherPlayers().containsKey(playerIndex))
                {
                    clientMain.getLevel().getOtherPlayers().get(playerIndex).move(playerLocationMap.get(playerIndex));
                }
                else
                {
                    System.out.println("New Other Player");
                    OtherPlayer player = new OtherPlayer(clientMain.getLevel().getPlayer_sphere_start_radius(),
                            playerIndex, playerLocationMap.get(playerIndex), clientMain.getAssetManager());

                    clientMain.createOtherPlayer(player);
                    clientMain.getLevel().getOtherPlayers().put(playerIndex, player);
                }
            }
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