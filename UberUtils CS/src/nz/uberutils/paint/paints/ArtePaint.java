package nz.uberutils.paint.paints;

import com.rsbuddy.script.methods.Environment;
import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Skills;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.task.Task;
import nz.uberutils.helpers.IGUI;
import nz.uberutils.helpers.IPaint;
import nz.uberutils.helpers.Skill;
import nz.uberutils.helpers.Utils;
import nz.uberutils.paint.components.PColumnLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ArtePaint implements IPaint {
    Rectangle trackerRect, paintRect, wpRect, one, two, three, skillBarLeft, skillBarRight;
    Point         wp;
    BufferedImage PAINT_BG, PAINT_LOGO, PAINT_ONE_NORMAL, PAINT_ONE_HIGHLIGHT, PAINT_TWO_NORMAL, PAINT_TWO_HIGHLIGHT, PAINT_THREE_NORMAL, PAINT_THREE_HIGHLIGHT, PAINT_TRACKER, PAINT_OPEN_PAINT, PAINT_CLOSE_PAINT;
    boolean overOne, overTwo, overThree, pressedMouse;
    boolean showPaint            = true;
    boolean showTracker          = false; // This is for those who use GUI trackers in addition to paint.
    int     wpX, wpY, tab, skill = 0;
    public int numTabs = 1;
    long startExp, startTime;
    int startLevel, percentToLevel, levelsGained = 0;
    final  RenderingHints rh         = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                          RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    public String[][][]   columnData = new String[][][]{new String[][]{new String[]{}, new String[]{}, new String[]{}},
                                                        new String[][]{new String[]{}, new String[]{}, new String[]{}},
                                                        new String[][]{new String[]{}, new String[]{}, new String[]{}}};
    final IGUI gui;
    public final ArrayList<Skill> skills = new ArrayList<Skill>();
    private final String scriptName;

    public ArtePaint(String scriptName, String bg, String logo, IGUI gui) {
        // Being logged in is important to determine the positoning of the paint.
        if (!Game.isLoggedIn()) {
            Environment.enableRandoms();
            while (!Game.isLoggedIn())
                Task.sleep(10, 20);
        }
        this.scriptName = scriptName;
        this.gui = gui;
        percentToLevel = Skills.getPercentToNextLevel(Skills.RUNECRAFTING);
        wp = Widgets.get(137).getComponent(0).getAbsLocation();
        wpRect = Widgets.get(137).getComponent(0).getBoundingRect();
        wpX = (int) wp.getX();
        wpY = (int) wp.getY();
        trackerRect = new Rectangle(wpX + 435, wpY - 30, 34, 30);
        paintRect = new Rectangle(wpX + 471, wpY - 30, 34, 30);
        one = new Rectangle(wpX + 470, wpY + 23, 19, 19);
        two = new Rectangle(wpX + 470, wpY + 45, 19, 19);
        three = new Rectangle(wpX + 470, wpY + 67, 19, 19);
        skillBarLeft = new Rectangle(wpX + 82, wpY - 41, 10, 30);
        skillBarRight = new Rectangle(wpX + 421, wpY - 41, 10, 30);
        startExp = Skills.getCurrentExp(Skills.RUNECRAFTING);
        startLevel = Skills.getRealLevel(Skills.RUNECRAFTING);
        startTime = System.currentTimeMillis();
        String sep = System.getProperty("file.separator");
        String saveLoc = Environment.getStorageDirectory() + sep + "artebots" + sep + scriptName + sep + "images" + sep;
        final File file = new File(saveLoc);
        if (!file.exists())
            file.mkdirs();
        PAINT_BG = Utils.loadImage("http://artebots.com/images/" + bg, saveLoc);
        PAINT_OPEN_PAINT = Utils.loadImage("http://artebots.com/images/open_paint.png", saveLoc);
        PAINT_CLOSE_PAINT = Utils.loadImage("http://artebots.com/images/close_paint.png", saveLoc);
        PAINT_LOGO = Utils.loadImage("http://artebots.com/images/" + logo, saveLoc);
        PAINT_ONE_NORMAL = Utils.loadImage("http://artebots.com/images/1_normal.png", saveLoc);
        PAINT_TWO_NORMAL = Utils.loadImage("http://artebots.com/images/2_normal.png", saveLoc);
        PAINT_THREE_NORMAL = Utils.loadImage("http://artebots.com/images/3_normal.png", saveLoc);
        PAINT_ONE_HIGHLIGHT = Utils.loadImage("http://artebots.com/images/1_highlight.png", saveLoc);
        PAINT_TWO_HIGHLIGHT = Utils.loadImage("http://artebots.com/images/2_highlight.png", saveLoc);
        PAINT_THREE_HIGHLIGHT = Utils.loadImage("http://artebots.com/images/3_highlight.png", saveLoc);
        PAINT_TRACKER = Utils.loadImage("http://artebots.com/images/openTracker.png", saveLoc);
        skills.clear();
    }

    public boolean paint(Graphics g) {
        if (!showPaint) {
            g.drawImage(PAINT_OPEN_PAINT, wpX + 473, wpY - 40, null);
            return false;
        }
        if (wp != null && showPaint) {
            int tempTab = tab;
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(rh);

            g.drawImage(PAINT_BG, wpX - 6, wpY - 41, null);
            if(!scriptName.equals("ArteHunter"))
                g.drawImage(PAINT_LOGO, wpX + 3, wpY - 55, null);
            else
                g.drawImage(PAINT_LOGO, wpX + 3, wpY + 10, null);
            g.drawImage(PAINT_CLOSE_PAINT, wpX + 471, wpY - 30, null);
            g.drawImage(PAINT_TRACKER, wpX + 435, wpY - 30, null);

            // Tabs
            if (tab == 0 || overOne)
                g.drawImage(PAINT_ONE_HIGHLIGHT, wpX + 470, wpY + 23, null);
            else
                g.drawImage(PAINT_ONE_NORMAL, wpX + 470, wpY + 23, null);

            if (numTabs > 1) {
                if (tab == 1 || overTwo)
                    g.drawImage(PAINT_TWO_HIGHLIGHT, wpX + 470, wpY + 45, null);
                else
                    g.drawImage(PAINT_TWO_NORMAL, wpX + 470, wpY + 45, null);
            }

            if (numTabs > 2) {
                if (tab == 2 || overThree)
                    g.drawImage(PAINT_THREE_HIGHLIGHT, wpX + 470, wpY + 67, null);
                else
                    g.drawImage(PAINT_THREE_NORMAL, wpX + 470, wpY + 67, null);
            }

            g2.setFont(new Font("Tahoma", Font.BOLD, 8));
            int barY = wpY - 34;
            Skill s = skills.get(skill);
            int percentToLevel = s.percentTL();
            int levelsGained = s.levelsGained();
            String text = percentToLevel +
                    "% TO LEVEL " +
                    (s.curLevel() + 1) +
                    " " +
                    s.shortName +
                    " (" +
                    levelsGained +
                    " LEVEL" +
                    ((levelsGained == 1) ? "" : "S") +
                    " GAINED) " +
                    Skill.nf.format(s.xpPH()) +
                    " XP P/H " +
                    ((s.xpGained() > 0) ? s.timeToLevel() + " TL" : "");
            FontMetrics metric = g2.getFontMetrics();
            Rectangle2D stringMetrics = metric.getStringBounds(text, g2);
            final int barX = wpX + 425 - ((int) ((100 - percentToLevel) * 3.36));

            if (percentToLevel < 100) {
                g.setColor(new Color(194, 178, 146, (pressedMouse) ? 10 : 255));
                g.fillRect(barX, barY, ((int) ((100 - percentToLevel) * 3.36)), 18);
                g2.setColor(new Color(155, 102, 0, (pressedMouse) ? 10 : 255));
                g2.setStroke(new BasicStroke(1));
                g.drawLine(barX, barY, barX, barY + 17);
            }
            g2.setColor(new Color(49, 42, 27, (pressedMouse) ? 10 : 255));
            g.drawString(text, wpX + 89 + ((int) (336 - stringMetrics.getWidth()) / 2), barY + 12);

            int alpha = 255;
            if (tab == 0 || overOne) {
                if (overTwo || overThree)
                    alpha = 0;
                else if (overOne && tab != 0)
                    alpha = 100;
                tempTab = 0;
            }

            if (numTabs > 1) {
                if (tab == 1 || overTwo) {
                    if (overOne || overThree)
                        alpha = 0;
                    else if (overTwo && tab != 1)
                        alpha = 100;
                    tempTab = 1;

                }
            }

            if (numTabs > 2) {
                if (tab == 2 || overThree) {
                    if (overOne || overTwo)
                        alpha = 0;
                    else if (overThree && tab != 2)
                        alpha = 100;
                    tempTab = 2;
                }
            }
            g2.setFont(new Font("Tahoma", Font.BOLD, 10));
            g2.setColor(new Color(49, 42, 27, (pressedMouse) ? 10 : alpha));
            PColumnLayout layout = new PColumnLayout(wpX + 130,
                                                     wpY + 27,
                                                     columnData[tempTab][0],
                                                     columnData[tempTab][1]);
            layout.repaint(g2);
            int tempY = wpY + 27;
            for (String msg : columnData[tempTab][2]) {
                g.drawString(msg,
                             (int) (wpX + 455 - g2.getFontMetrics().getStringBounds(msg, g2).getWidth()),
                             tempY);
                tempY += 15;
            }

            g.setColor(new Color(194, 178, 146, (pressedMouse) ? 10 : 255));
            g.drawString("TIME RUNNING: " + Utils.parseTime(System.currentTimeMillis() - startTime, true),
                         wpX + 145,
                         wpY + 118);
        }
        return true;
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    public void mouseClicked(MouseEvent e) {
        Point ep = e.getPoint();
        if (ep != null) {
            if (one.contains(ep)) {
                tab = 0;
            }
            else if (numTabs > 1 && two.contains(ep)) {
                tab = 1;
            }
            else if (numTabs > 2 && three.contains(ep)) {
                tab = 2;
            }

            if (trackerRect.contains(ep) && !showTracker) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        gui.showGUI();
                    }
                });
            }

            if (paintRect.contains(ep)) {
                showPaint = !showPaint;
                if (!showPaint)
                    paintRect = new Rectangle(wpX + 473, wpY - 40, 34, 30);
                else
                    paintRect = new Rectangle(wpX + 471, wpY - 30, 34, 30);
            }
            if (skillBarLeft.contains(ep)) {
                skill--;
                if (skill < 0)
                    skill = skills.size() - 1;
            }
            if (skillBarRight.contains(ep)) {
                skill++;
                if (skill > skills.size() - 1)
                    skill = 0;
            }
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mouseDragged(MouseEvent mouseEvent) {
    }

    public void mouseMoved(MouseEvent e) {
        Point ep = e.getPoint();
        if (ep != null) {
            try {
                overOne = one.contains(ep);
                if (numTabs > 1)
                    overTwo = two.contains(ep);
                if (numTabs > 2)
                    overThree = three.contains(ep);
            } catch (Exception r) {
            }
        }
    }
}
