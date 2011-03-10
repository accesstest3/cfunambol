/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
package com.funambol.foundation.engine.source;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.List;

import com.funambol.common.pim.converter.BaseConverter;
import com.funambol.common.pim.converter.NoteToSIFN;
import com.funambol.common.pim.note.Note;
import com.funambol.common.pim.sif.SIFNParser;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.items.manager.PIMNoteManager;
import com.funambol.foundation.util.SyncSourceUtil;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.beans.BeanInitializationException;

import com.funambol.server.config.Configuration;

/**
 * Sync source for note synchronization
 * @version $Id$
 */
public class PIMNoteSyncSource extends PIMSyncSource {

    // --------------------------------------------------------------- Constants
    private static final String EMPTY_BODY_STRING = "[empty note]";
    
    //--------------------------------------------------------------Private data
    private transient PIMNoteManager manager;
    
    private String rxContentType; // preferred content type as derived from the
                                  // analysis of the DevInf (RXPref)

    //------------------------------------------------------------Public methods    
    @Override
    public void beginSync(SyncContext context) {
        this.manager = new PIMNoteManager(context.getPrincipal().getUsername());
        super.manager = this.manager;
        super.beginSync(context);
        rxContentType = SyncSourceUtil.getNotePreferredType(context);
    }
        
