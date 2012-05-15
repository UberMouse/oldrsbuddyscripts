package nz.ubermouse.artepohfmer.stratagies

import com.rsbuddy.script.task.Task
import com.rsbuddy.script.methods.{Widgets, Npcs}
import nz.ubermouse.artepohfmer.data.{Constants, Static}
import nz.uberutils.helpers.{Utils, Strategy}
import nz.ubermouse.uberutils.helpers.Wait

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/25/11
 * Time: 10:49 PM
 * Package: nz.ubermouse.artepohfmer.stratagies; */
class PayButler extends Strategy
{
  def getStatus = "Paying butler"

  def isValid = Static.needToPayButler

  def execute() {
    val butler = Npcs.getNearest(Constants.DEMON_BUTLER_ID)
    if (butler != null) {
      butler.interact("Talk")
      Wait.For(Utils.getWidgetWithText("the 10000 coins") != null) {
                                                                     if (Utils.getWidgetWithText("at thy command") !=
                                                                         null)
                                                                       Static.needToPayButler = false
                                                                   }
      if (Widgets.canContinue)
        Widgets.clickContinue
      if (Wait.For(Utils.getWidgetWithText("from your bank") != null, 20) {}) {
        try {
          Utils.getWidgetWithText("from your bank").click
        } catch {
          case e: NullPointerException => Static.needToPayButler = false
        }
        Task.sleep(400, 500)
        Static.needToPayButler = false
        Static.butlerPaid += 1
      }
      else
        Static.needToPayButler = false
    }
  }
}