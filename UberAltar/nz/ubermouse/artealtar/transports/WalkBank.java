package nz.ubermouse.artealtar.transports;

import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.wrappers.Tile;
import com.rsbuddy.script.wrappers.TilePath;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.strategies.Transport;
import nz.ubermouse.artealtar.utils.Banking;
import nz.ubermouse.artealtar.utils.Failsafes;

/**
* Created by IntelliJ IDEA.
* User: Taylor
* Date: 5/23/11
* Time: 2:50 PM
* Package: nz.ubermouse.artealtar.transports;
*/
public class WalkBank extends Transport
{
    final Tile[]   bankPath;
    final Tile     bankTile;
    final TilePath path;
    private ArteAltar arteAltar;

    public WalkBank(ArteAltar arteAltar, Tile[] bankPath, Tile bankTile) {
        this.arteAltar = arteAltar;
        this.bankPath = bankPath;
        this.bankTile = bankTile;
        Shared.transportType = "Bank";
        Shared.transportMethod = "Bank";
        path = Walking.newTilePath(bankPath);
    }

    public void execute() {
        debug("Walking to Bank");
        if (Calculations.distanceBetween(Players.getLocal().getLocation(), bankTile) < 6) {
            if (!Calculations.isTileOnScreen(bankTile))
                Camera.turnTo(bankTile, arteAltar.random(10, 25));
            bankTile.interact("Walk");
            Failsafes.waitUntilMoving();
        }
        else {
            path.randomize(1, 1);
            path.traverse();
        }
    }

    public boolean isValid() {
        if (Objects.getNearest(GameConstants.PORTAL_ENTRACE_ID) == null && Inventory.getCount(Shared.boneid) == 0) {
            debug("Portal null");
            if (Banking.nearBank()) {
                debug("Near Bank true");
                if (!Banking.bankOnScreen()) {
                    debug("No Bank on screen");
                    return true;
                }
                else {
                    debug("Bank on screen");
                    return false;
                }
            }
            else {
                debug("Near Bank false");
                return true;
            }
        }
        return false;
    }
}
