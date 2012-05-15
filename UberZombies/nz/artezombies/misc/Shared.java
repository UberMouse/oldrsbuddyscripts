package nz.artezombies.misc;

import nz.uberutils.wrappers.BankItem;
import nz.uberutils.wrappers.LootItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/8/11
 * Time: 8:15 PM
 * Package: nz.artezombies.threads;
 */
public class Shared implements Serializable
{
    private static Shared instance = null;

    public static Shared instance() {
        if(instance == null)
            instance = new Shared();
        return instance;
    }

    public static void setInstance(Shared instance) {
        Shared.instance = instance;
    }

    public ArrayList<LootItem> loot = new ArrayList<LootItem>();
    public ArrayList<LootItem> priorityLoot = new ArrayList<LootItem>();
    public ArrayList<BankItem> bankItems = new ArrayList<BankItem>();
}
