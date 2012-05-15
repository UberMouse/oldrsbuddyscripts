package nz.artezombies.stratagies;

import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.SpecialAttack;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberInventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 8:30 PM
 * Package: nz.artezombies.strategies;
 */
public class Eat implements Strategy
{
    public void execute() {
        if (!Options.getBoolean("excalibur") || UberPlayer.hp() <= 25)
            UberPlayer.eat();
        else
            SpecialAttack.doSpecial();
    }

    public boolean isValid() {
        if (!Options.getBoolean("excalibur"))
            return ((!UberPlayer.inCombat() && UberPlayer.hp() <= 50) || UberPlayer.hp() <= 40) &&
                   UberPlayer.edibleItem() != null;
        else
            return (UberPlayer.hp() < 60 && SpecialAttack.canSpec() ||
                    UberPlayer.hp() <= 25 ||
                    UberInventory.contains(SpecialAttack.getPrimaryWeapon()));
    }

    public String getStatus() {
        return "Healing";
    }
}
