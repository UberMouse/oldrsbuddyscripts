package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.methods.Objects;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.utils.Banking;
import nz.ubermouse.artealtar.utils.Debug;
import org.rsbuddy.tabs.Inventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:47 PM
 * Package: nz.ubermouse.artealtar.strategies;
 */
public class Transport extends Debug implements ArteAltar.Strategy
{
    protected String transportType;
    protected String transportMethod;

    public boolean isValid() {
        if (transportType.equals("HOUSE") && (!transportMethod.equals(Shared.transportStrategyHouse)))
            return false;
        else if (transportType.equals("Bank") && (!transportMethod.equals(Shared.transportStrategyBank)))
            return false;
        if (transportType.equals("Bank") && Objects.getNearest(GameConstants.PORTAL_ENTRACE_ID) != null)
            return (Inventory.getCount(Shared.boneid) == 0);
        else if (transportType.equals("HOUSE"))
            return (Inventory.getCount(Shared.boneid) != 0 && Banking.nearBank());
        return false;
    }

    public void execute() {

    }
}
