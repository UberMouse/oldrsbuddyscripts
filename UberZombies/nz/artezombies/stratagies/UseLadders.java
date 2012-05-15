package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.wrappers.GameObject;
import nz.artezombies.misc.GameConstants;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 7:55 PM
 * Package: nz.artezombies.strategies;
 */
public class UseLadders implements Strategy
{
    public void execute() {
        if (Objects.getNearest(GameConstants.ALTARID) != null) {
            GameObject trapdoor = Objects.getNearest(GameConstants.TRAPDOORID);
            if (trapdoor != null) {
                UberMovement.turnTo(trapdoor);
                Camera.turnTo(trapdoor);
                if (!Combat.isQuickPrayerOn()) {
                    if (Widgets.getComponent(749, 5).click()) {
                        for (int i = 0; i < 15 && !Combat.isQuickPrayerOn(); i++)
                            Utils.sleep(100);
                        Utils.sleep(600, 700);
                    }
                }
                if (trapdoor.interact("Enter"))
                    for (int i = 0; i < 15 && Objects.getNearest(GameConstants.ALTARID) != null; i++)
                        Utils.sleep(100);
            }
        }
        else {
            GameObject ladder = Objects.getNearest(GameConstants.LADDERID);
            if (ladder != null) {
                UberMovement.turnTo(ladder);
                if (ladder.interact("Climb"))
                    for (int i = 0; i < 15 && Objects.getNearest(GameConstants.ALTARID) == null; i++)
                        Utils.sleep(100);
            }
        }
    }

    public boolean isValid() {
        if (Objects.getNearest(GameConstants.LADDERID, GameConstants.TRAPDOORID) == null)
            return false;
        if (Objects.getNearest(GameConstants.ALTARID) != null &&
            UberPlayer.prayer() == (Skills.getRealLevel(Skills.PRAYER) * 10))
            return true;
        else if (Objects.getNearest(GameConstants.ALTARID) == null && UberPlayer.prayer() <= 50)
            return true;
        return false;
    }

    public String getStatus() {
        return Objects.getNearest(GameConstants.ALTARID) != null ? "Climbing down trapdoor" : "Climbing up ladder";
    }
}
