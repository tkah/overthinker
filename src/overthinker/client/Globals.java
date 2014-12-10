package overthinker.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Class offers constants and other variables accessed by multiple classes
 *
 * Created by Torran, Sid, Josh, Peter, Derek on 11/11/14.
 */
public class Globals
{
  public static final String VERSION = "v0.1";
  public static final String DEFAULT_SERVER = "127.0.0.1";
  public static final String IP_ADDRESS = "";
  public static final boolean DEBUG = true;
  public static final int MAP_WIDTH = 512;
  public static final int MAP_HEIGHT = 512;
  public static final int TIMER_DELAY = 1000;
  public static final int SCALE_ANIM_TIME = 1;
  public static final float SCALE_BY = 1.005f;
  public static final float PLAYER_SPHERE_START_RADIUS = 2.0f;
  public static final float GRAVITY = 30;
  public static final float GROUND_RAY_ALLOWANCE = 0.8f;
  public static final int SPHERE_RESOURCE_COUNT = 100;
  public static final float SPHERE_RESOURCE_RADIUS = 1.5f;
  public static final int SPHERE_RESPAWN_RATE = 100;
  public static final float WATER_HEIGHT_DEFAULT_RATE = 0.005f;
  public static final float WATER_HEIGHT_PLAYER_RATE = 0.001f; // Should be somewhat lower than the DEFAULT_RATE,
                                                                // but water height should continue increase no matter what
  public static final float MAX_PLAYER_SIZE = 6f;

  private static Random rand = new Random();

  private static Timer timer;
  public static int timerCt = 0;

  /**
   * Set up game timer
   */
  public static void setUpTimer()
  {
    ActionListener taskPerformer = evt -> timerCt++;
    timer = new Timer(TIMER_DELAY, taskPerformer);
  }

  /**
   * Returns current minutes since timer started
   * @return timer time in minutes
   */
  public static int getCurMins()
  {
    return timerCt / 60;
  }

  /**
   * Returns current minutes since timer started
   * @return timer time in seconds
   */
  public static int getCurSecs()
  {
    return timerCt % 60;
  }

  /**
   * Returns total seconds
   * @return total seconds
   */
  public static int getTotSecs()
  {
    return timerCt;
  }

  /**
   * Start game timer
   */
  public static void startTimer()
  {
    timer.start();
  }

  /**
   * Stop game timer
   */
  public static void stopTimer()
  {
    timer.stop();
  }

  /**
   * Returns a random float
   * @return a random float
   */
  public static float getRandFloat()
  {
    return (short) rand.nextFloat();
  }

  /**
   * Returns a random int seeded by the given int
   *
   * @param n - the seed for the random number
   * @return a random int
   */
  public static int getRandInt(int n)
  {
    return rand.nextInt(n);
  }
}
