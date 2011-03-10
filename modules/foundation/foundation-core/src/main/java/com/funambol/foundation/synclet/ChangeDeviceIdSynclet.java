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
package com.funambol.foundation.synclet;

import java.util.*;

import com.funambol.framework.core.*;
import com.funambol.framework.engine.pipeline.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.CoreUtil;

/**
 * This class changes the input message client deviceId with a fixed deviceId.
 * This is done in order to allow any phone to sync without the need of adding
 * a new device/user/principal in the SyncAdmin. This is for the sake of easy
 * of use for beginners. To disable this behaviuor, just remove the synclet
 * from the pipeline.
 * The original device id will is replaced in the outgoing message so that the
 * phone device will not notice the change.
 *
 * <p>The change is done only if the sync sources to sync are in the given
 * <code>syncSourcesToProcess</code> list and the client device id is not in the
 * <code>devicesNotToProcess</code> list.
 *
 * This synclet uses the following context properties:
 * <ul>
 *  <li>funambol.foundation.changedevice.DEVICEID_ORIG - to store the original device id</li>
 * </ul>
 *
 * Note that because the Alert commands containing the syncsource to sync are
 * given only in the first message, we have to remember if we are changing the
 * deviceid or not for all the messages in the session. For this, we just check
 * if clientDeviceId is already set. If not, we check for the alert; if yes, it
 * means that we have already determined that the device id needs to be replaced,
 * therefore we replace it without further checks.
 *
 * <p>If the DEVICEID_ORIG in the processingContext is null, no change is performed.
 *
 * @version $Id: ChangeDeviceIdSynclet.java,v 1.1.1.1 2008-03-20 21:38:42 stefano_fornari Exp $
 */
public class ChangeDeviceIdSynclet
implements InputMessageProcessor, OutputMessageProcessor {

    // --------------------------------------------------------------- Constants
    private static final String PROPERTY_ORIG_ID =
        "funambol.foundation.changedevice.DEVICEID_ORIG";

    private static final String CLIENT_DEVICEID = "syncml-phone";

    private static final String SYNCLET_NAME = ChangeDeviceIdSynclet.class.getName();

    // ------------------------------------------------------------ Private data
    private static final FunambolLogger log =
            FunambolLoggerFactory.getLogger("engine.pipeline");

    /**
     * Contain the original client device Id
     */
    private String clientDeviceId = null;

    /**
     * Contains the uri of the sync sources to process.
     * If in the message to process, there are a syncsource specified in the list,
     * then the message is processed
     */
    private ArrayList syncSourcesToProcess = null;

    /**
     * Contains the list of the known devices of which the ID doesn't have
     * to be changed.
     */
    private ArrayList devicesNotToProcess    = null;

    // ---------------------------------------------------------- Public methods

    /**
     * Process input message and set MessageProcessingContext property.
     *
     * @param processingContext the message processing context
     * @param message the message to be processed
     * @throws Sync4jException
     */
    public void preProcessMessage(MessageProcessingContext processingContext,
                                  SyncML message) throws Sync4jException {

        if (log.isTraceEnabled()) {
            log.trace(SYNCLET_NAME + ".preProcessMessage(...)");
        }

        boolean processMessage = true;

        if (clientDeviceId == null) {
            processMessage = checkSourceUriToSync(message);
        }

        if (log.isTraceEnabled()) {
            log.trace(SYNCLET_NAME + " - device id replacement " + (processMessage ? "" : "not ") + "required");
        }

        if (processMessage) {
            if (clientDeviceId == null) {
                clientDeviceId = message.getSyncHdr().getSource().getLocURI();
            }

            if (log.isTraceEnabled()) {
                log.trace(SYNCLET_NAME + " - original clientDeviceId '" + clientDeviceId + "'");
            }

            if (!devicesNotToProcess.contains(clientDeviceId)) {
                Source source = message.getSyncHdr().getSource();
                source.setLocURI(CLIENT_DEVICEID);
                processingContext.setRequestProperty(PROPERTY_ORIG_ID, clientDeviceId);
            }
        }
    }


    /**
     * Process and manipulate the output message.
     *
     * @param processingContext the message processing context
     * @param message the message to be processed
     * @throws Sync4jException
     */
    public void postProcessMessage(MessageProcessingContext processingContext,
                                   SyncML message) throws Sync4jException {
        if (log.isTraceEnabled()) {
            log.trace(SYNCLET_NAME + ".postProcessMessage(...)");
        }

        if (clientDeviceId == null) {
            clientDeviceId = (String)processingContext.getRequestProperty(PROPERTY_ORIG_ID);
        }

        //
        // If still null, no processing is required
        //
         if (clientDeviceId == null) {
            if (log.isTraceEnabled()) {
                log.trace(SYNCLET_NAME + " - processing not required");
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace(SYNCLET_NAME + " - setting device id to '" + clientDeviceId + "'");
            }

            Target target = message.getSyncHdr().getTarget();
            target.setLocURI(clientDeviceId);

            //
            // We have also to change the SourceRef in the SyncHdr's status
            //
            SyncBody body = message.getSyncBody();
            AbstractCommand[] allServerCommands =
                 (AbstractCommand[])body.getCommands().toArray(new AbstractCommand[0]);

            ArrayList listStatus =
                CoreUtil.filterCommands(allServerCommands,
                                            Status.class);

            if (listStatus != null && listStatus.size() != 0) {

                Status status = (Status) listStatus.get(0);
                List sourceRefList = status.getSourceRef();
                if (sourceRefList != null && sourceRefList.size() != 0) {
                    SourceRef sourceRef =
                            (SourceRef)sourceRefList.get(0);
                    sourceRef.setValue(clientDeviceId);
                }
            }
        }

    }

    /**
     * Set the syncSourcesToProcess
     * @param list the syncSourcesToProcess
     */
    public void setSyncSourcesToProcess(ArrayList list) {
        this.syncSourcesToProcess = list;
    }

    /**
     * Set the devicesNotToProcess
     *
     * @param list the devicesNotToProcess
     */
    public void setDevicesNotToProcess(ArrayList list) {
        this.devicesNotToProcess = list;
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Checks if the sync sources that are in the syncml message are present
     * in the list <code>syncSourcesToProcess</code>
     *
     * @param message the message to process
     * @return boolean
     */
    private boolean checkSourceUriToSync(SyncML message) {

        if (syncSourcesToProcess == null) {
            return false;
        }

        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allClientCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        ArrayList alertList = CoreUtil.filterCommands(allClientCommands,
            Alert.class);

        Iterator itAlertList = alertList.iterator();

        Alert alert = null;
        ArrayList items = null;
        Iterator itItem = null;
        Item item = null;
        Target target = null;
        String sourceUri = null;

        while (itAlertList.hasNext()) {
            alert = (Alert)itAlertList.next();
            items = alert.getItems();
            itItem = items.iterator();
            while (itItem.hasNext()) {

                item = (Item)itItem.next();
                target = item.getTarget();
                if (target != null) {
                    sourceUri = target.getLocURI();

                    if (syncSourcesToProcess.indexOf(sourceUri) != -1) {
                        // the source uri is present in the list of the
                        // sync source to process
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
