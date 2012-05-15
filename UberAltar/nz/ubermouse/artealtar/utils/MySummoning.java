package nz.ubermouse.artealtar.utils;

import com.rsbuddy.script.methods.Npcs;
import com.rsbuddy.script.wrappers.Npc;
import nz.uberutils.helpers.UberPlayer;
import org.rsbuddy.tabs.Summoning;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 3:41 PM
 * Package: nz.ubermouse.artealtar.utils;
 */
public class MySummoning
{
    /**
     * Finds your current summoned NPC.
     *
     * @return your current familiar NPC.
     */
    public static Npc getSummonedNpc() {
        for (Npc npc : Npcs.getLoaded()) {
            for (Summoning.Familiar f : Summoning.Familiar.values()) {
                if (f != null && npc.getName().equals(f.getName()) && npc.getInteracting().equals(UberPlayer.get())) {
                    return npc;
                }
            }
        }
        return null;
    }

    /**
     * Finds your current summoned familiar.
     *
     * @return your current familiar
     */
    public static Summoning.Familiar getSummonedFamiliar() {
        for (Npc npc : Npcs.getLoaded()) {
            for (Summoning.Familiar f : Summoning.Familiar.values()) {
                if (f != null && npc.getName().equals(f.getName()) && npc.getInteracting().equals(UberPlayer.get())) {
                    return f;
                }
            }
        }
        return null;
    }
}
