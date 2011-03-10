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
package com.funambol.framework.protocol;

import java.util.List;
import java.util.ArrayList;

import com.funambol.framework.core.*;
import com.funambol.framework.database.Database;

import com.funambol.framework.protocol.ProtocolUtil;
import com.funambol.framework.protocol.v11.SyncModificationsRequirements;

/**
 * Represents a Client Modification package of the SyncML protocol.
 *
 *  The class is designed to be used in two times. First a <i>ClientModification</i>
 *  is created and checked for validity and compliancy with the protocol. Than
 *  <i>getResponse()</i> can be used to get a response message for the given
 *  request. During the request validation process some information about the
 *  request message are cached into instance variables and used in <i>getResponse()</i>.<br>
 *
 * @version $Id: SyncModifications.java,v 1.1.1.1 2008-02-21 23:35:39 stefano_fornari Exp $
 */
public class SyncModifications extends SyncPackage implements Flags {

    // ------------------------------------------------------------ Constructors

    /** Constructors. It creates a new instance from the message header and body
     * plus the databases to synchronize.
     * It also checks if the requirements specified by the SyncML protocol are
     * met; if not a Sync4jException is thrown.
     * @param syncHeader the header of the syncronization packet
     * @param syncBody the body of the syncronization packet
     * @param syncDb the array of databases to be synchronized
     * @throws Sync4jException in case SyncML requiremets are not respected
     */
    public SyncModifications(final SyncHdr syncHeader, final SyncBody syncBody, Database[] syncDb) throws Sync4jException {
        super(syncHeader, syncBody);
        checkRequirements();
        databases = syncDb;
    }

    public SyncModifications(final SyncHdr syncHeader, final SyncBody syncBody) throws Sync4jException {
        super(syncHeader, syncBody);
    }

    // -------------------------------------------------------------- Properties

    /**
     * Has the server sent its capabilities and is expecting a response?
     * If yes, <i>serverCapabilitiesCmdId</i> is set to the id of the Put command
     * sent by the server. If not, <i>serverCapabilitiesCmdId</i> is empty.
     */
    private CmdID serverCapabilitiesCmdId = null;

    /**
     * Returns the serverCapabilitiesCmdId property.
     *
     * @return the serverCapabilitiesCmdId property.
     */
    public CmdID getServerCapabilitiesCmdId() {
        return this.serverCapabilitiesCmdId;
    }

    /**
     * Sets the serverCapabilitiesCmdId property.
     *
     * @param serverCapabilitiesCmdId new value
     */
    public void setServerCapabilitiesCmdId(CmdID serverCapabilitiesCmdId) {
        this.serverCapabilitiesCmdId = serverCapabilitiesCmdId;
    }

    /**
     * The results command in response to the request of client capabilities
     */
    private Results clientCapabilitiesResults = null;

    /**
     * Returns the clientCapabilitiesResults property.
     *
     * @return the clientCapabilitiesResults property.
     */
    public Results getClientCapabilitiesResults() {
        return this.clientCapabilitiesResults;
    }

    /**
     * Return client DevInf sent into Results command by client
     */
    public DevInf getClientDeviceInfo() throws ProtocolException {
        if (clientCapabilitiesResults != null) {
            DevInfItem item = (DevInfItem)clientCapabilitiesResults.getItems().get(0);
            return item.getDevInfData().getDevInf();
        }
        return null;
    }

    /**
     * The status command in response to the sending of server capabilities
     */
    private Status serverCapabilitiesStatus = null;

    /**
     * Returns the serverCapabilitiesStatus property.
     *
     * @return the serverCapabilitiesStatus property.
     */
    public Status getServerCapabilitiesStatus() {
        return this.serverCapabilitiesStatus;
    }

    /**
     * The client Sync command identifier. It is used when a response is required.
     */
    private CmdID clientSyncCmdId = null;

