package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import java.util.HashMap;

/**
 * Created by Peter on 11/13/2014.
 */

@Serializable
public class ModelUpdate extends AbstractMessage {
    public long version;
    private HashMap<Integer, Vector3f> playerLocations;
    private HashMap<Integer, Boolean> playerAlive;
    private boolean gravityRight, gravityLeft, gravityForward, gravityBack;
    private float waterRate;

    public HashMap<Integer, Vector3f> getPlayerLocations() {
        return playerLocations;
    }

    public void setPlayerLocations(HashMap<Integer, Vector3f> playerLocations) {
        this.playerLocations = playerLocations;
    }

    public HashMap<Integer, Boolean> getPlayerAlive() {
        return playerAlive;
    }

    public void setPlayerAlive(HashMap<Integer, Boolean> playerAlive) {
        this.playerAlive = playerAlive;
    }

    public boolean isGravityRight() {
        return gravityRight;
    }

    public void setGravityRight(boolean gravityRight) {
        this.gravityRight = gravityRight;
    }

    public boolean isGravityLeft() {
        return gravityLeft;
    }

    public void setGravityLeft(boolean gravityLeft) {
        this.gravityLeft = gravityLeft;
    }

    public boolean isGravityForward() {
        return gravityForward;
    }

    public void setGravityForward(boolean gravityForward) {
        this.gravityForward = gravityForward;
    }

    public boolean isGravityBack() {
        return gravityBack;
    }

    public void setGravityBack(boolean gravityBack) {
        this.gravityBack = gravityBack;
    }

    public float getWaterRate() {
        return waterRate;
    }

    public void setWaterRate(float waterRate) {
        this.waterRate = waterRate;
    }
}
