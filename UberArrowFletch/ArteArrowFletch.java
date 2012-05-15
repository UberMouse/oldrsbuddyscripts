import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.GameObject;
import com.rsbuddy.script.wrappers.Item;
import nz.uberutils.arte.ArteNotifier;
import nz.uberutils.helpers.Skill;
import nz.uberutils.helpers.UberPlayer;

import java.awt.*;

@Manifest(authors = "UberMouse",
          name = "ArteArrowFletch",
          keywords = "fletching,woodcutting",
          version = 1.1,
          description = "Cuts normal tress, fletches ")
public class ArteArrowFletch extends ActiveScript implements PaintListener,
        MessageListener
{

    // ID/Names's
    private final int[] treeIDs = {5004,
                                   5005,
                                   5045,
                                   3879,
                                   3881,
                                   3882,
                                   3883,
                                   3885,
                                   3886,
                                   3887,
                                   3888,
                                   3889,
                                   3890,
                                   3891,
                                   3892,
                                   3893,
                                   3928,
                                   3967,
                                   3968,
                                   4048,
                                   4049,
                                   4050,
                                   4051,
                                   4052,
                                   4053,
                                   4054,
                                   3033,
                                   3034,
                                   3035,
                                   3036,
                                   2447,
                                   2448,
                                   1330,
                                   1331,
                                   1332,
                                   1310,
                                   1305,
                                   1304,
                                   1303,
                                   1301,
                                   1276,
                                   1277,
                                   1278,
                                   1279,
                                   1280,
                                   8742,
                                   8743,
                                   8973,
                                   8974,
                                   1315,
                                   1316};
    private final int knifeID = 946;
    private final int logID = 1511;
    private final int shaftID = 52;
    private final int headlessArrow = 53;
    private final int interfaceID = 905;
    private final int interfaceSubID = 14;
    private final int featherID = 314;
    private final String arrowTips = "arrowtips";
    private static int shaftprice;
    private static int headlessprice;
    private static int featherprice;
    private static int moneyGained = 0;
    private static int lastxp;
    private Skill fletchSkill;
    private Skill woodSkill;

    // Stats
    private long startTime;
    private int treesCut;
    private int shaftsMade;

    // Objects
    private GameObject tree;

    // Misc
    private boolean setup;

    // States/locations
    private enum States
    {
        FINDING_TREE, CHOPPING_TREE, FLETCHING_SHAFTS
    }

    private States state;

    public boolean onStart() {
        getContainer().submit(new ArteNotifier(409, true));
        startTime = System.currentTimeMillis();
        LoopTask antiban = new antiban();
        getContainer().submit(antiban);
        shaftprice = GrandExchange.lookup(shaftID).getGuidePrice();
        headlessprice = GrandExchange.lookup(headlessArrow).getGuidePrice();
        featherprice = GrandExchange.lookup(featherID).getGuidePrice();
        lastxp = Skills.getCurrentExp(Skills.FLETCHING);
        fletchSkill = new Skill(Skills.FLETCHING);
        woodSkill = new Skill(Skills.WOODCUTTING);
        return true;
    }

    public int random(int min, int max) {
        return Random.nextInt(min, max);
    }

    @Override
    public int loop() {
        try {
            Mouse.setSpeed(random(3, 8));
            if (!setup) {
                Camera.setPitch(true);
                setup = true;
            }
            if (Walking.getEnergy() >= random(55, 80))
                Walking.setRun(true);
            if (!Players.getLocal().isIdle())
                return 50;
            if (Skills.getCurrentExp(Skills.FLETCHING) > lastxp) {
                lastxp = Skills.getCurrentExp(Skills.FLETCHING);
                return 800;
            }
            if (tree != null) {
                if (!tree.getLocation().equals(getNearestTree().getLocation()))
                    tree = null;
            }
            state = getState();
            switch (state) {
                case CHOPPING_TREE:
                    chopTree();
                    break;
                case FLETCHING_SHAFTS:
                    fletch();
                    break;
                case FINDING_TREE:
                    tree = getNearestTree();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return random(500, 650);
    }

    // State functions
    public States getState() {
        if (Inventory.isFull() ||
            Players.getLocal().getAnimation() == 1298 ||
            (Inventory.contains(headlessArrow) && inventoryContains(arrowTips)) ||
            (Inventory.contains(shaftID) && Inventory.containsAll(featherID)))
            return States.FLETCHING_SHAFTS;
        else {
            if (tree == null)
                return States.FINDING_TREE;
            else
                return States.CHOPPING_TREE;
        }
    }

    public String getPrettyStatus() {
        if (state != null) {
            switch (state) {
                case FLETCHING_SHAFTS:
                    return "Fletching arrow shafts";
                case FINDING_TREE:
                    return "Finding new tree";
                case CHOPPING_TREE:
                    return "Chopping tree";
            }
        }
        return "Starting up";
    }

    // Logic functions

    public void chopTree() {
        turnToObject(tree);
        tree.interact("Chop down");
        sleep(random(400, 700));
    }

    public void fletch() {
        if (Inventory.getCount(logID) > 0) {
            if (Inventory.useItem(Inventory.getItem(knifeID), Inventory.getItem(logID))) {
                sleep(random(900, 1100));
            }
        }
        else if (Inventory.getCount(featherID) > 0) {
            if (Inventory.useItem(Inventory.getItem(featherID), Inventory.getItem(shaftID))) {
                sleep(random(900, 1100));
            }
        }
        else {
            Inventory.useItem(inventoryGetString(arrowTips), Inventory.getItem(headlessArrow));
            sleep(random(900, 1100));
        }
        if (Widgets.getComponent(interfaceID, interfaceSubID) != null)
            Widgets.getComponent(interfaceID, interfaceSubID).click();
        int timeout = 0;
        while(UberPlayer.get().getAnimation() == -1 && ++timeout <= 15)
            sleep(100);
    }

    public GameObject getNearestTree() {
        tree = Objects.getNearest(treeIDs);
        return tree;
    }

    public class antiban extends LoopTask
    {

        @Override
        public int loop() {
            if (Players.getLocal().isIdle()) {
                switch (random(0, 400)) {
                    case 0:
                        Game.openTab(Game.TAB_STATS);
                        Skills.hover(Skills.COMPONENT_WOODCUTTING);
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
                    case 12:
                        Game.openTab(Game.TAB_STATS);
                        Skills.hover(Skills.COMPONENT_FLETCHING);
                        sleep(random(500, 1250));
                        Game.openTab(Game.TAB_INVENTORY);
                        break;
                }
            }
            return 1000;
        }
    }

    // Misc functions

    public boolean inventoryContains(String itemName) {
        Item[] is = Inventory.getItems();
        for (Item i : is) {
            if (i.getName().contains(itemName))
                return true;
        }
        return false;
    }

    public Item inventoryGetString(String itemName) {
        Item[] is = Inventory.getItems();
        for (Item i : is) {
            if (i.getName().contains(itemName))
                return i;
        }
        return null;
    }

    public String getTime(long millis) {
        long time = millis / 1000;
        String seconds = Integer.toString((int) (time % 60));
        String minutes = Integer.toString((int) ((time % 3600) / 60));
        String hours = Integer.toString((int) (time / 3600));
        for (int i = 0; i < 2; i++) {
            if (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            if (hours.length() < 2) {
                hours = "0" + hours;
            }
        }
        String returnThis = hours + ":" + minutes + ":" + seconds;
        return returnThis;
    }

    public void turnToObject(GameObject object) {
        if (!object.isOnScreen()) {
            Camera.turnTo(object);
            if (!object.isOnScreen())
                Walking.getTileOnMap(object.getLocation()).clickOnMap();
        }
    }

    public void messageReceived(MessageEvent e) {
        String txt = e.getMessage();
        if (txt.contains("wood into 15")) {
            shaftsMade += 15;
            moneyGained += (shaftprice * 15);
        }
        if(txt.contains("attach feathers to")) {
            moneyGained += ((headlessprice - shaftprice - featherprice) * 15);
        }
        if (txt.contains("get some logs"))
            treesCut += 1;
    }

    public static double myCeil(double value, int precision) {
        double multiply = Math.pow(10, precision);
        return Math.ceil(value * multiply) / multiply;
    }

    // PaintUtils
    private final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                                   RenderingHints.VALUE_ANTIALIAS_ON);

    private final Color color1 = new Color(102, 102, 255, 240);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(255, 0, 0);
    private final Color color4 = new Color(0, 204, 0);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 12);

    int treesPH, shaftsPH, moneyPH;
    String TTL = "Calculating..";

    public void onRepaint(Graphics g1) {
        treesPH = (int) ((treesCut) * 3600000D / (System.currentTimeMillis() - startTime));
        shaftsPH = (int) ((shaftsMade) * 3600000D / (System.currentTimeMillis() - startTime));
        moneyPH = ((int) ((moneyGained) * 3600000D / (System.currentTimeMillis() - startTime)));
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHints(antialiasing);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color1);
        g.fillRoundRect(10, 348, 482, 106, 16, 16);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(10, 348, 482, 106, 16, 16);
        g.setFont(font1);
        g.drawString("Status: " + getPrettyStatus(), 15, 367);
        g.drawString("Shafts Made: " + shaftsMade, 15, 380);
        g.drawString("Trees cut: " + treesCut, 15, 394);
        g.drawString("Shafts P/H: " + shaftsPH, 15, 408);
        g.drawString("Trees P/H: " + treesPH, 15, 422);
        g.drawString("Money gained: " + moneyGained, 15, 436);
        g.drawString("Money gained P/H: " + moneyPH, 15, 450);
        g.drawString("Time Running:" + getTime(System.currentTimeMillis() - startTime), 150, 367);
        fletchSkill.drawSkill(g, 150, 380);
        woodSkill.drawSkill(g, 150, 395);
    }
}
