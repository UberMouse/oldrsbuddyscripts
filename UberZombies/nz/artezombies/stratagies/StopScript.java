package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.wrappers.GameObject;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberMovement;
import nz.artezombies.ArteZombies;
import nz.artezombies.misc.GameConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/3/11
 * Time: 5:56 PM
 * Package: nz.artezombies.strategies;
 */
public class StopScript implements Strategy
{
    public void execute() {
        if (Objects.getNearest(GameConstants.LADDERID) != null) {
            GameObject ladder = Objects.getNearest(GameConstants.LADDERID);
            UberMovement.turnTo(ladder);
            if (ladder.interact("Climb")) {
                for (int i = 0; i < 15 && Objects.getNearest(GameConstants.LADDERID) != null; i++)
                    Utils.sleep(100);
            }
        }
        else if (Game.isLoggedIn() && Game.getCurrentTab() == Game.TAB_INVENTORY)
            ArteZombies.stopScript = true;
    }

    public boolean isValid() {
        return UberPlayer.hp() <= 30 &&
               UberPlayer.edibleItem() == null &&
               Game.isLoggedIn() &&
               Game.getCurrentTab() == Game.TAB_INVENTORY;
    }

    public String getStatus() {
        return "Stopping script";
    }
}
