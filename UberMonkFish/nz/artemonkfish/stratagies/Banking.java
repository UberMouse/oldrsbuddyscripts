package nz.artemonkfish.stratagies;

import com.rsbuddy.script.methods.*;
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
 * Time: 5:06 PM
 * Package: nz.artemonkfish.strategies;
 */
public class Banking extends Strategy
{
    public Banking(ArteMonkFish parent) {
        super(parent);
    }

    @Override
    public void execute() {
        Npc arnold = Npcs.getNearest(GameConstants.ARNOLD);
        if (arnold != null) {
            if (!Calculations.isTileOnMap(arnold.getLocation()))
                Walking.findPath(arnold.getLocation().randomize(1, 2)).traverse();
            else
                UberMovement.turnTo(arnold);
            int timeout = 0;
            while (UberPlayer.isMoving() &&
                   (Calculations.distanceTo(arnold) > 5 || Calculations.distanceTo(Walking.getDestination()) > 5) &&
                   ++timeout <= 15)
                sleep(100);
            timeout = 0;
            if (Bank.open()) {
                while (!Bank.isOpen() && ++timeout <= 10)
                    sleep(100);
                if(Bank.isOpen()) {
                    Bank.depositAllExcept(GameConstants.NET_ID);
                }
            }
        }
    }

    @Override
    public boolean isValid() {
        return Inventory.isFull() && Npcs.getNearest(GameConstants.ARNOLD) != null;
    }

    @Override
    public String getStatus() {
        return "Banking";
    }
}