    /**
     * Returns the clientSyncCmdId property.
     *
     * @return the clientSyncCmdId property.
     */
    public CmdID getClientSyncCmdId() {
        return this.clientSyncCmdId;
    }

    /**
     * Sets the clientSyncCmdId property.
     *
     * @param clientSyncCmdId new value
     */
    public void setClientSyncCmdId(CmdID clientSyncCmdId) {
        this.clientSyncCmdId = clientSyncCmdId;
    }

    /**
     * The modification commands the server wants to sent to the client.
     */
    private AbstractCommand[] serverModifications = null;

    /**
     * Returns the serverModifications property.
     *
     * @return the serverModifications property.
     */
    public AbstractCommand[] getServerModifications() {
        return this.serverModifications;
    }

    /**
     * Sets the serverModifications property.
     *
     * @param serverModifications new value
     */
    public void setServerModifications(AbstractCommand[] serverModifications) {
        this.serverModifications = serverModifications;
    }

    /**
     * The status to be returned for the client sync command.
     */
    private Status[] clientModificationsStatus = null;

    /**
     * Returns the clientModificationsStatus property.
     *
     * @return the clientModificationsStatus property.
     */
    public Status[] getClientModificationsStatus() {
        return this.clientModificationsStatus;
    }

    /**
     * Sets the clientModificationsStatus property.
     *
     * @param clientModificationsStatus new value
     */
    public void setClientModificationsStatus(Status[] clientModificationsStatus) {
        this.clientModificationsStatus = clientModificationsStatus;
    }

    /**
     * Caches the commands sent by the client. It is set during the
     * checking of the requirements.
     */
    private AbstractCommand[] clientCommands = null;

    /**
     * Returns the clientCommands property.
     *
     * @return the clientCommands property.
     */
    public AbstractCommand[] getClientCommands() {
        return clientCommands;
    }

    /**
     * Caches the SyncCommand sent by the client. It is set during the checking
     * of requirements.
     */
    private Sync[] clientSyncCommands = null;

    /**
     * Returns the clientSyncCommands property.
     *
     * @return the clientSyncCommands property.
     */
    public Sync[] getClientSyncCommands() {
        return this.clientSyncCommands;
    }

    /**
     * Databases that the server wants to synchronize.
     */
    private Database[] databases = null;

    /**
     * Sets the databases property.
     *
     * @param databases new value
     */
    public void setDatabases(Database[] databases) {
        this.databases = databases;
    }

    /**
     * Returns the databases property.
     *
     * @return the databases property.
     */
    public Database[] getDatabases() {
        return this.databases;
    }

    /**
     * The alert commands the server wants to sent to the client.
     */
    private Alert[] modificationsAlert = null;

    /**
     * Returns the alertModifications property.
     *
     * @return the alertModifications property.
     */
    public Alert[] getModificationsAlert() {
        return this.modificationsAlert;
    }

    /**
     * Sets the alertModifications property.
     *
     * @param alertModifications new value
     */
    public void setModificationsAlert(Alert[] modificationsAlert) {
        this.modificationsAlert = modificationsAlert;
    }

    /**
     * Did the client challenge for authentication? If so, this property must
     * contain server credentials.
     */
    private Cred serverCredentials;

    /**
     * Sets serverCredentials
     *
     * @param serverCredentials the new server credentials
     */
    public void setServerCredentials(Cred serverCredentials) {
        this.serverCredentials = serverCredentials;
    }

    /**
     * Gets serverCredentials
     */
    public Cred getServerCredentials() {
        return serverCredentials;
    }
    // ---------------------------------------------------------- Public methods

    /** Checks that all requirements regarding the header of the initialization
     * packet are respected.
     * @throws ProtocolException if header requirements are not respected
     */
    public void checkHeaderRequirements() throws ProtocolException {
        SyncModificationsRequirements.checkDTDVersion     (syncHeader.getVerDTD()   );
        SyncModificationsRequirements.checkProtocolVersion(syncHeader.getVerProto() );
        SyncModificationsRequirements.checkSessionId      (syncHeader.getSessionID());
        SyncModificationsRequirements.checkMessageId      (syncHeader.getMsgID()    );
        SyncModificationsRequirements.checkTarget         (syncHeader.getTarget()               );
        SyncModificationsRequirements.checkSource         (syncHeader.getSource()               );
    }

