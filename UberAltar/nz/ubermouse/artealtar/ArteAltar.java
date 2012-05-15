package nz.ubermouse.artealtar;

import com.rsbuddy.event.events.MessageEvent;
import com.rsbuddy.event.listeners.MessageListener;
import com.rsbuddy.event.listeners.PaintListener;
import com.rsbuddy.script.ActiveScript;
import com.rsbuddy.script.Manifest;
import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.task.Task;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.util.Timer;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.strategies.*;
import nz.ubermouse.artealtar.transports.*;
import nz.ubermouse.artealtar.utils.Misc;
import nz.ubermouse.artealtar.utils.UberMovement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Manifest(authors = "UberMouse",
          name = "ArteAltar",
          keywords = "Prayer",
          version = 0.1,
          description = "Offers bones to altars in POH")
public class ArteAltar extends ActiveScript implements MessageListener,
                                                       MouseListener,
                                                       MouseMotionListener,
                                                       PaintListener
{
    private static final List<Strategy> strategies = new LinkedList<Strategy>();
    private boolean doneGUI;
    private boolean debug = true;

    // Classes

    // === Paint variables === //
    private boolean show = true;
    private boolean NEW  = true;
    private long    startExp;
    private int     expHour;
    private int     bonesHour;
    private boolean moreInfo;
    // === End Paint Variables === //

    // === End GUI Variables === //

    // Misc
    private updateThread updatethread;

    public interface Strategy
    {
        public void execute();

        public boolean isValid();
    }

    public boolean onStart() {
        final ArteAltar script = this;
        try {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run() {
                    UberAltarGUI GUI = new UberAltarGUI(script);
                    GUI.setVisible(true);
                }
            });
        } catch (Exception ignored) {
        }
        while (!doneGUI)
            sleep(100);
        if (Shared.friendName.length() > 0)
            Shared.useFriendsHouse = true;
