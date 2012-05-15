package nz.ubermouse.artealtar.utils;

import com.rsbuddy.script.methods.Skills;
import com.rsbuddy.script.util.Timer;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.artealtar.data.GameConstants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:29 PM
 * Package: nz.ubermouse.artealtar.utils;
 */
public class Misc extends Debug
{
    // Stats
    public static Timer updateTimer;
    public static final Map<Integer, Integer> boneMap       = new HashMap<Integer, Integer>();
    public static final Map<Integer, Integer> lastBoneMap   = new HashMap<Integer, Integer>();
    public static       String                statsUsername = "";
    public static int  lastBonesUsed;
    public static long lastTimeMillis;
    public static long lastXpGained;
    public static long lastLevelsGained;
    public static int  lastTripsMade;
    public static long expGained;
    public static long startLevel;
    public static int  bonesUsed = 0;
    public static int  tripsMade = 0;
    public static long st        = 0;

    public static boolean actionsContains(String[] actions, String contains) {
        for (String action : actions) {
            if (action == null)
                continue;
            if (action.toLowerCase().contains(contains.toLowerCase()))
                return true;
        }
        return false;
    }

    public static boolean actionsContains(GameObject GameObject, String contains) {
        return actionsContains(GameObject.getDef().getActions(), contains);
    }

    public static void updateSignature() {
        try {
            URL url;
            URLConnection urlConn;
            url = new URL("http://uberdungeon.com/updaters/arbiprayer.php");
            urlConn = url.openConnection();
            urlConn.setRequestProperty("User-Agent", "| supersecret |");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String content = "";
            String[] bones = {"normalBones", "bigBones", "dragonBones", "frostDragonBones"};
            for (int i = 0; i < bones.length; i++) {
                content += bones[i] +
                           "=" +
                           (boneMap.get(GameConstants.boneIDs[i]) - lastBoneMap.get(GameConstants.boneIDs[i])) +
                           "&";
            }
            String[] stats = {"bonesUsed", "timeSpent", "xpGained", "levelsGained", "uname", "tripsMade"};
            Object[] data = {bonesUsed - lastBonesUsed,
                             (System.currentTimeMillis() - st) - lastTimeMillis,
                             expGained - lastXpGained,
                             (Skills.getRealLevel(Skills.PRAYER) - startLevel) - lastLevelsGained,
                             statsUsername,
                             tripsMade - lastTripsMade};
            for (int i = 0; i < stats.length; i++) {
                content += stats[i] + "=" + data[i] + "&";
            }
            lastBonesUsed = bonesUsed;
            lastXpGained = expGained;
            lastTimeMillis = System.currentTimeMillis() - st;
            lastLevelsGained = Skills.getRealLevel(Skills.PRAYER) - startLevel;
            lastTripsMade = tripsMade;
            for (int id : GameConstants.boneIDs) {
                lastBoneMap.put(id, boneMap.get(id));
            }
            content = content.substring(0, content.length() - 1);
            debug(content);
            OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
            wr.write(content);
            wr.flush();
            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                debug(line);
                if (line.toLowerCase().contains("successfully"))
                    Logger.getLogger("Stats").info(line.replace("<br />", ""));
            }
            wr.close();
            rd.close();
            updateTimer.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
