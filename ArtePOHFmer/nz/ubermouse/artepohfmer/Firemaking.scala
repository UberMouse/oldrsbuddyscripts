package nz.ubermouse.artepohfmer

import data.Constants
import nz.uberutils.helpers.{UberPlayer, Utils}
import org.rsbuddy.tabs.Inventory
import com.rsbuddy.script.wrappers.{LocalPath, Tile}
import collection.mutable.ListBuffer
import com.rsbuddy.script.task.Task
import nz.uberutils.methods.UberMovement
import nz.ubermouse.uberutils.helpers.Wait
import com.rsbuddy.script.methods.{Calculations, Objects, Walking}

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 6/25/11
 * Time: 1:32 PM
 * Package: nz.ubermouse.artepohfmer; */
object Firemaking
{
  var logId                            = 0;
  var burnSuccessCallback: () => Any   = null
  var tilePath           : Array[Tile] = null
  var bestLength                       = 0;

  def canLight(tile: Tile): Boolean = {
    canLight(tile, true);
  }

  def canLight(tile: Tile, useAsPath: Boolean): Boolean = {
    if (!Walking.isLoaded(tile))
      false
    if (Objects.getTopAt(tile) != null)
      return false
    if (!useAsPath)
      (Utils.getCollisionFlagAtTile(tile) & LocalPath.BLOCKED) == 0
    else
      (Utils.getCollisionFlagAtTile(tile) & LocalPath.BLOCKED) == 0 &&
      (Utils.getCollisionFlagAtTile(tile) & LocalPath.WALL_WEST) == 0
  }

  def genPath(tile: Tile) = {
    var t = tile;
    val tiles: ListBuffer[Tile] = new ListBuffer[Tile];
    if (!canLight(t, false))
      null
    else {
      while (canLight(t) && tiles.length < 28) {
        tiles += t;
        t = new Tile(t.getX - 1, t.getY);
      }
      tiles;
    }
  }

  def pathLength(tile: Tile): Int = {
    val tiles = genPath(tile)
    if (tiles == null)
      0;
    else
      tiles.length
  }

  def getBestLoadedTile: Tile = {
    getBestLoadedTile(UberPlayer.location(), Inventory.getCount(logId))
  }

  def getBestLoadedTile(maxLength: Int): Tile = {
    getBestLoadedTile(UberPlayer.location(), maxLength)
  }

  def getBestLoadedTile(tile: Tile, maxLength: Int): Tile = {
    bestLength = 0;
    if (pathLength(tile) >= maxLength)
      tile
    else {
      val tiles = Utils.getLoadedTiles
      val tileIndex = tiles.indexOf(tile)
      var upperIndex = tileIndex + 1
      var lowerIndex = tileIndex - 1
      var bestTile: Tile = null
      def check(t: Tile): Boolean = {
        if (checkTile(t, bestLength)) {
          if (bestLength >= maxLength)
            return true
          else {
            bestLength = pathLength(t)
            bestTile = t
          }
        }
        false
      }
      while (upperIndex < tiles.size && lowerIndex > 0) {
        if (check(tiles(upperIndex)))
          return tiles(upperIndex)
        else if (check(tiles(lowerIndex)))
          return tiles(upperIndex)
        lowerIndex -= 1
        upperIndex += 1
      }
      bestTile
    }
  }

  private def checkTile(tile: Tile, bestLength: Int): Boolean = {
    pathLength(tile) > bestLength
  }

  def burnLogs(maxLength: Int) {
    if (UberPlayer.get().getAnimation != -1 || Inventory.getCount(logId) == 0)
      return;
    if (pathLength(UberPlayer.location()) < maxLength) {
      val tile = getBestLoadedTile(maxLength)
      if (!tile.equals(UberPlayer.location())) {
        UberMovement.turnTo(tile)
        tile.interact("Walk")
        Wait.While(UberPlayer.isMoving && Calculations.distanceTo(tile) > 5, 20) {}
      }
    }
    else {
      val tb = Inventory.getItem(Constants.TINDERBOX_ID)
      val curTile = UberPlayer.location()
      Inventory.useItem(tb, Inventory.getItem(logId))
      val logCount = Inventory.getCount(logId);
      Wait.For(UberPlayer.get().getAnimation != -1, 20) {};
      if (Wait.While(curTile.equals(UberPlayer.location()), 20) {})
        Task.sleep(400, 500)
      if (Inventory.getCount(logId) < logCount) {
        if (burnSuccessCallback != null) {
          burnSuccessCallback()
        }
      }
    }
  }
}