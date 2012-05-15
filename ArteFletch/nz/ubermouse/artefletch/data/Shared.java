package nz.ubermouse.artefletch.data;

import nz.ubermouse.artefletch.misc.Task;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 5:57 PM
 * Package: nz.ubermouse.artefletch.data;
 */
public class Shared
{
    public static final ArrayList<Task> tasks = new ArrayList<Task>();
    public static boolean fletching;
    public static Task currentTask;
    public static int actionsDone = 0;
}
