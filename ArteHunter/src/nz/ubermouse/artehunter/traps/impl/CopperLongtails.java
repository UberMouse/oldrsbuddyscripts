package nz.ubermouse.artehunter.traps.impl;

import com.rsbuddy.script.methods.Mouse;
import com.rsbuddy.script.methods.Skills;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Item;
import nz.ubermouse.artehunter.Hunter;
import nz.ubermouse.artehunter.traps.Trap;

public class CopperLongtails extends Trap {

    @Override
    public int InventoryTrapID() {
        return 10006;
    }

    @Override
    public int InventoryUnitID() {
        return 10091;
    }

    @Override
    public int animation_layTrap() {
        return 5208;
    }

    @Override
    public double expPerUnit() {
        return 61.2;
    }

    @Override
    public int getTrapAbilityCount() {
        int level = Skills.getCurrentLevel(Skills.HUNTER);
        if (level >= 9 && level < 20) {
            return 1;
        }
        if (level >= 20 && level < 40) {
            return 2;
        }
        if (level >= 40 && level < 60) {
            return 3;
        }
        if (level >= 60 && level < 80) {
            return 4;
        }
        if (level >= 80) {
            return 5;
        }
        return 0;
    }

    @Override
    public String message_missingTrap1() {
        return "high enough hunter level";
    }

    @Override
    public String message_missingTrap2() {
        return "can't have more";
    }

    @Override
    public String message_notOurTrap() {
        return "isn't your trap";
    }

    @Override
    public String message_cantLayTrap() {
        return "can't lay a trap here";
    }

    @Override
    public int obj_TrapCaught() {
        return 19186;
    }

    @Override
    public int obj_TrapFailed() {
        return 19174;
    }

    @Override
    public int[] obj_TrapOther() {
        return new int[]{19176, 19185};
    }

    @Override
    public int obj_TrapSetup() {
        return 19175;
    }

    @Override
    public void cleanseInventory() {
        try {
            int[] ids;
            if (Hunter.buryBones) {
                ids = new int[]{9978};
            } else {
                ids = new int[]{526, 9978};
            }
            int s = Mouse.getSpeed();
            Mouse.setSpeed(3);
            if (Hunter.buryBones) {
                Item[] arr = org.rsbuddy.tabs.Inventory.getItems(ids);
                int l = arr.length;
                int c = 0;
                Item[] arr3 = org.rsbuddy.tabs.Inventory.getItems(526);
                for (Item i : arr3) {
                    if (i.interact("Bury"))
                        Task.sleep(200, 500);
                }
                if (c < l) {
                    while (c < l) {
                        arr[c].interact("Drop");
                        c++;
                    }
                }
            } else {
                Item[] arr = org.rsbuddy.tabs.Inventory.getItems(ids);
                for (Item i : arr) {
                    i.interact("Drop");
                    Task.sleep(300, 600);
                }
            }
            Task.sleep(1000, 2000);
            Mouse.setSpeed(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int huntingNPCID() {
        return 0;
    }

}