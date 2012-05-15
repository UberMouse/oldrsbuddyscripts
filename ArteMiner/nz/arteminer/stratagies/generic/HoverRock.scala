package nz.arteminer.stratagies.generic

import nz.arteminer.misc.Strategy
import nz.uberutils.helpers.UberPlayer
import nz.arteminer.methods.MyObjects
import com.rsbuddy.script.wrappers.GameObject
import com.rsbuddy.script.methods.{Menu, Mouse}
import nz.arteminer.helpers.Util
import nz.uberutils.methods.UberCamera

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/29/11
 * Time: 4:26 PM
 * Package: nz.arteminer.strategies.generic; */
class HoverRock(rockIds: Int*) extends Strategy
{
  override def getStatus = "Hovering next rock"

  override def isValid: Boolean = {
    if (UberPlayer.isMoving)
      return false
    val secondNearest: GameObject = MyObjects.getSecondNearest(rockIds:_*)
    if(secondNearest != null)
      return !Util.pointInModel(Mouse.getLocation, secondNearest.getModel) || !Menu.contains("Mine")
    return false
  }

  override def execute() {
    val secondNearest: GameObject = MyObjects.getSecondNearest(rockIds:_*)
    if (secondNearest != null) {
      Mouse.move(secondNearest.getModel.getNextPoint)
      if(!secondNearest.isOnScreen)
        UberCamera.turnTo(secondNearest, 15)
    }
  }

}