package nz.ubermouse.artefletch.strategies;

import nz.ubermouse.artefletch.data.Shared;
import nz.ubermouse.artefletch.strategies.fletchstrategies.MakeArrows;
import nz.ubermouse.artefletch.strategies.fletchstrategies.StringBows;
import nz.uberutils.helpers.Strategy;
import org.rsbuddy.widgets.Bank;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 6:04 PM
 * Package: nz.ubermouse.artefletch.strategies;
 */
public class Fletch implements Strategy
{
    public void execute() {
        if (!Shared.fletching) {
            switch (Shared.currentTask.getType()) {
                case STRING:
                    StringBows.execute(Shared.currentTask.getId());
                    break;
                case ARROWS:
                    MakeArrows.execute(Shared.currentTask.getId());
                    break;
            }
        }
        if(Bank.isOpen())
            Bank.close();
    }

    public boolean isValid() {
        return Shared.currentTask.materialsInInventory();
    }

    public String getStatus() {
        return Shared.currentTask.getStatus();
    }
}
