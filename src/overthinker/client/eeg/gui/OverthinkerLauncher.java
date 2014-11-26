package overthinker.client.eeg.gui;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import overthinker.client.eeg.EEGMonitor;
import overthinker.client.eeg.Edk;
import overthinker.client.eeg.EdkErrorCode;
import overthinker.client.eeg.EmoState;

public class OverthinkerLauncher {

    public static void main(String args[]) {
        EEGMonitor monitor = new EEGMonitor();
        monitor.start();
    }
}