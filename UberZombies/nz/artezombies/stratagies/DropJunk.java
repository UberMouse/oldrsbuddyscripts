package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Inventory;
import com.rsbuddy.script.wrappers.Item;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberInventory;
import nz.artezombies.misc.GameConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/29/11
 * Time: 7:07 AM
 * Package: nz.artezombies.strategies;
 */
public class DropJunk implements Strategy
{
    public void execute() {
        for(Item item : UberInventory.getItems())
            if(Utils.arrayContains(GameConstants.JUNKIDS, item.getId()))
                item.interact("Drop");
    }

    public boolean isValid() {
        return Inventory.containsOneOf(GameConstants.JUNKIDS);
    }

    public String getStatus() {
        return "Dropping junk";
    }
}
