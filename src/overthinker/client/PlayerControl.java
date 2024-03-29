package overthinker.client;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.ArrayList;

/**
 * Class controls player movement in conjunction with UnderNode
 * Largely in charge of physics aspects of movement - gravity, rotation
 *
 * Created by Torran, Sid, Peter, Josh, Derek on 11/21/14.
 */
public class PlayerControl extends BetterCharacterControl
{
  private AudioNode audio_footsteps;
  private AudioNode audio_jump;
  private float camDir = 0;

  private ArrayList<AudioNode> audioList = new ArrayList<>();

  private boolean gravityLeft = false, gravityRight = false, gravityForward = false, gravityBack = false;
  private boolean changeInGravity = false;

  /**
   * Class constructor
   * @param radius - radius of player
   * @param height - height of player
   * @param mass   - mass of player
   */
  public PlayerControl(float radius, float height, float mass, AssetManager a)
  {
    super(radius, height, mass);
    setApplyPhysicsLocal(true);
    initAudio(a);

  }

  /**
   * Override BetterCharacterControl jump method
   * Necessary to bypass the parent class's onGround
   * check. This check is done in the simpleUpdate()
   * method of UClient using the playerNode's onGround
   * value.
   */
  @Override
  public void jump()
  {
    jump = true;
  }

  /**
   * Action to take when key pressed
   * @param binding   - name of key pressed
   * @param isPressed - status of key pressed
   * @param tpf       - frame rate
   */
  public void onAction(String binding, boolean isPressed, float tpf)
  {
    if (binding.equals("MapTiltForward"))
    {
      gravityForward = isPressed;
      setWalkDirection(new Vector3f(0,0,0));
    }
    else if (binding.equals("MapTiltLeft"))
    {
      gravityLeft = isPressed;
      setWalkDirection(new Vector3f(0,0,0));
    }
    else if (binding.equals("MapTiltRight"))
    {
      gravityRight = isPressed;
      setWalkDirection(new Vector3f(0,0,0));
    }
    else if (binding.equals("MapTiltBack"))
    {
      gravityBack = isPressed;
      setWalkDirection(new Vector3f(0,0,0));
    }
    changeInGravity = true;
  }

  /**
   * Turns player
   * @param dir - direction to turn in
   */
  public void turn(float dir)
  {
    camDir = dir;
    Quaternion turn = new Quaternion();
    turn.fromAngleAxis(dir, Vector3f.UNIT_Y);
    setViewDirection(turn.mult(getViewDirection()));
  }

  /**
   * Checks gravity status
   * @param nodeOnGround      - is the node on the ground
   * @param playerTranslation - player location
   * @param colNode           - collision node
   * @param pivot             - pivot node
   * @param camNode           - camera node
   * @return
   */
  public boolean checkGravity(boolean nodeOnGround, Vector3f playerTranslation, Node colNode, Node pivot, Node camNode)
  {
    boolean onGround = nodeOnGround;
    Vector3f dir;
    CollisionResults coll;

    if (gravityForward && gravityLeft)
    {
      coll = new CollisionResults();
      dir = new Vector3f(1,0,-1);
      Ray ray = new Ray(playerTranslation, dir);
      colNode.collideWith(ray, coll);
      if ((coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals.GROUND_RAY_ALLOWANCE) || coll.size() == 0)
      {
        onGround = false;
      }
      else onGround = true;
      setGravity(new Vector3f(Globals.GRAVITY, 1, -Globals.GRAVITY));
    }
    else if (gravityForward && gravityRight)
    {
      coll = new CollisionResults();
      dir = new Vector3f(-1,0,-1);
      Ray ray = new Ray(playerTranslation, dir);
      colNode.collideWith(ray, coll);
      onGround = !((coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals
            .GROUND_RAY_ALLOWANCE) || coll.size() == 0);
      setGravity(new Vector3f(-Globals.GRAVITY, 1, -Globals.GRAVITY));
    }
    else if (gravityBack && gravityRight)
    {
      coll = new CollisionResults();
      dir = new Vector3f(-1,0,1);
      Ray ray = new Ray(playerTranslation, dir);
      colNode.collideWith(ray, coll);
      onGround = !((coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals
            .GROUND_RAY_ALLOWANCE) || coll.size() == 0);
      setGravity(new Vector3f(-Globals.GRAVITY, 1, Globals.GRAVITY));
    }
    else if (gravityBack && gravityLeft)
    {
      coll = new CollisionResults();
      dir = new Vector3f(1,0,1);
      Ray ray = new Ray(playerTranslation, dir);
      colNode.collideWith(ray, coll);
      onGround = !((coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals
            .GROUND_RAY_ALLOWANCE) || coll.size() == 0);
      setGravity(new Vector3f(Globals.GRAVITY, 1, Globals.GRAVITY));
    }
    else if (gravityLeft)
    {
      System.out.println("PlayerControl left gravity");

      coll = new CollisionResults();
      dir = new Vector3f(1,0,0);
      Ray rayL = new Ray(playerTranslation, dir);
      colNode.collideWith(rayL, coll);
      onGround = !(coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals
            .GROUND_RAY_ALLOWANCE || coll.size() == 0);
      setGravity(new Vector3f(Globals.GRAVITY, 1, 0));
    }
    else if (gravityRight)
    {
      System.out.println("PlayerControl right gravity");

      coll = new CollisionResults();
      dir = new Vector3f(-1,0,0);
      Ray rayR = new Ray(playerTranslation, dir);
      colNode.collideWith(rayR, coll);
      onGround = !(coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals.GROUND_RAY_ALLOWANCE || coll.size() == 0);
      setGravity(new Vector3f(-Globals.GRAVITY, 1, 0));
    }
    else if (gravityForward)
    {
      System.out.println("PlayerControl forward gravity");

      System.out.println("forward");
      coll = new CollisionResults();
      dir = new Vector3f(0,0,-1);
      Ray rayF = new Ray(playerTranslation, dir);
      colNode.collideWith(rayF, coll);
      onGround = !((coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals
            .GROUND_RAY_ALLOWANCE) || coll.size() == 0);
      setGravity(new Vector3f(0, 1, -Globals.GRAVITY));
    }
    else if (gravityBack)
    {
      System.out.println("PlayerControl back gravity");

      coll = new CollisionResults();
      dir = new Vector3f(0,0,1);
      Ray rayB = new Ray(playerTranslation, dir);
      colNode.collideWith(rayB, coll);
      onGround = !((coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals.GROUND_RAY_ALLOWANCE) || coll.size() == 0);
      setGravity(new Vector3f(0, 1, Globals.GRAVITY));
    }
    else
    {

      System.out.println("PlayerControl default gravity");

      coll = new CollisionResults();
      dir = new Vector3f(0,-1,0);
      Ray rayD = new Ray(playerTranslation, dir);

      colNode.collideWith(rayD, coll);
      onGround = !((coll.size() > 0 && coll.getClosestCollision().getDistance() > getHeight() + Globals.GROUND_RAY_ALLOWANCE) || coll.size() == 0);
      setGravity(new Vector3f(0, -Globals.GRAVITY, 0));
    }

    if (changeInGravity)
    {
      setViewDirection(new Vector3f(0, 4, -18));
      changeInGravity = false;
    }

    return onGround;
  }

