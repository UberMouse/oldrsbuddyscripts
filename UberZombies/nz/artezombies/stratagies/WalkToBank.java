package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Item;
import com.rsbuddy.script.wrappers.Tile;
import nz.artezombies.misc.GameConstants;
import nz.artezombies.misc.Utils;
import nz.uberutils.helpers.SpecialAttack;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.methods.UberInventory;
import org.rsbuddy.tabs.Prayer;
import org.rsbuddy.tabs.Summoning;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/20/11
 * Time: 5:47 PM
 * Package: nz.artezombies.strategies;
 */
public class WalkToBank implements Strategy
{
    public void execute() {
        Item teletab = UberInventory.getItem("Varrock tel");
        if (teletab != null) {
            teletab.click(true);
            Tile curPos = UberPlayer.location();
            for (int i = 0; i <= 15 && curPos.equals(UberPlayer.location()); i++)
                Task.sleep(100);
        }
        if (Prayer.isQuickPrayersActive())
            Prayer.toggleQuickPrayers(false);
        if (UberInventory.contains(SpecialAttack.getPrimaryWeapon()))
            UberInventory.getItem(SpecialAttack.getPrimaryWeapon()).click(true);
        if (UberInventory.contains(SpecialAttack.getOffHand()))
            UberInventory.getItem(SpecialAttack.getOffHand()).click(true);
        if (Summoning.isFamiliarSummoned())
            Summoning.dismiss();
        if (Game.getFloorLevel() == 0)
            if (!GameConstants.BANK_PATH.traverse()) {
                if (!GameConstants.GE_BANK_PATH.traverse())
                    Walking.findPath(GameConstants.BANK_PATH.getEnd()).traverse();
            }
    }

    public boolean isValid() {
        return Utils.needsToBank();
    }

    public String getStatus() {
        return "Walking to bank";
    }
}
