package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.wrappers.Path;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.utils.Debug;

/**
* Created by IntelliJ IDEA.
* User: Taylor
* Date: 5/23/11
* Time: 2:46 PM
* Package: nz.ubermouse.artealtar.strategies;
*/
public class walkBankFromObelisk extends Debug implements ArteAltar.Strategy
{


    public void execute() {
        Path path = Walking.findPath(Shared.bankTile);
        path.traverse();
    }


    public boolean isValid() {
        return Objects.getNearest(GameConstants.OBELISK_IDS) != null;
    }

}
