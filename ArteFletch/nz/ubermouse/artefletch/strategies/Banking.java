package nz.ubermouse.artefletch.strategies;

import com.rsbuddy.script.task.Task;
import nz.ubermouse.artefletch.data.Shared;
import nz.uberutils.helpers.Strategy;
import org.rsbuddy.tabs.Inventory;
import org.rsbuddy.widgets.Bank;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 6:04 PM
 * Package: nz.ubermouse.artefletch.strategies;
 */
public class Banking implements Strategy
{
    public void execute() {
        Shared.fletching = false;
        if (Shared.currentTask.isDone()) {
            Shared.tasks.remove(0);
            Shared.currentTask = Shared.tasks.get(0);
        }
        if (!Shared.currentTask.materialsInInventory()) {
            if (!Bank.isOpen()) {
                if (Bank.open()) {
                    for (int i = 0; i <= 15 && !Bank.isOpen(); i++)
                        Task.sleep(100);
                }
            }
            if (Inventory.getCount() > 0) {
                if (Bank.depositAll())
                    for (int i = 0; i <= 15 && Inventory.getCount() > 0; i++)
                        Task.sleep(100);
                if (Inventory.getCount() > 0)
                    return;
            }
            if (Shared.currentTask.withdrawMaterials())
                Bank.close();
        }
        else
            Bank.close();
    }

    public boolean isValid() {
        return !Shared.currentTask.materialsInInventory() || Bank.isOpen();
    }

    public String getStatus() {
        return "Banking";
    }
}
