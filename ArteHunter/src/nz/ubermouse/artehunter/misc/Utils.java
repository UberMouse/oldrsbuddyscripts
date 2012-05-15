package nz.ubermouse.artehunter.misc;

import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.GameObject;
import com.rsbuddy.script.wrappers.GroundItem;
import com.rsbuddy.script.wrappers.Item;
import com.rsbuddy.script.wrappers.Tile;

import java.awt.*;

public class Utils {

        public boolean tileFree(final Tile tile, final java.util.List<Tile> ignore) {
            if (tile != null && !ignore.contains(tile)
                    && Objects.getTopAt(tile) == null) {
                final int blocks[][] = Walking
                        .getCollisionFlags(Game.getFloorLevel()).clone();
                Tile b = Game.getMapBase();
                int baseX = b.getX();
                int baseY = b.getY();
                for (int ii = 0; ii < 105; ii++) {
                    for (int j = 0; j < 105; j++) {
                        int x = ii + baseX - 1;
                        int y = j + baseY - 1;
                        int curBlock = blocks[ii][j];
                        if (tile.getX() == x && tile.getY() == y) {
                            if ((curBlock & 0x1280100) == 0) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }

        public boolean InventoryItem(final int id, final String action) {
            if (org.rsbuddy.tabs.Inventory.getSelectedItem() != null) {
                if (org.rsbuddy.tabs.Inventory.getSelectedItem().getId() == id) {
                    return true;
                } else {
                    Item wrongItem = org.rsbuddy.tabs.Inventory.getSelectedItem();
                    if (wrongItem.interact("Use")) {
                        long systemTime = System.currentTimeMillis();
                        while (System.currentTimeMillis() - systemTime < Random
                                .nextInt(3000, 4500)) {
                            if (org.rsbuddy.tabs.Inventory.getSelectedItem() == null) {
                                break;
                            } else {
                                Task.sleep(20, 50);
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }
            Item correctItem = org.rsbuddy.tabs.Inventory.getItem(id);
            return correctItem != null && correctItem.interact(action);
        }

        public boolean pickup(final Tile tile, final int id) {
            if (tile != null && id > -1) {
                Filter<GroundItem> whatWeWant = new Filter<GroundItem>() {

                    public boolean accept(GroundItem t) {
                        return t != null && t.getItem().getId() == id
                                && t.getLocation().equals(tile);
                    }
                };
                GroundItem[] items = GroundItems.getLoaded(whatWeWant);
                if (items.length > 0) {
                    for (GroundItem tryItem : items) {
                        if (tryItem != null) {
                            if (!tryItem.isOnScreen()) {
                                walkTile(tryItem.getLocation(), tryItem);
                            }
                            if (tryItem != null && tryItem.isOnScreen()) {
                                int invC = org.rsbuddy.tabs.Inventory.getCount();
                                if (tryItem
                                        .interact("Take " + tryItem.getItem().getName())) {
                                    long sysTimeout = System
                                            .currentTimeMillis()
                                            + Random.nextInt(3000, 5000);
                                    while (System.currentTimeMillis() < sysTimeout) {
                                        if (org.rsbuddy.tabs.Inventory.getCount() > invC) {
                                            return true;
                                        }
                                        Task.sleep(50, 120);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return false;
        }

        public boolean doRun() {
            if (!Walking.isRunEnabled()) {
                int rand = Random.nextInt(20, 50);
                if (Walking.getEnergy() >= rand) {
                    Walking.setRun(true);
                    long systemTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - systemTime < Random
                            .nextInt(3000, 4500)) {
                        if (Walking.isRunEnabled()) {
                            break;
                        } else {
                            Task.sleep(50, 90);
                        }
                    }
                    return Walking.isRunEnabled();// Return if running.
                } else {
                    return false;// Cannot run yet.
                }
            } else {
                return false;// Didn't run, already running.
            }
        }

        public boolean walkTile(final Tile tile, final Object o) {
            boolean haveClicked = false;
            try {
                if (org.rsbuddy.tabs.Inventory.isItemSelected()) {
                    org.rsbuddy.tabs.Inventory.getSelectedItem().interact("Use");
                }
            } catch (Exception ignored) {
            }
            if (tile != null) {
                if (!tile.equals(Players.getLocal().getLocation())) {
                    if (!tile.isOnScreen()) {
                        Walking.stepTowards(tile);
                        long systemTime3 = System.currentTimeMillis();
                        while (System.currentTimeMillis() - systemTime3 < Random
                                .nextInt(2300, 3200)) {
                            if (!Players.getLocal().isIdle()) {
                                break;
                            } else {
                                Task.sleep(80);
                            }
                        }
                        while (Players.getLocal().isMoving()) {
                            Task.sleep(300, 500);
                        }
                        if (!tile.isOnScreen()) {
                            return false;
                        }
                    }
                    if (Players.getLocal().getLocation().equals(tile)
                            && Players.getLocal().isIdle()) {
                        return true;
                    } else {
                        long systemTime = System.currentTimeMillis();
                        while (System.currentTimeMillis() - systemTime < 15000) {
                            if (Players.getLocal().getLocation().equals(tile)
                                    && Players.getLocal().isIdle()) {
                                break;
                            } else {
                                if (Walking.getDestination() != null
                                        && Walking.getDestination()
                                        .equals(tile)) {
                                    if (o != null) {
                                        if (o instanceof GameObject) {
                                            GameObject oo = (GameObject) o;
                                            Tile ooTile = oo.getLocation();
                                            Tile currentTile = Tile
                                                    .getTileUnderMouse();
                                            if (!ooTile.equals(currentTile)) {
                                                oo.hover();
                                            }
                                        }
                                        if (o instanceof Tile) {
                                            Tile ooTile = (Tile) o;
                                            Point MS = ooTile.getPoint(Game.getFloorLevel());
                                            if (Mouse.getLocation().distance(
                                                    MS.x, MS.y) > 20) {
                                                int ms = Mouse.getSpeed();
                                                Mouse.setSpeed(Random.nextInt(2, 8));
                                                Mouse.move(MS);
                                                Mouse.setSpeed(ms);
                                            }
                                        }
                                        if (o instanceof Item) {
                                            Item oo = (Item) o;
                                            Point itemPoint = oo.getComponent()
                                                    .getCenter();
                                            Point MousePoint = Mouse
                                                    .getLocation();
                                            if (MousePoint.distance(
                                                    itemPoint.x, itemPoint.y) > 8) {
                                                Mouse.move(itemPoint, 8, 8);
                                            }
                                        }
                                    }
                                    Task.sleep(50, 70);
                                } else {
                                    if (Players.getLocal().isMoving()
                                            && Walking.getDestination() == null) {
                                        if (o != null && haveClicked) {
                                            if (o instanceof GameObject) {
                                                GameObject oo = (GameObject) o;
                                                Tile ooTile = oo.getLocation();
                                                Tile currentTile = Tile
                                                        .getTileUnderMouse();
                                                if (!ooTile.equals(currentTile)) {
                                                    oo.hover();
                                                }
                                            }
                                            if (o instanceof Tile) {
                                                Tile ooTile = (Tile) o;
                                                Tile currentTile = Tile
                                                        .getTileUnderMouse();
                                                if (!ooTile.equals(currentTile)) {
                                                    Mouse.setSpeed(Random.nextInt(2, 8));
                                                    Point MS = ooTile.getPoint(Game.getFloorLevel());
                                                    Mouse.move(MS);
                                                }
                                            }
                                            if (o instanceof Item) {
                                                Item oo = (Item) o;
                                                Point itemPoint = oo
                                                        .getComponent()
                                                        .getCenter();
                                                Point MousePoint = Mouse
                                                        .getLocation();
                                                if (MousePoint.distance(
                                                        itemPoint.x,
                                                        itemPoint.y) > 8) {
                                                    Mouse.move(itemPoint, 8, 8);
                                                }
                                            }
                                        }
                                        Task.sleep(30, 100);
                                    } else {
                                        if (clickTileOnScreen(tile)) {
                                            haveClicked = true;
                                            long systemTime2 = System
                                                    .currentTimeMillis();
                                            while (System.currentTimeMillis()
                                                    - systemTime2 < Random
                                                    .nextInt(2300, 3200)) {
                                                if (!Players.getLocal()
                                                        .isIdle()) {
                                                    break;
                                                } else {
                                                    Task.sleep(50);
                                                }
                                            }
                                        } else {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                        return Players.getLocal().getLocation().equals(tile)
                                && Players.getLocal().isIdle();
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        public boolean clickTileOnScreen(final Tile tile) {
            if (tile != null) {
                if (tile.isOnScreen()) {
                    Point moveTo = tile.getPoint(Game.getFloorLevel());
                    if (moveTo != null) {
                        Mouse.move(moveTo);
                        Point click = tile.getPoint(Game.getFloorLevel());
                        if (click != null) {
                            Tile onTile = Tile.getTileUnderMouse();
                            if (onTile != null) {
                                if (onTile.equals(tile)) {
                                    com.rsbuddy.script.methods.Menu.click("Walk here");
                                } else {
                                    int oldSpeed = Mouse.getSpeed();
                                    Mouse.setSpeed(Random.nextInt(2, 3));
                                    com.rsbuddy.script.methods.Menu.click("Walk here");
                                    Mouse.setSpeed(oldSpeed);
                                }
                                return true;
                            } else {
                                com.rsbuddy.script.methods.Menu.click("Walk here");
                                return true;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }
