package overthinker.client.eeg;

import java.io.*;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Optional class, will write the EEG short-term excitement levels to
 * a time-stamped file in the current working directory.
 */
public class EEGLogger {

    private String logPath = null;
    private File logFile = null;
    private String osType = null;
    private String timestamp = null;
    private FileOutputStream outStream = null;
    private OutputStreamWriter out = null;


    /**
     * Sets up a time-stamped file for future writing.
     * Save path varies depending on OS--windows 7 saves
     * to Public Documents, everything else saves to
     * current working directory.  EEG only runs on Win,
     * so to avoid clutter I chose to change save path.
     */
    public EEGLogger() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        timestamp = dateFormat.format(date).toString();

        osType = System.getProperty("os.name");
        if (osType.contains("Win") && osType.contains("7")) {
            logPath = "C:\\Users\\Public\\Documents\\"; //To avoid cluttering working directory
            logFile = new File(logPath + timestamp + ".txt");
        } else {
            logPath = System.getProperty("user.dir") + "/" + timestamp + ".txt";
            logFile = new File(logPath);
        }

        try {
            outStream = new FileOutputStream(logFile);
            out = new OutputStreamWriter(outStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the given string to the current file,
     * concatenated with sys-independent newline char.
     * @param line String to write
     */
    public void writeLine(String line) {
        try {
            out.write(line+System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *Should be called when EEG is shut down.
     */
    public void closeStreams() {
        try {
            out.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO remove main method before submission--it's for testing only.
    public static void main(String[] args) {
        System.out.println(System.getProperty("os.name"));
        EEGLogger logger = new EEGLogger();
        for (int i = 0; i < 100; i++) {
            logger.writeLine(i+"-ith 'Hello' entry.");
        }
        logger.closeStreams();
        System.exit(0);
    }
}
