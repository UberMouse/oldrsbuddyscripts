package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.task.Task;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.utils.Banking;
import nz.ubermouse.artealtar.utils.Debug;
import nz.ubermouse.artealtar.utils.Misc;
import nz.uberutils.helpers.Utils;
import org.rsbuddy.tabs.Inventory;
import org.rsbuddy.tabs.Summoning;
import org.rsbuddy.widgets.Bank;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:45 PM
 * Package: nz.ubermouse.Shared.strategies;
 */
public class BankStrat extends Debug implements ArteAltar.Strategy
{
    public void execute() {
        if (!Shared.addedTrip) {
            Misc.tripsMade++;
            Shared.addedTrip = true;
        }
        Shared.offeredBones = false;
        Shared.bonesInBoB = 0;
        if (!Bank.isOpen()) {
            debug("Opening Bank");
            if (!Banking.open()) {
                debug("Bank failed to open");
                return;
            }
            int timeout = 0;
            while (!Bank.isOpen() && ++timeout <= 30)
                Task.sleep(100);
            if (!Bank.isOpen())
                return;
        }
        Task.sleep(Utils.random(600, 1000));
        if (Shared.useBoB) {
            Banking.makeInventoryCount(GameConstants.SUMMONING_POTION, 1);
            if (Summoning.getTimeLeft() < 5.0 && !Inventory.contains(Shared.pouchid)) {
                if (Bank.getCount(Shared.pouchid) == 0) {
                    Utils.log("Out of pouches, stopping");

                }
                if (!Banking.makeInventoryCount(Shared.pouchid, 1))
                    return;
            }
        }
        if (Shared.needNewROD && !Inventory.contains(GameConstants.RING_OF_DUELING_ID)) {
            debug("Need new ROD, withdrawing");
            if (Bank.getCount(GameConstants.RING_OF_DUELING_ID) == 0) {
                Utils.log("Out of rings of dueling, stopping");

                Task.sleep(1250);
            }
            if (Banking.makeInventoryCount(GameConstants.RING_OF_DUELING_ID, 1)) {
                debug("Withdrew ROD");
                Shared.needNewROD = false;
            }
            else
                return;
        }
        if (Shared.lightBurners && Inventory.getCount(GameConstants.MARENTILL_ID) != 2) {
            if (Bank.getCount(GameConstants.MARENTILL_ID) == 0) {
                Utils.log("Out of marentill, stopping");

                Task.sleep(1250);
            }
            debug("Light burners true, withdrawing marrentill");
            if (!Banking.makeInventoryCount(GameConstants.MARENTILL_ID, 2))
                return;
        }
        debug("Withdrawing bones");
        if (Bank.getCount(Shared.boneid) == 0) {
            debug("Out of bones, stopping");

            Task.sleep(1250);
        }
        if (!Inventory.isFull())
            Bank.withdraw(Shared.boneid, 0);
        Bank.close();
    }


    public boolean isValid() {
        if (Banking.nearBank() && Banking.bankOnScreen()) {
            if (!Inventory.isFull() || !(Inventory.getCount(Shared.boneid) > 0) || Shared.needNewROD) {
                return true;
            }
            if ((Shared.lightBurners) && Inventory.getCount(GameConstants.MARENTILL_ID) != 2)
                return true;
        }
        return false;
    }
}
