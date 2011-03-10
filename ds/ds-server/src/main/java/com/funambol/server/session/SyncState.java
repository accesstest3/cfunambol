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
package com.funambol.server.session;

import java.util.*;

import com.funambol.framework.core.*;
import com.funambol.framework.security.SecurityConstants;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jDevice;

/**
 * This class represents the state of the Syncronization process because cache
 * all informations that could be used in that process.
 * It is set during the Initialization process.
 *
 * @version $Id: SyncState.java,v 1.1.1.1 2008-02-21 23:35:58 stefano_fornari Exp $
 */
public class SyncState
implements SecurityConstants {

    // ------------------------------------------------------------- Public data

    /**
     * Cache the Sync4jDevice object (it contains Funambol information and nonces)
     */
    public Sync4jDevice device;

    /**
     * Credentials sent by client. If null no credentials has sent.
     */
    public Cred clientCredential;

    /**
     * Authenticated principal. If null, no principal has authenticated
     */
    public Sync4jPrincipal loggedPrincipal;

    /**
     * Authentication state. One of the following values:
     * <ul>
     *   <li>AUTH_UNAUTHENTICATED</li>
     *   <li>AUTH_MISSING_CREDENTIALS</li>
     *   <li>AUTH_INVALID_CREDENTIALS</li>
     *   <li>AUTH_AUTHENTICATED</li>
     * </ul>
     */
    public int authenticationState;

    /**
     * Server authentication state. One of the following values:
     * <ul>
     *   <li>AUTH_UNAUTHENTICATED</li>
     *   <li>AUTH_RETRY_1</li>
     *   <li>AUTH_RETRY_2</li>
     * </ul>
     */
    public int serverAuthenticationState;

    /**
     * Cache the SyncML Protocol version used by device
     */
    public String syncMLVerProto;

    /**
     * Is Sync with initialization?
     */
    public boolean syncWithInit;

    /**
     * Cache the response with initialization
     */
    public SyncML responseInit;

    /**
     * Cache the maximum message size
     * If the client doesn't specify it, its value is Long.MAX_VALUE
     */
    public long maxMsgSize = Long.MAX_VALUE;

    /**
     * Cache overhead for SyncHdr object
     */
    public long overheadHdr;

    /**
     * Cache overhead of Status for SyncHdr
     */
    public long sizeStatusSyncHdr;

    /**
     * Cache Status for SyncHdr
     */
    public Status statusSyncHdr;

    /**
     * Cache the maximum object size
     */
    public HashMap<String, Long> maxObjSizes;

    /**
     * Cache received large object
     */
    public String receivedLargeObject;

    /**
     * Cache size of received large object
     */
    public Long sizeOfReceivedLargeObject;

    /**
     * Cache Target LocURI of Sync command and Source LocURI of the Item that
     * contains the previous Large Object: this to check that in the next message
     * there is the end of the previous chuncked data object
     */
    public String syncLocURI;
    public String itemLocURI;

    /**
     * Large object URI of a LO being sent by the server
     */
    public String sendingLOURI;

    /**
     * This will cache commands to be returned in response to client PKG1
     */
    public ArrayList cmdCache1;

    /**
     * This will cache commands to be returned in response to client PKG3
     */
    public ArrayList cmdCache3;

    /**
     * The device capabilities sent by the client. Null if no capabilities are
     * sent.
     */
    public DevInf devInf;

    // ---------------------------------------------------------- Public Methods

    public SyncState() {
        reset();
    }

    /**
     * Reset this object to an initial state
     */
    public void reset() {
        device                       = null                ;
        clientCredential             = null                ;
        loggedPrincipal              = null                ;
        authenticationState          = AUTH_UNAUTHENTICATED;
        serverAuthenticationState    = AUTH_UNAUTHENTICATED;
        syncMLVerProto               = null                ;
        syncWithInit                 = false               ;
        responseInit                 = null                ;
        maxMsgSize                   = Integer.MAX_VALUE   ;
        overheadHdr                  = 0                   ;
        sizeStatusSyncHdr            = 0                   ;
        statusSyncHdr                = null                ;
        maxObjSizes                  = new HashMap<String, Long>();
        receivedLargeObject          = null                ;
        sizeOfReceivedLargeObject    = null                ;
        syncLocURI                   = null                ;
        itemLocURI                   = null                ;
        sendingLOURI                 = null                ;
        cmdCache1                    = new ArrayList()     ;
        cmdCache3                    = new ArrayList()     ;
        devInf                       = null                ;
    }

    /**
     * Cache the Alert command
     */
    private ArrayList listClientAlerts = new ArrayList();

    public void addClientAlerts(Alert[] clientAlerts) {
        for (int i=0; ((clientAlerts != null) && (i<clientAlerts.length)); i++) {
            listClientAlerts.add(clientAlerts[i]);
        }
    }

    public void removeClientAlert(Alert alert) {
        listClientAlerts.remove(alert);
    }

    public Alert[] getClientAlerts() {
        return (Alert[])listClientAlerts.toArray(new Alert[listClientAlerts.size()]);
    }

    /**
     * Cache the server modification commands: in the case of multimessage these
     * commands must be sent only when the client has sent its package
     */
    private ArrayList listServerModifications = new ArrayList();

    public void setServerModifications(AbstractCommand[] serverModifications) {

        int size = listServerModifications.size();

        for (int i=0; ((serverModifications != null) && (i<serverModifications.length)); i++) {
            boolean isAdded = false;
            Sync sync = (Sync)serverModifications[i];

            for (int y=0; y<size; y++) {
                Sync syncOld = (Sync)listServerModifications.get(y);

                if (sync.getTarget().getLocURI().equalsIgnoreCase(syncOld.getTarget().getLocURI()) &&
                    sync.getSource().getLocURI().equalsIgnoreCase(syncOld.getSource().getLocURI())   ) {

                    syncOld.getCommands().addAll(sync.getCommands());
                    isAdded = true;
                    break;
                }
            }
            if (!isAdded) {
                listServerModifications.add(serverModifications[i]);
            }
        }
    }

    public AbstractCommand[] getServerModifications() {
        return (AbstractCommand[])listServerModifications.toArray(
                    new AbstractCommand[listServerModifications.size()]
               );
    }

    public void clearServerModifications() {
        listServerModifications.clear();
    }

    /**
     * Cache the Status command presents in the response message
     */
    private LinkedList listStatusCmdOut = new LinkedList();

    public void addStatusCmdOut(List statusList) {
        listStatusCmdOut.addAll(statusList);
    }

    public LinkedList getStatusCmdOut() {
        return listStatusCmdOut;
    }

    public void removeStatusCmdOut(List statusCommand) {
        listStatusCmdOut.removeAll(statusCommand);
    }

    /**
     * Cache the Status command for Map presents in the response message
     */
    private LinkedList listMapStatusOut = new LinkedList();

    public void addMapStatusOut(List mapStatusList) {
        listMapStatusOut.addAll(mapStatusList);
    }

    public LinkedList getMapStatusOut() {
        return listMapStatusOut;
    }

    public void removeMapStatusOut(List mapStatus) {
        listMapStatusOut.removeAll(mapStatus);
    }

    /**
     * Cache the Alert command presents in the response message
     */
    private LinkedList listAlertCmdOut = new LinkedList();

    public void addAlertCmdOut(List alertList) {
        listAlertCmdOut.addAll(alertList);
    }

    public void addAlertCmdOut(Alert alert) {
        listAlertCmdOut.add(alert);
    }

    public LinkedList getAlertCmdOut() {
        return listAlertCmdOut;
    }

    public void removeAlertCmdOut(List alertList) {
        listAlertCmdOut.removeAll(alertList);
    }

    /**
     * Cache the AbstractCommand presents in the response message
     */
    private LinkedList listCmdOut = new LinkedList();

    public void addCmdOut(List cmdList) {
        listCmdOut.addAll(cmdList);
    }

    public LinkedList getCmdOut() {
        return listCmdOut;
    }

    public void removeCmdOut(List abstractCommand) {
        listCmdOut.removeAll(abstractCommand);
    }

    public void removeCmdOut(AbstractCommand abstractCommand) {
        listCmdOut.remove(abstractCommand);
    }

    /**
     * Cache the commands of Sync command doesn't sent
     */
    private LinkedList cmdsNotSent = new LinkedList();
    public void setCmdsNotSent(List cmdsNotSent) {
        this.cmdsNotSent.clear();
        this.cmdsNotSent.addAll(cmdsNotSent);
    }
    public LinkedList getCmdsNotSent() {
        return this.cmdsNotSent;
    }

    /**
     * Cache the part of Sync command that must still be to send
     */
    private Sync syncSplittedToSend = null;
    public void setSyncSplittedToSend(Sync syncSplittedToSend) {
        AbstractCommand[] cmds =
            (AbstractCommand[])this.getCmdsNotSent().toArray(
                new AbstractCommand[0]
            );
        this.syncSplittedToSend = new Sync(
            syncSplittedToSend.getCmdID(),
            syncSplittedToSend.isNoResp(),
            syncSplittedToSend.getCred(),
            syncSplittedToSend.getTarget(),
            syncSplittedToSend.getSource(),
            syncSplittedToSend.getMeta(),
            syncSplittedToSend.getNumberOfChanges(),
            cmds
        );
    }
    public Sync getSyncSplittedToSend() {
        return this.syncSplittedToSend;
    }
}
