package UClient;

import com.jme3.audio.AudioNode;
import com.jme3.input.InputManager;
import overthinker.client.eeg.EEGMonitor;

import java.util.ArrayList;

/**
 * Created by Torran on 11/26/14.
 */
public class OverNode extends PlayerNode
{
  private ArrayList<AudioNode> audioList = new ArrayList<AudioNode>();
  private EEGMonitor monitor = new EEGMonitor();
  private float waterRate = 0;
  private int tiltDirection = 0;
  private boolean gravityLeft = false, gravityRight = false, gravityForward = false, gravityBack = false;
  //Left = -1, right = 1, forward = 2, back = -2, clear all flags = 10


  public OverNode(String name)
  {
    super(name);
    //monitor.start();
  }

  public void update (float tpf)
  {
    tiltDirection = monitor.getTiltDirection();
    if (tiltDirection == 10) {
      clearTilt();
    }
    else setTilt(tiltDirection);
    waterRate = monitor.getStressLevel();
  }

  public void setUpPlayer()
  {

  }

  public void onAnalog(String binding, float value, float tpf)
  {

  }

  public void onAction(String binding, boolean isPressed, float tpf)
  {

  }

  private void clearTilt() {
    gravityLeft = false;
    gravityRight = false;
    gravityForward = false;
    gravityBack = false;
  }

  //TODO PlayerControl.checkGravity() likely the key, but I have no idea how to set up the args.
  private void setTilt(int direction) {
    if (direction == 0) return;
    if (direction == 1) {
      clearTilt();
      gravityRight = true;
      return;
    }
    if (direction == -1) {
      clearTilt();
      gravityLeft = true;

      return;
    }
    if (direction == 2) {
      clearTilt();
      gravityForward = true;
      return;
    }
    if (direction == -2) {
      clearTilt();
      gravityBack = true;
      return;
    }
  }

  @Override
  public float getWaterRate() {
    return waterRate;
  }


  public ArrayList getAudio()
  {
    return audioList;
  }

  public ArrayList setUpControls(InputManager inputManager)
  {
    return actionStrings;
  }
}
