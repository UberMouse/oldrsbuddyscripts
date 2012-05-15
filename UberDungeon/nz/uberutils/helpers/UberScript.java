package nz.uberutils.helpers;

import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import nz.uberutils.helpers.tasks.PriceThread;
import nz.uberutils.paint.PaintController;
import nz.uberutils.paint.components.PDialogue;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/17/11
 * Time: 5:13 PM
 * Package: nz.uberutils.helpers;
 */
public abstract class UberScript extends ActiveScript implements PaintListener,
                                                                 KeyListener,
                                                                 MouseListener,
                                                                 MouseMotionListener
{
    protected       String              status     = "Starting...";
    protected final ArrayList<Strategy> strategies = new ArrayList<Strategy>();
    protected final String              name       = getClass().getAnnotation(Manifest.class).name();
    protected int threadId;
    String sep = System.getProperty("file.separator");
    protected final Logger   logger    = new Logger(Environment.getStorageDirectory() +
                                                    sep +
                                                    "artebots" +
                                                    sep +
                                                    name +
                                                    sep +
                                                    "logs" +
                                                    sep +
                                                    name, name);
    protected       String[] changeLog = new String[]{"Not set"};
    private         boolean  setup     = false;
    protected       IPaint   paintType = null;

    public boolean onStart() {
        if (!Game.isLoggedIn()) {
            log("Not logged in... waiting for login random to finish");
            while (!Game.isLoggedIn() || Skills.getCurrentLevel(Skills.AGILITY) < 1)
                sleep(100);
            sleep(3000);
        }
        Options.setNode(name + Account.getName());
        getContainer().submit(new PriceThread());
        PaintController.reset();
        PaintController.startTimer();
        Options.add("prevVersion", getClass().getAnnotation(Manifest.class).version());
        return onBegin();
    }

    public void onFinish() {
        Options.put("prevVersion", getClass().getAnnotation(Manifest.class).version());
        Options.save();
        logger.cleanup();
        onEnd();
    }

    public void onEnd() {

    }

    public boolean onBegin() {
        return true;
    }

    @Override
    public int loop() {
        try {
            if (!setup) {
                if (Options.getDouble("prevVersion") < getClass().getAnnotation(Manifest.class).version()) {
                    PaintController.addComponent(new PDialogue("Script has been updated!",
                            changeLog,
                            new Font("Arial", 0, 12),
                            PDialogue.ColorScheme.GRAPHITE,
                            PDialogue.Type.INFORMATION)
                    {
                        public void okClick() {
                            PaintController.removeComponent(this);
                        }

                        public boolean shouldHandleMouse() {
                            return shouldPaint();
                        }

                        public boolean shouldPaint() {
                            return Game.isLoggedIn();
                        }
                    });
                }
                setup = true;
            }
            if (!Game.isLoggedIn())
                return 100;
            Mouse.setSpeed(Utils.random(1, 2));
            miscLoop();
            for (Strategy strategy : strategies) {
                if (strategy.isValid()) {
                    status = strategy.getStatus();
                    strategy.execute();
                    return Utils.random(400, 500);
                }
            }
        } catch (Exception e) {
            if (Utils.isDevMode())
                e.printStackTrace();
        }
        return 0;
    }

    protected void miscLoop() {

    }

    public void keyTyped(KeyEvent keyEvent) {
        if (paintType == null)
            return;
        paintType.keyTyped(keyEvent);
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (paintType == null)
            return;
        paintType.keyPressed(keyEvent);
    }

    public void keyReleased(KeyEvent keyEvent) {
        if (paintType == null)
            return;
        paintType.keyReleased(keyEvent);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if (paintType == null)
            return;
        paintType.mouseClicked(mouseEvent);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (paintType == null)
            return;
        paintType.mousePressed(mouseEvent);
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        if (paintType == null)
            return;
        paintType.mouseReleased(mouseEvent);
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        if (paintType == null)
            return;
        paintType.mouseEntered(mouseEvent);
    }

    public void mouseExited(MouseEvent mouseEvent) {
        if (paintType == null)
            return;
        paintType.mouseExited(mouseEvent);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if (paintType == null)
            return;
        paintType.mouseDragged(mouseEvent);
    }

    public void mouseMoved(MouseEvent mouseEvent) {
        if (paintType == null)
            return;
        paintType.mouseMoved(mouseEvent);
    }

    public void onRepaint(Graphics graphics) {
        if (paintType == null)
            return;
        if (paintType.paint(graphics))
            paint((Graphics2D) graphics);
    }

    protected void paint(Graphics2D g) {

    }
}