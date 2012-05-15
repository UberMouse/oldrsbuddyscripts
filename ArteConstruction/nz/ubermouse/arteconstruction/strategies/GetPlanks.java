package nz.ubermouse.arteconstruction.strategies;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Npc;
import nz.ubermouse.arteconstruction.data.GameConstants;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberInventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/5/11
 * Time: 8:00 PM
 * Package: nz.ubermouse.arteconstruction.strategies;
 */
public class GetPlanks implements Strategy
{
    public void execute() {
        Npc butler = Npcs.getNearest(GameConstants.DEMON_BUTLER);
        if (butler != null) {
            butler.interact("Fetch-");
            for (int i = 0; i <= 15 && Utils.getWidgetWithText("fetch another") == null; i++)
                Task.sleep(100);
            if (Utils.getWidgetWithText("fetch another") != null)
                Utils.getWidgetWithText("fetch another").click();
            Task.sleep(400,500);
        }
    }

    public boolean isValid() {
        return UberInventory.getCount("plank") < 20 &&
               Objects.getNearest(GameConstants.MAHOGANY_TABLE) == null &&
               Npcs.getNearest(GameConstants.DEMON_BUTLER) != null &&
               Utils.getWidgetWithText("select a piece") == null;
    }

    public String getStatus() {
        return "Getting planks from bank";
    }
}
