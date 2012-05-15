package nz.arteminer.stratagies.generic

import com.rsbuddy.script.wrappers.Tile
import nz.arteminer.misc.{GameConstants, Strategy}
import com.rsbuddy.script.methods._
import nz.uberutils.methods.UberMovement
import nz.arteminer.helpers.Wait
import nz.uberutils.helpers.{Utils, UberPlayer}

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/29/11
 * Time: 1:50 AM
 * Package: nz.arteminer.strategies.generic; */
class Bank(bankTile: Tile) extends Strategy
{
  override def isValid = Inventory.isFull || Bank.DepositBox.isOpen

  override def getStatus = "Banking"

  override def execute() {
    if (!Calculations.isTileOnScreen(bankTile) && !UberPlayer.isMoving)
      UberMovement.turnTo(bankTile)
    else if (!Calculations.isTileOnMap(bankTile) && !UberPlayer.isMoving)
      Walking.findPath(bankTile).traverse()
    else {
      if (!Bank.DepositBox.isOpen) {
        Objects.getNearest(25937).interact("Bank")
        Wait.For(() => Bank.DepositBox.isOpen)
      }
      while (Bank.DepositBox.getItems.exists((item) => !GameConstants.PICK_IDS.exists((pick) => pick == item.getId)) &&
             Bank.DepositBox.isOpen) {
        if (!Bank.DepositBox.depositAllExcept(GameConstants.PICK_IDS: _*))
          Bank.DepositBox.close
      }
      Utils.debug(Inventory.getCountExcept(GameConstants.PICK_IDS: _*))
      Bank.DepositBox.close
    }
  }

}