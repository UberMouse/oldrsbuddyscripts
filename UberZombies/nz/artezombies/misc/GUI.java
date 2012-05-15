/*
 * Created by JFormDesigner on Sat Sep 17 00:39:16 NZST 2011
 */

package nz.artezombies.misc;

import com.rsbuddy.script.methods.Environment;
import nz.uberutils.helpers.Options;
import nz.uberutils.wrappers.BankItem;
import nz.uberutils.wrappers.LootItem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

/**
 * @author Taylor Lodge
 */
public class GUI extends JFrame
{

    public static boolean finished;
    private static final String SEP           = System.getProperty("file.separator");
    private static final String SAVE_FILE_LOC = Environment.getStorageDirectory() +
                                                SEP +
                                                "ArteBots" +
                                                SEP +
                                                "ArteZombies" + SEP + "artezombies.info";

    public GUI() {
        initComponents();
        load();
    }

    private void addLootItemActionPerformed() {
        ((TableModel)lootTable.getModel()).addRow(new Object[] {"", "", new Boolean(false)});
    }

    private void removeLootItemActionPerformed() {
        int row = lootTable.getSelectedRow();
        if(row == -1)
            return;
        ((TableModel)lootTable.getModel()).removeRow(row);
    }

    private void saveBtnActionPerformed() {
        save();
        setVisible(false);
        dispose();
    }

    private void addBankItemActionPerformed() {
        ((TableModel)bankingTable.getModel()).addRow(new Object[] {"", "", new Integer(0)});
    }

    private void removeBankItemActionPerformed() {
        int row = bankingTable.getSelectedRow();
        if(row == -1)
            return;
        ((TableModel)bankingTable.getModel()).removeRow(row);

    }

