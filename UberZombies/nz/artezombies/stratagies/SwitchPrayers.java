package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Combat;
import com.rsbuddy.script.methods.Widgets;
import nz.artezombies.misc.GameConstants;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberPrayer;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 8:02 PM
 * Package: nz.artezombies.strategies;
 */
public class SwitchPrayers implements Strategy
{
    public void execute() {
        UberPrayer.Prayer prayer = null;
        if (!Options.getBoolean("useSoulSplit") && Options.getBoolean("useProtectionPrayers")) {
            if (Utils.arrayContains(GameConstants.ZOMBIEIDS, UberPlayer.getInteractor().getId())) {
                if (UberPrayer.isUsingRegularPrayers() ?
                    !UberPrayer.Prayer.PROTECT_FROM_MELEE.isSelected() :
                    !UberPrayer.Prayer.DEFLECT_MELEE.isSelected())
                    prayer = (UberPrayer.isUsingRegularPrayers()) ?
                             UberPrayer.Prayer.PROTECT_FROM_MELEE :
                             UberPrayer.Prayer.DEFLECT_MELEE;
            }
            else if (Options.getBoolean("switchPrayers") &&
                     Utils.arrayContains(GameConstants.ZOMBIERANGEIDS, UberPlayer.getInteractor().getId()))
                if (UberPrayer.isUsingRegularPrayers() ?
                    !UberPrayer.Prayer.PROTECT_FROM_MISSILES.isSelected() :
                    !UberPrayer.Prayer.DEFLECT_MISSILE.isSelected())
                    prayer = (UberPrayer.isUsingRegularPrayers()) ?
                             UberPrayer.Prayer.PROTECT_FROM_MISSILES :
                             UberPrayer.Prayer.DEFLECT_MISSILE;
        }
        else if (Options.getBoolean("useSoulSplit")) {
            if (!Options.getBoolean("ssAlwaysOn")) {
                if (!UberPrayer.Prayer.SOUL_SPLIT.isSelected() && UberPlayer.hp() <= 65)
                    prayer = UberPrayer.Prayer.SOUL_SPLIT;
                else if (UberPrayer.Prayer.SOUL_SPLIT.isSelected() && UberPlayer.hp() >= 95) {
                    UberPrayer.Prayer.SOUL_SPLIT.setActivated(false);
                    for (int i = 0; i < 35 && UberPrayer.Prayer.SOUL_SPLIT.isSelected(); i++)
                        Utils.sleep(100);
                }
            }
            else if (!UberPrayer.Prayer.SOUL_SPLIT.isSelected())
                prayer = UberPrayer.Prayer.SOUL_SPLIT;
        }
        if (prayer != null) {
            if (!Combat.isQuickPrayerOn())
                Widgets.getComponent(749, 5).click();
            prayer.setActivated(true);
            for (int i = 0; i < 35 && !prayer.isSelected(); i++)
                Utils.sleep(100);
        }
    }

    public boolean isValid() {
        if (UberPlayer.getInteractor() == null || UberPlayer.prayer() <= 5 || !UberPlayer.inCombat())
            return false;
        if (!Options.getBoolean("useSoulSplit") && Options.getBoolean("useProtectionPrayers")) {
            if (Utils.arrayContains(GameConstants.ZOMBIEIDS, UberPlayer.getInteractor().getId())) {
                if (UberPrayer.isUsingRegularPrayers() ?
                    !UberPrayer.Prayer.PROTECT_FROM_MELEE.isSelected() :
                    !UberPrayer.Prayer.DEFLECT_MELEE.isSelected())
                    return true;
            }
            else if (Options.getBoolean("switchPrayers") &&
                     Utils.arrayContains(GameConstants.ZOMBIERANGEIDS, UberPlayer.getInteractor().getId()))
                if (UberPrayer.isUsingRegularPrayers() ?
                    !UberPrayer.Prayer.PROTECT_FROM_MISSILES.isSelected() :
                    !UberPrayer.Prayer.DEFLECT_MISSILE.isSelected())
                    return true;
        }
        else if (Options.getBoolean("useSoulSplit")) {
            return !UberPrayer.Prayer.SOUL_SPLIT.isSelected() && UberPlayer.hp() <= 65 ||
                   UberPrayer.Prayer.SOUL_SPLIT.isSelected() && UberPlayer.hp() >= 95;
        }
        return false;
    }

    public String getStatus() {
        return "Switching prayers to attack style";
    }
}
