package nz.artepestcontrol.stratagies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.wrappers.Npc;
import com.rsbuddy.script.wrappers.Tile;
import nz.artepestcontrol.game.PestControl;
import nz.artepestcontrol.misc.GameConstants;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;
import nz.uberutils.methods.UberObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/9/11
 * Time: 8:33 PM
 * Package: nz.artepestcontrol.strategies;
 */
public class WalkToKnight implements Strategy
{
    public void execute() {
        Npc knight = Npcs.getNearest(GameConstants.VOID_KNIGHT);
        if (knight != null) {
            PestControl.knightLoc = knight.getLocation();
            if (!Calculations.isTileOnMap(knight.getLocation())) {
                Walking.findPath(knight.getLocation()).traverse();
                Utils.waitUntilMoving(5);
                Utils.waitUntilStopped(15);
            }
            else {
                UberMovement.turnTo(knight);
            }
        }
        else if (UberObjects.getTopAt(UberPlayer.location(), GameConstants.LANDER_FLOOR) != null &&
                 Utils.arrayContains(GameConstants.LANDER_FLOOR,
                                     UberObjects.getTopAt(UberPlayer.location(), GameConstants.LANDER_FLOOR).getId())) {
            new Tile(UberPlayer.location().getX(), UberPlayer.location().getY() - 17).randomize(2, 2).clickOnMap();
            PestControl.onGameStart();
            Utils.waitUntilMoving(5);
            Utils.waitUntilStopped(15);
        }
        else if (PestControl.getKnightArea() != null) {
            Walking.findPath(PestControl.knightLoc.randomize(4, 4)).traverse();
        }
    }

    public boolean isValid() {
        return Utils.inGame() &&
               ((PestControl.getKnightArea() != null &&
                 !PestControl.getKnightArea().contains(UberPlayer.location()) &&
                 !Options.getBoolean("attackPortals")) ||
                Objects.getTopAt(UberPlayer.location()) != null &&
                Utils.arrayContains(GameConstants.LANDER_FLOOR, Objects.getTopAt(UberPlayer.location()).getId()) &&
                !UberPlayer.inCombat());
    }

    public String getStatus() {
        return "Walking to knight";
    }
}
