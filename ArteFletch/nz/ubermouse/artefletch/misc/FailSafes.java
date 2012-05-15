package nz.ubermouse.artefletch.misc;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.script.task.LoopTask;
import com.rsbuddy.script.util.Timer;
import nz.ubermouse.artefletch.data.GameConstants;
import nz.ubermouse.artefletch.data.Shared;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 6:07 PM
 * Package: nz.ubermouse.artefletch.threads;
 */
public class FailSafes extends LoopTask implements MessageListener
{
    private static       int   fails     = 0;
    private static final Timer ammoTimer = new Timer(2500);

    @Override
    public int loop() {
        try {
            switch (Shared.currentTask.getType()) {
                case STRING:
                    if (fails >= 15) {
                        Shared.fletching = false;
                        fails = 0;
                    }
                    if (UberPlayer.get().getAnimation() == -1 && Shared.fletching)
                        fails++;
                    else
                        fails = 0;
                    break;
                case ARROWS:
                    if (!ammoTimer.isRunning() && Shared.fletching) {
                        Shared.fletching = false;
                        Utils.debug("Derpity duur");
                        ammoTimer.reset();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 100;
    }

    public void messageReceived(MessageEvent messageEvent) {
        if (messageEvent.isAutomated()) {
            String txt = messageEvent.getMessage();
            if (Utils.arrayContains(GameConstants.ITEM_MADE_MESSAGES, txt)) {
                Utils.debug("Herp derp");
                ammoTimer.reset();
            }
        }
    }
}
