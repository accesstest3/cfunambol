/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
import java.util.Map;

import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.converter.ContactToVcard;
import com.funambol.common.pim.converter.ContactToSIFC;
import com.funambol.common.pim.sif.SIFCParser;
import com.funambol.common.pim.vcard.VcardParser;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.items.manager.PIMContactManager;
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
 *
 * @version $Id: PIMContactSyncSource.java,v 1.1.1.1 2008-03-20 21:38:34 stefano_fornari Exp $
 */
public class PIMContactSyncSource extends PIMSyncSource {

    //--------------------------------------------------------------Private data

    private transient PIMContactManager manager;

    /** Must the contact's photo be sent to the client ? */
    private boolean syncPhoto = false;
    
    private String rxContentType; // preferred content type as derived from the
                                  // analysis of the DevInf (RXPref)

    //------------------------------------------------------------Public methods

    @Override
    public void beginSync(SyncContext context) {
        this.manager = new PIMContactManager(context.getPrincipal().getUsername());
        super.manager = this.manager;
        super.beginSync(context);
        rxContentType =
            SyncSourceUtil.getContactPreferredType(context);
        
        Map<String, String> parameters = getSourceQueryParameters(context.getSourceQuery());
        if (parameters != null) {
            if ("true".equalsIgnoreCase(parameters.get("photo"))) {
                syncPhoto = true;
            }
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing the ID(s) of the
     * twin(s) of a given contact.
     *
     * @param syncItem the SyncItem representing the contact whose twin(s) are
     *                 looked for
     * @throws SyncSourceException
     * @return possibly, just one or no key should be in the array, but it can't
     *         be ruled out a priori that several keys get returned by this
     *         method
     */
    @Override
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException {

        try {
            List idList = manager.getTwins(convert(syncItem));
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving twin item keys.", e);
        }
    }

    /**
     * Adds a SyncItem object (representing a contact).
     *
     * @param syncItem the SyncItem representing the contact
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.NEW and the GUID retrieved by the
     *         back-end
     */
    @Override
    public SyncItem addSyncItem(SyncItem syncItem)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("PIMContactSyncSource addSyncItem begin");
        }

        Contact c = null;
        String content = null;

        try {

            content = getContentFromSyncItem(syncItem);
            String contentType = syncItem.getType();

            c = convert(content, contentType);

            // Checking if at least one field used for the twin search in the
            // contact we are adding contains meaningful data, otherwise the
            // add operation is not allowed since there's no way to prevent
            // duplication.
            if(pushBackUncheckableItems &&
               !manager.isTwinSearchAppliableOn(c)) {
                if(log.isTraceEnabled()) {
                    log.trace("Rejecting add of contact with key '"+
                              ((syncItem!=null && syncItem.getKey()!=null)?
                                syncItem.getKey().getKeyAsString():"N/A"
                             )+"' since the contact doesn't contain any field " +
                             "usable for the twin search.");
                }
                throw new SyncSourceException("Adding a contact without any field " +
                                               "usable for twin search set is not allowed.");
            }

            Timestamp ts = syncItem.getTimestamp();

            // Adds the contact, wraps it in sync information and uses it to
            // create a new SyncItem which is the return value of this method
            SyncItemImpl newSyncItem = new SyncItemImpl(
                this                  , //syncSource
                manager.addItem(c, ts), //key
                null                  , //mappedKey
                SyncItemState.NEW     , //state
                content.getBytes()    , //content
                null                  , //format
                contentType           , //type
                ts                      //timestamp
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

    /**
     * Updates a SyncItem object (representing a contact).
     *
     * @param syncItem the SyncItem representing the contact
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.UPDATED and the GUID retrieved by the
     *         back-end
     */
    @Override
    public SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("updateSyncItem from " + sourceURI);
        }

        Contact c = null;
        String content = null;

        try {

            String id = syncItem.getKey().getKeyAsString();
            content = getContentFromSyncItem(syncItem);
            String contentType = syncItem.getType();

            c = convert(content, contentType);

            // Modifies the contact, wraps it in sync information and uses it to
            // create a new SyncItem which is the return value of this method
            SyncItemImpl newSyncItem = new SyncItemImpl(
                this                                              , //syncSource
                manager.updateItem(id, c, syncItem.getTimestamp()), //key
                null                                              , //mappedKey
                SyncItemState.UPDATED                             , //state
                content.getBytes()                                , //content
                null                                              , //format
                contentType                                       , //type
                null                                                //timestamp
            );

            return newSyncItem;

        } catch (Exception e) {
            log.error("SyncSource error updating a new synchronization item", e);
            throw new SyncSourceException("Error updating the item " + syncItem,
                                          e);
        }
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
                    log.trace("PIMContactSyncSource remove the SyncItem");
                }

                manager.removeItem(syncItemKey.getKeyAsString(), timestamp);

                if (log.isTraceEnabled()) {
                    log.trace("PIMContactSyncSource removed SyncItem");
                }
            }

        } catch (EntityException e) {
            log.error("Sync source error: could not delete item with key"
                    + syncItemKey, e);
            throw new SyncSourceException("Error deleting item. ", e);
        }

    }

    @Override
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {
        String id         = null;
        SyncItem syncItem = null;

        id = syncItemKey.getKeyAsString();
        if (log.isTraceEnabled()) {
            log.trace("PIMContactSyncSource get SyncItem from id " + id);
        }

        try {
            //
            // Retrieves the contact (with its photo), wraps it in sync information and uses it
            // to create a new SyncItem which is the return value of this method
            //
            syncItem = createSyncItem(id,
                                      manager.getItem(id, true).getContact(),
                                      SyncItemState.NEW);

        } catch (EntityException e) {
            throw new SyncSourceException("Error seeking SyncItem with ID: "
                                         + id, e);
        }
        return syncItem;

    }

    public boolean mergeSyncItems(SyncItemKey syncItemKey, SyncItem syncItem)
    throws SyncSourceException {
        try {

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
     * Makes an array of SyncItemKey objects representing all new contact
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     */
    @Override
    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        saveSyncTiming(since, to);

        try {
            List idList = manager.getNewItems(since, to);
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving new item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted contact
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        saveSyncTiming(since, to);

        try {
            List idList = manager.getUpdatedItems(since, to);
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving updated item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted contact
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     */
    @Override
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        saveSyncTiming(since, to);

        try {
            List idList = manager.getDeletedItems(since, to);
            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String) idList.get(i));
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving deleted item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all contact IDs.
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
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving all item keys. ", e);
        }

    }

    //---------------------------------------------------------- Private methods

    private Contact sif2Contact(String sifc) throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Converting: SIF-C => Contact ");
        }

        //
        // If funambol is not in the debug mode is not possible to print the
        // contact's content because it contains sensitive data.
        //
        if (Configuration.getConfiguration().isDebugMode()) {
            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder(sifc.length() + 11);
                sb.append("\nINPUT = {").append(sifc).append('}');
                log.trace(sb.toString());
            }
        }

        ByteArrayInputStream buffer = null;
        SIFCParser parser = null;
        Contact contact = null;
        try {
            contact = new Contact();
            buffer = new ByteArrayInputStream(sifc.getBytes());
            if ((sifc.getBytes()).length > 0) {
                parser = new SIFCParser(buffer);
                contact = (Contact) parser.parse();
            } else {
                throw new EntityException("No data");
            }
        } catch (EntityException e){
            throw e;
        } catch (Exception e){
            throw new EntityException("Error converting SIF-C to Contact. ", e);
        }

        if (log.isTraceEnabled()) {
            log.trace("Conversion done.");
        }
        return contact;
    }

     private Contact vcard2Contact(String vcard) throws EntityException {

         if (log.isTraceEnabled()) {
            log.trace("Converting: VCARD => Contact");
        }

        //
        // If funambol is not in the debug mode is not possible to print the
        // contact's content because it contains sensitive data.
        //
        if (Configuration.getConfiguration().isDebugMode()) {
            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder(vcard.length() + 11);
                sb.append("\nINPUT = {").append(vcard).append('}');
                log.trace(sb.toString());
            }
        }

        ByteArrayInputStream buffer = null;
        VcardParser parser = null;
        Contact contact = null;
        try {
            contact = new Contact();

            buffer = new ByteArrayInputStream(vcard.getBytes());
            if ((vcard.getBytes()).length > 0) {
                parser = new VcardParser(buffer,
                        deviceTimeZoneDescription, deviceCharset);
                contact = (Contact) parser.vCard();
            } else {
                throw new EntityException("No data");
            }
        } catch (EntityException e){
            throw e;
        } catch (Exception e){
            throw new EntityException("Error converting VCARD to Contact. ", e);
        }

        if (log.isTraceEnabled()) {
            log.trace("Conversion done.");
        }
        return contact;
    }

    private String contact2sif(Contact contact) throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Converting: Contact => SIF-C");
        }

        String xml = null;
        try{
            //this.deviceTimeZone, this.deviceCharset
            ContactToSIFC c2xml = new ContactToSIFC(null,null);
            xml = c2xml.convert(contact);

            //
            // If funambol is not in the debug mode is not possible to print the
            // contact's content because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    StringBuilder sb = new StringBuilder(xml.length() + 12);
                    sb.append("\nOUTPUT = {").append(xml).append('}');
                    log.trace(sb.toString());
                }
            }

            if (log.isTraceEnabled()) {
                log.trace("Conversion done.");
            }

        } catch (Exception e){
            throw new EntityException("Error converting Contact to SIF-C. ", e);
        }
        return xml;
     }

    private String contact2vcard(Contact contact) throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Converting: Contact => VCARD");
        }

        String vcard = null;

        try{
            ContactToVcard c2vc = new ContactToVcard(deviceTimeZone,
                    deviceCharset);
            vcard = c2vc.convert(contact);

            //
            // If funambol is not in the debug mode is not possible to print the
            // contact's content because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    StringBuilder sb = new StringBuilder(vcard.length() + 12);
                    sb.append("\nOUTPUT = {").append(vcard).append('}');
                    log.trace(sb.toString());
                }
            }

            if (log.isTraceEnabled()) {
                log.trace("Conversion done.");
            }

        } catch (Exception e){
            throw new EntityException("Error converting Contact to VCARD. ", e);
        }
        return vcard;
     }

    /**
     * Create a new SyncItem from a Contact. The target contentType and status
     * are passed as arguments.
     *
     * @param contact the Contact object representing the input information
     * @param contentType chosen among the TYPE array's elements
     * @param status
     * @throws EntityException if the content type is wrong or any problem
     *                         occurs while creating a new SyncItem
     * @return a newly created SyncItem object
     */
    private SyncItem createSyncItem(String id, Contact contact, char status)
    throws EntityException {

        String contentType;
        if (rxContentType != null) { 
            // Use device capabilities
            contentType = rxContentType;
        } else {
            // Use user-set default
            contentType = getInfo().getPreferredType().getType();
        }

        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder(100);
            sb.append("PIMContactSyncSource - creating item with:")
              .append("\n> id: ").append(id)
              .append("\n> status: ").append(status)
              .append("\n> content-type: ").append(contentType);
            log.trace(sb.toString());
        }


        SyncItem syncItem = null;
        String stream = convert(contact, contentType);

        try {
            syncItem = new SyncItemImpl(this, id, status);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        syncItem.setType(contentType);
        syncItem.setContent(stream.getBytes());

        if (log.isTraceEnabled()) {
            log.trace("PIMContactSyncSource created SyncItem");
        }

        return syncItem;
    }

    /**
     * Converts a SyncItem to a Contact object, provided it represents a contact
     * item in VCard or SIF-C format.
     *
     * @param syncItem
     * @throws EntityException if the contentType is wrong or the conversion
     *                         attempt doesn't succeed.
     * @return a Contact object
     */
    private Contact convert(SyncItem syncItem) throws EntityException {
        return convert(getContentFromSyncItem(syncItem), syncItem.getType());
    }

    /**
     * Converts a contact in vCard or SIF-C format to a Contact object.
     *
     * @param content as a String
     * @param contentType
     * @throws EntityException if the contentType is wrong or the conversion
     *                         attempt doesn't succeed.
     * @return a Contact object
     */
    private Contact convert(String content, String contentType)
    throws EntityException {
        // Finds out which target type is required
        for (int i = 0; i < TYPE.length; i++) {
            if (contentType.equals(TYPE[i])) { // Bingo!

                // Uses the proper converter method
                switch(i) {
                    case VCARD:
                        return vcard2Contact(content);
                    case SIFC:
                        return sif2Contact(content);
                    default:
                        throw new EntityException("Can't make a Contact "
                                                + "out of a " + TYPE[i] + "!");
                }
            }
        }
        throw new EntityException("Content type unknown: " + contentType);
    }

    /**
     * Converts a Contact back to a streamable (vCard, SIF-C) format.
     *
     * @param contact
     * @param contentType
     * @throws EntityException if the contentType is wrong or the conversion
     *                         attempt doesn't succeed.
     * @return the result in the required format
     */
    private String convert(Contact contact, String contentType)
    throws EntityException {

        if (!syncPhoto) {
            contact.getPersonalDetail().setPhotoObject(null);
        }

        // Finds out which target type is required
        for (int i = 0; i < TYPE.length; i++) {
            if (contentType.equals(TYPE[i])) { // Bingo!

                // Uses the proper converter method
                switch(i) {
                    case VCARD:
                        return contact2vcard(contact);
                    case SIFC:
                        return contact2sif(contact);
                    default:
                        throw new EntityException("Can't make a " + TYPE[i]
                                                + " out of a Contact!");
                }
            }
        }
        throw new EntityException("Content type unknown: " + contentType);
    }
    
}
