package overthinker.server;

import com.jme3.math.Vector3f;

import java.util.HashMap;

/**
 * Used for all game data managed by the server.
 *
 * @author Peter, Sid, Derek, Josh and Sid.
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

    /**
     * Empty constructor
     */
    public ServerModel() {}

    /**
     * Constructor used to initialize the client locations and model version.
     *
     * @param playerLocations - new client locations.
     * @param version - new model version.
     */
    public ServerModel(HashMap<Integer, Vector3f> playerLocations, long version)
    {
        this.playerLocations = playerLocations;
        this.version = version;
    }

    /**
     * Gets client locations.
     *
     * @return - map of client index to locations.
     */
    public HashMap<Integer, Vector3f> getPlayerLocations()
    {
        return playerLocations;
    }

    /**
     * Sets client locations.
     *
     * @param playerLocations - new client locations.
     */
    public synchronized void setPlayerLocations(HashMap<Integer, Vector3f> playerLocations)
    {
        this.playerLocations = playerLocations;
    }

    /**
     * Gets the player life states.
     *
     * @return - map of client index to life state.
     */
    public HashMap<Integer, Boolean> getPlayerAlive()
    {
        return playerAlive;
    }

    /**
     * Sets the player life states.
     *
     * @param playerAlive - new map of client index to life states.
     */
    public synchronized void setPlayerAlive(HashMap<Integer, Boolean> playerAlive)
    {
        this.playerAlive = playerAlive;
    }

    /**
     * Gets this models version
     *
     * @return - model version.
     */
    public long getVersion() {
        return version;
    }

    /**
     * Sets this models version.
     *
     * @param version - new model version.
     */
    public synchronized void setVersion(long version) {
        this.version = version;
    }

    /**
     * Gets if gravity is right.
     *
     * @return - is gravity right.
     */
    public boolean isGravityRight() {
        return gravityRight;
    }

    /**
     * Sets if gravity is right.
     *
     * @param gravityRight - if gravity is right.
     */
    public synchronized void setGravityRight(boolean gravityRight) {
        this.gravityRight = gravityRight;
    }

    /**
     * Gets if gravity is right.
     *
     * @return - is gravity right.
     */
    public boolean isGravityLeft() {
        return gravityLeft;
    }

    /**
     * Sets if gravity is left.
     *
     * @param gravityLeft - is gravity left.
     */
    public synchronized void setGravityLeft(boolean gravityLeft) {
        this.gravityLeft = gravityLeft;
    }

    /**
     * Get is gravity is forward.
     *
     * @return - is gravity forward.
     */
    public boolean isGravityForward() {
        return gravityForward;
    }

    /**
     * Sets if gravity is forward.
     *
     * @param gravityForward - if gravity is forward.
     */
    public synchronized void setGravityForward(boolean gravityForward) {
        this.gravityForward = gravityForward;
    }

    /**
     * Gets is gravity is back.
     *
     * @return - if gravity is back.
     */
    public boolean isGravityBack() {
        return gravityBack;
    }

    /**
     * Sets if gravity is back.
     *
     * @param gravityBack - if gravity is back.
     */
    public synchronized void setGravityBack(boolean gravityBack) {
        this.gravityBack = gravityBack;
    }

    /**
     * Gets water rate.
     *
     * @return - water rate.
     */
    public float getWaterRate() {
        return waterRate;
    }

    /**
     * Sets water rate.
     *
     * @param waterRate
     */
    public synchronized void setWaterRate(float waterRate) {
        this.waterRate = waterRate;
    }
}
