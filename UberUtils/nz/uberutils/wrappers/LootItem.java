package nz.uberutils.wrappers;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/8/11
 * Time: 7:04 PM
 * Package: nz.uberutils.wrappers;
 */
public class LootItem implements Serializable
{
    private final int id;
    private final String name;
    private final boolean lootInCombat;

    public LootItem(int id) {
        this(id, null, false);
    }

    public LootItem(String name) {
        this(-1, name, false);
    }

    public LootItem(int id, boolean lootInCombat) {
        this(id, null, lootInCombat);
    }

    public LootItem(String name, boolean lootInCombat) {
        this(-1, name, lootInCombat);
    }

    public LootItem(int id, String name, boolean lootInCombat) {
        this.id = id;
        this.name = name;
        this.lootInCombat = lootInCombat;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean lootInCombat() {
        return lootInCombat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LootItem lootItem = (LootItem) o;

        if (id != lootItem.id)
            return false;
        if (lootInCombat != lootItem.lootInCombat)
            return false;
        if (name != null ? !name.equals(lootItem.name) : lootItem.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (lootInCombat ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LootItem[id=" + id + "&name=" + name + "&lootInCombat=" + lootInCombat;
    }
}
