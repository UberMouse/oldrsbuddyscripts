package nz.artemonkfish.stratagies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Inventory;
import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.wrappers.Npc;
import nz.artemonkfish.ArteMonkFish;
import nz.artemonkfish.misc.GameConstants;
import nz.artemonkfish.misc.Strategy;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/11/11
 * Time: 5:01 PM
 * Package: nz.artemonkfish.strategies;
 */
public class Fishing extends Strategy
{
    public Fishing(ArteMonkFish parent) {
        super(parent);
    }

    @Override
    public void execute() {
        if(UberPlayer.get().getAnimation() == 621)
            return;
        Npc spot = Npcs.getNearest(GameConstants.SPOT_ID);
        if(spot != null) {
            UberMovement.turnTo(spot);
            int timeout = 0;
            while(UberPlayer.isMoving() && Calculations.distanceTo(spot) > 4 && ++timeout <= 15)
                sleep(100);
            if(spot.interact("net")) {
                timeout = 0;
                while(UberPlayer.get().getAnimation() == -1 && ++timeout <= 15)
                    sleep(100);
            }
        }
    }

    @Override
    public boolean isValid() {
        return !Inventory.isFull() && Npcs.getNearest(GameConstants.SPOT_ID) != null;
    }

    @Override
    public String getStatus() {
        return "Fishing";
    }
}
