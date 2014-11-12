package model;

import java.util.ArrayList;

/**
 * Created by sidholman on 11/11/14.
 */
public class Model {

    private static int distortionValue = 0;
    private static int gameMode = 0;
    private static ArrayList<float[]> playerLocations;
    private static int playerCount = 0;

    private static ArrayList<float[]> resourceLocations;
    private static int resourceCount = 0;

    private static ArrayList<float[]> obstacleLocations;
    private static int obstacleCount = 0;



    public static void Model() {
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
}
