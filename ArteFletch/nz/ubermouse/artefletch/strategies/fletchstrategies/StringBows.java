package nz.ubermouse.artefletch.strategies.fletchstrategies;

import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.wrappers.Item;
import nz.ubermouse.artefletch.data.GameConstants;
import nz.ubermouse.artefletch.data.Shared;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberInventory;
import org.rsbuddy.tabs.Inventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 6:05 PM
 * Package: nz.ubermouse.artefletch.strategies.fletchstrategies;
 */
public class StringBows
{
    public static void execute(int id) {
        Item unfinished = UberInventory.getItem(id);
        if(unfinished != null) {
            Item string = UberInventory.getItem(GameConstants.BOW_STRING);
            if(string != null) {
                if(Inventory.useItem(unfinished, string)) {
                    Utils.sleepUntilValid(GameConstants.MAKE_IFACE, GameConstants.MAKE_SUB_IFACE);
                    if(Widgets.getComponent(GameConstants.MAKE_IFACE, GameConstants.MAKE_SUB_IFACE).click())
                        Shared.fletching = true;
                }
            }
        }
    }
}
