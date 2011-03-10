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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.funambol.framework.core.*;
import com.funambol.framework.core.Map;
import com.funambol.framework.engine.pipeline.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.CoreUtil;

/**
 * This class changes the input message client source URI with a correspondent
 * server source URI
 *
 * This is done in order to allow any client to sync with different source URI
 * using the same SyncSource. For example into phone is possible to write
 * ./contact or only contact to synchorized with the same contact SyncSource.
 *
 * If specified an headerPatterns map, the processing is performed only if the headers
 * specified in the MessageProcessinContext match with at least one entry of the
 * headerPatterns that contains couples of HTTPHeader-pattern
 *
 * @version $Id: ChangeSourceUriSynclet.java,v 1.1.1.1 2008-03-20 21:38:42 stefano_fornari Exp $
 */
public class ChangeSourceUriSynclet
implements InputMessageProcessor, OutputMessageProcessor {

    // --------------------------------------------------------------- Constants
    private static final String SYNCLET_NAME =
        ChangeSourceUriSynclet.class.getName();
    private static final String PROPERTY_SOURCENAME_CHANGED =
        "funambol.foundation.changesourcename.SOURCENAME_CHANGED";

    // -------------------------------------------------------------- Properties

    /** This map contains couples HTTPheader-pattern */
    private java.util.Map headerPatterns = null;

    public java.util.Map getHeaderPatterns() {
        return headerPatterns;
    }

    public void setHeaderPatterns(java.util.Map headerPatterns) {
        this.headerPatterns = headerPatterns;
    }

    // ------------------------------------------------------------ Private data
    private static final FunambolLogger log =
            FunambolLoggerFactory.getLogger("engine.pipeline");

    private HashMap sourceNameMapping = null;

    // ---------------------------------------------------------- Public methods

    /**
     * Process input message and set MessageProcessingContext property.
     *
     * @param processingContext the message processing context
     * @param message the message to be processed
     * @throws Sync4jException
     */
    public void preProcessMessage(MessageProcessingContext processingContext,
                                  SyncML message                            )
    throws Sync4jException {

        if (!isDeviceToProcess(processingContext)) {
            return;
        }

        if (log.isTraceEnabled()) {
            log.trace(SYNCLET_NAME + ".preProcessMessage(...)");
        }

        HashMap sourceReplacedMap =
            (HashMap)processingContext.getSessionProperty(
                                                   PROPERTY_SOURCENAME_CHANGED);

        if (sourceReplacedMap == null) {
            sourceReplacedMap = new HashMap();
            processingContext.setSessionProperty(PROPERTY_SOURCENAME_CHANGED,
                                                 sourceReplacedMap          );
        }

        //
        // Store and change the source uri into commands
        //
        manageInputAlert (message, sourceReplacedMap);
        manageInputStatus(message, sourceReplacedMap);
        manageInputSync  (message, sourceReplacedMap);
        manageInputMap   (message, sourceReplacedMap);

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

        if (!isDeviceToProcess(processingContext)) {
            return;
        }

        if (log.isTraceEnabled()) {
            log.trace(SYNCLET_NAME + ".postProcessMessage(...)");
        }

        HashMap sourceReplacedMap =
            (HashMap)processingContext.getSessionProperty(
                                                   PROPERTY_SOURCENAME_CHANGED);

        if (sourceReplacedMap == null) {
            return;
        }
        if (!sourceReplacedMap.isEmpty()) {
            //
            // Replace original source uri into commands
            //
            manageOutputStatus (message, sourceReplacedMap);
            manageOutputResults(message, sourceReplacedMap);
            manageOutputAlert  (message, sourceReplacedMap);
            manageOutputSync   (message, sourceReplacedMap);
       }
    }

    /**
     * Set the hashmap with sources name's to change
     *
     * @param map
     */
    public void setSourceNameMapping(HashMap map) {
        this.sourceNameMapping = map;
    }

    /**
     * Get the hashmap with sources name's to change
     *
     * @return the map
     */
    public HashMap getSourceNameMapping() {
        return this.sourceNameMapping;
    }
    // --------------------------------------------------------- Private Methods

    /**
     * Store and change Target URI into Alert commands
     *
     * @param message the client message
     * @param sourceReplacedMap the HashMap in which store uri to replace
     */
    private void manageInputAlert(SyncML message, HashMap sourceReplacedMap) {

        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allClientCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        ArrayList alertList =
            CoreUtil.filterCommands(allClientCommands, Alert.class);

        Iterator itAlertList = alertList.iterator();

        Alert alert = null;
        ArrayList items = null;
        Iterator itItem = null;
        Item item = null;
        Target target = null;
        String targetUri = null;

        while (itAlertList.hasNext()) {
            alert = (Alert)itAlertList.next();
            items = alert.getItems();
            itItem = items.iterator();
            while (itItem.hasNext()) {

                item = (Item)itItem.next();
                target = item.getTarget();
                if (target != null) {
                    targetUri = target.getLocURI();

                    if (sourceNameMapping.containsKey(targetUri)) {
                        String targetNew =
                            (String)sourceNameMapping.get(targetUri);

                        target.setLocURI(targetNew);

                        sourceReplacedMap.put(targetNew, targetUri);
                    }
                }
            }
        }
    }

    /**
     * Store and change SourceRef into Status commmads
     *
     * @param message the client message
     * @param sourceReplacedMap the HashMap in which store uri to replace
     */
    private void manageInputStatus(SyncML message, HashMap sourceReplacedMap) {
        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allClientCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        ArrayList statusList =
            CoreUtil.filterCommands(allClientCommands, Status.class);

        Iterator itStatusList = statusList.iterator();

        Status status = null;
        while (itStatusList.hasNext()) {
            status = (Status)itStatusList.next();
            SourceRef[] srefs =
                (SourceRef[])status.getSourceRef().toArray(new SourceRef[0]);
            int s = srefs.length;
            for(int i=0;i<s;i++) {
                SourceRef sr = (SourceRef)srefs[i];
                String sourceRef = sr.getValue();

                if (sourceNameMapping.containsKey(sourceRef)) {
                    String sourceRefNew =
                        (String)sourceNameMapping.get(sr.getValue());

                    sr.setValue(sourceRefNew);

                    sourceReplacedMap.put(sourceRefNew, sourceRef);
                }
            }
        }
    }

    /**
     * Store and change Target into Sync commands
     *
     * @param message the client message
     * @param sourceReplacedMap the HashMap in which store uri to replace
     */
    private void manageInputSync(SyncML message, HashMap sourceReplacedMap) {

        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allClientCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        ArrayList syncList =
            CoreUtil.filterCommands(allClientCommands, Sync.class);

        Iterator itList = syncList.iterator();

        Sync sync = null;
        String targetUri = null;

        while (itList.hasNext()) {
            sync = (Sync)itList.next();
            targetUri = sync.getTarget().getLocURI();

            if (sourceNameMapping.containsKey(targetUri)) {
                String targetNew = (String)sourceNameMapping.get(targetUri);

                sync.getTarget().setLocURI(targetNew);

                sourceReplacedMap.put(targetNew, targetUri);
            }
        }
    }

    /**
     * Store and change Target into Map commands
     *
     * @param message the client message
     * @param sourceReplacedMap the HashMap in which store uri to replace
     */
    private void manageInputMap(SyncML message, HashMap sourceReplacedMap) {

        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allClientCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        ArrayList mapList =
            CoreUtil.filterCommands(allClientCommands, Map.class);

        Iterator itList = mapList.iterator();

        Map map = null;
        String targetUri = null;

        while (itList.hasNext()) {
            map = (Map)itList.next();
            targetUri = map.getTarget().getLocURI();

            if (sourceNameMapping.containsKey(targetUri)) {
                String targetNew = (String)sourceNameMapping.get(targetUri);

                map.getTarget().setLocURI(targetNew);

                sourceReplacedMap.put(targetNew, targetUri);
            }
        }
    }

    /**
     * Replace TargetRef into Status commands
     *
     * @param message the client message
     * @param sourceReplacedMap the HashMap with the uri to replace
     */
    private void manageOutputStatus(SyncML message, HashMap sourceReplacedMap) {
        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allServerCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        ArrayList statusList =
            CoreUtil.filterCommands(allServerCommands, Status.class);

        Iterator itStatusList = statusList.iterator();

        Status status = null;
        ArrayList items = null;
        Iterator itItem = null;
        Item item = null;
        Target target = null;
        String targetUri = null;

        while (itStatusList.hasNext()) {
            status = (Status)itStatusList.next();

            if (status.getTargetRef() == null) {
                continue;
            }
            TargetRef[] trefs =
                (TargetRef[])status.getTargetRef().toArray(new TargetRef[0]);
            int s = trefs.length;
            for(int i=0;i<s;i++) {
                TargetRef tr = (TargetRef)trefs[i];
                if (sourceReplacedMap.containsKey(tr.getValue())) {
                    tr.setValue((String)sourceReplacedMap.get(tr.getValue()));
                }
            }
            
            //-------------------
            
            items = status.getItems();
            itItem = items.iterator();
            while (itItem.hasNext()) {

                item = (Item)itItem.next();
                target = item.getTarget();
                if (target != null) {
                    targetUri = target.getLocURI();

                    if (sourceReplacedMap.containsKey(targetUri)) {
                        target.setLocURI((String)sourceReplacedMap.get(targetUri));
                    }
                }
            }
            
            //-------------------
        }
    }

    /**
     * Replace Source into Alert commands
     *
     * @param message the client message
     * @param sourceReplacedMap the HashMap with the uri to replace
     */
    private void manageOutputAlert(SyncML message, HashMap sourceReplacedMap) {
        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allServerCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        ArrayList alertList =
            CoreUtil.filterCommands(allServerCommands, Alert.class);

        Iterator itAlertList = alertList.iterator();

        Alert alert = null;
        ArrayList items = null;
        Iterator itItem = null;
        Item item = null;
        Source source = null;
        String sourceUri = null;

        while (itAlertList.hasNext()) {
            alert = (Alert)itAlertList.next();
            items = alert.getItems();
            itItem = items.iterator();
            while (itItem.hasNext()) {

                item = (Item)itItem.next();
                source = item.getSource();
                if (source != null) {
                    sourceUri = source.getLocURI();

                    if (sourceReplacedMap.containsKey(sourceUri)) {
                        //
                        // Here we have to create a new Source object because
                        // the Alert can contain the same Source used in the
                        // engine/sessionHandler to handle the Database.
                        // If here we change the source object of the alert we
                        // change also the source of the database because it's
                        // the same object!!.
                        //
                        //
                        Source newSource = new Source(
                            (String)sourceReplacedMap.get(sourceUri),
                            source.getLocName()
                        );
                        item.setSource(newSource);
                    }
                }
            }
        }
    }

    /**
     * Replace SourceRef into Results command
     *
     * @param message the client message
     * @param sourceReplacedMap the HashMap with the uri to replace
     */
    private void manageOutputResults(SyncML message, HashMap sourceReplacedMap) {
        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allServerCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        List list =
            CoreUtil.filterCommands(allServerCommands, Results.class);

        if (list.isEmpty()) {
            return;
        }

        Results results = (Results)list.get(0);

        Item[] items = (Item[])results.getItems().toArray(new Item[0]);

        if (items != null && items.length > 0) {
            if (items[0] instanceof DevInfItem) {
                DevInfItem item = (DevInfItem) items[0];
                ArrayList dss = item.getDevInfData().getDevInf().getDataStores();
                int s = dss.size();
                for (int i = 0; i < s; i++) {
                    DataStore ds = (DataStore) dss.get(i);
                    String sourceRef = ds.getSourceRef().getValue();
                    if (sourceReplacedMap.containsKey(sourceRef)) {
                        ds.getSourceRef().setValue((String) sourceReplacedMap.
                                get(sourceRef));
                    }
                }
            }
        }
    }


    /**
     * Replace Source into Sync commands
     *
     * @param message the client message
     * @param sourceReplacedMap the HashMap with the uri to replace
     */
    private void manageOutputSync(SyncML message, HashMap sourceReplacedMap) {
        SyncBody syncBody = message.getSyncBody();

        AbstractCommand[] allServerCommands =
            (AbstractCommand[])syncBody.getCommands().toArray(
            new AbstractCommand[0]);

        ArrayList syncList =
            CoreUtil.filterCommands(allServerCommands, Sync.class);

        Iterator itSyncList = syncList.iterator();

        Sync sync        = null;
        Source source    = null;
        String sourceUri = null;

        while (itSyncList.hasNext()) {
            sync = (Sync) itSyncList.next();

            source = sync.getSource();

            if (source != null) {
                sourceUri = source.getLocURI();

                if (sourceReplacedMap.containsKey(sourceUri)) {
                    //
                    // Here we have to create a new Source object because
                    // the Sync can contain the same Source used in the
                    // engine/sessionHandler to handle the Database.
                    // If here we change the source object of the sync commands we
                    // change also the source of the database because it's
                    // the same object!!.
                    //
                    //
                    Source newSource = new Source(
                            (String) sourceReplacedMap.get(sourceUri),
                            source.getLocName()
                                       );
                    sync.setSource(newSource);
                }
            }
        }
    }

    /**
     * Searches a match for the configured <code>headerPatterns</code> with the HTTP
     * headers specified in the context.
     *
     * @param context message processing context
     *
     * @return true if the device must be proced by this synclet, false otherwise
     */
    private boolean isDeviceToProcess(MessageProcessingContext context) {
        if (headerPatterns == null || headerPatterns.size() == 0) {
            return true;
        }
        Iterator i = headerPatterns.keySet().iterator();
        while (i.hasNext()) {
            String header  = (String)i.next();
            String pattern = (String)headerPatterns.get(header);

            if (isDeviceToProcess(context, header, pattern)) {
                return  true;
            }
        }
        return false;
    }

    /**
     * Searches a match for the configured <code>pattern</code> with the HTTP
     * header specified by <code>header</code>.
     * If header or pattern is empty, isDeviceToProcess() returns always true.
     * If the client does not provide the specified header, isDeviceToProcess()
     * returns false.
     *
     * @param context message processing context
     *
     * @param header the header to check
     * @param pattern the pattern
     * @return true if the device must be proced by this synclet, false otherwise
     */
    private boolean isDeviceToProcess(MessageProcessingContext context,
                                      String header,
                                      String pattern) {

        if ((pattern == null || pattern.trim().length() == 0) ||
            (header  == null || header.trim().length()  == 0)) {
            return true;
        }

        java.util.Map headers =
            (java.util.Map)context.getRequestProperty(MessageProcessingContext.PROPERTY_REQUEST_HEADERS);

        //
        // Search for the requested header value
        //
        String value = null;
        Iterator i = headers.keySet().iterator();
        while (i.hasNext()) {
            String h = (String)i.next();
            if (header.equalsIgnoreCase(h)) {
                value = (String)headers.get(h);
                break;
            }
        }

        if ((value == null) || (value.trim().length() == 0)) {
            //
            // Header not found or not specified
            //
            return false;
        }

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(value);

        return m.find();
    }

}
