package overthinker.net;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import java.util.HashMap;

/**
 * This class is used by the server to communicated model changes to all connected clients.
 *
 * @author Peter, Josh, Derek, Sid, and Torran.
 */
@Serializable
public class ModelUpdate extends AbstractMessage {
    public long version;
    private HashMap<Integer, Vector3f> playerLocations;
    private HashMap<Integer, Boolean> playerAlive;
    private boolean gravityRight, gravityLeft, gravityForward, gravityBack;
    private float waterRate;

    /**
     * Gets all client locations.
     *
     * @return - HashMap of client index to client locations
     */
    public HashMap<Integer, Vector3f> getPlayerLocations() {
        return playerLocations;
    }

    /**
     * Sets all client locations.
     *
     * @param playerLocations - new HashMap of client index to client locations.
     */
    public void setPlayerLocations(HashMap<Integer, Vector3f> playerLocations) {
        this.playerLocations = playerLocations;
    }

    /**
     * Gets the life states of all clients.
     *
     * @return - HashMap of client index to life state.
     */
    public HashMap<Integer, Boolean> getPlayerAlive() {
        return playerAlive;
    }

    /**
     * Sets the life states of all clients.
     *
     * @param playerAlive - new HashMap of client index to life state.
     */
    public void setPlayerAlive(HashMap<Integer, Boolean> playerAlive) {
        this.playerAlive = playerAlive;
    }

    /**
     * Gets if the gravity tilt is to the right.
     *
     * @return - is gravity tilt to the right.
     */
    public boolean isGravityRight() {
        return gravityRight;
    }

    /**
     * Sets if the gravity tilt is to the right.
     *
     * @param gravityRight - if gravity tilt is to the right.
     */
    public void setGravityRight(boolean gravityRight) {
        this.gravityRight = gravityRight;
    }

    /**
     * Gets if the gravity tilt is to the left.
     *
     * @return - is gravity tilt to the left.
     */
    public boolean isGravityLeft() {
        return gravityLeft;
    }

    /**
     * Sets if the gravity tilt is to the left.
     *
     * @param gravityLeft - if gravity tilt is to the left.
     */
    public void setGravityLeft(boolean gravityLeft) {
        this.gravityLeft = gravityLeft;
    }

    /**
     * Gets if the gravity tilt is forward.
     *
     * @return - is gravity tilt forward.
     */
    public boolean isGravityForward() {
        return gravityForward;
    }

    /**
     * Sets if the gravity tilt is forward.
     *
     * @param gravityForward - if gravity tilt is forward.
     */
    public void setGravityForward(boolean gravityForward) {
        this.gravityForward = gravityForward;
    }

    /**
     * Gets if the gravity tilt is to the back.
     *
     * @return - is gravity tilt to the back
     */
    public boolean isGravityBack() {
        return gravityBack;
    }

    /**
     * Sets if the gravity tilt is to the back.
     *
     * @param gravityBack - if gravity tilt is to the back.
     */
    public void setGravityBack(boolean gravityBack) {
        this.gravityBack = gravityBack;
    }

    /**
     * Gets the water rate.
     *
     * @return - water rate.
     */
    public float getWaterRate() {
        return waterRate;
    }

    /**
     * Sets the water rate.
     *
     * @param waterRate - new water rate.
     */
    public void setWaterRate(float waterRate) {
        this.waterRate = waterRate;
    }
}
