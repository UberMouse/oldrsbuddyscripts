package nz.ubermouse.arteconstruction;

import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.Skills;
import nz.ubermouse.arteconstruction.data.Shared;
import nz.ubermouse.arteconstruction.strategies.GetPlanks;
import nz.ubermouse.arteconstruction.strategies.MakeItem;
import nz.ubermouse.arteconstruction.strategies.PayButler;
import nz.ubermouse.arteconstruction.strategies.RemoveItem;
import nz.uberutils.helpers.Skill;
import nz.uberutils.helpers.UberScript;
import nz.uberutils.helpers.Utils;
import nz.uberutils.paint.PaintController;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/5/11
 * Time: 7:25 PM
 * Package: nz.ubermouse.arteconstruction;
 */
@Manifest(name = "ArteConstruction",
          authors = "UberMouse",
          description = "AIO Constructor, get that Skillcape!",
          keywords = "Fletching",
          version = 0.1,
          website = "http://artebots.com")
public class ArteConstruction extends UberScript
{
    public boolean onStart() {
        infoColumnValues = new String[]{"Time:", "Status:", "Items made (p/h):", "Planks used (p/h):"};
        skills.add(new Skill(Skills.CONSTRUCTION));
        strategies.add(new PayButler());
        strategies.add(new GetPlanks());
        strategies.add(new RemoveItem());
        strategies.add(new MakeItem());
        return super.onStart();
    }

    public void miscLoop() {
        infoColumnData = new String[]{PaintController.timeRunning(),
                                        status,
                                        "" +
                                        Shared.itemsMade +
                                        " (" +
                                        Utils.calcPH(Shared.itemsMade, PaintController.runTime()) +
                                        ")",
                                        "" +
                                        Shared.planksUsed +
                                        " (" +
                                        Utils.calcPH(Shared.planksUsed, PaintController.runTime()) +
                                        ")"};
        if (Utils.getWidgetWithText("render unto me") != null)
            Shared.payButler = true;
        if (Utils.getWidgetWithText("Misc...") != null)
            Shared.payButler = false;
        if (Utils.getWidgetWithText("thy inventory is full") != null)
            Shared.butlerHasExcess = true;
    }
}
