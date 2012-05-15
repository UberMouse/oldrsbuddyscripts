package nz.artefalconry.stratagies;

import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Npc;
import nz.artefalconry.ArteFalconry;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 12:01 PM
 * Package: nz.artefalconry.strategies;
 */
public class TakeFalcon implements Strategy
{
    public void execute() {
        Npc falcon = ArteFalconry.getFalcon();
        UberMovement.turnTo(falcon);
        if (falcon.interact("Retrieve")) {
            ArteFalconry.kebbitsCaught++;
            int timeout = 0;
            while (ArteFalconry.getFalcon() != null && ++timeout < 25)
                Task.sleep(100);
        }
    }


    public boolean isValid() {
        return (ArteFalconry.getFalcon() != null);
    }

    public String getStatus() {
        return "Picking up falcon";
    }
}
