package nz.arteminer.helpers

import nz.arteminer.misc.Strategy
import collection.mutable.{ListBuffer, HashMap}
import nz.uberutils.helpers.Utils

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 9:43 PM
 * Package: nz.arteminer.helpers; */

object StrategyContainer {
  val stratagies = new ListBuffer[HashMap[String, Strategy]]

  def addStrategy(name:String, strat:Strategy) {
    stratagies += (new HashMap[String, Strategy] += name -> strat)
  }

  def removeStrategy(name:String) {
    for(map <- stratagies) {
      if(map.contains(name))
        stratagies.remove(stratagies.indexOf(map))
    }
  }

  def insertStrategy(name:String, strat:Strategy, index:Int) {
    stratagies.insert(index, (new HashMap[String, Strategy] += name -> strat));
  }

  def execute:String = {
    for(map <- stratagies) {
      for(strat <- map.values)
        if(strat.isValid) {
          strat.execute();
          return strat.getStatus;
        }
    }
    return "Idling..";
  }
}