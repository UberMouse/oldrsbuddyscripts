package nz.artepestcontrol.stratagies;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.wrappers.Npc;
import nz.artepestcontrol.misc.GameConstants;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberNpcs;
import nz.uberutils.wrappers.UberNpc;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/2/11
 * Time: 8:18 PM
 * Package: nz.artepestcontrol.strategies;
 */
public class AttackPortals implements Strategy
{
    public void execute() {
        UberNpc portal = UberNpcs.getNearest(GameConstants.PORTALS);
        UberNpc spinner = UberNpcs.getNearest("spinner");
        UberNpc enemy = (spinner != null) ? spinner : portal;
        if (enemy != null && (!UberPlayer.inCombat(true) || !UberPlayer.interacting().equals(enemy)))
            UberPlayer.attack(enemy);
    }

    public boolean isValid() {
        if (!Options.getBoolean("attackPortals"))
            return false;
        Npc portal = Npcs.getNearest(GameConstants.PORTALS);
        return portal != null && portal.getHpPercent() > 0 && portal.isInCombat();
    }

    public String getStatus() {
        return "Attacking portal";
    }
}