    /** Checks that all requirements regarding the body of the initialization
     * packet are respected.
     *
     * NOTE: bullet 2 pag 34 is not clear. Ignored for now.
     * @throws ProtocolException if body requirements are not respected
     */
    public void checkBodyRequirements() throws ProtocolException {
        // NOTE: initializes the clientCommands property
        clientCommands = (AbstractCommand[])syncBody.getCommands().toArray(new AbstractCommand[0]);

        //
        // If the server sent the device information to the client and requested
        // a response, serverCapabilitiesCmdId contains the command id of the
        // request command. A Status command with the same cmd id reference
        // must exist.
        //
        checkServerCapabilitiesStatus();

        //
        // Check if there is a Results command.
        //
        checkClientCapabilitiesResult();

        //
        // The Sync command must exists
        //
        checkSyncCommand();
    }

    /**
     * Constructs a proper response message.<p>
     * The sync package to the client has the following purposes:
     * <ul>
     *  <li>To inform the client about the results of sync analysis.
     *  <li>To inform about all data modifications, which have happened in the
     *      server since the previous time when the server has sent the
     *      modifications to the client.
     * </ul>
     *
     * @param msgId the msg id of the response
     * @return the response message
     *
     * @throws ProtocolException in case of error or inconsistency
     */
    public SyncML getResponseMessage(String msgId)
    throws ProtocolException {
        SyncHdr responseHeader = getResponseHeader(msgId);
        AbstractCommand[] commands =
            (AbstractCommand[])getResponseCommands(msgId).toArray(new AbstractCommand[0]);

        SyncBody responseBody = new SyncBody(
                                   commands,
                                    isFlag(FLAG_FINAL_MESSAGE) /* final */
                                );

        try {
            return new SyncML(responseHeader, responseBody);
        } catch (RepresentationException e) {
            //
            // It should never happen !!!!
            //
            throw new ProtocolException("Unexpected error", e);
        }
    }

