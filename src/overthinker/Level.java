package overthinker;

import java.util.ArrayList;

/**
 * Created by Peter on 11/15/2014.
 */
public abstract class Level {

    public abstract String getHeightMapLocation();
    public abstract String getTextureMapLocation();
    public abstract int getInitialDistortionValue();
    public abstract float getInitialWaterHeight();
    public abstract ArrayList<float[]> getInitialSpawnPoints();
    public abstract ArrayList<float[]> getInitialResourceLocations();
    public abstract ArrayList<float[]> getInitialObstacleLocations();
}
