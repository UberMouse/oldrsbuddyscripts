package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Players;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.utils.Debug;
import nz.ubermouse.artealtar.utils.UberMovement;
import org.rsbuddy.tabs.Inventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:49 PM
 * Package: nz.ubermouse.artealtar.strategies;
 */
public class LightIncense extends Debug implements ArteAltar.Strategy
{

    public void execute() {
        GameObject burner = Objects.getNearest(GameConstants.BURNERS);
        UberMovement.turnTo(burner);
        debug("Lighting incense burner");
        if (burner.interact("Light")) {
            int timeout = 0;
            while (!(Players.getLocal().getAnimation() == GameConstants.BURNER_LIGHT_ANIM) && ++timeout <= 15)
                Task.sleep(100);
        }
        debug("Finished");
    }


    public boolean isValid() {
        GameObject burner = Objects.getNearest(GameConstants.BURNERS);
        return burner != null &&
               burner.isOnScreen() &&
               Inventory.getCount(GameConstants.MARENTILL_ID) > 0 &&
               Calculations.distanceTo(Objects.getNearest(GameConstants.PORTAL_ENTRACE_ID)) > 6;
    }

}
