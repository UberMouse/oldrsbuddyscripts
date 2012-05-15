package nz.arteminer.misc

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 3:04 AM
 * Package: nz.arteminer.threads; */

trait Strategy {
  def getStatus:String
  def execute()
  def isValid:Boolean
}