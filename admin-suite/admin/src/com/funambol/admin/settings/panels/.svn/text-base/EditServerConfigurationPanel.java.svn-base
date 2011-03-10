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
package com.funambol.admin.settings.panels;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.funambol.framework.core.VerDTD;

import com.funambol.server.config.EngineConfiguration;
import com.funambol.server.config.ServerConfiguration;

import com.funambol.admin.AdminException;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

import org.apache.commons.lang.StringUtils;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;


/**
 * Generate a panel used to edit a LoggerConfiguration object.
 *
 * @version $Id: EditServerConfigurationPanel.java,v 1.22 2008-06-30 15:11:57 nichele Exp $
 **/
public class EditServerConfigurationPanel extends JPanel {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data

    /** label for the panel's name */
    private JLabel panelNameLabel = new JLabel();

    private JLabel titleSectionEngineInfo = new JLabel();
    private JLabel titleSectionDevInfo = new JLabel();

    /** Label for the server uri */
    private JLabel serverUriLabel = new JLabel();

    /** TextField for the server uri */
    private JTextField serverUri = new JTextField();

    /** Label for the handler */
    private JLabel handlerLabel = new JLabel();

    /** TextField for the hanlder */
    private JTextField handler = new JTextField();

    /** Label for the strategy */
    private JLabel strategyLabel = new JLabel();

    /** TextField for the strategy */
    private JTextField strategy = new JTextField();

    /** Button for Sync4jStrategy properties*/
    private JButton strategyButton = new JButton();

    /** Label for the user manager */
    private JLabel userManagerLabel = new JLabel();

    /** TextField for the user manager */
    private JTextField userManager = new JTextField();

    /** Label for the device inventory */
    private JLabel deviceInventoryLabel = new JLabel();

    /** TextField for the device inventory */
    private JTextField deviceInventory = new JTextField();

    /** Label for the dataTransformerManager */
    private JLabel dataTransformerManagerLabel = new JLabel();

    /** TextField for the dataTransformerManager */
    private JTextField dataTransformerManager = new JTextField();

    /** Show DataTransformers properties button*/
    private JButton dataTransformerButton = new JButton();

    /** Label for the officer */
    private JLabel officerLabel = new JLabel();

    /** TextField for the officer */
    private JTextField officer = new JTextField();

    /** Label for the minMaxMsgSize */
    private JLabel minMaxMsgSizeLabel = new JLabel();

    /** TextField for the minMaxMsgSize */
    private JTextField minMaxMsgSize = new JTextField();

    /** Label for the check for updates */
    private JLabel checkForUpdatesLabel = new JLabel();

    /** TextField for the minMaxMsgSize */
    private JCheckBox checkForUpdates = new JCheckBox();

    /** Label for the oem */
    private JLabel oemLabel = new JLabel();

    /** TextField for the oem */
    private JTextField oem = new JTextField();

    /** Label for the devId */
    private JLabel devIdLabel = new JLabel();

    /** TextField for the devId */
    private JTextField devId = new JTextField();

    /** Label for the devTyp */
    private JLabel devTypLabel = new JLabel();

    /** TextField for the devTyp */
    private JTextField devTyp = new JTextField();

    /** Label for the fwV */
    private JLabel fwVLabel = new JLabel();

    /** TextField for the fwV */
    private JTextField fwV = new JTextField();

    /** Label for the hwV */
    private JLabel hwVLabel = new JLabel();

    /** TextField for the hwV */
    private JTextField hwV = new JTextField();

    /** Label for the swV */
    private JLabel swVLabel = new JLabel();

    /** TextField for the swV */
    private JTextField swV = new JTextField();

    /** Label for the man */
    private JLabel manLabel = new JLabel();

    /** TextField for the man */
    private JTextField man = new JTextField();

    /** Label for the mod */
    private JLabel modLabel = new JLabel();

    /** TextField for the mod */
    private JTextField mod = new JTextField();

    /** Label for the verDTD */
    private JLabel verDTDLabel = new JLabel();

    /** TextField for the verDTD */
    private JTextField verDTD = new JTextField();

    /** Label for the SMS Service */
    private JLabel smsServiceLabel = new JLabel();

    /** TextField for the SMS Service */
    private JTextField smsService = new JTextField();

