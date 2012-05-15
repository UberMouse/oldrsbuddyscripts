package nz.artefalconry.stratagies;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Settings;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Npc;
import nz.artefalconry.ArteFalconry;
import nz.artefalconry.misc.GameConstants;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 11:58 AM
 * Package: nz.artefalconry.strategies;
 */
public class ReGetFalcon implements Strategy
{
    public static int oldSetting;

    public void execute() {
        if (ArteFalconry.getFalcon() != null) {
            ArteFalconry.hasFalcon = true;
            return;
        }
        if (oldSetting != Settings.get(334)) {
            ArteFalconry.hasFalcon = true;
            oldSetting = Settings.get(334);
        }
        Walking.findPath(GameConstants.falconerTile).traverse();
        Npc falconer = Npcs.getNearest(GameConstants.falconerID);
        UberMovement.turnTo(falconer);
        if (falconer != null && falconer.isOnScreen()) {
            if (falconer.interact("Falconry")) {
                int timeout = 0;
                while (!Widgets.getComponent(236, 1).isValid() && ++timeout < 20)
                    Task.sleep(100);
                if (Widgets.getComponent(236, 1).click()) {
                    timeout = 0;
                    while (!Widgets.canContinue() && ++timeout < 20) {
                        Task.sleep(100);
                    }
                }
            }
        }
    }


    public boolean isValid() {
        return !ArteFalconry.hasFalcon;
    }

    public String getStatus() {
        return "Getting falcon back";
    }
}
