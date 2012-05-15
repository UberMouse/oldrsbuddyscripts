package nz.artefalconry.stratagies;

import com.rsbuddy.script.wrappers.Item;
import nz.artefalconry.misc.GameConstants;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;
import org.rsbuddy.tabs.Inventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 12:00 PM
 * Package: nz.artefalconry.strategies;
 */
public class DropJunk implements Strategy
{
    public void execute() {
            for (Item item : Inventory.getItems()) {
                if (Utils.arrayContains(GameConstants.junk, item.getId())) {
                    item.interact("Drop");
                }
            }
        }


        public boolean isValid() {
            return Inventory.getCount() > 26;
        }

    public String getStatus() {
        return "Dropping junk";
    }
}
