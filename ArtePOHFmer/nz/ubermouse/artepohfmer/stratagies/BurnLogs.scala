package nz.ubermouse.artepohfmer.stratagies

import nz.uberutils.helpers.Strategy
import org.rsbuddy.tabs.Inventory
import nz.ubermouse.artepohfmer.Firemaking
import nz.ubermouse.artepohfmer.data.Constants
import com.rsbuddy.script.methods.Npcs

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/25/11
 * Time: 1:29 PM
 * Package: nz.ubermouse.artepohfmer.stratagies; */
class BurnLogs extends Strategy
{
  def execute() {
    Firemaking.burnLogs(Inventory.getCount(Firemaking.logId))
  }

  def getStatus = "Burning logs"

  def isValid = Inventory.getCount(Firemaking.logId) > 0 &&
                !(Npcs.getNearest(Constants.DEMON_BUTLER_ID) != null &&
                  Inventory.getCount(Firemaking.logId) <= Constants.CERT_AT_LOG_COUNT);
}