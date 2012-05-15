package nz.ubermouse.arteconstruction.strategies;

import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Component;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.arteconstruction.data.GameConstants;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberInventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/5/11
 * Time: 7:32 PM
 * Package: nz.ubermouse.arteconstruction.strategies;
 */
public class MakeItem implements Strategy
{
    public void execute() {
        GameObject buildSpace = Objects.getNearest(GameConstants.TABLE_SPACE);
        if (buildSpace != null) {
            buildSpace.interact("Build Door space");
            for (int i = 0; i <= 15 && Utils.getWidgetWithText("a piece of") == null; i++)
                Task.sleep(100);
            Component c = Widgets.getComponent(394, 11).getComponent(0);
            if (c != null) {
                c.interact("build");
                for (int i = 0; i < 15 && c.isValid(); i++)
                    Task.sleep(100);
            }
        }
    }

    public boolean isValid() {
        return UberInventory.getCount("plank") >= 6 && Objects.getNearest(GameConstants.TABLE_SPACE) != null;
    }

    public String getStatus() {
        return "Making item";
    }
}
