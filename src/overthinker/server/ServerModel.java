package overthinker.server;

import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import overthinker.levels.Level;
import overthinker.levels.LevelType;
import overthinker.levels.maze1.Maze1;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Peter on 11/25/2014.
 */
public class ServerModel {
    private HashMap<Integer, Vector3f> playerLocations = new HashMap<Integer, Vector3f>();
    private HashMap<Integer, Boolean> playerAlive = new HashMap<Integer, Boolean>();

    private long version;

    public ServerModel() {}

    public ServerModel(HashMap<Integer, Vector3f> playerLocations, long version)
    {
        this.playerLocations = playerLocations;
        this.version = version;
    }
    public HashMap<Integer, Vector3f> getPlayerLocations()
    {
        return playerLocations;
    }

    public synchronized void setPlayerLocations(HashMap<Integer, Vector3f> playerLocations)
    {
        this.playerLocations = playerLocations;
    }

    public HashMap<Integer, Boolean> getPlayerAlive()
    {
        return playerAlive;
    }

    public synchronized void setPlayerAlive(HashMap<Integer, Boolean> playerAlive)
    {
        this.playerAlive = playerAlive;
    }

    public long getVersion() {
        return version;
    }

    public synchronized void setVersion(long version) {
        this.version = version;
    }
}
