package nz.ubermouse.artealtar.utils;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Magic;
import com.rsbuddy.script.methods.Players;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;

/**
* Created by IntelliJ IDEA.
* User: Taylor
* Date: 5/23/11
* Time: 2:38 PM
* Package: nz.ubermouse.artealtar.utils;
*/
public class Failsafes extends Debug
{

    public static void waitUntilMoving(int timeout) {
        int breakout = 0;
        while (!Players.getLocal().isMoving() && ++breakout < (timeout * 3)) {
            Task.sleep(333);
        }
    }

    public static void waitUntilMoving() {
        waitUntilMoving(5);
    }

    public static boolean canCastHouseTele() {
        if (Game.getCurrentTab() != Game.TAB_MAGIC) {
            debug("Magic tab not open, opening");
            Game.openTab(Game.TAB_MAGIC);
        }
        return (Widgets.getComponent(Magic.Book.MODERN.getWidgetId(), Magic.SPELL_TELEPORT_TO_HOUSE)
                       .getTextureId() == 355);
    }
}
