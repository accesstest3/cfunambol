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
package com.funambol.server.notification.builder;

import java.util.ArrayList;

import com.funambol.framework.core.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.Sync4jLoggerName;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.builder.DSNotificationMessageBuilder;
import com.funambol.framework.tools.SyncMLUtil;
import com.funambol.framework.tools.WBXMLTools;

import com.funambol.server.config.Configuration;

/**
 * This class is an implementation of DSNotificationMessageBuilder that build the
 * message according to the DS Protocol 1.1. Note that the protocol doesn't
 * specify nothing about that, but this way seems standard for Nokia and
 * SonyEricsson SyncML 1.1 phones.
 *
 * @see DSNotificationMessageBuilder
 *
 * @version $Id: DS11NotificationMessageBuilderImpl.java,v 1.2 2008-03-04 22:13:00 stefano_fornari Exp $
 */
public class DS11NotificationMessageBuilderImpl
implements DSNotificationMessageBuilder {

    // ------------------------------------------------------------ Private data
    private FunambolLogger logger =
        FunambolLoggerFactory.getLogger(Sync4jLoggerName.SERVER_NOTIFICATION);


    /** Creates a new instance of DS112NotificationMessageBuilder */
    public DS11NotificationMessageBuilderImpl() {
    }

    /**
     * Builds the notification message with the given information
     *
     * @param sessionId       the identifier of the session associated with the
     *                        Notification Package
     * @param deviceId        the device id
     * @param deviceAddress   the address or phone number (depending on the
     *                        implementation) of the device where the message
     *                        should be sent
     * @param serverId        the id of the server
     * @param serverPw        server's password
     * @param serverNonce     server's nonce
     * @param alerts          array of Alert command for the datastore to sync,
     *                        used to create the binary message to send
     * @param protocolVersion the version of the SyncML protocol
     *
     * @return array of byte containing the notification message
     * @throws NotificationException
     */
    public Message buildMessage(int     sessionId      ,
                                String  deviceId       ,
                                String  deviceAddress  ,
                                String  serverId       ,
                                String  serverPw       ,
                                byte[]  serverNonce    ,
                                Alert[] alerts         ,
                                int     uimode         ,
                                float   protocolVersion)
    throws NotificationException {

        SessionID sessId = new SessionID("1"); // must be 1
        String msgId = "1";  // must be 1
        Target target = new Target(deviceId);

        String serverURI = Configuration.getConfiguration()
                                        .getServerConfig()
                                        .getEngineConfiguration().getServerURI();

        Source source  = new Source(serverURI);
        String respURI = null;
        Cred   cred    = null;
        Meta   meta    = null;

        SyncHdr header = new SyncHdr(Constants.DTD_1_1,
                                     Constants.PROT_1_1,
                                     sessId,
                                     msgId,
                                     target,
                                     source,
                                     respURI,
                                     false,   // noResp
                                     cred,
                                     meta);


        SyncBody body = new SyncBody(alerts, true);

        fixAlerts(alerts);

        SyncML syncMLMessage = null;
        try {

            syncMLMessage = new SyncML(header, body);
        } catch (RepresentationException ex) {
            throw new NotificationException("Error while creating the xml messsage", ex);
        }

        //
        // The sensitive data are hidden if funambol is not in debug mode.
        //
        if (logger.isTraceEnabled()) {
            logger.trace("Outgoing notification xml message: " +
                         SyncMLUtil.toXML(syncMLMessage,
                                          Configuration.getConfiguration()
                                                       .isDebugMode()));
        }

        byte[] out = null;
        try {
            //
            // The marshalling is doing directly into WBXMLTools
            // because that method is calling in the other code too
            //
            out = WBXMLTools.toWBXML(syncMLMessage);

        } catch(Exception e) {

            throw new NotificationException("Error while converting the xml messsage in WBXML", e);
        }

        return new Message(Message.Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, out);
    }

    // ---------------------------------------------------------- Private method

    /**
     * Fixed the cmdID and the source/target in the given alerts
     * @param alerts the alerts
     */
    private void fixAlerts(Alert[] alerts) {
        if (alerts == null || alerts.length == 0) {
            return;
        }
        int    cont      = 0;
        String targetURI = null;
        Item   item      = null;
        Target t         = null;
        Source s         = null;
        ArrayList items  = null;
        for (Alert alert : alerts) {
            items = alert.getItems();
            targetURI = null;
            item      = null;
            t         = null;
            s         = null;
            if (items != null && items.size() > 0) {
                item = (Item)items.get(0);
                t = item.getTarget();
                if (t != null) {
                    targetURI = t.getLocURI();
                }
                s = item.getSource();
                if (s == null) {
                    //
                    // The item doesn't have a source uri but it has a targetURI.
                    // We have to set the source uri with the target uri value and
                    // set the target uri to null.
                    //
                    if (targetURI != null) {
                        item.setSource(new Source(targetURI));
                        item.setTarget(null);
                    }
                }
            }
            //
            // Setting cmdID
            //
            alert.setCmdID(new CmdID(String.valueOf(++cont)));
        }
    }
}
