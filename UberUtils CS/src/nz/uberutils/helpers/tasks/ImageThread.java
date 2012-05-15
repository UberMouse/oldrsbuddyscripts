package nz.uberutils.helpers.tasks;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.script.methods.Environment;
import com.rsbuddy.script.task.LoopTask;
import nz.uberutils.helpers.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/18/11
 * Time: 5:59 PM
 * Package: nz.uberutils.helpers.tasks;
 */
public class ImageThread extends LoopTask implements MessageListener
{
    private static boolean hourly;
    private static boolean levelup;
    private static String  scriptName;
    private static int     hour;
    private static boolean firstRun = true;
    private static boolean onFinish;
    private static int     x, y, h, w;
    private static int hourIncrement = 3;
    private static int id            = -1;

    private void log(Object text) {
        Logger.getLogger(scriptName).info("" + text);
    }

    /*
     * Constructs ImageThread 
     * @param name Name of script to save images for
     * @param id Id of script for ArteBots
     * @param hourly Save screenshot hourly
     * @param levelup Save screenshot on levelup (Any skill)
     * @param onFinish  Save screenshot on script finish (If script has been running for more then one hour
     * @param x upper left coordinate of rectangle to crop image to
     * @param y upper right coordinate of rectangle to crop image to
     * @param h height of rectangle to crop image to
     * @param w width of rectangle to crop image to
     */
    public ImageThread(String scriptName,
                       int id,
                       boolean hourly,
                       boolean levelup,
                       boolean onFinish,
                       int x,
                       int y,
                       int w,
                       int h) {
        ImageThread.hourly = hourly;
        ImageThread.levelup = levelup;
        ImageThread.scriptName = scriptName;
        ImageThread.onFinish = onFinish;
        ImageThread.id = id;
        hour = 1;
        firstRun = true;
        ImageThread.x = x;
        ImageThread.y = y;
        ImageThread.h = h;
        ImageThread.w = w;
    }

    /**
     * Constructs new ImageThread
     *
     * @param name Name of script to save images for
     */
    public ImageThread(String name, int id) {
        this(name, id, true, true, true, 0, 339, 515, 137);
    }

    public boolean onStart() {
        try {
            File f = new File(Environment.getStorageDirectory().getCanonicalPath() +
                              System.getProperty("file.separator") +
                              "artebots" +
                              System.getProperty("file.separator") +
                              scriptName +
                              System.getProperty("file.separator"));
            if (!f.exists())
                f.mkdirs();
        } catch (IOException ignored) {
        }
        return true;
    }

    @Override
    public int loop() {
        if (hourly && !firstRun) {
            saveImage(hour + "h");
            log("Script has run for " + hour + "h's, saving screenshot");
            hour += hourIncrement;
            if (hour > 12 && hour < 24)
                hourIncrement = 2;
            else if (hour >= 24)
                hourIncrement = 1;
        }
        if (firstRun)
            firstRun = false;
        return 3600000 * hourIncrement;
    }

    public void messageReceived(MessageEvent messageEvent) {
        if (levelup) {
            if (messageEvent.isAutomated()) {
                if (messageEvent.getMessage().contains("You've just advanced")) {
                    saveImage("levelup");
                    log("You just leveled up! Saving screenshot");
                }
            }
        }
    }

    public void saveImage(String name) {
        String imgHash = uploadToImgur(Environment.takeScreenshot(true).getSubimage(x, y, w, h));
        try {
            new URL("http://artebots.com/submit_proggie.php?id=" +
                    id +
                    "&hash=" +
                    44 +
                    "&url=" +
                    imgHash).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onFinish() {
        if (onFinish && hour >= 2) {
            saveImage("end");
            log("Script stopping, saving screenshot");
        }
    }

    private String uploadToImgur(BufferedImage bi) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);
            URL url = new URL("http://api.imgur.com/2/upload");

            //encodes picture with Base64 and inserts api key
            String data = URLEncoder.encode("image", "UTF-8") +
                          "=" +
                          URLEncoder.encode(String.valueOf(Base64.encode(baos.toByteArray())), "UTF-8");
            data += "&" +
                    URLEncoder.encode("key", "UTF-8") +
                    "=" +
                    URLEncoder.encode("4cd9fccda3f5c1c0a13e5104f4e74bd1", "UTF-8");

            // opens connection and sends data
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String xml = "";
            String temp;
            while ((temp = in.readLine()) != null) {
                xml += temp + "\n";
            }
            Pattern regex = Pattern.compile("<hash>[a-zA-Z0-9]+</hash>");
            Matcher match = regex.matcher(xml);
            if (match.find()) {
                String imghash = match.group();
                imghash = imghash.replace("<hash>", "");
                imghash = imghash.replace("</hash>", "");
                return imghash;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}