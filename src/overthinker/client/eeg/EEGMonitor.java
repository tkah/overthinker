package overthinker.client.eeg;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * Creates an Emotiv EEG engine to recieve and interpret
 * data from the headset.
 */
public class EEGMonitor extends Thread {

    private final boolean DEBUG = false;
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
    private boolean readytocollect = false;
    private IntByReference gyroX = new IntByReference(0);
    private IntByReference gyroY = new IntByReference(0);
    private int currentGravity = 0;
    private int requestedGravity = 0;
    private boolean gravityNormal = true;

    public volatile boolean updated = false;
    public float excitementShort = 0;
    public float frustrationShort = 0;


    public EEGMonitor() {
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
                        readytocollect = true;
                    }
            } else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
                if (DEBUG) System.out.println("Internal error in Emotiv Engine!");
                break;
            }

            if (readytocollect) {
                Edk.INSTANCE.EE_DataUpdateHandle(0, hData);

                Edk.INSTANCE.EE_DataGetNumberOfSample(hData, nSamplesTaken);

                if (nSamplesTaken != null) {
                    if (nSamplesTaken.getValue() != 0) {

                        if (DEBUG) System.out.print("Updated: ");
                        if (DEBUG) System.out.println(nSamplesTaken.getValue());

                        double[] data = new double[nSamplesTaken.getValue()];
                        //for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) {
                            Edk.INSTANCE.EE_HeadsetGetGyroDelta(userID.getValue(), gyroX, gyroY);
                            if (DEBUG)System.out.print(" GyroDelta[X]: " + gyroX.getValue() + " GyroDelta[Y]: " + gyroY.getValue());

                            int temp = interpretGyro();
                            if (temp != 0) requestedGravity = temp;

                            excitementShort = EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState);
                            interpretExcitement();
                            if (DEBUG)System.out.print(", Frust: " + EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState));
                            if (DEBUG) System.out.println();
                       // }
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
        if (DEBUG) System.out.println("Disconnected!");
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
                if (!gravityNormal && currentGravity == -1 )
                {
                    if (DEBUG) System.out.println("Tilt: 0 (RETURN TO UPRIGHT)");
                    gravityNormal = true;
                    return 10;
                }
                if (DEBUG) System.out.print(" Tilt: 1 (Right)");
                return 1;       //Right
            } else {
                if (!gravityNormal && currentGravity == 1 )
                {
                    if (DEBUG) System.out.println("Tilt: 0 (RETURN TO UPRIGHT)");
                    gravityNormal = true;
                    return 10;
                }
                if (DEBUG) System.out.print(" Tilt: -1 (Left)");
                return -1;     //Left
            }
        }
        if (Math.abs(yDelta) > MIN_GYRO_DELTA) {
            if (yDelta > 0) {
                if (!gravityNormal && currentGravity == -2 )
                {
                    if (DEBUG) System.out.println("Tilt: 0 (RETURN TO UPRIGHT)");
                    gravityNormal = true;
                    return 10;
                }
                if (DEBUG) System.out.print(" Tilt: 2 (Up)");
                return 2; //Up
            } else {
                if (!gravityNormal && currentGravity == 2 )
                {
                    if (DEBUG) System.out.println("Tilt: 0 (RETURN TO UPRIGHT)");
                    gravityNormal = true;
                    return 10;
                }
                if (DEBUG) System.out.print(" Tilt: -2 (Down)");
                return -2; //Down
            }
        }
        if (DEBUG) System.out.println("Tilt: 0 (No tilt)");
        return 0;
    }

    /**
     * Public getter-method for the needed tilt direction
     *
     * @return integer reflecting tilt direction (-1 = Left, 1 = Right, -2 = Down, 2 = Up)
     */
    public int getTiltDirection() {
        currentGravity = requestedGravity;
        if (currentGravity == 10) {
            currentGravity = 0;
        }
        return requestedGravity;
    }


    /**
     * Looks at the short-term excitment score
     *
     * @return integer 1 if excitement is above 50%, 0 otherwise
     */
    private int interpretExcitement() {
        if (DEBUG) System.out.println("\nShort term excitement: " + excitementShort);
        if (DEBUG) System.out.println("Frustration: " + frustrationShort);

        if (excitementShort > 1 || excitementShort < 0) {
            //System.out.println("\n Headset excitement out of bounds! Calm your bosom!");
            return 0;
        }
        if (excitementShort > 0.5) return 1;
        else return 0;
    }

    /**
     * Public getter-method for stress (whichever metric we decide
     * to use will be set in the 'interpretExcitement() method).
     *
     * @return 1 if stress is high, 0 otherwise.  **Will change, with gameplay testing.**
     */
    public int getStressLevel() {
        int stress = 0;
        synchronized (this) {
            stress = interpretExcitement();
        }
        return stress;
    }
}
