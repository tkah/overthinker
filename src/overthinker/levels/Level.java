package overthinker.levels;

import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.water.WaterFilter;
import overthinker.client.Globals;
import overthinker.client.SphereResource;
import overthinker.client.OtherPlayer;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Level {

    private LevelType levelType;

    private int sphere_resource_count;
    private int player_count;
    private float sphere_resource_radius;
    private float player_sphere_start_radius;
    private float map_tilt_rate;
    private float water_height_default_rate;
    private float water_height_player_rate;

    private float waterHeight;
    private float verticalAngle;
    private float maxVerticalAngle;
    private float minVerticalAngle;

    private Vector3f lightDir;
    private Vector3f walkDirection;
    private Vector3f camDir;
    private Vector3f camLeft;

    private Node resources;
    private Node playerNode;
    private Node pivot;
    private CameraNode camNode;

    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl playerControl;

    private SphereCollisionShape sphereShape;
    private Sphere playerSphere;
    private Geometry playerG;

    private FilterPostProcessor fpp;
    private WaterFilter water;
    private TerrainQuad terrain;
    private Material mat_terrain;

    private String heightMapLocation;
    private String matTerrainLocation;
    private String matTerrainAlphaTextureLocation;

    private boolean playerNeedsScaling;
    private int scaleStartTime;

    private boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;
    private boolean slowWater = false;
    private boolean mapTiltLeft = false;
    private boolean mapTiltRight = false;
    private boolean mapTiltForward = false;
    private boolean mapTiltBack = false;

    private ArrayList<SphereResource> sphereResource = new ArrayList<>();
    private ArrayList<SphereResource> sphereResourcesToShrink = new ArrayList<>();
    private HashMap<Integer, OtherPlayer> otherPlayers = new HashMap<>();
    private ArrayList<Vector3f> spawnLocations = new ArrayList<>();

    private Quaternion mapTilt = new Quaternion();
    private float rotation;
    private float tiltMapX, tiltMapY = 0;

    private AudioNode audioOcean;
    private AudioNode audioFootsteps;
    private AudioNode audioJump;
    private AudioNode audioCollect;

    private String audioOceanLocation;
    private String audioFootstepsLocation;
    private String audioJumpLocation;
    private String audioCollectLocation;

    public int getSphere_resource_count() {
        return sphere_resource_count;
    }

    public int getPlayerCount() { return player_count; }

    public void setPlayerCount(int player_count) {
        this.player_count = player_count;
    }

    public void setSphere_resource_count(int sphere_resource_count) {
        this.sphere_resource_count = sphere_resource_count;
    }

    public void addSpawnLocation(Vector3f spawnLocation)
    {
        spawnLocations.add(spawnLocation);
    }

    public float getSphere_resource_radius() {
        return sphere_resource_radius;
    }

    public void setSphere_resource_radius(float sphere_resource_radius) {
        this.sphere_resource_radius = sphere_resource_radius;
    }

    public float getPlayer_sphere_start_radius() {
        return player_sphere_start_radius;
    }

    public void setPlayer_sphere_start_radius(float player_sphere_start_radius) {
        this.player_sphere_start_radius = player_sphere_start_radius;
    }

    public float getMap_tilt_rate() {
        return map_tilt_rate;
    }

    public void setMap_tilt_rate(float map_tilt_rate) {
        this.map_tilt_rate = map_tilt_rate;
    }

    public float getWater_height_default_rate() {
        return water_height_default_rate;
    }

    public void setWater_height_default_rate(float water_height_default_rate) {
        this.water_height_default_rate = water_height_default_rate;
    }

    public float getWater_height_player_rate() {
        return water_height_player_rate;
    }

    public void setWater_height_player_rate(float water_height_player_rate) {
        this.water_height_player_rate = water_height_player_rate;
    }

    public float getWaterHeight() {
        return waterHeight;
    }

    public void setWaterHeight(float waterHeight) {
        this.waterHeight = waterHeight;
    }

    public float getVerticalAngle() {
        return verticalAngle;
    }

    public void setVerticalAngle(float verticalAngle) {
        this.verticalAngle = verticalAngle;
    }

    public float getMaxVerticalAngle() {
        return maxVerticalAngle;
    }

    public void setMaxVerticalAngle(float maxVerticalAngle) {
        this.maxVerticalAngle = maxVerticalAngle;
    }

    public float getMinVerticalAngle() {
        return minVerticalAngle;
    }

    public void setMinVerticalAngle(float minVerticalAngle) {
        this.minVerticalAngle = minVerticalAngle;
    }

    public Vector3f getLightDir() {
        return lightDir;
    }

    public void setLightDir(Vector3f lightDir) {
        this.lightDir = lightDir;
    }

    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
    }

    public Vector3f getCamDir() {
        return camDir;
    }

    public void setCamDir(Vector3f camDir) {
        this.camDir = camDir;
    }

    public Vector3f getCamLeft() {
        return camLeft;
    }

    public void setCamLeft(Vector3f camLeft) {
        this.camLeft = camLeft;
    }

    public Node getResources() {
        return resources;
    }

    public void setResources(Node resources) {
        this.resources = resources;
    }

    public Node getPlayerNode() {
        return playerNode;
    }

    public void setPlayerNode(Node playerNode) {
        this.playerNode = playerNode;
    }

    public Node getPivot() {
        return pivot;
    }

    public void setPivot(Node pivot) {
        this.pivot = pivot;
    }

    public CameraNode getCamNode() {
        return camNode;
    }

    public void setCamNode(CameraNode camNode) {
        this.camNode = camNode;
    }

    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    public RigidBodyControl getLandscape() {
        return landscape;
    }

    public void setLandscape(RigidBodyControl landscape) {
        this.landscape = landscape;
    }

    public CharacterControl getPlayerControl() {
        return playerControl;
    }

    public void setPlayerControl(CharacterControl playerControl) {
        this.playerControl = playerControl;
    }

    public SphereCollisionShape getSphereShape() {
        return sphereShape;
    }

    public void setSphereShape(SphereCollisionShape sphereShape) {
        this.sphereShape = sphereShape;
    }

    public Sphere getPlayerSphere() {
        return playerSphere;
    }

    public void setPlayerSphere(Sphere playerSphere) {
        this.playerSphere = playerSphere;
    }

    public Geometry getPlayerG() {
        return playerG;
    }

    public void setPlayerG(Geometry playerG) {
        this.playerG = playerG;
    }

    public FilterPostProcessor getFpp() {
        return fpp;
    }

    public void setFpp(FilterPostProcessor fpp) {
        this.fpp = fpp;
    }

    public WaterFilter getWater() {
        return water;
    }

    public void setWater(WaterFilter water) {
        this.water = water;
    }

    public TerrainQuad getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainQuad terrain) {
        this.terrain = terrain;
    }

    public Material getMat_terrain() {
        return mat_terrain;
    }

    public void setMat_terrain(Material mat_terrain) {
        this.mat_terrain = mat_terrain;
    }

    public String getHeightMapLocation() {
        return heightMapLocation;
    }

    public void setHeightMapLocation(String heightMapLocation) {
        this.heightMapLocation = heightMapLocation;
    }

    public String getMatTerrainLocation() {
        return matTerrainLocation;
    }

    public void setMatTerrainLocation(String matTerrainLocation) {
        this.matTerrainLocation = matTerrainLocation;
    }

    public String getMatTerrainAlphaTextureLocation() {
        return matTerrainAlphaTextureLocation;
    }

    public void setMatTerrainAlphaTextureLocation(String matTerrainAlphaTextureLocation) {
        this.matTerrainAlphaTextureLocation = matTerrainAlphaTextureLocation;
    }

    public boolean isPlayerNeedsScaling() {
        return playerNeedsScaling;
    }

    public void setPlayerNeedsScaling(boolean playerNeedsScaling) {
        this.playerNeedsScaling = playerNeedsScaling;
    }

    public int getScaleStartTime() {
        return scaleStartTime;
    }

    public void setScaleStartTime(int scaleStartTime) {
        this.scaleStartTime = scaleStartTime;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isSlowWater() {
        return slowWater;
    }

    public void setSlowWater(boolean slowWater) {
        this.slowWater = slowWater;
    }

    public boolean isMapTiltLeft() {
        return mapTiltLeft;
    }

    public void setMapTiltLeft(boolean mapTiltLeft) {
        this.mapTiltLeft = mapTiltLeft;
    }

    public boolean isMapTiltRight() {
        return mapTiltRight;
    }

    public void setMapTiltRight(boolean mapTiltRight) {
        this.mapTiltRight = mapTiltRight;
    }

    public boolean isMapTiltForward() {
        return mapTiltForward;
    }

    public void setMapTiltForward(boolean mapTiltForward) {
        this.mapTiltForward = mapTiltForward;
    }

    public boolean isMapTiltBack() {
        return mapTiltBack;
    }

    public void setMapTiltBack(boolean mapTiltBack) {
        this.mapTiltBack = mapTiltBack;
    }

    public HashMap<Integer, OtherPlayer> getOtherPlayers() { return otherPlayers; }

    public ArrayList<Vector3f> getSpawnLocations() { return spawnLocations; }

    public ArrayList<SphereResource> getSphereResource() {
        return sphereResource;
    }

    public ArrayList<SphereResource> getSphereResourcesToShrink() {
        return sphereResourcesToShrink;
    }

    public Quaternion getMapTilt() {
        return mapTilt;
    }

    public void setMapTilt(Quaternion mapTilt) {
        this.mapTilt = mapTilt;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getTiltMapX() {
        return tiltMapX;
    }

    public void setTiltMapX(float tiltMapX) {
        this.tiltMapX = tiltMapX;
    }

    public float getTiltMapY() {
        return tiltMapY;
    }

    public void setTiltMapY(float tiltMapY) {
        this.tiltMapY = tiltMapY;
    }

    public AudioNode getAudioOcean() {
        return audioOcean;
    }

    public void setAudioOcean(AudioNode audioOcean) {
        this.audioOcean = audioOcean;
    }

    public AudioNode getAudioFootsteps() {
        return audioFootsteps;
    }

    public void setAudioFootsteps(AudioNode audioFootsteps) {
        this.audioFootsteps = audioFootsteps;
    }

    public AudioNode getAudioJump() {
        return audioJump;
    }

    public void setAudioJump(AudioNode audioJump) {
        this.audioJump = audioJump;
    }

    public AudioNode getAudioCollect() {
        return audioCollect;
    }

    public void setAudioCollect(AudioNode audioCollect) {
        this.audioCollect = audioCollect;
    }

    public String getAudioOceanLocation() {
        return audioOceanLocation;
    }

    public void setAudioOceanLocation(String audioOceanLocation) {
        this.audioOceanLocation = audioOceanLocation;
    }

    public String getAudioFootstepsLocation() {
        return audioFootstepsLocation;
    }

    public void setAudioFootstepsLocation(String audioFootstepsLocation) {
        this.audioFootstepsLocation = audioFootstepsLocation;
    }

    public String getAudioJumpLocation() {
        return audioJumpLocation;
    }

    public void setAudioJumpLocation(String audioJumpLocation) {
        this.audioJumpLocation = audioJumpLocation;
    }

    public String getAudioCollectLocation() {
        return audioCollectLocation;
    }

    public void setAudioCollectLocation(String audioCollectLocation) {
        this.audioCollectLocation = audioCollectLocation;
    }

    public Vector3f getRandomSpawnLocation() {
        return spawnLocations.get(Globals.getRandInt(spawnLocations.size()));
    }

    public LevelType getLevelType() {
        return levelType;
    }

    public void setLevelType(LevelType levelType) {
        this.levelType = levelType;
    }
}
