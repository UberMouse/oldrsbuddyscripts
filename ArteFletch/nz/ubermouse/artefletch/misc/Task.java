package nz.ubermouse.artefletch.misc;

import nz.ubermouse.artefletch.ArteFletch;
import nz.ubermouse.artefletch.data.GameConstants;
import nz.ubermouse.artefletch.data.Shared;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberBanking;
import org.rsbuddy.tabs.Inventory;
import org.rsbuddy.widgets.Bank;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 5:58 PM
 * Package: nz.ubermouse.artefletch;
 */
public class Task
{
    private final TASK_TYPE type;
    private final int       id;
    private final int       amount;
    private int doneAmount = 0;

    public Task(int id, int amount, TASK_TYPE type) {
        this.amount = amount;
        this.id = id;
        this.type = type;
    }

    public boolean isDone() {
        return doneAmount >= amount;
    }

    public void iDoneAmount() {
        doneAmount++;
        Shared.actionsDone++;
    }

    public int getId() {
        return id;
    }

    public TASK_TYPE getType() {
        return type;
    }

    public boolean materialsInInventory() {
        switch (type) {
            case STRING:
                return Inventory.getCount(id) > 0 && Inventory.getCount(GameConstants.BOW_STRING) > 0;
            case ARROWS:
                if (!(Inventory.getCount(id) > 0 && Inventory.getCount(GameConstants.ARROW_SHAFT) > 0)) {
                    Logger.getLogger("ArteArrowFletch")
                          .warning("Remember to start script with all the needed materials for the arrow you're " +
                                   "making!");
                }
        }
        return false;
    }

    public boolean withdrawMaterials() {
        switch (type) {
            case STRING:
                if (Bank.isOpen() && (Bank.getCount(id) <= 0 || Bank.getCount(GameConstants.BOW_STRING) <= 0)) {
                    Logger.getLogger("ArteFletch").warning("Out of materials");
                    ArteFletch.stop = true;
                }
                int i;
                if (UberBanking.makeInventoryCount(id, 14)) {
                    for (i = 0; i <= 15 && Inventory.getCount(id) != 14; i++)
                        Utils.sleep(100);
                    if (i >= 15)
                        return false;
                }
                else
                    return false;
                if (UberBanking.makeInventoryCount(GameConstants.BOW_STRING, 14)) {
                    for (i = 0; i <= 15 && Inventory.getCount(id) != 14; i++)
                        Utils.sleep(100);
                    if (i < 15)
                        return true;
                }
                else
                    return false;
            case ARROWS:
                return true;
        }
        return false;
    }

    public String getStatus() {
        switch (type) {
            case STRING:
                return "Stringing bows";
            case ARROWS:
                return "Making arrows";
        }
        return "";
    }

    public static enum TASK_TYPE
    {
        STRING_FLETCH, STRING, FLETCH, BOLTS, ARROWS, HEADS, SHAFTS
    }
}
