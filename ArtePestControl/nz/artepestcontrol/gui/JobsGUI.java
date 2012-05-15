/*
 * Created by JFormDesigner on Sun May 01 21:29:35 NZST 2011
 */

package nz.artepestcontrol.gui;

import nz.artepestcontrol.game.Job;
import nz.artepestcontrol.game.PestControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Taylor Lodge
 */
public class JobsGUI extends JFrame
{
    private static Object[] comboValues = new Object[Job.values().length];

    public JobsGUI() {
        for (int i = 0; i < Job.values().length; i++)
            comboValues[i] = Job.values()[i].getName();
        initComponents();
    }

    private void delJobBtnActionPerformed(ActionEvent e) {
        if (jobsList.getSelectedIndex() != -1) {
            PestControl.jobs.remove(jobsList.getSelectedIndex());
            jobsModel.removeElementAt(jobsList.getSelectedIndex());
        }
    }

    private void addJobBtnActionPerformed(ActionEvent e) {
        if (jobComboBox.getSelectedIndex() != -1) {
            PestControl.jobs.add(Job.values()[jobComboBox.getSelectedIndex()]);
            jobsModel.addElement(jobComboBox.getSelectedItem());
        }
    }

    private void okBtnActionPerformed(ActionEvent e) {
        mySetVisible(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        jobPane = new JScrollPane();
        jobsList = new JList(jobsModel);
        addJobBtn = new JButton();
        delJobBtn = new JButton();
        jobComboBox = new JComboBox(comboValues);
        okButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== jobPane ========
        {
            jobPane.setViewportView(jobsList);
        }
        contentPane.add(jobPane);
        jobPane.setBounds(0, 0, 392, jobPane.getPreferredSize().height);

        //---- addJobBtn ----
        addJobBtn.setText("Add Job");
        addJobBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                addJobBtnActionPerformed(e);
            }
        });
        contentPane.add(addJobBtn);
        addJobBtn.setBounds(0, 130, 95, 25);

        //---- delJobBtn ----
        delJobBtn.setText("Delete Job");
        delJobBtn.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                delJobBtnActionPerformed(e);
            }
        });
        contentPane.add(delJobBtn);
        delJobBtn.setBounds(300, 130, 95, 25);
        contentPane.add(jobComboBox);
        jobComboBox.setBounds(95, 130, 205, 25);

        //---- okButton ----
        okButton.setText("OK");
        contentPane.add(okButton);
        okButton.setBounds(0, 155, 395, 45);
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent actionEvent) {
                okBtnActionPerformed(actionEvent);
            }
        });

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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane jobPane;
    private JList jobsList;
    private JButton addJobBtn;
    private JButton delJobBtn;
    private JComboBox jobComboBox;
    private JButton okButton;
    public DefaultListModel jobsModel = new DefaultListModel();
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void mySetVisible(final boolean visible) {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run() {
                setVisible(visible);
            }
        });
    }

    public void delJob(int index) {
        jobsModel.removeElementAt(index);
    }
}
