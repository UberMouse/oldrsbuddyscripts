package nz.ubermouse.artepohfmer.stratagies

import com.rsbuddy.script.methods.Objects
import nz.uberutils.methods.UberMovement
import nz.uberutils.helpers.{Utils, Strategy}
import nz.ubermouse.artepohfmer.data.{Static, Constants}
import nz.ubermouse.uberutils.helpers.Wait

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/25/11
 * Time: 4:10 PM
 * Package: nz.ubermouse.artepohfmer.stratagies; */
class EnterHouse extends Strategy
{
  def getStatus = "Entering house"

  def isValid = Objects.getNearest(Constants.OUTSIDE_PORTAL) != null

  def execute() {
    Static.rungBell = false;
    val portal = Objects.getNearest(Constants.OUTSIDE_PORTAL)
    UberMovement.turnTo(portal)
    portal.interact("Enter")
    Wait.For(Utils.getWidgetWithText("Go to your house") != null) {}
    if (Utils.getWidgetWithText("Go to your house") != null)
      Utils.getWidgetWithText("Go to your house (b").click();
    Wait.While(Objects.getNearest(Constants.OUTSIDE_PORTAL) != null) {}
  }
}