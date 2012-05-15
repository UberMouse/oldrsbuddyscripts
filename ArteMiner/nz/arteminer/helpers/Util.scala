package nz.arteminer.helpers

import java.awt.Point
import com.rsbuddy.script.wrappers.Model

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/29/11
 * Time: 4:44 PM
 * Package: nz.arteminer.helpers; */

object Util {
  def pointInModel(p:Point, m:Model):Boolean = {
    for(poly <- m.getTriangles)
      if(poly.contains(p))
        return true;
    return false;
  }
}