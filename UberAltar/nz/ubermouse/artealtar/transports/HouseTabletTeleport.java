package nz.ubermouse.artealtar.transports;

import com.rsbuddy.script.methods.Players;
import com.rsbuddy.script.task.Task;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.strategies.Transport;
import org.rsbuddy.tabs.Inventory;

/**
* Created by IntelliJ IDEA.
* User: Taylor
* Date: 5/23/11
* Time: 2:50 PM
* Package: nz.ubermouse.artealtar.transports;
*/
public class HouseTabletTeleport extends Transport
{
    public HouseTabletTeleport() {
        transportType = "HOUSE";
        transportMethod = "HOUSE_TELEPORT_TABLET";
    }

    public void execute() {
        debug("Teleporting to house");
        if (!Inventory.contains(GameConstants.HOUSE_TABLET_ID)) {
            debug("Out of teleport tablets, stopping");
        }
        if (Inventory.getItem(GameConstants.HOUSE_TABLET_ID).interact("Break")) {
            int timeout = 0;
            while (Players.getLocal().getAnimation() == -1 && ++timeout < 15)
                Task.sleep(100);
        }
    }
}
