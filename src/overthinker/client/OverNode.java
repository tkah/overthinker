package overthinker.client;

import com.jme3.audio.AudioNode;
import com.jme3.input.InputManager;
import com.jme3.network.Client;
import overthinker.client.eeg.EEGMonitor;
import overthinker.net.ChangeMapTiltRequest;
import overthinker.net.ChangeWaterRateRequest;

import java.util.ArrayList;

/**
 * This class defines the base methods and update process for the Overthinker client,
 * including the eeg data.
 * Created by Torran on 11/26/14.  EEG methods and misc added by Sid 10/3/14
 */
public class OverNode extends PlayerNode
{
  private ArrayList<AudioNode> audioList = new ArrayList<>();
  private EEGMonitor monitor = new EEGMonitor();
  private float waterRate = 0;
  private int tiltDirection = 0;
  private boolean gravityLeft = false, gravityRight = false, gravityForward = false, gravityBack = false;
  //Left = -1, right = 1, forward = 2, back = -2, clear all flags = 10
  private Client netClient;
  private ChangeWaterRateRequest waterRateRequest = new ChangeWaterRateRequest();

  public OverNode(String name, Client netClient)
  {
    super(name);
    monitor.start();
    this.netClient = netClient;
  }

  public void update (float tpf) {
    if (monitor.updated) {
      tiltDirection = monitor.getTiltDirection();
      if (tiltDirection == 10) {
        clearTilt();
      } else setTilt(tiltDirection);
      waterRate = monitor.getStressLevel() / 1000; //a rate of 1 fills instantly, eeg hovers around ~.5, so divide by 1000
      waterRateRequest.setWaterRate(waterRate);
      netClient.send(waterRateRequest);
      //TODO create netClient to send waterRate and tiltDirection
      monitor.updated = false;
    }
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

  /**
   * Called to return the gravity flags to normal
   */
  private void clearTilt() {
    gravityLeft = false;
    gravityRight = false;
    gravityForward = false;
    gravityBack = false;
  }

  /**
   * Takes an integer direction from monitor and sets the appropriate
   * gravity flag
   * @param direction integer representing tilt direction
   */
  private void setTilt(int direction) {
    ChangeMapTiltRequest changeMapTiltRequest = new ChangeMapTiltRequest();
    if (direction == 1) {
      clearTilt();
      gravityRight = true;
      changeMapTiltRequest.setRight(true);
    }
    else if (direction == -1) {
      clearTilt();
      gravityLeft = true;
      changeMapTiltRequest.setLeft(true);
    }
    else if (direction == 2) {
      clearTilt();
      gravityForward = true;
      changeMapTiltRequest.setForward(true);
      return;
    }
    else if (direction == -2) {
      clearTilt();
      gravityBack = true;
      changeMapTiltRequest.setBack(true);
    }
    netClient.send(changeMapTiltRequest);
  }

  @Override
  public float getWaterRate() {
    return waterRate;
  }
  @Override
  public boolean getForwardGrav() {
    return gravityForward;
  }
  @Override
  public boolean getBackwardGrav() {
    return gravityBack;
  }
  @Override
  public boolean getLeftGrav() {
    return gravityLeft;
  }
  @Override
  public boolean getRightGrav() {
    return gravityRight;
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
