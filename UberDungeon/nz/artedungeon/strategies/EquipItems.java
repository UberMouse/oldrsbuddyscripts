package nz.artedungeon.strategies;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Mouse;
import com.rsbuddy.script.wrappers.Item;
import nz.artedungeon.DungeonMain;
import nz.artedungeon.common.Strategy;
import nz.artedungeon.dungeon.Equipable;
import nz.artedungeon.dungeon.ItemHandler;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberEquipment;
import nz.uberutils.methods.UberInventory;


public class EquipItems extends Strategy
{
    private boolean openEquip = false;

    /**
     * Instantiates a new UberEquipment items.
     *
     * @param parent instance of main script
     */
    public EquipItems(DungeonMain parent) {
        super(parent);
    }

    enum ITEM_TYPES
    {
        HELM, CHEST, LEGS, FEET, HAND, WEAPON, AMMO
    }

    /*
      * (non-Javadoc)
      *
      * @see Common.Strategy#execute()
      */

    public int execute() {
        for (Item item : UberInventory.getItems()) {
            Equipable equip = new Equipable(item.getName());
            if (equip.getLocation() != Equipable.Location.NOVALUE) {
                switch (equip.getLocation()) {
                    case HELM:
                        doItem(item, UberEquipment.HELMET);
                        break;
                    case CHEST:
                        doItem(item, UberEquipment.BODY);
                        break;
                    case LEGS:
                        doItem(item, UberEquipment.LEGS);
                        break;
                    case FEET:
                        doItem(item, UberEquipment.FEET);
                        break;
                    case HANDS:
                        doItem(item, UberEquipment.HANDS);
                        break;
                    case WEAPON:
                        doItem(item, UberEquipment.WEAPON);
                        break;
                    case OFFHAND:
                        doItem(item, UberEquipment.SHIELD);
                        break;
                    case AMMO:
                        doItem(item, UberEquipment.AMMO);
                        break;
                }
            }
        }
        return random(400, 600);
    }

    /**
     * Does item equip.
     *
     * @param item           the item
     * @param EquipmentIndex the Equipment index
     */
    private void doItem(Item item, int EquipmentIndex) {
        Equipable toEquip = new Equipable(item.getName());
        if (toEquip.getType() == Equipable.Type.TOOL)
            return;
        if (ItemHandler.shouldEquip(toEquip, toEquip.getEquipmentIndex())) {
            //            if (ItemHandler.isBound(EquipmentIndex) && toEquip.getEquipmentIndex() != UberEquipment.AMMO) {

            //                ItemHandler.unBind(EquipmentIndex);
            //            }
            //            if ((EquipmentIndex == UberEquipment.WEAPON ||
            //                 EquipmentIndex == UberEquipment.AMMO) && !item.getName().contains("(b)")) {

            //                ItemHandler.bind(item.getId());
            //                sleep(random(500, 760));
            //            }

            item.interact("wear");
            Game.openTab(Game.TAB_EQUIPMENT);
            Utils.sleep(Utils.random(25, 50));
            Game.openTab(Game.TAB_INVENTORY);
            Mouse.move(item.getComponent().getAbsLocation());
            Mouse.click(true);
        }
        else if (!item.getName().contains("(b)")) {

            item.interact("rop");
        }
    }

    /*
      * (non-Javadoc)
      *
      * @see Common.Strategy#isValid()
      */

    public boolean isValid() {
        for (Item item : UberInventory.getItems()) {
            if (item != null) {
                Equipable check = new Equipable(item.getName());
                if (check.getLocation() != Equipable.Location.NOVALUE && check.getType() != Equipable.Type.TOOL) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
      * (non-Javadoc)
      *
      * @see Common.Strategy#reset()
      */

    public void reset() {

    }

    public String getStatus() {
        return "Upgrading Armour/Weapons";
    }

}
