package nz.uberutils.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 7/20/11
 * Time: 11:51 PM
 * Package: nz.uberutils.helpers;
 */
public class Logger
{
    public static final int LOGGING_MODE_ALL     = 0;
    public static final int LOGGING_MODE_DEBUG   = 1;
    public static final int LOGGING_MODE_WARNING = 2;
    public static final int LOGGING_MODE_INFO    = 3;

    private static Logger instance;

    private final ArrayList<String> outputList    = new ArrayList<String>();
    private final SimpleDateFormat  dFormat       = new SimpleDateFormat("hh:mm:ss.SSS");
    private       File              logFile       = null;
    private       BufferedWriter    wr            = null;
    private       int               loggingMode   = 0;
    private       String            scriptName    = null;
    private       boolean           stop          = false;
    private       String            fileExtension = ".txt";

    public Logger(File logFile, String name, int loggingLevel, String fileExt) {
        fileExtension = fileExt;
        setLogFile(logFile);
        setName(name);
        setLoggingMode(loggingLevel);
        instance = this;
        new Thread(new OutputThread()).start();
    }

    public Logger(String logFile, String name, int loggingLevel, String fileExt) {
        this(new File(logFile), name, loggingLevel, fileExt);
    }

    public Logger(String logFile, String name) {
        this(logFile, name, 1, ".txt");
    }

    public static Logger instance() {
        return instance;
    }

    /**
     * Sets File to write to for logging
     *
     * @param fName name of file to construct File object to write to
     */
    public void setLogFile(String fName) {
        setLogFile(new File(fName));
    }

    /**
     * Sets File to write to for logging
     *
     * @param f File to write to
     */
    public void setLogFile(File f) {
        String fName = f.getName() +
                       "-" +
                       new SimpleDateFormat("MMM-dd-EEE.hh-mm-ss").format(new Date()) +
                       fileExtension;
        f = new File(f.getParent() + System.getProperty("file.separator") + fName);
        logFile = f;
        if (!f.exists())
            try {
                if (!f.getParentFile().exists())
                    f.getParentFile().mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                System.err.println("Error creating new logfile");
                e.printStackTrace();
            }
        try {
            wr = new BufferedWriter(new FileWriter(f));
        } catch (IOException e) {
            System.err.println("Failed to open log file for writing");
            e.printStackTrace();
        }
    }

    /**
     * Sets current logging level
     *
     * @param mode int representing logging level
     */
    public void setLoggingMode(int mode) {
        loggingMode = mode;
    }

    public void setName(String name) {
        scriptName = name;
    }

    /**
     * Perform cleanup and finishing operations, should always be closed on application exit
     */
    public void cleanup() {
        stop = true;
        if (wr != null) {
            try {
                wr.close();
            } catch (IOException e) {
                System.err.println("Error closing file writer");
                e.printStackTrace();
            }
        }
    }

    /**
     * Log to logfile at given log level with message
     *
     * @param level   log level to log at
     * @param message message to log to file
     */
    public void log(int level, String message, Object... replacements) {
        if (level < loggingMode)
            return;
        for (Object replace : replacements)
            message = message.replaceFirst("\\{\\}", replace.toString());
        String lvlMessage = "";
        switch (level) {
            case 0:
                lvlMessage = "TRACE";
                break;
            case 1:
                lvlMessage = "DEBUG";
                break;
            case 2:
                lvlMessage = "WARN";
                break;
            case 3:
                lvlMessage = "INFO";
                break;
        }
        String msg = lvlMessage + ": " + message;
        if (lvlMessage.equals("TRACE"))
            java.util.logging.Logger.getLogger(scriptName).info(msg);
        outputList.add(msg);
    }

    /**
     * Log to logfile at trace level
     *
     * @param message      message to log
     * @param replacements Array of replacements for {} place holders
     */
    public void trace(String message, Object... replacements) {
        log(LOGGING_MODE_ALL, message, replacements);
    }

    /**
     * Log to logfile at debug level
     *
     * @param message      message to log
     * @param replacements Array of replacements for {} place holders
     */
    public void debug(String message, Object... replacements) {
        log(LOGGING_MODE_DEBUG, message, replacements);
    }

    /**
     * Log to logfile at warning level
     *
     * @param message      message to log
     * @param replacements Array of replacements for {} place holders
     */
    public void warn(String message, Object... replacements) {
        log(LOGGING_MODE_WARNING, message, replacements);
    }

    /**
     * Log to logfile at info level
     *
     * @param message      message to log
     * @param replacements Array of replacements for {} place holders
     */
    public void info(String message, Object... replacements) {
        log(LOGGING_MODE_INFO, message, replacements);
    }

    /**
     * Log to logfile at trace level
     *
     * @param message message to log
     */
    public void trace(String message) {
        trace(message, "");
    }

    /**
     * Log to logfile at debug level
     *
     * @param message message to log
     */
    public void debug(String message) {
        debug(message, "");
    }

    /**
     * Log to logfile at warning level
     *
     * @param message message to log
     */
    public void warn(String message) {
        warn(message, "");
    }

    /**
     * Log to logfile at info level
     *
     * @param message message to log
     */
    public void info(String message) {
        info(message, "");
    }

    /**
     * Class for handling threaded file writing to prevent slowdowns
     */
    class OutputThread implements Runnable
    {
        public void run() {
            while (!stop) {
                try {
                    if (!outputList.isEmpty() && wr != null) {
                        try {
                            wr.write(dFormat.format(new Date()) + " " + scriptName + " " + outputList.get(0) + "\r\n");
                            wr.flush();
                            outputList.remove(0);
                        } catch (IOException e) {
                            System.err.println("Error writing output to log");
                            e.printStackTrace();
                        }
                    }
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}