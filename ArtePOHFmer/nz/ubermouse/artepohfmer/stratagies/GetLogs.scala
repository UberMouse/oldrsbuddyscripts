package nz.ubermouse.artepohfmer.stratagies

import org.rsbuddy.tabs.Inventory
import nz.uberutils.methods.UberMovement
import com.rsbuddy.script.task.Task
import nz.ubermouse.artepohfmer.data.{Static, Constants}
import nz.uberutils.helpers.{UberPlayer, Strategy}
import nz.ubermouse.artepohfmer.Firemaking
import nz.ubermouse.uberutils.helpers.Wait
import com.rsbuddy.script.methods._


/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/25/11
 * Time: 5:27 PM
 * Package: nz.ubermouse.artepohfmer.stratagies; */
class GetLogs extends Strategy
{
  def getStatus = "Un certing logs with butler"

  def isValid = Npcs.getNearest(Constants.DEMON_BUTLER_ID) != null &&
                Inventory.getCount(Firemaking.logId) <= Constants.CERT_AT_LOG_COUNT

  def execute() {
    if (Widgets.getComponent(230, 2) != null && Widgets.getComponent(230, 2).isValid) {
      Widgets.getComponent(230, 2).click()
      Task.sleep(1400, 1800)
      Keyboard.sendText("" + Constants.MAX_LOGS, true)
      val count = Inventory.getCount(Firemaking.logId)
      Wait.For(Inventory.getCount(Firemaking.logId) > count, 100) {
                                                                    if (Firemaking.pathLength(UberPlayer.location()) <
                                                                        Constants.MAX_LOGS &&
                                                                        (!UberPlayer.isMoving ||
                                                                         (Walking.getDestination == null ||
                                                                          Calculations
                                                                          .distanceTo(Walking.getDestination) <= 5))) {
                                                                      val tile = Firemaking
                                                                                 .getBestLoadedTile(Constants.MAX_LOGS)
                                                                      if (Walking.getDestination == null ||
                                                                          !Walking.getDestination.equals(tile)) {
                                                                        UberMovement.turnTo(tile)
                                                                        tile.interact("Walk")
                                                                      }
                                                                    }
                                                                  }
    }
    else {
      val logs = Inventory.getItem(Firemaking.logId + 1);
      logs.click(true);
      val butler = Npcs.getNearest(Constants.DEMON_BUTLER_ID);
      if (butler == null) {
        Static.rungBell = false
        return;
      }
      if (Calculations.distanceTo(butler) > 10)
        Static.rungBell = false
      UberMovement.turnTo(butler)
      butler.interact("use")
      Wait.For(Widgets.getComponent(230, 2) != null && Widgets.getComponent(230, 2).isValid, 20) {}
    }
  }
}