package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Npc;
import com.rsbuddy.script.wrappers.Tile;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;
import nz.artezombies.misc.GameConstants;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/30/11
 * Time: 7:15 AM
 * Package: nz.artezombies.strategies;
 */
public class ReturnToArea implements Strategy
{
    public void execute() {
        if (Utils.getWidgetWithText("yes.") != null && Utils.getWidgetWithText("yes.").isValid()) {
            Tile curPos = UberPlayer.location();
            Utils.getWidgetWithText("yes.").click();
            for(int i = 0;i <= 40 && UberPlayer.location().equals(curPos);i++)
                Task.sleep(100);
        }
        if (Widgets.canContinue()) {
            Widgets.clickContinue();
            Utils.sleep(500, 600);
        }
        else {
            Npc hartwin = Npcs.getNearest(GameConstants.HARTWINID);
            if (hartwin != null) {
                if (hartwin.interact("Talk-to")) {
                    for (int i = 0; i < 40 && !Widgets.canContinue(); i++)
                        Utils.sleep(100);
                }
            }
        }
    }

    public boolean isValid() {
        return Npcs.getNearest(GameConstants.HARTWINID) != null;
    }

    public String getStatus() {
        return "Returning to Armoured Zombie area";
    }
}
