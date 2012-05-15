package nz.uberutils.wrappers;

import com.rsbuddy.script.methods.Menu;
import com.rsbuddy.script.methods.Mouse;
import com.rsbuddy.script.wrappers.Locatable;
import com.rsbuddy.script.wrappers.Npc;
import com.rsbuddy.script.wrappers.Tile;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 7/20/11
 * Time: 12:53 PM
 * Package: nz.uberutils.wrappers;
 */
public class UberNpc implements Locatable
{
    private final Npc npc;

    public UberNpc(Npc npc) {
        this.npc = npc;
    }

    public boolean interact(String action) {
        Mouse.move(npc);
        if (Menu.contains(npc.getName()) && !Menu.contains(action))
            return false;
        else if (Menu.isOpen() && !Menu.contains(action))
            Mouse.moveRandomly(1000);
        if (Menu.getIndex(action) != 0)
            Mouse.click(false);
        return Menu.contains(action) && Menu.click(action);
    }

    public int getHealth() {
        return npc.getHpPercent();
    }

    public String getName() {
        return npc.getName();
    }

    public int getId() {
        return npc.getId();
    }

    public Tile getLocation() {
        return npc.getLocation();
    }

    public boolean isNull() {
        return npc == null;
    }

    public Npc getNpc() {
        return npc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UberNpc uberNpc = (UberNpc) o;

        if (npc != null ? !npc.equals(uberNpc.getNpc()) : uberNpc.getNpc() != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return npc != null ? npc.hashCode() : 0;
    }
}
