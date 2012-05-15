package nz.arteminer.stratagies.generic

import nz.arteminer.misc.Strategy
import com.rsbuddy.script.wrappers.GameObject
import nz.arteminer.methods.MyObjects
import nz.uberutils.helpers.{Utils, UberPlayer}
import nz.uberutils.methods.UberMovement
import nz.arteminer.helpers.Wait

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 2:26 AM
 * Package: nz.arteminer.strategies; */
class Mine(rockIds: Array[Int]) extends Strategy
{
  var lastRock: GameObject = null

  override def isValid: Boolean = {
    if (lastRock != null) {
      val rock: GameObject = MyObjects.getNearest(rockIds:_*);
      if (rock != null && !rock.equals(lastRock)) {
        return true
      }
      else {
        return UberPlayer.get.isIdle
      }
    }

    return UberPlayer.get.isIdle;
  }

  override def getStatus = {
    "Mining"
  }

  override def execute() {
    val rock = MyObjects.getNearest(rockIds:_*)
    UberMovement.turnTo(rock)
    if (rock.interact("Mine")) {
      lastRock = rock
      Wait.For(() => UberPlayer.get.getAnimation != -1, 2000)
    }
  }
}