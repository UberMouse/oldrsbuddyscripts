package nz.uberutils.helpers;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Mouse;
import com.rsbuddy.script.methods.Settings;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.util.Timer;
import com.rsbuddy.script.wrappers.Item;
import nz.uberutils.methods.UberEquipment;
import nz.uberutils.methods.UberInventory;
import org.rsbuddy.tabs.Attack;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/19/11
 * Time: 8:17 PM
 * Package: nz.uberutils.helpers;
 */
public class SpecialAttack
{
    private static String  primaryWeapon;
    private static String  specialWeapon;
    private static String  offHand = null;
    private static int     specEnergy;
    private static boolean useSecondaryWeapon;
    private static final int SETTING_SPECIAL_ENERGY = 300;

    private static final int[]      amountUsage = {10, 25, 33, 35, 45, 50, 55, 60, 80, 100};
    private static final String[][] weapons     = {{"Rune thrownaxe", "Rod of ivandis"},
                                                   {"Dragon Dagger",
                                                    "Dragon dagger (p)",
                                                    "Dragon dagger (p+)",
                                                    "Dragon dagger (p++)",
                                                    "Dragon Mace",
                                                    "Dragon Spear",
                                                    "Dragon longsword",
                                                    "Rune claws"},
                                                   {"Dragon Halberd"},
                                                   {"Magic Longbow"},
                                                   {"Magic Composite Bow"},
                                                   {"Dragon Claws",
                                                    "Granite Maul",
                                                    "Darklight",
                                                    "Barrelchest Anchor",
                                                    "Armadyl Godsword"},
                                                   {"Magic Shortbow"},
                                                   {"Dragon Scimitar",
                                                    "Dragon 2H Sword",
                                                    "Zamorak Godsword",
                                                    "Korasi's sword"},
                                                   {"Dorgeshuun Crossbow",
                                                    "Bone Dagger",
                                                    "Bone Dagger (p+)",
                                                    "Bone Dagger (p++)"},
                                                   {"Bandos Godsword",
                                                    "Dragon Battleaxe",
                                                    "Dragon Hatchet",
                                                    "Seercull Bow",
                                                    "Excalibur",
                                                    "Enhanced excalibur",
                                                    "Ancient Mace",
                                                    "Saradomin Godsword",
                                                    "Saradomin sword"}};


    /**
     * Set primary weapon name
     *
     * @param name name of primary weapon
     */
    public static void setPrimaryWeapon(String name) {
        primaryWeapon = name;
    }

    /**
     * Set special weapon name
     *
     * @param name name of special weapon
     */
    public static void setSpecialWeapon(String name) {
        specialWeapon = name;
    }

    public static void setOffHand(String name) {
        offHand = name;
    }

    /**
     * Set percentage of special energy special attack uses
     *
     * @param percent percent used
     */
    public static void setSpecEnergy(int percent) {
        specEnergy = percent;
    }

    /**
     * Set whether to use secondary weapon for special or not
     *
     * @param use whether to use secondary weapon or not
     */
    public static void setUseSecondaryWeapon(boolean use) {
        useSecondaryWeapon = use;
    }

    /**
     * Should use special attack
     *
     * @return true if special attack should be used
     */
    public static boolean shouldSpec() {
        return canSpec() ||
               (useSecondaryWeapon && (UberInventory.contains(primaryWeapon) || UberInventory.contains(offHand)));
    }

    /**
     * Can use special attack
     *
     * @return true if special attack can be used
     */
    public static boolean canSpec() {
        return getSpecialEnergy() >= specEnergy;
    }

    public static void setSpecValues(String weapon) {
        for (int i = 0; i < weapons.length; i++) {
            for (int j = 0; j < weapons[i].length; j++) {
                if (weapons[i][j].equalsIgnoreCase(weapon)) {
                    specEnergy = amountUsage[i];
                }
            }
        }
    }

    public static void doSpecial() {
        if (!UberInventory.contains(specialWeapon) && getSpecialEnergy() >= specEnergy && !Attack.isSpecialEnabled()) {
            Timer fail = new Timer(4000);
            while (getSpecialEnergy() >= specEnergy && fail.isRunning()) {
                Game.openTab(Game.TAB_ATTACK);
                if (!Attack.isSpecialEnabled()) {
                    Widgets.getComponent(884, 4).click();
                    for (int i = 0; i < 15 && !Attack.isSpecialEnabled(); i++)
                        Utils.sleep(100);
                }
                Utils.sleep(100);
                if (!UberPlayer.inCombat())
                    break;
            }
        }
        else if (useSecondaryWeapon && UberInventory.contains(primaryWeapon)) {
            Game.openTab(Game.TAB_INVENTORY);
            Mouse.move(UberInventory.getItem(primaryWeapon).getComponent().getCenter());
            Mouse.click(true);
            for (int i = 0; i <= 15 && UberInventory.contains(primaryWeapon); i++)
                Task.sleep(100);
        }
        else if (useSecondaryWeapon && UberInventory.contains(offHand)) {
            Game.openTab(Game.TAB_INVENTORY);
            Mouse.move(UberInventory.getItem(offHand).getComponent().getCenter());
            Mouse.click(true);
            for (int i = 0; i <= 15 && UberInventory.contains(offHand); i++)
                Task.sleep(100);
        }
        else if (useSecondaryWeapon && UberInventory.contains(specialWeapon)) {
            Game.openTab(Game.TAB_INVENTORY);
            Mouse.move(UberInventory.getItem(specialWeapon).getComponent().getCenter());
            Mouse.click(true);
            for (int i = 0; i <= 15 && UberInventory.contains(specialWeapon); i++)
                Task.sleep(100);
        }
    }

    /**
     * Get current special energy
     *
     * @return special energy (0-100)
     */
    public static int getSpecialEnergy() {
        return Settings.get(SETTING_SPECIAL_ENERGY) / 10;
    }

    /**
     * Call to have the SpecialAttack class automatically setup up the special attack values and weapons
     */
    public static void setUpWeapons() {
        String prim;
        SpecialAttack.setSpecEnergy(101);
        Game.openTab(Game.TAB_EQUIPMENT);
        if (UberEquipment.getItem(UberEquipment.WEAPON).getId() > -1) {
            prim = UberEquipment.getItem(UberEquipment.WEAPON).getName();
            primaryWeapon = prim;
            String name = prim;
            if (name.contains(">"))
                name = name.split(">")[1];
            for (int i = 0; i < weapons.length; i++) {
                for (int j = 0; j < weapons[i].length; j++) {
                    if (weapons[i][j].equalsIgnoreCase(name)) {
                        specEnergy = amountUsage[i];
                    }
                }
            }
        }
        Game.openTab(Game.TAB_INVENTORY);
        for (Item item : UberInventory.getItems()) {
            String name = item.getName();
            if (name.contains(">"))
                name = name.split(">")[1];
            for (int i = 0; i < weapons.length; i++) {
                for (int j = 0; j < weapons[i].length; j++) {
                    if (weapons[i][j].equalsIgnoreCase(name)) {
                        specialWeapon = item.getName();
                        specEnergy = amountUsage[i];
                        useSecondaryWeapon = true;
                        offHand = UberEquipment.getItem(UberEquipment.SHIELD).getName();
                        if(offHand.equals(""))
                            offHand = null;
                    }
                }
            }
        }
    }

    public static String getPrimaryWeapon() {
        return primaryWeapon;
    }

    public static String getSpecialWeapon() {
        return specialWeapon;
    }

    public static String getOffHand() {
        return offHand;
    }
}