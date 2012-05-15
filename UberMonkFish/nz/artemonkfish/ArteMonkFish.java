package nz.artemonkfish;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.Area;
import com.rsbuddy.script.wrappers.Tile;
import nz.artemonkfish.misc.GameConstants;
import nz.artemonkfish.misc.Strategy;
import nz.artemonkfish.stratagies.Banking;
import nz.artemonkfish.stratagies.Fishing;
import nz.artemonkfish.stratagies.Movement;
import nz.uberutils.arte.ArteNotifier;
import nz.uberutils.helpers.Skill;
import nz.uberutils.helpers.Utils;

import java.awt.*;
import java.util.LinkedList;

@Manifest(authors = "UberMouse",
          name = "ArteMonkFish",
          keywords = "Fishing",
          version = 1.0,
          description = "Fishes and banks monk fish at the piscatoris Fishing colony")
public class ArteMonkFish extends ActiveScript implements PaintListener,
                                                          MessageListener
{

    private static LinkedList<Strategy> strategies = new LinkedList<Strategy>();


    // Areas
    private static final Area bankArea = new Area(new Tile(2327, 3686),
                                                  new Tile(2332, 3693));
    private static final Area fishArea = new Area(new Tile(2312, 3683),
                                                  new Tile(2353, 3702));
    int x = 1;

    // Paths

    // Stats
    private static int fishCaught = 0;
    private static long startTime;
    private static int monkPrice;
    private static String status = "";
    private static Skill fishingSkill;

    // Misc
    private static boolean setup = false;

    public boolean onStart() {
        getContainer().submit(new ArteNotifier(406, true));
        getContainer().submit(new antiban());
        fishingSkill = new Skill(Skills.FISHING);
        strategies.add(new Fishing(this));
        strategies.add(new Banking(this));
        strategies.add(new Movement(this));
        return true;
    }

    public int random(int min, int max) {
        return Random.nextInt(min, max);
    }

    @Override
    public int loop() {
        try {
            if (Game.isLoggedIn()) {
                if (Walking.getEnergy() >= random(55, 80))
                    Walking.setRun(true);
                if (!setup) {
                    Camera.setPitch(true);
                    monkPrice = Math.abs(GrandExchange.lookup(GameConstants.MONK_ID).getGuidePrice());
                    startTime = System.currentTimeMillis();
                    setup = true;
                }
                if (Players.getLocal().getAnimation() != -1)
                    return 400;
                Mouse.setSpeed(random(2, 5));
                for (Strategy strategy : strategies) {
                    if (strategy.isValid()) {
                        status = strategy.getStatus();
                        strategy.execute();
                        return random(400, 500);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return random(500, 1200);
    }

    // START: Code generated using Enfilade's Easel
    private final Color color1 = new Color(102, 102, 255, 225);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(255, 0, 0);
    private final Color color4 = new Color(0, 204, 0);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 12);

    int profit, xpGained, XPTL, xpPH;
    String TTL = "Calculating..";

    public void onRepaint(Graphics g1) {
        profit = fishCaught * monkPrice;
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color1);
        g.fillRoundRect(10, 348, 482, 106, 16, 16);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(10, 348, 482, 106, 16, 16);
        g.setFont(font1);
        g.drawString("Status: " + status, 16, 367);
        g.drawString(
                "Fish caught: (P/H): "
                + fishCaught
                + " ("
                + (int) ((fishCaught) * 3600000D / (System
                                                            .currentTimeMillis() - startTime)) + ")", 15,
                380);
        g.drawString(
                "Profit Gained (P/H): "
                + profit
                + " ("
                + (int) ((profit) * 3600000D / (System
                                                        .currentTimeMillis() - startTime)) + ")", 15,
                394);
        g.drawString(
                "XP Gained (Levels)(P/H): "
                + fishingSkill.xpGained()
                + " ("
                + fishingSkill.levelsGained()
                + ")"
                + "("
                + fishingSkill.xpPH() + ")", 15,
                409);
        g.setColor(color3);
        g.fillRoundRect(16, 429, 237, 22, 16, 16);
        g.setColor(color2);
        g.drawRoundRect(16, 429, 237, 22, 16, 16);
        g.setColor(color4);
        g.fillRoundRect(17, 430,
                        (int) (fishingSkill.percentTL() * 2.37),
                        21, 16, 16);
        g.setColor(color2);
        g.drawString(fishingSkill.percentTL() + "%", 113,
                     444);
        g.drawString("XPTL:" + fishingSkill.xpTL(), 263, 445);
        g.drawString("TTL:" + fishingSkill.timeToLevel(), 368, 446);
        g.drawString("Time Running:"
                     + Utils.parseTime(System.currentTimeMillis() - startTime), 258, 367);
    }

    // END: Code generated using Enfilade's Easel

    public void messageReceived(MessageEvent e) {
        String txt = e.getMessage();
        if (txt.contains("You catch a"))
            fishCaught++;
    }

    public static double myCeil(double value, int precision) {
        double multiply = Math.pow(10, precision);
        return Math.ceil(value * multiply) / multiply;
    }

    public class antiban extends LoopTask
    {
        @Override
        public int loop() {
            if (Players.getLocal().isIdle()) {
                switch (random(0, 400)) {
                    case 0:
                        Game.openTab(Game.TAB_STATS);
                        Mouse.move(705 + random(-30, 29), 277 + random(-13, 13));
                        sleep(random(500, 1250));
                        Game.openTab(Game.TAB_INVENTORY);
                        break;
                    case 1:
                        Game.openTab(Game.TAB_FRIENDS);
                        sleep(random(500, 1250));
                        Game.openTab(Game.TAB_INVENTORY);
                        break;
                    case 2:
                        Camera.setCompassAngle(random(0, 180));
                        break;
                    case 6:
                        Mouse.moveSlightly();
                }
            }
            return 1000;
        }
    }
}
