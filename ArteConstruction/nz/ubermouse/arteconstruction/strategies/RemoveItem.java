package nz.ubermouse.arteconstruction.strategies;

import com.rsbuddy.script.methods.Mouse;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.arteconstruction.data.GameConstants;
import nz.uberutils.helpers.Strategy;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/5/11
 * Time: 8:05 PM
 * Package: nz.ubermouse.arteconstruction.strategies;
 */
public class RemoveItem implements Strategy
{
    public void execute() {
        if (Widgets.getComponent(394, 13) != null || Widgets.getComponent(394, 13).isValid())
            Widgets.getComponent(394, 12).click();
        GameObject table = Objects.getNearest(GameConstants.MAHOGANY_TABLE);
        if (Objects.getNearest(13351) != null)
            Objects.getNearest(13351).interact("Close");
        if (table != null) {
            if (Widgets.getComponent(228, 2).getText().equals("") ||
                Widgets.getComponent(228, 2).getAbsLocation().getX() == 0)
                table.interact("Remove");
            for (int i = 0; i <= 5 && Widgets.getComponent(228, 2) == null; i++)
                Task.sleep(100);
            if (Widgets.getComponent(228, 2) != null) {
                Widgets.getComponent(228, 2).click();
                Mouse.move(table.getModel().getNextPoint());
                for (int i = 0; i <= 5 && Objects.getNearest(GameConstants.MAHOGANY_TABLE) != null; i++)
                    Task.sleep(100);
            }
        }
    }

    public boolean isValid() {
        return (Objects.getNearest(GameConstants.MAHOGANY_TABLE) != null || Objects.getNearest(13351) != null);
    }

    public String getStatus() {
        return "Removing item";
    }
}
