package nz.ubermouse.artealtar.data;

import com.rsbuddy.script.wrappers.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:24 PM
 * Package: nz.ubermouse.artealtar.data;
 */ // Constants
public interface GameConstants
{
    // Tiles
    Tile FALADOR_BANK_TILE     = new Tile(2946, 3370);
    Tile DAMONHEIM_BANK_TILE   = new Tile(3449, 3719);
    Tile CASTLE_WARS_BANK_TILE = new Tile(2444, 3083);
    Tile YANILLE_HOUSE_PORTAL  = new Tile(2544, 3093);
    Tile YANILLE_BANK_TILE     = new Tile(2612, 3092);
    Tile EDGE_BANK_TILE        = new Tile(3094, 3941);

    // Paths
    Tile[] FALADOR_BANK_PATH     = {new Tile(2957, 3382), new Tile(2949, 3375), new Tile(2946, 3369)};
    Tile[] DAMONHEIM_BANK_PATH   = {new Tile(3449, 3706), new Tile(3448, 3717)};
    Tile[] CASTLE_WARS_BANK_PATH = {new Tile(2443, 3084)};
    Tile[] YANILLE_BANK_HOUSE    = {new Tile(2609, 3092),
                                    new Tile(2601, 3098),
                                    new Tile(2591, 3098),
                                    new Tile(2580, 3095),
                                    new Tile(2571, 3089),
                                    new Tile(2560, 3089),
                                    new Tile(2551, 3092),
                                    new Tile(2544, 3094)};
    Tile[] EDGE_BANK_PATH        = {new Tile(3090, 3491)};
    // Areas

    // ID's/Names
    int[]  BURNERS               = {13212, 13210};
    int[]  LITBURNERS            = {13213};
    int    RING_OF_DUELING_ID    = 2552;
    int    MARENTILL_ID          = 251;
    int    HOUSE_TABLET_ID       = 8013;
    int    OFFER_INTERFACE_ID    = 905;
    int    OFFER_INTERFACE_SUBID = 14;
    int    BURNER_LIGHT_ANIM     = 3987;
    int[]  PORTAL_ENTRANCEEXT_ID = {15478, 15479, 15480, 15481, 15482, 15483};
    int    PORTAL_ENTRACE_ID     = 13405;
    int    KINSHIP_RING_ID       = 15707;
    int    ALTAR_ID              = 13197;
    int[]  BANKER_IDS            = {44,
                                    45,
                                    494,
                                    495,
                                    499,
                                    958,
                                    1036,
                                    2271,
                                    2354,
                                    2355,
                                    3824,
                                    5488,
                                    5901,
                                    5912,
                                    5913,
                                    6362,
                                    6532,
                                    6533,
                                    6534,
                                    6535,
                                    7605,
                                    8948,
                                    9710,
                                    14367,
                                    2213,
                                    4483,
                                    6084,
                                    11402,
                                    11758,
                                    12759,
                                    14367,
                                    19230,
                                    24914,
                                    25808,
                                    26972,
                                    27663,
                                    29085,
                                    34752,
                                    35647,
                                    4483,
                                    12308,
                                    21301,
                                    27663,
                                    42192};
    int[]  OBELISK_IDS           = {29954};
    int    MOUNTED_GLORY         = 13523;
    int[]  DOORS_IDS             = {13101, 13100};
    int[]  boneIDs               = {526, 532, 536, 18832};
    int[]  pouchIDs              = {12007, 12031, 12093};
    Tile[] obeliskTiles          = {new Tile(2462, 3087), new Tile(3128, 3516)};
    int    SUMMONING_POTION      = 12140;
}
