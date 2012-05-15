package nz.artezombies.misc;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.wrappers.Item;
import nz.artezombies.ArteZombies;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.methods.UberInventory;
import nz.uberutils.wrappers.BankItem;
import org.rsbuddy.tabs.Summoning;
import org.rsbuddy.widgets.Bank;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/20/11
 * Time: 6:10 PM
 * Package: nz.artezombies.threads;
 */
public class Utils extends nz.uberutils.helpers.Utils
{
    public static boolean needsToBank() {
        if (Shared.instance().bankItems.size() == 0 || !Options.getBoolean("bank"))
            return false;
        if (Options.getBoolean("useFood")) {
            if (UberPlayer.edibleItem() == null) {
                ArteZombies.log.debug("Banking because out of food");
                ArteZombies.log.debug("Inventory contains: ");
                for (Item i : UberInventory.getItems())
                    ArteZombies.log.debug("\t" + i.getName());
                return true;
            }
        }
        if (Options.getBoolean("usePots")) {
            if (!UberPlayer.hasPotions()) {
                ArteZombies.log.debug("Banking because out of potions");
                ArteZombies.log.debug("Inventory contains: ");
                for (Item i : UberInventory.getItems())
                    ArteZombies.log.debug("\t" + i.getName());
                return true;
            }
        }
        if (Options.getBoolean("useSummoning")) {
            if (!Summoning.isFamiliarSummoned() || Summoning.getSecondsLeft() <= 240) {
                if (!UberInventory.contains(GameConstants.BUNYIP_POUCH) &&
                    !UberInventory.contains(GameConstants.VOID_SPINNER_POUCH)) {
                    ArteZombies.log.debug("Banking because out of summoning pouches");
                    ArteZombies.log.debug("Inventory contains: ");
                    for (Item i : UberInventory.getItems())
                        ArteZombies.log.debug("\t" + i.getName());
                    return true;
                }
            }
        }
        boolean containsItems = true;
        for (BankItem bi : Shared.instance().bankItems) {
            if (!bi.inventoryContains()) {
                ArteZombies.log
                        .debug("Contains item: InventoryContains " +
                               bi.getName() +
                               ":" +
                               bi.getId() +
                               ":" +
                               bi.getQuantity());
                containsItems = false;
            }
            if (bi.inventoryCount() != bi.getQuantity()) {
                containsItems = false;
                ArteZombies.log
                        .debug("Contains item: Quantity " + bi.getName() + ":" + bi.getId() + ":" + bi.getQuantity());
            }
        }
        if (Npcs.getNearest(Bank.BANKERS) != null && !containsItems) {
            ArteZombies.log.debug("Banking because near NPC and not contains items");
            ArteZombies.log.debug("Inventory contains: ");
            for (Item i : UberInventory.getItems())
                ArteZombies.log.debug("\t" + i.getName());
            ArteZombies.log.debug("GUI Contains: ");
            for (BankItem bi : Shared.instance().bankItems)
                ArteZombies.log.debug("\t" + bi.getName() + ":" + bi.getId() + ":" + bi.getQuantity());
            return true;
        }

        return false;
    }
}
