package overthinker.client.eeg;

import java.io.*;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sidholman on 12/7/14.
 */
public class EEGLogger {

    private String logPath = null;
    private File logFile = null;
    private String osType = null;
    private String timestamp = null;
    private FileOutputStream outStream = null;
    private OutputStreamWriter out = null;

    public EEGLogger() {

        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
        Date date = new Date();
        timestamp = dateFormat.format(date).toString();//date.toString();

        osType = System.getProperty("os.name");
        if (osType.contains("win") && osType.contains("7")) {
            logPath = "C:\\Users\\Public\\Public Documents\\";
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

    public void writeLine(String line) {
        try {
            out.write(line+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        EEGLogger logger = new EEGLogger();
        for (int i = 0; i < 100; i++) {
            logger.writeLine(i+"-ith 'Hello' entry.");
        }
        logger.closeStreams();
        System.exit(0);
    }
}
