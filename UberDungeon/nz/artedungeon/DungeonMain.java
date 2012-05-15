package nz.artedungeon;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.util.Timer;
import com.rsbuddy.script.wrappers.Npc;
import com.rsbuddy.script.wrappers.Tile;
import nz.artedungeon.bosses.*;
import nz.artedungeon.common.Plugin;
import nz.artedungeon.common.PuzzlePlugin;
import nz.artedungeon.common.Strategy;
import nz.artedungeon.dungeon.Dungeon;
import nz.artedungeon.dungeon.EnemyDef;
import nz.artedungeon.dungeon.Explore;
import nz.artedungeon.dungeon.MyPlayer;
import nz.artedungeon.dungeon.doors.Door;
import nz.artedungeon.dungeon.doors.Skill;
import nz.artedungeon.dungeon.rooms.Boss;
import nz.artedungeon.dungeon.rooms.Normal;
import nz.artedungeon.dungeon.rooms.Puzzle;
import nz.artedungeon.dungeon.rooms.Room;
import nz.artedungeon.misc.FailSafeThread;
import nz.artedungeon.misc.GameConstants;
import nz.artedungeon.puzzles.*;
import nz.artedungeon.strategies.*;
import nz.artedungeon.utils.PluginFactory;
import nz.artedungeon.utils.RSArea;
import nz.artedungeon.utils.RoomUpdater;
import nz.artedungeon.utils.Util;
import nz.uberutils.arte.ArteNotifier;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Utils;
import nz.uberutils.paint.PaintController;
import nz.uberutils.paint.components.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.LineMetrics;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

@Manifest(authors = {"UberMouse"},
          keywords = "Dungeoneering",
          name = "ArteDungeon",
          version = 0.24,
          description = "Gets you 99 dungeoneering, so you don't have to")
