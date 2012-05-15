package nz.artefalconry.stratagies;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Npc;
import nz.artefalconry.ArteFalconry;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 11:41 AM
 * Package: nz.artefalconry.strategies;
 */
public class CatchKebbit implements Strategy
{
    public void execute() {
        Npc kebbit = Npcs.getNearest(ArteFalconry.kebbitCatchIDs);
        UberMovement.turnTo(kebbit);
        if (kebbit != null && kebbit.interact("Catch")) {
            int timeout = 0;
            while (ArteFalconry.getFalcon() == null && ++timeout < 20)
                Task.sleep(100);
        }
    }

    public boolean isValid() {
        return ArteFalconry.getFalcon() == null;
    }

    public String getStatus() {
        return "Catching kebbit";
    }
}
