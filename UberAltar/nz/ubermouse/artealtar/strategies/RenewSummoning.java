package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.wrappers.Item;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.utils.Debug;
import org.rsbuddy.tabs.Inventory;
import org.rsbuddy.tabs.Summoning;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:46 PM
 * Package: nz.ubermouse.artealtar.strategies;
 */
public class RenewSummoning extends Debug implements ArteAltar.Strategy
{
    public void execute() {
        //        Path path = Walking.findPath(Shared.obeliskTile);
        //        path.traverse();
        //        GameObject obelisk = Objects.getNearest(GameConstants.OBELISK_IDS);
        //        if (obelisk.interact("Renew")) {
        //            Timer timeout = new Timer(2500);
        //            while (Summoning.getPoints() != Skills.getRealLevel(Skills.SUMMONING) && timeout.isRunning()) {
        //                debug("Sleeping");
        //                Task.sleep(100);
        //            }
        //            if ((Summoning.isFamiliarSummoned()) ?
        //                Summoning.renewFamiliar() :
        //                Inventory.getItem(Shared.pouchid).interact("Summon")) {
        //                while (path.getNext() != null)
        //                    path.traverse();
        //            }
        //        }
        if (Summoning.getPoints() < 15) {
            Item potion = Inventory.getItem(GameConstants.SUMMONING_POTION);
            if (potion != null)
                potion.click(true);
        }
        else {
            if (Summoning.isFamiliarSummoned())
                Summoning.renewFamiliar();
            else
                Inventory.getItem(Shared.pouchid).interact("Summon");
        }
    }


    public boolean isValid() {
        try {
            return (Summoning.getTimeLeft() < 2.5 || !Summoning.isFamiliarSummoned()) &&
                   Inventory.contains(Shared.pouchid);
        } catch (Exception ignored) {
        }
        return false;
    }

}
