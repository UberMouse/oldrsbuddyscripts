package nz.arteminer.helpers

import com.rsbuddy.script.task.Task

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/28/11
 * Time: 3:55 AM
 * Package: nz.arteminer.helpers; */
object Wait
{
  def For(callback: () => Boolean, timeout: Int): Boolean = {
    var times = 0;
    while (times <= (timeout / 100) && !callback()) {
      Task.sleep(100)
      ({
        times += 1;
      })
    }
    times != (times / 100)
  }

  def For(callback: () => Boolean): Boolean = {
    For(callback, 10)
  }


  def Until(callback: () => Boolean, timeout: Int): Boolean = {
    var times = 0;
    while (times <= (timeout / 100) && callback()) {
      Task.sleep(100)
      ({
        times += 1;
      })
    }
    times != (times / 100)
  }

  def Until(callback: () => Boolean): Boolean = {
    Until(callback, 10);
  }
}