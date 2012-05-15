package nz.arteminer.modules

import com.rsbuddy.script.wrappers.GameObject
import nz.arteminer.misc.Module
import nz.uberutils.methods.UberMovement
import nz.arteminer.methods.MyObjects
import nz.arteminer.helpers.{StrategyContainer, Wait}
import nz.uberutils.helpers.{Utils, UberPlayer}

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 3:37 AM
 * Package: nz.arteminer.modules; */
class GenericMine extends Module
{
  override def loop():String = {
    StrategyContainer.execute
  }
}