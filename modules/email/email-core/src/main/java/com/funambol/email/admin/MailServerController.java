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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.funambol.admin.util.Log;

import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServer;
import com.funambol.email.util.Def;
import com.funambol.email.admin.dao.WSDAO;
import com.funambol.email.admin.mailservers.MailServerDetailsPanel;

/**
 * This class contains the logic and visual representation for public mail
 * server handling processes (insertion, modification, deletation).
 *
 * @version $Id: MailServerController.java,v 1.1 2008-03-25 11:25:33 gbmiglia Exp $
 */
public class MailServerController {

    // ------------------------------------------------------------ Private data

    /** process type */
    private OperationType operationType;

    /** current mail server */
    private MailServer mailServer;

    /** popup dialog */
    private JDialog dialog;

    /** panel for the popup window */
    private Container mainPanel;

    /** popup title */
    private String title;

    // -------------------------------------------------------------- Properties

    /** data access object */
    private WSDAO WSDao;

    /**
     * Sets the WSDao property.
     */
    public void setWSDao (WSDAO WSDao) {
        this.WSDao = WSDao;
    }

    /** parent frame */
    private Frame parentFrame;

    public void setParentFrame(Frame parent){
        this.parentFrame = parent;
    }

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of MailServerController */
    public MailServerController() {
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Mail server insertion process.
     */
    public void insertMailServer(){

        operationType = OperationType.MAILSERVER_INSERTION;

        // creates an empty mail server
        mailServer = createEmptyServer();

        mailServerDetails();
    }

    /**
     * Mail server modification process.
     * @param mailServer mail server to be updated
     * @return updated mail server
     */
    public MailServer modifyMailServer(MailServer mailServer){

        operationType = OperationType.MAILSERVER_UPDATE;

        this.mailServer = mailServer;

        final MailServerDetailsPanel mailServerDetailsPanel =
                new MailServerDetailsPanel(this.mailServer);

        mailServerDetails();

        return mailServerDetailsPanel.getMailServer();
    }

    /**
     * Mail server deletetion process.
     * @param mailServer
     * @return number of mail servers deleted, if greater than or equal to 0
     */
    public int deleteMailServer(MailServer mailServer){

        String[] options = {"Yes", "No"};
        String message = "Are you sure you want to delete this mail server";
        int choice = showMessagePopup(message, options);

        if (choice == JOptionPane.YES_OPTION){

            StringBuilder sb = new StringBuilder();
            try {
                int numDeleted = WSDao.deleteMailServer(mailServer.getMailServerId());

                if (numDeleted > 0){

                    sb.append("Mail server ")
                        .append(mailServer.getDescription())
                        .append(" deleted");
                    showMessagePopup(sb.toString());

                } else if (numDeleted == Def.ERR_SERVER_DELETION){

                    sb.append("Mail server ")
                        .append(mailServer.getDescription())
                        .append(" still in use. Not deleted");
                    showMessagePopup(sb.toString());

                }

                return numDeleted;
            } catch (InboxListenerException ex) {
            }

        }
        return 0;

    }

    // --------------------------------------------------------- Private methods

    /**
     * Shows the mail server data, for insertion or modification.
     */
    private void mailServerDetails(){

        final MailServerDetailsPanel mailServerDetailsPanel =
                new MailServerDetailsPanel(this.mailServer);

        mainPanel = mailServerDetailsPanel;

        mailServerDetailsPanel.setSaveAction(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                //
                // Validates data (fills account object only if data are valid).
                //
                ValidationError error = mailServerDetailsPanel.validateData();

                //
                // if data are valid then get the account
                //
                if (error.getErrorStrings().size() == 0){

                    mailServer = mailServerDetailsPanel.getMailServer();
                    try {
                        if (operationType == OperationType.MAILSERVER_INSERTION){
                            WSDao.insertMailServer(mailServer);
                            Log.info("Mail server data saved");
                            closePopup();
                        }

                        if (operationType == OperationType.MAILSERVER_UPDATE){
                            WSDao.updateMailServer(mailServer);
                            Log.info("Mail server data updated");
                            closePopup();
                        }

                    } catch (InboxListenerException e) {
                        Log.error(e);
                    }
                }
            }
        });

        mailServerDetailsPanel.setCancelAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closePopup();
            }
        });

        show();
    }

    /**
     * Show current process panel as a dialog.
     */
    private void show(){

        dialog = new JDialog(parentFrame);
        dialog.setContentPane(mainPanel);

        dialog.setModal(true);
        dialog.setResizable(false);

        dialog.setSize(600, 460);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension window = dialog.getSize();
        dialog.setLocation((screen.width - window.width) / 2, (screen.height - window.height) / 2);

        dialog.setVisible(true);

    }

    // --------------------------------------------------------- Private methods

    /**
     * Shows a given message with the given option in a popup window.
     *
     * @param message
     * @param options
     * @return selected option
     */
    private int showMessagePopup(String message, Object[] options){
        return JOptionPane.showOptionDialog(null, message, "Warning",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
    }

    /**
     * Creates an empty mail server.
     */
    private MailServer createEmptyServer(){

        MailServer customServer = new MailServer();

        customServer.setIsPublic(false);

        customServer.setMailServerType(Def.SERVER_TYPE_OTHER);
        customServer.setProtocol(Def.PROTOCOL_IMAP);

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

    /**
     * Closes popup windows.
     */
    private void closePopup(){
        dialog.setVisible(false);
        dialog.dispose();
    }

    /**
     * Shows a given message in a popup window.
     * @param message message to be displaied
     * @return selected option
     */
    private int showMessagePopup(String message){
        Object[] options = {"OK"};
        return JOptionPane.showOptionDialog(null, message, "Warning",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);
    }

}
