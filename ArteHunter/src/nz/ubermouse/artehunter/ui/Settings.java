package nz.ubermouse.artehunter.ui;

import com.rsbuddy.script.methods.Environment;
import nz.ubermouse.artehunter.Hunter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Settings extends JFrame {

    private int level;

    public Settings(int level) {
        this.level = level;
        try {
            initComponents();
        } catch (Exception ignored) {
        }
    }

    private void initComponents() throws MalformedURLException {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Matt B
        logoLabel = new JLabel();
        tabPanel = new JTabbedPane();
        scrollPane1 = new JScrollPane();
        editorPane1 = new JEditorPane();
        birdPanel = new JPanel();
        crimonSwiftButton = new JToggleButton();
        goldenWarblerButton = new JToggleButton();
        copperLongtailButton = new JToggleButton();
        ceruleanTwitchButton = new JToggleButton();
        tropicalWagtailButton = new JToggleButton();
        reservedBirdButton = new JToggleButton();
        chinchompaPanel = new JPanel();
        greyChinchompaButton = new JToggleButton();
        redChinchompaButton = new JToggleButton();
        panel3 = new JPanel();
        panel4 = new JPanel();
        buryBonesBox = new JCheckBox();
        checkBox2 = new JCheckBox();
        checkBox3 = new JCheckBox();
        checkBox4 = new JCheckBox();
        startButton = new JButton();

        ActionListener bonesListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                buryBonesBox.setEnabled(true);
                startButton.setEnabled(true);
            }
        };

        ActionListener nonBonesListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                buryBonesBox.setSelected(false);
                buryBonesBox.setEnabled(false);
                startButton.setEnabled(true);
            }
        };

        ActionListener startListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                int hunting = 0;
                if (crimonSwiftButton.isSelected())
                    hunting = 1;
                else if (copperLongtailButton.isSelected())
                    hunting = 2;
                else if (ceruleanTwitchButton.isSelected())
                    hunting = 3;
                else if (tropicalWagtailButton.isSelected())
                    hunting = 4;
                else if (greyChinchompaButton.isSelected())
                    hunting = 5;
                else if (redChinchompaButton.isSelected())
                    hunting = 6;
                Hunter.setHunting(hunting);
                Hunter.setBuryBones(buryBonesBox.isSelected());
                setVisible(false);
            }
        };

        //======== this ========
        setResizable(false);
        setAlwaysOnTop(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout) contentPane.getLayout()).columnWidths = new int[]{10, 454, 5};
        ((GridBagLayout) contentPane.getLayout()).rowHeights = new int[]{6, 255, 5};

        //---- logoLabel ----
        logoLabel.setText("ArteHunt");
        contentPane.add(logoLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                                          GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                                                          new Insets(0, 0, 5, 5), 0, 0));

        //======== tabPanel ========
        {

            //======== scrollPane1 ========
            {

                //---- editorPane1 ----
                URLConnection url = null;
                String changelog = "";
                try {
                    url = new URL("http://dl.dropbox.com/u/12976075/artehunter-changelog.txt").openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.getInputStream()));
                    String line = "";
                    while ((line = in.readLine()) != null)
                        changelog += line + "\n";
                } catch (IOException e) {
                    changelog = "Error loading changelog";
                }
                editorPane1.setText("Hello " + Environment
                        .getUsername() + ", and welcome to ArteHunter\n\nChangelog:\n" + changelog + "");
                editorPane1.setMargin(new Insets(1, 1, 1, 1));
                editorPane1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                editorPane1.setEditable(false);
                scrollPane1.setViewportView(editorPane1);
            }
            tabPanel.addTab("Changelog", scrollPane1);

            ButtonGroup npcGroup = new ButtonGroup();

            //======== birdPanel ========
            {

                // JFormDesigner evaluation mark
                birdPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) birdPanel.getLayout()).columnWidths = new int[]{180, 0, 175};
                ((GridBagLayout) birdPanel.getLayout()).rowHeights = new int[]{55, 55, 50};

                //---- crimonSwiftButton ----
                npcGroup.add(crimonSwiftButton);
                crimonSwiftButton.setText("Crimson swift");
                crimonSwiftButton.setIcon(new ImageIcon(new URL("http://i.imgur.com/q2YP7.gif")));
                crimonSwiftButton.addActionListener(bonesListener);
                birdPanel.add(crimonSwiftButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.BOTH,
                                                                        new Insets(0, 0, 5, 5),
                                                                        0,
                                                                        0));

                //---- goldenWarblerButton ----
                if (level >= 5)
                    goldenWarblerButton.setText("Golden warbler");
                else
                    goldenWarblerButton.setText("Level 5 needed");
                goldenWarblerButton.setEnabled(false);
                birdPanel.add(goldenWarblerButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.BOTH,
                                                                          new Insets(0, 0, 5, 0),
                                                                          0,
                                                                          0));

                //---- copperLongtailButton ----
                npcGroup.add(copperLongtailButton);
                copperLongtailButton.addActionListener(bonesListener);
                copperLongtailButton.setIcon(new ImageIcon(new URL("http://i.imgur.com/58fxe.gif")));
                if (level >= 9) {
                    copperLongtailButton.setText("Copper longtail");
                }
                else {
                    copperLongtailButton.setText("Level 9 needed");
                    copperLongtailButton.setEnabled(false);
                }
                birdPanel.add(copperLongtailButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.BOTH,
                                                                           new Insets(0, 0, 5, 5),
                                                                           0,
                                                                           0));

                //---- ceruleanTwitchButton ----
                npcGroup.add(ceruleanTwitchButton);
                ceruleanTwitchButton.setIcon(new ImageIcon(new URL("http://i.imgur.com/977SG.gif")));
                if (level >= 11) {
                    ceruleanTwitchButton.setText("Cerulean twitch");
                }
                else {
                    ceruleanTwitchButton.setText("Level 11 needed");
                    ceruleanTwitchButton.setEnabled(false);
                }
                ceruleanTwitchButton.addActionListener(bonesListener);
                birdPanel.add(ceruleanTwitchButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.BOTH,
                                                                           new Insets(0, 0, 5, 0),
                                                                           0,
                                                                           0));

                //---- tropicalWagtailButton ----
                npcGroup.add(tropicalWagtailButton);
                tropicalWagtailButton.setIcon(new ImageIcon(new URL("http://i.imgur.com/QtIZO.gif")));
                if (level >= 19) {
                    tropicalWagtailButton.setText("Tropical wagtail");
                }
                else {
                    tropicalWagtailButton.setText("Level 19 needed");
                    tropicalWagtailButton.setEnabled(false);
                }
                tropicalWagtailButton.addActionListener(bonesListener);
                birdPanel.add(tropicalWagtailButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                                                            GridBagConstraints.CENTER,
                                                                            GridBagConstraints.BOTH,
                                                                            new Insets(0, 0, 0, 5),
                                                                            0,
                                                                            0));

                //---- reservedBirdButton ----
                reservedBirdButton.setText("Reserved");
                reservedBirdButton.setEnabled(false);
                birdPanel.add(reservedBirdButton, new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.BOTH,
                                                                         new Insets(0, 0, 0, 0),
                                                                         0,
                                                                         0));
            }
            tabPanel.addTab("Birds", birdPanel);


            //======== chinchompaPanel ========
            {
                chinchompaPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) chinchompaPanel.getLayout()).columnWidths = new int[]{155, 0, 150};
                ((GridBagLayout) chinchompaPanel.getLayout()).rowHeights = new int[]{50};

                //---- greyChinchompaButton ----
                npcGroup.add(greyChinchompaButton);
                if (level >= 53) {
                    greyChinchompaButton.setIcon(new ImageIcon(new URL("http://i.imgur.com/MISQ4.gif")));
                    greyChinchompaButton.setText("Grey Chinchompa");
                }
                else {
                    greyChinchompaButton.setText("Level 53 needed");
                    greyChinchompaButton.setEnabled(false);
                }
                greyChinchompaButton.addActionListener(nonBonesListener);
                chinchompaPanel.add(greyChinchompaButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                                                 GridBagConstraints.CENTER,
                                                                                 GridBagConstraints.BOTH,
                                                                                 new Insets(0, 0, 0, 5),
                                                                                 0,
                                                                                 0));

                //---- redChinchompaButton ----
                npcGroup.add(redChinchompaButton);
                if (level >= 63) {
                    redChinchompaButton.setText("Red chinchompa");
                    redChinchompaButton.setIcon(new ImageIcon(new URL("http://i.imgur.com/OuT5o.gif")));
                }
                else {
                    redChinchompaButton.setText("Level 63 needed");
                    redChinchompaButton.setEnabled(false);
                }
                redChinchompaButton.addActionListener(nonBonesListener);
                chinchompaPanel.add(redChinchompaButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                                                                GridBagConstraints.CENTER,
                                                                                GridBagConstraints.BOTH,
                                                                                new Insets(0, 0, 0, 0),
                                                                                0,
                                                                                0));
            }
            tabPanel.addTab("Chinchompas", chinchompaPanel);

            //======== panel3 ========
            {
                panel3.setLayout(new GridBagLayout());
                ((GridBagLayout) panel3.getLayout()).rowHeights = new int[]{0, 40};

                //======== panel4 ========
                {
                    panel4.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel4.getLayout()).columnWidths = new int[]{55, 50, 0};
                    ((GridBagLayout) panel4.getLayout()).rowHeights = new int[]{0, 0, 0};
                    ((GridBagLayout) panel4.getLayout()).columnWeights = new double[]{0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel4.getLayout()).rowWeights = new double[]{0.0, 0.0, 1.0E-4};

                    //---- buryBonesBox ----
                    buryBonesBox.setText("Bury bones?");
                    buryBonesBox.setEnabled(false);
                    panel4.add(buryBonesBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                                                    new Insets(0, 0, 5, 5), 0, 0));
                }
                panel3.add(panel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                                          GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                                          new Insets(0, 0, 5, 0), 0, 0));

                //---- startButton ----
                startButton.setText("Start");
                startButton.addActionListener(startListener);
                startButton.setEnabled(false);
                panel3.add(startButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                                               GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                                               new Insets(0, 0, 0, 0), 0, 0));
            }
            tabPanel.addTab("Start", panel3);

        }
        contentPane.add(tabPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                                         GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                                         new Insets(0, 0, 5, 5), 0, 0));
        setSize(500, 320);
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Matt B
    private JLabel        logoLabel;
    private JTabbedPane   tabPanel;
    private JScrollPane   scrollPane1;
    private JEditorPane   editorPane1;
    private JPanel        birdPanel;
    private JToggleButton crimonSwiftButton;
    private JToggleButton goldenWarblerButton;
    private JToggleButton copperLongtailButton;
    private JToggleButton ceruleanTwitchButton;
    private JToggleButton tropicalWagtailButton;
    private JToggleButton reservedBirdButton;
    private JPanel        chinchompaPanel;
    private JToggleButton greyChinchompaButton;
    private JToggleButton redChinchompaButton;
    private JPanel        panel3;
    private JPanel        panel4;
    private JCheckBox     buryBonesBox;
    private JCheckBox     checkBox2;
    private JCheckBox     checkBox3;
    private JCheckBox     checkBox4;
    private JButton       startButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

