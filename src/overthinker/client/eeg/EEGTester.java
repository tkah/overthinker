package overthinker.client.eeg;

import overthinker.client.eeg.EEGMonitor;

/**
 * For verifying the EEG data is being captured.
 * Kept in project for testing, in case GUI has errors.
 */
public class EEGTester {

    public static void main(String args[]) {
        EEGMonitor monitor = new EEGMonitor();
        monitor.start();
    }
}