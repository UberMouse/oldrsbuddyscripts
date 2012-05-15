package nz.artezombies.stratagies;

import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.SpecialAttack;
import nz.uberutils.helpers.Strategy;

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
        return SpecialAttack.shouldSpec() && UberPlayer.inCombat();
    }

    public String getStatus() {
        return "Using special attack";
    }
}
