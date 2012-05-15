package nz.uberbase;

import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.Mouse;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.Utils;
import nz.uberutils.paint.PaintController;
import nz.uberutils.paint.components.PFancyButton;
import nz.uberutils.paint.components.PFrame;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/9/11
 * Time: 2:44 PM
 * Package: PACKAGE_NAME;
 */
@Manifest(authors = "UberMouse",
          name = "UberBase",
          keywords = "Utils",
          version = 0.1,
          description = "Base for scripts")
public class UberBase extends ActiveScript implements PaintListener, MouseListener, MouseMotionListener, KeyListener
{
    private static boolean showPaint = true;
    private static String status;
    private static final ArrayList<Strategy> stratagies = new ArrayList<Strategy>();
    private static final PFrame infoFrame = new PFrame("info")
    {
        public boolean shouldPaint() {
            return UberBase.menuIndex == 0;
        }

        public boolean shouldHandleMouse() {
            return shouldPaint();
        }
    };
    private static final PFrame optionFrame = new PFrame("options")
    {
        public boolean shouldPaint() {
            return UberBase.menuIndex == 1;
        }

        public boolean shouldHandleMouse() {
            return shouldPaint();
        }
    };
    public static int menuIndex = 0;

    public static void togglePaint() {
        showPaint = !showPaint;
    }

    public boolean onStart() {
        Options.setNode("UberBase");
        PaintController.clearComps();
        PaintController.addComponent(new PFancyButton(8, 450, "ArteBots", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                Utils.openURL("http://artebots.com");
            }
        });
        PaintController.addComponent(new PFancyButton(59, 450, "Feedback", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                Utils.openURL("http://doesnotexist.com");
            }
        });
        PaintController.addComponent(infoFrame);
        PaintController.addComponent(optionFrame);
        PaintController.addComponent(new PFancyButton(440, 345, "Toggle paint", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onStart() {
                forceMouse = true;
                forcePaint = true;
            }

            public void onPress() {
                UberBase.togglePaint();
                PaintController.toggleEvents();
            }
        });
        PaintController.addComponent(new PFancyButton(440, 365, 73, -1, "Info", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onStart() {
                setHovered(true);
            }

            public void onPress() {
                UberBase.menuIndex = 0;
            }

            public void mouseMoved(MouseEvent mouseEvent) {
                setHovered(pointInButton(mouseEvent.getPoint()) || UberBase.menuIndex == 0);
            }
        });
        PaintController.addComponent(new PFancyButton(440, 385, 73, -1, "Options", PFancyButton.ColorScheme.GRAPHITE)
        {
            public void onPress() {
                UberBase.menuIndex = 1;
            }

            public void mouseMoved(MouseEvent mouseEvent) {
                setHovered(pointInButton(mouseEvent.getPoint()) || UberBase.menuIndex == 1);
            }
        });
        PaintController.startTimer();
        return true;
    }

    public void onFinish() {
        Options.save();
    }

    @Override
    public int loop() {
        Mouse.setSpeed(Utils.random(1, 2));
        for (Strategy strategy : stratagies) {
            if (strategy.isValid()) {
                status = strategy.getStatus();
                strategy.execute();
                return Utils.random(400, 500);
            }
        }
        return 0;
    }

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

    public void onRepaint(Graphics graphics) {
        try {
            if (showPaint) {
                PaintController.onRepaint(graphics);
            }
        } catch (Exception ignored) {
        }
    }
}
