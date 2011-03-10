/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.foundation.admin;

import com.funambol.framework.engine.source.SyncSource;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import com.funambol.admin.AdminException;
import com.funambol.admin.mo.SyncSourceManagementObject;
import com.funambol.admin.ui.SourceManagementPanel;

import com.funambol.foundation.engine.source.PIMContactSyncSource;
import com.funambol.foundation.engine.source.PIMCalendarSyncSource;
import com.funambol.foundation.engine.source.PIMSyncSource;

/**
 * This class implements the configuration panel for a PIMSyncSource
 *
 *
 * @version $Id: PIMSyncSourceConfigPanel.java,v 1.1.1.1 2008-03-20 21:38:30 stefano_fornari Exp $
 */
public abstract class PIMSyncSourceConfigPanel extends SourceManagementPanel
        implements Serializable {

    // --------------------------------------------------------------- Constants

    protected static final String PANEL_NAME = "Edit PIM SyncSource";

    public static final String NAME_ALLOWED_CHARS =
           "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.";

    // All possible PIM SyncSource's
    public static final Class[] SYNCSOURCE = {
        PIMContactSyncSource.class,
        PIMCalendarSyncSource.class,
    };

    // ------------------------------------------------------------ Private data

    /** Label for the panel's name */
    protected JLabel panelName = new JLabel();

    /** Border to pose the panel's title in evidence */
    protected TitledBorder titledBorder;

    protected JLabel nameLabel = new JLabel();
    protected JTextField nameValue = new JTextField();

    protected JLabel typeLabel = new JLabel();
    protected JComboBox typeCombo = new JComboBox();

    protected JLabel sourceUriLabel = new JLabel();
    protected JTextField sourceUriValue = new JTextField();

    protected JCheckBox encryption = new JCheckBox();

    protected JCheckBox encoding = new JCheckBox();

    protected JButton confirmButton = new JButton();

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of PIMSyncSourceConfigPanel.
     */
    public PIMSyncSourceConfigPanel() {
        init();
    }

    /**
     * Updates the panel form with values from the linked SyncSource.
     */
    public void updateForm() {

        if (!(getSyncSource() instanceof PIMSyncSource)) {
            notifyError(
                    new AdminException("This is not a PIMSyncSource! "
                    + "Unable to process SyncSource values."
                    )
                    );
            return;
        }

        PIMSyncSource syncSource = (PIMSyncSource)getSyncSource();

        if (getState() == STATE_INSERT) {
            confirmButton.setText("Add");
        } else if (getState() == STATE_UPDATE) {
            confirmButton.setText("Save");
        }

        sourceUriValue.setText(syncSource.getSourceURI());
        nameValue.setText(syncSource.getName());

        if (syncSource.getSourceURI() != null) {
            sourceUriValue.setEditable(false);
        }

        // Preparing to populate the combo box...
        typeCombo.removeAllItems();
        List types = getTypes();
        if (types == null) {
            types = new ArrayList();
        }
        for (int i=0; i< types.size(); i++) {
            typeCombo.addItem(types.get(i));
        }
        String typeToSelect = getTypeToSelect(syncSource);
        if (typeToSelect != null)  {
            typeCombo.setSelectedItem(typeToSelect);
        } else {
            typeCombo.setSelectedIndex(0);
        }

        SyncSourceManagementObject mo =
                (SyncSourceManagementObject)getManagementObject();
        String transformationsRequired = mo.getTransformationsRequired();

        if (transformationsRequired == null) {
            encryption.setSelected(false);
            encoding.setSelected(false);
        } else {
            if ("des;b64".equals(transformationsRequired)) {
                encryption.setSelected(true);
                encoding.setSelected(true);
            } else if ("b64".equals(transformationsRequired)) {
                encryption.setSelected(false);
                encoding.setSelected(true);
            } else {
                encryption.setSelected(false);
                encoding.setSelected(false);
            }
        }

        // In any case, force the encoding for SIF data
        if (isSIFSelected()) {
            encoding.setSelected(true);
            encoding.setEnabled(false);
        }

    }

    /**
     * Set preferredSize of the panel
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(525, 280);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Checks if the values provided by the user are all valid. If they are, the
     * method ends regularly.
     *
     * @throws IllegalArgumentException if name, uri, type or directory are empty
     *                                  (null or zero-length)
     */
    protected void validateValues() throws IllegalArgumentException {
        String value = null;

        value = nameValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(
                    "Field 'Name' cannot be empty. "
                    + "Please provide a SyncSource name.");
        }

        if (!StringUtils.containsOnly(value, NAME_ALLOWED_CHARS.toCharArray())) {
            throw new IllegalArgumentException(
                    "Only the following characters are allowed for field 'Name':"
                    + "\n" + NAME_ALLOWED_CHARS);
        }

        value = (String)typeCombo.getSelectedItem();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Field 'Type' cannot be empty. "
                    + "Please provide a SyncSource type.");
        }

        value = sourceUriValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(
                    "Field 'Source URI' cannot be empty. "
                    + "Please provide a SyncSource URI.");
        }
    }

    /**
     * Sets the SyncSource's properties with the values provided by the user.
     */
    protected void getValues() {
        PIMSyncSource syncSource = (PIMSyncSource)getSyncSource();

        syncSource.setSourceURI(sourceUriValue.getText().trim());
        syncSource.setName(nameValue.getText().trim());

        String transformationsRequired = null;
        if(encryption.isSelected()){
            transformationsRequired = "des;b64";
        } else if (encoding.isSelected()){
            transformationsRequired = "b64";
        }
        SyncSourceManagementObject mo =
                (SyncSourceManagementObject) getManagementObject();
        mo.setTransformationsRequired(transformationsRequired);

        setSyncSourceInfo(syncSource, (String)typeCombo.getSelectedItem());
    }

    /**
     * Checks whether a SIF content type is selected.
     *
     * @return true if in the combo box the selected string starts with "SIF"
     */
    protected boolean isSIFSelected() {

        if (typeCombo.getItemCount() == 0) {
            return false;
        }
        return ((String)typeCombo.getSelectedItem()).startsWith("SIF");
    }

    /**
     * Adds extra components just above the confirm button.
     *
     * @param x horizontal position
     * @param y vertical position
     * @param xGap standard horizontal gap
     * @param yGap standard vertical gap
     * @return the new vertical position
     */
    protected int addExtraComponents(int x, int y, int xGap, int yGap) {
        // Nothing

        return y;
    }

    /**
     * Returns the panel name
     * @return the panel name
     */
    protected String getPanelName() {
        return PANEL_NAME;
    }

    /**
     * Returns the available types
     * @return the available types;
     */
    protected abstract List getTypes();

    /**
     * Returns the type to select based on the given syncsource
     * @return the type to select based on the given syncsource
     */
    protected abstract String getTypeToSelect(SyncSource syncSource);

    /**
     * Sets the source info of the given syncsource based on the given selectedType
     * @param syncSource the source
     * @param selectedType the selected type
     */
    protected abstract void setSyncSourceInfo(SyncSource syncSource, String selectedType);

    // --------------------------------------------------------- Private methods

    /**
     * Creates the panel's components and layout.
     * @todo adjust layout
     */
    private void init(){
        // set layout
        this.setLayout(null);
        // set properties of label, position and border
        //  referred to the title of the panel
        titledBorder = new TitledBorder("");
        panelName.setFont(titlePanelFont);
        panelName.setText(getPanelName());
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder);

        final int LABEL_X = 14;
        final int VALUE_X = 170;
        int y = 60;
        final int GAP_X = 150;
        final int GAP_Y = 30;

        sourceUriLabel.setText("Source URI: ");
        sourceUriLabel.setFont(defaultFont);
        sourceUriLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
        sourceUriValue.setFont(defaultFont);
        sourceUriValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));

        y += GAP_Y; // New line

        nameLabel.setText("Name: ");
        nameLabel.setFont(defaultFont);
        nameLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
        nameValue.setFont(defaultFont);
        nameValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));
        y += GAP_Y; // New line

        typeLabel.setText("Type: ");
        typeLabel.setFont(defaultFont);
        typeLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
        typeCombo.setFont(defaultFont);
        typeCombo.setBounds(new Rectangle(VALUE_X, y, 350, 18));

        // What happens when the Type value is changed?
        typeCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (isSIFSelected()) {
                    encoding.setSelected(true); // SIFs always encoded
                    encoding.setEnabled(false);
                } else {
                    encryption.setSelected(false);
                    encoding.setSelected(false);
                    encoding.setEnabled(true);
                }
            }
        });

        y += GAP_Y; // New line
        int x = LABEL_X;

        y = addExtraComponents(x, y, GAP_X, GAP_Y); // Add other components, if needed

        encryption.setText("Encrypt data");
        encryption.setFont(defaultFont);
        encryption.setSelected(false);
        encryption.setBounds(x, y, 150, 25);

        // What happens if the encryption is enabled?
        encryption.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == e.SELECTED) {
                    encoding.setSelected(true); // Encryption implies encoding
                    encoding.setEnabled(false);
                }
                if (e.getStateChange() == e.DESELECTED) {
                    if (!isSIFSelected()) {
                        encoding.setEnabled(true);
                    }
                }
            }
        });

        y += GAP_Y; // New line

        encoding.setText("Encode data in Base 64");
        encoding.setFont(defaultFont);
        encoding.setSelected(false);
        encoding.setBounds(x, y, 150, 25);

        y += GAP_Y; // New line
        y += GAP_Y; // New line

        confirmButton.setFont(defaultFont);
        confirmButton.setText("Add");
        confirmButton.setBounds(VALUE_X, y, 70, 25);

        // What happens when the confirmButton is pressed?
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event ) {
                try {
                    validateValues();
                    getValues();
                    if (getState() == STATE_INSERT) {
                        PIMSyncSourceConfigPanel.this.actionPerformed(
                                new ActionEvent(PIMSyncSourceConfigPanel.this,
                                ACTION_EVENT_INSERT, event.getActionCommand()));
                    } else {
                        PIMSyncSourceConfigPanel.this.actionPerformed(
                                new ActionEvent(PIMSyncSourceConfigPanel.this,
                                ACTION_EVENT_UPDATE, event.getActionCommand()));
                    }
                } catch (Exception e) {
                    notifyError(new AdminException(e.getMessage(), e));
                }
            }
        });

        // Adds all components to the panel
        this.add(panelName        , null);
        this.add(nameLabel        , null);
        this.add(sourceUriLabel   , null);
        this.add(sourceUriValue   , null);
        this.add(nameValue        , null);
        this.add(typeLabel        , null);
        this.add(typeCombo        , null);
        this.add(encryption       , null);
        this.add(encoding         , null);
        this.add(confirmButton    , null);
    }

}
