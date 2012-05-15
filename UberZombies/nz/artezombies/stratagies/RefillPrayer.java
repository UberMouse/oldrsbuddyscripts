package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Skills;
import com.rsbuddy.script.wrappers.GameObject;
import nz.artezombies.misc.GameConstants;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 7:48 PM
 * Package: nz.artezombies.strategies;
 */
public class RefillPrayer implements Strategy
{
    public void execute() {
        GameObject altar = Objects.getNearest(GameConstants.ALTARID);
        if (altar != null) {
            UberMovement.turnTo(altar);
            if (altar.interact("Pray-at")) {
                int p = UberPlayer.prayer();
                for (int i = 0; i < 15 && p == UberPlayer.prayer(); i++)
                    Utils.sleep(100);
            }
        }
    }

    public boolean isValid() {
        return Objects.getNearest(GameConstants.ALTARID) != null &&
               UberPlayer.prayer() < (Skills.getRealLevel(Skills.PRAYER) * 10) &&
               Objects.getNearest(GameConstants.TRAPDOORID) != null;
    }

    public String getStatus() {
        return "Refilling Prayer";
    }
}
