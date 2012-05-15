package nz.ubermouse.artehunter.traps.impl;

import com.rsbuddy.script.methods.Skills;
import nz.ubermouse.artehunter.traps.Trap;

public class GrayChinchompas extends Trap {

    @Override
    public int InventoryTrapID() {
        return 10008;
    }

    @Override
    public int InventoryUnitID() {
        return 10033;
    }

    @Override
    public int animation_layTrap() {
        return 5208;
    }

    @Override
    public double expPerUnit() {
        return 198.0;
    }

    @Override
    public int getTrapAbilityCount() {
        int level = Skills.getCurrentLevel(Skills.HUNTER);
        if (level >= 53 && level < 60) {
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
        return 19189;
    }

    @Override
    public int obj_TrapFailed() {
        return 19192;
    }

    @Override
    public int[] obj_TrapOther() {
        return new int[]{19188, 19194, 19196, 19195, 19193};
    }

    @Override
    public int obj_TrapSetup() {
        return 19187;
    }

    @Override
    public void cleanseInventory() {
    }

    @Override
    public int huntingNPCID() {
        return 0;
    }

}