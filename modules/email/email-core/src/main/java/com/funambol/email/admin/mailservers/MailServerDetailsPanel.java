/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.email.admin.mailservers;

import java.util.ArrayList;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.funambol.admin.ui.GuiFactory;

import com.funambol.email.admin.ValidationError;
import com.funambol.email.model.MailServer;
import com.funambol.email.util.Def;
import javax.swing.JComponent;

/**
 * Panel to show mail server details.
 * <p/>
 * A mail server can be inserted or modified, as suitable actions are added to
 * this panel using setSaveAction and setCancelAction methods.
 *
 * @version $Id: MailServerDetailsPanel.java,v 1.1 2008-03-25 11:25:33 gbmiglia Exp $
 */
public class MailServerDetailsPanel extends JPanel {

    // --------------------------------------------------------------- Constants

    private final int LEFT_PAD              = 14;
    private int left_pad                    = LEFT_PAD;
    private final int TOP_PAD               = 50;
    private final int LABEL_WIDTH           = 140;
    private final int WIDTH                 = 150;
    private final int HEIGHT                = 20;
    private final int SPACING_Y             = 2;
    private final int SPACING_X             = 2;
    private final int SPACING_SECTIONS_X    = 4;

    // ------------------------------------------------------------ Private data

    //
    // Visual elements
    //

    private JLabel panelName = new JLabel();
    private JLabel jLDescription = new JLabel();
    private JLabel jLServerProduct = new JLabel();
    private JLabel jLProtocol = new JLabel();
    private JLabel jLOutgoingServer = new JLabel();
    private JLabel jLIncomingServer = new JLabel();
    private JLabel jLOutgoingServerPort = new JLabel();
    private JLabel jLIncomingServerPort = new JLabel();
    private JLabel jLAuth = new JLabel();
    private JLabel jLSSLIn = new JLabel();
    private JLabel jLSSLOut = new JLabel();
    private JLabel jlInboxFolderName = new JLabel();
    private JLabel jlOutboxFolderName = new JLabel();
    private JLabel jlSentFolderName = new JLabel();
    private JLabel jlDraftsFolderName = new JLabel();
    private JLabel jlTrashFolderName = new JLabel();
    private JLabel jlDeleteOnServer = new JLabel();

    private JTextField jTFDescription = new JTextField();
    private JComboBox jCBoxServerProduct = new JComboBox();
    private JComboBox jCBoxProtocol = new JComboBox();
    private JTextField jTFOutgoingServer = new JTextField();
    private JTextField jTFIncomingServer = new JTextField();
    private JTextField jTFOutgoingServerPort = new JTextField();
    private JTextField jTFIncomingServerPort = new JTextField();
    private JCheckBox jCBAuth = new JCheckBox();
    private JCheckBox jCBSSlIn = new JCheckBox();
    private JCheckBox jCBSSlOut = new JCheckBox();
    private JCheckBox jCBInboxFolderName = new JCheckBox();
    private JCheckBox jCBOutboxFolderName = new JCheckBox();
    private JCheckBox jCBSentFolderName = new JCheckBox();
    private JCheckBox jCBDraftsFolderName = new JCheckBox();
    private JCheckBox jCBTrashFolderName = new JCheckBox();
    private JCheckBox jCBDeleteOnServer = new JCheckBox();
    private JTextField jTFInboxFolderName = new JTextField ();
    private JTextField jTFOutboxFolderName = new JTextField ();
    private JTextField jTFSentFolderName = new JTextField ();
    private JTextField jTFDraftsFolderName = new JTextField ();
    private JTextField jTFTrashFolderName = new JTextField ();

    private JButton jBCancel = new JButton();
    private JButton jBSave = new JButton();

    // -------------------------------------------------------------- Properties

    /** Current mail server */
    private MailServer mailServer;

