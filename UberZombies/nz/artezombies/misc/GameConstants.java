package nz.artezombies.misc;

import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.wrappers.Area;
import com.rsbuddy.script.wrappers.Tile;
import com.rsbuddy.script.wrappers.TilePath;
import nz.uberutils.methods.UberMovement;
import nz.uberutils.wrappers.LootItem;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 3/27/11
 * Time: 3:22 PM
 * Package: nz.artezombies.threads;
 */
public interface GameConstants
{
    final int[]      ZOMBIEIDS          = {8153, 8152, 8150, 8149, 8151};
    final int[]      ZOMBIERANGEIDS     = {8151};
    final int[]      ZOMBIEALLIDS       = {8153, 8152, 8150, 8149, 8151};
    final int        LADDERID           = 39191;
    final int        TRAPDOORID         = 39190;
    final int        ALTARID            = 2752;
    final int[]      JUNKIDS            = {229, 526, 1424};
    final int        HARTWINID          = 13485;
    final int[]      STAIRIDS           = {24353, 24352, 24350};
    final int        DOORID             = 15536;
    final Tile[]     HARTWINPATH        = {new Tile(3187, 3430),
                                           new Tile(3188, 3430),
                                           new Tile(3189, 3430),
                                           new Tile(3190, 3430),
                                           new Tile(3191, 3430),
                                           new Tile(3192, 3430),
                                           new Tile(3193, 3430),
                                           new Tile(3194, 3430),
                                           new Tile(3195, 3430),
                                           new Tile(3196, 3430),
                                           new Tile(3197, 3430),
                                           new Tile(3198, 3430),
                                           new Tile(3199, 3430),
                                           new Tile(3200, 3430),
                                           new Tile(3201, 3430),
                                           new Tile(3202, 3430),
                                           new Tile(3203, 3429),
                                           new Tile(3204, 3429),
                                           new Tile(3205, 3429),
                                           new Tile(3206, 3429),
                                           new Tile(3207, 3430),
                                           new Tile(3208, 3431),
                                           new Tile(3209, 3432),
                                           new Tile(3210, 3433),
                                           new Tile(3211, 3434),
                                           new Tile(3211, 3435),
                                           new Tile(3212, 3436),
                                           new Tile(3213, 3437),
                                           new Tile(3213, 3438),
                                           new Tile(3213, 3439),
                                           new Tile(3213, 3440),
                                           new Tile(3213, 3441),
                                           new Tile(3213, 3442),
                                           new Tile(3213, 3443),
                                           new Tile(3213, 3444),
                                           new Tile(3213, 3445),
                                           new Tile(3213, 3446),
                                           new Tile(3213, 3447),
                                           new Tile(3213, 3448),
                                           new Tile(3213, 3449),
                                           new Tile(3213, 3450),
                                           new Tile(3213, 3451),
                                           new Tile(3213, 3452),
                                           new Tile(3213, 3453),
                                           new Tile(3213, 3454),
                                           new Tile(3212, 3455),
                                           new Tile(3212, 3456),
                                           new Tile(3212, 3457),
                                           new Tile(3212, 3458),
                                           new Tile(3212, 3459),
                                           new Tile(3212, 3460),
                                           new Tile(3212, 3461),
                                           new Tile(3212, 3462),
                                           new Tile(3212, 3463),
                                           new Tile(3212, 3464),
                                           new Tile(3212, 3465),
                                           new Tile(3212, 3466),
                                           new Tile(3212, 3467),
                                           new Tile(3212, 3468),
                                           new Tile(3212, 3469),
                                           new Tile(3212, 3470),
                                           new Tile(3212, 3471),
                                           new Tile(3212, 3472),
                                           new Tile(3211, 3472),
                                           new Tile(3211, 3473),
                                           new Tile(3210, 3474),
                                           new Tile(3209, 3475),
                                           new Tile(3208, 3476),
                                           new Tile(3208, 3477),
                                           new Tile(3207, 3477),
                                           new Tile(3207, 3478),
                                           new Tile(3206, 3478),
                                           new Tile(3206, 3479),
                                           new Tile(3206, 3480),
                                           new Tile(3206, 3481),
                                           new Tile(3206, 3482),
                                           new Tile(3206, 3483),
                                           new Tile(3206, 3484),
                                           new Tile(3206, 3485),
                                           new Tile(3206, 3486),
                                           new Tile(3206, 3487),
                                           new Tile(3206, 3488),
                                           new Tile(3205, 3489),
                                           new Tile(3205, 3490),
                                           new Tile(3204, 3490),
                                           new Tile(3203, 3491),
                                           new Tile(3203, 3492),
                                           new Tile(3202, 3492)


    };
    final Tile[]     GE_BANK            = {new Tile(3165, 3461),
                                           new Tile(3166, 3461),
                                           new Tile(3167, 3461),
                                           new Tile(3168, 3461),
                                           new Tile(3169, 3460),
                                           new Tile(3170, 3459),
                                           new Tile(3171, 3458),
                                           new Tile(3172, 3458),
                                           new Tile(3172, 3457),
                                           new Tile(3173, 3456),
                                           new Tile(3174, 3455),
                                           new Tile(3175, 3455),
                                           new Tile(3175, 3454),
                                           new Tile(3176, 3453),
                                           new Tile(3177, 3452),
                                           new Tile(3178, 3451),
                                           new Tile(3179, 3451),
                                           new Tile(3180, 3451),
                                           new Tile(3181, 3451),
                                           new Tile(3182, 3451),
                                           new Tile(3183, 3450),
                                           new Tile(3184, 3449),
                                           new Tile(3185, 3448),
                                           new Tile(3186, 3447),
                                           new Tile(3186, 3446),
                                           new Tile(3186, 3445),
                                           new Tile(3185, 3444)};
    final TilePath   BANK_PATH          = Walking.newTilePath(UberMovement.reversePath(HARTWINPATH));
    final TilePath   BANK_PATH_REV      = Walking.newTilePath(HARTWINPATH);
    final TilePath   GE_BANK_PATH       = Walking.newTilePath(GE_BANK);
    final Area       BANK_AREA          = new Area(3194, 3447, 3179, 3431);
    final Tile       DOOR_TILE          = new Tile(3203, 3493);
    // Loot
    final LootItem[] LOOTIDS            = {new LootItem(995),
                                           new LootItem(18778),
                                           new LootItem(961),
                                           new LootItem(8779),
                                           new LootItem(7937),
                                           new LootItem(561),
                                           new LootItem(564),
                                           new LootItem(565),
                                           new LootItem(8781),};
    final LootItem[] PRIORITYLOOTIDS    = {new LootItem(985),
                                           new LootItem(987),
                                           new LootItem(1163),
                                           new LootItem(1247),
                                           new LootItem(1617),
                                           new LootItem(1249),
                                           new LootItem(2366),
                                           new LootItem(18757),
                                           new LootItem(18778),
                                           new LootItem(6798),
                                           new LootItem(6799),
                                           new LootItem(6800),
                                           new LootItem(6801),
                                           new LootItem(6802),
                                           new LootItem(6803),
                                           new LootItem(6804),
                                           new LootItem(6805),
                                           new LootItem(6806),
                                           new LootItem(6807),
                                           new LootItem(6808),
                                           new LootItem(15355),
                                           new LootItem(15356),
                                           new LootItem(15357),
                                           new LootItem(15358),
                                           new LootItem(15359),
                                           new LootItem(12163),
                                           new LootItem(12160),
                                           new LootItem(12158),
                                           new LootItem(12527),
                                           new LootItem(12159),
                                           new LootItem(18018)};
    final String     VOID_SPINNER_POUCH = "Void spinner pouch";
    final String     BUNYIP_POUCH       = "Bunyip pouch";
}