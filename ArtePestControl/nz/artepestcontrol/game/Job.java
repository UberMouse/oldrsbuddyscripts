package nz.artepestcontrol.game;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/13/11
 * Time: 3:29 PM
 * Package: nz.artepestcontrol.game;
 */
public enum Job
{
    STR("Strength x100", 103, 100, 1),
    ATT("Attack x100", 88, 100, 1),
    DEF("Defence x 100", 119, 100, 1),
    CON("Constitution x100", 135, 100, 1),
    MAGE("Magic x100", 167, 100, 1),
    RANGE("Ranged x100", 151, 100, 1),
    PRAYER("Prayer x100", 183, 100, 1),
    MELEE_HELM("Void knight melee helm", 290, 200, 2),
    RANGER_HELM("Void knight ranger helm", 195, 200, 2),
    MAGE_HELM("Void knight mage helm", 207, 200, 2),
    TOP("Void knight top", 219, 250, 2),
    ROBES("Void knight robes", 231, 250, 2),
    MACE("Void knight mace", 254, 250, 2),
    GLOVES("Void knight gloves", 243, 150, 2);
    String name;
    int subID, points, tab;

    Job(String name, int subID, int points, int tab) {
        this.name = name;
        this.subID = subID;
        this.points = points;
        this.tab = tab;
    }

    public int points() {
        return points;
    }

    public int tab() {
        return tab;
    }

    public int subID() {
        return subID;
    }

    public String getName() {
        return name;
    }
}
