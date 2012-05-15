package nz.ubermouse.artehunter;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.Environment;
import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Skills;
import com.rsbuddy.script.wrappers.Tile;
import nz.ubermouse.artehunter.traps.Trap;
import nz.ubermouse.artehunter.traps.TrapManager;
import nz.ubermouse.artehunter.traps.impl.*;
import nz.uberutils.helpers.IPaint;
import nz.uberutils.helpers.Logger;
import nz.uberutils.helpers.PaintUtils;
import nz.uberutils.helpers.Skill;
import nz.uberutils.paint.paints.ArtePaint;
import org.rsbuddy.net.GeItem;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Manifest(name = "ArteHunt",
          authors = "UberMouse",
          keywords = {"hunter"},
          description = "ArteBots brings you the ultimate Hunter experience.",
          version = 1.0)
public class Hunter extends ActiveScript implements PaintListener, MessageListener, MouseListener, KeyListener, MouseMotionListener {

    public static Manifest manifest = Hunter.class.getAnnotation(Manifest.class);

    private int hunterLevel = 0;

    /**
     * Random paint shit.
     */
    public static BufferedImage logo   = null;
    public static BufferedImage hide   = null;
    public static BufferedImage show   = null;
    public static boolean       hiding = false;
    private nz.ubermouse.artehunter.ui.Settings gui;
    String sep = System.getProperty("file.separator");
    protected final Logger logger = new Logger(Environment.getStorageDirectory() +
                                                       sep +
                                                       "ArteBots" +
                                                       sep +
                                                       "ArteHunter" +
                                                       sep +
                                                       "logs" +
                                                       sep +
                                                       "ArteHunter", "ArteHunter");
    private         IPaint paint  = null;

    //Sets the hunter level.
    public static void setHunting(int level) {
        scriptMode = level;
    }

    //Set whether or not to bury a bone,
    public static void setBuryBones(boolean bury) {
        buryBones = bury;
    }

    private BufferedImage loadImage(String url) {
        try {
            final URL link = new URL(url);
            return ImageIO.read(link);
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            return null;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public boolean onStart() {
        if (!Game.isLoggedIn()) {
            log.warning("You must start the script logged in.");
            return false;
        }
        ArtePaint p = new ArtePaint("ArteHunter", "/bots/hunt_bg.png", "/bots/artehunt.png", null);
        p.skills.add(new Skill(Skills.HUNTER));
        p.columnData[0][0] = new String[]{"Status:",
                                          "Profit:",
                                          "Profit/hr:",
                                          "Catches/hr:",
                                          "Catches:",
        };
        p.columnData[1][0] = new String[]{"Trap Info:",
                                          "Lost:"};
        p.numTabs = 2;
        paint = p;
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());
        logo = loadImage("http://imgur.com/pkFvw.png");
        hide = loadImage("http://imgur.com/XOhMk.png");
        show = loadImage("http://i.imgur.com/Zg9VC.png");
        hunterLevel = Skills.getCurrentLevel(Skills.HUNTER);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    gui = new nz.ubermouse.artehunter.ui.Settings(hunterLevel);
                    gui.setVisible(true);
                    log("Waiting on GUI.");
                }
            });
        } catch (Exception ie) {
            ie.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Prints the thank you message, then takes a screenshot before ending the script.
     */
    public void onFinish() {
        Environment.takeScreenshot(false);
        log(Color.YELLOW, "Thank you for using " + manifest.name() + " v" + manifest.version());
    }

    //Used for detecting if this is the first run (used for grabbing latest prices).
    private boolean firstRun = true;


    public int loop() {
        if (scriptMode == 0) {
            if (gui.isVisible()) {
                return 0;
            }
            else {
                log.warning("No script mode selected.");
                return -1;
            }
        }
        if (firstRun) {
            timeRun = new com.rsbuddy.script.util.Timer(0);
            switch (scriptMode) {
                case RED_CHINS:
                    huntMode = new RedChinchompas();
                    break;
                case GRAY_CHINS:
                    huntMode = new GrayChinchompas();
                    break;
                case CRIMSON_SWIFT:
                    CrimsonSwifts crimsonSwifts = new CrimsonSwifts();
                    crimsonSwifts.bird = true;
                    huntMode = crimsonSwifts;
                    break;
                case COPPER_LONGTAIL:
                    CopperLongtails cl = new CopperLongtails();
                    cl.bird = true;
                    huntMode = cl;
                    break;
                case CERULEAN_TWITCH:
                    CeruleanTwitches ct = new CeruleanTwitches();
                    ct.bird = true;
                    huntMode = ct;
                    break;
                case TROPICAL_WAGTAIL:
                    TropicalWagtails tw = new TropicalWagtails();
                    tw.bird = true;
                    huntMode = tw;
                    break;
            }
            huntMode.costPerUnit = GeItem.lookup(
                    huntMode.InventoryUnitID()).getGuidePrice();
            firstRun = false;
        }
        return huntMode.ai();
    }

    static               int  scriptMode       = 0;
    private static final int  RED_CHINS        = 6;
    private static final int  GRAY_CHINS       = 5;
    private static final int  CRIMSON_SWIFT    = 1;
    private static final int  COPPER_LONGTAIL  = 2;
    private static final int  CERULEAN_TWITCH  = 3;
    private static final int  TROPICAL_WAGTAIL = 4;
    public static        Trap huntMode         = null;

    public static com.rsbuddy.script.util.Timer timeRun   = null;
    public static boolean                       buryBones = false;


    public void keyTyped(KeyEvent keyEvent) {
        if (paint == null)
            return;
        paint.keyTyped(keyEvent);
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (paint == null)
            return;
        paint.keyPressed(keyEvent);
    }

    public void keyReleased(KeyEvent keyEvent) {
        if (paint == null)
            return;
        paint.keyReleased(keyEvent);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (paint == null)
            return;
        paint.mouseClicked(mouseEvent);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (paint == null)
            return;
        paint.mousePressed(mouseEvent);
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (paint == null)
            return;
        paint.mouseReleased(mouseEvent);
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        if (paint == null)
            return;
        paint.mouseEntered(mouseEvent);
    }

    public void mouseExited(MouseEvent mouseEvent) {
        if (paint == null)
            return;
        paint.mouseExited(mouseEvent);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if (paint == null)
            return;
        paint.mouseDragged(mouseEvent);
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        if (paint == null)
            return;
        paint.mouseMoved(mouseEvent);
    }

    private static final Color TRAP_HIGHTLIGHT = new Color(27, 245, 52, 100);

    public void onRepaint(Graphics graphics) {
        if (paint == null || (gui != null && gui.isVisible()))
            return;
        if (TrapManager.numberOfLaidTraps() > 0) {
            for (Tile t : TrapManager.getTiles())
                PaintUtils.drawTile(graphics, t, TRAP_HIGHTLIGHT, Color.BLACK);
        }
        ArtePaint ap = (ArtePaint) paint;
        ap.columnData[0][1] = new String[]{huntMode.status, huntMode.getTotalProfit() + "k",
                                           huntMode.getProfitPerHour() + "k/hr",
                                           huntMode.getUnitsPerHour(),
                                           "" + huntMode.unitsHunted,

        };
        ap.columnData[1][1] = new String[]{huntMode.getTrapInfo(),
                                           huntMode.getLostCount(),};
        paint = ap;
        paint.paint(graphics);
    }

    public void messageReceived(MessageEvent e) {
        huntMode.messageReceived(e);
    }

}
