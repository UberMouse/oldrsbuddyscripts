package nz.ubermouse.artealtar.transports;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.strategies.Transport;
import nz.ubermouse.artealtar.utils.UberMovement;

/**
* Created by IntelliJ IDEA.
* User: Taylor
* Date: 5/23/11
* Time: 2:49 PM
* Package: nz.ubermouse.artealtar.transports;
*/
public class YanilleBankMethod extends Transport
{
    public YanilleBankMethod() {
        transportType = "Bank";
        transportMethod = "YANILLE_WALK_BANK";
    }

    public void execute() {
        GameObject portal = Objects.getNearest(GameConstants.PORTAL_ENTRACE_ID);
        GameObject altar = Objects.getNearest(GameConstants.ALTAR_ID);
        UberMovement.turnTo(portal);
        if (portal.interact("Enter")) {
            int timeout = 0;
            while (Game.getClientState() != 11 && ++timeout < 20)
                Task.sleep(100);
        }
    }
}
