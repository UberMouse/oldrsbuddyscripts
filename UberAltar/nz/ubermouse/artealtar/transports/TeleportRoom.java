package nz.ubermouse.artealtar.transports;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.artealtar.strategies.Transport;
import nz.ubermouse.artealtar.utils.UberMovement;
import nz.uberutils.helpers.Utils;

/**
* Created by IntelliJ IDEA.
* User: Taylor
* Date: 5/23/11
* Time: 2:49 PM
* Package: nz.ubermouse.artealtar.transports;
*/
public class TeleportRoom extends Transport
{
    final String teleportPortal;
    public TeleportRoom(String portal) {
        teleportPortal = portal;
        transportType = "Bank";
        transportMethod = "TELEPORT_ROOM";
    }

    public void execute() {
        GameObject portal = Objects.getNearest(teleportPortal);
        if (portal == null)
            return;
        if (!Calculations.isReachable(portal.getLocation(), true))
            return;
        UberMovement.turnTo(portal);
        if (portal.interact("Enter " + teleportPortal)) {
            int timeout = 0;
            while (Game.getClientState() != 11 && ++timeout < 20) {
                Task.sleep(100);
            }
            timeout = 0;
            while (Game.getClientState() == 11 && ++timeout < 20)
                Task.sleep(100);
            Task.sleep(Utils.random(1000, 1500));
        }
    }
}
