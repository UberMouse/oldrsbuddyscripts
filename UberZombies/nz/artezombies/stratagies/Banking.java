package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Npcs;
import nz.artezombies.misc.GameConstants;
import nz.artezombies.misc.Shared;
import nz.artezombies.misc.Utils;
import nz.uberutils.helpers.SpecialAttack;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.methods.UberBanking;
import nz.uberutils.methods.UberInventory;
import nz.uberutils.methods.UberMovement;
import nz.uberutils.wrappers.BankItem;
import org.rsbuddy.widgets.Bank;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/29/11
 * Time: 12:42 PM
 * Package: nz.artezombies.strategies;
 */
public class Banking implements Strategy
{
    public void execute() {
        if (UberInventory.contains(SpecialAttack.getPrimaryWeapon()))
            UberInventory.getItem(SpecialAttack.getPrimaryWeapon()).click(true);
        if (UberInventory.contains(SpecialAttack.getOffHand()))
            UberInventory.getItem(SpecialAttack.getOffHand()).click(true);
        UberMovement.turnTo(Npcs.getNearest(Bank.BANKERS));
        UberBanking.doBanking(Shared.instance().bankItems.toArray(new BankItem[Shared.instance().bankItems.size()]));
    }

    public boolean isValid() {
        return Utils.needsToBank() &&
               GameConstants.BANK_AREA.contains(UberPlayer.location()) &&
               Npcs.getNearest(Bank.BANKERS) != null || Bank.isOpen();
    }

    public String getStatus() {
        return "Banking";
    }
}
