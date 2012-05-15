package nz.artepestcontrol;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Skills;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.wrappers.Npc;
import nz.artepestcontrol.game.PestControl;
import nz.artepestcontrol.game.Portal;
import nz.artepestcontrol.gui.JobsGUI;
import nz.artepestcontrol.misc.GameConstants;
import nz.artepestcontrol.misc.Shared;
import nz.artepestcontrol.misc.Utils;
import nz.artepestcontrol.stratagies.*;
import nz.artepestcontrol.stratagies.tasks.OpenGate;
import nz.uberutils.helpers.*;
import nz.uberutils.paint.PaintController;
import nz.uberutils.paint.components.PCheckBox;
import nz.uberutils.paint.components.PComboBox;
import nz.uberutils.paint.components.PFancyButton;
import nz.uberutils.paint.paints.UberPaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/9/11
 * Time: 2:44 PM
 * Package: PACKAGE_NAME;
 */
@Manifest(authors = "UberMouse",
        name = "ArtePestControl",
        keywords = "Combat, Minigames",
        version = 0.81,
        description = "Plays pest control for that full void set!")
public class ArtePestControl extends UberScript implements PaintListener,
                                                           MouseListener,
                                                           MouseMotionListener,
                                                           KeyListener,
                                                           MessageListener
{

    private static boolean locked;


    private static final Filter<Npc> knightFilter = new Filter<Npc>()
    {
        public boolean accept(Npc npc) {
            Pattern p = Pattern.compile("(shifter|spinner|defiler|torcher)");
            Matcher m = p.matcher(npc.getName().toLowerCase());
            return m.matches() &&
                   PestControl.getKnightArea() != null &&
                   PestControl.getKnightArea().contains(npc.getLocation());
        }
    };

    private static final Filter<Npc> portalFilter = new Filter<Npc>()
    {
        public boolean accept(Npc npc) {
            Pattern p = Pattern.compile("(shifter|spinner|defiler|torcher|brawler)");
            Matcher m = p.matcher(npc.getName().toLowerCase());
            final Portal portal = Utils.getPortal();
            if (portal != null)
                if (Calculations.distanceBetween(portal.getLocation(), npc.getLocation()) > 15)
                    return false;
            return m.matches();
        }
    };

    public static JobsGUI gui;

    public void messageReceived(MessageEvent messageEvent) {
        if (Utils.inGame()) {
            for (Portal p : PestControl.portals)
                p.messageReceived(messageEvent);
        }
    }

    class createGUI implements Runnable
    {

        public void run() {
            gui = new JobsGUI();
        }
    }

    public boolean onBegin() {
        threadId = 29337;
        UberPaint p = new UberPaint("ArtePestControl", threadId, getClass().getAnnotation(Manifest.class).version());
        SwingUtilities.invokeLater(new createGUI());
        strategies.add(new Exchange());
        strategies.add(new LeaveLander());
        strategies.add(new EnterLander());
        strategies.add(new WalkToKnight());
        strategies.add(new WalkToPortal());
        strategies.add(new UseSpecial());
        strategies.add(new AttackPortals());
        strategies.add(new AttackEnemies());
        p.skills.add(new Skill(Skills.ATTACK));
        p.skills.add(new Skill(Skills.DEFENSE));
        p.skills.add(new Skill(Skills.STRENGTH));
        p.skills.add(new Skill(Skills.CONSTITUTION));
        p.skills.add(new Skill(Skills.RANGE));
        p.skills.add(new Skill(Skills.PRAYER));
        p.skills.add(new Skill(Skills.MAGIC));
        p.infoColumnValues = new String[]{"Times won (P/H):",
                                          "Times lost (P/H):",
                                          "Times failed (P/H):",
                                          "Commendations earned (P/H):",
                                          "Commendations:",
                                          "Run time:",
                                          "Status:"};
        Options.add("firstRun", false);
        Options.add("landerGangplank", GameConstants.LANDER_GANGPLANKS[0]);
        Options.add("landerLadder", GameConstants.LANDER_LADDERS[0]);
        Options.add("landerIndex", 0);
        Options.add("landerText", "Novice");
        Options.add("attackPortals", false);
        SpecialAttack.setUpWeapons();
        PestControl.commendationMultiplier = GameConstants.COMMENDATION_MULTIPLIERS[Options.getInt("landerIndex")];
        p.getFrame("options").addComponent(new PCheckBox(10, 360, "Attack portals: ")
        {
            public void onStart() {
                state = Options.getBoolean("attackPortals");
            }

            public void onPress() {
                Options.flip("attackPortals");
                AttackEnemies.setFilter((Options.getBoolean("attackPortals")) ? portalFilter : knightFilter);
            }
        });
        p.getFrame("options")
         .addComponent(new PComboBox(120,
                 350,
                 new String[]{"Novice Lander", "Intermediate lander", "Veteren Lander"},
                 PComboBox.ColorScheme.GRAPHITE)
         {
             public void onStart() {
                 currentIndex = Options.getInt("landerIndex");
                 setIndex = true;
             }

             public void onChange() {
                 Options.put("landerGangplank", GameConstants.LANDER_GANGPLANKS[currentIndex]);
                 Options.put("landerLadder", GameConstants.LANDER_LADDERS[currentIndex]);
                 Shared.landerTile = GameConstants.LANDER_TILES[currentIndex];
                 PestControl.commendationMultiplier = GameConstants.COMMENDATION_MULTIPLIERS[currentIndex];
                 Options.put("landerIndex", currentIndex);
                 Options.put("landerText", GameConstants.LANDER_TEXT[currentIndex]);
             }
         });
        p.getFrame("options").addComponent(new PFancyButton(10, 365, "Show GUI")
        {
            public void onPress() {
                gui.setVisible(true);
            }
        });
        getContainer().submit(new PestControl());
        getContainer().submit(new OpenGate());
        AttackEnemies.setFilter(Options.getBoolean("attackPortals") ? portalFilter : knightFilter);
        Options.put("firstRun", false);
        Logger.instance().setLoggingMode(Logger.LOGGING_MODE_DEBUG);
        changeLog = new String[]{"0.82 changelog",
                                 " (If not current version number I forgot to update this)",
                                 "Fixed issue entering lander",
                                 "Decreased size of knight defend area so it sticks with knight better",
                                 "Paint should be working again"};
        paintType = p;
        return true;
    }

    public void onEnd() {
        Options.save();
    }

    public static boolean isLocked() {
        return locked;
    }

    public static void lock() {
        locked = true;
    }

    public static void unLock() {
        locked = false;
    }

    public void paint(Graphics2D g) {
        ((UberPaint) paintType).infoColumnData = new String[]{String.valueOf(PestControl.timesWon()) +
                                                              " (" +
                                                              Utils.calcPH(PestControl.timesWon(),
                                                                      PaintController.runTime()) +
                                                              ")",
                                                              String.valueOf(PestControl.timesLost()) +
                                                              " (" +
                                                              Utils.calcPH(PestControl.timesLost(),
                                                                      PaintController.runTime()) +
                                                              ")",
                                                              String.valueOf(PestControl.timesFailed()) +
                                                              " (" +
                                                              Utils.calcPH(PestControl.timesFailed(),
                                                                      PaintController.runTime()) +
                                                              ")",
                                                              String.valueOf(PestControl.commendationsEarned) +
                                                              " (" +
                                                              Utils.calcPH(PestControl.commendationsEarned,
                                                                      PaintController.runTime()) +
                                                              ")",
                                                              String.valueOf(PestControl.commendations),
                                                              PaintController.timeRunning(),
                                                              status};
    }
}
