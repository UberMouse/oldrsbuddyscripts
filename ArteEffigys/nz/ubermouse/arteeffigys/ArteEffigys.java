package nz.ubermouse.arteeffigys;

import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.wrappers.Item;
import nz.uberutils.helpers.Utils;
import nz.uberutils.methods.UberInventory;
import nz.uberutils.paint.PaintController;
import nz.uberutils.paint.components.PFancyButton;
import org.rsbuddy.tabs.Inventory;
import org.rsbuddy.widgets.Bank;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/31/11
 * Time: 6:12 PM
 * Package: nz.ubermouse.arteeffigys;
 */
@Manifest(authors = "UberMouse",
          name = "ArteEffigys",
          description = "Paints information about effigys on your inventory",
          version = 1.0,
          keywords = "Effigys",
          website = "http://artebots.com")
public class ArteEffigys extends ActiveScript implements PaintListener, MouseListener, MouseMotionListener, KeyListener
{
    public static ArrayList<Effigy> effigies = new ArrayList<Effigy>(28);
    private static MouseEvent lastMouse;
    private static boolean ctrlPressed = false;

    public boolean onStart() {
        PaintController.clearComps();
        PaintController.addComponent(new PFancyButton(418, 345, "Reload inventory")
        {
            public void onPress() {
                ArteEffigys.effigies.clear();
            }
        });
        return true;
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn() || Bank.isOpen())
            return 100;
        Mouse.setSpeed(Utils.random(1, 2));
        Environment.setUserInput(Environment.INPUT_KEYBOARD);
        try {
            if (effigies.size() < 28) {
                int index = effigies.size();
                Item effigy = Inventory.getItemAt(index);
                if (effigy == null || !effigy.getName().toLowerCase().contains("effigy")) {
                    effigies.add(null);
                    return 100;
                }
                checkEffigy(effigy);
            }
            else {
                for (int i = 0; i < effigies.size(); i++) {
                    Effigy e = effigies.get(i);
                    if (e == null &&
                        Inventory.getItemAt(i) != null &&
                        Inventory.getItemAt(i).getName().contains("effigy")) {
                        checkEffigy(Inventory.getItemAt(i), i);
                        return 100;
                    }
                }
                for (int i = 0; i < effigies.size(); i++) {
                    Effigy e = effigies.get(i);
                    if (e != null) {
                        boolean cando = false;
                        int skillIndex = 0;
                        for (skillIndex = 0; skillIndex < e.skills.length; skillIndex++)
                            if (Skills.getCurrentLevel(e.skills[skillIndex]) >= e.type.level) {
                                cando = true;
                                break;
                            }
                        if (cando)
                            doEffigy(i, ++skillIndex);
                    }
                }
                if (!ctrlPressed)
                    Environment.setUserInput(Environment.INPUT_KEYBOARD | Environment.INPUT_MOUSE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Utils.random(400, 500);
    }

    public void doEffigy(int index, int skillIndex) {
        Item effigy = Inventory.getItemAt(index);
        if (effigy != null) {
            if (effigy.interact("Investigate")) {
                for (int i = 0; i <= 15 && Utils.getWidgetWithText("As you inspect the") == null; i++)
                    Task.sleep(100);
                for (int i = 0; i <= 15 && !Widgets.canContinue(); i++)
                    Task.sleep(100);
                Widgets.clickContinue();
                for (int i = 0; i <= 15 && Utils.getWidgetWithText("images from your e") == null; i++)
                    Task.sleep(100);
                Widgets.clickContinue();
                for (int i = 0; i <= 25 && Utils.getWidgetWithText("Which images") == null; i++)
                    Task.sleep(100);
                if (Widgets.getComponent(236, skillIndex) != null && Widgets.getComponent(236, skillIndex).isValid()) {
                    if (Widgets.getComponent(236, skillIndex).click()) {
                        for (int i = 0; i <= 15 && !Widgets.canContinue(); i++)
                            Task.sleep(100);
                        if (Widgets.canContinue())
                            if (Widgets.clickContinue())
                                effigies.set(index, null);
                    }
                }
            }
        }
    }

    public void checkEffigy(Item effigy) {
        checkEffigy(effigy, -1);
    }

    public void checkEffigy(Item effigy, int index) {
        if (effigy.interact("Investigate")) {
            for (int i = 0; i <= 15 && Utils.getWidgetWithText("As you inspect the") == null; i++)
                Task.sleep(100);
            for (int i = 0; i <= 15 && !Widgets.canContinue(); i++)
                Task.sleep(100);
            Widgets.clickContinue();
            for (int i = 0; i <= 15 && Utils.getWidgetWithText("images from your e") == null; i++)
                Task.sleep(100);
            Effigy e = null;
            if (Utils.getWidgetWithText("life and cultivation") != null)
                e = new Effigy(effigy, new int[]{Skills.FISHING, Skills.FARMING});
            else if (Utils.getWidgetWithText("fire and preparation") != null)
                e = new Effigy(effigy, new int[]{Skills.FIREMAKING, Skills.COOKING});
            else if (Utils.getWidgetWithText("binding essence and<br>spirits") != null)
                e = new Effigy(effigy, new int[]{Skills.RUNECRAFTING, Skills.SUMMONING});
            else if (Utils.getWidgetWithText("deftness and precision") != null)
                e = new Effigy(effigy, new int[]{Skills.CRAFTING, Skills.AGILITY});
            else if (Utils.getWidgetWithText("buildings and security") != null)
                e = new Effigy(effigy, new int[]{Skills.CONSTRUCTION, Skills.THIEVING});
            else if (Utils.getWidgetWithText("metalwork and<br>minerals") != null)
                e = new Effigy(effigy, new int[]{Skills.SMITHING, Skills.MINING});
            else if (Utils.getWidgetWithText("flora and fauna") != null)
                e = new Effigy(effigy, new int[]{Skills.HUNTER, Skills.HERBLORE});
            else if (Utils.getWidgetWithText("lumber and<br>woodworking") != null)
                e = new Effigy(effigy, new int[]{Skills.FLETCHING, Skills.WOODCUTTING});
            if (index == -1)
                effigies.add(e);
            else
                effigies.set(index, e);
        }
        else
            effigies.add(null);
    }

    public void onRepaint(Graphics graphics) {
        for (Effigy e : effigies) {
            if (e == null)
                continue;
            e.paintRect((Graphics2D) graphics);
        }
        for (Effigy e : effigies) {
            if (e == null)
                continue;
            e.paintText((Graphics2D) graphics);
        }
        PaintController.onRepaint(graphics);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (!isPaused()) {
            PaintController.mousePressed(mouseEvent);
            if (ctrlPressed) {
                for (Item i : UberInventory.getItems()) {
                    if (i.getComponent().getBoundingRect().contains(mouseEvent.getPoint())) {
                        int nonffigys = 0;
                        for (Item i2 : UberInventory.getItems())
                            if (!i2.getName().contains("effigy"))
                                nonffigys++;
                        effigies.set(i.getComponent().getChildIndex() - nonffigys, null);
                        return;
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mouseDragged(MouseEvent mouseEvent) {
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        lastMouse = mouseEvent;
        PaintController.mouseMoved(mouseEvent);
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL)
            ctrlPressed = true;
    }

    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL)
            ctrlPressed = false;
    }

    public enum TYPES
    {
        STARVED(91), NOURISHED(93), SATED(95), GORGED(97), UNK(121);

        int level;

        TYPES(int level) {
            this.level = level;
        }

    }

    private static final Color RED   = new Color(207, 27, 72, 150);
    private static final Color GREEN = new Color(81, 204, 41, 150);
    private static final int   size  = 35;

    class Effigy
    {
        public final TYPES       type;
        public final int[]       skills;
        public final Rectangle2D rect;

        public Effigy(Item item, int[] skills) {
            String name = item.getName().toLowerCase();
            if (name.contains("starved an"))
                type = TYPES.STARVED;
            else if (name.contains("nourished an"))
                type = TYPES.NOURISHED;
            else if (name.contains("sated an"))
                type = TYPES.SATED;
            else if (name.contains("gorged an"))
                type = TYPES.GORGED;
            else
                type = TYPES.UNK;
            this.skills = skills;
            rect = item.getComponent().getBoundingRect();
        }

        public void paintRect(Graphics2D g) {
            boolean cando = false;
            for (int i : skills)
                if (Skills.getCurrentLevel(i) >= type.level)
                    cando = true;
            g.setColor(Color.BLACK);
            g.drawRect((int) rect.getX() - 1,
                       (int) rect.getY() - 1,
                       (int) rect.getWidth() + 1,
                       (int) rect.getHeight() + 1);
            g.setColor((cando) ? GREEN : RED);
            g.fill(rect);
        }

        public void paintText(Graphics2D g) {
            if (lastMouse != null && rect.contains(lastMouse.getPoint())) {
                String skill1 = Skills.SKILL_NAMES[skills[0]] + " lvl " + type.level;
                String skill2 = Skills.SKILL_NAMES[skills[1]] + " lvl " + type.level;
                g.setFont(new Font("Arial", 0, 12));
                g.setColor(Color.WHITE);
                g.drawString(skill1,
                             (int) rect.getX(),
                             (int) rect.getCenterY() - g.getFontMetrics().getLineMetrics(skill1, g).getAscent());
                g.drawString(skill2,
                             (int) rect.getX(),
                             (int) rect.getCenterY() + g.getFontMetrics().getLineMetrics(skill1, g).getAscent());
            }
        }
    }
}