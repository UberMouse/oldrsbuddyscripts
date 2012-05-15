package nz.artepestcontrol.stratagies;

import com.rsbuddy.script.methods.Combat;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.SpecialAttack;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.UberPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/10/11
 * Time: 8:41 PM
 * Package: nz.artezombies.strategies;
 */
public class UseSpecial implements Strategy
{
    public void execute() {
        SpecialAttack.doSpecial();
    }

    public boolean isValid() {
        return Utils.inGame() && SpecialAttack.shouldSpec() && !Combat.isSpecialEnabled() && UberPlayer.inCombat(true);
    }

    public String getStatus() {
        return "Using special attack";
    }
}
