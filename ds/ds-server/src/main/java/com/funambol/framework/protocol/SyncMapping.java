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

import java.util.ArrayList;
import java.util.List;

import com.funambol.framework.core.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.Sync4jLogger;
import com.funambol.framework.protocol.v11.BasicRequirements;
import com.funambol.framework.protocol.ProtocolUtil;

/**
 * ClientCompletion class represent the phase or state that come after
 * Modification phase. In this state client send status for the sync command,
 * a status for every sub sync command like Add, Replace and finally send a
 * MapCommand in the case a AddCommand was done succesfully.
 * We need to extract this mapping information to update the LUID GUID mapping.
 * see the SyncML sync protocol for a detailed example
 *
 * @version $Id: SyncMapping.java,v 1.1.1.1 2008-02-21 23:35:39 stefano_fornari Exp $
 */
public class SyncMapping extends SyncPackage {

    // -------------------------------------------------------------- Properties
    /**
     * Funambol Logging facility
     */
    private transient FunambolLogger log = FunambolLoggerFactory.getLogger();

    /**
     * Cache the commands sent by the client. It is set during the
     * check for the requirements.
     */
    private AbstractCommand[] clientCommands = null;

    /**
     * Get all the commands extracted from the client last message.
     * @return An array of AbstractCommand
     */
    public AbstractCommand[] getClientCommands() {
        return clientCommands;
    }

    /**
     * The Map Command from the client. It's used to construct the
     * response back to client.
     */
    private Map[] mapCommands = null;

    /**
     * Get the Map command
     */
    public Map[] getMapCommands() {
        return mapCommands;
    }

    /**
     * true if Map Command where find
     */
    private boolean mapCommandFind = false;

    /**
     * @return true if a map Command were find
     */
    public boolean isMapCommandFind() {
        return mapCommandFind;
    }

    /**
     * The last message from client Identifier.
     */
    private String lastMessageId = null;

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

    // ------------------------------------------------------------ Constructors
    /**
     * Contruct a ClientCompletion State processing treatment
     * @param syncHeader The header of the client message
     * @param syncBody The body of the client message
     * @throws Sync4jException
     */
    public SyncMapping(final SyncHdr syncHeader, final SyncBody syncBody)
    throws Sync4jException {
        super(syncHeader, syncBody);
        checkHeaderRequirements();
        checkBodyRequirements();
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Checks that all requirements regarding the header of the initialization
     * packet are respected.
     *
     * @throws ProtocolException
     */
    public void checkHeaderRequirements() throws ProtocolException {
        BasicRequirements.checkDTDVersion(syncHeader.getVerDTD());
        BasicRequirements.checkProtocolVersion(syncHeader.getVerProto());
        BasicRequirements.checkSessionId(syncHeader.getSessionID());
        BasicRequirements.checkMessageId(syncHeader.getMsgID());
        BasicRequirements.checkTarget(syncHeader.getTarget());
        BasicRequirements.checkSource(syncHeader.getSource());

        //Conserve the message ID from the client message header.
        lastMessageId = syncHeader.getMsgID();
    }

    /**
     * Checks that all requirements regarding the body of the initialization
     * packet are respected.
     *
     * @throws ProtocolException
     */
    public void checkBodyRequirements() throws ProtocolException {
        clientCommands = (AbstractCommand[])syncBody.getCommands().toArray(new AbstractCommand[0]);

        // Check the status command if add ak then check for map command
        checkStatusCommands();

        //If status for AddCommand was find
        checkMapCommand();
    }

    /**
     * Check the status commands from the client to check failure and to check
     * wether or not a status from a Add Command Exist. In this case true is
     * return to toggle the Map existence.
     * todo : check the failure
     */
    private void checkStatusCommands() {
    }

    /**
     * Check The Map Command for new mapped items.
     * @throws ProtocolException
     */
    private void checkMapCommand() throws ProtocolException {
        List list = ProtocolUtil.filterCommands(clientCommands, Map.class);

        if (list.size() > 0) {
            mapCommands = (Map[]) list.toArray(new Map[list.size()]);
            mapCommandFind = true;
        } else {
            mapCommands = null;
            mapCommandFind = false;
        }
    }

    /**
     * Constructs a proper response message. That include from the syncMl spec
     * <br>
     *  <ui>
     *    <li>Status for last message syncheader</li>
     *    <li>May include Status for the mapping command</li>
     *  <ui>
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
            (AbstractCommand[]) getResponseCommands(msgId).toArray(new AbstractCommand[0]);
        SyncBody responseBody = new SyncBody(
            commands,
            isFlag(Flags.FLAG_FINAL_MESSAGE)
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
     * Returns the response commands in response to the incoming mapping
     * message.
     *
     * @param msgId the message id to use
     *
     * @return an array of AbstractCommand
     *
     * @throws ProtocolException in case of errors
     */
    public List getResponseCommands( String msgId)
    throws ProtocolException {
        ArrayList commands = new ArrayList();

        /*
        2. The Status element(s) MUST be included in SyncBody. It is now used to indicate the status of
          the Map operation(s). This or these can be sent before Package #5 is completely received.
        */
        //
        // Create the synhdr status response
        //
        TargetRef[] targetRefs = new TargetRef[] {
                            new TargetRef(syncHeader.getTarget().getLocURI()) };
        SourceRef[] sourceRefs = new SourceRef[] {
                            new SourceRef(syncHeader.getSource().getLocURI()) };
        Status synchdrStatus = new Status(
            idGenerator.next(),
            lastMessageId,
            "0",
            "SyncHdr",
            targetRefs,
            sourceRefs,
            null,
            null,
            new Data(StatusCode.OK),
            new Item[0]
        );
        commands.add(synchdrStatus);

        //
        //Create the map commands status response
        //
        if (mapCommandFind) {
            //Construct the status command for the Map command

            Status mapStatus = null;
            for (int i=0; i<mapCommands.length; ++i) {

                targetRefs = new TargetRef[] { new TargetRef(mapCommands[i].getTarget()) };
                sourceRefs = new SourceRef[] { new SourceRef(mapCommands[i].getSource()) };

                mapStatus = new Status(
                    idGenerator.next(),
                    lastMessageId,
                    mapCommands[i].getCmdID().getCmdID(),
                    Map.COMMAND_NAME,
                    targetRefs,
                    sourceRefs,
                    null,
                    null,
                    new Data(StatusCode.OK),
                    new Item[0]
                );
                commands.add(mapStatus);
            }
        }

        return commands;
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
        /*
        1. Requirements for the elements within the SyncHdr element.
            - The value of the VerDTD element MUST be '1.1'.
            - The value of the VerProto element MUST be 'SyncML/1.1'.
            - Session ID MUST be included to indicate the ID of a sync session.
            - MsgID MUST be used to unambiguously identify the message belonging a sync session
              and traveling from the client to the server.
            - The Target element MUST be used to identify the target device and service.
            - The Source element MUST be used to identify the source device.
            - The response MUST NOT be required for this message.
        */
        Target target = new Target(syncHeader.getSource().getLocURI(),
                                   syncHeader.getSource().getLocName());
        Source source = new Source(syncHeader.getTarget().getLocURI(),
                                   syncHeader.getTarget().getLocName());
        SyncHdr responseHeader = new SyncHdr(
                getDTDVersion()          ,
                getProtocolVersion()     ,
                syncHeader.getSessionID(),
                msgId                    ,
                target                   ,
                source                   ,
                null  /* response URI */ ,
                false /* Response not required */ ,
                serverCredentials        ,
                null /* meta date */
        );

        return responseHeader;

    }
}

