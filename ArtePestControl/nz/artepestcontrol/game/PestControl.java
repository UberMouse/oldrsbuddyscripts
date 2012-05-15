package nz.artepestcontrol.game;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.wrappers.Area;
import com.rsbuddy.script.wrappers.Npc;
import com.rsbuddy.script.wrappers.Tile;
import nz.artepestcontrol.misc.GameConstants;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.Logger;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/10/11
 * Time: 11:23 AM
 * Package: nz.artepestcontrol.threads;
 */
public class PestControl extends LoopTask
{
    private static int timesWon    = 0;
    private static int timesLost   = 0;
    private static int timesFailed = 0;
    private static boolean addedGame;
    private static      Area              knightArea             = null;
    public static       int               commendations          = 0;
    public static       int               commendationsEarned    = 0;
    public static       int               commendationMultiplier = 0;
    public static final ArrayList<Job>    jobs                   = new ArrayList<Job>();
    public static final ArrayList<Portal> portals                = new ArrayList<Portal>();
    public static Tile knightLoc;

    @Override
    public int loop() {
        if (Widgets.getComponent(407, 16) != null && Widgets.getComponent(407, 16).isValid()) {
            String txt = Widgets.getComponent(407, 16).getText();
            int commendations = Integer.parseInt(txt.replace("Commendations: ", ""));
            if (PestControl.commendations != commendations)
                PestControl.commendations = commendations;
        }
        if (!addedGame) {
            if (Utils.getWidgetWithText("was killed") != null) {
                timesLost++;
                addedGame = true;
                onGameFinish();
            }
            else if (Utils.getWidgetWithText("destroy all the portals") != null) {
                timesWon++;
                commendations += commendationMultiplier;
                commendationsEarned += commendationMultiplier;
                addedGame = true;
                onGameFinish();
            }
            else if (Utils.getWidgetWithText("lack of zeal") != null) {
                timesFailed++;
                addedGame = true;
                onGameFinish();
            }
        }
        if (getKnightArea() == null) {
            Npc knight = Npcs.getNearest(GameConstants.VOID_KNIGHT);
            if (knight != null) {
                setKnightArea(new Area(new Tile(knight.getLocation().getX() + 6, knight.getLocation().getY() + 4),
                        new Tile(knight.getLocation().getX() - 6, knight.getLocation().getY() - 4)));
                portals.add(new Portal("W", knight.getLocation()));
                portals.add(new Portal("E", knight.getLocation()));
                portals.add(new Portal("SE", knight.getLocation()));
                portals.add(new Portal("SW", knight.getLocation()));
            }
        }
        return 100;
    }

    public static void onGameStart() {
        addedGame = false;
    }

    public static void onGameFinish() {
        knightArea = null;
        portals.clear();
        Logger.instance().trace("Game end, cleared portals");
    }

    public static int timesWon() {
        return timesWon;
    }

    public static int timesLost() {
        return timesLost;
    }

    public static Area getKnightArea() {
        return knightArea;
    }

    public static void setKnightArea(Area area) {
        knightArea = area;
    }

    public static int timesFailed() {
        return timesFailed;
    }
}
