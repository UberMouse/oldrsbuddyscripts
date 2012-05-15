package nz.artepestcontrol.misc;

import com.rsbuddy.script.wrappers.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/9/11
 * Time: 3:23 PM
 * Package: nz.uberbase.threads;
 */
public interface GameConstants
{
    int[] LANDER_GANGPLANKS = {14315, 25631, 25632};
    int[] LANDER_LADDERS = {14314, 25629, 25630};
    Tile[] LANDER_TILES = {new Tile(2657, 2639), new Tile(2644, 2644), new Tile(2638, 2653)};
    String[] LANDER_TEXT = {"Novice", "Intermediate", "Veteran"};
    int[] COMMENDATION_MULTIPLIERS = {2, 3, 4};

    int[] VOID_KNIGHT = {3782, 3783, 3784, 3785, 7203};
    int[] LANDER_FLOOR = {14256, 14257, 14270, 14271, 14272, 14273};
    int[] EXCHANGE_NPC = {3788, 3789};
    int[] PORTALS = {6151, 6156, 6153, 6150, 6152};

    //Exchange interfaces
    int EXCHANGE_MAIN_IFACE = 1011;
    int TAB_TWO = 28;
    int CONFIRM = 383;
    int CLOSE = 51;
    int COMMENDATIONS = 62;

    Tile COMMENDATION_EXCHANGE = new Tile(2659, 2649);
}
