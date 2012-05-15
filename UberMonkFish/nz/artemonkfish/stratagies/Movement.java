package nz.artemonkfish.stratagies;

import com.rsbuddy.script.methods.Inventory;
import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Walking;
import nz.artemonkfish.ArteMonkFish;
import nz.artemonkfish.misc.GameConstants;
import nz.artemonkfish.misc.Strategy;
import nz.uberutils.helpers.UberPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/11/11
 * Time: 5:12 PM
 * Package: nz.artemonkfish.strategies;
 */
public class Movement extends Strategy
{
    public Movement(ArteMonkFish parent) {
        super(parent);
    }

    @Override
    public void execute() {
        if (UberPlayer.isMoving())
            return;
        if (Inventory.isFull())
            Walking.findPath(GameConstants.BANK_TILE.randomize(1, 2)).traverse();
        else
            Walking.findPath(GameConstants.FISH_TILE.randomize(1, 2)).traverse();
    }

    @Override
    public boolean isValid() {
        return (!Inventory.isFull() && Npcs.getNearest(GameConstants.SPOT_ID) == null) ||
               (Inventory.isFull() && Npcs.getNearest(GameConstants.ARNOLD) == null);
    }

    @Override
    public String getStatus() {
        return "Walking";
    }
}
