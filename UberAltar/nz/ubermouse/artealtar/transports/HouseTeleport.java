package nz.ubermouse.artealtar.transports;

import com.rsbuddy.script.methods.Players;
import com.rsbuddy.script.task.Task;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.strategies.Transport;
import nz.ubermouse.artealtar.utils.Failsafes;
import org.rsbuddy.tabs.Magic;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:50 PM
 * Package: nz.ubermouse.artealtar.transports;
 */
public class HouseTeleport extends Transport
{
    public HouseTeleport(ArteAltar arteAltar) {
        transportType = "HOUSE";
        transportMethod = "HOUSETELE";
    }

    public void execute() {
        if (Failsafes.canCastHouseTele()) {
            debug("Casting house tele");
            Magic.castSpell(Magic.SPELL_TELEPORT_TO_HOUSE);
            int timeout = 0;
            while (Players.getLocal().getAnimation() == -1 && ++timeout < 20)
                Task.sleep(100);
        }
        else {
            debug("Out of runes");
        }

    }
}