    /**
     * Makes an array of SyncItemKey objects representing all notes IDs.
     *
     * @return a SyncItemKey array
     */
    @Override
    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
        try {
            List idList = manager.getAllItems();
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch (EntityException e) {
            throw new SyncSourceException("Error retrieving all item keys. ", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted note
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws com.funambol.framework.engine.source.SyncSourceException 
     */
    @Override
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp to) throws SyncSourceException {
        saveSyncTiming(since, to);

        try {
            List idList = manager.getDeletedItems(since, to);
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch (EntityException e) {
            throw new SyncSourceException("Error retrieving deleted item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted note
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws com.funambol.framework.engine.source.SyncSourceException 
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp to) throws SyncSourceException {
        saveSyncTiming(since, to);

        try {
            List idList = manager.getUpdatedItems(since, to);
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch (EntityException e) {
            throw new SyncSourceException("Error retrieving updated item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all new note
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws com.funambol.framework.engine.source.SyncSourceException 
     */
    @Override
    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp to) throws SyncSourceException {
        saveSyncTiming(since, to);

        try {
            List idList = manager.getNewItems(since, to);
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch (EntityException e) {
            throw new SyncSourceException("Error retrieving new item keys.", e);
        }
    }

    @Override
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey) throws SyncSourceException {
        String id = null;
        SyncItem syncItem = null;

        id = syncItemKey.getKeyAsString();
        if (log.isTraceEnabled()) {
            log.trace("PIMNoteSyncSource get SyncItem from id " + id);
        }

        try {
            //
            // Retrieves the note, wraps it in sync information and uses it
            // to create a new SyncItem which is the return value of this method
            //
            
            //
            // fix for the empty note
            //
            Note note = manager.getItem(id).getNote();
            
            String contentType;
            if (rxContentType != null) { 
                // Use device capabilities
                contentType = rxContentType;
            } else {
                // Use user-set default
                contentType = getInfo().getPreferredType().getType();
            }

            String content     = note.getTextDescription().getPropertyValueAsString();
            note.getTextDescription().setPropertyValue(
                    fromEmptyBodyToEmptyBodyString(content, contentType));
            
            syncItem = createSyncItem(id,
                    note,
                    SyncItemState.NEW,
                    contentType);

        } catch (EntityException e) {
            throw new SyncSourceException("Error seeking SyncItem with ID: " + id, e);
        }
        return syncItem;
    }

    /**
     * Deletes the item with a given syncItemKey.
     *
     * @param syncItemKey
     * @param timestamp in case of a soft deletion, this will be the registered
     *                  moment of deletion; if a hard deletion is used, this
     *                  field is irrelevant and may also be null
     * @param softDelete it is true if the client requires a soft deletion
     * @throws SyncSourceException
     */
    @Override
    public void removeSyncItem(SyncItemKey syncItemKey,
            Timestamp timestamp,
            boolean softDelete)
            throws SyncSourceException {
        try {

            if (!softDelete) {
                if (log.isTraceEnabled()) {
                    log.trace("PIMNoteSyncSource remove the SyncItem");
                }

                manager.removeItem(syncItemKey.getKeyAsString(), timestamp);

                if (log.isTraceEnabled()) {
                    log.trace("PIMNoteSyncSource removed SyncItem");
                }
            }

        } catch (EntityException e) {
            log.error("Sync source error: could not delete item with key" + syncItemKey, e);
            throw new SyncSourceException("Error deleting item. ", e);
        }
    }

    /**
     * Updates a SyncItem object (representing a note).
     *
     * @param syncItem the SyncItem representing the note
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.UPDATED and the GUID retrieved by the
     *         back-end
     */
    @Override
    public SyncItem updateSyncItem(SyncItem syncItem) throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("updateSyncItem from " + sourceURI);
        }

        Note note = null;
        String content = null;

        try {

            String id = syncItem.getKey().getKeyAsString();
            content = getContentFromSyncItem(syncItem);
            String contentType = syncItem.getType();

            //
            // fix for empty notes
            //
            content = fromEmptyBodyStringToEmptyString(content, contentType);

            note = convert(content, contentType);

            // Modifies the note, wraps it in sync information and uses it to
            // create a new SyncItem which is the return value of this method
            SyncItemImpl newSyncItem = new SyncItemImpl(
                    this, //syncSource
                    manager.updateItem(id, note, syncItem.getTimestamp()), //key
                    null, //mappedKey
                    SyncItemState.UPDATED, //state
                    content.getBytes(), //content
                    null, //format
                    contentType, //type
                    null //timestamp
                    );

            return newSyncItem;

        } catch (Exception e) {
            log.error("SyncSource error updating a new synchronization item", e);
            throw new SyncSourceException("Error updating the item " + syncItem,
                    e);
        }
    }

    /**
     * Adds a SyncItem object (representing a Note).
     *
     * @param syncItem the SyncItem representing the note
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.NEW and the GUID retrieved by the
     *         back-end
     */
    @Override
    public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("PIMNoteSyncSource addSyncItem begin");
        }

        Note note = null;
        String content = null;

        try {

            content = getContentFromSyncItem(syncItem);
            String contentType = syncItem.getType();

            //
            // fix for empty notes
            //
            content = fromEmptyBodyStringToEmptyString(content, contentType);
            
            note = convert(content, contentType);

             // Checking if at least one field used for the twin search in the
            // calendar we are adding contains meaningful data, otherwise the
            // add operation is not allowed since there's no way to prevent
            // duplication.
            if(pushBackUncheckableItems &&
               !manager.isTwinSearchAppliableOn(note)) {
                if(log.isTraceEnabled()) {
                    log.trace("Rejecting add of note with key '"+
                              ((syncItem!=null && syncItem.getKey()!=null)?
                                syncItem.getKey().getKeyAsString():"N/A"
                             )+"' since that note doesn't contain any field " +
                             "usable for twin search.");
                }
                throw new SyncSourceException("Adding a note without any field " +
                                               "usable for twin search set is not allowed.");
            }
            

            Timestamp ts = syncItem.getTimestamp();
            
            // Adds the note, wraps it in sync information and uses it to
            // create a new SyncItem which is the return value of this method
            SyncItemImpl newSyncItem = new SyncItemImpl(
                    this, //syncSource
                    manager.addItem(note, ts), //key
                    null, //mappedKey
                    SyncItemState.NEW, //state
                    content.getBytes(), //content
                    null, //format
                    contentType, //type
                    ts //timestamp
                    );

            return newSyncItem;

        } catch(SyncSourceException e) {
            throw e;
        } catch (Exception e) {
            log.error("SyncSource error adding a new synchronization item", e);
            throw new SyncSourceException("Error adding the item " + syncItem,
                    e);
        }
    }

    @Override
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem) throws SyncSourceException {

        try {
            String content = getContentFromSyncItem(syncItem);
            String contentType = syncItem.getType();

            //
            // fix for empty notes
            //
            content = fromEmptyBodyStringToEmptyString(content, contentType);
            syncItem.setContent(content.getBytes());
            
            List idList = manager.getTwins(convert(syncItem));
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch (EntityException e) {
            throw new SyncSourceException("Error retrieving twin item keys.", e);
        }
    }

    public boolean mergeSyncItems(SyncItemKey syncItemKey, SyncItem syncItem) throws SyncSourceException {

        try {

            String content = getContentFromSyncItem(syncItem);
            String contentType = syncItem.getType();

            //
            // fix for empty notes
            //
            content = fromEmptyBodyStringToEmptyString(content, contentType);
            syncItem.setContent(content.getBytes());

            boolean clientUpdateRequired =
                manager.mergeItems(syncItemKey.getKeyAsString(),
                                   convert(syncItem)           ,
                                   syncItem.getTimestamp()
                );

            if(clientUpdateRequired) {
                syncItem = getSyncItemFromId(syncItemKey);
            }
            return clientUpdateRequired;

        } catch(EntityException e) {
            log.error("SyncSource error: a merge did not succeed.", e);
            throw new SyncSourceException("Error merging SyncItem with key '"
                                        + syncItemKey
                                        + "' with SyncItem '"
                                        + syncItem
                                        + "'", e);
        }        
    }

    public void init() throws BeanInitializationException {

    }

    /**
     * Converts a SyncItem to a Note object, provided it represents a note
     * item in SIF-N or plain text format.
     *
     * @param syncItem
     * @throws EntityException if the contentType is wrong or the conversion
     *                         attempt doesn't succeed.
     * @return a Note object
     */
    private Note convert(SyncItem syncItem) throws EntityException {
        return convert(getContentFromSyncItem(syncItem), syncItem.getType());
    }

    /**
     * Converts the content in a Note
     *
     * @param content as a String
     * @param contentType
     * @throws EntityException if the contentType is wrong or the conversion
     *                         attempt doesn't succeed.
     * @return a Note  object
     */
    private Note convert(String content, String contentType)
            throws EntityException {
        // Finds out which target type is required
        for (int i = 0; i < TYPE.length; i++) {
            if (contentType.equals(TYPE[i])) {

                // Uses the proper converter method
                switch (i) {
                    case PLAINTEXT:
                        return plainText2Note(content, contentType);
                    case SIFN:
                        return sif2Note(content, contentType);
                    default:
                        throw new EntityException("Can't make a note " + "out of a " + TYPE[i] + "!");
                }
            }
        }
        throw new EntityException("Content type unknown: " + contentType);
    }

    /**
     * Converts a Note back to a streamable (plain/text, sif) format.
     *
     * @param note
     * @param contentType
     * @throws EntityException if the contentType is wrong or the conversion
     *                         attempt doesn't succeed.
     * @return the result in the required format
     */
    private String convert(Note note, String contentType)
            throws EntityException {

        // Finds out which target type is required
        for (int i = 0; i < TYPE.length; i++) {
            if (contentType.equals(TYPE[i])) {
                // Uses the proper converter method
                switch (i) {
                    case PLAINTEXT:                        
                        return note2PlainText(note);
                    case SIFN:
                        return note2sif(note);
                    default:
                        throw new EntityException("Can't make a " + TYPE[i] + "out of a note!");
                }
            }
        }
        throw new EntityException("Content type unknown: " + contentType);
    }

    /**
     * Create a new SyncItem from a Note. The target contentType and status
     * are passed as arguments.
     *
     * @param note the Note object representing the input information
     * @param contentType chosen among the TYPE array's elements
     * @param status
     * @throws EntityException if the content type is wrong or any problem
     *                         occurs while creating a new SyncItem
     * @return a newly created SyncItem object
     */
    private SyncItem createSyncItem(String id, Note note, char status, String contentType)
            throws EntityException {

        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder(100);
            sb.append("PIMNoteSyncSource - creating item with:").append("\n> id: ").append(id).append("\n> status: ").append(status).append("\n> content-type: ").append(contentType);
            log.trace(sb.toString());
        }

        SyncItem syncItem = null;
        String stream = convert(note, contentType);

        try {
            syncItem = new SyncItemImpl(this, id, status);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        syncItem.setType(contentType);
        syncItem.setContent(stream.getBytes());

        if (log.isTraceEnabled()) {
            log.trace("PIMNoteSyncSource created SyncItem");
        }

        return syncItem;
    }

    /**
     * 
     * @param xml 
     * @param sifType 
     * @throws com.funambol.Exchange.exception.EntityException 
     * @return 
     */
    private Note sif2Note(String xml, String sifType)
            throws EntityException {

        //
        // If funambol is not in the debug mode is not possible to print the
        // note's content because it contains sensitive data.
        //
        if (Configuration.getConfiguration().isDebugMode()) {
            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder(xml.length() + 60);
                sb.append("Converting: ").append(sifType).append(" => Note ").append("\nINPUT = {").append(xml).append('}');
                log.trace(sb.toString());
            }
        }
        
        ByteArrayInputStream buffer = null;
        Note note = null;
        try {
            note = new Note();
            buffer = new ByteArrayInputStream(xml.getBytes());
            if ((xml.getBytes()).length > 0) {
                SIFNParser parser = new SIFNParser(buffer);
                note = parser.parse();
            }

            //unused fields for sif-n
            note.getDate().setPropertyValue(null);
            note.getSubject().setPropertyValue(null);
        } catch (Exception e) {
            throw new EntityException("Error converting " + sifType + " to Note. ", e);
        }

        if (log.isTraceEnabled()) {
            log.trace("Conversion done.");
        }

        return note;
    }

    /**
     * 
     * @param content
     * @param contentType
     * @return
     */
    private Note plainText2Note(String content, String contentType) {
        Note note = new Note();
        if (log.isTraceEnabled()) {
            log.trace("plainText2Note");
        }

        note.getTextDescription().setPropertyValue(content);


        return note;
    }

    /**
     * 
     * @param note
     * @param contentType
     * @return
     */
    private String note2PlainText(Note note) {
        if (log.isTraceEnabled()) {
            log.trace("note2PlainText");
        }
        if (note.getTextDescription() != null) {
            return note.getTextDescription().getPropertyValueAsString();
        }
        return "";
    }

    /**
     * 
     * @param note
     * @param sifType
     * @return
     * @throws com.funambol.exchange.exception.EntityException
     */
    private String note2sif(Note note)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Converting note2sif ");
        }

        String xml = null;
        BaseConverter c2xml;



        try {

            //unused fields for sif-n
            note.getDate().setPropertyValue(null);
            
            //initializing subject with 1st row os notes description
            //because it is needed on WM 
            String noteDescription = note.getTextDescription().getPropertyValueAsString();
            if(noteDescription != null) {
                
                //
                // Some synchronization clients use "\r\n" instead of "\n" as
                // line separator.
                //
                int index = noteDescription.indexOf("\r\n");
                if (index == -1){
                    index = noteDescription.indexOf("\n");
                }
                
                if (index == -1){
                    note.getSubject().setPropertyValue(noteDescription);
                } else {
                    note.getSubject().setPropertyValue(noteDescription.substring(0, index));
                }
            } else {
                note.getSubject().setPropertyValue("");
            }
            
            
            c2xml = new NoteToSIFN(deviceTimeZone, deviceCharset);

            if (log.isTraceEnabled()) {
                log.trace("converting...");
            }

            xml = c2xml.convert(note);

            if (log.isTraceEnabled()) {
                log.trace("converted...");
            }

            //
            // If funambol is not in the debug mode is not possible to print the
            // contact's content because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("OUTPUT = {").append(xml).append("}. Conversion done.");
                    log.trace(sb.toString());
                }
            }

        } catch (Exception e) {
            throw new EntityException("Error converting note to " + SIFN, e);
        }
        return xml;
    }
    
    /**
     * If note comes from a plain text note device and body is the EMPTY_BODY_STRING,
     * then body of the note has to be replaced with the empty string.
     */
    private String fromEmptyBodyStringToEmptyString(String content, String contentType) {
        
        if (TYPE[PLAINTEXT].equals(contentType)) {
            //
            // fix for the empty body
            // EMPTY_BODY_STRING --> empty string
            //
            if (EMPTY_BODY_STRING.equals(content)) {
                return "";
            }
        }        
        return content;
    }            
    
    /**
     * Many plain-text note phones do not accept notes with an empty body. So a 
     * specific string (EMPTY_BODY_STRING) is sent to the device instead of the
     * empty string.
     * If body of a note coming from a plain-text note phone is formed exactly by
     * the EMPTY_BODY_STRING then that string is replaced by the 
     * 
     * @param content
     * @param contentType
     * @return
     */
    private String fromEmptyBodyToEmptyBodyString(String content, String contentType) {
        
        if (TYPE[PLAINTEXT].equals(contentType)) {
            //
            // fix for the empty body
            // EMPTY_BODY_STRING <-- empty string
            //
            if (content.length() == 0) {
                return EMPTY_BODY_STRING;
            }
        }
        return content;
    }

}
