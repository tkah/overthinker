package overthinker.levels.maze1;

import overthinker.levels.Level;

import java.util.ArrayList;

/**
 * Created by Peter on 11/15/2014.
 */
public class Maze1 extends Level {

    public static final int SPHERE_RESOURCE_COUNT = 100;
    public static final float SPHERE_RESOURCE_RADIUS = 1.0f;
    public static final float PLAYER_SPHERE_START_RADIUS = 2.0f;
    public static final float MAP_TILT_RATE = 0.008f;
    public static final float WATER_HEIGHT_DEFAULT_RATE = 0.005f;
    public static final float WATER_HEIGHT_PLAYER_RATE = 0.001f;

    private String heightMapLocation;
    private String textureMapLocation;
    private int initialDistortionValue;
    private float initialWaterHeight;

    private ArrayList<float[]> initialSpawnPoints = new ArrayList<float[]>();
    private ArrayList<float[]> initialResourceLocations = new ArrayList<float[]>();
    private ArrayList<float[]> initialObstacleLocations = new ArrayList<float[]>();

    public Maze1(){
        heightMapLocation="overthinker/levels/maze1/maze1.jpg";
        textureMapLocation="overthinker/levels/maze1/maze1color.png";
        initialDistortionValue = 0;
        initialWaterHeight = 20.0f;
        initialSpawnPoints.add(new float[]{-340, 50, -400});

    }

    @Override
    public String getHeightMapLocation() {
        return heightMapLocation;
    }

    @Override
    public String getTextureMapLocation() {
        return textureMapLocation;
    }

    @Override
    public int getInitialDistortionValue() {
        return initialDistortionValue;
    }

    @Override
    public float getInitialWaterHeight() {
        return initialWaterHeight;
    }

    @Override
    public ArrayList<float[]> getInitialSpawnPoints() {
        return initialSpawnPoints;
    }

    @Override
    public ArrayList<float[]> getInitialResourceLocations() {
        return initialResourceLocations;
    }

    @Override
    public ArrayList<float[]> getInitialObstacleLocations() {
        return initialObstacleLocations;
    }
}
