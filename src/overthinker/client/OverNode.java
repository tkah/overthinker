package overthinker.client;

import com.jme3.audio.AudioNode;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.network.Client;
import overthinker.client.eeg.EEGMonitor;
import overthinker.client.eeg.EEGSimulator;
import overthinker.net.ChangeMapTiltRequest;
import overthinker.net.ChangeWaterRateRequest;

import java.util.ArrayList;

/**
 * This class defines the base methods and update process for the Overthinker client,
 * including the eeg data.
 *
 * Created by Torran, Sid, Peter, Josh, Derek on 11/26/14.  EEG methods and misc added by Sid 12/3/14
 */
public class OverNode extends PlayerNode
{
  private final boolean DEBUG = false;

  private ArrayList<AudioNode> audioList = new ArrayList<>();
  private EEGMonitor monitor = new EEGMonitor();
  //private EEGSimulator monitor = new EEGSimulator();
  private float waterRate = 0;
  private float waterHeight = 20;
  private int tiltDirection = 0;
  private boolean gravityLeft = false, gravityRight = false, gravityForward = false, gravityBack = false;
  private boolean eegGrav = false;
  //Left = -1, right = 1, forward = 2, back = -2, clear all flags = 10

  private Client netClient;
  private ChangeWaterRateRequest waterRateRequest = new ChangeWaterRateRequest();

  /**
   * Class constructor
   * @param name      - name of overthinker node
   * @param netClient - client
   */
  public OverNode(String name, Client netClient)
  {
    super(name);
    monitor.start();
    this.netClient = netClient;
  }

  /**
   * Update method for overthinker
   * @param tpf - frame rate
   */
  public void update (float tpf) {
    if (monitor.updated) {
      if (DEBUG) System.out.println("Updating from EEG: Entering method.");

      if (eegGrav)
      {
        tiltDirection = monitor.getTiltDirection();
        if (tiltDirection == 10) {
          clearTilt();
          setTilt(0);
        } else setTilt(tiltDirection);
      }

      waterRate = monitor.getStressLevel() / 100; //a rate of 1 fills instantly, eeg hovers around ~.5, so divide by 1000
      waterHeight += waterRate;
      if (DEBUG) System.out.println("Update from EEG: waterRate = "+Float.toString(waterRate));
      waterRateRequest.setWaterRate(waterHeight);
      netClient.send(waterRateRequest);
    }
  }

  /**
   * Sets up player overnode player
   */
  public void setUpPlayer()
  {

  }

  /**
   * Action to take on key press
   * @param binding   - name of key press binding
   * @param isPressed - key is pressed or not pressed
   * @param tpf       - frame rate
   */
  public void onAction(String binding, boolean isPressed, float tpf)
  {

    if (binding.equals("GravityNorm"))
    {
      eegGrav = !eegGrav;
      clearTilt();
      ChangeMapTiltRequest changeMapTiltRequest = new ChangeMapTiltRequest();
      changeMapTiltRequest.setRight(false);
      changeMapTiltRequest.setLeft(false);
      changeMapTiltRequest.setForward(false);
      changeMapTiltRequest.setBack(false);
      tiltDirection = 0;
      netClient.send(changeMapTiltRequest);
    }
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
      gravityRight= true;
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
    }
    else if (direction == -2) {
      clearTilt();
      gravityBack = true;
      changeMapTiltRequest.setBack(true);
    }
    else if (direction == 0) return;
    netClient.send(changeMapTiltRequest);
  }

  /**
   * Getter for water rate
   * @return water rise rate
   */
  @Override
  public float getWaterRate() {
    return waterRate;
  }

  /**
   * Getter for forward grav status
   * @return forward gravity status
   */
  @Override
  public boolean getForwardGrav() {
    return gravityForward;
  }

  /**
   * Getter for back gravity status
   * @return back gravity status
   */
  @Override
  public boolean getBackwardGrav() {
    return gravityBack;
  }

  /**
   * Getter for left gravity status
   * @return left grav status
   */
  @Override
  public boolean getLeftGrav() {
    return gravityLeft;
  }

  /**
   * Getter for right gravity status
   * @return right grav status
   */
  @Override
  public boolean getRightGrav() {
    return gravityRight;
  }

  /**
   * Getter for playerControl audio
   * @return list of audio to add
   */
  public ArrayList getAudio()
  {
    return audioList;
  }

  /**
   * Setter for overnode controls
   * @param inputManager - program's input manager
   * @return list of control
   */
  public ArrayList setUpControls(InputManager inputManager)
  {
    inputManager.addMapping("GravityNorm", new KeyTrigger(KeyInput.KEY_SPACE));
    actionStrings.add("GravityNorm");

    return actionStrings;
  }
}
