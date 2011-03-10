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
package com.funambol.email.admin;


import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Log;
import com.funambol.email.admin.dao.WSDAO;

import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.util.Def;

import com.funambol.framework.server.Sync4jUser;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * Panel that shows account details. Account data can be inserted/modified.
 *
 * @version $Id: AccountDetailsPanel.java,v 1.2 2008-05-13 10:10:00 gbmiglia Exp $
 *
 */
public class AccountDetailsPanel extends JPanel {

    // --------------------------------------------------------------- Constants

    private final static int DEFAULT_REFRESH_TIME = 5;

    // ------------------------------------------------------------ Private data

    /** process that uses this panel is used */
    private OperationType operationType;

    /** data access object */
    private WSDAO WSDao;

    /** empty server, for selection (that is: non public server) */
    private MailServer customServer;

    /** array of all the public mail servers */
    private MailServer[] publicMailServers;

    /** user to which this account may be bound */
    private Sync4jUser user;

    /**
     * Object that holds the "next step logic" (that is: how the process using
     * this panel goes to the next process step)
     */
    private Step step;

    /* selected mail server */
    private MailServer mailServer;

    /* array of all the selectable mail servers */
    private MailServer[] mailServers;

    //
    // Visual elements and related fields.
    //

    // Constats used to align visual component in account detail panel.
    // Values are specified as pixels.

    private final int LEFT_PAD              = 14;
    private int left_pad                    = LEFT_PAD;
    private final int TOP_PAD               = 50;
    private final int LABEL_WIDTH           = 130;
    private final int WIDTH                 = 150;
    private final int HEIGHT                = 20;
    private final int SPACING_Y             = 2;
    private final int SPACING_X             = 2;
    private final int SPACING_SECTIONS_X    = 4;

    // title

    JLabel panelName = new JLabel();

    // left side

    private JLabel jLUser = new JLabel();
    private JLabel jLPassword = new JLabel();
    private JLabel jLConfirmPassword = new JLabel();
    private JLabel jLActive = new JLabel();
    private JLabel jLPush = new JLabel();
    private JLabel jLMaxEmail = new JLabel();
    private JLabel jLRefreshTime = new JLabel();
    private JLabel jLMaxIMAP = new JLabel();
    private JLabel jLMSAddress = new JLabel();

    private JTextField jTFUser = new JTextField();
    private JPasswordField jTFPassword = new JPasswordField();
    private JPasswordField jTFConfirmPassword = new JPasswordField();
    private JCheckBox jCBActive = new JCheckBox();
    private JCheckBox jCBPush = new JCheckBox();
    private JComboBox jCBoxMaxEmail = new JComboBox();
    private JTextField jFTRefreshTime = new JTextField();
    private JComboBox jCBoxMaxIMAP = new JComboBox();
    private JTextField jTFAddress = new JTextField();

    // right side

    private JLabel jLMailServer = new JLabel();
    private JLabel jLDescription = new JLabel();
    private JLabel jLServerProduct = new JLabel();
    private JLabel jLProtocol = new JLabel();
    private JLabel jLOutgoingServer = new JLabel();
    private JLabel jLIncomingServer = new JLabel();
    private JLabel jLOutgoingServerPort = new JLabel();
    private JLabel jLIncomingServerPort = new JLabel();
    private JLabel jLAuth = new JLabel();

    private JLabel jLSSLIn  = new JLabel();
    private JLabel jLSSLOut = new JLabel();
    private JLabel jlInboxFolderName = new JLabel();
    private JLabel jlOutboxFolderName = new JLabel();
    private JLabel jlSentFolderName = new JLabel();
    private JLabel jlDraftsFolderName = new JLabel();
    private JLabel jlTrashFolderName = new JLabel();
    private JLabel jlDeleteOnServer = new JLabel();

    private JTextField jTFDescription = new JTextField();
    private JComboBox jCBoxMailServer = new JComboBox();
    private JComboBox jCBoxServerProduct = new JComboBox();
    private JComboBox jCBoxProtocol = new JComboBox();
    private JTextField jTFOutgoingServer = new JTextField();
    private JTextField jTFIncomingServer = new JTextField();
    private JTextField jTFOutgoingServerPort = new JTextField();
    private JTextField jTFIncomingServerPort = new JTextField();
    private JCheckBox jCBAuth = new JCheckBox();
    private JCheckBox jCBSSlIn  = new JCheckBox();
    private JCheckBox jCBSSlOut = new JCheckBox();
    private JCheckBox jCBInboxFolderName = new JCheckBox();
    private JCheckBox jCBOutboxFolderName = new JCheckBox();
    private JCheckBox jCBSentFolderName = new JCheckBox();
    private JCheckBox jCBDraftsFolderName = new JCheckBox();
    private JCheckBox jCBTrashFolderName = new JCheckBox();
    private JCheckBox jCBDeleteOnServer = new JCheckBox();

    private JTextField jTFInboxFolderName = new JTextField();
    private JTextField jTFOutboxFolderName = new JTextField();
    private JTextField jTFSentFolderName = new JTextField();
    private JTextField jTFDraftsFolderName = new JTextField();
    private JTextField jTFTrashFolderName = new JTextField();

    // buttons

    private JButton jBSave = new JButton();
    private JButton jBCancel = new JButton();
    private JButton jBListFolders = new JButton();

    // convenient aobject to store a validated account

    private MailServerAccount validatedAccount;

    // -------------------------------------------------------------- Properties

    /** account to be processed */
    private MailServerAccount account;

