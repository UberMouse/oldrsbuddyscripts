package nz.ubermouse.artealtar.transports;

import com.rsbuddy.script.methods.Players;
import com.rsbuddy.script.task.Task;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.strategies.Transport;
import org.rsbuddy.tabs.Equipment;

/**
* Created by IntelliJ IDEA.
* User: Taylor
* Date: 5/23/11
* Time: 2:50 PM
* Package: nz.ubermouse.artealtar.transports;
*/
public class KinshipRing extends Transport
{

    public KinshipRing() {
       transportType = "Bank";
       transportMethod = "KINSHIP_RING";
    }

    public void execute() {
        debug("Teleporting to daemonheim");
        Equipment.getItem(Equipment.RING).interact("Teleport To Daemonheim");
        int timeout = 0;
        while (Players.getLocal().getAnimation() == -1 && ++timeout < 20)
            Task.sleep(100);
    }
}
