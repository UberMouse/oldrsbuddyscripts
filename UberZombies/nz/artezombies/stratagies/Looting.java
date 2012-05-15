package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.GroundItems;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.wrappers.GroundItem;
import com.rsbuddy.script.wrappers.Item;
import nz.artezombies.ArteZombies;
import nz.artezombies.misc.GameConstants;
import nz.artezombies.misc.Shared;
import nz.uberutils.helpers.*;
import nz.uberutils.helpers.tasks.PriceThread;
import nz.uberutils.wrappers.LootItem;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 8:13 PM
 * Package: PACKAGE_NAME;
 */
public class Looting implements Strategy
{
    public static final Filter<GroundItem> filterDrops = new Filter<GroundItem>()
    {
        public boolean accept(GroundItem item) {
            if (Calculations.distanceTo(item.getLocation()) >= 15 ||
                !Options.getBoolean("pickupLoot") ||
                Utils.arrayContains(GameConstants.JUNKIDS, item.getItem().getId()))
                return false;
            LootItem[] loot = Shared.instance().loot.toArray(new LootItem[Shared.instance().loot.size()]);
            LootItem[] pLoot = Shared.instance()
                    .priorityLoot
                    .toArray(new LootItem[Shared.instance().priorityLoot.size()]);
            final Item i = item.getItem();
            if (!Options.getBoolean("onlyPriorityLoot") && !UberPlayer.inCombat()) {
                if (Utils.arrayContains(loot, i.getName()) || Utils.arrayContains(loot, i.getId()))
                    return true;
            }
            return Utils.arrayContains(pLoot, i.getName()) ||
                   Utils.arrayContains(pLoot, i.getId()) ||
                   (PriceThread.priceForId(item.getItem().getId()) * item.getItem().getStackSize()) >
                   ArteZombies.lootPrice;
        }
    };

    public static final Filter<GroundItem> paintFilter = new Filter<GroundItem>()
    {
        public boolean accept(GroundItem item) {
            if (Calculations.distanceTo(item.getLocation()) >= 15 ||
                !Options.getBoolean("pickupLoot") ||
                Utils.arrayContains(GameConstants.JUNKIDS, item.getItem().getId()))
                return false;
            LootItem[] loot = Shared.instance().loot.toArray(new LootItem[Shared.instance().loot.size()]);
            LootItem[] pLoot = Shared.instance().priorityLoot.toArray(new LootItem[Shared.instance().loot.size()]);
            if ((Utils.arrayContains(loot, item.getItem().getId()) ||
                 Utils.arrayContains(loot, item.getItem().getName())) && !Options.getBoolean("onlyPriorityLoot"))
                return true;
            return Utils.arrayContains(pLoot, item.getItem().getId()) ||
                   (PriceThread.priceForId(item.getItem().getId()) * item.getItem().getStackSize()) >
                   ArteZombies.lootPrice ||
                   Utils.arrayContains(pLoot, item.getItem().getName());
        }
    };

    public void execute() {
        Loot.takeLoot(filterDrops);
    }

    public boolean isValid() {
        return Loot.shouldLoot() &&
               UberPlayer.prayer() > 5 &&
               Options.getBoolean("pickupLoot") &&
               Objects.getNearest(GameConstants.LADDERID) != null;
    }

    public String getStatus() {
        if (GroundItems.getNearest(filterDrops) != null)
            return "Looting: " + GroundItems.getNearest(filterDrops).getItem().getName();
        return "Looting";
    }
}
