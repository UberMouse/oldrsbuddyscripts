import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Manifest(authors = "UberMouse",
          name = "Arzinian gold miner",
          keywords = "Mining",
          version = 1.1,
          description = "Mines and banks ore with dwarven boatman using the arzinian mines")
public class ArteGoldMine extends ActiveScript implements PaintListener,
                                                          MessageListener
{

    // ID's
    private static final int[] goldIDs = {2098, 2099, 34977, 34976, 5989};
    private static final int[] gemIDs = {1623, 1619, 1621, 1617};
    private static final int dondakenID = 1836;
    private static final int boatman = 1845;
    private static final int goldID = 444;
    private static final int goldHelmetID = 4567;
    private static final int ringOfCharosID = 6465;

    // Tiles
    private static final Tile mineTile = new Tile(2824, 10169);
    private static final Tile bankTile = new Tile(2870, 10167);

    // Areas
    private static final Area mineEntrance = new Area(new Tile(2820, 10164), new Tile(2829, 10171));
    private static final Area bank = new Area(new Tile(2866, 10159), new Tile(2874, 10170));
    private static final Area walkingArea = new Area(new Tile(2831, 10160), new Tile(2864, 10171));
    private static final Area mine = new Area(new Tile(2622, 4990), new Tile(2590, 4920));

    // Paths
    private static final Tile[] mineToBank = {new Tile(2825, 10169),
                                              new Tile(2837, 10165),
                                              new Tile(2848, 10162),
                                              new Tile(2860, 10167),
                                              new Tile(2871, 10168)};

    // Stats
    private int oresMined = 0;
    private int goldXP = 65;
    private long startTime;
    private int goldGP;
    private int startXP = 0;
    private int levelsGained = 0;
    private double goldSmithXP = 22.5;
    private double goldSmithXPG = 53.2;

    // Misc
    private boolean setup = false;
    GameObject ore;
    boolean banked = false;
    private DecimalFormat k = new DecimalFormat("#.#");
    private boolean ringOfCharos;
    String className = this.getClass().getName();

    // States/locations
    private enum States
    {
        FAILSAFE, MINING, BANKING, WALKING_BANK, WALKING_MINE, ENTERING_MINE, LEAVING_MINE, DROPPING_GEMS, UNK
    }

    private enum Locations
    {
        MINE, MINE_ENTRANCE, BANK_TO_MINE, BANK, UNK
    }

    private States state;

    public boolean onStart() {
        LoopTask antiBan = new antiban();
        getContainer().submit(antiBan);
        return true;
    }

    public int random(int min, int max) {
        return Random.nextInt(min, max);
    }

    @Override
    public int loop() {
        try {
            if (Game.isLoggedIn()) {
                Mouse.setSpeed(random(4, 7));
                Camera.setPitch(180);
                if (Walking.getEnergy() >= random(55, 80))
                    Walking.setRun(true);
                if (startXP == 0)
                    startXP = Skills.getCurrentExp(Skills.MINING);
                if (!setup) {
                    goldGP = GrandExchange.lookup(goldID).getGuidePrice();
                    startTime = System.currentTimeMillis();
                    if (Equipment.containsOneOf(ringOfCharosID)) {
                        ringOfCharos = true;
                        log("Detected Ring Of Charos, changing paintType calculations");
                    }
                    setup = true;
                    return 1;
                }
                if (!Players.getLocal().isIdle())
                    return random(300, 500);
                state = getState();
                switch (state) {
                    case DROPPING_GEMS:
                        Inventory.getItem(gemIDs).interact("drop");
                        break;
                    case FAILSAFE:
                        Inventory.getItem(goldID).interact("drop");
                    case ENTERING_MINE:
                        enterMine();
                        break;
                    case MINING:
                        if (!Players.getLocal().isMoving() &&
                            !(Players.getLocal().getAnimation() == 624) &&
                            !(Players.getLocal().getAnimation() == 6752))
                            mineOre();
                        break;
                    case LEAVING_MINE:
                        Equipment.getItem(Equipment.HELMET).interact("Remove");
                        break;
                    case WALKING_BANK:
                        Path path = Walking.newTilePath(mineToBank).randomize(1, 1);
                        path.traverse();
                        break;
                    case WALKING_MINE:
                        Path pathBank = Walking.newTilePath(mineToBank).reverse().randomize(1, 1);
                        pathBank.traverse();
                        break;
                    case BANKING:
                        bank();
                        break;

                    case UNK:
                        findWayBack();
                        break;
                }
            }
        } catch (Exception e) {
        }
        return random(500, 1200);
    }

    // State/location functions
    public States getState() {
        if (Inventory.getCount(gemIDs) > 0)
            return States.DROPPING_GEMS;
        if (Inventory.getCount() == 28 && !Inventory.contains(goldHelmetID))
            return States.FAILSAFE;
        switch (getLocation()) {
            case MINE_ENTRANCE:
                if (Inventory.getCount(goldIDs) == 27 || Inventory.getCount() == 28)
                    return States.WALKING_BANK;
                else
                    return States.ENTERING_MINE;
            case MINE:
                if (Inventory.getCount(goldIDs) == 27 || Inventory.getCount() == 27)
                    return States.LEAVING_MINE;
                else
                    return States.MINING;
            case BANK:
                if (Inventory.getCount(goldID) > 0)
                    return States.BANKING;
                else
                    return States.WALKING_MINE;
            case BANK_TO_MINE:
                if (Inventory.getCount(goldID) > 0)
                    return States.WALKING_BANK;
                else
                    return States.WALKING_MINE;

        }
        return States.UNK;
    }

    public Locations getLocation() {
        if (mineEntrance.contains(Players.getLocal().getLocation()))
            return Locations.MINE_ENTRANCE;

        if (bank.contains(Players.getLocal().getLocation()))
            return Locations.BANK;

        if (walkingArea.contains(Players.getLocal().getLocation()))
            return Locations.BANK_TO_MINE;

        String[] loc = Players.getLocal().getLocation().toString().split(",");
        int parsedLoc = Integer.parseInt(loc[1].toString().replace(")", "").trim());
        if (parsedLoc > 3800 && parsedLoc < 8000)
            return Locations.MINE;

        return Locations.UNK;
    }

    // Logic functions
    public void mineOre() {
        if (ore == null) {
            ore = Objects.getNearest(goldIDs);
            if (ore != null) {
                turnToObject(ore);
                if (!Players.getLocal().isMoving())
                    ore.interact("Mine");
                while (Players.getLocal().getAnimation() == 624 || Players.getLocal().getAnimation() == 6752) {
                    if (Players.getLocal().isMoving() || !isActive())
                        break;
                    sleep(100);
                }
                ore = null;
            }
        }
    }

    public void bank() {
        Npc boatmanNPC = Npcs.getNearest(boatman);
        if (boatmanNPC != null) {
            turnToNPC(boatmanNPC);
            sleep(random(300, 750));
            while (!boatmanNPC.interact("Deliver-gold")) {
                if (!Players.getLocal().isMoving())
                    boatmanNPC.interact("Deliver-gold");
            }

        }
        banked = false;
        int curGoldCount = Inventory.getCount(goldID);
        String oldLoc = Players.getLocal().getLocation().toString();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(new Runnable()
        {
            public void run() {
                banked = true;
            }
        }, 15, TimeUnit.SECONDS);
        while (!banked) {
            if (Widgets.canContinue()) {
                Widgets.clickContinue();
                continue;
            }
            sleep(random(300, 600));
            while (Widgets.getComponent(228, 1).containsText("Select an"))
                Widgets.getComponent(228, 2).click();
            sleep(random(300, 600));
            if (curGoldCount != Inventory.getCount(goldID))
                banked = true;
        }
    }

    public void enterMine() {
        Npc dondaken = Npcs.getNearest(dondakenID);
        if (Inventory.containsOneOf(goldHelmetID)) {
            Inventory.getItem(goldHelmetID).interact("wear");
        }
        else if (!Equipment.containsOneOf(goldHelmetID)) {
            log.severe("No gold helemet detected! Start with one in Inventory or equiped!");
        }
        turnToNPC(dondaken);
        if (dondaken != null) {
            if (!Players.getLocal().isMoving())
                dondaken.interact("Fire-into-rock");
        }
    }

    public void findWayBack() {
        int disBank = Calculations.distanceTo(bankTile);
        int disMine = Calculations.distanceTo(mineTile);
        if (disMine < disBank)
            Walking.findPath(mineTile).traverse();
        else
            Walking.findPath(bankTile).traverse();
    }

    // Misc functions
    public void turnToObject(GameObject object) {
        try {
            if (!object.isOnScreen()) {
                Camera.turnTo(object);
                if (!object.isOnScreen())
                    Walking.getTileOnMap(object.getLocation()).clickOnMap();
            }
        } catch (Throwable t) {
        }
    }

    public void turnToNPC(Npc npc) {
        try {
            if (!npc.isOnScreen()) {
                Camera.turnTo(npc);
                if (!npc.isOnScreen())
                    Walking.getTileOnMap(npc.getLocation()).clickOnMap();
            }
        } catch (Throwable t) {
        }
    }

    // START: Code generated using Enfilade's Easel
    private final Color color1 = new Color(102, 102, 255, 235);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(255, 0, 0);
    private final Color color4 = new Color(0, 204, 0);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 12);

    int profit, xpGained, XPTL, xpPH;
    double percentLoss, oresKept = 0;
    String TTL = "Calculating..";
    double smithXP, smithXPG;

    public void onRepaint(Graphics g1) {
        percentLoss = (ringOfCharos) ? 0.9 : 0.8;
        oresKept = Math.floor((oresMined * percentLoss));
        profit = (int) ((oresKept * goldGP));
        xpGained = oresMined * goldXP;
        smithXP = (oresKept * goldSmithXP);
        smithXPG = (oresKept * goldSmithXPG);
        xpPH = (int) ((xpGained) * 3600000D / (System.currentTimeMillis() - startTime));
        XPTL = Skills.getExpToNextLevel(Skills.MINING);
        if (xpPH != 0) {
            long ttlCalc = (long) (XPTL * 3600000D) / xpPH;
            TTL = getTime(ttlCalc);
        }
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color1);
        g.fillRoundRect(10, 348, 482, 106, 16, 16);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(10, 348, 482, 106, 16, 16);
        g.setFont(font1);
        g.drawString("Status: " + getPrettyStatus(), 16, 367);
        g.drawString("Ore's mined (P/H)(Amount kept): " +
                     oresMined +
                     " (" +
                     (int) ((oresMined) * 3600000D / (System.currentTimeMillis() - startTime)) +
                     ")" +
                     "(" +
                     oresKept +
                     ")", 15, 380);
        g.drawString("Profit Gained (P/H): " +
                     profit +
                     " (" +
                     (int) ((profit) * 3600000D / (System.currentTimeMillis() - startTime)) +
                     ")", 15, 394);
        g.drawString("XP Gained (Levels)(P/H): " + xpGained + " (" + levelsGained + ")" + "(" + xpPH + ")", 15, 409);
        g.drawString("Smithing XP Gained (With gauntlets): " + smithXP + " (" + myCeil(smithXPG, 1) + ")", 14, 421);
        g.setColor(color3);
        g.fillRoundRect(16, 429, 237, 22, 16, 16);
        g.setColor(color2);
        g.drawRoundRect(16, 429, 237, 22, 16, 16);
        g.setColor(color4);
        g.fillRoundRect(17, 430, (int) (Skills.getPercentToNextLevel(Skills.MINING) * 2.37), 21, 17, 17);
        g.setColor(color2);
        g.drawString(Skills.getPercentToNextLevel(Skills.MINING) + "%", 113, 444);
        g.drawString("XPTL:" + XPTL, 263, 445);
        g.drawString("TTL:" + TTL, 368, 446);
        g.drawString("Time Running:" + getTime(System.currentTimeMillis() - startTime), 258, 367);
    }

    // END: Code generated using Enfilade's Easel

    public String getPrettyStatus() {
        if (state != null) {
            switch (state) {
                case BANKING:
                    return "Banking";
                case WALKING_BANK:
                    return "Walking to bank";
                case WALKING_MINE:
                    return "Walking to mine";
                case ENTERING_MINE:
                    return "Entering mine";
                case MINING:
                    return "Mining";
                case LEAVING_MINE:
                    return "Leaving mine";
                case DROPPING_GEMS:
                    return "Dropping Gems";
                default:
                    return "Unknown";
            }
        }
        return "Starting up";
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

    public void messageReceived(MessageEvent e) {
        String txt = e.getMessage();
        if (txt.contains("You've just advanced a"))
            levelsGained++;
        if (txt.contains("You manage to mine"))
            oresMined++;
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
                switch (random(0, 40)) {
                    case 0:
                        Game.openTab(Game.TAB_STATS);
                        Mouse.move(704 + random(-30, 29), 223 + random(-13, 13));
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
