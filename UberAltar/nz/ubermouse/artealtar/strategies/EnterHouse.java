package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.methods.Keyboard;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Component;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.utils.Debug;
import nz.ubermouse.artealtar.utils.UberMovement;
import nz.uberutils.helpers.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:46 PM
 * Package: nz.ubermouse.artealtar.strategies;
 */
public class EnterHouse extends Debug implements ArteAltar.Strategy
{
    boolean firstEnter = true;

    public void execute() {
        if (!Shared.useFriendsHouse)
            debug("Out side for some reason, re entering");
        GameObject portal = Objects.getNearest(GameConstants.PORTAL_ENTRANCEEXT_ID);
        UberMovement.turnTo(portal);
        if (portal.interact("Enter")) {
            int timeout;
            if (!Shared.useFriendsHouse) {
                Component enterInterface = Widgets.getComponent(232, 2);
                timeout = 0;
                while (!Widgets.getComponent(232, 2).isValid() && ++timeout <= 15)
                    Task.sleep(100);
                if (enterInterface.click())
                    debug("Succesfully entered house");
            }
            else {
                Component friendInterface = Widgets.getComponent(232, 4);
                timeout = 0;
                while (!Widgets.getComponent(232, 4).isValid() && ++timeout <= 15) {
                    debug("Waiting");
                    Task.sleep(100);
                }
                if (friendInterface.click()) {
                    if (firstEnter) {
                        timeout = 0;
                        while (!Widgets.getComponent(752, 4).getText().equals("Enter name") && ++timeout <= 15)
                            Task.sleep(100);
                        Task.sleep(Utils.random(500, 600));
                        Keyboard.sendText(Shared.friendName, true);
                        firstEnter = false;
                    }
                    else {
                        timeout = 0;
                        while (!Widgets.getComponent(752, 3).isValid() && ++timeout < 15)
                            Task.sleep(100);
                        Widgets.getComponent(752, 3).getComponent(0).click();
                    }
                }
            }
            timeout = 0;
            while (Objects.getNearest(GameConstants.PORTAL_ENTRACE_ID) == null && ++timeout < 50) {
                Task.sleep(100);
            }
        }
    }


    public boolean isValid() {
        GameObject portal = Objects.getNearest(GameConstants.PORTAL_ENTRANCEEXT_ID);
        return (portal != null && portal.isOnScreen());
    }
}
