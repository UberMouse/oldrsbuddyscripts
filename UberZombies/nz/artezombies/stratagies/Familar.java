package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.wrappers.Item;
import nz.artezombies.misc.GameConstants;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberInventory;
import nz.uberutils.methods.UberSummoning;
import org.rsbuddy.tabs.Summoning;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/6/11
 * Time: 3:26 PM
 * Package: nz.artezombies.strategies;
 */
public class Familar implements Strategy
{
    public void execute() {
        if (Summoning.isFamiliarSummoned() && UberSummoning.getSummonedNpc() == null)
            Summoning.call();
        else {
            Item bunyip = UberInventory.getItem(GameConstants.BUNYIP_POUCH);
            Item spinner = UberInventory.getItem(GameConstants.VOID_SPINNER_POUCH);
            if (bunyip != null)
                bunyip.interact("summon");
            else if (spinner != null)
                spinner.interact("summon");
        }
    }

    public boolean isValid() {
        if (!Options.getBoolean("useSummoning") || Objects.getNearest(GameConstants.LADDERID) == null)
            return false;
        if (Summoning.isFamiliarSummoned() && UberSummoning.getSummonedNpc() == null)
            return true;
        return Options.getBoolean("useSummoning") &&
               (UberInventory.contains(GameConstants.BUNYIP_POUCH) ||
                UberInventory.contains(GameConstants.VOID_SPINNER_POUCH)) &&
               !Summoning.isFamiliarSummoned();
    }

    public String getStatus() {
        return "Summoning familiar";
    }
}
