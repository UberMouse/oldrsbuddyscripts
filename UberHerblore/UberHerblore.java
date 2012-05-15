import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.Item;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.text.NumberFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/5/11
 * Time: 12:30 PM
 * Package: PACKAGE_NAME;
 */
@Manifest(authors = "UberMouse",
          name = "UberHerblore",
          keywords = "Herblore",
          version = 0.1,
          description = "Creates herblore")
public class UberHerblore extends ActiveScript implements PaintListener, MouseMotionListener
{
    private static int PRIMARY = 241;
    private static int SECONDARY = 2483;
    private static int HERB = 0;
    private static boolean herbed;
    private static long startTime;
    private static int startHerbXP;
    private static Point m = new Point(-1, -1);
    private static int mode = 0;

    public boolean onStart() {
        startTime = System.currentTimeMillis();
        startHerbXP = Skills.getCurrentExp(Skills.HERBLORE);
        try {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run() {
                    if (mode == 0) {
                        String first = JOptionPane.showInputDialog(null,
                                                                   "Enter primary ingredient ID",
                                                                   "Primary",
                                                                   JOptionPane.QUESTION_MESSAGE);
                        String second = JOptionPane.showInputDialog(null,
                                                                    "Enter secondary ingredient ID",
                                                                    "Secondary",
                                                                    JOptionPane.QUESTION_MESSAGE);
                        PRIMARY = Integer.parseInt(first);
                        SECONDARY = Integer.parseInt(second);
                    }
                    else {
                        String first = JOptionPane.showInputDialog(null,
                                                                   "Enter herb ID",
                                                                   "Herb",
                                                                   JOptionPane.QUESTION_MESSAGE);
                        HERB = Integer.parseInt(first);
                    }
                }
            });
        } catch (Exception ignored) {
        }
        return true;
    }

    @Override
    public int loop() {
        Mouse.setSpeed(1);
        if (Widgets.canContinue())
            Widgets.clickContinue();
        if (!Players.getLocal().isIdle())
            return Random.nextInt(500, 600);
        switch (mode) {
            case 0:
                potions();
                break;
            case 1:
                herbs();
                break;
        }
        return Random.nextInt(600, 700);
    }

    public void herbs() {
        if (Inventory.getCount(HERB) > 0 && !Bank.isOpen()) {
            for (Item item : Inventory.getItems()) {
                if (item.getId() == HERB) {
                    item.interact("Clean");
                    sleep(Random.nextInt(400, 600));
                }
            }
        }
        else if (!Bank.isOpen() && Inventory.getCount(HERB) < 1) {
            Bank.open();
            sleep(Random.nextInt(500, 600));
        }
        else if (Bank.isOpen()) {
            if ((Inventory.isFull() && !Inventory.contains(PRIMARY)) ||
                (Inventory.getCount() > 0 && !Inventory.isFull()))
                Bank.depositAll();
            else {
                Bank.withdraw(HERB, 28);
                sleep(Random.nextInt(600, 700));
                Bank.close();
            }
        }
    }

    public void potions() {
        if (Inventory.getCount(PRIMARY) > 0 && Inventory.getCount(SECONDARY) > 0 && !Bank.isOpen() && !herbed) {
            if (Inventory.useItem(Inventory.getItem(PRIMARY), Inventory.getItem(SECONDARY))) {
                int timeout = 0;
                while (!Widgets.getComponent(905, 14).isValid() && ++timeout <= 15)
                    sleep(100);
                if (Widgets.getComponent(905, 14).isValid()) {
                    if (Widgets.getComponent(905, 14).click())
                        herbed = true;
                }
                sleep(Random.nextInt(500, 600));
            }
        }
        else if (!Bank.isOpen() && !(Inventory.getCount(PRIMARY) > 0 && Inventory.getCount(SECONDARY) > 0)) {
            Bank.open();
            sleep(Random.nextInt(500, 600));
        }
        else if (Bank.isOpen()) {
            herbed = false;
            if ((Inventory.getCount() == 14 && !Inventory.contains(PRIMARY)) || Inventory.isFull())
                Bank.depositAll();
            else {
                Bank.withdraw(PRIMARY, 14);
                sleep(Random.nextInt(600, 700));
                Bank.withdraw(SECONDARY, 14);
                sleep(Random.nextInt(600, 700));
                Bank.close();
            }
        }
    }

    private final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                                                   RenderingHints.VALUE_ANTIALIAS_ON);

    private final Color color1 = new Color(153, 0, 0);
    private final Color color2 = new Color(0, 0, 0);
    private final Color color3 = new Color(102, 102, 102);
    private final Color color4 = new Color(255, 255, 255);
    private final Color color5 = new Color(51, 51, 51, 225);

    private final BasicStroke stroke1 = new BasicStroke(1);

    private final Font font1 = new Font("Arial", 0, 10);
    private final Font font2 = new Font("Arial", 0, 15);

    private GeneralPath pathFrom(int[] xs, int[] ys) {
        GeneralPath gp = new GeneralPath();
        gp.moveTo(xs[0], ys[0]);
        for (int i = 1; i < xs.length; i++)
            gp.lineTo(xs[i], ys[i]);
        gp.closePath();
        return gp;
    }

    public void onRepaint(Graphics render) {
        Graphics2D g = (Graphics2D) render;
        g.setRenderingHints(antialiasing);
        g.setColor(color5);
        g.fillRoundRect(3, 338, 514, 139, 16, 16);
        g.setColor(color1);
        g.setStroke(stroke1);
        g.drawRoundRect(3, 338, 514, 139, 16, 16);
        String timeRan = getTime(System.currentTimeMillis() - startTime);
        g.setFont(font2);
        g.setColor(color2);
        g.setColor(color4);
        g.drawString("Time running: " + timeRan, 237, 381);
        drawSkill(g,
                  Skills.HERBLORE,
                  "Herblore",
                  Skills.getCurrentExp(Skills.HERBLORE)
                  - startHerbXP,
                  10,
                  350);

    }

    private void drawSkill(Graphics2D g, int skill, String name, int xpGained,
                           int x, int y) {
        Color start = g.getColor();
        int width = 225;
        int height = 20;
        Rectangle hover = new Rectangle(x, y, width, height);
        int percentTL = Skills.getPercentToNextLevel(skill);
        int xpTL = Skills.getExpToNextLevel(skill);
        int xpPH = (int) ((xpGained) * 3600000D / (System.currentTimeMillis() - startTime));
        String TTL = "Calculating..";
        long ttlCalculations;
        if (xpPH != 0) {
            ttlCalculations = (long) (xpTL * 3600000D) / xpPH;
            TTL = getTime(ttlCalculations);
        }
        Font oldFont = g.getFont();
        g.setColor(color1);
        g.fillRoundRect(x, y, width, height, 16, 16);
        g.setColor(color2);
        g.setStroke(stroke1);
        g.drawRoundRect(x, y, width, height, 16, 16);
        g.setColor(color3);
        g.fillRoundRect(x, y, (int) (percentTL * 2.25), 20, 16, 16);
        g.setFont(font1);
        g.setColor(color4);
        NumberFormat nf = NumberFormat.getIntegerInstance();
        g.drawString(name + " - " + percentTL + "% - " + nf.format(xpTL),
                     x + 30,
                     y + 15);
        if (hover.contains(m)) {
            final GeneralPath polygon1 = pathFrom(new int[]{
                    (int) (x + (width / 3.25) + 35),
                    (int) (x + (width / 3.25) + 49),
                    (int) (x + (width / 3.25) + 52 + 12)}, new int[]{
                    y - 10,
                    y - 4,
                    y - 10});
            g.setColor(color3);
            g.fillRoundRect((int) (x + (width / 3.25)),
                            (int) (y - (height * 2.45)),
                            110,
                            38,
                            16,
                            16);
            g.setColor(color1);
            g.drawRoundRect((int) (x + (width / 3.25)),
                            (int) (y - (height * 2.45)),
                            110,
                            38,
                            16,
                            16);
            g.setFont(font1);
            g.setColor(color4);
            g.drawString("P/H: " + xpPH,
                         (int) (x + (width / 3.25)) + 5,
                         y - (height * 2) + 2);
            g.drawString("TTL: " + TTL,
                         (int) (x + (width / 3.25)) + 5,
                         y - (height * 2) + 11);
            g.drawString("Gained: " + nf.format(xpGained),
                         (int) (x + (width / 3.25)) + 5,
                         20 + y - (height * 2));
            g.setColor(color2);
            g.fill(polygon1);
            g.setColor(color1);
            g.setStroke(stroke1);
            g.draw(polygon1);

        }
        g.setColor(start);
        g.setFont(oldFont);
    }

    private String getTime(long millis) {
        long time = millis / 1000;
        String seconds = Integer.toString((int) (time % 60));
        String minutes = Integer.toString((int) ((time % 3600) / 60));
        String hours = Integer.toString((int) (time / 3600));
        for (int i = 0; i < 2; i++) {
            if (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            if (hours.length() < 2) {
                hours = "0" + hours;
            }
        }
        return hours + ":" + minutes + ":" + seconds;
    }

    public void mouseDragged(MouseEvent mouseEvent) {
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        m = mouseEvent.getPoint();
    }
}