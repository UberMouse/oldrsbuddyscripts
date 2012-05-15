package nz.artepestcontrol.stratagies;

import com.rsbuddy.script.methods.Combat;
import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.wrappers.Npc;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberCombat;
import nz.uberutils.methods.UberMovement;
import nz.uberutils.wrappers.UberNpc;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/9/11
 * Time: 8:37 PM
 * Package: nz.artepestcontrol.strategies;
 */
public class AttackEnemies implements Strategy
{
    private static Filter<Npc> enemyFilter = null;

    public void execute() {
        if (!UberPlayer.inCombat(true)) {
            UberNpc enemy = new UberNpc(Npcs.getNearest(enemyFilter));
            if (!enemy.isNull()) {
                UberMovement.turnTo(enemy);
                if (enemy.interact("attack")) {
                    if (!Combat.isQuickPrayerOn() && UberPlayer.prayer() > 0)
                        UberCombat.toggleQuickPrayers();
                    for (int i = 0; i < 15 && !UberPlayer.inCombat(); i++) {
                        if (UberPlayer.isMoving())
                            i = 0;
                        Utils.sleep(100);
                    }
                }
            }
        }
    }

    public boolean isValid() {
        return Npcs.getNearest(enemyFilter) != null;
    }

    public String getStatus() {
        return (UberPlayer.isInteracting()) ? "Attacking: " + UberPlayer.interacting().getName() : "Attacking";
    }

    public static void setFilter(Filter<Npc> filter) {
        enemyFilter = filter;
    }
}
