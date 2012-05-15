package nz.artezombies;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Keyboard;
import com.rsbuddy.script.methods.Mouse;
import com.rsbuddy.script.methods.Skills;
import com.rsbuddy.script.task.Task;
import nz.artezombies.misc.GUI;
import nz.artezombies.stratagies.*;
import nz.uberutils.helpers.*;
import nz.uberutils.methods.UberEquipment;
import nz.uberutils.methods.UberInventory;
import nz.uberutils.paint.components.PFancyButton;
import nz.uberutils.paint.paints.UberPaint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;

//import nz.uberutils.arte.ArteNotifier;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/27/11
 * Time: 3:16 PM
 * Package: PACKAGE_NAME;
 */
@Manifest(authors = "UberMouse",
        name = "ArteZombies",
        keywords = "Combat",
        version = 0.992,
        description = "Kills Armoured Zombies")
public class ArteZombies extends UberScript implements PaintListener,
                                                       MessageListener,
                                                       MouseMotionListener,
                                                       MouseListener
{

    public static boolean stopScript;

    //===== Options ===== \\
    public static int lootPrice = 10000;

    public static GUI gui;
    public static Logger log = null;

    class createGUI implements Runnable
    {
        public void run() {
            gui = new GUI();
        }
    }

    public boolean onBegin() {
        //        ArtePaint p = new ArtePaint("ArteZombies", "abyssPaintBG.png", "abyssPaintLogo.png", gui);
        UberPaint p = new UberPaint("ArteZombies", 14310, getClass().getAnnotation(Manifest.class).version());
        p.skills.add(new Skill(Skills.CONSTITUTION));
        p.skills.add(new Skill(Skills.DEFENSE));
        p.skills.add(new Skill(Skills.STRENGTH));
        p.skills.add(new Skill(Skills.ATTACK));
        p.skills.add(new Skill(Skills.PRAYER));
        p.skills.add(new Skill(Skills.RANGE));
        Loot.addLoot(995, "Coins", 1);
        Loot.addLoot(18778, "Starved Ancient Effigy", 50000);
        Loot.setFilter(Looting.filterDrops);
        Loot.setPaintFilter(Looting.paintFilter);
        Options.setNode("ArteZombies");
        Options.add("pickupLoot", true);
        Options.add("useSoulSplit", false);
        Options.add("useSummoning", false);
        Options.add("useProtectionPrayers", true);
        Options.add("switchPrayers", true);
        Options.add("onlyPriorityLoot", false);
        Options.add("slowMouse", false);
        Options.add("excalibur", false);
        Options.add("usePots", false);
        Options.add("useFood", false);
        Options.add("bank", false);
        Options.add("ssAlwaysOn", false);
        setupStrats();
        if (UberPlayer.hasPotions())
            Options.put("usePots", true);
        if (UberPlayer.edibleItem() != null)
            Options.put("useFood", true);
        if (UberInventory.contains("Varrock teleport"))
            Options.put("bank", true);
        SpecialAttack.setUpWeapons();
        if (UberInventory.contains("enhanced excalibur") || Options.getBoolean("excalibur")) {
            Game.openTab(Game.TAB_EQUIPMENT);
            Game.openTab(Game.TAB_INVENTORY);
            Options.put("excalibur", true);
            SpecialAttack.setSpecialWeapon("enhanced excalibur");
            SpecialAttack.setUseSecondaryWeapon(true);
            SpecialAttack.setPrimaryWeapon(UberEquipment.getItem(UberEquipment.WEAPON).getName());
            if (UberEquipment.getItem(UberEquipment.SHIELD).getId() != -1)
                SpecialAttack.setOffHand(UberEquipment.getItem(UberEquipment.SHIELD).getName());
        }
        else
            Options.put("excalibur", false);
        try {
            SwingUtilities.invokeAndWait(new createGUI());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        gui.showGUI();
        while (!GUI.finished)
            Task.sleep(100);
        //        p.columnData[0][0] = new String[]{"Status:", "Looted value:"};
        p.infoColumnValues = new String[]{"Status:", "Looted value:"};
        log = Logger.instance();
        changeLog = new String[]{"0.991 changelog",
                                 " (If not current version number I forgot to update this)",
                                 "Added path to bank from GE",
                                 "Fixed issues caused by x10 prayer update",};
        paintType = p;
        setupPaint();
        return true;
    }

    public void miscLoop() {
        Options.put("onlyPriorityLoot", false);
        if (!Game.isLoggedIn())
            return;
        //        ((ArtePaint) paintType).columnData[0][1] = new String[]{status, String.valueOf(Loot.totalPrice)};
        ((UberPaint) paintType).infoColumnData = new String[]{status, String.valueOf(Loot.totalPrice)};
        Mouse.setSpeed(Options.getBoolean("slowMouse") ? Utils.random(6, 7) : Utils.random(1, 2));
        if (stopScript) {
            log.warn("Out of food and low HP, stopping script");
            stop();
        }
    }

    public void paint(Graphics2D g) {
        if (Loot.isPaintValid() && Options.getBoolean("pickupLoot"))
            Loot.repaint(g);
    }

    public void messageReceived(MessageEvent messageEvent) {
        String txt = messageEvent.getMessage();
        if (messageEvent.isAutomated()) {
            if (txt.contains(".*ring.*life.*breaks.*"))
                stop();
        }
        else if (messageEvent.getID() == MessageEvent.MESSAGE_CHAT) {
            if (txt.contains("roogleberry"))
                Keyboard.sendTextInstant((Utils.random(0, 2) == 1) ? "Wat" : "Wut", true);
        }
    }

    public void onEnd() {
        GUI.save();
    }

    public void setupPaint() {
        UberPaint p = (UberPaint) paintType;
        p.addOption("Use soulsplit:", "useSoulSplit");
        p.addOption("Pickup loot:", "pickupLoot");
        p.addOption("Use protection prayers:", "useProtectionPrayers");
        p.addOption("Switch protection prayers:", "switchPrayers");
        p.addOption("Use Summoning:", "useSummoning");
        p.addOption("Only pickup priority loot:", "onlyPriorityLoot");
        p.addOption("Slow mouse:", "slowMouse");
        p.addOption("Use potions:", "usePots");
        p.addOption("Use food:", "useFood");
        p.addOption("Bank:", "bank");
        p.addOption("Soul Split always on:", "ssAlwaysOn");
        p.addOption("Use enhanced excalibur (Restart):", "excalibur");

        p.getFrame("options")
         .addComponent(new PFancyButton(440, 385, 73, -1, "Show GUI", PFancyButton.ColorScheme.GRAPHITE)
         {
             public void onPress() {
                 gui.showGUI();
             }
         });
        paintType = p;
    }

    public void setupStrats() {
        strategies.add(new Banking());
        strategies.add(new WalkToBank());
        strategies.add(new StopScript());
        strategies.add(new Eat());
        strategies.add(new ReturnToArea());
        strategies.add(new UseLadders());
        strategies.add(new SwitchPrayers());
        strategies.add(new DropJunk());
        strategies.add(new Looting());
        strategies.add(new RefillPrayer());
        strategies.add(new QuickPrayers());
        strategies.add(new DrinkPots());
        strategies.add(new Familar());
        strategies.add(new UseSpecial());
        strategies.add(new AttackZombies());
        strategies.add(new WalkToHartWin());
    }
}