/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2006 - 2007 Funambol, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission 
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE 
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License 
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 * 
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite 
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 * 
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 * 
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably 
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.admin.tools;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.openide.windows.WindowManager;

import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

/**
 * Shows the Options Panel.
 *
 * @version $Id: OptionsDialog.java,v 1.9 2007-11-28 14:31:27 luigiafassina Exp $
 */
public class OptionsDialog extends JDialog {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data
    private JTabbedPane optionsJTabbedPane;

    private JLabel     contextPathLabel;
    private JTextField contextPathEntry;
    private JLabel     maxResultsLabel ;
    private JTextField maxResultsEntry ;
    private JCheckBox  debugCheck      ;
    private JLabel     noteLabel       ;
    private JButton    confirmButton   ;
    private JButton    cancelButton    ;

    // Needed to work with different font size
    private int noteLabelWidth        = 0;
    private int contextPathLabelWidth = 0;
    private int panelWidth            = 0;

    // ------------------------------------------------------------ Construstors

    /**
     * Creates a new instance of OptionsDialog and loads the initial values.
     */
    public OptionsDialog() {
        super(WindowManager.getDefault().getMainWindow());
        try {
            init();

            loadInitValue();

        } catch (Exception e) {
            Log.error(
                Bundle.getMessage(Bundle.ERROR_CREATING + getClass().getName())
                + e.getMessage()
            );
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates the panel
     *
     * @throws Exception if error occures during creation of the panel
     */
    private void init() throws Exception {

        optionsJTabbedPane = new JTabbedPane();
        getContentPane().add(optionsJTabbedPane);

        getContentPane().setLayout(null);

        setFont(GuiFactory.defaultFont);
        contextPathLabel = new JLabel    ();
        contextPathEntry = new JTextField();
        maxResultsLabel  = new JLabel    ();
        maxResultsEntry  = new JTextField();
        debugCheck       = new JCheckBox ();
        noteLabel        = new JLabel    ();
        confirmButton    = new JButton   ();
        cancelButton     = new JButton   ();

        String contextPathLabelText = Bundle.getMessage(Bundle.OPTIONS_PANEL_CONTEXT_PATH) + ':';
        contextPathLabel.setText(contextPathLabelText);
        Font contextPathFont  = contextPathLabel.getFont();
        contextPathLabelWidth = contextPathLabel.getFontMetrics(contextPathFont).stringWidth(contextPathLabelText);

        int x1  = 20 ;
        int dx1 = contextPathLabelWidth;
        int x2  = 30 + contextPathLabelWidth;
        int dx2 = 145;
        int y   = 20 ;
        int dy  = 30 ;

        panelWidth = x2 +dx2 + 20;

        contextPathLabel.setBounds(x1, y, dx1, 20);
        contextPathEntry.setBounds(x2, y, dx2, 20);

        y += dy;
        maxResultsLabel.setText(
            Bundle.getMessage(Bundle.OPTIONS_PANEL_MAX_RESULTS) + ':');
        maxResultsLabel.setBounds(x1, y, dx1, 20);
        maxResultsEntry.setBounds(x2, y, dx2, 20);

        y += dy;
        debugCheck.setText(Bundle.getMessage(Bundle.OPTIONS_PANEL_DEBUG));
        debugCheck.setBounds(17, y, 150, 23);
        debugCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setDebugState(debugCheck.isSelected());
            }
        });
        debugCheck.setLayout(null);

        y += dy;
        String noteLabelText = Bundle.getMessage(Bundle.OPTIONS_PANEL_NOTE);
        Font noteLabelFont = noteLabel.getFont();
        noteLabelWidth = noteLabel.getFontMetrics(noteLabelFont).stringWidth(noteLabelText);
        noteLabel.setText(noteLabelText);
        noteLabel.setBounds(x1, y, noteLabelWidth, 20);

        if ((x1 + noteLabelWidth + 20) > panelWidth) {
            panelWidth = x1 + noteLabelWidth + 20;
        }

        y += dy;
        confirmButton.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE));
        confirmButton.setBounds(new Rectangle(panelWidth - 20 - 75 - 80, y, 75, 20));
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveOptions();
            }
        });

        cancelButton = new JButton(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL));
        cancelButton.setBounds(panelWidth - 20 - 75, y, 75, 20);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancelAction();
            }
        });

        // Set the characteristics for this dialog instance
        setTitle(Bundle.getMessage(Bundle.OPTIONS_PANEL_NAME));
        setSize(panelWidth, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setModal(true);
        setLocationRelativeTo(this.getParent());

        getContentPane().add(contextPathLabel);
        getContentPane().add(contextPathEntry);
        getContentPane().add(maxResultsLabel );
        getContentPane().add(maxResultsEntry );
        getContentPane().add(debugCheck      );
        getContentPane().add(noteLabel       );
        getContentPane().add(confirmButton, null);
        getContentPane().add(cancelButton , null);
    }

    /**
     * Loads initial values.
     */
    private void loadInitValue() {
        AdminConfig config = AdminConfig.getAdminConfig();
        contextPathEntry.setText(config.getContextPath());
        maxResultsEntry.setText (String.valueOf(config.getMaxResults()));
        debugCheck.setSelected  (config.isDebugEnabled ());
    }

    /**
     * Saves options
     */
    private void saveOptions() {
        try {
            validateInput();
        } catch (IllegalArgumentException e) {
             JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                                           JOptionPane.ERROR_MESSAGE);
            return;
        }

        AdminConfig config = AdminConfig.getAdminConfig();
        config.setContextPath(contextPathEntry.getText().trim());
        config.setMaxResults (Integer.parseInt(maxResultsEntry.getText().trim()));
        config.setEnableDebug(debugCheck.isSelected());

        config.saveConfiguration();

        this.setVisible(false);
    }

    /**
     * Returns to the previous panel.
     */
    private void cancelAction() {
        this.setVisible(false);
    }

    /**
     * Enables/disables the debug field accordingly to <i>enable</i>
     *
     * @param enable true if the debug must be enabled, false otherwise
     */
    private void setDebugState(boolean enable) {
        debugCheck.setSelected(enable);
    }

    /**
     * Validates the panel input. If an incorrect parameter is found, an
     * IllegalArguementException is thrown with a message explaining the error.
     *
     * @throws IllegalAgumentException in case of wrong input values
     */
    private void validateInput() throws IllegalArgumentException {
        String value;

        value = maxResultsEntry.getText().trim();
        try {
            int i = Integer.parseInt(value);
            if (i<0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_NUMERIC_INPUT),
                new String[] { Bundle.getMessage(Bundle.OPTIONS_PANEL_MAX_RESULTS),
                               "0",
                               String.valueOf(Integer.MAX_VALUE)
                             }
            );

            throw new IllegalArgumentException(msg);
        }
    }
}
