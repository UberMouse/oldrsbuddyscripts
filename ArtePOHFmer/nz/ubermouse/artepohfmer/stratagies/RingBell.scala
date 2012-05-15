package nz.ubermouse.artepohfmer.stratagies

import nz.ubermouse.artepohfmer.data.{Constants, Static}
import nz.uberutils.methods.UberMovement
import com.rsbuddy.script.methods.{Calculations, Objects}
import nz.uberutils.helpers.{Utils, UberPlayer, Strategy}
import nz.ubermouse.uberutils.helpers.Wait

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/25/11
 * Time: 10:35 PM
 * Package: nz.ubermouse.artepohfmer.stratagies; */
class RingBell extends Strategy
{
  def getStatus = "Ringing bell to get butlers ass over here"

  def isValid = !Static.rungBell && Objects.getNearest(Constants.BELL_PULLS: _*) != null

  def execute() {
    val pull = Objects.getNearest(Constants.BELL_PULLS: _*);
    UberMovement.turnTo(pull)
    pull.interact("Ring")
    if (Wait.While(!UberPlayer.isMoving) {}) {
      if (Calculations.distanceTo(pull) <= 2)
        if (Wait.For(Utils.getWidgetWithText("What is thy") != null) {})
          Static.rungBell = true
    }
  }
}