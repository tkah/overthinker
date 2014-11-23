package UClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by Torran on 11/11/14.
 */
public class Globals
{
  public static final int MAP_WIDTH = 512;
  public static final int MAP_HEIGHT = 512;
  public static final int TIMER_DELAY = 1000;
  public static final int SCALE_ANIM_TIME = 1;
  public static final float SCALE_BY = 1.001f;
  private static Random rand = new Random();

  private static Timer timer;
  public static int timerCt = 0;

  /**
   * Set up game timer
   */
  public static void setUpTimer()
  {
    ActionListener taskPerformer = new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
      {
        timerCt++;
      }
    };
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
