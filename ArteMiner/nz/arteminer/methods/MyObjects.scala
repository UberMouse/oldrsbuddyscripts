package nz.arteminer.methods

import com.rsbuddy.script.util.Filter
import com.rsbuddy.script.methods.{Calculations, Objects}
import com.rsbuddy.script.wrappers.{Tile, GameObject}
import nz.uberutils.helpers.UberPlayer

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 4:22 PM
 * Package: nz.arteminer.methods; */
object MyObjects
{
  def getNearest(filter: Filter[GameObject]): GameObject = {
    var cur: GameObject = null;
    var dist: Double = -1;
    val objs: Array[GameObject] = Objects.getLoaded
    objs.foreach {
                   o =>
                     if (filter.accept(o)) {
                       val distTmp: Double = Calculations.distanceBetween(UberPlayer.get().getLocation, o.getLocation);
                       if (cur == null) {
                         dist = distTmp;
                         cur = o;
                       }
                       else if (distTmp < dist) {
                         cur = o;
                         dist = distTmp;
                       }
                     }
                 }
    return cur;
  }

  def getNearest(ids: Int*): GameObject = {
    getNearest(new Filter[GameObject]
    {
      def accept(o: GameObject) = ids.exists((p) => o.getId == p)
    })
  }

  def getSecondNearest(filter: Filter[GameObject]): GameObject = {
    val nearest = getNearest(filter)
    var cur: GameObject = null;
    var dist: Double = -1;
    val objs: Array[GameObject] = Objects.getLoaded
    objs.foreach {
                   o =>
                     if (!o.equals(nearest) && filter.accept(o)) {
                       val distTmp: Double = Calculations.distanceBetween(UberPlayer.get().getLocation, o.getLocation);
                       if (cur == null) {
                         dist = distTmp;
                         cur = o;
                       }
                       else if (distTmp < dist) {
                         cur = o;
                         dist = distTmp;
                       }
                     }
                 }
    return cur;
  }

  def compareObjects(o1: GameObject, o2: GameObject): Boolean = {
    return o1.getLocation.equals(o2.getLocation)
  }

  def getSecondNearest(ids: Int*): GameObject = {
    getSecondNearest(new Filter[GameObject]
    {
      def accept(o: GameObject) = ids.exists((p) => o.getId == p)
    })
  }

  def getTopAt(tile: Tile, id: Array[Int]): GameObject = {
    val objects: Array[GameObject] = Objects.getAllAt(tile)
    for (o <- objects) {
      for (doorID <- id) if (o.getId == doorID) return o
    }
    return null
  }
}