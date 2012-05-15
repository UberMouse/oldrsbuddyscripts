package nz.uberutils.paint.paints;

import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Mouse;
import nz.uberutils.helpers.IPaint;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Skill;
import nz.uberutils.helpers.Utils;
import nz.uberutils.paint.PaintController;
import nz.uberutils.paint.abstracts.PComponent;
import nz.uberutils.paint.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UberPaint implements IPaint
{
    public UberPaint(final String name, final int threadId, final double version) {
        PaintController.addComponent(new PFancyButton(8, 450, "ArteBots", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                Utils.openURL("http://artebots.com");
            }
        });
        PaintController.addComponent(new PFancyButton(59, 450, "Feedback", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                Utils.openURL("http://rsbuddy.com/forum/showthread.php?t=" + threadId);
            }
        });
        addFrame("info");
        addFrame("options");
        PaintController.addComponent(new PFancyButton(461, 481, 54, 24, "Hide", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onStart() {
                forceMouse = true;
                forcePaint = true;
            }

            public void onPress() {
                text = (text.equals("Hide")) ? "Show" : "Hide";
                togglePaint();
                PaintController.toggleEvents();
            }
        });
        addTab("Info", 440, 345, 73, -1, 0, -1);
        addTab("Options", 440, 365, 73, -1, 1, -1);
        this.version = version;
        this.name = name;
    }

    public void togglePaint() {
        showPaint = !showPaint;
    }

    protected boolean showPaint = true;

    public          String[]               infoColumnValues = new String[0];
    public          String[]               infoColumnData   = new String[0];
    protected final Map<String, PFrame>    frames           = new HashMap<String, PFrame>();
    protected final Map<String, PButton>   buttons          = new HashMap<String, PButton>();
    protected final Map<String, PCheckBox> checkBoxes       = new HashMap<String, PCheckBox>();
    public final    ArrayList<Skill>       skills           = new ArrayList<Skill>();
    public          int                    menuIndex        = 0;
    public          int                    subMenuIndex     = 0;
    public          String                 name             = "";
    private double version;

    public void keyTyped(KeyEvent keyEvent) {
        PaintController.keyTyped(keyEvent);
    }

    public void keyPressed(KeyEvent keyEvent) {
        PaintController.keyPressed(keyEvent);
    }

    public void keyReleased(KeyEvent keyEvent) {
        PaintController.keyReleased(keyEvent);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        PaintController.mouseClicked(mouseEvent);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        PaintController.mousePressed(mouseEvent);
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        PaintController.mouseReleased(mouseEvent);
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        PaintController.mouseEntered(mouseEvent);
    }

    public void mouseExited(MouseEvent mouseEvent) {
        PaintController.mouseExited(mouseEvent);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        PaintController.mouseDragged(mouseEvent);
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        PaintController.mouseMoved(mouseEvent);
    }

    static final Map<String, PCheckBox> firstColumn  = new HashMap<String, PCheckBox>();
    static final Map<String, PCheckBox> secondColumn = new HashMap<String, PCheckBox>();
    static       PCheckBoxLayout        firstLayout  = null;
    static       PCheckBoxLayout        secondLayout = null;

    public boolean paint(Graphics graphics) {
        if (!Game.isLoggedIn())
            return false;
        try {
            Graphics2D g = (Graphics2D) graphics;
            PComponent clayout = null;
            try {
                clayout = new PColumnLayout(227,
                        354,
                        infoColumnValues,
                        infoColumnData,
                        new Font("Arial", 0, 9),
                        PColumnLayout.ColorScheme.WHITE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getFrame("options").removeComponent(firstLayout);
            getFrame("options").removeComponent(secondLayout);
            int secondColx = -1;
            int bestLength = -1;
            firstColumn.clear();
            secondColumn.clear();
            if (checkBoxes.size() <= 6)
                firstColumn.putAll(checkBoxes);
            else {
                for (int i = 0; i < checkBoxes.size(); i++) {
                    if (i <= 5) {
                        String text;
                        Iterator it = checkBoxes.keySet().iterator();
                        for (int j = 0; j < i; j++)
                            it.next();
                        text = (String) it.next();
                        int length = SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont()), text);
                        if (length > bestLength)
                            bestLength = length;
                        firstColumn.put(text, checkBoxes.get(text));
                    }
                    else {
                        String text;
                        Iterator it = checkBoxes.keySet().iterator();
                        for (int j = 0; j < i; j++)
                            it.next();
                        text = (String) it.next();
                        secondColumn.put(text, checkBoxes.get(text));
                    }
                }
            }
            secondColx = 8 + bestLength;
            firstLayout = new PCheckBoxLayout(8,
                    362,
                    firstColumn.keySet().toArray(new String[(firstColumn.size() > 6) ? 6 : firstColumn.size()]),
                    firstColumn.values().toArray(new PCheckBox[(firstColumn.size() > 6) ? 6 : firstColumn.size()]),
                    new Font("Arial", 0, 11),
                    PCheckBoxLayout.ColorScheme.WHITE);
            secondLayout = new PCheckBoxLayout(secondColx + 12,
                    362,
                    secondColumn.keySet().toArray(new String[(secondColumn.size() > 6) ? 6 : secondColumn.size()]),
                    secondColumn.values().toArray(new PCheckBox[(secondColumn.size() > 6) ? 6 : secondColumn.size()]),
                    new Font("Arial", 0, 11),
                    PCheckBoxLayout.ColorScheme.WHITE);
            getFrame("options").addComponent(firstLayout);
            getFrame("options").addComponent(secondLayout);
            if (showPaint) {
                Paint p = g.getPaint();
                g.setPaint(new GradientPaint(0,
                        1000,
                        new Color(55, 55, 55, 240),
                        512,
                        472,
                        new Color(15, 15, 15, 240)));
                g.fillRect(7, 345, 505, 127);
                final Point loc = Mouse.getLocation();
                if (Mouse.isPressed()) {
                    g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
                    g.drawOval(loc.x - 5, loc.y - 5, 10, 10);
                }
                g.drawLine(0, loc.y + 1, 766, loc.y + 1);
                g.drawLine(0, loc.y - 1, 766, loc.y - 1);
                g.drawLine(loc.x + 1, 0, loc.x + 1, 505);
                g.drawLine(loc.x - 1, 0, loc.x - 1, 505);
                g.setPaint(p);
            }
            if (clayout != null)
                getFrame("info").addComponent(clayout);
            PaintController.onRepaint(graphics);
            if (clayout != null)
                getFrame("info").removeComponent(clayout);
            if (!showPaint)
                return false;
            String infoTxt = name + " - " + "v" + version;
            g.drawString(infoTxt, 510 - SwingUtilities.computeStringWidth(g.getFontMetrics(g.getFont()), infoTxt), 468);

            int offset = 0;
            for (Skill skill : skills) {
                if (skill.xpGained() > 0) {
                    PSkill skillComp = new PSkill(8, 346 + offset, skill.getSkill(), PSkill.ColorScheme.GRAPHITE);
                    if (!getFrame("info").containsComponent(skillComp)) {
                        getFrame("info").addComponent(skillComp);
                    }
                    offset += 20;
                }
            }

            // == Mouse ==
            if (Mouse.isPressed()) {
                g.setColor(new Color(255, 252, 0, 150));

                g.setColor(new Color(255, 252, 0, 100));
            }
            else {
                g.setColor(new Color(255, 252, 0, 50));
            }


            g.setColor(new Color(0, 0, 0, 50));

            // == End mouse ==
        } catch (Exception ignored) {
            if (Utils.isDevMode())
                ignored.printStackTrace();
        }
        return true;
    }

    public void addFrame(String name) {
        addFrame(name, frames.size(), -1);
    }

    public void addFrame(String name, final int pIndex, final int subMIndex) {
        PFrame frame = new PFrame(name)
        {
            int index = pIndex;
            int subIndex = subMIndex;

            public boolean shouldPaint() {
                if (subIndex != -1)
                    return menuIndex == index && subMenuIndex == subIndex;
                return menuIndex == index;
            }

            public boolean shouldHandleMouse() {
                return shouldPaint();
            }
        };
        frames.put(name, frame);
        PaintController.addComponent(frame);
    }

    public PFrame getFrame(String name) {
        return frames.get(name);
    }

    public void removeFrame(String name) {
        PFrame frame = frames.get(name);
        PaintController.removeComponent(frame);
        frames.remove(name);
    }

    public void addTab(String name, int x, int y, final int primaryIndex) {
        addTab(name, x, y, -1, -1, primaryIndex, -1);
    }

    public void addTab(String name, int x, int y, final int primaryIndex, int subIndex) {
        addTab(name, x, y, -1, -1, primaryIndex, subIndex);
    }

    public void addTab(String name, int x, int y, int width, int height, final int primaryIndex, final int subIndex) {
        PFancyButton button = new PFancyButton(x, y, width, height, name, PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                if (primaryIndex != -1)
                    menuIndex = primaryIndex;
                if (subIndex != -1)
                    subMenuIndex = subIndex;
            }

            public void mouseMoved(MouseEvent mouseEvent) {
                boolean hovered = false;
                if (pointInButton(mouseEvent.getPoint()))
                    hovered = true;
                if (primaryIndex != -1 && menuIndex == primaryIndex)
                    hovered = true;
                if (subIndex != -1 && subMenuIndex == subIndex)
                    hovered = true;
                setHovered(hovered);
            }
        };
        buttons.put(name, button);
        PaintController.addComponent(button);
    }

    public void removeTab(String name) {
        PButton button = buttons.get(name);
        PaintController.removeComponent(button);
        buttons.remove(name);
    }

    public void addOption(String text, final String option, boolean state) {
        addOption(text, new PCheckBox(0, 0, state)
        {
            public void onPress() {
                Options.flip(option);
            }
        });
    }

    public void addOption(String text, String option) {
        addOption(text, option, Options.getBoolean(option));
    }

    public void addOption(String text, PCheckBox box) {
        checkBoxes.put(text, box);
    }

    public void removeOption(String text) {
        checkBoxes.remove(text);
    }
}