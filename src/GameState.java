import java.util.ArrayList;

/**
 * Created by sidholman on 11/11/14
 */


//Every time the model updates, increment timestamp and broadcast update to all clients.
    //Create method that takes the GameData held by a client, and compares it to what
    //the Model is--sending only info tha has changed.

    //Create method that will send all the model data, for initial connection
public class GameState {

    private static int distortionValue = 0;
    private static int gameMap = 0;
    private static int gameMode = 0;
    private static ArrayList<float[]> playerLocations;
    private static int playerCount = 0;

    private static ArrayList<float[]> resourceLocations;
    private static int resourceCount = 0;

    private static ArrayList<float[]> obstacleLocations;
    private static int obstacleCount = 0;



    public static void GameState() {
    }

    public static void init() {
        playerLocations = new ArrayList<float[]>(playerCount);
        resourceLocations = new ArrayList<float[]>(resourceCount);
        obstacleLocations = new ArrayList<float[]>(obstacleCount);
    }

    public static synchronized void setDistortionValue(int val) {
        distortionValue = val;
    }

    public static int getDistortionValue() {
        return distortionValue;
    }

    public static synchronized void setClientPosition(int playerNumber, float[] position) {
        float[] toSet = playerLocations.get(playerNumber);
        for (int i = 0; i < 3; i++)
            toSet[i] = position[i];
    }

    public static float[] getClientPosition(int playerNumber) {
        return playerLocations.get(playerNumber);
    }


    public static ArrayList<float[]> getResourceLocations() { return resourceLocations; }

    public static float[] getResourceLocation(int index) {return resourceLocations.get(index); }

    public static void setResourceLocations(ArrayList<float[]> val) { resourceLocations = val; }


    private static ArrayList<float[]> getObstacleLocations() { return obstacleLocations; }

    private static float[] getObstacleLocation(int index) { return obstacleLocations.get(index); }

    private static void setObstacleLocations(ArrayList<float[]> val) { obstacleLocations = val;}

}
