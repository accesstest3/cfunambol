/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
package com.funambol.admin.module.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Rectangle;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceInfo;

import com.funambol.admin.AdminException;
import com.funambol.admin.mo.SyncSourceManagementObject;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.SourceManagementPanel;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

/**
 * Panel shown when a SyncSource does not have a specified configuration panel.
 *
 * @version $Id$
 */
public class DefaultSyncSourceConfigPanel extends SourceManagementPanel
implements Serializable {

    // --------------------------------------------------------------- Constants
    public static final String NAME_ALLOWED_CHARS =
           "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.";
    // ------------------------------------------------------------ Private data

    /** Label for the panel's name */
    private JLabel panelName = new JLabel();

    /** Border to pose the panel's title in evidence */
    private TitledBorder titledBorder;

    private JLabel     nameLabel = new JLabel();
    private JTextField nameValue = new JTextField();

    private JLabel     sourceUriLabel = new JLabel();
    private JTextField sourceUriValue = new JTextField();

    private JLabel     infoTypesLabel = new JLabel();
    private JTextField infoTypesValue = new JTextField();

    private JLabel     infoVersionsLabel = new JLabel();
    private JTextField infoVersionsValue = new JTextField();

    private JCheckBox encryption = new JCheckBox();

    private JCheckBox encoding = new JCheckBox();

    private JButton confirmButton = new JButton();

    private String sourceTypeDescription = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a new ShowSyncSourceExceptionPanel
     */
    public DefaultSyncSourceConfigPanel(String sourceTypeDescription) {
        try {
            this.sourceTypeDescription = sourceTypeDescription;
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Set preferredSize of the panel
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(525, 330);
    }

    /**
     * Updates the panel form with values from the linked SyncSource.
     */
    public void updateForm() {

        SyncSource syncSource = getSyncSource();

        if (getState() == STATE_INSERT) {
            confirmButton.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_ADD));
        } else if (getState() == STATE_UPDATE) {
            confirmButton.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE));
        }

        sourceUriValue.setText(syncSource.getSourceURI());
        nameValue.setText(syncSource.getName());

        if (syncSource.getSourceURI() != null) {
            sourceUriValue.setEditable(false);
        }

        SyncSourceInfo info = syncSource.getInfo();
        if (info != null) {
            ContentType[] types = info.getSupportedTypes();

            StringBuffer typesList    = new StringBuffer(),
                         versionsList = new StringBuffer();
            for (int i=0; ((types != null) && (i<types.length)); ++i) {
                if (typesList.length() > 0) {
                    typesList.append(',');
                    versionsList.append(',');
                }
                typesList.append(types[i].getType());
                versionsList.append(types[i].getVersion());
            }

            infoTypesValue.setText(typesList.toString());
            infoVersionsValue.setText(versionsList.toString());
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
    }

    // --------------------------------------------------------- Private methods
    /**
     * Creates the panel
     */
    private void init() {

        this.setLayout(null);

        titledBorder = new TitledBorder("");

        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText(Bundle.getMessage(Bundle.LABEL_SYNC_SOURCE_EDIT) + " " + sourceTypeDescription);
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder);

        final int LABEL_X = 14;
        final int VALUE_X = 170;
        int y = 60;
        final int GAP_Y = 30;

        sourceUriLabel.setText(Bundle.getMessage(Bundle.LABEL_SYNC_SOURCE_URI) + ": ");
        sourceUriLabel.setFont(defaultFont);
        sourceUriLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
        sourceUriValue.setFont(defaultFont);
        sourceUriValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));
        y += GAP_Y; // New line

        nameLabel.setText(Bundle.getMessage(Bundle.LABEL_SYNC_SOURCE_NAME) + ": ");
        nameLabel.setFont(defaultFont);
        nameLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
        nameValue.setFont(defaultFont);
        nameValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));
        y += GAP_Y; // New line

        infoTypesLabel.setText(Bundle.getMessage(Bundle.LABEL_SYNC_SOURCE_SUPPORTED_TYPES) + ": ");
        infoTypesLabel.setFont(defaultFont);
        infoTypesLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
        infoTypesValue.setFont(defaultFont);
        infoTypesValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));
        y += GAP_Y; // New line

        infoVersionsLabel.setText(Bundle.getMessage(Bundle.LABEL_SYNC_SOURCE_SUPPORTED_VERSIONS) + ": ");
        infoVersionsLabel.setFont(defaultFont);
        infoVersionsLabel.setBounds(new Rectangle(LABEL_X, y, 150, 18));
        infoVersionsValue.setFont(defaultFont);
        infoVersionsValue.setBounds(new Rectangle(VALUE_X, y, 350, 18));
        y += GAP_Y; // New line

        int x = LABEL_X;

        encryption.setText(Bundle.getMessage(Bundle.LABEL_SYNC_SOURCE_ENCRYPTION));
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
                    encoding.setSelected(false);
                    encoding.setEnabled(true);
                }
            }
        });
        y += GAP_Y; // New line

        encoding.setText(Bundle.getMessage(Bundle.LABEL_SYNC_SOURCE_ENCODING));
        encoding.setFont(defaultFont);
        encoding.setSelected(false);
        encoding.setBounds(x, y, 150, 25);
        y += GAP_Y; // New line
        y += GAP_Y; // New line

        confirmButton.setFont(defaultFont);
        confirmButton.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_ADD));
        confirmButton.setBounds(VALUE_X, y, 70, 25);

        // What happens when the confirmButton is pressed?
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event ) {
                try {
                    validateValues();
                    getValues();
                    if (getState() == STATE_INSERT) {
                        DefaultSyncSourceConfigPanel.this.actionPerformed(
                                new ActionEvent(DefaultSyncSourceConfigPanel.this,
                                ACTION_EVENT_INSERT, event.getActionCommand()));
                    } else {
                        DefaultSyncSourceConfigPanel.this.actionPerformed(
                                new ActionEvent(DefaultSyncSourceConfigPanel.this,
                                ACTION_EVENT_UPDATE, event.getActionCommand()));
                    }
                } catch (Exception e) {
                    notifyError(new AdminException(e.getMessage()));
                }
            }
        });

        // Adds all components to the panel
        this.add(panelName        , null);
        this.add(nameLabel        , null);
        this.add(sourceUriLabel   , null);
        this.add(sourceUriValue   , null);
        this.add(nameValue        , null);
        this.add(infoTypesLabel   , null);
        this.add(infoTypesValue   , null);
        this.add(infoVersionsLabel, null);
        this.add(infoVersionsValue, null);
        this.add(encryption       , null);
        this.add(encoding         , null);
        this.add(confirmButton    , null);
    }

    /**
     * Checks if the values provided by the user are all valid. If they are, the
     * method ends regularly.
     *
     * @throws IllegalArgumentException if the needed values are empty (null or
     *                                  zero-length)
     */
    private void validateValues() throws IllegalArgumentException {
        String value = null;

        value = sourceUriValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(
                    "Field 'Source URI' cannot be empty. "
                    + "Please provide a SyncSource URI.");
        }

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

        int typesCount = new StringTokenizer(infoTypesValue.getText(), ",").countTokens();
        if (typesCount == 0) {
            throw new IllegalArgumentException(
                "Field 'Supported types' cannot be empty. " +
                "Please provide one or more supported types.");
        }

        value = infoVersionsValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(
                    "Field 'Supported versions' cannot be empty. "
                    + "Please provide one or more supported versions.");
        }

        if (typesCount != new StringTokenizer(infoVersionsValue.getText(), ",").countTokens()) {
            throw new IllegalArgumentException(
                "Number of supported types does not match number of versions");
        }

    }

    /**
     * Sets the SyncSource's properties with the values provided by the user.
     */
    private void getValues() throws AdminException {

        SyncSource syncSource = getSyncSource();

        Class c = syncSource.getClass();
        Method setSourceURI = null, setName = null, setInfo = null;
        try {

            setSourceURI =
                      c.getMethod("setSourceURI", new Class[]{String.class});
            setName = c.getMethod("setName", new Class[]{String.class});
            setInfo = c.getMethod("setInfo", new Class[]{SyncSourceInfo.class});

        } catch(NoSuchMethodException e) {
            throw new AdminException(e.getMessage(), e);
        }

        try {
            setSourceURI.invoke(syncSource, new Object[] {sourceUriValue.getText().trim()});
            setName.invoke(syncSource, new Object[] {nameValue.getText().trim()});

            StringTokenizer types    =
                new StringTokenizer(infoTypesValue.getText()   , ",");
            StringTokenizer versions =
                new StringTokenizer(infoVersionsValue.getText(), ",");
            ContentType[] contentTypes= new ContentType[types.countTokens()];

            for(int i = 0; i<contentTypes.length; ++i) {
              contentTypes[i] = new ContentType(types.nextToken().trim()   ,
                                                versions.nextToken().trim());
            }

            setInfo.invoke(syncSource, new Object[] {new SyncSourceInfo(contentTypes, 0)});

            String transformationsRequired = null;
            if(encryption.isSelected()){
                transformationsRequired = "des;b64";
            } else if (encoding.isSelected()){
                transformationsRequired = "b64";
            }

            SyncSourceManagementObject mo =
                    (SyncSourceManagementObject)getManagementObject();
            mo.setTransformationsRequired(transformationsRequired);
        } catch(IllegalAccessException e) {
            notifyError(new AdminException(e.getMessage(), e));
        } catch(InvocationTargetException e) {
            notifyError(new AdminException(e.getMessage(), e));
        }
    }

}
