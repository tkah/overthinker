package overthinker.client.eeg;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Creates an Emotiv EEG engine to recieve and interpret
 * data from the headset.  Boolean "LOG" enables/disables write to log file.
 */
public class EEGMonitor extends Thread {

    private final boolean DEBUG = false;
    private final boolean LOG = true; //Enables logging of eeg output.
    private final int MIN_GYRO_DELTA = 50;

    private Pointer eEvent = Edk.INSTANCE.EE_EmoEngineEventCreate();
    private Pointer eState = Edk.INSTANCE.EE_EmoStateCreate();
    private Pointer hData = null;
    private IntByReference userID = new IntByReference(0);
    private IntByReference nSamplesTaken = new IntByReference(0);
    private short composerPort = 1726;
    private int option = 1;
    private int state = 0;
    private float secs = 1;
    private boolean readyToCollect = false;
    private IntByReference gyroX = new IntByReference(0);
    private IntByReference gyroY = new IntByReference(0);
    private int currentGravity = 0;
    private int requestedGravity = 0;
    private boolean gravityNormal = true;
    private Timer timer = new Timer();

    private EEGLogger log;

    public volatile boolean updated = true;
    public float excitementShort = 0;
    public float frustrationShort = 0;


    public EEGMonitor() {
        if (LOG) log = new EEGLogger();
        userID = new IntByReference(0);
        nSamplesTaken = new IntByReference(0);

        switch (option) {
            case 1: {
                if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
                    if (DEBUG) System.out.println("Emotiv Engine start up failed.");
                    return;
                }
                break;
            }
            case 2: {
                if (DEBUG) System.out.println("Target IP of EmoComposer: [127.0.0.1] ");

                if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
                    if (DEBUG) System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
                    return;
                }
                if (DEBUG) System.out.println("Connected to EmoComposer on [127.0.0.1]");
                break;
            }
            default:
                if (DEBUG) System.out.println("Invalid option...");
                return;
        }
        hData = Edk.INSTANCE.EE_DataCreate();
        Edk.INSTANCE.EE_DataSetBufferSizeInSec(secs);
        if (DEBUG) System.out.print("Buffer size in secs: ");
        if (DEBUG) System.out.println(secs);

        if (DEBUG) System.out.println("Start receiving EEG Data!");
    }

    @Override
    public void run() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setUpdateTrue();
            }
        }, 1000);

        while (true) {
            state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);

            //New event needs to be handled
            if (state == EdkErrorCode.EDK_OK.ToInt()) {
                int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
                Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

                //Log the EmoState if it has been updated
                if (eventType == Edk.EE_Event_t.EE_UserAdded.ToInt())
                    if (userID != null) {
                        if (DEBUG) System.out.println("User added");
                        Edk.INSTANCE.EE_DataAcquisitionEnable(userID.getValue(), true);
                        readyToCollect = true;
                    }
            } else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
                if (DEBUG) System.out.println("Internal error in Emotiv Engine!");
                break;
            }

            if (readyToCollect) {
                Edk.INSTANCE.EE_DataUpdateHandle(0, hData);

                Edk.INSTANCE.EE_DataGetNumberOfSample(hData, nSamplesTaken);

                if (nSamplesTaken != null) {
                    if (nSamplesTaken.getValue() != 0) {

                        if (DEBUG) System.out.print("Updated: ");
                        if (DEBUG) System.out.println(nSamplesTaken.getValue());

                        double[] data = new double[nSamplesTaken.getValue()];
                        Edk.INSTANCE.EE_HeadsetGetGyroDelta(userID.getValue(), gyroX, gyroY);
                        if (DEBUG)
                            System.out.print(" GyroDelta[X]: " + gyroX.getValue() + " GyroDelta[Y]: " + gyroY.getValue());

                        requestedGravity = interpretGyro();

                        Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
                        excitementShort = EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState);
                        if (LOG) frustrationShort = EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState);

                        if (DEBUG) System.out.println("Short term excitement: "+excitementShort);
                        if (LOG) log.writeLine(" ");
                        if (LOG) log.writeLine(System.nanoTime() + " short term excitement: " + excitementShort);
                        if (LOG) log.writeLine(System.nanoTime() + " frustration level: "+ frustrationShort);
                    }
                }
            }
            updated = true;
        }
        this.shutdown();
    }

    /**
     * Shutdown and deallocate the EEG engine.
     */
    private void shutdown() {
        Edk.INSTANCE.EE_EngineDisconnect();
        Edk.INSTANCE.EE_EmoStateFree(eState);
        Edk.INSTANCE.EE_EmoEngineEventFree(eEvent);

        log.closeStreams();
        if (DEBUG) System.out.println("Disconnected!");
    }

    private void setUpdateTrue() {
        updated = true;
    }

    /**
     * Determines the tilt requested, based on the amount of change from gyroscopes.
     * THIS METHOD GIVES PRECEDENCE TO SIDE-TO-SIDE MOVEMENT, because it is difficult
     * to move your head horizontally without affecting the vertical movement.
     *
     * @return integer reflecting tilt direction (-1 = Left, 1 = Right, -2 = Down, 2 = Up)
     */
    private int interpretGyro() {
        int xDelta = 0;
        int yDelta = 0;
        synchronized (this) {
            xDelta = gyroX.getValue();
            yDelta = gyroY.getValue();
        }
        if (xDelta == 0 && yDelta == 0) {
            if (DEBUG) System.out.print("Tilt: 0 (No tilt)");
            return 0;  //nothing happening.
        }
        if (Math.abs(xDelta) > MIN_GYRO_DELTA) {
            if (xDelta > 0) {
                if (DEBUG) System.out.print(" Tilt: 1 (Right)");
                return 1;   //RIGHT
            } else {
                if (DEBUG) System.out.print(" Tilt: -1 (Left)");
                return -1;     //Left
            }
        }
        if (Math.abs(yDelta) > MIN_GYRO_DELTA) {
            if (yDelta > 0) {
                    if (DEBUG) System.out.print(" Tilt: 2 (Up)");
                    return 2; //Up
            } else {
                    if (DEBUG) System.out.print(" Tilt: -2 (Down)");
                    return -2; //Down
            }
        }
        if (DEBUG) System.out.println("Tilt: 0 (No tilt) fell through");
        return 0;
    }

    /**
     * Public getter-method for the needed tilt direction
     *
     * @return integer reflecting tilt direction (-1 = Left, 1 = Right, -2 = Down, 2 = Up)
     */
    public int getTiltDirection() {
        currentGravity = requestedGravity;

        return currentGravity;
    }


    /**
     * Looks at the short-term excitement score
     *
     * @return integer 1 if excitement is above 50%, 0 otherwise
     */
    private float interpretExcitement() {
        if (DEBUG) System.out.println("\nShort term excitement: " + excitementShort);

        if (excitementShort > 1 || excitementShort < 0) {
            if (DEBUG) System.out.println("Excitement out of bounds! ( 0 < x < 1");
            return 0.2f;
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
            stress = interpretExcitement();
        }
        return stress;
    }
}
