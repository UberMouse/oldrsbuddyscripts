package nz.ubermouse.arteconstruction.strategies;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Npc;
import nz.ubermouse.arteconstruction.data.GameConstants;
import nz.ubermouse.arteconstruction.data.Shared;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/6/11
 * Time: 5:29 PM
 * Package: nz.ubermouse.arteconstruction.strategies;
 */
public class PayButler implements Strategy
{
    public void execute() {
        Npc butler = Npcs.getNearest(GameConstants.DEMON_BUTLER);
        if (butler != null) {
            butler.interact("Talk");
            for (int i = 0; i <= 15 && Utils.getWidgetWithText("coins that are due") == null; i++)
                Task.sleep(100);
            if (Widgets.canContinue())
                Widgets.clickContinue();
            for (int i = 0; i <= 15 && Utils.getWidgetWithText("from your bank") == null; i++) {
                if (Widgets.canContinue())
                    Widgets.clickContinue();
                Task.sleep(100);
            }
            if (Utils.getWidgetWithText("from your bank") != null) {
                Utils.getWidgetWithText("from your bank").click();
                Task.sleep(400, 500);
            }
            Shared.payButler = false;
        }
    }

    public boolean isValid() {
        return Shared.payButler;
    }

    public String getStatus() {
        return "Paying butler";
    }
}
