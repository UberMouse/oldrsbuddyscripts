package nz.artepestcontrol.stratagies.tasks;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.wrappers.Npc;
import nz.artepestcontrol.ArtePestControl;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.methods.UberNpcs;
import nz.uberutils.wrappers.UberNpc;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 3:56 PM
 * Package: nz.artepestcontrol.strategies.tasks;
 */
public class BrawlerBlock extends LoopTask
{
    private static final Filter<Npc> f = new Filter<Npc>()
    {
        public boolean accept(Npc npc) {
            return npc.getName().equalsIgnoreCase("brawler") && Calculations.distanceTo(npc) <= 2;
        }
    };

    @Override
    public int loop() {
        boolean killBrawler = false;
        if (Utils.inGame()) {
            if (Walking.getDestination() != null)
                killBrawler = !Calculations.isReachable(Walking.getDestination(), false);
            if (!killBrawler && UberPlayer.isInteracting())
                killBrawler = !Calculations.isReachable(UberPlayer.interacting().getLocation(), false);
            if (killBrawler) {
                UberNpc brawler = UberNpcs.getNearest(f);
                if (brawler != null) {
                    if (ArtePestControl.isLocked())
                        ArtePestControl.lock();
                    UberPlayer.attack(brawler);
                }
            }
        }
        if (!killBrawler || !Utils.inGame())
            if (ArtePestControl.isLocked())
                ArtePestControl.unLock();
        return Utils.random(400, 500);
    }
}
