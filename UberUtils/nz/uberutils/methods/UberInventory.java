package nz.uberutils.methods;

import com.rsbuddy.script.methods.Mouse;
import com.rsbuddy.script.wrappers.Item;
import com.rsbuddy.script.wrappers.Npc;
import org.rsbuddy.tabs.Inventory;

import java.util.ArrayList;


public class UberInventory
{
    public static Item[] getItems(boolean cached) {
        if (getCount() <= 0)
            return new Item[0];
        ArrayList<Item> items = new ArrayList<Item>();
        int idx = 0;
        for (Item item : (cached) ? Inventory.getCachedItems() : Inventory.getItems()) {
            if (item != null && item.getId() > 0)
                items.add(item);
        }
        return items.toArray(new Item[items.size()]);
    }

    public static Item[] getItems() {
        return getItems(false);
    }

    public static int getCount() {
        int count = 0;
        for (Item item : Inventory.getItems()) {
            if (item.getId() > 0)
                count++;
        }
        return count;
    }

    public static int getCount(final String name, boolean includeStacks) {
        int count = 0;
        for (Item i : getItems()) {
            if (i.getName().toLowerCase().contains(name.toLowerCase()) || i.getName().equalsIgnoreCase(name))
                count += (includeStacks) ? i.getStackSize() : 1;
        }
        return count;
    }

    public static int getCount(final String name) {
        return getCount(name, false);
    }

    public static int getCount(final int id) {
        int count = 0;
        for (Item i : getItems()) {
            if (i.getId() == id)
                count++;
        }
        return count;
    }

    public static boolean isFull() {
        return getCount() == 28;
    }

    public static Item getItem(int id) {
        return Inventory.getItem(id);
    }

    /**
     * Check if inventory contains item
     *
     * @param id     id of item to check for
     * @param cached use cached inventory
     * @return true if item in inventory
     */
    public static boolean contains(int id, boolean cached) {
        for (Item item : getItems(cached)) {
            if (item.getId() == id)
                return true;
        }
        return false;
    }

    /**
     * Check if inventory contains item
     *
     * @param id id of item to check for
     * @return true if item in inventory
     */
    public static boolean contains(int id) {
        return contains(id, false);
    }

    /**
     * Check if inventory contains item
     *
     * @param name   name of item to check for
     * @param cached use cached inventory
     * @return true if item in inventory
     */
    public static boolean contains(String name, boolean cached) {
        for (Item item : getItems(cached)) {
            if (name != null && item.getName().toLowerCase().contains(name.toLowerCase()))
                return true;
        }
        return false;
    }

    /**
     * Check if inventory contains item
     *
     * @param name name of item to check for
     * @return true if item in inventory
     */
    public static boolean contains(String name) {
        return contains(name, false);
    }

    /**
     * Get item in inventory
     *
     * @param name name of item to get
     * @return Item if found or null if no item was found
     */
    public static Item getItem(String name) {
        for (Item i : getItems()) {
            if (i != null && name != null && (i.getName().toLowerCase().contains(name.toLowerCase()) || i.getName().equalsIgnoreCase(name)))
                return i;
        }
        return null;
    }

    public static void useItem(Item item, Npc npc) {
        item.interact("use");
        Mouse.click(npc.getModel().getNextPoint(), true);
    }
}
