package nz.artedungeon.strategies;

import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.wrappers.GameObject;
import nz.artedungeon.DungeonMain;
import nz.artedungeon.common.Strategy;
import nz.artedungeon.dungeon.Dungeon;
import nz.artedungeon.dungeon.Explore;
import nz.artedungeon.misc.GameConstants;
import nz.uberutils.methods.UberMovement;


public class JumpStairs extends Strategy
{

    public JumpStairs(DungeonMain parent) {
        super(parent);
    }


    public int execute() {
        parent.clearAll();
        parent.inDungeon = false;
        Dungeon.setFloor(0);
        Explore.setExit(false);
        if (parent.teleportFailSafe == GameConstants.TELEPORT_FAILSAFE) {
            DungeonMain.timesAborted++;
        }
        parent.teleportFailSafe = 0;
        GameObject stairs = Objects.getNearest(GameConstants.END_STAIRS);
        UberMovement.turnTo(stairs);
        if (stairs.interact("Jump")) {

            sleep(1200, 1500);
        }
        return random(400, 600);
    }


    public boolean isValid() {
        return Objects.getNearest(GameConstants.END_STAIRS) != null;
    }


    public void reset() {

    }

    public String getStatus() {
        return "Jumping down stairs";
    }

}
