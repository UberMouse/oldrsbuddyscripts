package nz.ubermouse.artealtar.transports;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.util.Timer;
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
public class MountedGlory extends Transport
{

    public MountedGlory() {
        transportType = "Bank";
        transportMethod = "GLORY";
    }

    public void execute() {
        GameObject glory = Objects.getNearest(GameConstants.MOUNTED_GLORY);
        if (glory == null)
            return;
        if (!glory.isOnScreen())
            UberMovement.turnTo(glory);
        else
            glory.getLocation().interact("Walk");
        if (glory.interact("Rub")) {
            debug("Rubbed");
            Timer time = new Timer(1500);
            while (!Widgets.getComponent(234, 2).isValid() && time.isRunning())
                Task.sleep(100);
            if (Widgets.getComponent(234, 2).isValid())
                Widgets.getComponent(234, 2).click();
            debug("Clicked");
            int timeout = 0;
            while (Game.getClientState() != 11 && ++timeout < 20) {
                debug("Sleeping until 11");
                Task.sleep(100);
            }
            timeout = 0;
            while (Game.getClientState() == 11 && ++timeout < 20) {
                debug("Sleep until not 11");
                Task.sleep(100);
            }
            debug("Slept");
        }
    }
}
