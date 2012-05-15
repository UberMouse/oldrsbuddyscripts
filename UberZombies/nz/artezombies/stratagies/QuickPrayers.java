package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Combat;
import com.rsbuddy.script.methods.Widgets;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Utils;
import org.rsbuddy.tabs.Prayer;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 10:07 PM
 * Package: nz.artezombies.strategies;
 */
public class QuickPrayers implements Strategy
{
    public void execute() {
        if (Widgets.getComponent(749, 5).click()) {
            for (int i = 0; i < 15 && !Combat.isQuickPrayerOn(); i++)
                Utils.sleep(100);
            Utils.sleep(600, 700);
        }
    }

    public boolean isValid() {
        return UberPlayer.inCombat() && !Combat.isQuickPrayerOn() && Prayer.getRemainingPoints() > 0;
    }

    public String getStatus() {
        return "Turning quick prayers on";
    }
}