    /** Confirm button*/
    private JButton confirmButton = new JButton();

    /** Cancel button*/
    private JButton cancelButton = new JButton();

    private ServerConfiguration serverConfiguration;

    // ------------------------------------------------------------ Constructors

    /**
     * Create a panel for the given LoggerConfiguration object.
     */
    public EditServerConfigurationPanel() {
        try {
            init();
            serverConfiguration = null;
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }

    }

    // ---------------------------------------------------------- Public methods

   /**
     * Loads the LoggerConfiguration values into the panel.
     *
     * @param loggerNode the logger settings node
     */
    public void loadSettings(ServerConfiguration configuration) {

        this.serverConfiguration = configuration;

        EngineConfiguration engineConfiguration = serverConfiguration.getEngineConfiguration();

        // engine info
        serverUri.setText(engineConfiguration.getServerURI());
        officer.setText(engineConfiguration.getOfficer());
        deviceInventory.setText(engineConfiguration.getDeviceInventory());
        dataTransformerManager.setText(engineConfiguration.getDataTransformerManager());
        minMaxMsgSize.setText(String.valueOf(engineConfiguration.getMinMaxMsgSize()));
        handler.setText(engineConfiguration.getSessionHandler());
        strategy.setText(engineConfiguration.getStrategy());
        userManager.setText(engineConfiguration.getUserManager());
        smsService.setText(engineConfiguration.getSmsService());
        checkForUpdates.setSelected(engineConfiguration.getCheckForUpdates());

        // server info
        oem.setText(serverConfiguration.getServerInfo().getOEM());
        fwV.setText(serverConfiguration.getServerInfo().getFwV());
        hwV.setText(serverConfiguration.getServerInfo().getHwV());
        man.setText(serverConfiguration.getServerInfo().getMan());
        mod.setText(serverConfiguration.getServerInfo().getMod());
        swV.setText(serverConfiguration.getServerInfo().getSwV());
        verDTD.setText(serverConfiguration.getServerInfo().getVerDTD().getValue());
        devId.setText(serverConfiguration.getServerInfo().getDevID());
        devTyp.setText(serverConfiguration.getServerInfo().getDevTyp());
    }

