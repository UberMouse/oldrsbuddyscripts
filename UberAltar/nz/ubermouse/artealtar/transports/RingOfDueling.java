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
public class RingOfDueling extends Transport
{

    public RingOfDueling() {
        transportType = "Bank";
        transportMethod = "ROD";
    }

    public void execute() {
        debug("Teleporting to castle wars");
        Equipment.getItem(Equipment.RING).interact("Castle Wars");
        int timeout = 0;
        while (Players.getLocal().getAnimation() == -1 && ++timeout < 20)
            Task.sleep(100);
    }
}
