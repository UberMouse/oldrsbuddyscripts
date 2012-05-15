package nz.ubermouse.artefletch;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.Skills;
import nz.ubermouse.artefletch.data.GameConstants;
import nz.ubermouse.artefletch.data.Shared;
import nz.ubermouse.artefletch.misc.FailSafes;
import nz.ubermouse.artefletch.misc.Task;
import nz.ubermouse.artefletch.strategies.Banking;
import nz.ubermouse.artefletch.strategies.Fletch;
import nz.uberutils.helpers.Skill;
import nz.uberutils.helpers.UberScript;
import nz.uberutils.helpers.Utils;
import nz.uberutils.paint.PaintController;

@Manifest(name = "ArteFletch",
          authors = "UberMouse",
          description = "AIO Fletcher, get that Skillcape!",
          keywords = "Fletching",
          version = 0.1,
          website = "http://artebots.com")
public class ArteFletch extends UberScript implements MessageListener
{
    public static boolean stop;

    public boolean onStart() {
        infoColumnValues = new String[]{"Time:", "Status:", "Actions done (p/h):"};
        skills.add(new Skill(Skills.FLETCHING));
        strategies.add(new Fletch());
        strategies.add(new Banking());
        getContainer().submit(new FailSafes());
        Shared.tasks.add(new Task(GameConstants.UNFINISHED_BOWS[8], 999999, Task.TASK_TYPE.STRING));
        //        Shared.tasks.add(new Task(40, 1500, Task.TASK_TYPE.ARROWS));
        //        Shared.tasks.add(new Task(GameConstants.UNFINISHED_BOWS[8], 14, Task.TASK_TYPE.STRING));
        //        Shared.tasks.add(new Task(40, 15, Task.TASK_TYPE.ARROWS));
        //        Shared.tasks.add(new Task(GameConstants.UNFINISHED_BOWS[8], 14, Task.TASK_TYPE.STRING));
        //        Shared.tasks.add(new Task(40, 15, Task.TASK_TYPE.ARROWS));
        Shared.currentTask = Shared.tasks.get(0);
        return super.onStart();
    }

    public void miscLoop() {
        infoColumnData = new String[]{PaintController.timeRunning(),
                                      status,
                                      "" +
                                      Shared.actionsDone +
                                      " (" +
                                      Utils.calcPH(Shared.actionsDone, PaintController.runTime()) +
                                      ")"};
        if (stop)
            stop();
    }

    public void messageReceived(MessageEvent messageEvent) {
        if (messageEvent.isAutomated()) {
            String txt = messageEvent.getMessage();
            if (txt.contains("add a string to"))
                Shared.currentTask.iDoneAmount();
        }
    }
}