    /**
     * Returns the response commands in response to the incoming synchronization
     * message.
     *
     * @param msgId the message id to use
     *
     * @return an array of AbstractCommand
     *
     * @throws ProtocolException in case of errors
     */
    public List getResponseCommands(String msgId) throws ProtocolException {
        ArrayList commandList = new ArrayList();

        assert(idGenerator != null);

        //
        // Constructs all required response commands.
        //
        // NOTE: if NoResp is specified in the header element, than no
        //       response commands must be returned regardless NoResp is
        //       specified or not in subsequent commands
        //
        if (syncHeader.isNoResp() == false) {

            TargetRef[] targetRefs = new TargetRef[] { new TargetRef(syncHeader.getTarget().getLocURI()) };
            SourceRef[] sourceRefs = new SourceRef[] { new SourceRef(syncHeader.getSource().getLocURI()) };

            Status statusCommand = new Status(
                                              idGenerator.next()               ,
                                              syncHeader.getMsgID()            ,
                                              "0" /* command ref */            ,
                                              SyncHdr.COMMAND_NAME /* see SyncML specs */ ,
                                              targetRefs                       ,
                                              sourceRefs                       ,
                                              null /* credential */            ,
                                              null /* challenge */             ,
                                              new Data(StatusCode.OK)          ,
                                              new Item[0]
                                          );

            commandList.add(statusCommand);

            //
            // 2. The Status element MUST be included in SyncBody if requested by
            //    the client. It is now used to indicate the general status of
            //    the sync analysis and the status information related to data
            //    items sent by the client (e.g., a conflict has happened.).
            //
            for (int i=0; (  isFlag(FLAG_SYNC_STATUS_REQUIRED)
                          && (clientModificationsStatus != null)
                          && (i<clientModificationsStatus.length) ); ++i) {
                commandList.add(clientModificationsStatus[i]);
            }

            //
            // Create Status command for client Alerts which have a code that
            // isn't an initialization code and for Results command if present
            //
            for (int i=0; clientCommands != null && i<clientCommands.length; ++i) {

                if (clientCommands[i].isNoResp()) {
                    continue;
                }

                targetRefs = null;
                sourceRefs = null;
                Item[] items = new Item[0];
                int status = StatusCode.OK;
                String commandReference = clientCommands[i].getCmdID().getCmdID();

                if (clientCommands[i] instanceof Alert) {
                    //
                    // We are in modifications and so the initialization alerts
                    // are skipped
                    //
                    if (AlertCode.isInitializationCode(((Alert)clientCommands[i]).getData())) {
                        continue;
                    }
                }

                if (clientCommands[i] instanceof Alert ||
                    clientCommands[i] instanceof Results) {

                    items = (Item[])((ItemizedCommand)clientCommands[i]).getItems().toArray(new Item[0]);

                    ArrayList trefs = new ArrayList();
                    ArrayList srefs = new ArrayList();
                    Target t;
                    Source s;
                    for (int j=0; j<items.length; ++j) {
                        t = items[j].getTarget();
                        s = items[j].getSource();

                        if (t != null) {
                            trefs.add(new TargetRef(t));
                        }
                        if (s != null) {
                            srefs.add(new SourceRef(s));
                        }
                    }  // next j

                    if (trefs.size() > 0) {
                        targetRefs = (TargetRef[])trefs.toArray(new TargetRef[trefs.size()]);
                    }
                    if (srefs.size() > 0) {
                        sourceRefs = (SourceRef[])srefs.toArray(new SourceRef[srefs.size()]);
                    }
                } else {
                    continue;
                }

                //
                // Removing the items of type DevInfItem
                // because a Status can't contain a DevInfItem
                // 
                ArrayList newItems = new ArrayList(items.length);
                for (int j = 0; j<items.length; j++) {
                    if (!(items[j] instanceof DevInfItem)) {
                        newItems.add(items[j]);
                    }
                }

                statusCommand = new Status(
                        idGenerator.next(),
                        syncHeader.getMsgID(),
                        clientCommands[i].getCmdID().getCmdID(),
                        clientCommands[i].getName(),
                        targetRefs,
                        sourceRefs,
                        null /* credential */       ,
                        null /* challenge */        ,
                        new Data(status)            ,
                        (Item[])(newItems.toArray(new Item[0]))
                );

                commandList.add(statusCommand);
            }//end for clientCommands
        }

        //
        // The Alert element, if present, MUST be included in SyncBody.
        //
        for (int i=0; ((modificationsAlert != null) && (i<modificationsAlert.length)); ++i) {
            commandList.add(modificationsAlert[i]);
        }

        //
        // 3. The Sync element MUST be included in SyncBody, if earlier there
        // were no occurred errors, which could prevent the server to process
        // the sync analysis and to send its modifications back to the client.
        //
        for (int i=0; ((serverModifications != null) && (i<serverModifications.length)); ++i) {
            commandList.add(serverModifications[i]);
        }

    	return commandList;
    }

    /**
     * Create a Status command for the Sync sent by the client.
     *
     * <b>NOTE</b>: the protocol does not specify any information about the format
     * and the content of this message. By now a dummy status command is created
     * and returned.
     *
     * @return a StatusCommand object
     */
    public Status createSyncStatusCommand() {
        return new Status(  idGenerator.next(),
                            "0" /* message id; TO DO */          ,
                            clientSyncCmdId.getCmdID()           ,
                            Sync.COMMAND_NAME                    ,
                            (TargetRef[])null /* target refs */  ,
                            (SourceRef[])null /* source refs */  ,
                            null /* credential */                ,
                            null /* chal */                      ,
                            null /* Data */                      ,
                            null /* items */
        );

    }

