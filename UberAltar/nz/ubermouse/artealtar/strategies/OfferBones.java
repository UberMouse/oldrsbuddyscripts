package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Players;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.utils.Debug;
import nz.ubermouse.artealtar.utils.UberMovement;
import org.rsbuddy.tabs.Inventory;
import org.rsbuddy.tabs.Summoning;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:49 PM
 * Package: nz.ubermouse.artealtar.strategies;
 */
public class OfferBones extends Debug implements ArteAltar.Strategy
{
    int bobTakenCount = 0;


    public void execute() {
        if (!Shared.offeredBones &&
            !Widgets.getComponent(GameConstants.OFFER_INTERFACE_ID, GameConstants.OFFER_INTERFACE_SUBID).isValid()) {
            GameObject altar = Objects.getNearest(GameConstants.ALTAR_ID);
            UberMovement.turnTo(altar);
            debug("Offering bones");
            if (Widgets.getComponent(GameConstants.OFFER_INTERFACE_ID, GameConstants.OFFER_INTERFACE_SUBID).isValid())
                return;
            Inventory.useItem(Inventory.getItem(Shared.boneid), altar);
            int timeout = 0;
            while (!Widgets.getComponent(GameConstants.OFFER_INTERFACE_ID, GameConstants.OFFER_INTERFACE_SUBID)
                           .isValid() && ++timeout <= 15)
                Task.sleep(100);
        }
        else if (Widgets.getComponent(GameConstants.OFFER_INTERFACE_ID, GameConstants.OFFER_INTERFACE_SUBID)
                        .isValid()) {
            if (Widgets.getComponent(GameConstants.OFFER_INTERFACE_ID, GameConstants.OFFER_INTERFACE_SUBID).click()) {
                int timeout = 0;
                while (Players.getLocal().getAnimation() == -1 && ++timeout <= 30)
                    Task.sleep(100);
                Shared.offeredBones = true;
            }
        }
        else if (Shared.bonesInBoB > 0) {
            if (Inventory.getCount(Shared.boneid) < 6) {
                int count = Summoning.getSummonedFamiliar().getInventorySpace();
                if (count > (28 - Inventory.getCount()))
                    count = 28 - Inventory.getCount();
                Summoning.takeBob();
                bobTakenCount++;
                Shared.bonesInBoB -= count;
            }
        }
    }


    public boolean isValid() {
        return Inventory.getCount(Shared.boneid) > 0 &&
               ((Shared.lightBurners) ?
                Objects.getNearest(GameConstants.LITBURNERS) != null :
                Objects.getNearest(GameConstants.ALTAR_ID) != null &&
                Calculations.distanceTo(Objects.getNearest(GameConstants.ALTAR_ID)) < 6);
    }

}
