package overthinker.net.message;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 11/13/2014.
 */

@Serializable
public class ModelUpdate extends AbstractMessage {
    private String heightMapLocation;
    private String textureMapLocation;

    private int distortionValue = -1;
    private float waterHeight = -1;

    private float[][] spawnPoints;
    private float[][] playerLocations;
    private float[][] resourceLocations;
    private float[][] obstacleLocations;

    private long version;

    public ModelUpdate(){};

    public String getHeightMapLocation() {
        return heightMapLocation;
    }


    public void setHeightMapLocation(String heightMapLocation) {
        this.heightMapLocation = heightMapLocation;
    }

    public String getTextureMapLocation() {
        return textureMapLocation;
    }

    public void setTextureMapLocation(String textureMapLocation) {
        this.textureMapLocation = textureMapLocation;
    }

    public int getDistortionValue() {
        return distortionValue;
    }

    public void setDistortionValue(int distortionValue) {
        this.distortionValue = distortionValue;
    }

    public float getWaterHeight() {
        return waterHeight;
    }

    public void setWaterHeight(float waterHeight) {
        this.waterHeight = waterHeight;
    }

    public float[][] getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(float[][] spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public float[][] getPlayerLocations() {
        return playerLocations;
    }

    public void setPlayerLocations(float[][] playerLocations) {
        this.playerLocations = playerLocations;
    }

    public float[][] getResourceLocations() {
        return resourceLocations;
    }

    public void setResourceLocations(float[][] resourceLocations) {
        this.resourceLocations = resourceLocations;
    }

    public float[][] getObstacleLocations() {
        return obstacleLocations;
    }

    public void setObstacleLocations(float[][] obstacleLocations) {
        this.obstacleLocations = obstacleLocations;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
