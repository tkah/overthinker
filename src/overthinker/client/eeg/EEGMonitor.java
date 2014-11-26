package overthinker.client.eeg;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * Created by sidholman on 11/25/14.
 */
public class EEGMonitor extends Thread {

    private final boolean DEBUG = true;
    private final int MIN_GYRO_DELTA = 200;

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
                        for (int sampleIdx = 0; sampleIdx < nSamplesTaken.getValue(); ++sampleIdx) {
//                            for (int i = 0 ; i < 14 ; i++) {
//
//                                Edk.INSTANCE.EE_DataGet(hData, i, data, nSamplesTaken.getValue());
//                                System.out.print(data[sampleIdx]);
//                                System.out.print(",");
//
//                            }
                            Edk.INSTANCE.EE_HeadsetGetGyroDelta(userID.getValue(), gyroX, gyroY);
                            if (DEBUG) System.out.print(" GyroDelta[X]: " + gyroX.getValue() + " GyroDelta[Y]: " + gyroY.getValue());
                            if (DEBUG) System.out.print(", tilt direction: " + interpretGyro());
                            //Edk.INSTANCE. dot HOW DO I ACCESS FRUSTRATION dot BAD API
                            Edk.INSTANCE.EE_EmoEngineEventGetEmoState(hData, eState);
                            if (DEBUG) System.out.print(", Frust: " + EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState));
                            if (DEBUG) System.out.println();
                        }
                    }
                }
            }
        }
        this.shutDown();
    }

    private void shutDown() {
        Edk.INSTANCE.EE_EngineDisconnect();
        Edk.INSTANCE.EE_EmoStateFree(eState);
        Edk.INSTANCE.EE_EmoEngineEventFree(eEvent);
        if (DEBUG) System.out.println("Disconnected!");
    }

    private int interpretGyro() {
        int xDelta = 0;
        int yDelta = 0;
        synchronized (this) {
            xDelta = gyroX.getValue();
            yDelta = gyroY.getValue();
        }
        if (xDelta == 0 && yDelta == 0) {
            if (DEBUG) System.out.println("Tilt: 0");
            return 0;  //nothing happening.
        }
        if (Math.abs(xDelta) > MIN_GYRO_DELTA) {
            if (xDelta > 0) {
                if (DEBUG) System.out.println("Tilt: 1");
                return 1; //UP or Down, not sure yet.
            }
            else {
                if (DEBUG) System.out.println("Tilt: -1");
                return -1;     //Down or up, not sure yet.
            }
        }
        if (Math.abs(yDelta) > MIN_GYRO_DELTA) {
            if (yDelta > 0) {
                if (DEBUG) System.out.println("Tilt: 2");
                return 2; //Left or right, not sure yet.
            }
            else {
                if (DEBUG) System.out.println("Tilt: -2");
                return -2; //right or left, not sure yet.
            }
        }
        return 0;
    }

    public int getTiltDirection() {
        int tilt = 0;
        synchronized (this) {
            tilt = interpretGyro();
        }
        return tilt;
    }

    public static int interpretFrustration() {
        return 0;
    }

}
