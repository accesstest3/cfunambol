/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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
package com.funambol.admin.device.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.*;

import com.funambol.framework.tools.CharsetTools;

import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.ConvertDatePolicy;
import com.funambol.framework.server.Sync4jDevice;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.ui.TimeZonesComboModel;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

/**
 * Generate a panel used to insert a device.
 * The panel includes all the necessary fields to create a device:
 * ID, Type, Description, Address, Phone Number (Msisdn) and Notification Builder
 *
 * @version $Id: CreateDevicePanel.java,v 1.17 2007-11-28 10:28:17 nichele Exp $
 **/
public class CreateDevicePanel extends JPanel {

    // --------------------------------------------------------------- Constants

    private static final String DEFAULT_DEVICE_CHARSET = "UTF-8";

    /** constant for query mode */
    private static int MODE_UPDATE = 0;
    private static int MODE_INSERT = 1;

    private static final String NOTIFICATION_BUILDER_DS_12 =
            "com/funambol/server/notification/DSNotificationBuilder.xml";
    private static final String NOTIFICATION_BUILDER_DS_11 =
            "com/funambol/server/notification/DS11NotificationBuilder.xml";

    private static final String LABEL_BUILDER_11 =
            Bundle.getMessage(Bundle.DEVICE_PANEL_NOTIFICATION_BUILDER_11);

    private static final String LABEL_BUILDER_12 =
            Bundle.getMessage(Bundle.DEVICE_PANEL_NOTIFICATION_BUILDER_12);

    private static final String CONVERSION_TO_CURRENT_TZ_ENABLED =
            Bundle.getMessage(Bundle.DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_ENABLED);

    private static final String CONVERSION_TO_CURRENT_TZ_ENABLED_DEFAULT =
            Bundle.getMessage(Bundle.DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_ENABLED_DEFAULT);

    private static final String CONVERSION_TO_CURRENT_TZ_DISABLED =
            Bundle.getMessage(Bundle.DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_DISABLED);

    private static final String CONVERSION_TO_CURRENT_TZ_DISABLED_DEFAULT =
            Bundle.getMessage(Bundle.DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ_DISABLED_DEFAULT);

    private static final String DEFAULT_NOTIFICATION_BUILDER = LABEL_BUILDER_12;

    // ------------------------------------------------------------ Private data

    /** panel mode (insert or update) */
    private int mode = MODE_INSERT;

    /** label for the panel's name */
    private JLabel panelName = new JLabel();

    /** label for id */
    private JLabel id = new JLabel();

    /**  textfield associated to id label */
    private JTextField jTid = new JTextField();

    /**  label for type */
    private JLabel type = new JLabel();

    /**  textfield associated to type label */
    private JTextField jTType = new JTextField();

    /**  label for timezone */
    private JLabel timezone = new JLabel();

    /**  combobox associated to timezone */
    private TimeZonesComboModel timeZonesComboModel= new TimeZonesComboModel();
    private JComboBox jCBTimezone = new JComboBox(timeZonesComboModel);

    /**  label for utc support */
    private JLabel utcSupport = new JLabel();

    /**  button associated to "Conversion to current TZ" */
    private JRadioButton jRBEnableConvertDate  = new JRadioButton();
    private JRadioButton jRBDisableConvertDate = new JRadioButton();

    /**  label for charset */
    private JLabel charset = new JLabel();

    /**  combobox associated to charset */
    private JComboBox jCBCharset = new JComboBox();

    /** label for address */
    private JLabel address = new JLabel();

    /** textfield associated to address */
    private JTextField jTAddress = new JTextField();

    /** label for msisdn */
    private JLabel msisdn = new JLabel();

    /** textfield associated to msisdn */
    private JTextField jTMsisdn = new JTextField();

    /** label for notification builder */
    private JLabel notificationBuilder = new JLabel();

    /**  combobox associated to notification builder */
    private JComboBox jCBNotificationBuilder = new JComboBox();

    /** label for description */
    private JLabel description = new JLabel();

    /**  textarea associated to description label */
    private JTextArea jTdescription = new JTextArea();

    /** button for insert action  */
    private JButton jBInsert = new JButton();

    /** button for cancel action  */
    private JButton jBCancel = new JButton();

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    /** border to evidence title */
    private Border border1;

    /** The loaded device */
    private Sync4jDevice loadedDevice = null;