    /**
     * Gets the mail server property.
     * @return the current mail server
     */
    public MailServer getMailServer() {
        return mailServer;
    }


    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of MailServerDetailsPanel
     */
    public MailServerDetailsPanel(MailServer mailServer) {
        this.mailServer = mailServer;
        initComboBoxes();
        initFields();
        initGui();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Initializes visual elements.
     */
    private void initGui(){

        setLayout(null);

        // title

        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText("Mail Server Details");
        panelName.setBounds(new Rectangle(LEFT_PAD, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(new TitledBorder(""));

        left_pad = LEFT_PAD;

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

        jLDescription.setBounds(new Rectangle(left_pad, getYCoord(0), LABEL_WIDTH, HEIGHT));
        jLServerProduct.setBounds(new Rectangle(left_pad, getYCoord(1), LABEL_WIDTH, HEIGHT));
        jLProtocol.setBounds(new Rectangle(left_pad, getYCoord(2), LABEL_WIDTH, HEIGHT));
        jLOutgoingServer.setBounds(new Rectangle(left_pad, getYCoord(3), LABEL_WIDTH, HEIGHT));
        jLIncomingServer.setBounds(new Rectangle(left_pad, getYCoord(4), LABEL_WIDTH, HEIGHT));
        jLSSLOut.setBounds(new Rectangle(left_pad, getYCoord(5), LABEL_WIDTH, HEIGHT));
        jLSSLIn.setBounds(new Rectangle(left_pad, getYCoord(6), LABEL_WIDTH, HEIGHT));
        jlDeleteOnServer.setBounds(new Rectangle(left_pad, getYCoord(7), LABEL_WIDTH, HEIGHT));
        jlInboxFolderName.setBounds(new Rectangle(left_pad, getYCoord(8), LABEL_WIDTH, HEIGHT));
        jlOutboxFolderName.setBounds(new Rectangle(left_pad, getYCoord(9), LABEL_WIDTH, HEIGHT));
        jlSentFolderName.setBounds(new Rectangle(left_pad, getYCoord(10), LABEL_WIDTH, HEIGHT));
        jlDraftsFolderName.setBounds(new Rectangle(left_pad, getYCoord(11), LABEL_WIDTH, HEIGHT));
        jlTrashFolderName.setBounds(new Rectangle(left_pad, getYCoord(12), LABEL_WIDTH, HEIGHT));

        left_pad += LABEL_WIDTH + 60;

        jTFDescription.setLocation(left_pad, getYCoord(0));
        jTFDescription.setSize(300, HEIGHT);
        jTFDescription.setColumns(20);

        // server product

        jCBoxServerProduct.setBounds(new Rectangle(left_pad, getYCoord(1), WIDTH, HEIGHT));

        // protocol

        jCBoxProtocol.setBounds(new Rectangle(left_pad, getYCoord(2), WIDTH, HEIGHT));
        jCBoxProtocol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePortsValues();
                updateFolderNamesEnabled();
                updateFolderActivationEnabled();
                resetFolderValues();
            }
        });



        jTFOutgoingServer.setBounds(new Rectangle(left_pad, getYCoord(3), WIDTH, HEIGHT));
        jTFIncomingServer.setBounds(new Rectangle(left_pad, getYCoord(4), WIDTH, HEIGHT));

        jCBSSlOut.setBounds(new Rectangle(left_pad - 4, getYCoord(5), WIDTH, HEIGHT));
        jCBSSlIn.setBounds(new Rectangle(left_pad - 4, getYCoord(6), WIDTH, HEIGHT));

        jTFOutgoingServer.setColumns(10);
        jTFIncomingServer.setColumns(10);

        jCBDeleteOnServer.setBounds(new Rectangle(left_pad - 4, getYCoord(7), WIDTH, HEIGHT));

        jCBInboxFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(8), 18, HEIGHT));
        jCBOutboxFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(9), 18, HEIGHT));
        jCBSentFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(10), 18, HEIGHT));
        jCBDraftsFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(11), 18, HEIGHT));
        jCBTrashFolderName.setBounds(new Rectangle(left_pad - 4, getYCoord(12), 18, HEIGHT));

        left_pad += WIDTH + SPACING_X;

        jLOutgoingServerPort.setBounds(new Rectangle(left_pad, getYCoord(3), 40, HEIGHT));
        jLOutgoingServerPort.setText("Port");

        jLIncomingServerPort.setBounds(new Rectangle(left_pad, getYCoord(4), 40, HEIGHT));
        jLIncomingServerPort.setText("Port");

        left_pad += 40 + SPACING_SECTIONS_X;

        // outgoing and incoming server ports

        jTFOutgoingServerPort.setBounds(new Rectangle(left_pad, getYCoord(3), 60, HEIGHT));
        jTFOutgoingServerPort.setColumns(5);

        jTFIncomingServerPort.setBounds(new Rectangle(left_pad, getYCoord(4), 60, HEIGHT));
        jTFIncomingServerPort.setColumns(5);

        left_pad += 60 + SPACING_SECTIONS_X;

        jLAuth.setBounds(new Rectangle(left_pad, getYCoord(3), 60, HEIGHT));
        jLAuth.setText("Auth");

        left_pad += 40 + SPACING_SECTIONS_X;

        jCBAuth.setBounds(new Rectangle(left_pad, getYCoord(3), 60, HEIGHT));

        // inbox folder name

        //left_pad = LEFT_PAD + LABEL_WIDTH + 20 + SPACING_X + WIDTH + SPACING_SECTIONS_X + LABEL_WIDTH + 20;
        left_pad -= 280;

        jTFInboxFolderName.setBounds(new Rectangle(left_pad, getYCoord(8), WIDTH, HEIGHT));
        jTFOutboxFolderName.setBounds(new Rectangle(left_pad, getYCoord(9), WIDTH, HEIGHT));
        jTFSentFolderName.setBounds(new Rectangle(left_pad, getYCoord(10), WIDTH, HEIGHT));
        jTFDraftsFolderName.setBounds(new Rectangle(left_pad, getYCoord(11), WIDTH, HEIGHT));
        jTFTrashFolderName.setBounds(new Rectangle(left_pad, getYCoord(12), WIDTH, HEIGHT));

        // fields always disabled

        jCBInboxFolderName.setEnabled(false);
        jCBOutboxFolderName.setEnabled(false);
        //jTFInboxFolderName.setEnabled(false);

        // buttons

        left_pad = 150;

        jBSave.setText("Ok");
        jBCancel.setText("Close");

        jBSave.setBounds(new Rectangle(200, getYCoord(15), 100, 20));
        jBCancel.setBounds(new Rectangle(310, getYCoord(15), 100, 20));

        //
        // Adds components to this panel
        //

        add(panelName);
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
    }

    /**
     * Initializes combo boxes.
     */
    private void initComboBoxes(){

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
    }

    /**
     * Help methods to align visual components.
     */
    private int getYCoord(int i) {
        return TOP_PAD + i * (HEIGHT + SPACING_Y);
    }

    /**
     * Updates the server ports fields as the selected protocol changes.
     */
    private void updatePortsValues(){

        int outPort = mailServer.getOutPort();
        if (outPort == 0){
            jTFOutgoingServerPort.setText("25");
        } else {
            jTFOutgoingServerPort.setText(Integer.toString(outPort));
        }

        int inPort = mailServer.getInPort();
        if (inPort == 0){
            String protocol = (String)jCBoxProtocol.getSelectedItem();
            if (protocol.toLowerCase().equals(Def.PROTOCOL_IMAP.toLowerCase())){
                jTFIncomingServerPort.setText(Integer.toString(Def.PROTOCOL_IMAP_PORT));
            } else
                if (protocol.toLowerCase().equals(Def.PROTOCOL_POP3.toLowerCase())){
                jTFIncomingServerPort.setText(Integer.toString(Def.PROTOCOL_POP3_PORT));
                }
        } else {
             jTFIncomingServerPort.setText(Integer.toString(inPort));
        }
    }

    /*
     * Enable/disable folder path fields.
     */
    private void updateFolderNamesEnabled(){

        //jTFInboxFolderName.setEnabled(false);

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

        JComponent[] components = new JComponent[5];
        components[0] = jCBInboxFolderName;
        components[1] = jCBOutboxFolderName;
        components[2] = jCBSentFolderName;
        components[3] = jCBDraftsFolderName;
        components[4] = jCBTrashFolderName;

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
     * Initialize this panel fields.
     */
    private void initFields(){
        fillMailServerData(mailServer);
        updatePortsValues();
        resetFolderValues();
    }

    /**
     * Fill fields with the values of the mail server coupled to
     * this panel.
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
     * Check if a string is empty or not.
     * @param text text to be verified
     */
    private boolean isEmpty(String text){
        if (text == null || text.trim().equals("")){
            return true;
        }
        return false;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Validates fields values.
     *
     * @return an error object that contains one or more error message strings,
     * if some validation failed
     */
    public com.funambol.email.admin.ValidationError validateData() {

        ValidationError error = new ValidationError();

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
            // Fills mail server object.
            //

            mailServer.setDescription(description.trim());
            String serverProduct = (String)jCBoxServerProduct.getSelectedItem();
            mailServer.setMailServerType(serverProduct);
            String protocol = (String)jCBoxProtocol.getSelectedItem();
            mailServer.setProtocol(protocol);
            mailServer.setOutServer(outServer.trim());
            mailServer.setInServer(inServer);
            mailServer.setOutPort(outPortInt);
            mailServer.setInPort(inPortInt);
            mailServer.setOutAuth(jCBAuth.isSelected());

            mailServer.setIsSSLIn(jCBSSlIn.isSelected());
            mailServer.setIsSSLOut(jCBSSlOut.isSelected());

            mailServer.setIsSoftDelete(jCBDeleteOnServer.isSelected());
            mailServer.setInboxPath(InboxFolderName);
            mailServer.setOutboxPath(OutboxFolderName);
            mailServer.setSentPath(SentFolderName);
            mailServer.setDraftsPath(DraftsFolderName);
            mailServer.setTrashPath(TrashFolderName);
            mailServer.setInboxActivation(jCBInboxFolderName.isSelected());
            mailServer.setOutboxActivation(jCBOutboxFolderName.isSelected());
            mailServer.setSentActivation(jCBSentFolderName.isSelected());
            mailServer.setDraftsActivation(jCBDraftsFolderName.isSelected());
            mailServer.setTrashActivation(jCBTrashFolderName.isSelected());
            mailServer.setIsPublic(true);

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
