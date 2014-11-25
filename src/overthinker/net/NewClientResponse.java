package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import overthinker.levels.LevelType;

import java.awt.*;

/**
 * Created by Peter on 11/24/2014.
 */
@Serializable
public class NewClientResponse extends AbstractMessage {
    private int spawnX, spawnY, spawnZ;
    private LevelType levelType;

    public NewClientResponse() {}

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public int getSpawnZ() { return spawnZ; }

    public void setSpawnPoint(int x, int y, int z){
        spawnX = x;
        spawnY = y;
        spawnZ = z;
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public void setLevelType(LevelType levelType) {
        this.levelType = levelType;
    }
}
