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
package com.funambol.email.inboxlistener.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.funambol.framework.core.Alert;
import com.funambol.framework.core.CmdID;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Target;

import com.funambol.framework.server.PushFlowEvent;
import com.funambol.framework.server.error.ServerException;
import com.funambol.framework.tools.encryption.EncryptionException;
import com.funambol.framework.tools.encryption.EncryptionTool;

import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.UnauthorizedException;

import com.funambol.pushlistener.service.registry.dao.DataAccessException;
import com.funambol.pushlistener.service.ws.NotificationWrapper;
import com.funambol.pushlistener.service.ws.UnexpectedException;

import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.inboxlistener.dbdao.InboxListenerDAO;
import com.funambol.email.inboxlistener.msdao.MailboxMSDAOCommon;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.model.SyncItemInfoInbox;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.transport.MailServerWrapperFactory;
import com.funambol.email.util.DBIDGeneratorTokenSequence;
import com.funambol.email.util.Def;
import com.funambol.email.util.token.TokenGenerator;
import com.funambol.email.util.token.TokenException;
import com.funambol.email.util.token.TokenSequence;
import com.funambol.email.util.Utility;

/**
 *
 *
 * @version $Id: InboxListenerManager.java,v 1.9 2008-06-17 12:01:52 gbmiglia Exp $
 */
public class InboxListenerManager {
    
    // -------------------------------------------------------------- Properties

    /**
     * The logger
     */
    private Logger log     = Logger.getLogger(Def.LOGGER_NAME);
    private Logger logPush = Logger.getLogger("funambol.push");

    /**
     * dao layer for the DB
     */
    private InboxListenerDAO dbdao = null;

    /**
     *
     */
    private IMailServerWrapper mswf = null;

    /**
     *
     */
    private MailboxMSDAOCommon msdao = null;

    /**
     *
     */
    private MailServerAccount msa = null;

    private TokenGenerator tokenGenerator;

    /**
     * map that contains the uid, index of the email
     * that will be added to the DB
     */
    private LinkedHashMap uidsToBeAdded = new LinkedHashMap();
    /**
     * list of the SyncItemInfo that will be added into the DB
     */
    public List<SyncItemInfo> serverInfoTobeAdded   =
            new ArrayList<SyncItemInfo>();

    /**
     * list of the SyncItemInfo that will be updated into the DB
     */
    public List<SyncItemInfoInbox> serverInfoTobeUpdated =
            new ArrayList<SyncItemInfoInbox>();

    /**
     * list of the GUID that will be removed from the DB
     */
    public ArrayList<SyncItemInfo> serverInfoToBeRemoved =
            new ArrayList<SyncItemInfo>();


    // ------------------------------------------------------------ Constructors

    /**
     *
     */
    public InboxListenerManager(InboxListenerDAO _dbdao){
        this.dbdao = _dbdao;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     *
     */
    public void init(
            MailServerAccount _msa,
            boolean saveSubject,
            boolean saveSender,
            String timeoutStore,
            String timeoutConnection,
            boolean checkCertificates) throws InboxListenerException{
        try {

            this.msa = _msa;
            this.mswf = MailServerWrapperFactory.getMailServerWrapper(msa.getMailServer().getProtocol());
            this.msdao = new MailboxMSDAOCommon(msa, saveSubject, saveSender, timeoutStore, timeoutConnection, checkCertificates);

            TokenSequence sequence = new DBIDGeneratorTokenSequence(Def.TOKEN_SEQUENCE);
            this.tokenGenerator = new TokenGenerator(sequence);
        } catch (TokenException ex) {
            throw new InboxListenerException("Error initializing InboxListenerManager Object ", ex);
        }

    }

    /**
     * This method has all the steps about the Inbox-listener job
     *
     * @param msa
     * @param ilmanager
     * @param justNewNotification
     * @param syncSourceEmail
     * @param contentTypeEmail
     */
    public void engine(MailServerAccount    msa                ,
                       boolean              justNewNotification,
                       String               syncSourceEmail    ,
                       String               contentTypeEmail   )
    throws InboxListenerException{

        boolean hasNew = false;
        long    totTimeStart = 0;
        long    totTimeStop  = 0;

        LinkedHashMap localInfos = null;
        String        newestGUID = null;
        try {

            // check the login for the gmail server
            msa.setMsLogin(Utility.getLogin(msa));

            // refresh the cache
            if (log.isTraceEnabled()) {
                log.trace("Start inboxlistener for " +
                          msa.toStringForCommandLine());
            }

            //
            // Be carefully when you change this log because is used to notify
            // the inbox-listener starting event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILCheckEvent(
                        msa.getUsername(),
                        null,
                        null,
                        "Start inboxlistener for " + msa.toStringForCommandLine())
                );
            }

