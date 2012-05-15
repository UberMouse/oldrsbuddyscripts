package nz.ubermouse.artealtar.utils;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Players;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.uberutils.methods.UberBanking;
import org.rsbuddy.widgets.Bank;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:38 PM
 * Package: nz.ubermouse.artealtar.utils;
 */
public class Banking extends UberBanking
{
    public static boolean nearBank() {
        if (Npcs.getNearest(GameConstants.BANKER_IDS) != null)
            return Calculations.distanceBetween(Players.getLocal().getLocation(),
                                                Npcs.getNearest(GameConstants.BANKER_IDS).getLocation()) < 8;
        return Objects.getNearest(GameConstants.BANKER_IDS) != null &&
               Calculations.distanceBetween(Players.getLocal().getLocation(),
                                            Objects.getNearest(GameConstants.BANKER_IDS).getLocation()) < 8;
    }

    public static boolean bankOnScreen() {
        return Npcs.getNearest(GameConstants.BANKER_IDS) != null &&
               Npcs.getNearest(GameConstants.BANKER_IDS).isOnScreen() ||
               Objects.getNearest(GameConstants.BANKER_IDS) != null &&
               Objects.getNearest(GameConstants.BANKER_IDS).isOnScreen();
    }

    public static boolean open() {
        return Bank.open();
    }
}
