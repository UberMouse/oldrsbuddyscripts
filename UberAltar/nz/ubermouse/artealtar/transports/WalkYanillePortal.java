package nz.ubermouse.artealtar.transports;

import com.rsbuddy.script.methods.Inventory;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.wrappers.TilePath;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.strategies.Transport;

/**
* Created by IntelliJ IDEA.
* User: Taylor
* Date: 5/23/11
* Time: 2:50 PM
* Package: nz.ubermouse.artealtar.transports;
*/
public class WalkYanillePortal extends Transport
{
    final TilePath portalPath;

    public WalkYanillePortal() {
        transportType = "HOUSE";
        transportMethod = "YANILLE_WALK_HOUSE";
        portalPath = Walking.newTilePath(GameConstants.YANILLE_BANK_HOUSE);
    }

    public void execute() {
        portalPath.randomize(1, 1);
        portalPath.traverse();
    }

    public boolean isValid() {
        return Inventory.isFull();
    }
}
