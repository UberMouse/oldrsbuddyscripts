package nz.ubermouse.artehunter.traps;

import com.rsbuddy.script.wrappers.Tile;

import java.util.ArrayList;
import java.util.List;

public class TrapManager {
    private static List<Tile> ourTiles = new ArrayList<Tile>();

    public static void addTile(final Tile tile) {
        if (!ourTiles.contains(tile)) {
            ourTiles.add(tile);
        }
    }

    public static void removeTile(final Tile tile) {
        ourTiles.remove(tile);
    }

    public static Tile[] getTiles() {
        return ourTiles.toArray(new Tile[ourTiles.size()]);
    }

    public static int numberOfLaidTraps() {
        return ourTiles.size();
    }
}