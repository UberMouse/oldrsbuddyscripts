package nz.uberutils.methods;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.wrappers.Npc;
import com.rsbuddy.script.wrappers.Tile;
import nz.uberutils.wrappers.UberNpc;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/21/11
 * Time: 9:15 PM
 * Package: nz.uberutils.methods;
 */
public class UberNpcs
{
    /**
     * Get Npc at Tile
     * @param tile tile to find Npc on
     * @return Npc if Npc found on tile, else null
     */
    public static UberNpc getTopAt(final Tile tile) {
        return sanitizeNpc(Npcs.getNearest(new Filter<Npc>()
        {
            public boolean accept(Npc npc) {
                return npc.getLocation().equals(tile);
            }
        }));
    }

    /**
     * Gets nearest Npc to Player (With regex or .contains)
     *
     * @param names the names of the <tt>Npc</tt> to search for
     * @return <tt>Npc</tt>, if one is found, else <tt>null</tt>
     */
    public static UberNpc getNearest(final String... names) {
        return sanitizeNpc(Npcs.getNearest(new Filter<Npc>()
        {
            public boolean accept(Npc Npc) {
                if (Npc == null || Npc.getHpPercent() < 1)
                    return false;
                for (String name : names) {
                    if ((Npc.getName().toLowerCase().matches(name.toLowerCase()) ||
                         Npc.getName().toLowerCase().contains(name)))
                        return true;
                }
                return false;
            }
        }));
    }

    /**
     * Gets nearest Npc to Player
     *
     * @param ids the ids of the <tt>Npc</tt> to search for
     * @return <tt>Npc</tt>, if one is found, else <tt>null</tt>
     */
    public static UberNpc getNearest(final int... ids) {
        return sanitizeNpc(Npcs.getNearest(new Filter<Npc>()
        {
            public boolean accept(Npc Npc) {
                if (Npc == null || Npc.getHpPercent() < 1)
                    return false;
                for (int id : ids) {
                    if (Npc.getId() == id)
                        return true;
                }
                return false;
            }
        }));
    }

    public static UberNpc getNearest(final Filter<Npc> filter) {
        return sanitizeNpc(Npcs.getNearest(filter));
    }

    /**
     * Sanitize null inputs properly when creating new instance of UberNpc based on Npc reference
     * @param n Npc refernce to check
     * @return MyNpc if Npc != null, else null
     */
    public static UberNpc sanitizeNpc(Npc n) {
        if (n != null)
            return new UberNpc(n);
        return null;
    }
}