  /**
   * Setters for left gravity. Determined by EEG gyroscope.
   * @param val - left gravity val
   */
  public void setGravityLeft (boolean val)
  {
    gravityLeft = val;
  }

  /**
   * Setters for right gravity. Determined by EEG gyroscope.
   * @param val - right gravity val
   */
  public void setGravityRight (boolean val)
  {
    gravityRight = val;
  }

  /**
   * Setters for forward gravity. Determined by EEG gyroscope.
   * @param val - forward gravity val
   */
  public void setGravityForward (boolean val)
  {
    gravityForward = val;
  }

  /**
   * Setters for back gravity. Determined by EEG gyroscope.
   * @param val - back gravity val
   */
  public void setGravityBack (boolean val)
  {
    gravityBack = val;
  }

  /**
   * Sets scale of player physics space
   * @param size - the amount to scale by
   */
  public void setScale (float size)
  {
    height *= size;
    radius *= size;
    rigidBody.setCollisionShape(getShape());
  }

  /**
   * Getter for physics height
   * @return height of player physics space
   */
  public float getHeight()
  {
    return height;
  }

  /**
   * Sett or physics height
   * @param height - height to set
   */
  public void setHeight(float height)
  {
    this.height = height;
  }

  /**
   * Initializes player specific audio
   * @param assetManager - program's assetmanager
   */
  public void initAudio(AssetManager assetManager)
  {
    //walking sounds
    audio_footsteps = new AudioNode(assetManager, "overthinker/assets/sounds/footsteps.ogg",false);
    audio_footsteps.setPositional(false);
    audio_footsteps.setLooping(true);
    audio_footsteps.setVolume(2);
    audioList.add(audio_footsteps);

    //jumping sound
    audio_jump = new AudioNode(assetManager, "overthinker/assets/sounds/jump.ogg",false);
    audio_jump.setPositional(false);
    audio_jump.setLooping(false);
    audio_jump.setVolume(4);
    audioList.add(audio_jump);
  }

  /**
   * Getter for cam direction
   * @return camera direction
   */
  public float getCamDir()
  {
    return camDir;
  }

  /**
   * Getter for list of audio used by player
   * @return list of audio used by player
   */
  public ArrayList getAudio()
  {
    return audioList;
  }

  /** Method to add sounds when buttons are pressed **/
  public void addMovementSound(boolean emmit){
    if(emmit){
      audio_footsteps.play();
    }else{
      audio_footsteps.stop();
    }
  }

  /**
   * Plays jump sound
   */
  public void playJump()
  {
    audio_jump.playInstance();
  }

  /**
   * Changes gravity
   * @param val - gravity change
   */
  public void setChangeInGravity(boolean val)
  {
    changeInGravity = val;
  }
}
