package overthinker.server;

import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;

import java.util.HashMap;

/**
 * Created by Peter on 11/25/2014.
 */
public class ServerModel {
    private HashMap<HostedConnection, Vector3f> playerLocations = new HashMap<HostedConnection, Vector3f>();
    public long version = 0L;

    public ServerModel() {
        version = 0L;
    }

    public HashMap<HostedConnection, Vector3f> getPlayerLocations()
    {
        return playerLocations;
    }
}