            totTimeStart = System.currentTimeMillis();

            // get local info;
            // map with (key:GUID, value:SyncItemInfo)
            String[] firstGUIDArray = new String[1] ;
            localInfos = getInfosInDB(firstGUIDArray);
            newestGUID = firstGUIDArray[0];

            msdao.open(this.mswf);
        
            getInfosInMS(localInfos);
        
            msdao.close(this.mswf);

            // check new email
            hasNew = hasNewMessages(this.serverInfoTobeAdded,
                    localInfos, newestGUID, justNewNotification);

            // set info in DB
            setInfosInDB(this.serverInfoTobeAdded,
                    this.serverInfoTobeUpdated, this.serverInfoToBeRemoved);

            totTimeStop = System.currentTimeMillis();

            if (log.isTraceEnabled()) {
                log.trace("Created local cache for user: " + msa.getUsername() +
                          ", execution time: " + (totTimeStop - totTimeStart)  +
                          " ms.");
            }

        } catch (InboxListenerException e) {
            //
            // Be carefully when you change this log because is used to notify
            // an exception during the inbox-listener starting event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILCheckEventOnError(
                        msa.getUsername(),
                        null,
                        null,
                        "Error checking email account for user: " + msa.getUsername()), e
                );
            }
            throw e;
        } catch (Exception e) {
            //
            // Be carefully when you change this log because is used to notify
            // an exception during the inbox-listener starting event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILCheckEventOnError(
                        msa.getUsername(),
                        null,
                        null,
                        "Error checking email account for user: " + msa.getUsername()), e
                );
            }
            throw new InboxListenerException(e.getMessage(), e);
        }


        if (hasNew) {
            //
            // Be carefully when you change this log because it is used to notify
            // when a new email is detected
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILNewEmailEvent(msa.getUsername(),
                                                        null,
                                                        null, 
                                                        "New mail detected")
                );
            }
 
            // call notification system and notify the user
            if (msa.getPush()) {
                if (log.isTraceEnabled()) {
                    log.trace("Sending notification");
                }

                notifyUser(msa.getUsername(), syncSourceEmail, contentTypeEmail);
            } else {
                //
                // Be carefully when you change this log because it is used to 
                // notify that the push is disabled
                //
                if (logPush.isTraceEnabled()) {
                    logPush.trace(
                        PushFlowEvent.createILPushEventOnError(
                            msa.getUsername(),
                            null,
                            null,
                            "Push disabled")
                    );
                }
            }
        }

    }

    // --------------------------------------------------------- Private Methods

    /**
     *
     */
    private void getInfosInMS(LinkedHashMap localInfos)
    throws InboxListenerException {

        String username = msa.getUsername();
        String protocol = msa.getMailServer().getProtocol();
        int    max      = msa.getMaxEmailNumber();

        try {

            String[] validity = new String[1] ;

            // get uid list from MS (protocol command UIDL)
            // get 2 emails over the max
            // this method also calculate the new crc
            List<SyncItemInfoInbox> serverUIDS = msdao.getUids(protocol, 
                                                               max + Def.MAX_MAIL_NUMBER_OFFSET, 
                                                               validity);
            String UIDV = validity[0];

            // create the New, Deleted, Updated
            setOperationList(localInfos, serverUIDS);

            // get info from MS just for the New Emails
            serverInfoTobeAdded = msdao.getNewEmails(uidsToBeAdded, UIDV);

            // add tokens to the items to be added
            for (SyncItemInfo syncItemInfo : serverInfoTobeAdded) {
                
                // create a token
                byte[] token = tokenGenerator.getToken();
                
                // encrypt the token
                String encryptedToken = EncryptionTool.encrypt(new String(token));
                
                syncItemInfo.setToken(encryptedToken);
            }
            
            if (log.isTraceEnabled()) {
                log.trace("OperationList - email to be added:" + serverInfoTobeAdded.size() +
                        ", email to be updated:" + serverInfoTobeUpdated.size() +
                        ", email to be removed:" + serverInfoToBeRemoved.size());
            }

        } catch (InboxListenerException e) {
            throw new InboxListenerException("Error getting info in Mail Server for the user " +
                    username + " protocol " + protocol + ". ", e);
        } catch (TokenException e){
            throw new InboxListenerException("Error while retrieving token", e);            
        } catch (EncryptionException e){
            throw new InboxListenerException("Error while encrypting token", e);            
        }
    }

    /**
     * this method returns the items in the caching system for the
     * given user the map contains the email order by received_date desc.
     * like in the admin tool --> email connector --> account --> cache
     *
     * @param firstGUIDArray first GUID in the Array. store the newest item info
     * @return list of items
     */
    private LinkedHashMap getInfosInDB(String[] firstGUIDArray)
    throws InboxListenerException {
        LinkedHashMap localInfos = new LinkedHashMap();
        String username = msa.getUsername();
        String protocol = msa.getMailServer().getProtocol();
        try {
            localInfos = dbdao.getItems(username, protocol, firstGUIDArray);
        } catch (DataAccessException ex) {
            throw new InboxListenerException("Unable to load the local items " +
                    "with username: " + username + " and protocol " + protocol, ex);
        }
        return localInfos;
    }

    /**
     * return the items in the caching system for the given user
     *
     *
     * @param serverInfoTobeAdded
     * @param serverInfoTobeUpdated
     * @param deletedGUIDS
     * @throws com.funambol.email.exception.InboxListenerException
     */
    private void setInfosInDB(List<SyncItemInfo> serverInfoTobeAdded,
            List<SyncItemInfoInbox> serverInfoTobeUpdated,
            List<SyncItemInfo> deletedGUIDS)
            throws InboxListenerException {

        String username = msa.getUsername();
        String protocol = msa.getMailServer().getProtocol();

        try {
            dbdao.setItems(username, protocol,
                    serverInfoTobeAdded, serverInfoTobeUpdated, deletedGUIDS);

            // this code is commented because EntityDAO.getAllEmailsInbox()
            // just 'maxEmailNumber' items
            // in the cache the inbox-listener save 2 items over the maxEmailNumber
            // because the method getMessages() in the javamail API
            // is not accurated and could be return an incorrect list of emails
            /*
            // remove the item over maxEmailNumber
            LinkedHashMap localInfos = new LinkedHashMap();
            String[] firstGUIDArray = new String[1];
            localInfos = dbdao.getItems(username, protocol, firstGUIDArray);
            int num = localInfos.size();
            String localGUID = null;
            if (num > msa.getMaxEmailNumber()){
                // get GUID to remove at the end of the list
                int index = 1;
                Iterator it1 = localInfos.keySet().iterator();
                while (it1.hasNext()){
                    localGUID = (String)it1.next();
                    if (index > msa.getMaxEmailNumber()){
                        dbdao.removeItem(username, protocol, localGUID);
                    }
                    index++;
                }
            }
            */

        } catch (DataAccessException ex) {
            throw new InboxListenerException("Unable to set the local items " +
                    "with username: " + username + " and protocol " + protocol, ex);
        }
    }

    /**
     *
     * check if there is a new email. compare local info and mail server info
     * and the notification can be configurated:
     * <ul>
     * <li> - send notification only if there is a NEW email. (real new email)
     * <li> - send notification if there is a email that before was not in the inbox
     * </ul>
     *
     *
     * @param serverInfoTobeAdded (just the new messages)
     * @param localInfos
     * @param newestGUID String
     * @param justNewNotification boolean
     * @return true if there is a new email
     */
    private boolean hasNewMessages(List<SyncItemInfo> serverInfoTobeAdded,
            LinkedHashMap localInfos,
            String newestGUID,
            boolean justNewNotification)
            throws InboxListenerException {

        boolean hasNewEmail    = false;

        try {

            if (justNewNotification){
                if (localInfos.size() == 0 && serverInfoTobeAdded.size() > 0){
                    return true;
                } else {
                    hasNewEmail = checkNew(serverInfoTobeAdded, localInfos,
                            newestGUID, justNewNotification);
                }
            } else {
                if (localInfos.size() == 0 && serverInfoTobeAdded.size() > 0){
                    return true;
                } else  if (localInfos.size() < serverInfoTobeAdded.size()){
                    return true;
                } else  if (serverInfoToBeRemoved.size() != 0){
                    return true;
                } else {
                    hasNewEmail = checkNew(serverInfoTobeAdded, localInfos,
                            newestGUID, justNewNotification);
                }
            }

        } catch (Exception me){
            throw new InboxListenerException("Error checking new messages", me);
        }

        return hasNewEmail;
    }



    /**
     * Send notification to the device corresponding to the given
     * principal for the kinds of item in sourcesToNotify
     *
     * @param username user to notify
     * @param syncSourceEmail string
     * @param contentTypeEmail string
     */
    private void notifyUser(String username        ,
                            String syncSourceEmail ,
                            String contentTypeEmail) {

        ArrayList<Alert> alerts = new ArrayList<Alert>();

        if (log.isTraceEnabled()){
            log.trace("Triggering a notification to be sent");
        }

        Target target = new Target(syncSourceEmail);
        Meta meta = new Meta();
        meta.setType(contentTypeEmail);
        Item item = new Item(null, target, meta, null, false);

        Alert alert = new Alert(new CmdID(0), false, null, 206, new Item[]{item});

        alerts.add(alert);

        Alert[] alertArray = (Alert[])alerts.toArray(new Alert[alerts.size()]);

        try {

            //
            // Be carefully when you change this log because is used to notify
            // the inbox-listener pushing event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILPushEvent(
                        username,
                        null,
                        null, 
                        "Triggering a notification to be sent")
                );
            }

            NotificationWrapper.getInstance().notifyUser(username, alertArray);

        }  catch (UnauthorizedException ex) {
            //
            // This exception is logged to "ERROR" since we are interesting in the
            // issue also if the pim-listener log is set to INFO and since most likely
            // it is  a configuration issue. The stacktrace is not logged since
            // is pretty useless.
            //
            log.error("Unauthorized exception triggering a notification: "
                     + ex.getMessage());
            
            //
            // Be carefully when you change this log because is used to notify
            // an error in the inbox-listener pushing event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILPushEventOnError(
                        username,
                        null,
                        null,
                        "Unauthorized exception triggering a notification: " + ex.getMessage()), ex
                );
            }
        } catch (AdminException ex) {
            //
            // The AdminException is used from the server to notify readable error
            // message to the caller. Not sure if this exception should be logged
            // to error...
            //
            log.error(ex);

            //
            // Be carefully when you change this log because is used to notify
            // an error in the inbox-listener pushing event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILPushEventOnError(
                        username,
                        null,
                        null,
                        "Admin exception triggering a notification: " + ex.getMessage()), ex
                );
            }
        } catch (ServerException ex) {
            //
            // The server exception is used from the server to notify any issue
            // (not necessarily error) that occurs in sending notification message.
            // Since it is not an error in the pim-listener, it is logged to "TRACE"
            //
            if (log.isTraceEnabled()) {
                log.trace("Server exception triggering a notification (check the server log for details): " + ex.getMessage());
            }
            
            //
            // Be carefully when you change this log because is used to notify
            // an error in the inbox-listener pushing event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILPushEventOnError(
                        username,
                        null,
                        null,
                        "Server exception triggering a notification (check the server log for details): " + ex.getMessage()), ex
                );
            }
        } catch (UnexpectedException ex) {
            //
            // Since we don't know the error, this exception is logged to "ERROR"
            //
            log.error("Unexpected error triggering a notification", ex);
            
            //
            // Be carefully when you change this log because is used to notify
            // an error in the inbox-listener pushing event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILPushEventOnError(
                        username,
                        null,
                        null,
                        "Unexpected error triggering a notification"), ex
                );
            }
        } catch (Throwable ex) {
            //
            // Since we don't know the error, this exception is logged to "ERROR"
            //
            log.error("Error triggering a notification", ex);
            
            //
            // Be carefully when you change this log because is used to notify
            // an error in the inbox-listener pushing event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createILPushEventOnError(
                        username,
                        null,
                        null,
                        "Error triggering a notification"), ex
                );
            }
        }
    }

    // --------------------------------------------------------- Private Methods

    /**
     *
     * @param serverInfoTobeAdded
     * @param localInfos
     * @param newestGUID
     * @param justNewNotification
     * @return true if there is a new email
     */
    private boolean checkNew(List<SyncItemInfo> serverInfoTobeAdded,
            LinkedHashMap localInfos,
            String newestGUID,
            boolean justNewNotification){

        boolean hasNewEmail    = false;

        SyncItemInfo sii       = null;
        String       GUID      = null;
        String       syncLabel = null;
        Date         received  = null;

        // get the data of the oldest email
        SyncItemInfo newestSII = (SyncItemInfo)localInfos.get(newestGUID);
        Date newestReceived = null;
        if (newestSII != null){
            newestReceived = newestSII.getHeaderReceived();
        }

        for (int i=0; i<serverInfoTobeAdded.size(); i++){

            GUID      = serverInfoTobeAdded.get(i).getGuid().getKeyAsString();
            syncLabel = serverInfoTobeAdded.get(i).getInternal();
            received  = serverInfoTobeAdded.get(i).getHeaderReceived();

            if (GUID != null){
                if (syncLabel == null) {
                    sii = (SyncItemInfo)localInfos.get(GUID);
                    // if null .. it's an external email (no "X-Funambol" label)
                    if (sii == null){
                        // the notification can be configurated:
                        // - send notification only if there is a NEW email.
                        //   (real new email)
                        // - send notification if there is a email that before
                        //   was not in the inbox
                        if (justNewNotification){
                            if (newestReceived != null && received != null){
                                if (Utility.d1Afterd2(received, newestReceived)){
                                    // the received date is before the newest in the local list
                                    // so this is a new email
                                    hasNewEmail = true;
                                    break;
                                }
                            }
                        } else {
                            hasNewEmail = true;
                            break;
                        }


                    }
                }
            }
        }

        return hasNewEmail;

    }


    /**
     * fill up the 3 arrays with all the ids that:
     * <br>
     * - will be add from the cache
     * <br>
     * - will be updated from the cache
     * <br>
     * - will be removed from the cache (it looks for the id present in the
     * localInfos list but don't present in the serverInfos list)
     *
     * @param localInfos
     * @param serverGUIDS
     * @param guidsToBeAdd
     * @param guidsToBeUpdated
     * @param guidsToBeRemoved
     * @return list with the ids to be removed
     *
     */
    private void setOperationList(LinkedHashMap localInfos, List<SyncItemInfoInbox> serverUIDS)
      throws InboxListenerException {

        String serverGUID = null;
        String localGUID  = null;

        // look for the items that will be removed
        boolean found = false;
        Iterator it1ocal = localInfos.keySet().iterator();
        while (it1ocal.hasNext()){
            localGUID = (String)it1ocal.next();
            found = false;
            for (int y=0; y<serverUIDS.size(); y++){
                serverGUID  = serverUIDS.get(y).getGuid().getKeyAsString();
                if (serverGUID.equals(localGUID)){
                    found = true;
                    break;
                }
            }
            if (!found){
                serverInfoToBeRemoved.add((SyncItemInfo)localInfos.get(localGUID));
            }
        }
        found = false;
        
        // look for the id that will be add and updated
        SyncItemInfoInbox syncItemInfoInbox = null;
        SyncItemInfo      syncItemInfoDB    = null;
        int               index             = -1;
        String            uid               = null;
        long              newCRC            = 0;  
        long              oldCRC            = 0;
        for (int i=0; i<serverUIDS.size(); i++){
            
            syncItemInfoInbox = serverUIDS.get(i);
            serverGUID        = syncItemInfoInbox.getGuid().getKeyAsString();
            uid               = syncItemInfoInbox.getUid();
            index             = syncItemInfoInbox.getIndex();
            newCRC            = syncItemInfoInbox.getLastCrc();
            
            if (localInfos.containsKey(serverGUID)){
                // is in the local cache
                // check if the crc is the same; if so we don't need the update query                
                syncItemInfoDB = (SyncItemInfo)localInfos.get(serverGUID);
                oldCRC         = syncItemInfoDB.getLastCrc();                
                if (newCRC != oldCRC){
                    serverInfoTobeUpdated.add(syncItemInfoInbox);
                }
            } else {
                // is not in the local cache
                uidsToBeAdded.put(uid, new Integer(index));
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Info from mailserver - new email(s):" + uidsToBeAdded.size() +
                    ", updated mail(s):" + serverInfoTobeUpdated.size() +
                    ", removed mail(s):" + serverInfoToBeRemoved.size());
        }

    }

}