    /**
     * Returns the preferred dimension of this panel
     * @return the preferred dimension of this panel
     */
    public Dimension getPreferredSize() {
        return new Dimension(670, 700);
    }

    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel.
     */
    private void init() throws Exception {
        final int dy = 25;
        int y = 25;

        int x1 = 15;
        int w1 = 140;
        int w2 = 350;
        int dx = 21;

        int x2 = x1 + w1 + dx;

        setAutoscrolls(true);
        setLayout(null);
        setName(Bundle.getMessage(Bundle.EDIT_SERVER_CONFIGURATION_PANEL_NAME));

        panelNameLabel.setText(Bundle.getMessage(Bundle.EDIT_SERVER_CONFIGURATION_PANEL_NAME));
        panelNameLabel.setBounds(new Rectangle(14, 5, 216, 28));
        panelNameLabel.setAlignmentX(SwingConstants.CENTER);
        panelNameLabel.setBorder(new TitledBorder(""));

        y += dy;

        titleSectionDevInfo.setText(Bundle.getMessage(Bundle.LABEL_DEV_INF));
        titleSectionDevInfo.setBounds(new Rectangle(14, y, 162, 28));
        titleSectionDevInfo.setAlignmentX(SwingConstants.CENTER);
        titleSectionDevInfo.setBorder(new TitledBorder(""));

        y += dy;
        y += 10;

        manLabel.setText(Bundle.getMessage(Bundle.LABEL_MAN) + " :");
        manLabel.setBounds(new Rectangle(x1, y, w1, 18));
        man.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        modLabel.setText(Bundle.getMessage(Bundle.LABEL_MOD) + " :");
        modLabel.setBounds(new Rectangle(x1, y, w1, 18));
        mod.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        swVLabel.setText(Bundle.getMessage(Bundle.LABEL_SWV) + " :");
        swVLabel.setBounds(new Rectangle(x1, y, w1, 18));
        swV.setBounds(new Rectangle(x2, y, w2, 18));
        swV.setEditable(false);

        y += dy;

        hwVLabel.setText(Bundle.getMessage(Bundle.LABEL_HWV) + " :");
        hwVLabel.setBounds(new Rectangle(x1, y, w1, 18));
        hwV.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        fwVLabel.setText(Bundle.getMessage(Bundle.LABEL_FWV) + " :");
        fwVLabel.setBounds(new Rectangle(x1, y, w1, 18));
        fwV.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        oemLabel.setText(Bundle.getMessage(Bundle.LABEL_OEM) + " :");
        oemLabel.setBounds(new Rectangle(x1, y, w1, 18));
        oem.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        devIdLabel.setText(Bundle.getMessage(Bundle.LABEL_DEV_ID) + " :");
        devIdLabel.setBounds(new Rectangle(x1, y, w1, 18));
        devId.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        devTypLabel.setText(Bundle.getMessage(Bundle.LABEL_DEV_TYP) + " :");
        devTypLabel.setBounds(new Rectangle(x1, y, w1, 18));
        devTyp.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        verDTDLabel.setText(Bundle.getMessage(Bundle.LABEL_VER_DTD) + " :");
        verDTDLabel.setBounds(new Rectangle(x1, y, w1, 18));
        verDTD.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;
        y += 10;

        titleSectionEngineInfo.setText(Bundle.getMessage(Bundle.LABEL_ENGINE_INFO));
        titleSectionEngineInfo.setBounds(new Rectangle(14, y, 162, 28));
        titleSectionEngineInfo.setAlignmentX(SwingConstants.CENTER);
        titleSectionEngineInfo.setBorder(new TitledBorder(""));

        y += 10;
        y += dy;

        serverUriLabel.setText(Bundle.getMessage(Bundle.LABEL_SERVER_URI) + " :");
        serverUriLabel.setBounds(new Rectangle(x1, y, w1, 18));
        serverUri.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        officerLabel.setText(Bundle.getMessage(Bundle.LABEL_OFFICER) + " :");
        officerLabel.setBounds(new Rectangle(x1, y, w1, 18));
        officer.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        handlerLabel.setText(Bundle.getMessage(Bundle.LABEL_HANDLER) + " :");
        handlerLabel.setBounds(new Rectangle(x1, y, w1, 18));
        handler.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        deviceInventoryLabel.setText(
             Bundle.getMessage(Bundle.LABEL_DEVICE_INVENTORY) + " :"
        );
        deviceInventoryLabel.setBounds(new Rectangle(x1, y, w1, 18));
        deviceInventory.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        dataTransformerManagerLabel.setText(
             Bundle.getMessage(Bundle.LABEL_DATA_TRANSFORMER_MANAGER) + " :"
        );
        dataTransformerManagerLabel.setBounds(new Rectangle(x1, y, w1, 18));
        dataTransformerManager.setBounds(new Rectangle(x2, y, w2, 18));
        dataTransformerButton.setBounds(new Rectangle(x2+w2+10,y,85,18));
        dataTransformerButton.setText(Bundle.getMessage(Bundle.DATA_TRANSFORMER_CONFIGURE_BUTTON));
        dataTransformerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path=dataTransformerManager.getText();
                try {
                    ExplorerUtil.getSyncAdminController()
                        .getServerSettingsController()
                        .editDataTrasnformerConfiguration(path);
                } catch (AdminException ex) {
                    String msg = Bundle.getMessage(Bundle.ERROR_GETTING_DTM_CONFIGURATION);
                    NotifyDescriptor desc = new NotifyDescriptor.Message(ex.getMessage());
                    DialogDisplayer.getDefault().notify(desc);

                    Log.error(msg, ex);

                    return;
                }
            }
        });

        y += dy;

        strategyLabel.setText(Bundle.getMessage(Bundle.LABEL_STRATEGY) + " :");
        strategyLabel.setBounds(new Rectangle(x1, y, w1, 18));
        strategy.setBounds(new Rectangle(x2, y, w2, 18));
        strategyButton.setBounds(new Rectangle(x2+w2+10,y,85,18));
        strategyButton.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_CONFIGURE));
        strategyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path=strategy.getText();
                try {
                    ExplorerUtil.getSyncAdminController()
                        .getServerSettingsController()
                        .editStrategyConfiguration(path);
                } catch (AdminException ex) {
                    String msg = Bundle.getMessage(Bundle.ERROR_GETTING_STRATEGY_CONFIGURATION);
                    NotifyDescriptor desc = new NotifyDescriptor.Message(ex.getMessage());
                    DialogDisplayer.getDefault().notify(desc);

                    Log.error(msg, ex);

                    return;
                }
            }
        });

        y += dy;

        userManagerLabel.setText(Bundle.getMessage(Bundle.LABEL_USER_MANAGER) + " :");
        userManagerLabel.setBounds(new Rectangle(x1, y, w1, 18));
        userManager.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        smsServiceLabel.setText(Bundle.getMessage(Bundle.LABEL_SMS_SERVICE) + " :");
        smsServiceLabel.setBounds(new Rectangle(x1, y, w1, 18));
        smsService.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        minMaxMsgSizeLabel.setText(Bundle.getMessage(Bundle.LABEL_MIN_MAX_MSG_SIZE) + " :");
        minMaxMsgSizeLabel.setBounds(new Rectangle(x1, y, w1, 18));
        minMaxMsgSize.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;

        checkForUpdatesLabel.setText(Bundle.getMessage(Bundle.LABEL_CHECK_FOR_UPDATES) + " :");
        checkForUpdatesLabel.setBounds(new Rectangle(x1, y, w1, 18));
        checkForUpdates.setBounds(new Rectangle(x2, y, w2, 18));

        y += dy;
        y += dy;

        confirmButton.setText(Bundle.getMessage(Bundle.LABEL_BUTTON_SAVE));
        confirmButton.setBounds(new Rectangle(225, y, 60, 25));

        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setServerConfiguration();
            }
        });

        cancelButton = new JButton(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL));
        cancelButton.setBounds(290, y, 70, 25);

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancelAction();
            }
        });

        //add components to panel
        add(panelNameLabel        , null);
        add(titleSectionDevInfo   , null);
        add(manLabel              , null);
        add(man                   , null);
        add(modLabel              , null);
        add(mod                   , null);
        add(swVLabel              , null);
        add(swV                   , null);
        add(hwVLabel              , null);
        add(hwV                   , null);
        add(fwVLabel              , null);
        add(fwV                   , null);
        add(oemLabel              , null);
        add(oem                   , null);
        add(devIdLabel            , null);
        add(devId                 , null);
        add(devTypLabel           , null);
        add(devTyp                , null);
        add(verDTDLabel           , null);
        add(verDTD                , null);
        add(titleSectionEngineInfo, null);
        add(serverUriLabel        , null);
        add(serverUri             , null);
        add(officerLabel          , null);
        add(officer               , null);
        add(handlerLabel          , null);
        add(handler               , null);
        add(deviceInventoryLabel  , null);
        add(deviceInventory       , null);
        add(dataTransformerManagerLabel, null);
        add(dataTransformerManager, null);
        add(dataTransformerButton , null);
        add(strategyLabel         , null);
        add(strategy              , null);
        add(strategyButton        , null);
        add(userManagerLabel      , null);
        add(userManager           , null);
        add(smsServiceLabel       , null);
        add(smsService            , null);
        add(minMaxMsgSizeLabel    , null);
        add(minMaxMsgSize         , null);
        add(checkForUpdatesLabel  , null);
        add(checkForUpdates       , null);
        add(confirmButton         , null);
        add(cancelButton          , null);

        GuiFactory.setDefaultFont(this);

        panelNameLabel.setFont(GuiFactory.titlePanelFont);
        titleSectionEngineInfo.setFont(new java.awt.Font("Arial", 2, 12));
        titleSectionDevInfo.setFont(new java.awt.Font("Arial", 2, 12));

    }

    /**
     * Return to the previous panel.
     */
    private void cancelAction() {
        PanelManager.removePanel(this);
    }

    /**
     * Validates the inputs anf throws an Exception in case of any invalid data.
     *
     * @throws IllegalArgumentException in case of invalid input
     */
    private void validateInput() throws IllegalArgumentException {

        String value = null;

        value = man.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_MAN)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = mod.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_MOD)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = swV.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_SWV)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = hwV.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_HWV)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = fwV.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_FWV)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = oem.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_OEM)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = devId.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_DEV_ID)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = devTyp.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_DEV_TYP)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = verDTD.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_VER_DTD)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = serverUri.getText();
        if (StringUtils.isNotEmpty(value)) {
            try {
                //
                // Checks if serverURI is a well formed URI.
                // Used the URL because the URI does not throw 
                // URISyntaxException in some cases when expected
                // (see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4668181)
                //
                URL url = new URL(value);
                //
                // The serverUri must start with http:// or https:// in order to
                // be used as a valid URL
                //
                if (!value.startsWith("http://") && !value.startsWith("https://")) {
                    throw new MalformedURLException();
                }
            } catch(MalformedURLException e) {
                String msg = MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_SERVER_URI_NOT_VALID),
                    new String[] {
                       value
                    }
                );
                throw new IllegalArgumentException(msg);
            }
        }

        value = officer.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_OFFICER)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = deviceInventory.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_DEVICE_INVENTORY)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = handler.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_HANDLER)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = strategy.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_STRATEGY)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = userManager.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_USER_MANAGER)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        value = smsService.getText();
        if (StringUtils.isEmpty(value)) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_EMPTY_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_SMS_SERVICE)
                }
            );
            throw new IllegalArgumentException(msg);
        }

        try {
            int i = Integer.parseInt(minMaxMsgSize.getText());
            if (i < 1 || i > 100000) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            String msg = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_NUMERIC_INPUT),
                new String[] {
                    Bundle.getMessage(Bundle.LABEL_MIN_MAX_MSG_SIZE),
                    "1",
                    String.valueOf("100000")
                }
            );
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Saves the inserted value in the node's LoggerConfiguration.
     *
     */
    private void getValues() {

        EngineConfiguration engineConfiguration =
            serverConfiguration.getEngineConfiguration();

        // engine info
        engineConfiguration.setServerURI(serverUri.getText());
        engineConfiguration.setOfficer(officer.getText());
        engineConfiguration.setSessionHandler(handler.getText());
        engineConfiguration.setDeviceInventory(deviceInventory.getText());
        engineConfiguration.setDataTransformerManager(dataTransformerManager.getText());
        engineConfiguration.setStrategy(strategy.getText());
        engineConfiguration.setUserManager(userManager.getText());
        engineConfiguration.setSmsService(smsService.getText());
        engineConfiguration.setMinMaxMsgSize(Long.parseLong(minMaxMsgSize.getText()));
        engineConfiguration.setCheckForUpdates(checkForUpdates.isSelected());

        // server info
        serverConfiguration.getServerInfo().setDevID(devId.getText());
        serverConfiguration.getServerInfo().setDevTyp(devTyp.getText());
        serverConfiguration.getServerInfo().setFwV(fwV.getText());
        serverConfiguration.getServerInfo().setHwV(hwV.getText());
        serverConfiguration.getServerInfo().setMan(man.getText());
        serverConfiguration.getServerInfo().setMod(mod.getText());
        serverConfiguration.getServerInfo().setOEM(oem.getText());
        serverConfiguration.getServerInfo().setSwV(swV.getText());
        serverConfiguration.getServerInfo().setVerDTD(new VerDTD(verDTD.getText()));
    }


    /**
     * Retrieves the inserted values and, if all inputs are valid, calls back
     * the controller to actually store the new logger configuration to the
     * server.
     *
     */
    private void setServerConfiguration() {
        Log.debug("setServerConfiguration");

        //
        // First of all, perform input validation
        //
        try {
            validateInput();
        } catch (IllegalArgumentException e) {
            NotifyDescriptor desc = new NotifyDescriptor.Message(e.getMessage());
            DialogDisplayer.getDefault().notify(desc);
            return;
        }

        //
        // Read the inserted values
        //
        getValues();

        //
        // Here inputs are ok. Call the controller's
        // saveServerConfiguration()
        //

        try {
            ExplorerUtil.getSyncAdminController()
                        .getServerSettingsController()
                        .setServerConfiguration(serverConfiguration);
        } catch (AdminException e) {
            String msg = Bundle.getMessage(Bundle.ERROR_SAVING_SERVER_CONFIGURATION);
            NotifyDescriptor desc = new NotifyDescriptor.Message(e.getMessage());
            DialogDisplayer.getDefault().notify(desc);

            Log.error(msg, e);

            return;
        }

        Log.info(Bundle.getMessage(Bundle.MSG_SERVER_CONFIGURATION_SETTINGS_SAVED));
    }
}