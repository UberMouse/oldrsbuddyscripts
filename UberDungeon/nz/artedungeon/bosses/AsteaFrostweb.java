package nz.artedungeon.bosses;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.Npc;
import nz.artedungeon.common.Plugin;
import nz.artedungeon.dungeon.EnemyDef;
import nz.artedungeon.dungeon.MyPlayer;
import nz.artedungeon.misc.GameConstants;
import nz.artedungeon.utils.MyCombat;
import nz.uberutils.methods.UberMovement;


public class AsteaFrostweb extends Plugin
{
    public boolean isValid() {
        return Npcs.getNearest(GameConstants.ASTEA_FROSTWEB) != null;
    }

    public int loop() {
        Npc frostWeb = Npcs.getNearest(GameConstants.ASTEA_FROSTWEB);
        MyCombat.doPrayerFor(new EnemyDef(frostWeb));
        if (MyPlayer.hp() < 60)
            MyPlayer.eat();
        if (frostWeb.isOnScreen()) {
            frostWeb.interact("Attack");
            sleep(Random.nextInt(700, 1500));
        }
        else
            UberMovement.turnTo(frostWeb);
        return Random.nextInt(500, 800);
    }

    public String getStatus() {
        return "Killing " + getName();
    }

    public String getAuthor() {
        return "UberMouse";
    }

    public String getName() {
        return "Astea Frostweb";
    }
}
