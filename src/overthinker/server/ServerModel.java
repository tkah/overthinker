package overthinker.server;

import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import overthinker.levels.Level;
import overthinker.levels.LevelType;
import overthinker.levels.maze1.Maze1;

import java.util.HashMap;

/**
 * Created by Peter on 11/25/2014.
 */
public class ServerModel {
    private HashMap<Integer, Vector3f> playerLocations = new HashMap<Integer, Vector3f>();
    public long version = 0L;

    public ServerModel() {
        version = 0L;
    }

    public HashMap<Integer, Vector3f> getPlayerLocations()
    {
        return playerLocations;
    }


}
