package nz.artefalconry.misc;

import com.rsbuddy.script.wrappers.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 11:52 AM
 * Package: nz.artefalconry.threads;
 */
public interface GameConstants
{
    //IDS
    int[] kebbitIDs = {5098, 5099, 5100};

    int[] falconIDs      = {5094, 5095, 5096};
    int   SPOTTED_KEBBIT = kebbitIDs[0];
    int   DARK_KEBBIT    = kebbitIDs[1];
    int   DASHING_KEBBIT = kebbitIDs[2];
    int   falconerID     = 5093;
    int[] junk           = {526, 10125, 10115, 10127};

    //Tiles
    Tile falconerTile = new Tile(2375, 3604);
}
