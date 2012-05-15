package nz.ubermouse.artehunter;

import nz.uberutils.helpers.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: UberMouse
 * Date: 10/21/11
 * Time: 3:00 PM
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        Logger.instance().debug("Thread: " + t.getName() + " threw exception: " + e.getMessage() + " at:");
        Logger.instance().debug(e.getStackTrace()[0].getFileName() + ":" + e.getStackTrace()[0].getLineNumber());
    }
}
