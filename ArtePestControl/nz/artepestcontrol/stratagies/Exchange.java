package nz.artepestcontrol.stratagies;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Npc;
import nz.artepestcontrol.ArtePestControl;
import nz.artepestcontrol.game.Job;
import nz.artepestcontrol.game.PestControl;
import nz.artepestcontrol.misc.GameConstants;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/13/11
 * Time: 4:10 PM
 * Package: nz.artepestcontrol.strategies;
 */
public class Exchange implements Strategy
{
    public void execute() {
        if (!Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, 27).isValid()) {
            Npc exchange = Npcs.getNearest(GameConstants.EXCHANGE_NPC);
            if (exchange != null) {
                UberMovement.turnTo(exchange);
                if (exchange.interact("exchange"))
                    Utils.sleepUntilValid(GameConstants.EXCHANGE_MAIN_IFACE, 27);
            }
            else
                Walking.findPath(GameConstants.COMMENDATION_EXCHANGE).traverse();
        }
        else {
            int commendations = Integer.parseInt(Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE,
                    GameConstants.COMMENDATIONS).getText());
            Job job = PestControl.jobs.get(0);
            if (PestControl.commendations != commendations) {
                PestControl.commendations = commendations;
                if (PestControl.commendations < job.points())
                    Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, GameConstants.CLOSE).click();


            }
            if (job.tab() == 2) {
                if (Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, GameConstants.TAB_TWO).click())
                    for (int i = 0; i <= 15 &&
                                    !Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, job.subID())
                                            .isVisible(); i++)
                        Task.sleep(100);
            }
            if (Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, job.subID()).click()) {
                Utils.sleepUntilValid(GameConstants.EXCHANGE_MAIN_IFACE, GameConstants.CONFIRM);
                for (int i = 0; i <= 15 &&
                                !Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, GameConstants.CONFIRM)
                                        .isVisible(); i++)
                    Task.sleep(100);
            }
            Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, GameConstants.CONFIRM).click();
            for (int i = 0; i <= 15 &&
                            Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, GameConstants.CONFIRM)
                                   .isVisible(); i++)
                Task.sleep(100);
            if (commendations >
                Integer.parseInt(Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, GameConstants.COMMENDATIONS)
                                        .getText())) {
                ArtePestControl.gui.delJob(PestControl.jobs.indexOf(job));
                PestControl.jobs.remove(job);
                PestControl.commendations -= job.points();
                Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, GameConstants.CLOSE).click();
                for (int i = 0; i < 15 && Widgets.getComponent(GameConstants.EXCHANGE_MAIN_IFACE, 0).isValid(); i++)
                    Utils.sleep(100);
            }
        }
    }

    public boolean isValid() {
        return PestControl.jobs.size() > 0 &&
               PestControl.commendations >= PestControl.jobs.get(0).points() &&
               !Utils.inLander() &&
               !Utils.inGame();
    }

    public String getStatus() {
        return "Exchanging points for: " + PestControl.jobs.get(0).getName();
    }
}
