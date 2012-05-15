package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.util.Timer;
import com.rsbuddy.script.wrappers.Npc;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.utils.Banking;
import nz.ubermouse.artealtar.utils.Debug;
import nz.ubermouse.artealtar.utils.MySummoning;
import nz.uberutils.helpers.Utils;
import org.rsbuddy.tabs.Inventory;
import org.rsbuddy.tabs.Summoning;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:46 PM
 * Package: nz.ubermouse.artealtar.strategies;
 */
public class BoB extends Debug implements ArteAltar.Strategy
{


    public void execute() {
        Npc bob = MySummoning.getSummonedNpc();
        debug(Shared.bonesInBoB);
        if (bob == null)
            return;
        if (bob.interact("Store")) {
            Timer timer = new Timer(2000);
            while (!Widgets.getComponent(671, 27).isValid() && timer.isRunning())
                Task.sleep(100);
            int count = Inventory.getCount(Shared.boneid);
            if (Inventory.getItem(Shared.boneid) != null && Inventory.getItem(Shared.boneid).interact("Store-All")) {
                Task.sleep(Utils.random(400, 600));
                if (count > MySummoning.getSummonedFamiliar().getInventorySpace())
                    count = MySummoning.getSummonedFamiliar().getInventorySpace();
                Shared.bonesInBoB += count;
                debug(Shared.bonesInBoB);
            }
            while (Widgets.getComponent(671, 27).isValid()) {
                Widgets.getComponent(671, 13).click();
                Task.sleep(Utils.random(400, 600));
            }
        }
    }


    public boolean isValid() {
        return MySummoning.getSummonedFamiliar() != null &&
               Banking.nearBank() &&
               Shared.bonesInBoB < MySummoning.getSummonedFamiliar().getInventorySpace() &&
               Inventory.getCount(Shared.boneid) > 0;
    }

}