    private void useFoodChkStateChanged(ChangeEvent e) {
        Options.put("useFood", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void useSummoningChkStateChanged(ChangeEvent e) {
        Options.put("useSummoning", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void usePotionsChkStateChanged(ChangeEvent e) {
        Options.put("usePots", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void useSoulsplitChkStateChanged(ChangeEvent e) {
        Options.put("useSoulSplit", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void useExcaliburChkStateChanged(ChangeEvent e) {
        Options.put("excalibur", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void bankChkStateChanged(ChangeEvent e) {
        Options.put("bank", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void slowMouseChkStateChanged(ChangeEvent e) {
        Options.put("slowMouse", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void soulsplitAlwaysChkStateChanged(ChangeEvent e) {
        Options.put("ssAlwaysOn", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void pickupLootChkStateChanged(ChangeEvent e) {
        Options.put("pickupLoot", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void priorityLootChkStateChanged(ChangeEvent e) {
        Options.put("onlyPriorityLoot", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void protectionPrayersChkStateChanged(ChangeEvent e) {
        Options.put("useProtectionPrayers", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void switchPrayersChkStateChanged(ChangeEvent e) {
        Options.put("switchPrayers", ((AbstractButton) e.getSource()).getModel().isPressed());
    }

    private void cancelBtnActionPerformed() {
        setVisible(false);
        dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        saveBtn = new JButton();
        primaryTabbedContainer = new JTabbedPane();
        scriptInfoPanel = new JPanel();
        scrollPane3 = new JScrollPane();
        scriptInfoTPane = new JTextPane();
        scrollPane4 = new JScrollPane();
        updateLogTPane = new JTextPane();
        lootingPanel = new JPanel();
        scrollPane5 = new JScrollPane();
        lootTable = new JTable(new TableModel());
        ((TableModel)lootTable.getModel()).addColumn("Name");
        ((TableModel)lootTable.getModel()).addColumn("ID");
        ((TableModel)lootTable.getModel()).addColumn("Loot in combat");
        lootTable.getTableHeader().setReorderingAllowed(false);

        lootValueLbl = new JLabel();
        lootValueTxtField = new JTextField();
        scrollPane6 = new JScrollPane();
        lootInformationTPane = new JTextPane();
        addLootItem = new JButton();
        removeLootItem = new JButton();
        bankPanel = new JPanel();
        scrollPane7 = new JScrollPane();
        bankingTable = new JTable(new TableModel());
        ((TableModel)bankingTable.getModel()).addColumn("Name");
        ((TableModel)bankingTable.getModel()).addColumn("ID");
        ((TableModel)bankingTable.getModel()).addColumn("Quantity");
        bankingTable.getTableHeader().setReorderingAllowed(false);

        addBankItem = new JButton();
        removeBankItem = new JButton();
        scrollPane8 = new JScrollPane();
        bankingInfoTPane = new JTextPane();
        optionsPanel = new JPanel();
        useFoodChk = new JCheckBox();
        useFoodChk.setSelected(Options.getBoolean("useFood"));
        useExcaliburChk = new JCheckBox();
        useExcaliburChk.setSelected(Options.getBoolean("excalibur"));
        pickupLootChk = new JCheckBox();
        pickupLootChk.setSelected(Options.getBoolean("pickupLoot"));
        useSoulsplitChk = new JCheckBox();
        useSoulsplitChk.setSelected(Options.getBoolean("useSoulSplit"));
        soulsplitAlwaysChk = new JCheckBox();
        soulsplitAlwaysChk.setSelected(Options.getBoolean("ssAlwaysOn"));
        switchPrayersChk = new JCheckBox();
        switchPrayersChk.setSelected(Options.getBoolean("switchPrayers"));
        useSummoningChk = new JCheckBox();
        useSummoningChk.setSelected(Options.getBoolean("useSummoning"));
        bankChk = new JCheckBox();
        bankChk.setSelected(Options.getBoolean("bank"));
        priorityLootChk = new JCheckBox();
        priorityLootChk.setSelected(Options.getBoolean("onlyPriorityLoot"));
        usePotionsChk = new JCheckBox();
        usePotionsChk.setSelected(Options.getBoolean("usePots"));
        slowMouseChk = new JCheckBox();
        slowMouseChk.setSelected(Options.getBoolean("slowMouse"));
        protectionPrayersChk = new JCheckBox();
        protectionPrayersChk.setSelected(Options.getBoolean("useProtectionPrayers"));

        //======== this ========
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- saveBtn ----
        saveBtn.setText("Begin botting!");
        saveBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveBtnActionPerformed();
            }
        });
        contentPane.add(saveBtn);
        saveBtn.setBounds(0, 555, 710, saveBtn.getPreferredSize().height);

        //======== primaryTabbedContainer ========
        {

            //======== scriptInfoPanel ========
            {
                scriptInfoPanel.setLayout(null);

                //======== scrollPane3 ========
                {

                    //---- scriptInfoTPane ----
                    scriptInfoTPane.setEditable(false);
                    scrollPane3.setViewportView(scriptInfoTPane);
                }
                scriptInfoPanel.add(scrollPane3);
                scrollPane3.setBounds(5, 5, 335, 505);

                //======== scrollPane4 ========
                {

                    //---- updateLogTPane ----
                    updateLogTPane.setEditable(false);
                    scrollPane4.setViewportView(updateLogTPane);
                }
                scriptInfoPanel.add(scrollPane4);
                scrollPane4.setBounds(360, 5, 335, 505);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < scriptInfoPanel.getComponentCount(); i++) {
                        Rectangle bounds = scriptInfoPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = scriptInfoPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    scriptInfoPanel.setMinimumSize(preferredSize);
                    scriptInfoPanel.setPreferredSize(preferredSize);
                }
            }
            primaryTabbedContainer.addTab("Script Info", scriptInfoPanel);


            //======== lootingPanel ========
            {
                lootingPanel.setLayout(null);

                //======== scrollPane5 ========
                {
                    scrollPane5.setViewportView(lootTable);
                }
                lootingPanel.add(scrollPane5);
                scrollPane5.setBounds(0, 0, 695, 270);

                //---- lootValueLbl ----
                lootValueLbl.setText("Loot everything valued above:");
                lootValueLbl.setLabelFor(lootValueTxtField);
                lootingPanel.add(lootValueLbl);
                lootValueLbl.setBounds(new Rectangle(new Point(5, 315), lootValueLbl.getPreferredSize()));
                lootingPanel.add(lootValueTxtField);
                lootValueTxtField.setBounds(200, 310, 70, lootValueTxtField.getPreferredSize().height);

                //======== scrollPane6 ========
                {

                    //---- lootInformationTPane ----
                    lootInformationTPane.setText("Loot is split into two categoreys, priority and non priority. Priority loot is looted regardless of whether or not you are in combat, and therefor suited to valueble items. Non priority loot is only looted when you are not in combat, suited for lower value items like coins and runes.\n\nItems can be added using either the items name OR the items ID. The item name is case insensitive and will loot if the item name CONTAINS the item name, therefor pie will loot pie, cake pie, pie cake and orange pie cake.");
                    lootInformationTPane.setEditable(false);
                    scrollPane6.setViewportView(lootInformationTPane);
                }
                lootingPanel.add(scrollPane6);
                scrollPane6.setBounds(5, 340, 690, 170);

                //---- addLootItem ----
                addLootItem.setText("Add Item");
                addLootItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        addLootItemActionPerformed();
                    }
                });
                lootingPanel.add(addLootItem);
                addLootItem.setBounds(new Rectangle(new Point(0, 275), addLootItem.getPreferredSize()));

                //---- removeLootItem ----
                removeLootItem.setText("Remove Item");
                removeLootItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        removeLootItemActionPerformed();
                    }
                });
                lootingPanel.add(removeLootItem);
                removeLootItem.setBounds(new Rectangle(new Point(85, 275), removeLootItem.getPreferredSize()));

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < lootingPanel.getComponentCount(); i++) {
                        Rectangle bounds = lootingPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = lootingPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    lootingPanel.setMinimumSize(preferredSize);
                    lootingPanel.setPreferredSize(preferredSize);
                }
            }
            primaryTabbedContainer.addTab("Looting", lootingPanel);


            //======== bankPanel ========
            {
                bankPanel.setLayout(null);

                //======== scrollPane7 ========
                {
                    scrollPane7.setViewportView(bankingTable);
                }
                bankPanel.add(scrollPane7);
                scrollPane7.setBounds(0, 0, 690, 270);

                //---- addBankItem ----
                addBankItem.setText("Add Item");
                addBankItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        addBankItemActionPerformed();
                    }
                });
                bankPanel.add(addBankItem);
                addBankItem.setBounds(new Rectangle(new Point(0, 275), addBankItem.getPreferredSize()));

                //---- removeBankItem ----
                removeBankItem.setText("Remove Item");
                removeBankItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        removeBankItemActionPerformed();
                    }
                });
                bankPanel.add(removeBankItem);
                removeBankItem.setBounds(new Rectangle(new Point(85, 275), removeBankItem.getPreferredSize()));

                //======== scrollPane8 ========
                {

                    //---- bankingInfoTPane ----
                    bankingInfoTPane.setEditable(false);
                    bankingInfoTPane.setText("The bot will bank when any of these conditions are met     \n  - Out of food and you have the \"Use Food\" option ticked   \n  - Out of potions and you have the \"Use Potions\" option ticked   \n  - Out of summoning pouches or potions and you have the \"Use Summoning\" option ticked   \n  - Your inventory does not EXACTLY match the contents of the banking table and you're in the vicinity of a bank.  \n\nThe bot will bank at Varrock west bank, so you will require a varrock teleport tablet. Both teleporting to the GE entrance and the Varrock square will work fine.  \n\nItems can be entered using either names or IDs, names are case insensitive and check if the item contains the name, pie matches pie, cake pie, pie cake, orange pie cake for example.  \n\nMake sure to add any item you use as a special attack weapon to the banking list or the bot will deposit it when it attempts to bank. ");
                    bankingInfoTPane.setAutoscrolls(false);
                    bankingInfoTPane.setCaretPosition(1);
                    scrollPane8.setViewportView(bankingInfoTPane);
                }
                bankPanel.add(scrollPane8);
                scrollPane8.setBounds(5, 340, 685, 170);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < bankPanel.getComponentCount(); i++) {
                        Rectangle bounds = bankPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = bankPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    bankPanel.setMinimumSize(preferredSize);
                    bankPanel.setPreferredSize(preferredSize);
                }
            }
            primaryTabbedContainer.addTab("Banking", bankPanel);


            //======== optionsPanel ========
            {
                optionsPanel.setLayout(null);

                //---- useFoodChk ----
                useFoodChk.setText("Use Food");
                useFoodChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        useFoodChkStateChanged(e);
                    }
                });
                optionsPanel.add(useFoodChk);
                useFoodChk.setBounds(new Rectangle(new Point(5, 5), useFoodChk.getPreferredSize()));

                //---- useExcaliburChk ----
                useExcaliburChk.setText("Use Enhanced excalibur");
                useExcaliburChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        useExcaliburChkStateChanged(e);
                    }
                });
                optionsPanel.add(useExcaliburChk);
                useExcaliburChk.setBounds(5, 25, 140, 23);

                //---- pickupLootChk ----
                pickupLootChk.setText("Pickup loot");
                pickupLootChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        pickupLootChkStateChanged(e);
                    }
                });
                optionsPanel.add(pickupLootChk);
                pickupLootChk.setBounds(5, 45, 85, 23);

                //---- useSoulsplitChk ----
                useSoulsplitChk.setText("Use Soulsplit");
                useSoulsplitChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        useSoulsplitChkStateChanged(e);
                    }
                });
                optionsPanel.add(useSoulsplitChk);
                useSoulsplitChk.setBounds(545, 5, 85, 23);

                //---- soulsplitAlwaysChk ----
                soulsplitAlwaysChk.setText("Soulsplit always on");
                soulsplitAlwaysChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        soulsplitAlwaysChkStateChanged(e);
                    }
                });
                optionsPanel.add(soulsplitAlwaysChk);
                soulsplitAlwaysChk.setBounds(545, 25, 120, 23);

                //---- switchPrayersChk ----
                switchPrayersChk.setText("Switch Protection prayers");
                switchPrayersChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        switchPrayersChkStateChanged(e);
                    }
                });
                optionsPanel.add(switchPrayersChk);
                switchPrayersChk.setBounds(545, 45, 150, 23);

                //---- useSummoningChk ----
                useSummoningChk.setText("Use Summoning");
                useSummoningChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        useSummoningChkStateChanged(e);
                    }
                });
                optionsPanel.add(useSummoningChk);
                useSummoningChk.setBounds(275, 5, 100, 23);

                //---- bankChk ----
                bankChk.setText("Bank");
                bankChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        bankChkStateChanged(e);
                    }
                });
                optionsPanel.add(bankChk);
                bankChk.setBounds(275, 25, 55, 23);

                //---- priorityLootChk ----
                priorityLootChk.setText("Only Pickup Priority loot");
                priorityLootChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        priorityLootChkStateChanged(e);
                    }
                });
                optionsPanel.add(priorityLootChk);
                priorityLootChk.setBounds(275, 45, 140, 23);

                //---- usePotionsChk ----
                usePotionsChk.setText("Use Potions");
                usePotionsChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        usePotionsChkStateChanged(e);
                    }
                });
                optionsPanel.add(usePotionsChk);
                usePotionsChk.setBounds(415, 5, 85, 20);

                //---- slowMouseChk ----
                slowMouseChk.setText("Slow mouse");
                slowMouseChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        slowMouseChkStateChanged(e);
                    }
                });
                optionsPanel.add(slowMouseChk);
                slowMouseChk.setBounds(415, 25, 85, 23);

                //---- protectionPrayersChk ----
                protectionPrayersChk.setText("Use Protection prayers");
                protectionPrayersChk.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        protectionPrayersChkStateChanged(e);
                    }
                });
                optionsPanel.add(protectionPrayersChk);
                protectionPrayersChk.setBounds(415, 45, 140, 23);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < optionsPanel.getComponentCount(); i++) {
                        Rectangle bounds = optionsPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = optionsPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    optionsPanel.setMinimumSize(preferredSize);
                    optionsPanel.setPreferredSize(preferredSize);
                }
            }
            primaryTabbedContainer.addTab("Options", optionsPanel);

        }
        contentPane.add(primaryTabbedContainer);
        primaryTabbedContainer.setBounds(5, 5, 705, 545);

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JButton saveBtn;
    private JTabbedPane primaryTabbedContainer;
    private JPanel scriptInfoPanel;
    private JScrollPane scrollPane3;
    private JTextPane scriptInfoTPane;
    private JScrollPane scrollPane4;
    private JTextPane updateLogTPane;
    private JPanel lootingPanel;
    private JScrollPane scrollPane5;
    private JTable lootTable;
    private JLabel lootValueLbl;
    private JTextField lootValueTxtField;
    private JScrollPane scrollPane6;
    private JTextPane lootInformationTPane;
    private JButton addLootItem;
    private JButton removeLootItem;
    private JPanel bankPanel;
    private JScrollPane scrollPane7;
    private JTable bankingTable;
    private JButton addBankItem;
    private JButton removeBankItem;
    private JScrollPane scrollPane8;
    private JTextPane bankingInfoTPane;
    private JPanel optionsPanel;
    private JCheckBox useFoodChk;
    private JCheckBox useExcaliburChk;
    private JCheckBox pickupLootChk;
    private JCheckBox useSoulsplitChk;
    private JCheckBox soulsplitAlwaysChk;
    private JCheckBox switchPrayersChk;
    private JCheckBox useSummoningChk;
    private JCheckBox bankChk;
    private JCheckBox priorityLootChk;
    private JCheckBox usePotionsChk;
    private JCheckBox slowMouseChk;
    private JCheckBox protectionPrayersChk;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void showGUI() {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run() {
                setVisible(true);
            }
        });
    }

    public static void save() {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(SAVE_FILE_LOC);
            out = new ObjectOutputStream(fos);
            out.writeObject(Shared.instance());
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void load() {
        if(!new File(SAVE_FILE_LOC).exists())
            return;
        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(SAVE_FILE_LOC);
            in = new ObjectInputStream(fis);
            Shared.setInstance((Shared) in.readObject());
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        for(LootItem li : Shared.instance().priorityLoot)
            ((TableModel)lootTable.getModel()).addRow(new Object[] {li.getName(), li.getId(), li.lootInCombat()});
        for(LootItem li : Shared.instance().loot)
            ((TableModel)lootTable.getModel()).addRow(new Object[] {li.getName(), li.getId(), li.lootInCombat()});
        ((TableModel)lootTable.getModel()).addRow(new Object[] {null, null, new Boolean(false)});
        for(BankItem bi : Shared.instance().bankItems)
            ((TableModel)bankingTable.getModel()).addRow(new Object[] {bi.getName(), bi.getId(), bi.getQuantity()});
        ((TableModel)bankingTable.getModel()).addRow(new Object[] {null, null, new Integer(0)});
    }

    class TableModel extends DefaultTableModel
    {
        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c) == null ? null : getValueAt(0, c).getClass();
        }
    }
}
