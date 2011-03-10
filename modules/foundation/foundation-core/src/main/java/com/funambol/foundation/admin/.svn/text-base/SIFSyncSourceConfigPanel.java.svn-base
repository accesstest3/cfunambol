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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.*;

import java.io.Serializable;

import java.lang.reflect.Field;

import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import com.funambol.foundation.engine.source.SIFSyncSource;
import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSourceInfo;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.SourceManagementPanel;
import com.funambol.admin.mo.SyncSourceManagementObject;


/**
 * This class implements the configuration panel for a SIFSyncSource
 *
 * @version $Id: SIFSyncSourceConfigPanel.java,v 1.1.1.1 2008-03-20 21:38:31 stefano_fornari Exp $
 */
public class SIFSyncSourceConfigPanel
extends SourceManagementPanel
implements Serializable {

    // --------------------------------------------------------------- Constants

    public static final String DEFAULT_TRANSFORMATION = "b64";

    public static final String NAME_ALLOWED_CHARS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.";

    private static final String TYPE_SIF_C = "SIF-C";
    private static final String TYPE_SIF_E = "SIF-E";
    private static final String TYPE_SIF_N = "SIF-N";
    private static final String TYPE_SIF_T = "SIF-T";

    public static final String CONTENT_TYPE_SIF_C = "text/x-s4j-sifc";
    public static final String VERSION_SIF_C      = "1.0";
    public static final String CONTENT_TYPE_SIF_E = "text/x-s4j-sife";
    public static final String VERSION_SIF_E      = "1.0";
    public static final String CONTENT_TYPE_SIF_N = "text/x-s4j-sifn";
    public static final String VERSION_SIF_N      = "1.0";
    public static final String CONTENT_TYPE_SIF_T = "text/x-s4j-sift";
    public static final String VERSION_SIF_T      = "1.0";

    private static final String ROOT_TAG_FIELD = "ROOT_TAG";
    // ------------------------------------------------------------ Private data
    /** label for the panel's name */
    private JLabel panelName = new JLabel();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    private JLabel nameLabel           = new JLabel();
    private JTextField nameValue       = new JTextField();

    private JLabel typeLabel           = new JLabel();
    private JComboBox typeCombo        = new JComboBox();

    private JLabel sourceUriLabel      = new JLabel();
    private JTextField sourceUriValue  = new JTextField();

    private JLabel directoryLabel      = new JLabel();
    private JTextField directoryValue  = new JTextField();

    private JLabel twinPropertiesLabel = new JLabel();
    private JLabel descrTwinPropLabel  = new JLabel();
    private JScrollPane scrollTable    = null;

    private JList twinPropertiesValue  = new JList();

    private JLabel multiUserLabel      = new JLabel();
    private JCheckBox multiUserValue   = new JCheckBox();

    private JButton confirmButton      = new JButton();

    private List fieldsList            = null;

    private String transformationsRequired = null;

    // ------------------------------------------------------------ Constructors
    public SIFSyncSourceConfigPanel() {
        init();
    }

    // --------------------------------------------------------- Private methods
    /**
     * Create the panel
     *
     * @throws Exception if error occures during creation of the panel
     */
    private void init() {
        this.setLayout(null);
        //
        // Set properties of label, position and border referred to the title
        //of the panel
        //
        titledBorder1 = new TitledBorder("");

        panelName.setFont(titlePanelFont);
        panelName.setText("Edit SIF SyncSource");
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        int y  = 60;
        int dy = 30;
        int x1 = 14;
        int x2 = 170;
        int w1 = 150;
        int w2 = 350;
        int h  = 18;

        sourceUriLabel.setText("Source URI: ");
        sourceUriLabel.setFont(defaultFont);
        sourceUriLabel.setBounds(x1, y, w1, h);
        sourceUriValue.setFont(defaultFont);
        sourceUriValue.setBounds(x2, y, w2, h);

        y += dy;

        nameLabel.setText("Name: ");
        nameLabel.setFont(defaultFont);
        nameLabel.setBounds(x1, y, w1, h);
        nameValue.setFont(defaultFont);
        nameValue.setBounds(x2, y, w2, h);

        y += dy;

        typeLabel.setText("Type: ");
        typeLabel.setFont(defaultFont);
        typeLabel.setBounds(x1, y, w1, h);
        typeCombo.setFont(defaultFont);
        typeCombo.setBounds(x2, y, 100, h);
        typeCombo.addItem(TYPE_SIF_C);
        typeCombo.addItem(TYPE_SIF_E);
        typeCombo.addItem(TYPE_SIF_N);
        typeCombo.addItem(TYPE_SIF_T);

        typeCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                handleTypeSelected();
            }
        });

        handleTypeSelected();

        y += dy;

        directoryLabel.setText("Source Directory: ");
        directoryLabel.setFont(defaultFont);
        directoryLabel.setBounds(x1, y, w1, h);
        directoryValue.setFont(defaultFont);
        directoryValue.setBounds(x2, y, w2, h);

        y += dy;

        twinPropertiesLabel.setText("Twin properties: ");
        twinPropertiesLabel.setFont(defaultFont);
        twinPropertiesLabel.setBounds(x1, y, w1, h);
        descrTwinPropLabel.setText("(Used to identify twins)");
        descrTwinPropLabel.setFont(defaultFont);
        descrTwinPropLabel.setBounds(x1, (y + 15), w1, h);

        twinPropertiesValue.setFont(defaultFont);
        twinPropertiesValue.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        twinPropertiesValue.setAutoscrolls(true);

        scrollTable = new JScrollPane(twinPropertiesValue);
        scrollTable.setWheelScrollingEnabled(true);
        scrollTable.setBounds(x2, y, 200, 66);
        scrollTable.setColumnHeader(null);
        scrollTable.setMinimumSize(new Dimension(200, 66));
        scrollTable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        y += 45;
        y += dy;

        multiUserLabel.setText("MultiUser: ");
        multiUserLabel.setFont(defaultFont);
        multiUserLabel.setBounds(x1, y, w1, h);
        multiUserValue.setSelected(false);
        multiUserValue.setBounds(x2, y, h, h);

        y += 5;
        y += dy;

        confirmButton.setFont(defaultFont);
        confirmButton.setText("Add");
        confirmButton.setBounds(x2, y, 70, 25);
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event ) {
                try {
                    validateValues();
                    getValues();
                    if (getState() == STATE_INSERT) {
                        SIFSyncSourceConfigPanel.this.actionPerformed(new ActionEvent(SIFSyncSourceConfigPanel.this, ACTION_EVENT_INSERT, event.getActionCommand()));
                    } else {
                        SIFSyncSourceConfigPanel.this.actionPerformed(new ActionEvent(SIFSyncSourceConfigPanel.this, ACTION_EVENT_UPDATE, event.getActionCommand()));
                    }
                } catch (Exception e) {
                    notifyError(new AdminException(e.getMessage()));
                }
            }
        });

        //
        // Add all components to the panel
        //
        this.add(panelName          , null);
        this.add(sourceUriLabel     , null);
        this.add(sourceUriValue     , null);
        this.add(nameLabel          , null);
        this.add(nameValue          , null);
        this.add(typeLabel          , null);
        this.add(typeCombo          , null);
        this.add(directoryLabel     , null);
        this.add(directoryValue     , null);
        this.add(twinPropertiesLabel, null);
        this.add(scrollTable        , null);
        this.add(descrTwinPropLabel , null);
        this.add(multiUserLabel     , null);
        this.add(multiUserValue     , null);
        this.add(confirmButton      , null);
    }

    /**
     * Load the SyncSource
     */
    public void updateForm() {
        if (!(getSyncSource() instanceof SIFSyncSource)) {
            notifyError(new AdminException(
            "This is not a SIFSyncSource! Unable to process SyncSource values.")
            );
            return;
        }


        SyncSourceManagementObject mo = (SyncSourceManagementObject)getManagementObject();
        this.transformationsRequired = mo.getTransformationsRequired();

        SIFSyncSource syncSource = (SIFSyncSource) getSyncSource();

        if (getState() == STATE_INSERT) {
            confirmButton.setText("Add");
        } else if (getState() == STATE_UPDATE) {
            confirmButton.setText("Save");
        }

        directoryValue.setText(syncSource.getSourceDirectory());
        sourceUriValue.setText(syncSource.getSourceURI()      );
        nameValue.setText     (syncSource.getName()           );

        String type = null;
        SyncSourceInfo info = syncSource.getInfo();

        if (info != null) {
            type = info.getPreferredType().getType();
        }
        if (CONTENT_TYPE_SIF_C.equals(type)) {
            typeCombo.setSelectedItem(TYPE_SIF_C);
        } else if (CONTENT_TYPE_SIF_E.equals(type)) {
            typeCombo.setSelectedItem(TYPE_SIF_E);
        } else if (CONTENT_TYPE_SIF_N.equals(type)) {
            typeCombo.setSelectedItem(TYPE_SIF_N);
        } else if (CONTENT_TYPE_SIF_T.equals(type)) {
            typeCombo.setSelectedItem(TYPE_SIF_T);
        } else {
            //
            // We use the first type as default
            //
            typeCombo.setSelectedIndex(0);
        }

        selectFields(syncSource);

        multiUserValue.setSelected(syncSource.isMultiUser());

        if (syncSource.getSourceURI() != null) {
            sourceUriValue.setEditable(false);
        }
    }

    /**
     * Set preferredSize of the panel used by the ScrollBar
     *
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(525, 330);
    }

    // --------------------------------------------------------- Private methods
    /**
     * Checks if the values provided by the user are all valid.
     * In the case of errors, a IllegalArgumentException is thrown.
     *
     * @throws IllegalArgumentException if:
     * <ul>
     *  <li>name, uri, type or directory are empty (null or zero-length)
     *  <li>the types list length does not match the versions list length
     * </ul>
     */
    private void validateValues() throws IllegalArgumentException {
        String value = null;

        value = nameValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Field 'Name' cannot be empty. Please provide a SyncSource name.");
        }

        if (!StringUtils.containsOnly(value, NAME_ALLOWED_CHARS.toCharArray())) {
            throw new IllegalArgumentException("Only the following characters are allowed for field 'Name': \n" + NAME_ALLOWED_CHARS);
        }

        value = sourceUriValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Field 'Source URI' cannot be empty. Please provide a SyncSource URI.");
        }

        value = directoryValue.getText();
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Field 'Source directory' cannot be empty. Please provide a SyncSource db directory.");
        }

    }

    /**
     * Set syncSource properties with the values provided by the user.
     */
    private void getValues() {
        SIFSyncSource syncSource = (SIFSyncSource)getSyncSource();

        syncSource.setSourceURI      (sourceUriValue.getText().trim());
        syncSource.setName           (nameValue.getText().trim()     );
        syncSource.setSourceDirectory(directoryValue.getText().trim());

        syncSource.setEncode(false);

        String typeSelected = (String)typeCombo.getSelectedItem();

        ContentType[]  contentTypes = new ContentType[1];
        if (typeSelected.equals(TYPE_SIF_C)) {
            contentTypes[0] = new ContentType(CONTENT_TYPE_SIF_C, VERSION_SIF_C);
        } else if (typeSelected.equals(TYPE_SIF_E)) {
            contentTypes[0] = new ContentType(CONTENT_TYPE_SIF_E, VERSION_SIF_E);
        } else if (typeSelected.equals(TYPE_SIF_N)) {
            contentTypes[0] = new ContentType(CONTENT_TYPE_SIF_N, VERSION_SIF_N);
        } else if (typeSelected.equals(TYPE_SIF_T)) {
            contentTypes[0] = new ContentType(CONTENT_TYPE_SIF_T, VERSION_SIF_T);
        } else {
            notifyError(
                           new AdminException(
                               "The selected type isn't valid (" + typeSelected + ")"
                           )
                       );
        }

        syncSource.setInfo(new SyncSourceInfo(contentTypes, 0));

        syncSource.setTargetsToCompare(
             arrayToList(twinPropertiesValue.getSelectedValues())
        );

        syncSource.setMultiUser(multiUserValue.isSelected());

        if (StringUtils.isEmpty(transformationsRequired)) {
            SyncSourceManagementObject mo =
                (SyncSourceManagementObject)getManagementObject();
            mo.setTransformationsRequired(DEFAULT_TRANSFORMATION);
        }
    }


    /**
     * Loads the list of fields for the selected type and selects the fields
     * used by the syncsouce
     */
    private void handleTypeSelected() {
        String typeSelected = (String)typeCombo.getSelectedItem();
        List fieldsList     = null;

        if (typeSelected.equals(TYPE_SIF_C)) {
            fieldsList = getFieldsList("com.funambol.common.pim.contact.SIFC");
        } else if (typeSelected.equals(TYPE_SIF_E)) {
            fieldsList = getFieldsList("com.funambol.common.pim.calendar.SIFE");
        } else if (typeSelected.equals(TYPE_SIF_N)) {
            fieldsList = getFieldsList("com.funambol.common.pim.note.SIFN");
        } else if (typeSelected.equals(TYPE_SIF_T)) {
            fieldsList = getFieldsList("com.funambol.common.pim.calendar.SIFT");
        } else {
            notifyError(
                           new AdminException(
                               "The type selected isn't valid (" + typeSelected + ")"
                           )
                       );
        }

        twinPropertiesValue.removeAll();
        twinPropertiesValue.setListData(fieldsList.toArray());
        twinPropertiesValue.ensureIndexIsVisible(0);

        this.fieldsList = fieldsList;

        if (super.getSyncSource() != null) {
             if (super.getSyncSource() instanceof SIFSyncSource) {
                 SIFSyncSource syncsource = (SIFSyncSource)super.getSyncSource();
                 selectFields(syncsource);
             }
        }
    }

    /**
     * Selects the fields used by the syncsouce
     * @param syncsource SIFSyncSource
     */
    private void selectFields(SIFSyncSource syncsource) {
        List fieldsToSelect = syncsource.getTargetsToCompare();
        if (fieldsToSelect == null) {
            twinPropertiesValue.removeSelectionInterval(0, twinPropertiesValue.getMaxSelectionIndex());
            return ;
        }

        int numFieldsToSelect = fieldsToSelect.size();
        int[] indices         = new int[numFieldsToSelect];

        for (int i=0; i<numFieldsToSelect; i++) {
            indices[i] = fieldsList.indexOf(fieldsToSelect.get(i));
        }

        twinPropertiesValue.setSelectedIndices(indices);
    }

    /**
     * Using reflection, returns a ordered list of the values of the fields
     * of the given class excluding ROOT_RAG_FIELD
     * @param className String
     * @return List
     */
    private List getFieldsList(String className) {
        List fieldsList = new ArrayList();
        try {
            Class sifc = Class.forName(className);
            Field[] fields = sifc.getDeclaredFields();

            for (int i = 0; i < fields.length; i++) {
                if (!ROOT_TAG_FIELD.equals(fields[i].getName())) {
                    fieldsList.add(fields[i].get(null));
                }
            }
            Collections.sort(fieldsList);
        } catch (Exception ex) {
            //
            // returns an empty list
            //
        }
        return fieldsList;
    }

    /**
     * Converts the given array into a List
     * @param objects Object[]
     * @return List
     */
    private List arrayToList(Object[] objects) {

        List list = new ArrayList();
        if (objects == null || objects.length == 0) {
            return list;
        }
        for (int i=0; i<objects.length; i++) {
            list.add(objects[i]);
        }
        return list;

    }
}