    /** For the Sync element, there are the following requirements.
     * <ul>
     *   <li> CmdID is required.
     *   <li> The response can be required for the Sync command. (See the Caching of Map Item,
     *        Chapter 2.3.1)
     *   <li> Target is used to specify the target database.
     *   <li> Source is used to specify the source database.
     * </ul>
     *
     * 5. If there is any modification in the server after the previous sync,
     * there are following requirements for the operational elements (e.g.,
     * Replace, Delete, and Add 4 ) within the Sync element.
     * <ul>
     *   <li> CmdID is required.
     *   <li> The response can be required for these operations.
     *   <li> Source MUST be used to define the temporary GUID (See Definitions)
     *        of the data item in the server if the operation is an addition.
     *        If the operation is not an addition, Source MUST NOT be included.
     *   <li> Target MUST be used to define the LUID (See Definitions) of the
     *        data item if the operation is not an addition. If the operation is
     *        an addition, Target MUST NOT be included.
     *   <li> The Data element inside Item is used to include the data itself if
     *        the operation is not a seletion.
     *   <li> The Type element of the MetaInf DTD MUST be included in the Meta
     *        element to indicate the type of the data item (E.g., MIME type).
     *        The Meta element inside an operation or inside an item can be used.
     * </ul>
     * @param db the database to be synchronized
     * @return a Sync command
     * @throws ProtocolException if any protocol requirement is not respected
     */
    public Sync createSyncCommand(Database db)
    throws ProtocolException {
        CmdID syncId = idGenerator.next();

        AbstractCommand[] commands = null;

        // if db.getMethod is One_Way_Sync_CLIENT
        // no synccommand from Server to Client

        if ((db.getMethod() != AlertCode.ONE_WAY_FROM_CLIENT) &&
                (db.getMethod() != AlertCode.SMART_ONE_WAY_FROM_CLIENT)) {
            commands = prepareCommands(db);
        }

        return new Sync(
            syncId                             ,
            isFlag(FLAG_SYNC_RESPONSE_REQUIRED),
            null                               , /* credentials */
            db.getTarget()                     ,
            db.getSource()                     ,
            null                               , /* Meta        */
            new Long(0)                        ,
            commands
        );

    }

    /**
     * Returns an array of synchronization commands (Add, Copy, Delete, Exec,
     * Replace) based on the content of the given database.
     *
     * @param db the database to be synchronized
     *
     * @return an array of AbstractCommand
     */
    public AbstractCommand[] prepareCommands(Database db) {
        ArrayList commands = new ArrayList();

        Meta meta = new Meta();
        meta.setType(db.getType());

        Item[] items = null;  // reused many times

        //
        // Add
        //
        items = db.getAddItems();
        if (items != null) {
            commands.add(
                new Add(
                    idGenerator.next()                          ,
                    isFlag(FLAG_MODIFICATIONS_RESPONSE_REQUIRED),
                    null /* credentials */                      ,
                    meta                                        ,
                    items                                       )
            );
        }

        //
        // Copy
        //
        items = db.getCopyItems();
        if (items != null) {
            commands.add(
                new Copy(
                    idGenerator.next()                          ,
                    isFlag(FLAG_MODIFICATIONS_RESPONSE_REQUIRED),
                    null /* credentials */                      ,
                    meta                                        ,
                    items                                       )
            );
        }

        //
        // Delete
        //
        items = db.getDeleteItems();
        if (items != null) {
            commands.add(
                new Delete(
                    idGenerator.next()                          ,
                    isFlag(FLAG_MODIFICATIONS_RESPONSE_REQUIRED),
                    isFlag(FLAG_ARCHIVE_DATA)                   ,
                    isFlag(FLAG_SOFT_DELETE)                    ,
                    null /* credentials */                      ,
                    meta                                        ,
                    items                                       )
            );
        }

        //
        // Exec
        //
        items = db.getExecItems();

        for (int i=0; ((items != null) && (i<items.length)); ++i) {
            commands.add(
                new Exec(
                    idGenerator.next()                          ,
                    isFlag(FLAG_MODIFICATIONS_RESPONSE_REQUIRED),
                    null /* credentials */                      ,
                    items[i]                                    )
            );
        }

        //
        // Replace
        //
        items = db.getReplaceItems();
        if (items != null) {
            commands.add(
                new Replace(
                    idGenerator.next()                          ,
                    isFlag(FLAG_MODIFICATIONS_RESPONSE_REQUIRED),
                    null /* credentials */                      ,
                    meta                                        ,
                    items                                       )
            );
        }

        int size = commands.size();
        AbstractCommand [] aCommands = new AbstractCommand[size];
        for (int i=0; i < size; i++) {
            aCommands[i] = (AbstractCommand)commands.get(i);
        }
        return aCommands;
    }

