package nz.artezombies.stratagies;

import com.rsbuddy.script.wrappers.Item;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberInventory;
import org.rsbuddy.tabs.Summoning;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/28/11
 * Time: 8:07 PM
 * Package: nz.artezombies.strategies;
 */
public class DrinkPots implements Strategy
{
    private boolean summoning;

    public void execute() {
        if (UberPlayer.shouldPot())
            UberPlayer.drinkPotions();
        if (summoning && Summoning.getTimeLeft() < 2.6 && Summoning.getPoints() <= 10) {
            Item sum = UberInventory.getItem("summoning potion");
            Item res = UberInventory.getItem("super restore potion");
            Item pot = (sum != null) ? sum : res;
            if (pot != null)
                pot.click(true);
            summoning = false;
        }
    }

    public boolean isValid() {
        if(!UberPlayer.inCombat())
            return false;
        if (UberPlayer.shouldPot())
            return true;
        if (Options.getBoolean("useSummoning") &&
            (UberInventory.contains("summoning potion") || UberInventory.contains("super restore potion")) &&
            (Summoning.getTimeLeft() < 2.6 || !Summoning.isFamiliarSummoned()) &&
            Summoning.getPoints() <= 10) {
            summoning = true;
            return true;
        }
        return false;
    }

    public String getStatus() {
        return "Drinking potions";
    }
}
