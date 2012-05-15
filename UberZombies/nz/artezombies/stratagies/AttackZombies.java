package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.wrappers.Npc;
import nz.artezombies.ArteZombies;
import nz.artezombies.misc.GameConstants;
import nz.artezombies.misc.Utils;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 8:25 PM
 * Package: nz.artezombies.strategies;
 */
public class AttackZombies implements Strategy
{
    public void execute() {
        ArteZombies.log.debug("Entering AttackZombies execute()");
        if (UberPlayer.inCombat()) {
            ArteZombies.log.debug("Returning because in combat");
            return;
        }
        Npc zombie = null;
        Npc zombieInteract = Npcs.getNearest(new Filter<Npc>()
        {
            public boolean accept(Npc npc) {
                return npc.getInteracting() != null &&
                       npc.getInteracting().equals(UberPlayer.get()) &&
                       Utils.arrayContains(GameConstants.ZOMBIEALLIDS, npc.getId());
            }
        });
        if (zombieInteract == null) {
            zombie = Npcs.getNearest(new Filter<Npc>()
            {
                public boolean accept(Npc npc) {
                    return (Utils.arrayContains(GameConstants.ZOMBIEIDS, npc.getId()) &&
                            npc.getHpPercent() > 0 &&
                            !npc.isInCombat() &&
                            Calculations.distanceTo(npc) <= 10);
                }
            });
        }
        else
            zombie = zombieInteract;

        if (zombie != null) {
            UberMovement.turnTo(zombie);
            zombie.interact("Attack");
            for (int i = 0; i < 15 && !UberPlayer.inCombat(); i++)
                Utils.sleep(100);
        }
        ArteZombies.log.debug("Returning from AttackZombies execute()");
    }

    public boolean isValid() {
        return Objects.getNearest(GameConstants.ALTARID) == null &&
               UberPlayer.prayer() > 5 &&
               Npcs.getNearest(GameConstants.ZOMBIEALLIDS) != null;
    }

    public String getStatus() {
        return (UberPlayer.isInteracting()) ? "Attacking: " + UberPlayer.interacting().getName() : "Attacking";
    }
}