    /** is the enable conversion to this TZ the default value ? */
    private boolean enabledConversionDefaultValue = false;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for device insert.
     */
    public CreateDevicePanel() {
        try {
            init();
            resetPanelState();
        } catch (Exception e) {
            String errorMessage =
                    Bundle.getMessage(Bundle.ERROR_CREATING) + ": " +
                    getClass().getName() +"[" + e.getMessage() + "]";

            Log.error(errorMessage);
        }
    }

    // ---------------------------------------------------------- Public methods

    //----------------------------------------
    // Overrides javax.swing.JComponent method
    //----------------------------------------
    /**
     * Set preferredSize of the panel
     * @return preferredSize of the panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(400, 500);
    }

    /**
     * Load the device into the panel
     * @param syncDevice the device to edit
     */
    public void loadDevice(Sync4jDevice syncDevice) {
        mode = MODE_UPDATE;

        loadedDevice = syncDevice;

        panelName.setText(Bundle.getMessage(Bundle.EDIT_DEVICE_PANEL_NAME));
        this.setName(Bundle.getMessage(Bundle.EDIT_DEVICE_PANEL_NAME));

        jBInsert.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE));

        initBuildersList(jCBNotificationBuilder);

        jTid.setEditable(false);
        jTid.setText(syncDevice.getDeviceId());

        String deviceTimeZone = syncDevice.getTimeZone();
        Capabilities caps = syncDevice.getCapabilities();
        boolean capsFound = false;
        boolean utcSupport = false;
        if (caps != null) {
            if (caps.getDevInf() != null) {
                capsFound = true;
                if (caps.getDevInf().getUTC() != null) {
                    utcSupport = caps.getDevInf().getUTC().booleanValue();
                } else {
                    utcSupport = false;
                }
            }
        }

        boolean timeZoneFound = false;
        if (deviceTimeZone != null) {
            timeZoneFound = timeZonesComboModel.setSelectedTimeZoneId(deviceTimeZone);
        }

        if (utcSupport) {
            setDisableConvertionAsDefaultValue();

            if (!timeZoneFound) {
                jRBEnableConvertDate.setEnabled(false);
                jRBDisableConvertDate.setEnabled(false);
            } else {
                jRBEnableConvertDate.setEnabled(true);
                jRBDisableConvertDate.setEnabled(true);
            }

        } else {
            if (!capsFound) {
                //
                // Probably the device never sync
                //
                setEnableConvertionAsDefaultValue();

                if (!timeZoneFound) {
                    jRBEnableConvertDate.setEnabled(false);
                    jRBDisableConvertDate.setEnabled(false);
                } else {
                    jRBEnableConvertDate.setEnabled(true);
                    jRBDisableConvertDate.setEnabled(true);
                }
            } else {
                //
                // The device declares in the caps that it doesn't support utc
                //
                setEnableConvertionAsDefaultValue();

                //
                // We don't have choice...the checkbox must be disabled
                //
                jRBEnableConvertDate.setEnabled(false);
                jRBDisableConvertDate.setEnabled(false);
            }
        }

        if (!timeZoneFound) {
            timeZonesComboModel.setFirstItem();

            handleTimezoneChange();

        } else {

            switch (syncDevice.getConvertDatePolicy()) {
                case ConvertDatePolicy.CONVERT_DATE:
                    jRBEnableConvertDate.setSelected(true);
                    jRBDisableConvertDate.setSelected(false);
                    break;
                case ConvertDatePolicy.NO_CONVERT_DATE:
                    jRBEnableConvertDate.setSelected(false);
                    jRBDisableConvertDate.setSelected(true);
                    break;
                default:
                    if (utcSupport) {
                        jRBEnableConvertDate.setSelected(false);
                        jRBDisableConvertDate.setSelected(true);
                    } else {
                        jRBEnableConvertDate.setSelected(true);
                        jRBDisableConvertDate.setSelected(false);
                    }
                    break;
            }
        }

        String deviceCharset = syncDevice.getCharset();
        if (deviceCharset == null || "".equals(deviceCharset)) {
            deviceCharset = DEFAULT_DEVICE_CHARSET;
        }
        jCBCharset.setSelectedItem(deviceCharset);

        jTType.setText(syncDevice.getType());

        jTAddress.setText(syncDevice.getAddress());

        jTMsisdn.setText(syncDevice.getMsisdn());

        String notificationBuilder = syncDevice.getNotificationBuilder();
        if (NOTIFICATION_BUILDER_DS_11.equals(notificationBuilder)) {
            jCBNotificationBuilder.setSelectedItem(LABEL_BUILDER_11);
        } else if (NOTIFICATION_BUILDER_DS_12.equals(notificationBuilder)) {
            jCBNotificationBuilder.setSelectedItem(LABEL_BUILDER_12);
        } else if (notificationBuilder          != null &&
                   notificationBuilder.length() != 0     ) {
            jCBNotificationBuilder.addItem(notificationBuilder);
            jCBNotificationBuilder.setSelectedItem(notificationBuilder);
        } else {
            jCBNotificationBuilder.setSelectedItem("");
        }

        jTdescription.setText(syncDevice.getDescription());
    }

    // --------------------------------------------------------- Private methods
    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel
     */
    private void init() throws Exception {
        // set layout
        this.setLayout(null);
        this.setName(Bundle.getMessage(Bundle.INSERT_DEVICE_PANEL_NAME));

        int x1 = 13;
        int w1 = 135;
        int x2 = 153;
        int w2 = 230;

        int y  = 48;
        int dy = 36;

        int h = 18;

        // set properties of label, position and border referred to the title of the panel
        titledBorder1 = new TitledBorder("");
        border1 = BorderFactory.createLineBorder(Color.black, 1);
        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText(Bundle.getMessage(Bundle.INSERT_DEVICE_PANEL_NAME));
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        // set properties of label and textfield referred to ID including position
        id.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_ID) + " :");
        id.setBounds(new Rectangle(x1, y, w1, h));
        jTid.setBounds(new Rectangle(x2, y, w2, h));

        y += dy;

        // set properties of label and textfield referred to TYPE including position
        type.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_TYPE) + " :");
        type.setBounds(new Rectangle(x1, y, w1, h));
        jTType.setBounds(new Rectangle(x2, y, w2, h));

        y += dy;

        // set properties of label and combobox referred to TIMEZONE including position
        timezone.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_TIMEZONE) + " :");
        timezone.setBounds(new Rectangle(x1, y, w1, h));
        jCBTimezone.setBounds(new Rectangle(x2, y, w2, h));
        timeZonesComboModel.setFirstItem();
        jCBTimezone.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                handleTimezoneChange();
            }
        });

        y += dy;


        // set properties of label and combobox referred to UTC Support selection
        utcSupport.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_CONVERSION_TO_CURRENT_TZ) + " :");
        utcSupport.setBounds(new Rectangle(x1, y, w1, h));

        ButtonGroup convertDateGroup = new ButtonGroup();
        convertDateGroup.add(jRBEnableConvertDate);
        convertDateGroup.add(jRBDisableConvertDate);

        jRBEnableConvertDate.setText("Enabled");
        jRBEnableConvertDate.setBounds(new Rectangle(x2, y - 10, w2, h));
        jRBDisableConvertDate.setText("Disabled");
        jRBDisableConvertDate.setBounds(new Rectangle(x2, y + 10, w2, h));

        y += dy;

        // set properties of label and combobox referred to CHARSET including position
        charset.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_CHARSET) + " :");
        charset.setBounds(new Rectangle(x1, y, w1, h));
        jCBCharset.setBounds(new Rectangle(x2, y, w2, h));

        String[] availablesCharset = CharsetTools.getAvailablesCharset();
        for (int i=0; i<availablesCharset.length; i++) {
            jCBCharset.addItem(availablesCharset[i]);
        }
        jCBCharset.setEditable(true);

        y += dy;

        //
        // Set properties of label and textfield referred to ADDRESS
        // including position
        //
        address.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_ADDRESS) + " :");
        address.setBounds(new Rectangle(x1, y, w1, h));
        jTAddress.setBounds(new Rectangle(x2, y, w2, h));

        y += dy;

        //
        // Set properties of label and textfield referred to MSISDN
        // including position
        //
        msisdn.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_MSISDN) + " :");
        msisdn.setBounds(new Rectangle(x1, y, w1, h));
        jTMsisdn.setBounds(new Rectangle(x2, y, w2, h));

        y += dy;

        //
        // Set properties of label and textfield referred to NOTIFICATION
        // BUILDER including position
        //
        notificationBuilder.setText(
            Bundle.getMessage(Bundle.DEVICE_PANEL_NOTIFICATION_BUILDER) + " :"
        );
        notificationBuilder.setBounds(new Rectangle(x1, y, w1, h));

        jCBNotificationBuilder.setBounds(new Rectangle(x2, y, w2, h));
        initBuildersList(jCBNotificationBuilder);

        y += dy;

        // set properties of label and textarea referred to DESCRIPTION
        //   including position

        int descriptionHeight = 62;

        description.setText(Bundle.getMessage(Bundle.DEVICE_PANEL_DESCRIPTION) + " :");
        description.setBounds(new Rectangle(x1, y, w1, h));
        jTdescription.setBounds(new Rectangle(x2, y, 232, descriptionHeight));
        jTdescription.setLineWrap(true);
        jTdescription.setWrapStyleWord(true);

        JScrollPane jScrollDescription=new JScrollPane(jTdescription);
        jScrollDescription.setBounds(new Rectangle(x2, y, 232, descriptionHeight));

        y = y + dy + descriptionHeight;

        // set position of button
        jBInsert.setBounds(new Rectangle(100, y, 65, 28));
        jBInsert.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_ADD));

        // associate action to button
        jBInsert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                setDevice();
            }
        });

        jBCancel.setBounds(new Rectangle(170, y, 70, 28));
        jBCancel.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL));

        jBCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelAction();
            }
        });

        jTType.setFont(GuiFactory.defaultFont);
        type.setFont(GuiFactory.defaultFont);
        timezone.setFont(GuiFactory.defaultFont);
        jCBTimezone.setFont(GuiFactory.defaultFont);
        utcSupport.setFont(GuiFactory.defaultFont);
        jRBEnableConvertDate.setFont(GuiFactory.defaultFont);
        jRBDisableConvertDate.setFont(GuiFactory.defaultFont);
        charset.setFont(GuiFactory.defaultFont);
        jCBCharset.setFont(GuiFactory.defaultFont);
        address.setFont(GuiFactory.defaultFont);
        jTAddress.setFont(GuiFactory.defaultFont);
        msisdn.setFont(GuiFactory.defaultFont);
        jTMsisdn.setFont(GuiFactory.defaultFont);
        notificationBuilder.setFont(GuiFactory.defaultFont);
        jCBNotificationBuilder.setFont(GuiFactory.defaultFont);
        description.setFont(GuiFactory.defaultFont);
        jTdescription.setFont(GuiFactory.defaultFont);
        id.setFont(GuiFactory.defaultFont);
        jTid.setFont(GuiFactory.defaultFont);
        jBInsert.setFont(GuiFactory.defaultFont);
        jBCancel.setFont(GuiFactory.defaultFont);

        // add all components to the panel
        this.add(panelName, null);
        this.add(id, null);
        this.add(jTid, null);
        this.add(jTType, null);
        this.add(type, null);
        this.add(timezone, null);
        this.add(jCBTimezone, null);
        this.add(utcSupport, null);
        this.add(jRBEnableConvertDate, null);
        this.add(jRBDisableConvertDate, null);
        this.add(charset, null);
        this.add(jCBCharset, null);
        this.add(address, null);
        this.add(jTAddress, null);
        this.add(msisdn, null);
        this.add(jTMsisdn, null);
        this.add(notificationBuilder, null);
        this.add(jCBNotificationBuilder, null);
        this.add(description, null);
        this.add(jScrollDescription, null);
        this.add(jBInsert, null);
        this.add(jBCancel, null);
    }

    /**
     * Return to the previous panel.
     */
    private void cancelAction() {
        PanelManager.removePanel(this);
    }

    /**
     * Create new device
     */
    private void setDevice() {
        // create new device with edited parameters
        Sync4jDevice device = new Sync4jDevice();

        device.setDeviceId(jTid.getText());
        device.setType(jTType.getText());

        if (jCBTimezone.getSelectedIndex() != 0) {
            device.setTimeZone(timeZonesComboModel.getSelectedTimeZoneId());

            if (jRBEnableConvertDate.isSelected()) {
                device.setConvertDatePolicy(ConvertDatePolicy.CONVERT_DATE);
            } else {
                device.setConvertDatePolicy(ConvertDatePolicy.NO_CONVERT_DATE);
            }

        } else {
            device.setConvertDatePolicy(ConvertDatePolicy.UNSPECIFIED);
        }

        device.setCharset((String)jCBCharset.getSelectedItem());

        device.setAddress(jTAddress.getText());

        device.setMsisdn(jTMsisdn.getText());

        if (LABEL_BUILDER_11.equals(jCBNotificationBuilder.getSelectedItem())) {
            device.setNotificationBuilder(NOTIFICATION_BUILDER_DS_11);
        } else if (LABEL_BUILDER_12.equals(jCBNotificationBuilder.getSelectedItem())) {
            device.setNotificationBuilder(NOTIFICATION_BUILDER_DS_12);
        } else {
            device.setNotificationBuilder("");
        }

        device.setDescription(jTdescription.getText());

        if (mode == MODE_INSERT) {
            boolean deviceSaved = false;

            try {
                deviceSaved = ExplorerUtil.getSyncAdminController().getDevicesController().
                              saveNewDevice(
                                  device);

            } catch (AdminException e) {
                Log.error(
                    MessageFormat.format(
                        Bundle.getMessage(Bundle.ERROR_HANDLING_DEVICE),
                        new String[] {Bundle.getMessage(Bundle.UPDATING)}
                    ), e
                 );
            }
            if (deviceSaved) {
                // clear values in the panel's
                resetPanelState();
            }
        } else {
            boolean deviceUpdated = false;

            if (loadedDevice != null) {
                device.setNotificationSender(loadedDevice.getNotificationSender());
                Capabilities cap = loadedDevice.getCapabilities();
                if (cap != null) {
                   device.setCapabilities(cap);
                }
                device.setServerNonce(loadedDevice.getServerNonce());
                device.setClientNonce(loadedDevice.getClientNonce());
                device.setServerPassword(loadedDevice.getServerPassword());
            }

            try {
                deviceUpdated = ExplorerUtil.getSyncAdminController().getDevicesController().
                                updateDevice(device);
            } catch (AdminException e) {
                Log.error(
                    MessageFormat.format(
                        Bundle.getMessage(Bundle.ERROR_HANDLING_DEVICE),
                        new String[] {Bundle.getMessage(Bundle.UPDATING)}
                    ), e
                 );
            }
        }
    }

    /**
     * Inits the given combo box with the available builders
     * @param builders the combo box to initialize
     */
    private void initBuildersList(JComboBox builders) {
        if (builders == null) {
            return ;
        }
        builders.removeAllItems();
        builders.addItem("");
        builders.addItem(Bundle.getMessage(Bundle.DEVICE_PANEL_NOTIFICATION_BUILDER_11));
        builders.addItem(Bundle.getMessage(Bundle.DEVICE_PANEL_NOTIFICATION_BUILDER_12));
        builders.setSelectedItem(DEFAULT_NOTIFICATION_BUILDER);
    }

    private void showConversionToTZSection(boolean show) {
        if (show) {
            utcSupport.setVisible(true);
            jRBDisableConvertDate.setVisible(true);
            jRBEnableConvertDate.setVisible(true);
        } else {
            utcSupport.setVisible(false);
            jRBDisableConvertDate.setVisible(false);
            jRBEnableConvertDate.setVisible(false);
        }

    }

    /**
     * Resets the panel state to its original state
     */
    private void resetPanelState() {
        // clear values in the panel's
        jTid.setText("");
        timeZonesComboModel.setFirstItem();
        jCBTimezone.updateUI();
        jTdescription.setText("");
        jTType.setText("");
        jCBCharset.setSelectedIndex(0);
        jTAddress.setText("");
        jTMsisdn.setText("");
        jCBNotificationBuilder.setSelectedItem(DEFAULT_NOTIFICATION_BUILDER);
        jCBTimezone.setSelectedIndex(0);

        setEnableConvertionAsDefaultValue();

        jRBEnableConvertDate.setSelected(true);
        jRBEnableConvertDate.setEnabled(false);

        jRBDisableConvertDate.setSelected(false);
        jRBDisableConvertDate.setEnabled(false);

        loadedDevice = null;
    }

    /**
     * Called when the timezone combolist changes
     */
    private void handleTimezoneChange() {
        if (jCBTimezone.getSelectedIndex() == 0) {
            jRBDisableConvertDate.setEnabled(false);
            jRBEnableConvertDate.setEnabled(false);
            if (enabledConversionDefaultValue) {
                jRBEnableConvertDate.setSelected(true);
                jRBDisableConvertDate.setSelected(false);
            } else {
                jRBEnableConvertDate.setSelected(false);
                jRBDisableConvertDate.setSelected(true);
            }
        } else {
            jRBDisableConvertDate.setEnabled(true);
            jRBEnableConvertDate.setEnabled(true);
        }
    }

    private void setEnableConvertionAsDefaultValue() {
        jRBEnableConvertDate.setText(CONVERSION_TO_CURRENT_TZ_ENABLED_DEFAULT);
        jRBDisableConvertDate.setText(CONVERSION_TO_CURRENT_TZ_DISABLED);
        enabledConversionDefaultValue = true;
    }

    private void setDisableConvertionAsDefaultValue() {
        jRBEnableConvertDate.setText(CONVERSION_TO_CURRENT_TZ_ENABLED);
        jRBDisableConvertDate.setText(CONVERSION_TO_CURRENT_TZ_DISABLED_DEFAULT);
        enabledConversionDefaultValue = false;
    }
}
