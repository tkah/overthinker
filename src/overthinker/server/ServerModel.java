package overthinker.server;

import com.jme3.math.Vector3f;

import java.util.HashMap;

/**
 * This class is used to hold game model data that is changed by and sent to all server clients.
 */
public class ServerModel {
    private HashMap<Integer, Vector3f> playerLocations = new HashMap<>();
    private HashMap<Integer, Boolean> playerAlive = new HashMap<>();
    private boolean gravityRight = false;
    private boolean gravityLeft = false;
    private boolean gravityForward = false;
    private boolean gravityBack = false;
    private float waterRate = 0;

    private long version;


    public ServerModel() {

    }

    /**
     * Constructs a new model with given client locations and a starting version number.
     *
     * @param playerLocations - locations of all clients
     * @param version - version of this model
     */

    public ServerModel(HashMap<Integer, Vector3f> playerLocations, long version)
    {
        this.playerLocations = playerLocations;
        this.version = version;
    }

    /**
     * Returns all client locations
     *
     * @return - HashMap of client index to client locations.
     */
    public HashMap<Integer, Vector3f> getPlayerLocations()
    {
        return playerLocations;
    }

    /**
     * Sets all client locations
     *
     * @param playerLocations - HashMap of client index to client locations.
     */
    public synchronized void setPlayerLocations(HashMap<Integer, Vector3f> playerLocations)
    {
        this.playerLocations = playerLocations;
    }

    /**
     * Returns all client life states.
     *
     * @return - HashMap of client index to life state
     */
    public HashMap<Integer, Boolean> getPlayerAlive()
    {
        return playerAlive;
    }

    /**
     * Sets all client life states.
     *
     * @param playerAlive - HashMap of client index to life state.
     */
    public synchronized void setPlayerAlive(HashMap<Integer, Boolean> playerAlive)
    {
        this.playerAlive = playerAlive;
    }

    /**
     * Returns the version of this model
     *
     * @return - version
     */
    public long getVersion() {
        return version;
    }

    /**
     * Sets the version of this model.
     *
     * @param version - new version of this model.
     */
    public synchronized void setVersion(long version) {
        this.version = version;
    }

    /**
     * Gets the right gravity tilt of.
     *
     * @return - If gravity tilt is to the right.
     */
    public boolean isGravityRight() {
        return gravityRight;
    }

    /**
     * Sets if the gravity tilt is to the right.
     *
     * @param gravityRight - right gravity tilt.
     */
    public synchronized void setGravityRight(boolean gravityRight) {
        this.gravityRight = gravityRight;
    }

    /**
     * Gets the left gravity tilt of.
     *
     * @return - If gravity tilt is to the left.
     */
    public boolean isGravityLeft() {
        return gravityLeft;
    }

    /**
     * Sets if the gravity tilt is to the left.
     *
     * @param gravityLeft - left gravity tilt.
     */
    public synchronized void setGravityLeft(boolean gravityLeft) {
        this.gravityLeft = gravityLeft;
    }

    /**
     * Gets the forward gravity tilt of.
     *
     * @return - If gravity tilt is to the forward.
     */
    public boolean isGravityForward() {
        return gravityForward;
    }


    /**
     * Sets if the gravity tilt is to the forward.
     *
     * @param gravityForward - forward gravity tilt.
     */
    public synchronized void setGravityForward(boolean gravityForward) {
        this.gravityForward = gravityForward;
    }

    /**
     * Gets the back gravity tilt of.
     *
     * @return - If gravity tilt is to the back.
     */
    public boolean isGravityBack() {
        return gravityBack;
    }

    /**
     * Sets if the gravity tilt is to the back.
     *
     * @param gravityBack - back gravity tilt.
     */
    public synchronized void setGravityBack(boolean gravityBack) {
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
     * Sets the water rate
     *
     * @param waterRate - new water rate.
     */
    public synchronized void setWaterRate(float waterRate) {
        this.waterRate = waterRate;
    }
}
