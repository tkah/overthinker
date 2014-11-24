package overthinker.net.message;

import com.jme3.network.AbstractMessage;

import java.awt.*;

/**
 * Created by Peter on 11/24/2014.
 */
public class NewClientResponse extends AbstractMessage {
    private int spawnX, spawnY;

    public NewClientResponse() {}

    public int getSpawnX() {
        return spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnPoint(Point point){
        spawnX = point.x;
        spawnY = point.y;
    }
}
