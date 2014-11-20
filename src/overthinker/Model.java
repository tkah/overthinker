package overthinker;

import overthinker.net.message.ModelUpdate;

import java.util.ArrayList;

/**
 * Created by sidholman on 11/11/14, probably needlessly.
 */


//Every time the model updates, increment timestamp and broadcast update to all clients.
//Create method that takes the GameData held by a client, and compares it to what
//the Model is--sending only info tha has changed.

//Create method that will send all the model data, for initial connection
public class Model {

    private String heightMapLocation;
    private String textureMapLocation;

    private int distortionValue;
    private float waterHeight;

    private int gameMap = 0;
    private int gameMode = 0;

    private ArrayList<float[]> spawnPoints = new ArrayList<float[]>();
    private ArrayList<float[]> playerLocations = new ArrayList<float[]>();
    private ArrayList<float[]> resourceLocations = new ArrayList<float[]>();
    private ArrayList<float[]> obstacleLocations = new ArrayList<float[]>();


    public Model(Level level){
        this.heightMapLocation = level.getHeightMapLocation();
        this.textureMapLocation = level.getTextureMapLocation();
        this.distortionValue = level.getInitialDistortionValue();
        this.waterHeight = level.getInitialWaterHeight();
        this.spawnPoints = level.getInitialSpawnPoints();
        this.resourceLocations = level.getInitialResourceLocations();
        this.obstacleLocations = level.getInitialObstacleLocations();
    }
    public Model() {
    }
    public ModelUpdate toModelUpdate()
    {
        ModelUpdate modelUpdate = new ModelUpdate();
        modelUpdate.setSpawnPoints(arrayListTo2dArray(spawnPoints));
        modelUpdate.setPlayerLocations(arrayListTo2dArray(playerLocations));
        modelUpdate.setResourceLocations(arrayListTo2dArray(resourceLocations));
        modelUpdate.setObstacleLocations(arrayListTo2dArray(obstacleLocations));
        modelUpdate.setHeightMapLocation(heightMapLocation);
        modelUpdate.setTextureMapLocation(textureMapLocation);
        modelUpdate.setDistortionValue(distortionValue);
        modelUpdate.setWaterHeight(waterHeight);
        return modelUpdate;
    }

    private float[][] arrayListTo2dArray(ArrayList<float[]> arrayList){
        if(arrayList == null || arrayList.isEmpty()) return null;
        float[][] array = new float[arrayList.size()][arrayList.get(0).length];
        for(int i = 0; i < arrayList.size(); i++)
        {
            for(int j = 0; j < arrayList.get(0).length; j++)
            {
                array[i][j] = arrayList.get(i)[j];
            }
        }
        return array;
    }

    public synchronized void setDistortionValue(int val) {
        distortionValue = val;
    }

    public int getDistortionValue() {
        return distortionValue;
    }

    public synchronized void setWaterHeight(float waterHeight) {
        this.waterHeight = waterHeight;
    }

    public float getWaterHeight() {
        return waterHeight;
    }

    public void setGameMap(int val) {
        gameMap = val;
    }
    public int getGameMap() {
        return gameMap;
    }


    public void setGameMode(int val) {
        gameMode = val;
    }
    public int getGameMode() { return gameMode; }


    public synchronized void setClientPosition(int playerNumber, float[] position) {
        float[] toSet = playerLocations.get(playerNumber);
        for (int i = 0; i < 3; i++)
            toSet[i] = position[i];
    }

    public float[] getClientPosition(int playerNumber) {
        return playerLocations.get(playerNumber);
    }


    public ArrayList<float[]> getResourceLocations() { return resourceLocations; }

    public float[] getResourceLocation(int index) {return resourceLocations.get(index); }

    public void setResourceLocations(ArrayList<float[]> val) { resourceLocations = val; }


    private ArrayList<float[]> getObstacleLocations() { return obstacleLocations; }

    private float[] getObstacleLocation(int index) { return obstacleLocations.get(index); }

    private void setObstacleLocations(ArrayList<float[]> val) { obstacleLocations = val;}

    public void update(ModelUpdate activeModel) {
        if(activeModel.getHeightMapLocation()!=null) heightMapLocation = activeModel.getHeightMapLocation();
        if(activeModel.getTextureMapLocation()!=null) textureMapLocation = activeModel.getTextureMapLocation();
        if(activeModel.getDistortionValue()!= -1) distortionValue = activeModel.getDistortionValue();
        if(activeModel.getWaterHeight()!= -1) waterHeight = activeModel.getWaterHeight();
        if(activeModel.getSpawnPoints()!= null)
        {
            spawnPoints.clear();
            for(float[] point : activeModel.getSpawnPoints()) spawnPoints.add(point);
        }
        if(activeModel.getPlayerLocations()!= null)
        {
            playerLocations.clear();
            for(float[] point : activeModel.getPlayerLocations()) playerLocations.add(point);
        }
        if(activeModel.getResourceLocations()!= null)
        {
            resourceLocations.clear();
            for(float[] point : activeModel.getResourceLocations()) resourceLocations.add(point);
        }
        if(activeModel.getObstacleLocations()!=null)
        {
            obstacleLocations.clear();
            for(float[] point : activeModel.getObstacleLocations()) obstacleLocations.add(point);
        }
    }
}

