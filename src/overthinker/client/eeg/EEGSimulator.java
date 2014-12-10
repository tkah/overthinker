package overthinker.client.eeg;

import java.util.Random;
import java.util.Timer;

/**
 * For use on non-windows machines, or if no headset is available.
 * Provides a demonstration of the EEG functionality.  Overnode.monitor
 * must be changed from an EEGMonitor object to an EEGSimulator object.
 */
public class EEGSimulator extends Thread{

    private int[] values= {1, -1, 2, -2, 10, 0};
    public volatile boolean updated = true;
    public float excitementShort = 0;
    private Timer timer1 = null;
    private Timer timer2 = null;
    private int currentGravity = 0;
    private int requestedGravity = 0;
    private boolean gravityNormal = true;
    private Random randomIndex = new Random();
    private Random randomDouble = new Random();

    public EEGSimulator() {

    }

    @Override
    public void run() {
        while(true) {
            requestedGravity = values[randomIndex.nextInt(6)];
            excitementShort = randomDouble.nextFloat();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        }

    /**
     * Public getter-method for the needed tilt direction
     *
     * @return integer reflecting tilt direction (-1 = Left, 1 = Right, -2 = Down, 2 = Up)
     */
    public int getTiltDirection() {
        currentGravity = requestedGravity;
        if (currentGravity == 10) {
            gravityNormal = true;
            currentGravity = 0;
            return requestedGravity;
        }
        gravityNormal = false;
        return requestedGravity;
    }


    /**
     * Looks at the short-term excitement score
     *
     * @return integer 1 if excitement is above 50%, 0 otherwise
     */
    private int interpretExcitement() {

        if (excitementShort > 1 || excitementShort < 0) {
            System.out.println("excitement out of range");
            return 0;
        }
        if (excitementShort > 0.5) return 1;
        else {
            return 0;
        }
    }

    /**
     * Public getter-method for stress (whichever metric we decide
     * to use will be set in the 'interpretExcitement() method).
     *
     * @return 1 if stress is high, 0 otherwise.  **Will change, with gameplay testing.**
     */
    public float getStressLevel() {
        float stress = 0;
        synchronized (this) {
            stress = excitementShort;
        }
        return stress;
    }
}