public class DungeonMain extends ActiveScript implements PaintListener,
                                                         MessageListener,
                                                         MouseMotionListener,
                                                         MouseListener
{
    // Arrays
    private final       LinkedList<Strategy>     strategies = new LinkedList<Strategy>();
    public static final LinkedList<Plugin>       bosses     = new LinkedList<Plugin>();
    private final       LinkedList<PuzzlePlugin> puzzles    = new LinkedList<PuzzlePlugin>();

    // Objects

    // Classes

    // Booleans
    public static final boolean debug = true;
    public boolean foundBoss;
    public boolean prestige;
    public boolean inDungeon;
    public static boolean showPaint = true;

    // Stats
    private nz.uberutils.helpers.Skill attSkill;
    private nz.uberutils.helpers.Skill strSkill;
    private nz.uberutils.helpers.Skill defSkill;
    private nz.uberutils.helpers.Skill rangeSkill;
    private nz.uberutils.helpers.Skill magicSkill;
    private nz.uberutils.helpers.Skill dungSkill;
    private nz.uberutils.helpers.Skill conSkill;
    public int dungeonsDone   = 0;
    public int prestiegeCount = 0;
    private int  lastDungeonsDone;
    private long startTime;
    private long lastTimeMillis;
    private final Timer updateTimer  = new Timer(300000);
    public static int   timesAborted = 0;

    //General variables
    private String status           = "";
    public  int    teleportFailSafe = 0;
    private Point  m                = new Point(0, 0);
    private Room   currentRoom      = new Normal(new RSArea(new Tile[]{}), new LinkedList<Door>(), this);

    //Paint images/vars
    private              Image                    pBackground  = null;
    private static final String                   PAINT_BASE   = "http://uberdungeon.com/paintimages/";
    private static final String                   STORAGE_BASE = Environment.getStorageDirectory() + "/uberdungeon/";
    private static final ArrayList<PClickableURL> urls         = new ArrayList<PClickableURL>();
    public static        int                      menuIndex    = 0;
    public static        int                      subMenuIndex = 0;
    private static final PFrame                   mainFrame    = new PFrame("overview")
    {
        public boolean shouldPaint() {
            return DungeonMain.menuIndex == 0;
        }

        public boolean shouldHandleMouse() {
            return shouldPaint();
        }
    };

    private static final PFrame optionFrame = new PFrame("options")
    {
        public boolean shouldPaint() {
            return DungeonMain.menuIndex == 1;
        }

        public boolean shouldHandleMouse() {
            return shouldPaint();
        }
    };

    private static final PFrame infoFrame = new PFrame("info")
    {
        public boolean shouldPaint() {
            return DungeonMain.menuIndex == 0 && DungeonMain.subMenuIndex == 0;
        }

        public boolean shouldHandleMouse() {
            return shouldPaint();
        }
    };

    private static final PFrame skillFrame = new PFrame("skills")
    {
        public boolean shouldPaint() {
            return DungeonMain.menuIndex == 0 && DungeonMain.subMenuIndex == 1;
        }

        public boolean shouldHandleMouse() {
            return shouldPaint();
        }
    };

    private static final PFrame miscFrame = new PFrame("misc")
    {
        public boolean shouldPaint() {
            return DungeonMain.menuIndex == 0 && DungeonMain.subMenuIndex == 2;
        }

        public boolean shouldHandleMouse() {
            return shouldPaint();
        }
    };
    public static boolean members;

    int random(int min, int max) {
        return Random.nextInt(min, max);
    }

    public boolean onStart() {
        if (!Game.isLoggedIn()) {
            log("Start script logged in");
            return false;
        }
        members = Utils.isWorldMembers();
        attSkill = new nz.uberutils.helpers.Skill(Skills.ATTACK);
        strSkill = new nz.uberutils.helpers.Skill(Skills.STRENGTH);
        defSkill = new nz.uberutils.helpers.Skill(Skills.DEFENSE);
        rangeSkill = new nz.uberutils.helpers.Skill(Skills.RANGE);
        magicSkill = new nz.uberutils.helpers.Skill(Skills.MAGIC);
        dungSkill = new nz.uberutils.helpers.Skill(Skills.DUNGEONEERING);
        conSkill = new nz.uberutils.helpers.Skill(Skills.CONSTITUTION);
        getContainer().submit(new updateThread());
        getContainer().submit(new FailSafeThread());
        getContainer().submit(new RoomUpdater());
        getContainer().submit(new ArteNotifier(81, true));
        Mouse.setSpeed(1);
        startTime = System.currentTimeMillis();
        int offset = 0;
        Options.add("prayDoors", Skills.getCurrentLevel(Skills.PRAYER) != 1);
        Options.add("pickupLowLevelFood", Skills.getCurrentLevel(Skills.CONSTITUTION) <= 30);
        Options.add("buryBones", false);
        int defLevel = Skills.getCurrentLevel(Skills.DEFENSE);
        Options.add("pureMode", (defLevel == 1 || defLevel == 20 || defLevel == 40));
        Options.add("usePrayers", Skills.getCurrentLevel(Skills.PRAYER) >= 37);
        Options.add("switchStyles", true);
        PaintController.reset();
        skillFrame.addComponent(new PClickableURL("http://www.uberdungeon.com", "Development blog", 10, 355));
        skillFrame.addComponent(new PClickableURL("https://arbidungeon.fogbugz.com/", "Report bugs", 10, 370));
        skillFrame.addComponent(new PClickableURL("http://www.uberdungeon.uservoice.com", "Feature request", 10, 385));
        skillFrame.addComponent(new PSkill(135, 350, dungSkill.getSkill(), PSkill.ColorScheme.GRAPHITE));
        PaintController.addComponent(new PClickableURL("http://www.rsbuddy.com/forum/showthread.php?81-UberDungeon",
                                                       "RSBuddy Thread",
                                                       15,
                                                       464));
        mainFrame.addComponent(new PFancyButton(445, 350, 67, -1, "Main", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                DungeonMain.subMenuIndex = 0;
            }
        });
        mainFrame.addComponent(new PFancyButton(445, 370, 67, -1, "Links/Skills", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                DungeonMain.subMenuIndex = 1;
            }
        });
        mainFrame.addComponent(new PFancyButton(445, 390, 67, -1, "Misc", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                DungeonMain.subMenuIndex = 2;
            }
        });
        PaintController.addComponent(new PFancyButton(445, 420, 67, -1, "Overview", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                DungeonMain.menuIndex = 0;
            }
        });
        PaintController.addComponent(new PFancyButton(445, 440, 67, -1, "Options", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                DungeonMain.menuIndex = 1;
            }
        });
        optionFrame.addComponent(new PButton(15, 355, "Bury Bones: ", new String[]{"Yes", "No"})
        {
            public void onStart() {
                startIndex = (Options.getBoolean("buryBones")) ? 0 : 1;
            }

            @Override
            public void onPress() {
                Options.flip("buryBones");
            }
        });
        optionFrame.addComponent(new PButton(15, 370, "Pure Mode: ", new String[]{"Yes", "No"})
        {
            public void onStart() {
                startIndex = (Options.getBoolean("pureMode")) ? 0 : 1;
            }

            public void onPress() {
                Options.getBoolean("pureMode");
            }
        });
        optionFrame.addComponent(new PButton(15, 385, "Switch Styles: ", new String[]{"Yes", "No"})
        {
            public void onStart() {
                startIndex = (Options.getBoolean("switchStyles")) ? 0 : 1;
            }

            public void onPress() {
                Options.flip("switchStyles");
            }
        });
        optionFrame.addComponent(new PButton(15, 400, "Use Prayers: ", new String[]{"Yes", "No"})
        {
            public void onStart() {
                startIndex = (Options.getBoolean("usePrayers")) ? 0 : 1;
            }

            public void onPress() {
                Options.flip("usePrayers");
            }
        });
        URL backgroundUrl = null;
        try {
            if (!new File(STORAGE_BASE).exists())
                new File(STORAGE_BASE).mkdir();
            backgroundUrl = new URL((new File(Environment.getStorageDirectory() +
                                              "/uberdungeon/background.png").exists()) ?
                                    new File(Environment.getStorageDirectory() + "/uberdungeon/background.png").toURI()
                                                                                                               .toString() :
                                    PAINT_BASE + "background.png");
            pBackground = ImageIO.read(backgroundUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (pBackground != null) {
            Util.saveImage(pBackground, STORAGE_BASE + "background.png", "png");
        }
        mainFrame.addComponent(infoFrame);
        mainFrame.addComponent(skillFrame);
        mainFrame.addComponent(miscFrame);
        PaintController.addComponent(mainFrame);
        PaintController.addComponent(optionFrame);
        PaintController.addComponent(new PFancyButton(461, 481, 52, 24, "Hide", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onStart() {
                forceMouse = true;
                forcePaint = true;
            }

            public void onPress() {
                text = (text.equals("Hide")) ? "Show" : "Hide";
                DungeonMain.showPaint = !showPaint;
            }
        });
        //        try {
        //            SwingUtilities.invokeAndWait(new Runnable()
        //            {
        //                public void run() {
        //                    String complexity = JOptionPane.showInputDialog(null,
        //                                                                    "Enter complexity level 1-4",
        //                                                                    "Complexity",
        //                                                                    JOptionPane.QUESTION_MESSAGE);
        //                    try {
        //                        UberPlayer.setComplexity(Integer.parseInt(complexity));
        //                    } catch (NumberFormatException ignored) {
        //                    }
        //                }
        //            });
        //        } catch (Exception ignored) {
        //        }
        MyPlayer.setComplexity(3);
        loadPlugins();
        final ArrayList<Plugin> temp = new ArrayList<Plugin>();
        temp.addAll(bosses);
        temp.addAll(puzzles);
        PluginFactory.instance().setPlugins(temp);
        return true;
    }

    void loadPlugins() {
        strategies.clear();
        bosses.clear();
        puzzles.clear();
        strategies.add(new MenuFailSafe(this));
        strategies.add(new JumpStairs(this));
        strategies.add(new Prestiege(this));
        strategies.add(new EnterDungeon(this));
        strategies.add(new LeaveDungeon(this));
        strategies.add(new DungeonStart(this));
        strategies.add(new EndDungeon(this));
        strategies.add(new ChangeRoom(this));
        if (MyPlayer.getComplexity() >= 5)
            strategies.add(new GroupGateStone(this));
        strategies.add(new ChangeAttackStyle(this));
        strategies.add(new bosses(this));
        strategies.add(new AttackEnemies(this));
        strategies.add(new PickupItems(this));
        strategies.add(new EquipItems(this));
        strategies.add(new WalkToBoss(this));
        strategies.add(new OpenDoor(this));
        strategies.add(new WalkToRoom(this));
        strategies.add(new TeleportHome(this));
        bosses.add(new AsteaFrostweb());
        bosses.add(new BloodChiller());
        bosses.add(new GluttonousBehemoth());
        bosses.add(new IcyBones());
        bosses.add(new LuminescentIcefiend());
        bosses.add(new PlaneFreezer());
        bosses.add(new BulwarkBeast());
        bosses.add(new SkeletalHorde());
        bosses.add(new HobgoblinGeomancer());
        bosses.add(new Rammernaut());
        bosses.add(new LexicusRunewright());
        bosses.add(new RiftSplitter());
        bosses.add(new Sagittare());
        bosses.add(new Stomp());
        bosses.add(new nz.artedungeon.bosses.NightGazerKhighorahk());
        bosses.add(new Default());
        puzzles.add(new Maze());
        puzzles.add(new Monolith());
        puzzles.add(new XStatueWeapon());
        puzzles.add(new ColoredFerret());
        puzzles.add(new FishingFerret());
        puzzles.add(new FlipTiles());
        puzzles.add(new FollowTheLeader());
        puzzles.add(new Ghosts());
        puzzles.add(new Levers());
        puzzles.add(new PondSkater());
        puzzles.add(new SlidingStatue());
        puzzles.add(new SuspiciousGrooves());
        puzzles.add(new ThreeStatueWeapon());
        puzzles.add(new AgilityMaze());
        puzzles.add(new Barrels());
        puzzles.add(new BloodFountain());
        puzzles.add(new CollapsingRooms());
        puzzles.add(new ColoredFerret());
        puzzles.add(new ColoredRecesses());
        puzzles.add(new CrystalLights());
        puzzles.add(new DamagedBridge());
        puzzles.add(new DamagedConstruct());
        puzzles.add(new FremennikCamp());
        puzzles.add(new GrappleChasm());
        puzzles.add(new HoardStalker());
        puzzles.add(new HunterFerret());
        puzzles.add(new IcyPads());
        puzzles.add(new LodestonePower());
        puzzles.add(new Poltergeist());
        puzzles.add(new RamokeeFamiliars());
        puzzles.add(new SeekerSentinel());
        puzzles.add(new SleepingGuards());
        puzzles.add(new StatueBridge());
        puzzles.add(new StrangeFlowers());
        puzzles.add(new UnfinishedBridge());
        puzzles.add(new WinchBridge());
        puzzles.add(new SlidingTiles());
    }

    public void onFinish() {
        log("Did: " +
            dungeonsDone +
            " Dungeons for: " +
            dungSkill.xpGained() +
            " xp and: " +
            MyPlayer.tokensGained() +
            " tokens in: " +
            Util.parseTime(System.currentTimeMillis() - startTime));
    }

    public int loop() {
        try {
            if (!Game.isLoggedIn())
                return 500;
            if (FailSafeThread.leaving() && !MyPlayer.get().isInCombat()) {
                if (MyPlayer.get().isInCombat())
                    MyPlayer.attack(MyPlayer.currentRoom().getNearestEnemy());
                else
                    return 500;
            }
            if (Walking.getEnergy() > random(60, 75) && !Walking.isRunEnabled())
                Walking.setRun(true);
            if (Combat.isAutoRetaliateEnabled())
                Combat.setAutoRetaliate(false);
            if (Explore.getBossRoom() != null) {
                Boss room = (Boss) Explore.getBossRoom();
                if (Explore.getBossRoom().contains(MyPlayer.location()) &&
                    MyPlayer.currentRoom().equals(Explore.getBossRoom()) &&
                    room.getBoss().isValid()) {
                    status = room.status();
                    return room.kill();
                }
            }
            //For testing bosses
            if (false) {
                if (MyPlayer.currentRoom() != null) {
                    for (Plugin boss : bosses) {
                        if (boss.isValid()) {
                            boss.startupMessage();
                            status = boss.getStatus();
                            return boss.loop();
                        }
                    }
                }
            }
            Camera.setPitch(true);
            if (MyPlayer.hp() < 50)
                MyPlayer.eat();
            if (MyPlayer.currentRoom() != null && MyPlayer.currentRoom().contains(MyPlayer.location())) {
                if (MyPlayer.getComplexity() > 4 &&
                    Explore.inDungeon() &&
                    MyPlayer.currentRoom().getType() == Room.PUZZLE &&
                    !((Puzzle) MyPlayer.currentRoom()).isSolved()) {
                    if (MyPlayer.currentRoom().hasEnemies()) {
                        MyPlayer.attack(MyPlayer.currentRoom().getNearestEnemy());
                    }
                    Puzzle room = ((Puzzle) MyPlayer.currentRoom());
                    status = room.status();
                    return room.solve();
                }
            }
            Npc smuggler = Npcs.getNearest(GameConstants.SMUGGLER);
            if (smuggler != null)
                if (Explore.getRooms().indexOf(MyPlayer.currentRoom()) == 1 &&
                    MyPlayer.currentRoom().contains(smuggler))
                    clearAll();
            for (Strategy strategy : strategies) {
                if (strategy.isValid()) {
                    status = strategy.getStatus();
                    if (status == null || status.length() < 1)
                        status = "Switching strategy";
                    return strategy.execute();
                }
            }
            log("No strats valid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return random(400, 600);
    }

    private final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                                   RenderingHints.VALUE_ANTIALIAS_ON);

    private final Color white = new Color(255, 255, 255);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font arial = new Font("Arial", 0, 12);


    public void onRepaint(Graphics render) {
        Graphics2D g = (Graphics2D) render;
        if (showPaint) {
            g.setRenderingHints(antialiasing);
            g.drawImage(pBackground, 0, 338, null);
            g.drawString("Version: " + String.valueOf(getClass().getAnnotation(Manifest.class).version()), 119, 464);
            g.setStroke(stroke1);
            String timeRan = Util.parseTime(System.currentTimeMillis() - startTime);
            PColumnLayout mainLayout = null;
            PColumnLayout mainLayoutColTwo = null;
            PColumnLayout miscLayout = null;
            PColumnLayout debugLayout = null;
            try {
                try {
                    String[] columns = {"Room type:", "Has enemies:"};
                    Room cur = MyPlayer.currentRoom();
                    String[] data = {"" + cur.getType(), "" + cur.hasEnemies()};
                    if (cur.getType() == Room.Type.PUZZLE) {
                        columns = Arrays.copyOf(columns, columns.length + 1);
                        data = Arrays.copyOf(data, data.length + 1);
                        columns[columns.length] = "Is solved:";
                        data[data.length] = "" + ((Puzzle) cur).isSolved();
                    }
                    debugLayout = new PColumnLayout(15, 60, columns, data);
                } catch (Exception ignored) {
                }
                mainLayout = new PColumnLayout(15,
                                               355,
                                               new String[]{"Status:",
                                                            "Time running:",
                                                            "Dungeons completed:",
                                                            "Dungeons aborted:",
                                                            "Dungeons p/h:",
                                                            "Times died:",
                                                            "Prestiege count: "},
                                               new String[]{status,
                                                            timeRan,
                                                            String.valueOf(dungeonsDone),
                                                            String.valueOf(timesAborted),
                                                            String.valueOf(nz.uberutils
                                                                                   .helpers
                                                                                   .Utils
                                                                                   .calcPH(dungeonsDone, startTime)),
                                                            String.valueOf(MyPlayer.timesDied()),
                                                            String.valueOf(prestiegeCount)});
                mainLayoutColTwo = new PColumnLayout(230,
                                                     385,
                                                     13,
                                                     new String[]{"Current Level:",
                                                                  "XP Gained:",
                                                                  "XP P/H:",
                                                                  "Time To Level:",
                                                                  "Tokens Gained:"},
                                                     new String[]{dungSkill.curLevel() +
                                                                  " (+" +
                                                                  dungSkill.levelsGained() +
                                                                  ")",
                                                                  String.valueOf(dungSkill.xpGained()),
                                                                  String.valueOf(dungSkill.xpPH()),
                                                                  dungSkill.timeToLevel(),
                                                                  String.valueOf(MyPlayer.tokensGained())});
                miscLayout = new PColumnLayout(15,
                                               355,
                                               new String[]{"Current Dungeon Time:",
                                                            "Current Deaths:",
                                                            "Fastest Dungeon:",
                                                            "Slowest Dungeon:",
                                                            "Current Floor:"},
                                               new String[]{Dungeon.curTime(),
                                                            String.valueOf(Dungeon.timesDied()),
                                                            Dungeon.fastestTime(),
                                                            Dungeon.slowestTime(),
                                                            (Dungeon.curFloor() > 0) ?
                                                            String.valueOf(Dungeon.curFloor()) :
                                                            "Unknown"});
            } catch (Exception ignored) {
            }
            if (miscLayout != null) {
                if (debugLayout != null)
                    PaintController.addComponent(debugLayout);
                infoFrame.addComponent(mainLayout);
                infoFrame.addComponent(mainLayoutColTwo);
                miscFrame.addComponent(miscLayout);
            }
            PaintController.onRepaint(render);
            if (miscLayout != null) {
                if (debugLayout != null)
                    PaintController.removeComponent(debugLayout);
                infoFrame.removeComponent(mainLayout);
                infoFrame.removeComponent(mainLayoutColTwo);
                miscFrame.removeComponent(miscLayout);
            }
            nz.uberutils.helpers.Skill[] skillIndex = {attSkill, strSkill, defSkill, rangeSkill, magicSkill, conSkill};
            int y = 375;
            for (nz.uberutils.helpers.Skill skill : skillIndex) {
                if (skill.xpGained() > 0) {
                    PSkill skillComp = new PSkill(135, y, skill.getSkill(), PSkill.ColorScheme.GRAPHITE);
                    if (!skillFrame.containsComponent(skillComp)) {
                        skillFrame.addComponent(skillComp);
                    }
                    y += 20;
                }
            }
            final Point loc = Mouse.getLocation();
            if (Mouse.isPressed()) {
                g.setColor(new Color(255, 252, 0, 150));
                g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
                g.setColor(new Color(0, 0, 0, 225));
                g.drawOval(loc.x - 5, loc.y - 5, 10, 10);
                g.setColor(new Color(255, 252, 0, 100));
            }
            else {
                g.setColor(new Color(255, 252, 0, 50));
            }

            g.drawLine(0, loc.y, 766, loc.y);
            g.drawLine(loc.x, 0, loc.x, 505);

            g.setColor(new Color(0, 0, 0, 50));
            g.drawLine(0, loc.y + 1, 766, loc.y + 1);
            g.drawLine(0, loc.y - 1, 766, loc.y - 1);
            g.drawLine(loc.x + 1, 0, loc.x + 1, 505);
            g.drawLine(loc.x - 1, 0, loc.x - 1, 505);
            if (debug) {
                try {
                    if (Game.getCurrentTab() == Game.TAB_INVENTORY) {
                        for (Iterator<Door> it = Explore.getDoors().iterator(); it.hasNext();) {
                            Door door = it.next();
                            if (door == null)
                                continue;
                            String[] text = {"Open: " + door.isOpen(),
                                             "Locked: " + door.isLocked(),
                                             "Connector: " + Explore.getRooms().indexOf(door.getConnector()),
                                             "Can Open: " + door.canOpen(),
                                             "Door type: " + door.getType()};
                            drawDoor(g, door, text, white);
                        }
                    }
                    for (Iterator<Room> it = Explore.getRooms().iterator(); it.hasNext();) {
                        Room room = it.next();
                        drawRoom(g, room, white);
                    }
                    Npc[] allNpcsInRoom = Npcs.getLoaded(new Filter<Npc>()
                    {
                        public boolean accept(Npc npc) {
                            if (MyPlayer.currentRoom() == null)
                                return false;
                            return !Util.arrayContains(GameConstants.NONARGRESSIVE_NPCS, npc.getId()) &&
                                   MyPlayer.currentRoom().contains(npc) &&
                                   npc.getHpPercent() > 0;
                        }
                    });
                    for (Npc npc : allNpcsInRoom)
                        paintEnemy(g, npc, Color.WHITE);

                } catch (ArrayIndexOutOfBoundsException aioob) {
                    aioob.printStackTrace();
                    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
                    log(elements[0]);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void drawDoor(Graphics2D g, Door door, String[] text, Color tc) {
        Color oldColor = g.getColor();
        g.setColor(tc);
        Point point;
        point = Calculations.tileToScreen(door.getLocation(), 0.5, 0.5, 1);
        if (point.x == -1 || !Calculations.isPointOnScreen(point))
            return;
        g.setColor(tc);
        int x = point.x;
        int y = point.y;
        for (String s : text) {
            final FontMetrics fm = g.getFontMetrics(arial);
            final LineMetrics lm = fm.getLineMetrics(s, g);
            g.drawString(s, x, y += lm.getHeight());
        }
        g.setColor(oldColor);
    }

    private void drawRoom(Graphics2D g, Room room, Color tc) {
        try {
            Color oldColor = g.getColor();
            g.setColor(tc);
            Polygon roomArea = new Polygon();
            for (Tile tile : room.getArea().getEdgeTiles()) {
                if (Calculations.isTileOnMap(tile)) {
                    Point tom = Calculations.tileToMap(tile);
                    roomArea.addPoint(tom.x, tom.y);
                }
            }
            for (int i = 0; i < roomArea.xpoints.length; i++) {
                g.fillRect(roomArea.xpoints[i], roomArea.ypoints[i], 4, 4);
            }
            if (Calculations.isTileOnMap(room.getArea().getCentralTile())) {
                Point tom = Calculations.tileToMap(room.getArea().getCentralTile());
                g.drawString(String.valueOf(Explore.getRooms().indexOf(room)), tom.x, tom.y);
            }
            g.setColor(oldColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void highlight(Graphics g, Tile t, Color fill) {
        Point pn = Calculations.tileToScreen(t, 0, 0, 0);
        Point px = Calculations.tileToScreen(t, 1, 0, 0);
        Point py = Calculations.tileToScreen(t, 0, 1, 0);
        Point pxy = Calculations.tileToScreen(t, 1, 1, 0);
        if (py.x != -1 && pxy.x != -1 && px.x != -1 && pn.x != -1) {
            g.setColor(fill);
            g.fillPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y, pxy.y, px.y, pn.y}, 4);
        }
    }

    private void paintEnemy(Graphics g, Npc enemy, Color color) {
        g.setColor(color);
        final EnemyDef enemyDef = new EnemyDef(enemy, MyPlayer.currentRoom());
        final Point location = Calculations.tileToScreen(enemy.getLocation(), enemy.getHeight() / 2);
        if (!Calculations.isPointOnScreen(location)) {
            return;
        }
        g.fillRect((int) location.getX() - 1, (int) location.getY() - 1, 2, 2);
        String s = "" + enemyDef.getPriority();
        g.drawString(s,
                     location.x - g.getFontMetrics().stringWidth(s) / 2,
                     location.y - g.getFontMetrics().getHeight() / 2);
    }

    public void messageReceived(MessageEvent e) {
        String txt = e.getMessage();
        if (txt.contains("Oh dear,")) {
            MyPlayer.setTimesDied(MyPlayer.timesDied() + 1);
            MyPlayer.setTeleBack(true);
            Dungeon.iTimesDied();
        }
        if (txt.contains("Floor")) {
            Dungeon.setFloor(Integer.parseInt(txt.split(">")[1].replaceAll("[a-zA-Z<>=]", "").trim()));
        }
        if (Explore.inDungeon()) {
            if (Explore.getBossRoom() != null) {
                if (Explore.getBossRoom().contains(MyPlayer.location())) {
                    ((Boss) Explore.getBossRoom()).getBoss().messageReceived(e);
                }
            }
            if (MyPlayer.currentRoom() != null && MyPlayer.currentRoom().contains(MyPlayer.location())) {
                if (Explore.inDungeon() && MyPlayer.currentRoom().getType() == Room.PUZZLE) {
                    ((Puzzle) MyPlayer.currentRoom()).getPuzzle().messageReceived(e);
                }
            }
            if (MyPlayer.lastDoorOpened() != null &&
                MyPlayer.lastDoorOpened().getType() == Door.SKILL &&
                !MyPlayer.lastDoorOpened().isOpen()) {
                Skill skillDoor = (Skill) MyPlayer.lastDoorOpened();
                skillDoor.messageReceived(e);
            }
        }
    }

    void updateSignature() {
        if (!Game.isLoggedIn() || Skills.getCurrentExp(Skills.DUNGEONEERING) == -1)
            return;
        try {
            URL url;
            URLConnection urlConn;
            url = new URL("http://uberdungeon.com/updaters/uberdungeon.php");
            urlConn = url.openConnection();
            urlConn.setRequestProperty("User-Agent", "| supersecret |");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String content = "";
            String[] stats = {"timeRunning", "dungeonsDone", "uname", "xpGained", "levelsGained"};
            String password = "123456";

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Account.getName().getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }

            Object[] data = {(System.currentTimeMillis() - startTime) - lastTimeMillis,
                             dungeonsDone - lastDungeonsDone,
                             (debug) ? "UberMouse" : sb.toString(),
                             dungSkill.getXpMinusLast(),
                             dungSkill.getLevelMinusLast()};
            for (int i = 0; i < stats.length; i++) {
                content += stats[i] + "=" + data[i] + "&";
            }
            content = content.substring(0, content.length() - 1);
            OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
            lastTimeMillis = System.currentTimeMillis() - startTime;
            lastDungeonsDone = dungeonsDone;
            wr.write(content);
            wr.flush();
            BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                if (line.toLowerCase().contains("success"))
                    log(line);
            }
            wr.close();
            rd.close();
            updateTimer.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearAll() {
        Explore.getRooms().clear();
        Explore.getDoors().clear();
        MyPlayer.setLastRoom(new Normal(new RSArea(new Tile[]{}), new LinkedList<Door>(), this));
        MyPlayer.setCurrentRoom(new Normal(new RSArea(new Tile[]{}), new LinkedList<Door>(), this));
        Explore.setBossRoom(null);
        Explore.setStartRoom(null);
        for (Strategy strategy : strategies)
            strategy.reset();
        for (Plugin puzzle : puzzles)
            puzzle.reset();
        for (Plugin boss : bosses)
            boss.reset();
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        PaintController.mouseClicked(mouseEvent);
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    private class updateThread extends LoopTask
    {
        @Override
        public int loop() {
            if (updateTimer.isRunning())
                return (int) updateTimer.getRemaining();
            else {
                updateSignature();
                updateTimer.reset();
                return 500;
            }
        }

    }

    public void mouseDragged(MouseEvent e) {

    }


    public void mouseMoved(MouseEvent e) {
        PaintController.mouseMoved(e);
        m = e.getPoint();
    }

    ArrayList<Object> loadPluginsInDirectory(String directory) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NullPointerException, IOException {
        ArrayList<Object> plugins = new ArrayList<Object>();
        File dir = new File(directory);
        if (!dir.exists())
            return plugins;
        for (File file : dir.listFiles()) {
            String name = file.getName();
            if (name.contains(".class")) {
                try {
                    Class<?> clz = Class.forName(name.replace(".class", ""));
                    plugins.add(clz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return plugins;
    }

    public LinkedList<PuzzlePlugin> getPuzzles() {
        return puzzles;
    }

    public LinkedList<Plugin> getBosses() {
        return bosses;
    }
}