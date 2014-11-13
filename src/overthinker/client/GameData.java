package overthinker.client;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.water.WaterFilter;

/**
 * Created by Peter on 11/12/2014.
 */
public class GameData {
    private FilterPostProcessor fpp;
    private WaterFilter water;
    private Vector3f lightDir;
    private float waterHeight;
    private TerrainQuad terrain;
    private Material mat_terrain;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    private ColorRGBA backgroundColor;


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

    public Vector3f getLightDir() {
        return lightDir;
    }

    public void setLightDir(Vector3f lightDir) {
        this.lightDir = lightDir;
    }

    public float getWaterHeight() {
        return waterHeight;
    }

    public void setWaterHeight(float waterHeight) {
        this.waterHeight = waterHeight;
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

    public CharacterControl getPlayer() {
        return player;
    }

    public void setPlayer(CharacterControl player) {
        this.player = player;
    }

    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public void setWalkDirection(Vector3f walkDirection) {
        this.walkDirection = walkDirection;
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

    public ColorRGBA getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(ColorRGBA backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
