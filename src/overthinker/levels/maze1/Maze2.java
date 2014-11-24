package overthinker.levels.maze1;

import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;

/**
 * Created by Peter on 11/20/2014.
 */
public class Maze2 {
    private final int SPHERE_RESOURCE_COUNT = 100;
    private final float SPHERE_RESOURCE_RADIUS = 1.0f;
    private final float PLAYER_SPHERE_START_RADIUS = 2.0f;
    private final float MAP_TILT_RATE = 0.008f;
    private final float WATER_HEIGHT_DEFAULT_RATE = 0.005f;
    private final float WATER_HEIGHT_PLAYER_RATE = 0.001f;

    private float waterHeight = 20.0f;
    private float verticalAngle = 30 * FastMath.DEG_TO_RAD;
    private float maxVerticalAngle = 85 * FastMath.DEG_TO_RAD;
    private float minVerticalAngle = -85 * FastMath.DEG_TO_RAD;

    private Vector3f lightDir = new Vector3f(-4.9f, -2.3f, 5.9f);
    private Vector3f walkDirection = new Vector3f();
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();

    private Node resources;
    private Node playerNode;
    private Node pivot;
    private CameraNode camNode;

    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl playerControl;

    /** Create AudioNodes **/
    private AudioNode audio_ocean;
    private AudioNode audio_footsteps;
    private AudioNode audio_jump;
    private AudioNode audio_collect;

}
