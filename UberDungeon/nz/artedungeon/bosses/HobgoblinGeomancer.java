package nz.artedungeon.bosses;

import com.rsbuddy.script.util.Random;
import nz.artedungeon.common.Plugin;
import nz.artedungeon.dungeon.Enemy;
import nz.artedungeon.dungeon.MyPlayer;
import nz.uberutils.methods.UberMovement;
import nz.uberutils.methods.UberNpcs;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/24/11
 * Time: 5:59 PM
 * Package: nz.artedungeon.bosses;
 */
public class HobgoblinGeomancer extends Plugin
{
    private static enum State
    {
        EAT("Eating"), ATTACK("Attacking");

        private State(String name) {
            get = name;
        }

        private final String get;

        public String toString() {
            return get;
        }
    }

    @Override
    public boolean isValid() {
        return UberNpcs.getNearest(".*goblin geoman.*") != null;
    }

    @Override
    public String getStatus() {
        return "Killing Hobgoblin Geomancer: " + getState().toString();
    }

    @Override
    public String getAuthor() {
        return "UberMouse";
    }

    @Override
    public String getName() {
        return "Hobgoblin Geomancer";
    }

    @Override
    public int loop() {
        switch (getState()) {
            case EAT:
                MyPlayer.eat();
                break;
            case ATTACK:
                attack();
                break;
        }
        return Random.nextInt(500, 600);
    }

    private void attack() {
        if (MyPlayer.interacting() == null && !MyPlayer.inCombat()) {
            Enemy.setNPC(MyPlayer.currentRoom().getNearestNpc(".*goblin geoman.*"));
            UberMovement.turnTo(Enemy.getNPC());
            Enemy.interact("attack");
        }
    }

    private State getState() {
        if (MyPlayer.needToEat() && MyPlayer.hasFood())
            return State.EAT;
        else
            return State.ATTACK;
    }
}
