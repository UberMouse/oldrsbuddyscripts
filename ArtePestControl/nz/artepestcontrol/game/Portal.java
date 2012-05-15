package nz.artepestcontrol.game;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.wrappers.GameObject;
import com.rsbuddy.script.wrappers.Tile;
import nz.uberutils.helpers.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/13/11
 * Time: 7:53 PM
 * Package: nz.artepestcontrol.game;
 */
public class Portal implements MessageListener
{
    String name;
    Tile location = null;
    Tile gateLoc  = null;
    int     widgetId;
    boolean open;

    Portal(String name, Tile voidLoc) {
        this.name = name;
        if (name.equals("E")) {
            location = new Tile(voidLoc.getX() + 23, voidLoc.getY() - 4);
            gateLoc = new Tile(voidLoc.getX() + 14, voidLoc.getY() + 1);
            widgetId = 14;
        }
        else if (name.equals("W")) {
            location = new Tile(voidLoc.getX() - 27, voidLoc.getY() + 1);
            gateLoc = new Tile(voidLoc.getX() - 13, voidLoc.getY() + 1);
            widgetId = 13;
        }
        else if (name.equals("SE")) {
            location = new Tile(voidLoc.getX() + 14, voidLoc.getY() - 20);
            gateLoc = new Tile(voidLoc.getX(), voidLoc.getY() - 7);
            widgetId = 15;
        }
        else if (name.equals("SW")) {
            location = new Tile(voidLoc.getX() - 11, voidLoc.getY() - 21);
            gateLoc = new Tile(voidLoc.getX(), voidLoc.getY() - 7);
            widgetId = 16;
        }
        Logger.instance()
              .trace("Created new portal at {} with gate at {} distance from player {}",
                      location,
                      gateLoc,
                      Calculations.distanceTo(location));
    }

    public GameObject getGate() {
        return Objects.getTopAt(getGateLocation());
    }

    public Tile getGateLocation() {
        return gateLoc;
    }

    public Tile getLocation() {
        return location;
    }

    public boolean isGateOpen() {
        return getGate() == null || getGate().getId() > 14240;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isAlive() {
        try {
            return Integer.parseInt(Widgets.getComponent(408, widgetId).getText()) > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Portal portal = (Portal) o;

        return !(name != null ? !name.equals(portal.name) : portal.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public String getName() {
        return name;
    }

    public void messageReceived(MessageEvent messageEvent) {
        if (messageEvent.isAutomated()) {
            String txt = messageEvent.getMessage();
            if (txt.contains("western") && name.equals("W"))
                open = true;
            else if (txt.contains("eastern") && name.equals("E"))
                open = true;
            else if (txt.contains("south-eastern") && name.equals("SE"))
                open = true;
            else if (txt.contains("south-western") && name.equals("SW"))
                open = true;
        }
    }
}