//        if (Shared.pouchid != 0) {
//            if (!Shared.transportStrategyBank.equals("GLORY") && !Shared.transportStrategyBank.equals("ROD")) {
//                log.severe("Please select either Mounted Glory or Ring of Dueling for Banking if you're using a BoB");
//                return false;
//            }
//        }

        // CHANGE THE SKILL TO W/E YOU'RE MEASURING
        startExp = (long) Skills.getCurrentExp(Skills.PRAYER);
        Misc.startLevel = (long) Skills.getRealLevel(Skills.PRAYER);
        Misc.st = System.currentTimeMillis();
        // === End PaintUtils Setup === //

        strategies.add(new BankStrat());
        if (Shared.useBoB) {
            strategies.add(new BoB());
            strategies.add(new RenewSummoning());
        }
        strategies.add(new HouseTabletTeleport());
        strategies.add(new HouseTeleport(this));
        strategies.add(new WalkAltarRoom());
        strategies.add(new YanilleBankMethod());
        if (Shared.lightBurners) {
            strategies.add(new LightIncense());
        }
        strategies.add(new OfferBones());
        strategies.add(new MountedGlory());
        strategies.add(new TeleportRoom(Shared.portalName));
        strategies.add(new KinshipRing());
        strategies.add(new RingOfDueling());
        if (Shared.useBoB)
            strategies.add(new walkBankFromObelisk());
        strategies.add(new WalkBank(this, Shared.bankPath, Shared.bankTile));
        strategies.add(new EnterHouse());
        strategies.add(new WalkYanillePortal());
        if (Shared.boneid == 0 ||
            Shared.transportStrategyBank.equals("error") ||
            Shared.transportStrategyHouse.equals("error")) {
            log.severe("You forgot too fill out parts/all of the GUI");
            return false;
        }
        Camera.setPitch(true);
        Misc.updateTimer = new Timer(300000);
        for (int id : GameConstants.boneIDs) {
            Misc.boneMap.put(id, 0);
        }
        for (int id : GameConstants.boneIDs) {
            Misc.lastBoneMap.put(id, 0);
        }
        updatethread = new updateThread(this);
        return true;
    }

    public void onFinish() {
        Misc.updateSignature();
        log("Thanks for testing ArteAltar, please report bugs and feedback.");
    }

    public int random(int min, int max) {
        return Random.nextInt(min, max);
    }

    @Override
    public int loop() {
        if (Walking.getEnergy() > random(60, 80) && !Walking.isRunEnabled())
            Walking.setRun(true);
        if (Widgets.getComponent(740, 3) != null)
            Widgets.getComponent(740, 3).click();
        if (Players.getLocal().getAnimation() != -1 || !Game.isLoggedIn())
            return 400;
        if (Inventory.contains(GameConstants.RING_OF_DUELING_ID))
            Inventory.getItem(GameConstants.RING_OF_DUELING_ID).interact("Wear");
        Mouse.setSpeed(random(4, 7));
        for (Strategy strategy : strategies) {
            try {
                if (strategy.isValid()) {
                    strategy.execute();
                    return random(500, 1000);
                }
            } catch (Exception e) {
                if (debug)
                    e.printStackTrace();
            }
        }
        return 10;
    }

    public class updateThread extends Thread
    {
        final ArteAltar script;

        public updateThread(ArteAltar script) {
            this.script = script;
            start();
        }

        public void run() {
            while (script.isActive()) {
                if (!script.isPaused() && !Misc.updateTimer.isRunning()) {
                    Misc.updateSignature();
                }
                Task.sleep(1000);
            }
        }
    }


    public void messageReceived(MessageEvent e) {
        String txt = e.getMessage();
        if (txt.contains("pleased with your offering")) {
            Misc.bonesUsed++;
            Misc.boneMap.put(Shared.boneid, Misc.boneMap.get(Shared.boneid) + 1);
        }
        if (txt.contains("crumbles to dust"))
            Shared.needNewROD = true;
    }

    // === PaintUtils Relatated Functions/Variables === //
    private boolean pressed   = false;
    private Point   location2 = new Point(-1, -1);


    public void mouseClicked(MouseEvent arg0) {
    }


    public void mouseEntered(MouseEvent arg0) {
    }


    public void mouseExited(MouseEvent arg0) {
    }


    public void mouseDragged(MouseEvent arg0) {
    }


    public void mouseMoved(MouseEvent arg0) {
        location2 = arg0.getPoint();
    }


    public void mousePressed(MouseEvent arg0) {
        pressed = true;
    }


    public void mouseReleased(MouseEvent arg0) {
        pressed = false;
    }

    public boolean mousePressed() {
        return pressed;
    }

    private final RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
                                                         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Time for the paintType.
    // NOTE: DO NOT EDIT THE PAINT'S OVERALL LOOK WITHOUT TRIGOON'S PERMISSION.
    // ONLY SCRIPTS HOSTED BY ARBIBOTS ARE PERMITTED TO USE THIS PAINT
    // THIS MEANS YOUR PRIVATE SCRIPTS OR SCRIPTS OFF ARBIBOTS ARE NOT
    // ALLOWED TO USE THIS PAINT.
    // LOOK FOR THE WORD (CHANGE) TO SEE WHAT YOU MAY NEED TO CHANGE.
    // PAINT: Version 2.0
    // BY: TRIGOON
    public void onRepaint(Graphics g) {
        NumberFormat comma = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(rh);

        int fade = 255;
        if (pressed) {
            if (!(location2.x >= 419 && location2.x <= 490 && location2.y >= 348 && location2.y <= 362)) {
                if (location2.x >= 6 && location2.x <= 512 && location2.y >= 344 && location2.y <= 472) {
                    fade = 50;
                }
            }
        }

        // (CHANGE) Change the skill to what you want.
        if ((Skills.getCurrentExp(Skills.PRAYER) - startExp) > 0 && startExp > 0) {
            Misc.expGained = Skills.getCurrentExp(Skills.PRAYER) - startExp;
        }

        if (show) {
            g2.setColor(new Color(194, 178, 146, fade));
            g2.fillRect(6, 344, 506, 128);
            g2.setColor(new Color(49, 42, 27, 255));
            g2.drawRect(6, 344, 506, 128);

            g2.setColor(new Color(108, 107, 107, fade));
            g2.drawRect(9, 347, 390 + 17, 22);

            g2.setColor(new Color(184, 170, 142, fade));
            g2.drawRect(10, 348, 388 + 17, 20);

            g2.setColor(new Color(214, 196, 160, fade));
            g2.fillRect(12, 350, 385 + 17, 17);

            long millis = System.currentTimeMillis() - Misc.st;
            long totalseconds = millis / 1000;
            long hours = millis / (1000 * 60 * 60);
            millis -= hours * 1000 * 60 * 60;
            long minutes = millis / (1000 * 60);
            millis -= minutes * 1000 * 60;
            long seconds = millis / 1000;

            // (CHANGE) Remove the else statement if you don't plan on having a
            // more info button.
            if (!moreInfo) {
                long timeToLevel;
                String timeToLevel2 = "Calculating...";
                if (expHour > 0) {
                    timeToLevel = (Skills.getExpToNextLevel(Skills.PRAYER) * 60 / expHour);
                    if (timeToLevel >= 60) {
                        long thours = (int) timeToLevel / 60;
                        long tmin = (timeToLevel - (thours * 60));
                        timeToLevel2 = thours + " Hours, " + tmin + " Minutes";
                    }
                    else {
                        timeToLevel2 = timeToLevel + " Minutes";
                    }
                }

                if (Misc.expGained > 0 && totalseconds > 0) {
                    expHour = (int) (3600 * Misc.expGained / totalseconds);
                    bonesHour = (int) (3600 * Misc.bonesUsed / totalseconds);
                }

                g2.setColor(new Color(0, 0, 0, fade));
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.setColor(new Color(168, 9, 9, fade));
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("Time Running:", 12, 385);
                g2.drawString("Time To Level:", 12, 401);
                g2.drawString("Exp Gained:", 12, 417);
                g2.drawString("Bones Used:", 12, 433);

                g2.setColor(new Color(0, 0, 0, fade));
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                if (Misc.st != 0) {
                    g2.drawString(hours + " Hours " + minutes + " Minutes " + seconds + " Seconds", 100, 385);
                }
                else {
                    g2.drawString("Script loading...", 100, 385);
                }
                g2.drawString(timeToLevel2, 100, 401);
                // (CHANGE) Uncomment these parts as you need to show the info,
                // I commented them out since some variables don't exist in your
                // scripts.
                g2.drawString(comma.format(Misc.expGained) + " XP (" + comma.format(expHour) + "/H)", 100, 417);
                g2.drawString(comma.format(Misc.bonesUsed) + " Bones (" + comma.format(bonesHour) + "/H)", 100, 433);
            }
            else {
                g2.setColor(new Color(0, 0, 0, fade));
                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.setColor(new Color(168, 9, 9, fade));
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("Trips Made:", 12, 385);
                g2.drawString("XP / Trip:", 12, 401);
                if (Misc.statsUsername.length() > 0)
                    g2.drawString("Sig update:", 12, 417);

                g2.setColor(new Color(0, 0, 0, fade));
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                String tripsHour = "0";
                int xpTrip = 0;

                // (CHANGE) Uncomment these parts as you need to show the info,
                // I commented them out since some variables don't exist in your
                // scripts.
                if (Misc.tripsMade > 0) {
                    tripsHour = comma.format(Misc.tripsMade * 3600 / totalseconds);
                }
                g2.drawString(comma.format(Misc.tripsMade) + " Trips (" + tripsHour + "/H)", 125, 385);

                if (Misc.expGained > 0 && Misc.tripsMade > 0) {
                    xpTrip = (int) Misc.expGained / Misc.tripsMade;
                }
                g2.drawString(comma.format(xpTrip), 125, 401);
                if (Misc.statsUsername.length() > 0)
                    g2.drawString(Misc.updateTimer.toRemainingString(), 125, 417);
            }

            g2.setColor(new Color(168, 9, 9, fade));
            g2.fillRect(493, 347, 16, 15);
            g2.setColor(new Color(102, 0, 0, fade));
            g2.drawRect(493, 347, 16, 15);
            g2.setColor(new Color(255, 255, 255, fade));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("X", 497, 360);

            // (CHANGE) Remove the code below if you dont want a more info tab.
            g2.setColor(new Color(62, 59, 54, fade));
            g2.fillRect(420, 347, 70, 16);
            g2.setColor(new Color(136, 125, 103, fade));
            g2.fillRect(421, 348, 68, 14);
            g2.setColor(new Color(86, 80, 69, fade));
            g2.fillRect(422, 349, 66, 12);
            g2.setColor(new Color(255, 255, 255, fade));
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString("More Info", 430, 359);
        }
        else {
            g2.setColor(new Color(40, 130, 0, fade));
            g2.fillRect(493, 347, 16, 15);
            g2.setColor(new Color(32, 104, 0, fade));
            g2.drawRect(493, 347, 16, 15);
            g2.setColor(new Color(255, 255, 255, fade));
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("O", 496, 360);
        }

        Point m = location2;
        if (!show && pressed && m.x >= 493 && m.x <= (493 + 16) && m.y >= 347 && m.y <= (347 + 15) && NEW) {
            show = true;
            NEW = false;
        }
        else if (show && pressed && m.x >= 493 && m.x <= (493 + 16) && m.y >= 347 && m.y <= (347 + 15) && NEW) {
            show = false;
            NEW = false;
        }

        if (show && pressed && m.x >= 420 && m.x <= (420 + 70) && m.y >= 347 && m.y <= (347 + 15) && NEW) {
            moreInfo = !moreInfo;
            NEW = false;
        }

        if (!pressed) {
            NEW = true;
        }

        if (show) {
            // TRIGOON'S PATENTED DYNAMIC TEXT POSITIONING.
            // DO NOT EDIT OR SHARE THIS PART OF THE CODE TO ANYONE.
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            int percent = Skills.getPercentToNextLevel(Skills.PRAYER);
            String a = percent +
                       "% To Level " +
                       (Skills.getRealLevel(Skills.PRAYER) + 1) +
                       " (" +
                       (Skills.getRealLevel(Skills.PRAYER) - Misc.startLevel) +
                       " Gained)";
            FontMetrics a_font_metric = g2.getFontMetrics();
            Rectangle2D string_width = a_font_metric.getStringBounds(a, g2);
            int re = 174 + (percent * (223 - 174) / 100);
            int gr = 160 + (percent * (215 - 160) / 100);
            int bl = 133 + (percent * (207 - 133) / 100);
            g2.setColor(new Color(re, gr, bl, fade));
            g2.fill3DRect(12, 350, (int) (4.03 * percent), 17, true);
            g2.setColor(new Color(33, 28, 28, fade));
            g2.drawString(a, 214 - ((int) string_width.getWidth() / 2), 363);
            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            // (CHANGE) Edit below with your script version #.
            g2.drawString("Beta", 478, 468);
            drawMouse(g);
        }
    }

    void drawMouse(final Graphics g) {
        // (CHANGE) Change colours to fit your script.
        final Point loc = Mouse.getLocation();
        if (Mouse.isPressed()) {
            g.setColor(new Color(255, 252, 0, 150));
            g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
            g.setColor(new Color(0, 0, 0, 225));
            g.drawOval(loc.x - 5, loc.y - 5, 10, 10);
            g.setColor(new Color(255, 252, 0, 100));
        }
        else {
            g.setColor(new Color(255, 252, 0, 50));
        }

        g.drawLine(0, loc.y, 766, loc.y);
        g.drawLine(loc.x, 0, loc.x, 505);

        g.setColor(new Color(0, 0, 0, 50));
        g.drawLine(0, loc.y + 1, 766, loc.y + 1);
        g.drawLine(0, loc.y - 1, 766, loc.y - 1);
        g.drawLine(loc.x + 1, 0, loc.x + 1, 505);
        g.drawLine(loc.x - 1, 0, loc.x - 1, 505);
    }

    class UberAltarGUI extends JFrame
    {

        private static final long serialVersionUID = 1L;

        final ArteAltar script;
        private final Object[] bones = {"Please select", "Bones", "Big Bones", "Dragon Bones", "Frost Dragon Bones"};
        private final Object[] house = {"Please select",
                                        "Teleport tablet",
                                        "House teleport spell",
                                        "Yanille House Portal"};
        private final Object[] Bank  = {"Please select",
                                        "Falador Teleport Portal",
                                        "Ring Of Kinship",
                                        "Ring Of Dueling",
                                        "Mounted Amulet of Glory",
                                        "Yanille Bank"};
        private final Object[] beast = {"Don't use", "Spirit Terror Bird", "War tortoise", "Pack Yak"};

        private final String saveFile = Environment.getStorageDirectory() + "/ArteAltar.settings";

        public UberAltarGUI(final ArteAltar script) {
            this.script = script;
            initComponents();
            loadSettings();
        }

        public void saveSettings() {
            try {
                System.out.println(new File(".").getCanonicalPath());
                File saveFile = new File(this.saveFile);
                if (!saveFile.exists())
                    saveFile.createNewFile();
                else {
                    saveFile.delete();
                    saveFile.createNewFile();
                }
                FileWriter fStream = new FileWriter(this.saveFile);
                BufferedWriter out = new BufferedWriter(fStream);
                String[] names = {"boneSelection",
                                  "houseSelection",
                                  "bankSelection",
                                  "beastSelection",
                                  "username",
                                  "friendsName"};
                Object[] data = {boneSelection.getSelectedIndex(),
                                 houseSelection.getSelectedIndex(),
                                 bankSelection.getSelectedIndex(),
                                 beastSelection.getSelectedIndex(),
                                 username.getText(),
                                 friendsname.getText()};
                for (int i = 0; i < names.length; i++) {
                    out.write(names[i] + ":" + data[i] + "\n");
                    out.flush();
                }
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void loadSettings() {
            try {
                File loadFile = new File(this.saveFile);
                if (!loadFile.exists())
                    return;
                BufferedReader reader = new BufferedReader(new FileReader(loadFile));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("boneS")) {
                        boneSelection.setSelectedIndex(Integer.parseInt(line.split(":")[1]));
                    }
                    if (line.contains("houseS")) {
                        houseSelection.setSelectedIndex(Integer.parseInt(line.split(":")[1]));
                    }
                    if (line.contains("bankS")) {
                        bankSelection.setSelectedIndex(Integer.parseInt(line.split(":")[1]));
                    }
                    if (line.contains("friend")) {
                        friendsname.setText(line.split(":")[1]);
                    }
                    else if (line.contains("user")) {
                        username.setText(line.split(":")[1]);
                    }
                    if (line.contains("beast"))
                        beastSelection.setSelectedIndex(Integer.parseInt(line.split(":")[1]));
                }
            } catch (Exception ignored) {
            }
        }

        private void startBtnActionPerformed(ActionEvent e) {
            if (boneSelection.getSelectedIndex() != 0)
                Shared.boneid = GameConstants.boneIDs[boneSelection.getSelectedIndex() - 1];
            if (beastSelection.getSelectedIndex() != 0) {
                Shared.pouchid = GameConstants.pouchIDs[beastSelection.getSelectedIndex() - 1];
                Shared.useBoB = true;
            }

            switch (houseSelection.getSelectedIndex()) {
                case 0:
                    Shared.transportStrategyHouse = "error";
                    break;
                case 1:
                    Shared.transportStrategyHouse = "HOUSE_TELEPORT_TABLET";
                    break;
                case 2:
                    Shared.transportStrategyHouse = "HOUSETELE";
                    break;
                case 3:
                    Shared.transportStrategyHouse = "YANILLE_WALK_HOUSE";
                    break;
            }
            switch (bankSelection.getSelectedIndex()) {
                case 0:
                    Shared.transportStrategyBank = "error";
                    break;
                case 1:
                    Shared.transportStrategyBank = "TELEPORT_ROOM";
                    Shared.bankPath = GameConstants.FALADOR_BANK_PATH;
                    Shared.bankTile = GameConstants.FALADOR_BANK_TILE;
                    Shared.portalName = "Falador Portal";
                    break;
                case 2:
                    Shared.transportStrategyBank = "KINSHIP_RING";
                    Shared.bankPath = GameConstants.DAMONHEIM_BANK_PATH;
                    Shared.bankTile = GameConstants.DAMONHEIM_BANK_TILE;
                    break;
                case 3:
                    Shared.transportStrategyBank = "ROD";
                    Shared.bankPath = GameConstants.CASTLE_WARS_BANK_PATH;
                    Shared.bankTile = GameConstants.CASTLE_WARS_BANK_TILE;
                    Shared.obeliskTile = GameConstants.obeliskTiles[0];
                    break;
                case 4:
                    Shared.transportStrategyBank = "GLORY";
                    Shared.bankPath = GameConstants.EDGE_BANK_PATH;
                    Shared.bankTile = GameConstants.EDGE_BANK_TILE;
                    Shared.obeliskTile = GameConstants.obeliskTiles[1];
                    break;
                case 5:
                    Shared.transportStrategyBank = "YANILLE_WALK_BANK";
                    Shared.bankPath = UberMovement
                            .reversePath(GameConstants.YANILLE_BANK_HOUSE);
                    Shared.bankTile = GameConstants.YANILLE_BANK_TILE;
                    break;
            }
            saveSettings();
            Misc.statsUsername = username.getText();
            Shared.friendName = friendsname.getText();
            script.doneGUI = true;
            Shared.lightBurners = lightBurners.isSelected();
            dispose();
        }

        private void initComponents() {
            // JFormDesigner - Component initialization - DO NOT MODIFY
            // //GEN-BEGIN:initComponents
            // Generated using JFormDesigner Evaluation license - Taylor Lodge
            userLabel = new JLabel();
            username = new JTextField();
            friendLabel = new JLabel();
            friendsname = new JTextField();
            bobLabel = new JLabel();
            beastSelection = new JComboBox(beast);
            bonelabel = new JLabel();
            boneSelection = new JComboBox(bones);
            houseLabel = new JLabel();
            houseSelection = new JComboBox(house);
            bankLabel = new JLabel();
            bankSelection = new JComboBox(Bank);
            crap = new JLabel();
            startBtn = new JButton();
            lightBurners = new JCheckBox("Light burners", true);

            // ======== this ========
            Container contentPane = getContentPane();
            contentPane.setLayout(null);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setResizable(false);

            // ---- lightBurners
            contentPane.add(lightBurners);
            lightBurners.setBounds(165,
                                   75,
                                   lightBurners.getPreferredSize().width,
                                   lightBurners.getPreferredSize().height);

            // ---- userLabel ----
            userLabel.setText("UserName");
            contentPane.add(userLabel);
            userLabel.setBounds(new Rectangle(new Point(5, 5), userLabel.getPreferredSize()));
            username.setText("");
            contentPane.add(username);
            username.setBounds(5, 25, 135, username.getPreferredSize().height);

            // ---- friendLabel ----
            friendLabel.setText("Friends Name?");
            contentPane.add(friendLabel);
            friendLabel.setBounds(new Rectangle(new Point(5, 55), friendLabel.getPreferredSize()));

            // ---- friendsname ----
            friendsname.setText("");
            contentPane.add(friendsname);
            friendsname.setBounds(5, 75, 135, friendsname.getPreferredSize().height);

            // ---- bobLabel ----
            bobLabel.setText("Use Beast of Burden?");
            contentPane.add(bobLabel);
            bobLabel.setBounds(new Rectangle(new Point(5, 115), bobLabel.getPreferredSize()));
            contentPane.add(beastSelection);
            beastSelection.setBounds(5, 135, 135, beastSelection.getPreferredSize().height);

            // ---- bonelabel ----
            bonelabel.setText("Bones to use?");
            contentPane.add(bonelabel);
            bonelabel.setBounds(new Rectangle(new Point(5, 170), bonelabel.getPreferredSize()));
            contentPane.add(boneSelection);
            boneSelection.setBounds(5, 190, 135, boneSelection.getPreferredSize().height);

            // ---- houseLabel ----
            houseLabel.setText("House transport method?");
            contentPane.add(houseLabel);
            houseLabel.setBounds(new Rectangle(new Point(5, 225), houseLabel.getPreferredSize()));
            contentPane.add(houseSelection);
            houseSelection.setBounds(5, 245, 135, houseSelection.getPreferredSize().height);

            // ---- bankLabel ----
            bankLabel.setText("Bank transport method?");
            contentPane.add(bankLabel);
            bankLabel.setBounds(new Rectangle(new Point(165, 5), bankLabel.getPreferredSize()));
            contentPane.add(bankSelection);
            bankSelection.setBounds(165, 25, 135, bankSelection.getPreferredSize().height);

            // ---- crap ----
            crap.setText("fffuuuuu empty space");
            contentPane.add(crap);
            crap.setBounds(165, 135, 125, crap.getPreferredSize().height);

            // ---- startBtn ----
            startBtn.setText("Start");
            startBtn.addActionListener(new ActionListener()
            {

                public void actionPerformed(ActionEvent e) {
                    startBtnActionPerformed(e);
                }
            });
            contentPane.add(startBtn);
            startBtn.setBounds(new Rectangle(new Point(255, 245), startBtn.getPreferredSize()));

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for (int i = 0; i < contentPane.getComponentCount(); i++) {
                    Rectangle bounds = contentPane.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = contentPane.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                contentPane.setMinimumSize(preferredSize);
                contentPane.setPreferredSize(preferredSize);
            }
            pack();
            setLocationRelativeTo(getOwner());
            // JFormDesigner - End of component initialization
            // //GEN-END:initComponents
        }

        // JFormDesigner - Variables declaration - DO NOT MODIFY
        // //GEN-BEGIN:variables
        // Generated using JFormDesigner Evaluation license - Taylor Lodge
        private JLabel     userLabel;
        private JTextField username;
        private JLabel     friendLabel;
        private JTextField friendsname;
        private JLabel     bobLabel;
        private JComboBox  beastSelection;
        private JLabel     bonelabel;
        private JComboBox  boneSelection;
        private JLabel     houseLabel;
        private JComboBox  houseSelection;
        private JLabel     bankLabel;
        private JComboBox  bankSelection;
        private JLabel     crap;
        private JButton    startBtn;
        private JCheckBox  lightBurners;
        // JFormDesigner - End of variables declaration //GEN-END:variables
    }
}