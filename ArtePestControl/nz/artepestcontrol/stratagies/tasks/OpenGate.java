package nz.artepestcontrol.stratagies.tasks;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.GameObject;
import nz.artepestcontrol.ArtePestControl;
import nz.artepestcontrol.game.Portal;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 4:27 PM
 * Package: nz.artepestcontrol.strategies.tasks;
 */
public class OpenGate extends LoopTask
{
    @Override
    public int loop() {
        if (Utils.inGame()) {
            final Portal portal = Utils.getPortal();
            if (portal != null) {
                if (!portal.isGateOpen() &&
                    Calculations.distanceTo(portal.getLocation()) >= 15 &&
                    Calculations.distanceTo(portal.getGateLocation()) <= 15) {
                    final GameObject gate = Objects.getTopAt(portal.getGateLocation());
                    ArtePestControl.lock();
                    if(gate.interact("Open")) {
                        Logger.instance().trace("Opening gate at {}", portal.getGateLocation());
                        for(int i = 0;i <= 15 && Objects.getTopAt(portal.getGateLocation()) != null || Objects.getTopAt(portal.getGateLocation()).getId() == gate.getId();i++)
                            Task.sleep(100);
                    }
                }
                else
                    ArtePestControl.unLock();
            }
            else
                ArtePestControl.unLock();
        }
        else
            ArtePestControl.unLock();
        return Utils.random(400, 500);
    }
}
