package nz.arteminer.stratagies.generic

import nz.arteminer.misc.Strategy
import com.rsbuddy.script.methods.Inventory
import nz.uberutils.methods.UberInventory
import com.rsbuddy.script.task.Task
import com.rsbuddy.script.wrappers.Item

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 9:24 PM
 * Package: nz.arteminer.strategies; */
class Drop(rockNames: Array[String]) extends Strategy
{
  override def isValid = Inventory.isFull

  override def getStatus = "Dropping"

  override def execute() {
    for(item:Item <- UberInventory.getItems) {
      if(rockNames.exists((name) => item.getName.contains(name))) {
        item.interact("Drop")
        Task.sleep(50,100);
      }
    }
  }
}