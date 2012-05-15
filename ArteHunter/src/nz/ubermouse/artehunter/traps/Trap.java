package nz.ubermouse.artehunter.traps;

import nz.ubermouse.artehunter.Hunter;
import nz.ubermouse.artehunter.misc.Utils;
import nz.ubermouse.artehunter.ui.PaintShell;
import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Trap extends PaintShell {

    public int unitsHunted = 0;
    private int lostTraps = 0;
    private boolean trapMissing = false;
    private boolean notOurTrap = false;
    private boolean cantLayTrap = false;
    private Tile homeTile = null;
    private Area huntingArea = null;
    public int costPerUnit = -1;
    public boolean bird = false;
    private Tile closestNPCTrapTile = null;
    private Tile lClosestTile = null;
    private long lClosestTileT = 0;

    public String status = null;

    private Utils utils = new Utils();

    private long failTime = -1;

    public int ai() {
        if (utils.doRun()) {
            status = "Turning on run!";
        }
        switch (getState(false)) {
            case LAY_TRAP:
                status = "Laying trap.";
                lay();
                break;
            case LOOT_TRAP:
                status = "Looting trap.";
                loot();
                break;
            case EVADING_TRAP:
                status = "Finding missing trap.";
                evadingTrap();
                break;
            case FALLEN_TRAP:
                status = "Fixing fallen trap.";
                handleFallenTrap();
                break;
            case NOT_LOGGED_IN:
                status = "null";
                Task.sleep(5000, 15000);
                break;
            case ABORT:
                status = "null";
                if (failTime == -1) {
                    failTime = System.currentTimeMillis();
                } else if (failTime + 30000 < System.currentTimeMillis()) {
                    status = "Aborting...";
                    Game.logout(false);
                    return -1;
                } else {
                }
                return 1000;
            case SLEEP:
                antiBan();
                pruneTraps();
                Task.sleep(100, 300);
        }
        failTime = -1;
        return 0;
    }

    private void antiBan() {
        try {
            int randomInt = Random.nextInt(1, 100);
            switch (randomInt) {
                case 5:
                    status = "Moving mouse randomly...";
                    Mouse.moveRandomly(800);
                    break;
                case 9:
                    status = "Moving camera randomly...";
                    int angle = Camera.getCompassAngle() + Random.nextInt(-200, 200);
                    if (angle < 0) {
                        angle += 359;
                    }
                    if (angle > 359) {
                        angle -= 359;
                    }
                    Camera.setCompassAngle(angle);
                    break;
                case 20:
                    status = "Examining a random object.";
                    GameObject[] gameObjects = Objects.getLoaded();
                    java.util.List<GameObject> onScreen = new ArrayList<GameObject>();
                    for (GameObject obj : gameObjects) {
                        if (obj != null && obj.isOnScreen()) {
                            onScreen.add(obj);
                        }
                    }
                    GameObject[] onScreenO = onScreen
                            .toArray(new GameObject[onScreen.size()]);
                    int g = -1;
                    int m = 0;
                    final int mO = 50;
                    while (g == -1 && m < mO) {
                        int ran = Random.nextInt(1, onScreenO.length) - 1;
                        if (onScreenO[ran] != null) {
                            g = ran;
                        }
                    }
                    if (g > -1) {
                        GameObject exam = onScreenO[g];
                        if (exam != null) {
                            exam.interact("Examine");
                        }
                    }
                    break;
            }
            boolean preWalk = false;
            if (closestNPCTrapTile != null) {
                if (lClosestTile == null) {
                    lClosestTile = closestNPCTrapTile;
                    lClosestTileT = System.currentTimeMillis();
                } else if (!lClosestTile.equals(closestNPCTrapTile)) {
                    lClosestTile = null;
                } else if (System.currentTimeMillis() > lClosestTileT
                        + Random.nextInt(2500, 3500)
                        && preWalk) {
                    if (Calculations.distanceTo(closestNPCTrapTile) > 1) {
                        utils.walkTile(
                                getClosestFreeTile(closestNPCTrapTile), "");
                    }
                }
                Point tP = closestNPCTrapTile.getPoint(Game.getFloorLevel());
                Point mP = Mouse.getLocation();
                if (mP.distance(tP.x, tP.y) > 20) {
                    Mouse.move(tP, 15, 15);
                }
            } else {
                Tile[] traps = TrapManager.getTiles();
                for (Tile t : traps) {
                    if (trapOther(t)) {
                        Point tP = t.getPoint(Game.getFloorLevel());
                        Point mP = Mouse.getLocation();
                        if (Calculations.distanceTo(t) > 1 && preWalk) {
                            utils.walkTile(getClosestFreeTile(t), t);
                        } else {
                            if (mP.distance(tP.x, tP.y) > 20) {
                                Mouse.move(tP, 15, 15);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    public void messageReceived(MessageEvent e) {
        String message = e.getMessage();
        if (message.toLowerCase().contains(
                message_notOurTrap().toLowerCase())) {
            notOurTrap = true;
        } else if (message.toLowerCase().contains(
                message_missingTrap1().toLowerCase())) {
            trapMissing = true;
        } else if (message.toLowerCase().contains(
                message_missingTrap2().toLowerCase())) {
            trapMissing = true;
        } else if (message.toLowerCase().contains(
                message_cantLayTrap().toLowerCase())) {
            cantLayTrap = true;
        }
    }

    public abstract String message_notOurTrap();

    public abstract String message_missingTrap1();

    public abstract String message_missingTrap2();

    public abstract String message_cantLayTrap();

    public abstract void cleanseInventory();

    private Traps.TrapState getState(final boolean isPaint) {
        if (!Game.isLoggedIn()) {
            return Traps.TrapState.NOT_LOGGED_IN;
        }
        if (!isPaint && org.rsbuddy.tabs.Inventory.getCount() >= 25) {
            cleanseInventory();
        }
        if (outOfTraps()) {
            return Traps.TrapState.ABORT;
        } else if (isTrapFallen()) {
            return Traps.TrapState.FALLEN_TRAP;
        } else if (trapMissing()) {
            return Traps.TrapState.EVADING_TRAP;
        } else if (trapNeedsLaid()) {
            return Traps.TrapState.LAY_TRAP;
        } else if (trapNeedsLooted()) {
            return Traps.TrapState.LOOT_TRAP;
        }
        if (!isPaint) {
            cleanseInventory();
        }
        return Traps.TrapState.SLEEP;
    }

    private boolean trapMissing() {
        return trapMissing;
    }

    private boolean notOurTrap() {
        return notOurTrap;
    }

    private boolean findEvadingTrap() {
        Filter<GameObject> trapsNearUs = new Filter<GameObject>() {

            public boolean accept(GameObject t) {
                try {
                    return t != null
                            && huntingArea.contains(t.getLocation());
                } catch (Exception e) {
                    return false;
                }
            }
        };
        GameObject[] tryObjects = Objects.getLoaded(trapsNearUs);
        Tile[] ourTiles = TrapManager.getTiles();
        java.util.List<Tile> skipTiles = new ArrayList<Tile>();
        skipTiles.addAll(Arrays.asList(ourTiles));
        for (GameObject checkTrap : tryObjects) {
            if (checkTrap != null
                    && !skipTiles.contains(checkTrap.getLocation())) {
                if (!checkTrap.isOnScreen()) {
                    if (!utils.walkTile(checkTrap.getLocation(), -1)) {
                        continue;
                    }
                }
                if (checkTrap.isOnScreen()) {
                    if (checkTrap.interact("Dismantle")
                            || checkTrap.interact("Check")) {
                        long systemTime = System.currentTimeMillis();
                        int oldIC = org.rsbuddy.tabs.Inventory.getCount();
                        while (System.currentTimeMillis() - systemTime < 8000) {
                            if (notOurTrap
                                    || Players.getLocal()
                                    .getAnimation() > 0) {
                                break;
                            } else {
                                Task.sleep(50);
                            }
                        }
                        if (notOurTrap) {
                            notOurTrap = false;
                        } else if (Players.getLocal().getAnimation() > 0) {
                            long sysTime = System.currentTimeMillis();
                            while (System.currentTimeMillis() - sysTime < 8000) {
                                if (notOurTrap()) {
                                    notOurTrap = false;
                                    break;
                                } else {
                                    if (org.rsbuddy.tabs.Inventory.getCount() > oldIC) {
                                        trapMissing = false;
                                        return true;
                                    }
                                }
                                Task.sleep(40, 180);
                            }
                        }
                    }
                }
            }
        }
        //log("No missing trap[s] found...we guess the trap fell, hopefully, we will find it and re-calibrate...");
        lostTraps++;
        trapMissing = false;
        notOurTrap = false;
        return false;
    }

    private void evadingTrap() {
        if (findEvadingTrap()) {
            //log("Found evading trap!");
        } else {
            //log("We were unable to find evading trap...");
        }
    }

    public abstract double expPerUnit();

    public abstract int InventoryTrapID();

    public abstract int InventoryUnitID();

    public abstract int obj_TrapSetup();

    public abstract int obj_TrapFailed();

    public abstract int obj_TrapCaught();

    public abstract int[] obj_TrapOther();

    public abstract int huntingNPCID();

    private boolean outOfTraps() {
        return org.rsbuddy.tabs.Inventory.getCount(InventoryTrapID()) == 0
                && TrapManager.getTiles().length == 0;
    }

    public abstract int getTrapAbilityCount();

    private Tile modTile(final Tile tile, final int x, final int y) {
        if (tile != null) {
            return new Tile(tile.getX() + x, tile.getY() + y);
        }
        return null;
    }

    private Tile getFreeTile() {
        Tile fTile = null;
        if (homeTile == null) {
            homeTile = Players.getLocal().getLocation();
            huntingArea = new Area(new Tile(homeTile.getX() + 2, homeTile
                    .getY() - 2), new Tile(homeTile.getX() - 2, homeTile
                    .getY() + 2));
        }
        if (homeTile != null) {
            Tile[] ourTiles = TrapManager.getTiles();
            java.util.List<Tile> listTiles = new ArrayList<Tile>();
            listTiles.addAll(Arrays.asList(ourTiles));
            Tile[] X = new Tile[5];
            X[0] = homeTile;
            X[1] = modTile(homeTile, -1, 1);
            X[2] = modTile(homeTile, 1, 1);
            X[3] = modTile(homeTile, 1, -1);
            X[4] = modTile(homeTile, -1, -1);
            for (Tile aX : X) {
                if (utils.tileFree(aX, listTiles)) {
                    return aX;
                }
            }
            Tile[] tiles = huntingArea.getTileArray();
            double dist = 99999.0;
            for (Tile t : tiles) {
                if (utils.tileFree(t, listTiles)) {
                    double d = Calculations.distanceBetween(Players
                            .getLocal().getLocation(), t);
                    if (d < dist) {
                        dist = d;
                        fTile = t;
                    }
                }
            }
        }
        return fTile;
    }

    private Tile getClosestFreeTile(final Tile tile) {
        if (homeTile == null) {
            homeTile = Players.getLocal().getLocation();
            huntingArea = new Area(new Tile(homeTile.getX() + 2, homeTile
                    .getY() - 2), new Tile(homeTile.getX() - 2, homeTile
                    .getY() + 2));
        }
        if (homeTile != null) {
            Tile[] ourTiles = TrapManager.getTiles();
            java.util.List<Tile> listTiles = new ArrayList<Tile>();
            listTiles.addAll(Arrays.asList(ourTiles));
            Tile[] X = new Tile[5];
            X[0] = modTile(tile, -1, 0);
            X[1] = modTile(tile, 1, 0);
            X[2] = modTile(tile, 0, -1);
            X[3] = modTile(tile, 0, 1);
            java.util.List<Tile> l2 = new ArrayList<Tile>();
            for (Tile aX : X) {
                if (utils.tileFree(aX, listTiles)) {
                    l2.add(aX);
                }
            }
            if (l2.size() > 0) {
                try {
                    return l2.get(Random.nextInt(0, l2.size() - 1));
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    private Tile getFreeTile(final Tile tile1) {
        Tile fTile = null;
        if (homeTile == null) {
            homeTile = Players.getLocal().getLocation();
            huntingArea = new Area(new Tile(homeTile.getX() + 2, homeTile
                    .getY() - 2), new Tile(homeTile.getX() - 2, homeTile
                    .getY() + 2));
        }
        if (homeTile != null) {
            Tile[] ourTiles = TrapManager.getTiles();
            java.util.List<Tile> listTiles = new ArrayList<Tile>();
            listTiles.addAll(Arrays.asList(ourTiles));
            Tile[] X = new Tile[5];
            X[0] = homeTile;
            X[1] = modTile(homeTile, -1, 1);
            X[2] = modTile(homeTile, 1, 1);
            X[3] = modTile(homeTile, 1, -1);
            X[4] = modTile(homeTile, -1, -1);
            for (Tile aX : X) {
                if (utils.tileFree(aX, listTiles)
                        && (!aX.equals(tile1))) {
                    return aX;
                }
            }
            Tile[] tiles = huntingArea.getTileArray();
            double dist = 99999.0;
            for (Tile t : tiles) {
                if (utils.tileFree(t, listTiles) && (!t.equals(tile1))) {
                    double d = Calculations.distanceBetween(Players
                            .getLocal().getLocation(), t);
                    if (d < dist) {
                        dist = d;
                        fTile = t;
                    }
                }
            }
        }
        return fTile;
    }

    private Item getInventoryTrapItem() {
        if (org.rsbuddy.tabs.Inventory.contains(InventoryTrapID())) {
            Item theTrap = org.rsbuddy.tabs.Inventory.getItem(InventoryTrapID());
            if (theTrap != null) {
                return theTrap;
            }
        }
        return null;
    }

    public abstract int animation_layTrap();

    private boolean layTrap(final Tile tile) {
        if (tile != null) {
            // if (hasTrap(tile) || hasTrapItem(tile)) {
            if (hasTrap(tile)) {
                return false;
            }
            utils.walkTile(tile, getInventoryTrapItem());
            if (Players.getLocal().getLocation().equals(tile)) {
                Filter<Player> playersOnUs = new Filter<Player>() {
                    public boolean accept(Player t) {
                        return t != null && t.getLocation().equals(tile)
                                && !t.equals(Players.getLocal());
                    }
                };
                Player[] chars = Players.getLoaded(playersOnUs);
                if (chars.length > 0) {
                    status = "Someone is under us, waiting...";
                    long timeOut = System.currentTimeMillis()
                            + Random.nextInt(3000, 5000);
                    while (System.currentTimeMillis() <= timeOut) {
                        for (Player check : chars) {
                            if (check != null
                                    && check.getAnimation() == animation_layTrap()) {
                                status = "Lol... noobs laid a trap under us.";
                                cantLayTrap = false;
                                return false;
                            }
                        }
                        Task.sleep(50, 80);
                    }
                }
                if (utils.InventoryItem(InventoryTrapID(), "Lay")) {
                    long waitAnimation = System.currentTimeMillis()
                            + Random.nextInt(3000, 4300);
                    while (System.currentTimeMillis() < waitAnimation) {
                        if (Players.getLocal().getAnimation() > 0
                                || cantLayTrap) {
                            break;
                        }
                        Mouse.setSpeed(Random.nextInt(2, 8));
                        Tile lt = getLootTrap(false);
                        if (trapNeedsLaid(1)) {
                            lt = getFreeTile(tile);
                        }
                        Tile tum = Tile.getTileUnderMouse();
                        if (lt != null && tum != null) {
                            if (!tum.equals(lt)) {
                                Point tP = lt.getPoint(Game.getFloorLevel());
                                if (tP != null) {
                                    Mouse.move(tP);
                                }
                            }
                        } else if (lt != null) {
                            Point tP = lt.getPoint(Game.getFloorLevel());
                            if (tP != null) {
                                Mouse.move(tP);
                            }
                        }
                        Task.sleep(50, 80);
                    }
                }
                if (notOurTrap) {
                    trapMissing = true;
                    notOurTrap = false;
                    return false;
                }
                if (cantLayTrap) {
                    if (hasTrapItem(tile)) {
                        utils.pickup(tile, InventoryTrapID());
                    }
                    cantLayTrap = false;
                    notOurTrap = false;
                    return false;
                }
                if (Players.getLocal().getAnimation() > 0) {
                    while (Players.getLocal().getAnimation() > 0) {
                        Task.sleep(50, 80);
                        Tile lt = getLootTrap(false);
                        if (trapNeedsLaid(1)) {
                            lt = getFreeTile(tile);
                        }
                        Tile tum = Tile.getTileUnderMouse();
                        if (lt != null && tum != null) {
                            if (!tum.equals(lt)) {
                                Point tP = lt.getPoint(Game.getFloorLevel());
                                if (tP != null) {
                                    Mouse.move(tP);
                                }
                            }
                        } else if (lt != null) {
                            Point tP = lt.getPoint(Game.getFloorLevel());
                            if (tP != null) {
                                Mouse.move(tP);
                            }
                        }
                        if (hasTrapSetup(tile) && !hasTrapItem(tile)) {
                            Task.sleep(650, 880);
                            TrapManager.addTile(tile);
                            cantLayTrap = false;
                            notOurTrap = false;
                            return true;
                        }
                    }
                    Task.sleep(180, 200 + Random.nextInt(0, 80));
                    if (hasTrapSetup(tile) || hasTrapItem(tile)) {
                        TrapManager.addTile(tile);
                        cantLayTrap = false;
                        notOurTrap = false;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean trapNeedsLaid() {
        return org.rsbuddy.tabs.Inventory.getCount(InventoryTrapID()) > 0
                && TrapManager.numberOfLaidTraps() < getTrapAbilityCount();
    }

    private boolean trapNeedsLaid(final int offset) {
        return org.rsbuddy.tabs.Inventory.getCount(InventoryTrapID()) > 0
                && TrapManager.numberOfLaidTraps() + offset < getTrapAbilityCount();
    }

    private boolean trapNeedsLooted() {
        Tile[] ourTiles = TrapManager.getTiles();
        for (Tile tile : ourTiles) {
            if (hasTrap(tile) && !hasTrapSetup(tile) && !trapOther(tile)) {
                return true;
            }
        }
        return false;
    }

    private Tile getLootTrap(final boolean relaying) {
        Tile[] ourTiles = TrapManager.getTiles();
        if (!relaying) {
            for (Tile tile : ourTiles) {
                if (hasTrapItem(tile) && !hasTrap(tile)) {
                    return tile;
                }
            }
        }
        ourTiles = TrapManager.getTiles();
        for (Tile tile : ourTiles) {
            if (hasTrapCaught(tile) && !hasTrapSetup(tile)) {
                return tile;
            }
        }
        ourTiles = TrapManager.getTiles();
        for (Tile tile : ourTiles) {
            if (hasTrap(tile) && !hasTrapSetup(tile)) {
                return tile;
            }
        }
        return null;
    }

    private void lay() {
        if (org.rsbuddy.tabs.Inventory.getCount(InventoryTrapID()) > 0) {
            Tile tile = getFreeTile();
            if (tile != null) {
                if (!layTrap(tile)) {
                    //log("Failed to lay a trap at " + tile.toString() + ".");
                }
            }
        }
    }

    private boolean lootTrap(final Tile tile) {
        if (tile != null) {
            if (hasTrap(tile) && !hasTrapSetup(tile)) {
                GameObject trap = Objects.getTopAt(tile,
                        Objects.TYPE_INTERACTIVE);
                if (trap != null) {
                    String option;
                    int id = trap.getId();
                    if (id == obj_TrapCaught()) {
                        option = "Check";
                    } else if (id == obj_TrapFailed()) {
                        option = "Dismantle";
                    } else {
                        // //log("Wtf bbq? " + id);
                        return false;
                    }
                    int tries = 0;
                    int count = org.rsbuddy.tabs.Inventory
                            .getCount(InventoryTrapID());
                    while (tries <= 3 && trap != null) {
                        if (trap.interact(option)) {
                            break;
                        }
                        tries++;
                    }
                    if (tries <= 3) {
                        long sysTimeout = System.currentTimeMillis()
                                + Random.nextInt(4000, 6000);
                        while (System.currentTimeMillis() < sysTimeout) {
                            if (Players.getLocal().getAnimation() > 0) {
                                break;
                            }
                            Task.sleep(50, 80);
                        }
                        if (notOurTrap) {
                            notOurTrap = false;
                            trapMissing = true;
                            TrapManager.removeTile(tile);
                        }
                        if (Players.getLocal().getAnimation() > 0) {
                            while (Players.getLocal().getAnimation() > 0) {
                                Task.sleep(50, 80);
                                Point p = tile.getPoint(Game.getFloorLevel());
                                if (p != null) {
                                    if (p.distance(
                                            Mouse.getLocation().x,
                                            Mouse.getLocation().y) > 20) {
                                        Mouse.move(p, 15, 15);
                                    }
                                }
                                if (!hasTrap(tile)
                                        && !hasTrapItem(tile)) {
                                    TrapManager.removeTile(tile);
                                    if (option
                                            .equalsIgnoreCase("check")) {
                                        unitsHunted++;
                                    }
                                    return true;
                                }
                            }
                            if (!hasTrap(tile)) {
                                if (hasTrapItem(tile)) {
                                    if (org.rsbuddy.tabs.Inventory
                                            .getCount(InventoryTrapID()) <= count) {
                                        return false;
                                    }
                                }
                                TrapManager.removeTile(tile);
                                if (option.equalsIgnoreCase("check")) {
                                    unitsHunted++;
                                }
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isTrapFallen() {
        Filter<GroundItem> ourFallen = new Filter<GroundItem>() {

            public boolean accept(GroundItem t) {
                Tile[] ourTiles = TrapManager.getTiles();
                java.util.List<Tile> ourList = new ArrayList<Tile>();
                ourList.addAll(Arrays.asList(ourTiles));
                return t != null && ourList.contains(t.getLocation())
                        && hasTrapItem(t.getLocation());
            }

        };
        return GroundItems.getLoaded(ourFallen).length > 0;
    }

    private boolean relayFallenTrap() {
        Filter<GroundItem> ourFallen = new Filter<GroundItem>() {
            public boolean accept(GroundItem t) {
                Tile[] ourTiles = TrapManager.getTiles();
                java.util.List<Tile> ourList = new ArrayList<Tile>();
                ourList.addAll(Arrays.asList(ourTiles));
                return t != null && ourList.contains(t.getLocation())
                        && hasTrapItem(t.getLocation());
            }

        };
        GroundItem groundItem = GroundItems.getNearest(ourFallen);
        if (groundItem != null) {
            final Tile tile = groundItem.getLocation();
            if (tile != null) {
                if (hasTrap(tile) && hasTrapItem(tile)) {
                    return false;
                }
                Filter<Player> playersOnUs = new Filter<Player>() {
                    public boolean accept(Player t) {
                        return t != null && t.getLocation().equals(tile)
                                && !t.equals(Players.getLocal());
                    }
                };
                Player[] chars = Players.getLoaded(playersOnUs);
                if (chars.length > 0) {
                    status = "Someone is under us, waiting...";

                    long timeOut = System.currentTimeMillis()
                            + Random.nextInt(3000, 5000);
                    while (System.currentTimeMillis() <= timeOut) {
                        for (Player check : chars) {
                            if (check != null
                                    && check.getAnimation() == animation_layTrap()) {
                                status = "Noob laid a trap under us...";
                                cantLayTrap = false;
                                return false;
                            }
                        }
                        Task.sleep(50, 80);
                    }
                }
                if (groundItem.interact("Lay")) {
                    long waitAnimation = System.currentTimeMillis()
                            + Random.nextInt(6000, 8300);
                    while (System.currentTimeMillis() < waitAnimation) {
                        if (Players.getLocal().getAnimation() > 0
                                || cantLayTrap) {
                            break;
                        }
                        Mouse.setSpeed(Random.nextInt(2, 8));
                        Tile lt = getLootTrap(true);
                        if (trapNeedsLaid(1)) {
                            lt = getFreeTile(tile);
                        }
                        Tile tum = Tile.getTileUnderMouse();
                        if (lt != null && tum != null) {
                            if (!tum.equals(lt)) {
                                Point tP = lt.getPoint(Game.getFloorLevel());
                                if (tP != null) {
                                    Mouse.move(tP);
                                }
                            }
                        } else if (lt != null) {
                            Point tP = lt.getPoint(Game.getFloorLevel());
                            if (tP != null) {
                                Mouse.move(tP);
                            }
                        }
                        Task.sleep(50, 80);
                    }
                }
                if (notOurTrap) {
                    trapMissing = true;
                    notOurTrap = false;
                    return false;
                }
                if (cantLayTrap) {
                    cantLayTrap = false;
                    notOurTrap = false;
                    return false;
                }
                if (Players.getLocal().getAnimation() > 0) {
                    while (Players.getLocal().getAnimation() > 0) {
                        Mouse.setSpeed(Random.nextInt(2, 6));
                        Tile lt = getLootTrap(true);
                        if (trapNeedsLaid(1)) {
                            lt = getFreeTile(tile);
                        }
                        Tile tum = Tile.getTileUnderMouse();
                        if (lt != null && tum != null) {
                            if (!tum.equals(lt)) {
                                Point tP = lt.getPoint(Game.getFloorLevel());
                                if (tP != null) {
                                    Mouse.move(tP);
                                }
                            }
                        } else if (lt != null) {
                            Point tP = lt.getPoint(Game.getFloorLevel());
                            if (tP != null) {
                                Mouse.move(tP);
                            }
                        }
                         Task.sleep(50, 80);
                        if (hasTrapSetup(tile) && !hasTrapItem(tile)) {
                            Task.sleep(650, 880);
                            cantLayTrap = false;
                            notOurTrap = false;
                            return true;
                        }
                    }
                    Task.sleep(180, 200 + Random.nextInt(0, 80));
                    if (hasTrapSetup(tile) || hasTrapItem(tile)) {
                        cantLayTrap = false;
                        notOurTrap = false;
                        return (hasTrapItem(tile) && hasTrapSetup(tile)) || !hasTrapItem(tile);
                    }
                }
            }
        }
        return false;
    }

    private void handleFallenTrap() {
        if (relayFallenTrap()) {
        } else {
            Tile tile = getFallenTrapTile();
            if (tile != null) {
                if (utils.pickup(tile, InventoryTrapID())) {
                    TrapManager.removeTile(tile);
                } else {
                    //log("Failed to pickup trap...");
                }
            }
        }
    }

    private Tile getFallenTrapTile() {
        Filter<GroundItem> ourFallen = new Filter<GroundItem>() {
            public boolean accept(GroundItem t) {
                Tile[] ourTiles = TrapManager.getTiles();
                java.util.List<Tile> ourList = new ArrayList<Tile>();
                ourList.addAll(Arrays.asList(ourTiles));
                return t != null && ourList.contains(t.getLocation());
            }

        };
        GroundItem[] traps = GroundItems.getLoaded(ourFallen);
        if (traps.length > 0) {
            for (GroundItem verify : traps) {
                if (verify != null && hasTrapItem(verify.getLocation())) {
                    return verify.getLocation();
                }
            }
        }
        return null;
    }

    private void loot() {
        Tile tile = getLootTrap(true);
        if (tile != null) {
            if (!lootTrap(tile)) {
                /* //log("Failed to loot trap at " + tile.toString() + "."); */
            }
        }
    }

    public int getExpGained() {
        return (int) Math.round(expPerUnit() * unitsHunted);
    }

    private boolean hasTrap(final Tile tile) {
        GameObject obj = Objects.getTopAt(tile, Objects.TYPE_INTERACTIVE);
        if (obj != null) {
            int id = obj.getId();
            if (id == obj_TrapCaught() || id == obj_TrapFailed()
                    || id == obj_TrapSetup()) {
                return true;
            }
            for (int testID : obj_TrapOther()) {
                if (id == testID) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean trapOther(final Tile tile) {
        GameObject obj = Objects.getTopAt(tile, Objects.TYPE_INTERACTIVE);
        if (obj != null) {
            int id = obj.getId();
            for (int testID : obj_TrapOther()) {
                if (id == testID) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasTrapSetup(final Tile tile) {
        GameObject obj = Objects.getTopAt(tile, Objects.TYPE_INTERACTIVE);
        if (obj != null) {
            int id = obj.getId();
            if (id == obj_TrapSetup()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTrapCaught(final Tile tile) {
        GameObject obj = Objects.getTopAt(tile, Objects.TYPE_INTERACTIVE);
        if (obj != null) {
            int id = obj.getId();
            if (id == obj_TrapCaught()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasTrapItem(final Tile tile) {
        GroundItem[] items = GroundItems.getAllAt(tile);
        if (items.length > 0) {
            for (GroundItem item : items) {
                if (item != null) {
                    int id = item.getItem().getId();
                    if (id == InventoryTrapID()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void pruneTraps() {
        Tile[] ourTiles = TrapManager.getTiles();
        for (Tile check : ourTiles) {
            if (!hasTrap(check) && !hasTrapItem(check)) {
                TrapManager.removeTile(check);
                GameObject obj = Objects.getTopAt(check,
                        Objects.TYPE_INTERACTIVE);
                if (obj != null) {
                    //log("REMOVED BY ID, REPORT THIS IF REMOVED BY MISTAKE [INCLUDE ID]: " + obj.getId());
                }
            }
        }
    }

    @Override
    public void _paint(Graphics render) {
        final com.rsbuddy.script.wrappers.Component chatBox = Widgets.getComponent(137, 0);
        final int x = chatBox.getAbsLocation().x;
        final int y = chatBox.getAbsLocation().y;
        render.setFont(new Font("Arial", Font.PLAIN, 10));
        final Point report = Widgets.getComponent(751, 15).getAbsLocation();
        if (!Hunter.hiding && Hunter.logo != null) {
            drawGh0sT(render);
            render.drawImage(Hunter.hide, report.x, report.y, null);
            render.drawImage(Hunter.logo, x, y, null);
            drawTextWithShadow(render, Color.WHITE, status, x + 160, y + 21);
            drawTextWithShadow(render, Color.WHITE, " v" + Hunter.manifest.version() + " :: by Pulse", x + 350, y + 21);
            drawTextWithShadow(render, Color.WHITE, Hunter.timeRun.toElapsedString(), x + 125, y + 45);
            drawTextWithShadow(render, Color.WHITE, getTotalProfit() + "k | "
                    + getProfitPerHour() + "k/hr", x + 125, y + 59);
            drawTextWithShadow(render, Color.WHITE, groupDigits(getExpGained()) + " | "
                    + groupDigits(getExpPerHour()) + " xp/hr | " + getUnitsPerHour()
                    + " units/hr", x + 125, y + 71);
            drawTextWithShadow(render, Color.WHITE, getTrapInfo() + " [Lost count - "
                    + getLostCount() + "] | " + " Caught: " + unitsHunted, x + 125, y + 84);
            drawTextWithShadow(render, Color.WHITE, getLevelInfo() + " | TTL: "
                    + getTimeTillLevel() + " (" + groupDigits(getExpTillLevel()) + " exp)", x + 125, y + 98);
            drawProgressBar(render);
            drawMouseTrail(render);
            drawMouse(render);

            Tile[] mark = TrapManager.getTiles();
            render.setFont(new Font("Tahoma", Font.BOLD, 9));
            render.setColor(Color.green);
            for (Tile m : mark) {
                if (m != null) {
                    GameObject topAt = Objects.getTopAt(m);
                    if (topAt != null || hasTrapItem(m)) {
                        int id;
                        try {
                            id = topAt.getId();
                        } catch (NullPointerException e) {
                            id = -1;
                        }
                        String text = "?";
                        if (id == obj_TrapCaught()) {
                            text = "caught";
                        } else if (id == obj_TrapFailed()) {
                            text = "failed";
                        } else if (id == obj_TrapSetup()) {
                            text = "ok";
                        } else if (hasTrapItem(m)) {
                            text = "fallen";
                        }
                        Point d = m.getPoint(Game.getFloorLevel());
                        if (d != null) {
                            render.drawString(text, d.x - 5, d.y + 3);
                        }
                    }
                }
            }
            if (getState(true) == Traps.TrapState.SLEEP) {
                Filter<Npc> huntNPC = new Filter<Npc>() {
                    public boolean accept(Npc arg0) {
                        try {
                            return arg0.getId() == huntingNPCID();
                        } catch (Exception e) {
                            return false;
                        }
                    }
                };
                Tile[] cTs = TrapManager.getTiles();
                for (Tile ttt : cTs) {
                    if (trapOther(ttt)) {
                        closestNPCTrapTile = null;
                        return;
                    }
                }
                Npc[] loaded = Npcs.getLoaded(huntNPC);
                double dist = 99999.0;
                Tile fT = null;
                Tile fTN = null;
                if (loaded != null && loaded.length > 0) {
                    for (Npc n : loaded) {
                        try {
                            Tile t = n.getLocation();
                            for (Tile cT : cTs) {
                                double d = Calculations.distanceBetween(cT, t);
                                if (d < dist) {
                                    dist = d;
                                    fT = cT;
                                    fTN = t;
                                }
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    for (Npc n : loaded) {
                        Model m = n.getModel();
                        Polygon[] mT = m.getTriangles();
                        for (Polygon pG : mT) {
                            if (fT != null && n.getLocation().equals(fTN)) {
                                render.setColor(Color.yellow);
                            } else {
                                render.setColor(Color.gray);
                            }
                            render.setColor(render.getColor().darker());
                            render.drawPolygon(pG);
                            render.setColor(render.getColor().brighter());
                            render.fillPolygon(pG);
                        }
                    }
                    if (fT != null) {
                        GameObject gO = Objects.getTopAt(fT);
                        if (gO != null) {
                            Model m = gO.getModel();
                            Polygon[] mT = m.getTriangles();
                            for (Polygon gP : mT) {
                                render.setColor(Color.red.darker());
                                render.drawPolygon(gP);
                                render.setColor(Color.red.brighter());
                                render.fillPolygon(gP);
                            }
                        }
                    }
                }
                closestNPCTrapTile = fT;
            } else {
                closestNPCTrapTile = null;
            }
        } else {
            render.drawImage(Hunter.show, report.x, report.y, null);
        }
    }

    private int alpha = 255;
    private boolean asc = false;

    public void drawMouse(Graphics g2) {
        Graphics2D g = (Graphics2D) g2;
        g.setRenderingHints(rh);
        if (Hunter.hiding) return;
        final Point p = Mouse.getLocation();
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawLine(p.x - 6, p.y, p.x + 6, p.y);
        g.drawLine(p.x, p.y - 6, p.x, p.y + 6);
    }

    final RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private ArrayList<Point> points = new ArrayList<Point>();

    public void drawMouseTrail(Graphics g2) {
        final Graphics2D g = (Graphics2D) g2;
        g.setRenderingHints(rh);
        if (Hunter.hiding) return;
        final Point p = Mouse.getLocation();
        points.add(p);
        if (points.size() == 50) {
            points.remove(0);
        }
        try {
            int alpha = 180;
            g.setStroke(new BasicStroke(2));
            for (int i = 0; i < points.size(); i++) {
                if (alpha + 10 < 255) {
                    alpha += 10;
                }
                g.setColor(new Color(133, 120, 75, alpha));
                final Point p1 = points.get(i);
                final Point p2 = points.get(i + 1);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        } catch (Exception ignored) {
        }
    }

    public void drawGh0sT(Graphics g) {
        final Player Gh0stifyed = Players.getNearest("Gh0stifyed");
        final Player Damn_Gh0sT = Players.getNearest("Damn Gh0st");
        if (Gh0stifyed == null && Damn_Gh0sT == null) {
            return;
        }
        final Tile location = (Gh0stifyed != null ? Gh0stifyed.getLocation() : Damn_Gh0sT.getLocation());
        if (!location.isOnScreen()) {
            return;
        }
        final Point p = location.getCenterPoint();
        drawTextWithShadow(g, Color.WHITE, "Gh0sT", p.x - 6, p.y - 15);
    }

    public void drawProgressBar(Graphics g) {
        final com.rsbuddy.script.wrappers.Component chatBox = Widgets.getComponent(137, 0);
        final int x = chatBox.getAbsLocation().x;
        final int y = chatBox.getAbsLocation().y;
        g.setColor(Color.BLACK);
        g.fillRect(x + 19, y + 107, 470, 20);
        if (asc) {
            if (alpha + 5 > 255) {
                asc = false;
            } else {
                alpha += 5;
            }
        } else {
            if (alpha - 5 < 100) {
                asc = true;
            } else {
                alpha -= 5;
            }
        }
        g.setColor(new Color(133, 120, 75, alpha));
        final int percent = Skills.getPercentToNextLevel(Skills.HUNTER);
        g.fillRect(x + 21, y + 109, (int) (percent * 4.66), 16);
        g.setColor(new Color(255, 255, 255, 80));
        g.fillRect(x + 20, y + 108, 467, 8);
        drawTextWithShadow(g, Color.WHITE, percent + "% to lvl " + (Skills.getRealLevel(Skills.HUNTER) + 1) + " (" + groupDigits(Skills.getExpToNextLevel(Skills.HUNTER)) + ")", x + 186, y + 120);

    }

    private final DecimalFormat df = new DecimalFormat("###,###,###");

    private String groupDigits(int num) {
        return df.format(num);
    }

    private void drawTextWithShadow(Graphics g, Color color, String text, int x, int y) {
        g.setColor(Color.BLACK);
        g.drawString(text, x + 1, y + 1);
        g.setColor(color);
        g.drawString(text, x, y);
    }

    public String getUnitsPerHour() {
        long oneHour = (60 * 60 * 1000);
        long timeRunning = Hunter.timeRun.getElapsed();
        double r = unitsHunted * oneHour / timeRunning;
        return (int) Math.round(r) + "";
    }

    public String getLostCount() {
        return lostTraps + "";
    }

    public String getTrapInfo() {
        return "[" + TrapManager.numberOfLaidTraps() + "/"
                + getTrapAbilityCount() + "]";
    }

    public int getExpPerHour() {
        long oneHour = (60 * 60 * 1000);
        long timeRunning = Hunter.timeRun.getElapsed();
        double r = getExpGained() * oneHour / timeRunning;
        return (int) Math.round(r);
    }

    public int getProfitPerHour() {
        long oneHour = (60 * 60 * 1000);
        long timeRunning = Hunter.timeRun.getElapsed();
        double r = getTotalProfit() * oneHour / timeRunning;
        return (int) Math.round(r);
    }

    public double getTotalProfit() {
        return (double) Math
                .round(((bird ? (unitsHunted * 7) : unitsHunted) * costPerUnit) / 1000);
    }
    
}