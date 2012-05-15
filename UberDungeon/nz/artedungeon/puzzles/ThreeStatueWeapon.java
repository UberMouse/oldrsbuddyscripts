package nz.artedungeon.puzzles;

import com.rsbuddy.script.methods.GroundItems;
import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.*;
import nz.artedungeon.common.PuzzlePlugin;
import nz.artedungeon.dungeon.Enemy;
import nz.artedungeon.dungeon.MyPlayer;
import nz.artedungeon.utils.Util;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberEquipment;
import nz.uberutils.methods.UberInventory;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/19/11
 * Time: 11:26 AM
 * Package: nz.artedungeon.puzzles;
 */
public class ThreeStatueWeapon extends PuzzlePlugin
{
    int CRUMBLING_WALL[] = {49647}, PICKAXE[] = {16295,
                                                 16297,
                                                 16299}, CHISEL = 17444, HAMMER = 17883, STONE_BLOCK = 17415, STATUE_BOW = 11030, STATUE_STAFF = 11033, STATUE_SWORD = 11027, STATUE_NONE[] = {
            11012,
            11013}, STONE_SWORD = 17416, STONE_BOW = 17418, STONE_STAFF = 17420;
    Filter<Npc> enemyFilter = new Filter<Npc>()
    {
        public boolean accept(Npc npc) {
            return !npc.getName().equals("Statue") && Util.tileInRoom(npc.getLocation());
        }
    };

    public boolean isValid() {
        if (Npcs.getNearest(STATUE_NONE) != null &&
            Util.tileInRoom(Npcs.getNearest(STATUE_NONE).getLocation()) &&
            Npcs.getLoaded(new Filter<Npc>()
            {
                public boolean accept(Npc npc) {
                    return Utils.arrayContains(STATUE_NONE, npc.getId());
                }
            }).length == 1) {
            if (inventoryContains("Chisel") || nearestGroundItem("Chisel") != null) {
                return (inventoryContains("Pickaxe") || equipmentContains("Pickaxe")) && inventoryContains("Hammer");
            }
        }
        return false;
    }

    public String getStatus() {
        return "Solving: Three Statue Weapon";
    }

    public String getAuthor() {
        return "Zippy";
    }

    public String getName() {
        return "Three Statue Weapon";
    }

    @Override
    public int loop() {
        if (MyPlayer.get().getHpPercent() < 50) {
            // EAT FOOD
        }
        if (!MyPlayer.get().isInCombat()) {
            if (Npcs.getNearest(enemyFilter) != null) {
                Npc enemy = Npcs.getNearest(enemyFilter);
                UberMovement.turnTo(enemy);
                Enemy.setEnemy(enemy);
                Enemy.interact("Attack");
                return Random.nextInt(500, 1000);
            }
            if (MyPlayer.get().isIdle()) {
                if (!inventoryContains("Chisel")) {
                    GroundItem chisel = nearestGroundItem("Chisel");
                    if (chisel != null) {
                        if (chisel.isOnScreen()) {
                            chisel.interact("Take");
                        }
                        else {
                            UberMovement.turnTo(chisel.getLocation());
                        }
                    }
                }
                Npc next = Npcs.getNearest(STATUE_NONE);
                if (next != null) {
                    String nextWep = getNecessaryWeapon(next);
                    if (clickInterface(nextWep + "."))
                        return Random.nextInt(500, 1000);
                    if (!inventoryContains("stone " + nextWep)) {
                        if (!inventoryContains("Stone block")) {
                            GameObject wall = Objects.getNearest("crumbling wall");
                            if (wall != null) {
                                if (wall.isOnScreen()) {
                                    wall.interact("Mine");
                                }
                                else {
                                    UberMovement.turnTo(wall);
                                }
                            }
                        }
                        else {
                            getInventoryItem("Stone block").interact("Carve");
                        }
                    }
                    else {
                        UberMovement.turnTo(next);
                        next.interact("Arm");
                    }
                }
                return Random.nextInt(500, 1000);
            }
        }
        return 0;
    }

    public boolean clickInterface(String txt) {
        for (Widget w : Widgets.getLoaded()) {
            if (w.containsText(txt)) {
                for (Component c : w.getComponents()) {
                    if (c.getText().equalsIgnoreCase(txt)) {
                        c.click();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getNecessaryWeapon(final Npc n) {
        boolean sword = Npcs.getNearest(STATUE_SWORD) != null;
        boolean bow = Npcs.getNearest(STATUE_BOW) != null;
        boolean staff = Npcs.getNearest(STATUE_STAFF) != null;

        if (!sword)
            return "Sword";
        if (!staff)
            return "Staff";
        if (!bow)
            return "Bow";
        return "null";
    }

    public Item getInventoryItem(String name) {
        for (Item i : UberInventory.getItems()) {
            if (i.getName().toLowerCase().contains(name.toLowerCase()))
                return i;
        }
        return null;
    }

    public boolean inventoryContains(String name) {
        for (Item i : UberInventory.getItems()) {
            if (i.getName().toLowerCase().contains(name.toLowerCase()))
                return true;
        }
        return false;
    }

    public boolean equipmentContains(String name) {
        for (Item i : UberEquipment.getItems()) {
            if (i.getName().toLowerCase().contains(name.toLowerCase()))
                return true;
        }
        return false;
    }

    public GroundItem nearestGroundItem(String name) {
        for (GroundItem i : GroundItems.getLoaded()) {
            if (i != null)
                if (i.getItem().getName().toLowerCase().contains(name.toLowerCase()))
                    return i;
        }
        return null;
    }
}
