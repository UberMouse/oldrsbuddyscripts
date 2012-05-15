package nz.ubermouse.artefletch.strategies.fletchstrategies;

import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import nz.ubermouse.artefletch.data.GameConstants;
import nz.ubermouse.artefletch.data.Shared;
import nz.uberutils.helpers.Utils;
import org.rsbuddy.tabs.Inventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 9:19 PM
 * Package: nz.ubermouse.artefletch.strategies.fletchstrategies;
 */
public class MakeArrows
{
    public static void execute(int id) {
        if (Inventory.getCount(GameConstants.LOG) > 0) {
            Inventory.useItem(Inventory.getItem(GameConstants.KNIFE), Inventory.getItem(GameConstants.LOG));
        }
        else if (Inventory.getCount(GameConstants.FEATHER) > 0) {
            Inventory.useItem(Inventory.getItem(GameConstants.FEATHER), Inventory.getItem(GameConstants.ARROW_SHAFT))
            ;
        }
        else {
            Inventory.useItem(Inventory.getItem(id), Inventory.getItem(GameConstants.HEADLESS_ARROW));
        }
        Utils.sleepUntilValid(GameConstants.MAKE_IFACE, GameConstants.MAKE_SUB_IFACE);
        if (Widgets.getComponent(GameConstants.MAKE_IFACE, GameConstants.MAKE_SUB_IFACE) != null)
            if (Widgets.getComponent(GameConstants.MAKE_IFACE, GameConstants.MAKE_SUB_IFACE).click()) {
                Shared.fletching = true;
                for (int i = 0; i <= 20 &&
                                Widgets.getComponent(GameConstants.MAKE_IFACE, GameConstants.MAKE_SUB_IFACE) !=
                                null; i++)
                    Task.sleep(100);
            }
    }
}
