package nz.uberutils.wrappers;

import nz.uberutils.methods.UberBanking;
import nz.uberutils.methods.UberInventory;
import org.rsbuddy.tabs.Inventory;
import org.rsbuddy.widgets.Bank;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/8/11
 * Time: 7:05 PM
 * Package: nz.uberutils.wrappers;
 */
public class BankItem implements Serializable
{
    private final int    id;
    private final String name;
    private final int    quantity;

    public BankItem(int id, int quantity) {
        this(id, null, quantity);
    }

    public BankItem(String name, int quantity) {
        this(-1, name, quantity);
    }

    public BankItem(int id, String name, int quantity) {
        this.id = id;
        this.name = (name != null) ? name.trim() : null;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "BankItem[id=" + id + "&name=" + name + "&quantity=" + quantity;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean bankContains() {
        if (!Bank.isOpen())
            return false;
        return UberBanking.getItem(name) != null || Bank.getItem(id) != null;
    }

    public boolean inventoryContains() {
        return  UberInventory.getItem(name) != null || UberInventory.getItem(id) != null;
    }

    public int inventoryCount() {
        if (id != -1)
            return Inventory.getCount(true, id);
        else
            return UberInventory.getCount(name, true);
    }
}
