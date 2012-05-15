import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.GameObject;
import com.rsbuddy.script.wrappers.Player;

import javax.swing.*;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/6/11
 * Time: 9:26 PM
 * Package: PACKAGE_NAME;
 */
@Manifest(authors = "UberMouse",
          name = "UberCooker",
          keywords = "Cooking",
          version = 1.0,
          description = "Cooks food at the rogues den")
public class UberCooker extends ActiveScript
{
    private static LinkedList<Strategy> strategies = new LinkedList<Strategy>();
    private static int foodID = 0;
    private static boolean cooked;

    private static interface GameConstants
    {
        int fireID = 2732;
    }

    public boolean onStart() {
        strategies.add(new cook());
        strategies.add(new bank());
        try {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run() {
                    String food = JOptionPane.showInputDialog(null,
                                                              "Enter food ID",
                                                              "Food",
                                                              JOptionPane.QUESTION_MESSAGE);
                    try {
                        foodID = Integer.parseInt(food);
                    } catch (NumberFormatException ignored) {
                    }
                }
            });
        } catch (Exception ignored) {
        }
        return true;
    }

    private static interface Strategy
    {
        public void execute();

        public boolean isValid();
    }

    private int random(int min, int max) {
        return Random.nextInt(min, max);
    }

    @Override
    public int loop() {
        if (Players.getLocal().getAnimation() != -1)
            return 400;
        for (Strategy strategy : strategies) {
            if (strategy.isValid()) {
                strategy.execute();
                return random(300, 400);
            }
        }
        return random(400, 500);
    }

    private class cook implements Strategy
    {

        public void execute() {
            GameObject fire = Objects.getNearest(GameConstants.fireID);
            if (fire != null) {
                if (Inventory.getItem(foodID).click(true)) {
                    if (fire.interact(" " + fire.getDef().getName())) {
                        int timeout = 0;
                        Player player = Players.getLocal();
                        while (player.isMoving() && ++timeout <= 15) {
                            if (player.isMoving())
                                timeout = 0;
                            sleep(100);
                        }
                        timeout = 0;
                        while (!Widgets.getComponent(905, 14).isValid() && ++timeout <= 15)
                            sleep(100);
                        if (Widgets.getComponent(905, 14).click()) {
                            timeout = 0;
                            while (player.getAnimation() == -1 && ++timeout <= 15)
                                sleep(100);
                            if (player.getAnimation() != -1)
                                cooked = true;
                        }
                    }
                }
            }
        }

        public boolean isValid() {
            return !Bank.isOpen() && Inventory.getCount(foodID) > 0 && !cooked;
        }
    }

    private class bank implements Strategy
    {

        public void execute() {
            cooked = false;
            if (!Bank.isOpen()) {
                if (Bank.open()) {
                    int timeout = 0;
                    while (!Bank.isOpen() && ++timeout <= 15)
                        sleep(100);
                    if (!Bank.isOpen())
                        return;
                }
                else
                    return;
            }
            Bank.depositAll();
            Bank.withdraw(foodID, 0);
            sleep(random(400, 500));
            if (Inventory.getCount(foodID) > 0)
                Bank.close();
        }

        public boolean isValid() {
            return Inventory.getCount(foodID) == 0;
        }
    }
}