    /**
     * Returns the account.
     *
     * @return current mail server account
     */
    public MailServerAccount getAccount() {
        return this.account;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of AccountDetailsPanel.
     *
     * @param user user to which the account is bound
     * @param account account object to be filled/modified
     * @param WSDao access object
     * @param operationType process that uses this panel is used
     */
    public AccountDetailsPanel(Sync4jUser user,
            MailServerAccount account,
            WSDAO WSDao,
            OperationType operationType) {

        this.user = user;
        this.account = account;
        this.WSDao = WSDao;
        this.operationType = operationType;

        try {

            // init all the combo boxes
            initComboBoxes();

            // fills fields with given account, if not null
            if (account == null){
                this.account = new MailServerAccount();
                this.account.setMsAddress(this.user.getEmail());
                this.account.setPeriod(DEFAULT_REFRESH_TIME);
                this.account.setActive(true);
                this.account.setPush(true);
                this.account.setMailServer(customServer);
            }

            // init visual component graphic properties
            initGui();

            // init visual component data
            fillFields();

        } catch (Exception e) {
            Log.error(e);
        }
    }

    // --------------------------------------------------------- Private methods


    /**
     * Initializes the visual elements. Only graphic properties of each component
     * are set, not their data.
     *
     * @throws java.lang.Exception
     */
    private void initGui() throws Exception {

        setLayout(null);

        //
        // Configure components
        //

        // title

        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setBounds(new Rectangle(14, 5, 400, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(new TitledBorder(""));

        // left side

        jLUser.setText("Login");
        jLPassword.setText("Password");
        jLConfirmPassword.setText("Confirm password");
        jLActive.setText("Enable Polling");
        jLPush.setText("Enable Push");
        jLMaxEmail.setText("Max email number");
        jLRefreshTime.setText("Refresh time (min)");
        jLMaxIMAP.setText("Max IMAP emails");
        jLMSAddress.setText("E-mail address");

        jLUser.setBounds(new Rectangle(LEFT_PAD, getYCoord(0), LABEL_WIDTH, HEIGHT));
        jLPassword.setBounds(new Rectangle(LEFT_PAD, getYCoord(1), LABEL_WIDTH, HEIGHT));
        jLConfirmPassword.setBounds(new Rectangle(LEFT_PAD, getYCoord(2), LABEL_WIDTH, HEIGHT));
        jLMSAddress.setBounds(new Rectangle(LEFT_PAD, getYCoord(3),LABEL_WIDTH, HEIGHT));
        jLActive.setBounds(new Rectangle(LEFT_PAD, getYCoord(6), LABEL_WIDTH, HEIGHT));
        jLPush.setBounds(new Rectangle(LEFT_PAD, getYCoord(5), LABEL_WIDTH, HEIGHT));
        jLRefreshTime.setBounds(new Rectangle(LEFT_PAD, getYCoord(7), LABEL_WIDTH, HEIGHT));
        jLMaxEmail.setBounds(new Rectangle(LEFT_PAD, getYCoord(8), LABEL_WIDTH, HEIGHT));
        jLMaxIMAP.setBounds(new Rectangle(LEFT_PAD, getYCoord(9),LABEL_WIDTH, HEIGHT));

        left_pad += LABEL_WIDTH + SPACING_X;

        jTFUser.setBounds(new Rectangle(left_pad, getYCoord(0), WIDTH, HEIGHT));
        jTFPassword.setBounds(new Rectangle(left_pad, getYCoord(1), WIDTH, HEIGHT));
        jTFConfirmPassword.setBounds(new Rectangle(left_pad, getYCoord(2), WIDTH, HEIGHT));
        jTFAddress.setBounds(new Rectangle(left_pad, getYCoord(3), WIDTH, HEIGHT));
        jCBActive.setBounds(new Rectangle(left_pad - 4, getYCoord(6), WIDTH, HEIGHT));
        jCBPush.setBounds(new Rectangle(left_pad - 4, getYCoord(5), WIDTH, HEIGHT));
        jFTRefreshTime.setBounds(new Rectangle(left_pad, getYCoord(7), WIDTH, HEIGHT));
        jCBoxMaxEmail.setBounds(new Rectangle(left_pad, getYCoord(8), WIDTH, HEIGHT));
        jCBoxMaxIMAP.setBounds(new Rectangle(left_pad, getYCoord(9), WIDTH, HEIGHT));

        jTFUser.setColumns(20);
        jTFPassword.setColumns(20);
        jTFConfirmPassword.setColumns(20);

        jCBActive.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateRefreshtimeEnabled();
            }
        });

        // right side

        left_pad = LEFT_PAD + LABEL_WIDTH + 50 + SPACING_X + WIDTH + SPACING_SECTIONS_X;

        jLMailServer.setText("Mail server");
        jLDescription.setText("Description");
        jLServerProduct.setText("Server product");
        jLProtocol.setText("Protocol");
        jLOutgoingServer.setText("Outgoing server");
        jLIncomingServer.setText("Incoming server");
        jLSSLOut.setText("SSL Outgoing");
        jLSSLIn.setText("SSL Incoming");
        jlInboxFolderName.setText("Inbox folder name");
        jlOutboxFolderName.setText("Outbox folder name");
        jlSentFolderName.setText("Sent folder name");
        jlDraftsFolderName.setText("Draft folder name");
        jlTrashFolderName.setText("Trash folder name");
        jlDeleteOnServer.setText("Soft-Delete on server");

        jLMailServer.setBounds(new Rectangle(left_pad, getYCoord(0), LABEL_WIDTH, HEIGHT));
        jLDescription.setBounds(new Rectangle(left_pad, getYCoord(1), LABEL_WIDTH, HEIGHT));
        jLServerProduct.setBounds(new Rectangle(left_pad, getYCoord(2), LABEL_WIDTH, HEIGHT));
        jLProtocol.setBounds(new Rectangle(left_pad, getYCoord(3), LABEL_WIDTH, HEIGHT));
        jLOutgoingServer.setBounds(new Rectangle(left_pad, getYCoord(4), LABEL_WIDTH, HEIGHT));
        jLIncomingServer.setBounds(new Rectangle(left_pad, getYCoord(5), LABEL_WIDTH, HEIGHT));
        jLSSLOut.setBounds(new Rectangle(left_pad, getYCoord(6), LABEL_WIDTH, HEIGHT));
        jLSSLIn.setBounds(new Rectangle(left_pad, getYCoord(7), LABEL_WIDTH, HEIGHT));
        jlDeleteOnServer.setBounds(new Rectangle(left_pad, getYCoord(8), LABEL_WIDTH, HEIGHT));
        jlInboxFolderName.setBounds(new Rectangle(left_pad, getYCoord(9), LABEL_WIDTH, HEIGHT));
        jlOutboxFolderName.setBounds(new Rectangle(left_pad, getYCoord(10), LABEL_WIDTH, HEIGHT));
        jlSentFolderName.setBounds(new Rectangle(left_pad, getYCoord(11), LABEL_WIDTH, HEIGHT));
        jlDraftsFolderName.setBounds(new Rectangle(left_pad, getYCoord(12), LABEL_WIDTH, HEIGHT));
        jlTrashFolderName.setBounds(new Rectangle(left_pad, getYCoord(13), LABEL_WIDTH, HEIGHT));

        left_pad += LABEL_WIDTH + 20;

        // mail server combo

        jCBoxMailServer.setLocation(left_pad, getYCoord(0));
        jCBoxMailServer.setSize(new Dimension(300, HEIGHT));

        //
        // When a user selects a public mail server the fields for that mail
        // server are automatically filled.
        //
        jCBoxMailServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                mailServer = (MailServer)jCBoxMailServer.getSelectedItem();

                updateRefreshtimeEnabled();

                updateMailServerData();

                updateMailServerDataEnabled();
                updatePortsValues();
                updateFolderNamesEnabled();

                updateKeyStoreFieldsEnabled();
            }
        });

        jTFDescription.setLocation(left_pad, getYCoord(1));
        jTFDescription.setSize(300, HEIGHT);
        jTFDescription.setColumns(20);

        // server product

        jCBoxServerProduct.setBounds(new Rectangle(left_pad, getYCoord(2), WIDTH, HEIGHT));

        // protocol

        jCBoxProtocol.setBounds(new Rectangle(left_pad, getYCoord(3), WIDTH, HEIGHT));
        jCBoxProtocol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePortsValues();
                updateFolderNamesEnabled();
                updateFolderActivationEnabled();
                resetFolderValues();
            }
        });

        jTFOutgoingServer.setBounds(new Rectangle(left_pad, getYCoord(4), WIDTH, HEIGHT));
        jTFIncomingServer.setBounds(new Rectangle(left_pad, getYCoord(5), WIDTH, HEIGHT));

        jCBSSlOut.setBounds(new Rectangle(left_pad - 4, getYCoord(6), WIDTH, HEIGHT));
        jCBSSlIn.setBounds(new Rectangle(left_pad - 4, getYCoord(7), WIDTH, HEIGHT));

        jCBSSlIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateKeyStoreFieldsEnabled();
            }
        });
        jCBSSlOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateKeyStoreFieldsEnabled();
            }
        });

        jTFOutgoingServer.setColumns(10);
        jTFIncomingServer.setColumns(10);

        jCBDeleteOnServer.setBounds(new Rectangle(left_pad - 4, getYCoord(8), WIDTH, HEIGHT));

        jCBInboxFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(9), 18, HEIGHT));
        jCBOutboxFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(10), 18, HEIGHT));
        jCBSentFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(11), 18, HEIGHT));
        jCBDraftsFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(12), 18, HEIGHT));
        jCBTrashFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(13), 18, HEIGHT));

        left_pad += WIDTH + SPACING_X;

        jLOutgoingServerPort.setBounds(new Rectangle(left_pad, getYCoord(4), 40, HEIGHT));
        jLOutgoingServerPort.setText("Port");

        jLIncomingServerPort.setBounds(new Rectangle(left_pad, getYCoord(5), 40, HEIGHT));
        jLIncomingServerPort.setText("Port");

        left_pad += 40 + SPACING_SECTIONS_X;

        // outgoing and incoming server ports

        jTFOutgoingServerPort.setBounds(new Rectangle(left_pad, getYCoord(4), 60, HEIGHT));
        jTFOutgoingServerPort.setColumns(5);

        jTFIncomingServerPort.setBounds(new Rectangle(left_pad, getYCoord(5), 60, HEIGHT));
        jTFIncomingServerPort.setColumns(5);

        left_pad += 60 + SPACING_SECTIONS_X;

        jLAuth.setBounds(new Rectangle(left_pad, getYCoord(4), 60, HEIGHT));
        jLAuth.setText("Auth");

        left_pad += 40 + SPACING_SECTIONS_X;

        jCBAuth.setBounds(new Rectangle(left_pad, getYCoord(4), 60, HEIGHT));

        // inbox folder name

        //left_pad = LEFT_PAD + LABEL_WIDTH + 20 + SPACING_X + WIDTH + SPACING_SECTIONS_X + LABEL_WIDTH + 20;
        left_pad -= 280;

        jTFInboxFolderName.setBounds(new Rectangle(left_pad, getYCoord(9), WIDTH, HEIGHT));
        jTFOutboxFolderName.setBounds(new Rectangle(left_pad, getYCoord(10), WIDTH, HEIGHT));
        jTFSentFolderName.setBounds(new Rectangle(left_pad, getYCoord(11), WIDTH, HEIGHT));
        jTFDraftsFolderName.setBounds(new Rectangle(left_pad, getYCoord(12), WIDTH, HEIGHT));
        jTFTrashFolderName.setBounds(new Rectangle(left_pad, getYCoord(13), WIDTH, HEIGHT));
        jBListFolders.setBounds(new Rectangle(left_pad + WIDTH + 10, getYCoord(9), 100, 20));

        // buttons

        left_pad = 100; // TODO improve positioning

        jBSave.setText("Ok");
        jBCancel.setText("Close");
        jBListFolders.setText("Folders");

        jBSave.setBounds(new Rectangle(335, getYCoord(15), 100, 20));
        jBCancel.setBounds(new Rectangle(455, getYCoord(15), 100, 20));

        final Container container = this;
        jBListFolders.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //
                // Gets current account data.
                //
                // @todo only a subset of the account data may be necessary in
                // order to get folders list and only some validation may be
                // necessary. By now all the account data are validated
                // (validateData) and filled (fillAccount) before saving just to
                // list folder names.
                //
                ValidationError error = validateData();

                if (error.getErrorStrings().size() != 0){
                    return;
                }

                fillAccount();

                //
                // Asks mail server for folders names for current account. If
                // folders names retrieving is not possible (eg: connection
                // parameters are not properly configured) then an error message
                // is shown in the log message windows and method exits.
                //
                String[] foldersNames = null;
                try {
                    foldersNames = WSDao.getImapFolders(account);
                } catch (InboxListenerException ex) {
                    Log.error("Error while retrieving folders names.", ex);
                    return;
                }
                final FolderListPanel folderListPanel =
                        new FolderListPanel(foldersNames, account);

                //
                // Folder list UI is shown as a popup window.
                //
                Container parent = container;
                while ( (parent = parent.getParent()) != null) {
                    if (parent instanceof Dialog) {
                        break;
                    }
                }

                final JDialog folderListDialog = new JDialog((Dialog)parent);

                folderListDialog.setModal(true);
                folderListDialog.setResizable(false);

                folderListDialog.setSize(400, 360);
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension window = folderListDialog.getSize();
                folderListDialog.setLocation((screen.width - window.width) / 2,
                        (screen.height - window.height) / 2);

                folderListDialog.setContentPane(folderListPanel);

                folderListPanel.setCancelAction(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        folderListDialog.setVisible(false);
                        folderListDialog.dispose();
                    }
                });

                folderListPanel.setSaveAction(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {

                        //
                        // updates folder names
                        //
                        Map mapping = folderListPanel.getFolderMapping();

                        if (mapping.containsKey("Inbox")){
                            jTFInboxFolderName.setText((String)mapping.get("Inbox"));
                        }
                        if (mapping.containsKey("Outbox")){
                            jTFOutboxFolderName.setText((String)mapping.get("Outbox"));
                        }
                        if (mapping.containsKey("Sent")){
                            jTFSentFolderName.setText((String)mapping.get("Sent"));
                        }
                        if (mapping.containsKey("Drafts")){
                            jTFDraftsFolderName.setText((String)mapping.get("Drafts"));
                        }
                        if (mapping.containsKey("Trash")){
                            jTFTrashFolderName.setText((String)mapping.get("Trash"));
                        }

                        //
                        // close popup
                        //
                        folderListDialog.setVisible(false);
                        folderListDialog.dispose();
                    }
                });

                folderListDialog.setVisible(true);
            }
        });


        //
        // Add componenents to this panel
        //
        add(panelName);

        // left side

        add(jLUser);
        add(jLPassword);
        add(jLConfirmPassword);
        add(jLActive);
        add(jLPush);
        add(jLMaxEmail);
        add(jLRefreshTime);
        add(jLMaxIMAP);
        add(jLMSAddress);
        add(jTFUser);
        add(jTFPassword);
        add(jTFConfirmPassword);
        add(jCBActive);
        add(jCBPush);
        add(jCBoxMaxEmail);
        add(jFTRefreshTime);
        add(jCBoxMaxIMAP);
        add(jTFAddress);

        // right side

        add(jLMailServer);
        add(jLDescription);
        add(jLServerProduct);
        add(jLProtocol);
        add(jLOutgoingServer);
        add(jLIncomingServer);
        add(jLOutgoingServerPort);
        add(jLIncomingServerPort);
        add(jLSSLIn);
        add(jLSSLOut);
        add(jlInboxFolderName);
        add(jlOutboxFolderName);
        add(jlSentFolderName);
        add(jlDraftsFolderName);
        add(jlTrashFolderName);
        add(jlDeleteOnServer);

        add(jCBoxMailServer);
        add(jTFDescription);
        add(jCBoxServerProduct);
        add(jCBoxProtocol);
        add(jTFOutgoingServer);
        add(jTFIncomingServer);
        add(jCBSSlIn);
        add(jCBSSlOut);
        add(jCBInboxFolderName);
        add(jCBOutboxFolderName);
        add(jCBSentFolderName);
        add(jCBDraftsFolderName);
        add(jCBTrashFolderName);
        add(jCBDeleteOnServer);

        add(jLOutgoingServerPort);
        add(jLIncomingServerPort);

        add(jTFOutgoingServerPort);
        add(jTFIncomingServerPort);

        add(jLAuth);
        add(jCBAuth);
        add(jTFInboxFolderName);
        add(jTFOutboxFolderName);
        add(jTFSentFolderName);
        add(jTFDraftsFolderName);
        add(jTFTrashFolderName);
        add(jBSave);
        add(jBCancel);
        add(jBListFolders);

        //
        // Sets the traversal order policy
        //
        setTabOrder();
    }

    /**
     * Fills all the fields with the given account.
     * <p/>
     * This method only fills data of each component.
     */
    public void fillFields(){

        //
        // title
        //
        StringBuilder sb = new StringBuilder("User Account Details (");
        sb.append(user.getUsername());

        if (this.account.getMsMailboxname() != null &&
                !"".equals(this.account.getMsMailboxname())){
            sb.append(" - " + this.account.getMsMailboxname());
        }
        sb.append(")");
        panelName.setText(sb.toString());

        //
        // left side
        //
        jTFUser.setText(account.getMsLogin());
        jTFPassword.setText(account.getMsPassword());
        jTFConfirmPassword.setText(account.getMsPassword());
        jCBActive.setSelected(account.getActive());
        jCBPush.setSelected(account.getPush());
        jFTRefreshTime.setText(Long.toString(account.getPeriod()));
        jCBoxMaxEmail.setSelectedItem(Integer.toString(account.getMaxEmailNumber()));
        jCBoxMaxIMAP.setSelectedItem(Integer.toString(account.getMaxImapEmail()));
        jTFAddress.setText(account.getMsAddress());

        updateRefreshtimeEnabled();

        //
        // right side
        //

        // Like if an event on mail server combo occurs.

        if (operationType == OperationType.ACCOUNT_INSERTION){
            this.mailServer = customServer;
        } else if (operationType == OperationType.ACCOUNT_UPDATE){
            this.mailServer = account.getMailServer();
        }

        //
        // If the account mail server is a public one then "selects" it in the
        // mail servers combo box.
        //
        // It is not possible to compare the account mail server against a public
        // one using its id; so the "description" is used, as it can't be modified
        // by a user if it is a public server.
        //
        if (!isCustomServer(this.mailServer)) {
            for (int i = 0; i < publicMailServers.length; i++) {
                if (publicMailServers[i].getDescription().equals(this.mailServer.getDescription())){
                    jCBoxMailServer.setSelectedItem(publicMailServers[i]);
                }
            }
        }

        // fill fields containing server data
        updateMailServerData();
        updateMailServerDataEnabled();

        if (operationType == OperationType.ACCOUNT_INSERTION) {
            updatePortsValues();
        } else if (operationType == OperationType.ACCOUNT_UPDATE) {
            fillPortsValues(mailServer);
        }

        updateFolderNamesEnabled();
        updateKeyStoreFieldsEnabled();
        updateFolderActivationEnabled();
    }

    /**
     * Fills the account with fields data.
     */
    public void fillAccount(){
        this.account = validatedAccount;
    }

    /**
     * Help methods to align visual components.
     */
    private int getYCoord(int i) {
        return TOP_PAD + i * (HEIGHT + SPACING_Y);
    }

    /**
     * Validates fields values.
     *
     * @return an error object that contains one or more error message strings,
     * if some validation failed
     */
    public ValidationError validateData() {

        if (validatedAccount == null){
            validatedAccount = new MailServerAccount();
            validatedAccount.setMailServer(new MailServer());
        }

        validatedAccount.setId(account.getId());
        validatedAccount.getMailServer().setMailServerId(mailServer.getMailServerId()); //account.getMailServer().getMailServerId());

        ValidationError error = new ValidationError();

        //
        // Fields on the right side
        //

        String username = jTFUser.getText();
        if (isEmpty(username)){
            error.addErrorString("Username is empty");
        }

        String password = new String(jTFPassword.getPassword());
        String confirmPassword = new String(jTFConfirmPassword.getPassword());

        if (isEmpty(password) || isEmpty(confirmPassword)){
            error.addErrorString("Field 'Password' or 'Confirm password' is empty");
        } else {
            if (!password.equals(confirmPassword)){
                error.addErrorString("Fields 'Password' and 'Confirm password' must be the same");
            }
        }

        int maxEmailInt = Integer.parseInt((String)jCBoxMaxEmail.getSelectedItem());

        String refreshTimeText = jFTRefreshTime.getText();
        int refreshTimeInt = -1;
        if (isEmpty(refreshTimeText)){
            error.addErrorString("Refresh time is empty");
        } else {
            try {
                refreshTimeInt = Integer.parseInt(refreshTimeText.trim());
                if (refreshTimeInt == 0){
                    error.addErrorString("Refresh time must be greater than 0");
                }
            } catch (Exception e) {
                error.addErrorString("Refresh time must be an integer number");
            }
        }

        int maxIMAPInt = Integer.parseInt((String)jCBoxMaxIMAP.getSelectedItem());

        String address = jTFAddress.getText();
        if (isEmpty(address)){
            error.addErrorString("Address is empty");
        }

        //
        // Fields on the left side
        //

        String description = jTFDescription.getText();
        if (isEmpty(description)){
            error.addErrorString("Description for the mail server is null");
        }

        String outServer = jTFOutgoingServer.getText();
        if (isEmpty(outServer)){
            error.addErrorString("Outgoing server is empty");
        }

        String inServer = jTFIncomingServer.getText();
        if (isEmpty(inServer)){
            error.addErrorString("Incoming server is empty");
        }

        String outPort = jTFOutgoingServerPort.getText();
        int outPortInt = -1;
        if (isEmpty(outPort)){
            error.addErrorString("Port for outgoing server is empty");
        } else {
            try {
                outPortInt = Integer.parseInt(outPort.trim());
                if (outPortInt<0){
                    error.addErrorString("Port for outgoing server must be a positive number");
                }
            } catch (Exception e) {
                error.addErrorString("Port for outgoing server must be an integer number");
            }
        }

        String inPort = jTFIncomingServerPort.getText();
        int inPortInt = -1;
        if (isEmpty(inPort)){
            error.addErrorString("Port for incoming server is empty");
        } else {
            try {
                inPortInt = Integer.parseInt(inPort.trim());
                if (inPortInt<0){
                    error.addErrorString("Port for incoming server must be a positive number");
                }
            } catch (Exception e) {
                error.addErrorString("Port for incoming server must be an integer number");
            }
        }

        // folder names

        String InboxFolderName = jTFInboxFolderName.getText();
        if (isEmpty(InboxFolderName)){
            error.addErrorString("Inbox folder name is empty");
        }

        String OutboxFolderName = jTFOutboxFolderName.getText();
        if (isEmpty(OutboxFolderName)){
            error.addErrorString("Outbox folder name is empty");
        }

        String SentFolderName = jTFSentFolderName.getText();
        if (isEmpty(SentFolderName)){
            error.addErrorString("Sent folder name is empty");
        }

        String DraftsFolderName = jTFDraftsFolderName.getText();
        if (isEmpty(DraftsFolderName)){
            error.addErrorString("Drafts folder name is empty");
        }

        String TrashFolderName = jTFTrashFolderName.getText();
        if (isEmpty(TrashFolderName)){
            error.addErrorString("Trash folder name is empty");
        }

        if (error.getErrorStrings().size() == 0){

            //
            // Fill validated account.
            //

            validatedAccount.setUsername(user.getUsername());
            validatedAccount.setMsLogin(username);
            validatedAccount.setMsPassword(new String(jTFPassword.getPassword()));
            validatedAccount.setActive(jCBActive.isSelected());
            validatedAccount.setPush(jCBPush.isSelected());
            validatedAccount.setMaxEmailNumber(maxEmailInt);
            validatedAccount.setPeriod(refreshTimeInt);
            validatedAccount.setMaxImapEmail(maxIMAPInt);
            validatedAccount.setMsAddress(address);

            MailServer validatedMailServer = validatedAccount.getMailServer();

            validatedMailServer.setIsPublic(mailServer.getIsPublic());

            validatedMailServer.setDescription(description.trim());
            String serverProduct = (String)jCBoxServerProduct.getSelectedItem();
            validatedMailServer.setMailServerType(serverProduct);
            String protocol = (String)jCBoxProtocol.getSelectedItem();
            validatedMailServer.setProtocol(protocol);
            validatedMailServer.setOutServer(outServer.trim());
            validatedMailServer.setInServer(inServer);
            validatedMailServer.setOutPort(outPortInt);
            validatedMailServer.setInPort(inPortInt);
            validatedMailServer.setOutAuth(jCBAuth.isSelected());

            validatedMailServer.setIsSSLIn(jCBSSlIn.isSelected());
            validatedMailServer.setIsSSLOut(jCBSSlOut.isSelected());

            validatedMailServer.setIsSoftDelete(jCBDeleteOnServer.isSelected());
            validatedMailServer.setInboxPath(InboxFolderName);
            validatedMailServer.setOutboxPath(OutboxFolderName);
            validatedMailServer.setSentPath(SentFolderName);
            validatedMailServer.setDraftsPath(DraftsFolderName);
            validatedMailServer.setTrashPath(TrashFolderName);
            validatedMailServer.setInboxActivation(jCBInboxFolderName.isSelected());
            validatedMailServer.setOutboxActivation(jCBOutboxFolderName.isSelected());
            validatedMailServer.setSentActivation(jCBSentFolderName.isSelected());
            validatedMailServer.setDraftsActivation(jCBDraftsFolderName.isSelected());
            validatedMailServer.setTrashActivation(jCBTrashFolderName.isSelected());

            validatedAccount.setMailServer(validatedMailServer);

        } else {
            //
            // In case of error a popup windows is shown.
            //

            ArrayList<String> errorStrings = error.getErrorStrings();
            StringBuffer errorMessage = new StringBuffer();
            errorMessage.append("Error while validating data:\n\n");
            for (String errorString : errorStrings) {
                errorMessage.append(errorString);
                errorMessage.append('\n');
            }

            Object[] options = {"OK"};
            JOptionPane.showOptionDialog(this, errorMessage.toString(), "Warning",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, options, options[0]);

        }

        return error;
    }

    /**
     * Util method to verify if a text is empty or not (for example to check if
     * a mandatory field is empty).
     *
     * @param text text to be verified
     */
    private boolean isEmpty(String text){
        if (text == null || text.trim().equals("")){
            return true;
        }
        return false;
    }

    /**
     * Initializes combo boxes.
     */
    private void initComboBoxes(){

        //
        // init the max imap emails combo box
        //
        MutableComboBoxModel jCBoxMaxEmailModel = new DefaultComboBoxModel();
        jCBoxMaxEmailModel.addElement("20");
        jCBoxMaxEmailModel.addElement("30");
        jCBoxMaxEmailModel.addElement("50");
        jCBoxMaxEmailModel.addElement("100");
        jCBoxMaxEmailModel.addElement("150");
        jCBoxMaxEmailModel.addElement("200");
        jCBoxMaxEmailModel.addElement("250");
        jCBoxMaxEmailModel.addElement("300");
        jCBoxMaxEmailModel.addElement("350");
        jCBoxMaxEmail.setModel(jCBoxMaxEmailModel);

        //
        // init the max imap emails combo box
        //
        MutableComboBoxModel jCBoxMaxIMAPModel = new DefaultComboBoxModel();
        jCBoxMaxIMAPModel.addElement("10");
        jCBoxMaxIMAPModel.addElement("20");
        jCBoxMaxIMAPModel.addElement("30");
        jCBoxMaxIMAP.setModel(jCBoxMaxIMAPModel);

        //
        // Initialize the Protocol combo box.
        //

        MutableComboBoxModel jCBoxProtocolModel = new DefaultComboBoxModel();
        jCBoxProtocolModel.addElement(Def.PROTOCOL_IMAP);
        jCBoxProtocolModel.addElement(Def.PROTOCOL_POP3);
        jCBoxProtocol.setModel(jCBoxProtocolModel);

        //
        // Initialize the Server Product combo box.
        //

        MutableComboBoxModel jCBoxServerProductModel = new DefaultComboBoxModel();
        jCBoxServerProductModel.addElement(Def.SERVER_TYPE_OTHER);
        jCBoxServerProductModel.addElement(Def.SERVER_TYPE_COURIER);
        jCBoxServerProductModel.addElement(Def.SERVER_TYPE_EXCHANGE);
        jCBoxServerProductModel.addElement(Def.SERVER_TYPE_GMAIL);
        jCBoxServerProductModel.addElement(Def.SERVER_TYPE_AOL);
        jCBoxServerProductModel.addElement(Def.SERVER_TYPE_HOTMAIL);
        jCBoxServerProduct.setModel(jCBoxServerProductModel);

        //
        // Init mail server combo box.
        //

        //
        // Creates the mail servers list :
        //
        // - creates the empty mail server, whose description is "custom"; this
        //   mail server object represent each non-public mail server
        // - retrieves the list of the public mail servers
        //
        customServer = createCustomServer();

        try {
            publicMailServers = WSDao.getMailServers(null);
        } catch (Exception ex) {
        }

        mailServers = new MailServer[publicMailServers.length + 1];

        mailServers[0] = customServer;
        for (int i = 0; i < publicMailServers.length; i++) {
            mailServers[i + 1] = publicMailServers[i];
        }

        //
        // Each item in the combo is rendered as its text description.
        // The MailServerComboBoxRendered class implement this behavior.
        //
        jCBoxMailServer.setRenderer(new MailServerComboBoxRendered());

        // Add mailservers to the combo model.

        ArrayComboBoxModel jCBoxMailServerModel =
                new ArrayComboBoxModel(mailServers);
        jCBoxMailServer.setModel(jCBoxMailServerModel);
    }

    /**
     * Updates protocol combo box as the user make a selection of a mail server
     * from the list (that is: as the selected mail server changes).
     */
    private void updateProtocolComboValue(){

        if (mailServer.getProtocol() == null) {
            return;
        }

        String protocol = mailServer.getProtocol().toLowerCase();

        String item = null;
        if (protocol.equals(Def.PROTOCOL_IMAP.toLowerCase())){
            item = Def.PROTOCOL_IMAP;
        } else if (protocol.equals(Def.PROTOCOL_POP3.toLowerCase())) {
            item = Def.PROTOCOL_POP3;
        }

        jCBoxProtocol.setSelectedItem(item);

    }

    /**
     * Sets the mailserver data.
     */
    private void fillMailServerData(MailServer msa){

        jTFDescription.setText(msa.getDescription());

        if (msa.getMailServerType().equals(Def.SERVER_TYPE_OTHER)){
            jCBoxServerProduct.setSelectedItem(Def.SERVER_TYPE_OTHER);
        } else if (msa.getMailServerType().equals(Def.SERVER_TYPE_COURIER)){
            jCBoxServerProduct.setSelectedItem(Def.SERVER_TYPE_COURIER);
        } else if (msa.getMailServerType().equals(Def.SERVER_TYPE_EXCHANGE)){
            jCBoxServerProduct.setSelectedItem(Def.SERVER_TYPE_EXCHANGE);
        } else if (msa.getMailServerType().equals(Def.SERVER_TYPE_GMAIL)){
            jCBoxServerProduct.setSelectedItem(Def.SERVER_TYPE_GMAIL);
        } else if (msa.getMailServerType().equals(Def.SERVER_TYPE_AOL)){
            jCBoxServerProduct.setSelectedItem(Def.SERVER_TYPE_AOL);
        } else if (msa.getMailServerType().equals(Def.SERVER_TYPE_HOTMAIL)){
            jCBoxServerProduct.setSelectedItem(Def.SERVER_TYPE_HOTMAIL);
        }

        if (msa.getProtocol().equals(Def.PROTOCOL_IMAP)){
            jCBoxProtocol.setSelectedItem(Def.PROTOCOL_IMAP);
        } else if (msa.getProtocol().equals(Def.PROTOCOL_POP3)){
            jCBoxProtocol.setSelectedItem(Def.PROTOCOL_POP3);
        }

        jTFOutgoingServer.setText(msa.getOutServer());
        jTFIncomingServer.setText(msa.getInServer());

        jCBAuth.setSelected(msa.getOutAuth());
        jCBSSlIn.setSelected(msa.getIsSSLIn());
        jCBSSlOut.setSelected(msa.getIsSSLOut());
        jCBDeleteOnServer.setSelected(msa.getIsSoftDelete());

        jTFInboxFolderName.setText(msa.getInboxPath());
        jTFOutboxFolderName.setText(msa.getOutboxPath());
        jTFSentFolderName.setText(msa.getSentPath());
        jTFDraftsFolderName.setText(msa.getDraftsPath());
        jTFTrashFolderName.setText(msa.getTrashPath());

        jCBInboxFolderName.setSelected(msa.getInboxActivation());
        jCBOutboxFolderName.setSelected(msa.getOutboxActivation());
        jCBSentFolderName.setSelected(msa.getSentActivation());
        jCBDraftsFolderName.setSelected(msa.getDraftsActivation());
        jCBTrashFolderName.setSelected(msa.getTrashActivation());

    }

    /**
     * Updates mail server data as the user make a selection of a mail server
     * from the list (that is: as the selected mail server changes).
     */
    private void updateMailServerData(){
        fillMailServerData(mailServer);
    }

    /**
     * Enable/disable the refresh time field depending on the enable polling flag.
     */
    private void updateRefreshtimeEnabled(){

        if (jCBActive.isSelected()){
            jFTRefreshTime.setEnabled(true);
        } else {
            jFTRefreshTime.setEnabled(false);
        }
    }

    /**
     * Enable/disable mail server data fields depending on the selected server.
     * If the selected server is not the custom server then all the server data
     * fileds are disabled.
     */
    private void updateMailServerDataEnabled(){

        JComponent[] components = new JComponent[9];
        components[0] = jTFDescription;
        components[1] = jCBoxServerProduct;
        components[2] = jCBoxProtocol;
        components[3] = jTFOutgoingServer;
        components[4] = jTFOutgoingServerPort;
        components[5] = jTFIncomingServer;
        components[6] = jTFIncomingServerPort;
        components[7] = jCBAuth;
        components[8] = jCBDeleteOnServer;

        if (isCustomServer(mailServer)){
            for (int i = 0; i < components.length; i++) {
                components[i].setEnabled(true);
            }
        } else {
            for (int i = 0; i < components.length; i++) {
                components[i].setEnabled(false);
            }
        }

    }

    /**
     * Sets the the in/out server ports fields with the given account object.
     */
    private void fillPortsValues(MailServer msa){

        jTFOutgoingServerPort.setText(Integer.toString(msa.getOutPort()));
        jTFIncomingServerPort.setText(Integer.toString(msa.getInPort()));

    }

    /**
     * Updates the server ports fields as the selected protocol changes.
     */
    private void updatePortsValues(){

        if (isCustomServer(mailServer)) {

            jTFOutgoingServerPort.setText("25");
            String protocol = (String)jCBoxProtocol.getSelectedItem();

            if (protocol.toLowerCase().equals(Def.PROTOCOL_IMAP.toLowerCase())){
                jTFIncomingServerPort.setText(Integer.toString(Def.PROTOCOL_IMAP_PORT));
            } else if (protocol.toLowerCase().equals(Def.PROTOCOL_POP3.toLowerCase())){
                jTFIncomingServerPort.setText(Integer.toString(Def.PROTOCOL_POP3_PORT));
            }
        } else {
            fillPortsValues(mailServer);
        }
    }

    /**
     * Enable/disable the key store fields (that is: key store filename,
     * key store passphrase) depending on the SSL flag.
     */
    private void updateKeyStoreFieldsEnabled(){

        if (!isCustomServer(mailServer)){
            jCBSSlIn.setEnabled(false);
            jCBSSlOut.setEnabled(false);
        } else {
            jCBSSlIn.setEnabled(true);
            jCBSSlOut.setEnabled(true);
        }
    }
    /*
     * Enable/disable folder path fields.
     */
    private void updateFolderNamesEnabled(){

        JComponent[] components = new JComponent[5];
        components[0] = jTFInboxFolderName;
        components[1] = jTFOutboxFolderName;
        components[2] = jTFSentFolderName;
        components[3] = jTFDraftsFolderName;
        components[4] = jTFTrashFolderName;

        String protocol = (String)jCBoxProtocol.getSelectedItem();
        if (protocol.toLowerCase().equals(Def.PROTOCOL_POP3.toLowerCase())){
            for (int i = 0; i < components.length; i++) {
                components[i].setEnabled(false);
            }
        } else
            if (protocol.toLowerCase().equals(Def.PROTOCOL_IMAP.toLowerCase())) {
            for (int i = 0; i < components.length; i++) {
                components[i].setEnabled(true);
            }
            }
    }

    /*
     * reset folder name values for POP mail server.
     */
    private void resetFolderValues(){

        String protocol = (String)jCBoxProtocol.getSelectedItem();

        if (protocol.toLowerCase().equals(Def.PROTOCOL_POP3.toLowerCase())){

            jTFInboxFolderName.setText(Def.FOLDER_INBOX_ENG);
            jTFInboxFolderName.setEnabled(false);
            jTFOutboxFolderName.setText(Def.FOLDER_OUTBOX_ENG);
            jTFOutboxFolderName.setEnabled(false);
            jTFSentFolderName.setText(Def.FOLDER_SENT_ENG);
            jTFSentFolderName.setEnabled(false);
            jTFDraftsFolderName.setText(Def.FOLDER_DRAFTS_ENG);
            jTFDraftsFolderName.setEnabled(false);
            jTFTrashFolderName.setText(Def.FOLDER_TRASH_ENG);
            jTFTrashFolderName.setEnabled(false);

            jCBInboxFolderName.setEnabled(false);
            jCBInboxFolderName.setSelected(true);
            jCBOutboxFolderName.setEnabled(false);
            jCBInboxFolderName.setSelected(true);
            jCBSentFolderName.setEnabled(false);
            jCBDraftsFolderName.setEnabled(false);
            jCBTrashFolderName.setEnabled(false);

        }

    }


    /**
     * Enable/disable mail server folder names depending on the protocol.
     */
    private void updateFolderActivationEnabled(){

        JComponent[] components = new JComponent[6];
        components[0] = jCBInboxFolderName;
        components[1] = jCBOutboxFolderName;
        components[2] = jCBSentFolderName;
        components[3] = jCBDraftsFolderName;
        components[4] = jCBTrashFolderName;
        components[5] = jBListFolders;

        components[0].setEnabled(false);
        components[1].setEnabled(false);

        String protocol = (String)jCBoxProtocol.getSelectedItem();
        if (protocol.toLowerCase().equals(Def.PROTOCOL_POP3.toLowerCase())){
            for (int i = 2; i < components.length; i++) {
                components[i].setEnabled(false);
            }
        } else
            if (protocol.toLowerCase().equals(Def.PROTOCOL_IMAP.toLowerCase())) {
            for (int i = 2; i < components.length; i++) {
                components[i].setEnabled(true);
            }
            }
    }
    /**
     * Creates the custom mail server object.
     */
    private MailServer createCustomServer(){

        MailServer customServer = new MailServer();

        customServer.setIsPublic(false);

        customServer.setMailServerType((String)jCBoxServerProduct.getItemAt(0));
        customServer.setProtocol((String)jCBoxProtocol.getItemAt(0));

        customServer.setDescription("custom");

        customServer.setInboxPath(Def.FOLDER_INBOX_ENG);
        customServer.setOutboxPath(Def.FOLDER_OUTBOX_ENG);
        customServer.setSentPath(Def.FOLDER_SENT_ENG);
        customServer.setDraftsPath(Def.FOLDER_DRAFTS_ENG);
        customServer.setTrashPath(Def.FOLDER_TRASH_ENG);

        customServer.setInboxActivation(true);
        customServer.setOutboxActivation(true);
        customServer.setSentActivation(false);
        customServer.setDraftsActivation(false);
        customServer.setTrashActivation(false);

        return customServer;
    }

    // ----------------------------------------------------------- Inner classes

    /**
     * This class represent the renderer for the JComboBox for selecting mail
     * server.
     */
    class MailServerComboBoxRendered extends DefaultListCellRenderer {

        /** Creates a new instance of MailServerComboBoxRendered */
        public MailServerComboBoxRendered() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            super.getListCellRendererComponent(list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus);

            if (value instanceof MailServer){
                MailServer mailServer = (MailServer)value;
                setText(mailServer.getDescription());
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }
    }

    /**
     * Checks if a given mail server is custom or not (that is: is public or
     * not).
     *
     * @param msa mail server to be checked
     * @return <code>true</code> if the mail server is custom (that is: not
     * public), <code>false</code> if the mail server is not custom (that is:
     * the server is a public one)
     */
    private boolean isCustomServer(MailServer msa){
        return !msa.getIsPublic();
    }

    /**
     * Sets the tab order for the fields in the panel.
     */
    private void setTabOrder(){

        JComponent[] traversalOrder = new JComponent[] {

            // left side
            jTFUser,
            jTFPassword,
            jTFConfirmPassword,
            jTFAddress,
            jCBActive,
            jCBPush,
            jFTRefreshTime,
            jCBoxMaxEmail,
            jCBoxMaxIMAP,

            // right side
            jCBoxMailServer,
            jTFDescription,
            jCBoxServerProduct,
            jCBoxProtocol,
            jTFOutgoingServer,
            jTFOutgoingServerPort,
            jCBAuth,
            jTFIncomingServer,
            jTFIncomingServerPort,
            jCBSSlIn,
            jCBSSlOut,
            jCBInboxFolderName,
            jTFInboxFolderName,
            jCBOutboxFolderName,
            jTFOutboxFolderName,
            jCBSentFolderName,
            jTFSentFolderName,
            jCBDraftsFolderName,
            jTFDraftsFolderName,
            jCBTrashFolderName,
            jTFTrashFolderName,
            jCBDeleteOnServer
        };

        setFocusCycleRoot(true);
        setFocusTraversalPolicy(new ArrayFocusTraversalPolicy(traversalOrder));
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Sets the action bound to the save button.
     */
    public void setSaveAction(ActionListener actionListener){
        jBSave.addActionListener(actionListener);
    }

    /**
     * Sets the action bound to the cancel button.
     */
    public void setCancelAction(ActionListener actionListener){
        jBCancel.addActionListener(actionListener);
    }
}
