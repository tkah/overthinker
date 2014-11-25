package overthinker.levels.maze1;

import com.jme3.bullet.BulletAppState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import overthinker.levels.Level;

import java.util.ArrayList;

/**
 * Created by Peter on 11/15/2014.
 */
public class Maze1 extends Level {

    public Maze1(){
        setSphere_resource_count(100);
        setSphere_resource_radius(1.0f);
        setPlayer_sphere_start_radius(2.0f);
        setMap_tilt_rate(0.008f);
        setWater_height_default_rate(0.005f);
        setWater_height_player_rate(0.001f);

        setWaterHeight(20.0f);
        setVerticalAngle(30 * FastMath.DEG_TO_RAD);
        setMaxVerticalAngle(85 * FastMath.DEG_TO_RAD);
        setMinVerticalAngle(-85 * FastMath.DEG_TO_RAD);
        
        setLightDir(new Vector3f(-4.9f, -1.3f, 5.9f));
        setWalkDirection(new Vector3f());
        setCamDir(new Vector3f());
        setCamLeft(new Vector3f());

        setResources(new Node("Resources"));
        setPlayerNode(new Node("player"));
        setPivot(new Node("Pivot"));

        setBulletAppState(new BulletAppState());

        setHeightMapLocation("overthinker/assets/terrains/tieredmaze1.png");
        setMatTerrainLocation("Common/MatDefs/Terrain/Terrain.j3md");
        setMatTerrainAlphaTextureLocation("overthinker/assets/terrains/tieredmaze1color.png");

        setAudioOceanLocation("overthinker/assets/sounds/wavesLoop.ogg");
        setAudioCollectLocation("overthinker/assets/sounds/collect.ogg");
        setAudioFootstepsLocation("overthinker/assets/sounds/footsteps.ogg");
        setAudioJumpLocation("overthinker/assets/sounds/pop.ogg");
    }
}