    /**
     * Returns the response SyncHdr to return in response of the incoming SyncML
     * message.
     *
     * @param msgId the message id to use
     *
     * @return the SyncHdr object
     *
     * @thorws ProtocolException in case of errors
     */
    public SyncHdr getResponseHeader(String msgId)
    throws ProtocolException {
        Target target = new Target(syncHeader.getSource().getLocURI(),
                                   syncHeader.getSource().getLocName());
        Source source = new Source(syncHeader.getTarget().getLocURI(),
                                   syncHeader.getTarget().getLocName());
        return new SyncHdr (
                   getDTDVersion()          ,
                   getProtocolVersion()     ,
                   syncHeader.getSessionID(),
                   msgId                    ,
                   target                   ,
                   source                   ,
                   null  /* response URI */ ,
                   false                    ,
                   serverCredentials        ,
                   null /* meta data */
               );
    }

    // --------------------------------------------------------- Private methods

    /**
     * Checks if the requested status for server capabilities has been specified.
     * <p>
     *
     * @throws ProtocolException
     */
    private void checkServerCapabilitiesStatus()
    throws ProtocolException {
        //
        // If serverCapabilitiesCmdId is null no serverCapabilities status is required
        //
        if (serverCapabilitiesCmdId == null) return;

        List list = ProtocolUtil.filterCommands(clientCommands         ,
                                                Status.class    ,
                                                serverCapabilitiesCmdId);

        if (list.size() == 0) {
            Object[] args = new Object[] { serverCapabilitiesCmdId.getCmdID() };
            throw new ProtocolException(SyncModificationsRequirements.ERRMSG_MISSING_STATUS_COMMAND, args);
        }

        serverCapabilitiesStatus = (Status)list.get(0);
    }

    /**
     * Checks if the result for client capabilities has been given.
     * <p>
     *
     * @throws ProtocolException
     */
    private void checkClientCapabilitiesResult()
    throws ProtocolException {

        ArrayList list = ProtocolUtil.filterCommands(clientCommands,
                                                     Results.class );

        if (list.size() > 0) {
            Results results = (Results)list.get(0);

            SyncModificationsRequirements.checkCapabilities(
                results,
                SyncModificationsRequirements.CLIENT_CAPABILITIES
            );

            clientCapabilitiesResults = results;
        }
    }

    /**
     * Checks the Sync command.
     * <p>Filters out the Sync Messages from client to Server with
     * ONE_WAY_SYNC_SERVER
     *
     * @throws ProtocolException
     */
    private void checkSyncCommand()
    throws ProtocolException {
        List list = ProtocolUtil.filterCommands(clientCommands   ,
                                                Sync.class);

        if (list.size() == 0) {
            clientSyncCommands = new Sync[0];
            return;
        }

        clientSyncCommands = (Sync[])list.toArray(new Sync[list.size()]);
    }
}

