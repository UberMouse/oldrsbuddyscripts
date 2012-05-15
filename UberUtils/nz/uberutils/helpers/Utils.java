package nz.uberutils.helpers;

import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.*;
import com.rsbuddy.script.wrappers.Component;
import nz.uberutils.methods.UberEquipment;
import nz.uberutils.methods.UberInventory;
import nz.uberutils.wrappers.BankItem;
import nz.uberutils.wrappers.LootItem;
import org.rsbuddy.net.WorldData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;


public class Utils
{
    private static final int    WALL = 0x200000;
    private static final Logger log  = Logger.getAnonymousLogger();

    /**
     * Finds if the current world is a members world or not.
     *
     * @return <tt>true</tt> if the current world is a members world.
     * @author UberMouse
     */
    public static boolean isWorldMembers() {
        int world = 0;
        try {
            world = Integer.valueOf(Widgets.get(550).getComponent(19).getText().replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            Game.openTab(Game.TAB_FRIENDS);
            Game.openTab(Game.TAB_INVENTORY);
            isWorldMembers();
        }
        WorldData w = WorldData.lookup(world);
        return w.isMember();
    }

    /**
     * Determines if the script is current running in Developer mode
     *
     * @return true if in dev mode
     */
    public static boolean isDevMode() {
        return Environment.getUsername() == null;
    }

    /**
     * Log text
     *
     * @param text info to be logged
     */
    public static void log(Object text) {
        log.info("" + text);
    }

    /**
     * Check if array contains String(s) check.
     *
     * @param array     the array to check
     * @param contains, using .contains instead of .equals
     * @param check     the String(s) to check
     * @return true, if successful
     */
    public static boolean arrayContains(String[] array, boolean contains, String... check) {
        if (array == null || check == null || array.length < 1)
            return false;
        for (String i : array) {
            if (i == null)
                continue;
            for (String l : check) {
                if (l == null)
                    continue;
                if (contains) {
                    if (l.toLowerCase().contains(i.toLowerCase())) {
                        return true;
                    }
                }
                else {
                    if (i.toLowerCase().equals(l.toLowerCase()))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if array contains String(s) check.
     *
     * @param array the array
     * @param check the check
     * @return true, if successful
     */
    public static boolean arrayContains(String[] array, String... check) {
        return arrayContains(array, false, check);
    }

    /**
     * Check if array contains int(s) check.
     *
     * @param array the array
     * @param check the check
     * @return true, if successful
     */
    public static boolean arrayContains(int[] array, int... check) {
        if (array == null || check == null || array.length < 1)
            return false;
        for (int i : array) {
            for (int l : check) {
                if (i == l)
                    return true;
            }
        }
        return false;
    }

    /**
     * Check if array of LootItems contains id(s) check.
     *
     * @param array the array
     * @param check the check
     * @return true, if successful
     */
    public static boolean arrayContains(LootItem[] array, int... check) {
        if (array == null || check == null || array.length < 1)
            return false;
        for (LootItem i : array) {
            for (int l : check) {
                if (i != null && i.getId() == l)
                    return true;
            }
        }
        return false;
    }

    /**
     * Check if array of LootItems contains names(s) check.
     *
     * @param array the array
     * @param check the check
     * @return true, if successful
     */
    public static boolean arrayContains(LootItem[] array, String... check) {
        if (array == null || check == null || array.length < 1)
            return false;
        for (LootItem i : array) {
            for (String l : check) {
                if (i != null &&
                    l != null &&
                    i.getName() != null &&
                    (i.getName().equalsIgnoreCase(l) || l.toLowerCase().contains(i.getName().toLowerCase())))
                    return true;
            }
        }
        return false;
    }

    /**
     * Check if array of BankItems contains id(s) check.
     *
     * @param array the array
     * @param check the check
     * @return true, if successful
     */
    public static boolean arrayContains(BankItem[] array, int... check) {
        if (array == null || check == null || array.length < 1)
            return false;
        for (BankItem i : array) {
            for (int l : check) {
                if (i.getId() == l)
                    return true;
            }
        }
        return false;
    }

    /**
     * Check if array of BankItems contains names(s) check.
     *
     * @param array the array
     * @param check the check
     * @return true, if successful
     */
    public static boolean arrayContains(BankItem[] array, String... check) {
        if (array == null || check == null || array.length < 1)
            return false;
        for (BankItem i : array) {
            for (String l : check) {
                if (i != null &&
                    l != null &&
                    i.getName() != null &&
                    (i.getName().equalsIgnoreCase(l) || l.toLowerCase().contains(i.getName().toLowerCase())))
                    return true;
            }
        }
        return false;
    }

    public static boolean arrayContains(int[][] arrays, int... check) {
        for (int[] array : arrays) {
            if (arrayContains(array, check))
                return true;
        }
        return false;
    }

    public static Tile getNearestNonWallTile(Tile tile) {
        return getNearestNonWallTile(tile, false);
    }

    public static Tile getNearestNonWallTile(Tile tile, boolean eightTiles) {
        Tile[] checkTiles = getSurroundingTiles(tile, eightTiles);
        int[] flags = getSurroundingCollisionFlags(tile, eightTiles);
        for (int i = 0; i < checkTiles.length; i++) {
            if ((flags[i] & WALL) == 0)
                return checkTiles[i];
            //            else
            //                getNearestNonWallTile(tile);
        }
        return null;
    }

    public static Tile[] getSurroundingTiles(Tile tile) {
        return getSurroundingTiles(tile, false);
    }

    public static Tile[] getSurroundingTiles(Tile tile, boolean eightTiles) {
        int x = tile.getX();
        int y = tile.getY();
        Tile north = new Tile(x, y + 1);
        Tile east = new Tile(x + 1, y);
        Tile south = new Tile(x, y - 1);
        Tile west = new Tile(x - 1, y);
        Tile northEast;
        Tile southEast;
        Tile southWest;
        Tile northWest;
        if (eightTiles) {
            northEast = new Tile(x + 1, y + 1);
            southEast = new Tile(x + 1, y - 1);
            southWest = new Tile(x - 1, y - 1);
            northWest = new Tile(x - 1, y + 1);
            return new Tile[]{north, northEast, east, southEast, south, southWest, west, northWest};
        }
        return new Tile[]{north, east, south, west};
    }

    public static Tile[] getDiagonalTiles(Tile tile) {
        int x = tile.getX();
        int y = tile.getY();
        Tile northEast;
        Tile southEast;
        Tile southWest;
        Tile northWest;
        northEast = new Tile(x + 1, y + 1);
        southEast = new Tile(x + 1, y - 1);
        southWest = new Tile(x - 1, y - 1);
        northWest = new Tile(x - 1, y + 1);
        return new Tile[]{northEast, southEast, southWest, northWest};
    }

    public static int[] getSurroundingCollisionFlags(Tile tile) {
        return getSurroundingCollisionFlags(tile, false);
    }

    public static int[] getSurroundingCollisionFlags(Tile tile, boolean eightTiles) {
        int[][] flags = Walking.getCollisionFlags(Game.getFloorLevel());
        int x = tile.getX();
        int y = tile.getY();
        int xOff = x - Game.getMapBase().getX() - Walking.getCollisionOffset(Game.getFloorLevel()).getX();
        int yOff = y - Game.getMapBase().getY() - Walking.getCollisionOffset(Game.getFloorLevel()).getY();
        int fNorth = flags[xOff][yOff + 1];
        int fEast = flags[xOff + 1][yOff];
        int fSouth = flags[xOff][yOff - 1];
        int fWest = flags[xOff - 1][yOff];
        int fNorthEast;
        int fSouthEast;
        int fSouthWest;
        int fNorthWest;
        if (eightTiles) {
            fNorthEast = flags[xOff + 1][yOff + 1];
            fSouthEast = flags[xOff + 1][yOff - 1];
            fSouthWest = flags[xOff - 1][yOff - 1];
            fNorthWest = flags[xOff - 1][yOff + 1];
            return new int[]{fNorth, fNorthEast, fEast, fSouthEast, fSouth, fSouthWest, fWest, fNorthWest};
        }
        return new int[]{fNorth, fEast, fSouth, fWest};
    }

    public static int getCollisionFlagAtTile(Tile tile) {
        if (!Walking.isLoaded(tile))
            return -1;
        int[][] flags = Walking.getCollisionFlags(Game.getFloorLevel());
        int x = tile.getX();
        int y = tile.getY();
        int xOff = x - Game.getMapBase().getX() - Walking.getCollisionOffset(Game.getFloorLevel()).getX();
        int yOff = y - Game.getMapBase().getY() - Walking.getCollisionOffset(Game.getFloorLevel()).getY();
        try {
            return flags[xOff][yOff];
        } catch (ArrayIndexOutOfBoundsException e) {
            log("Tile: " + tile);
            log("xOff: " + xOff + " yOff: " + yOff);
            log("Mapbase: " + Game.getMapBase());
            log("Collision offset: " + Walking.getCollisionOffset(Game.getFloorLevel()));
            log("Flags arraycount: " + flags.length + " Flag subarray length: " + flags[0].length);
        }
        return -1;
    }

    public static Tile[] getLoadedTiles() {
        int[][] flags = Walking.getCollisionFlags(Game.getFloorLevel());
        ArrayList<Tile> t = new ArrayList<Tile>();
        for (int i = 0; i < flags.length; i++) {
            int xOff = i + Game.getMapBase().getX() + Walking.getCollisionOffset(Game.getFloorLevel()).getX();
            for (int j = 0; j < flags[i].length; j++) {
                int yOff = j + Game.getMapBase().getY() + Walking.getCollisionOffset(Game.getFloorLevel()).getY();
                t.add(new Tile(xOff, yOff));
            }
        }
        return t.toArray(new Tile[t.size()]);
    }

    public static boolean playerHas(String name) {
        return playerHas(name, false);
    }

    public static boolean playerHas(String name, boolean cached) {
        return UberInventory.contains(name) || UberEquipment.contains(name, cached);
    }

    public static void saveImage(Image image, String location) {
        saveImage(image, location, null);
    }

    public static void saveImage(Image image, String location, String type) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        //using "painter" we can draw in to "bufferedImage"
        Graphics2D painter = bufferedImage.createGraphics();

        //draw the "image" to the "bufferedImage"
        painter.drawImage(image, null, null);

        //the new image file
        File outputImg = new File(location);
        if (!outputImg.exists())
            try {
                outputImg.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        //write the image to the file
        try {
            ImageIO.write(bufferedImage, (type != null) ? type : "jpg", outputImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage loadImage(String url, String path) {
        if (!path.endsWith("\\"))
            path += "\\";
        final String[] split = url.split("/");
        final String imgName = split[split.length - 1];
        final File file = new File(path + imgName);
        try {
            if (file.exists())
                return ImageIO.read(file);
            else {
                final BufferedImage read = ImageIO.read(new URL(url));
                if (read != null)
                    Utils.saveImage(read, path + imgName, url.substring(url.lastIndexOf(".")+1, url.length()));
                return read;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    public static void openURL(String url) {
        if (!java.awt.Desktop.isDesktopSupported()) {
            return;
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

        if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
            return;
        }
        try {

            java.net.URI uri = new java.net.URI(url);
            desktop.browse(uri);
        } catch (Exception ignored) {
        }
    }

    public static boolean canReach(Tile tile) {
        AStar pf = new AStar()
        {
            @Override
            public java.util.List<AStar.Node> customSuccessors(Node t) {
                java.util.LinkedList<Node> path = new java.util.LinkedList<Node>();
                for (Tile tile : getSurroundingTiles(t.toTile(Game.getMapBase().getX(), Game.getMapBase().getY()),
                        true)) {
                    if ((getCollisionFlagAtTile(tile) & WALL) == 0) {
                        path.add(new Node(tile.getX() - Game.getMapBase().getX(),
                                tile.getY() - Game.getMapBase().getY()));
                    }
                }
                return path;
            }
        };
        return pf.findPath(UberPlayer.location(), tile) != null;
    }

    //    public static Object callMethod(Object object, String name) {
    //        try {
    //            Method[] allMethods = object.getClass().getDeclaredMethods();
    //            for (Method m : allMethods) {
    //                String mname = m.getName();
    //                if (!mname.equals(name)) {
    //                    continue;
    //                }
    //                try {
    //                    m.setAccessible(true);
    //                    return m.invoke(object);
    //
    //                    // Handle any exceptions thrown by method to be invoked.
    //                } catch (InvocationTargetException x) {
    //                    Throwable cause = x.getCause();
    //                    cause.printStackTrace();
    //                }
    //            }
    //        } catch (IllegalAccessException x) {
    //            x.printStackTrace();
    //        }
    //        return null;
    //    }
    //
    //    public static Object callMethod(Object object, String name, Object argument) {
    //        try {
    //            Method[] allMethods = object.getClass().getDeclaredMethods();
    //            for (Method m : allMethods) {
    //                String mname = m.getName();
    //                if (!mname.equals(name)) {
    //                    continue;
    //                }
    //                try {
    //                    m.setAccessible(true);
    //                    return m.invoke(object, argument);
    //
    //                    // Handle any exceptions thrown by method to be invoked.
    //                } catch (InvocationTargetException x) {
    //                    Throwable cause = x.getCause();
    //                    cause.printStackTrace();
    //                }
    //            }
    //        } catch (IllegalAccessException x) {
    //            x.printStackTrace();
    //        }
    //        return null;
    //    }

    /**
     * Gets center point of GameObject
     *
     * @param obj GameObject to get center point of
     * @return Point representing center point of model if one was found, else null
     */
    public static Point getModelCenter(GameObject obj) {
        if (obj == null || obj.getModel() == null)
            return null;
        Model model = obj.getModel();
        Polygon[] polygons = model.getTriangles();
        System.out.println(polygons.length);
        ArrayList<Point> points = new ArrayList<Point>();
        for (Polygon polygon : polygons) {
            for (int i = 0; i < polygon.npoints; i++) {
                points.add(new Point(polygon.xpoints[i], polygon.ypoints[i]));
            }
        }
        int xTotal = 0;
        int yTotal = 0;
        for (Point p : points) {
            xTotal += p.x;
            yTotal += p.y;
        }
        if (points.size() <= 5) {
            System.out.println("Points < 5");
            return null;
        }
        return new Point(xTotal / points.size(), yTotal / points.size());
    }

    public static String parseTime(long millis, boolean newFormat) {
        long time = millis / 1000;
        String seconds = Integer.toString((int) (time % 60));
        String minutes = Integer.toString((int) ((time % 3600) / 60));
        String hours = Integer.toString((int) (time / 3600));
        String days = Integer.toString((int) (time / (3600 * 24)));
        for (int i = 0; i < 2; i++) {
            if (seconds.length() < 2)
                seconds = "0" + seconds;
            if (minutes.length() < 2)
                minutes = "0" + minutes;
            if (hours.length() < 2)
                hours = "0" + hours;
        }
        if (!newFormat)
            return hours + ":" + minutes + ":" + seconds;
        days = days + " day" + ((Integer.valueOf(days) != 1) ? "s" : "");
        hours = hours + " hour" + ((Integer.valueOf(hours) != 1) ? "s" : "");
        minutes = minutes + " minute" + ((Integer.valueOf(minutes) != 1) ? "s" : "");
        seconds = seconds + " second" + ((Integer.valueOf(seconds) != 1) ? "s" : "");
        return days + ", " + hours + ", " + minutes + ", " + seconds;
    }

    public static String parseTime(long millis) {
        return parseTime(millis, false);
    }

    /**
     * Check if skill is boosted
     *
     * @param skill skill to check
     * @return true if skill is boosted
     */
    public static boolean isBoosted(int skill) {
        return isBoosted(skill, true);
    }

    /**
     * Check if skill is boosted
     *
     * @param skill      skill to check
     * @param boostEarly boost skill before it's fully unboosted
     * @return true if skill is boosted
     */
    public static boolean isBoosted(int skill, boolean boostEarly) {
        if (boostEarly)
            return Skills.getCurrentLevel(skill) >= ((int) Math.ceil(Skills.getRealLevel(skill) * 1.05 + 3));
        else
            return Skills.getCurrentLevel(skill) >= Skills.getRealLevel(skill);
    }

    /**
     * Sleep script
     *
     * @param min min time to sleep
     * @param max max time to sleep
     */
    public static void sleep(int min, int max) {
        Task.sleep(min, max);
    }

    /**
     * Sleep script
     *
     * @param time time to sleep
     */
    public static void sleep(int time) {
        Task.sleep(time);
    }

    /**
     * Debugs text along with function caller and line numbers.
     *
     * @param text the text
     */
    public static void debug(Object text) {
        if (true) {
            String className = Thread.currentThread().getStackTrace()[2].getClassName();
            if (className.contains("$"))
                className = className.split("\\$")[1];
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            StackTraceElement stacktrace = stackTraceElements[2];
            String methodName = stacktrace.getMethodName();
            int lineNumber = stacktrace.getLineNumber();
            log.info("[" +
                     stackTraceElements[3].getClassName() +
                     "#" +
                     stackTraceElements[3].getMethodName() +
                     ":" +
                     stackTraceElements[3].getLineNumber() +
                     "] -> [" +
                     className +
                     "#" +
                     methodName +
                     ":" +
                     lineNumber +
                     "] -> " +
                     text);
        }
    }

    public static int random(int min, int max) {
        return Random.nextInt(min, max);
    }

    /**
     * Returns a Widget containing the search text
     *
     * @param text the text to search for
     * @return Widget if found, else null
     */
    public static Component getWidgetWithText(String text) {
        for (Widget widget : Widgets.getLoaded()) {
            for (Component comp : widget.getComponents()) {
                if (comp.getText().toLowerCase().contains(text.toLowerCase()))
                    return comp;
                for (Component comp2 : comp.getComponents()) {
                    if (comp2.getText().toLowerCase().contains(text.toLowerCase()))
                        return comp2;
                }
            }
        }
        return null;
    }

    /**
     * Is item noted (Returns false for items no on GE
     *
     * @param id item id to check
     * @return true if item is noted
     */
    public static boolean isNoted(int id) {
        try {
            return GrandExchange.getItemName(id).equals("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Perform p/h calculation
     *
     * @param num       number to calculate p/h on
     * @param startTime starttime to use
     * @return p/h calculation
     */
    public static int calcPH(int num, long startTime) {
        return (int) ((num) * 3600000D / (System.currentTimeMillis() - startTime));
    }

    public static void waitUntilMoving(int timeout) {
        for (int i = 0; i < timeout && !UberPlayer.isMoving(); i++)
            sleep(100);
    }

    public static void waitUntilStopped(int timeout) {
        for (int i = 0; i < timeout && UberPlayer.isMoving(); i++)
            sleep(100);
    }

    /**
     * Click Item
     *
     * @param item to click
     */
    public static void clickItem(Item item) {
        Mouse.move(item.getComponent().getCenter());
        Mouse.moveRandomly(0, 4);
        Mouse.click(true);
    }

    /**
     * Sleeps until widget is valid
     *
     * @param c1   Main component ID
     * @param c2   Sub component ID
     * @param time Time to wait (1 = 100ms)
     */
    public static void sleepUntilValid(int c1, int c2, int time) {
        for (int i = 0; i < time && !Widgets.getComponent(c1, c2).isValid(); i++)
            Utils.sleep(100);
    }

    /**
     * Sleeps until widget is valid
     *
     * @param c1 Main component ID
     * @param c2 Sub component ID
     */
    public static void sleepUntilValid(int c1, int c2) {
        sleepUntilValid(c1, c2, 15);
    }

    /**
     * Get random Tile in Area
     *
     * @param area Area to get Tile in
     * @return random Tile in Area
     */
    public static Tile getRandomTile(Area area) {
        final Tile[][] tiles = area.getTiles();
        final int y = random(0, tiles.length - 1);
        final int x = random(0, tiles[y].length - 1);
        return tiles[x][y];
    }

    /**
     * Waits for player to start and stop moving
     *
     * @param timeout to use for waiting for movement and then waiting to stop
     */
    public static void waitToStop(int timeout) {
        waitUntilMoving(timeout);
        waitUntilStopped(timeout);
    }

    /*
     * Waits for player to start and stop moving
    */
    public static void waitToStop() {
        waitToStop(7);
    }
}