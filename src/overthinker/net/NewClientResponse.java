package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import overthinker.levels.LevelType;

import java.awt.*;

/**
 * Created by Peter on 11/24/2014.
 */
@Serializable
public class NewClientResponse extends AbstractMessage {
    private Vector3f spawnLocation;
    private LevelType levelType;

    private boolean connected;

    public NewClientResponse() {}

    public Vector3f getSpawnLocation()
    {
        return spawnLocation;
    }

    public void setSpawnLocation(Vector3f spawnLocation){
        this.spawnLocation = spawnLocation;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public void setLevelType(LevelType levelType) {
        this.levelType = levelType;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
