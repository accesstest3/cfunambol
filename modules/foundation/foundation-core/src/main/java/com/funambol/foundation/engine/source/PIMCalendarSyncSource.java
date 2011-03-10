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

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.calendar.Task;
import com.funambol.common.pim.converter.BaseConverter;
import com.funambol.common.pim.converter.CalendarToSIFE;
import com.funambol.common.pim.converter.TaskToSIFT;
import com.funambol.common.pim.converter.VCalendarConverter;
import com.funambol.common.pim.converter.VComponentWriter;
import com.funambol.common.pim.icalendar.ICalendarParser;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.sif.SIFCalendarParser;
import com.funambol.common.pim.xvcalendar.XVCalendarParser;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.beans.BeanInitializationException;

import com.funambol.server.config.Configuration;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.items.manager.PIMCalendarManager;
import com.funambol.foundation.items.model.CalendarWrapper;
import com.funambol.foundation.util.Def;
import com.funambol.foundation.util.SyncSourceUtil;

/**
 * This class is a PIMSyncSource for ICal and VCal data, which could include
 * either an Event or a Todo.
 *
 * @version $Id: PIMCalendarSyncSource.java,v 1.1.1.1 2008-03-20 21:38:33 stefano_fornari Exp $
 */
public class PIMCalendarSyncSource extends PIMSyncSource {

    
    //---------------------------------------------------------------- Constants
    
    protected static final String TYPE_ANYSIF = "text/x-s4j-sif?";
    
    //------------------------------------------------------------- Private data

    private PIMCalendarManager manager;
    private String rxContentType; // preferred content type as derived from the
                                  // analysis of the DevInf (RXPref)

    //--------------------------------------------------------------- Properties

    private Class entityType; // CalendarContent or one of its subclasses

    public Class getEntityType() {
        return entityType;
    }

    public void setEntityType(Class entityType) {
        this.entityType = entityType;
    }

    //----------------------------------------------------------- Public methods

    public void beginSync(SyncContext context) {
        this.manager =
            new PIMCalendarManager(context.getPrincipal().getUsername(), entityType);
        super.manager = this.manager;
        super.beginSync(context);
        rxContentType = 
            SyncSourceUtil.getCalendarPreferredType(context, entityType);
    }

    /**
     * Makes an array of SyncItemKey objects representing the ID(s) of the
     * twin(s) of a given calendar.
     *
     * @param syncItem the SyncItem representing the calendar whose twin(s) are
     *                 looked for
     * @throws SyncSourceException
     * @return possibly, just one or no key should be in the array, but it can't
     *         be ruled out a priori that several keys get returned by this
     *         method
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException {

        try {

            Calendar calendar = convert(getContentFromSyncItem(syncItem),
                                        syncItem.getType());

            List idList = null;
            idList = manager.getTwins(calendar);

            SyncItemKey[] keyList = new SyncItemKey[idList.size()];
            for (int i = 0; i < idList.size(); i++) {
                keyList[i] = new SyncItemKey((String)idList.get(i));
            }
            return keyList;
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving twin item keys.", e);
        }
    }

    /**
     * Adds a SyncItem object (representing a calendar).
     *
     * @param syncItem the SyncItem representing the calendar
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.NEW and the GUID retrieved by the
     *         back-end
     */
    public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("PIMCalendarSyncSource addSyncItem begin");
        }

        Calendar c = null;
        String content = null;

        try {

            content = getContentFromSyncItem(syncItem);
            String contentType = syncItem.getType();

            c = convert(content, contentType);

            // Checking if at least one field used for the twin search in the
            // calendar we are adding contains meaningful data, otherwise the
            // add operation is not allowed since there's no way to prevent
            // duplication.
            if(pushBackUncheckableItems &&
               !manager.isTwinSearchAppliableOn(c)) {
                if(log.isTraceEnabled()) {
                    log.trace("Rejecting add of calendar with key '"+
                              ((syncItem!=null && syncItem.getKey()!=null)?
                                syncItem.getKey().getKeyAsString():"N/A"
                             )+"' since that calendar doesn't contain any field " +
                             "usable for twin search.");
                }
                throw new SyncSourceException("Adding a calendar without any field " +
                                               "usable for twin search set is not allowed.");
            }

            Timestamp ts = syncItem.getTimestamp();

            // Adds the calendar, wraps it in sync information and uses it to
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
            log.error("SyncSource error adding a new synchronization item.");
            throw new SyncSourceException("Error adding the item " + syncItem, e);
        }
    }

    /**
     * Updates a SyncItem object (representing a calendar).
     *
     * @param syncItem the SyncItem representing the calendar
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.UPDATED and the GUID retrieved by the
     *         back-end
     */
    public SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Updates a SyncItem from " + sourceURI);
        }

        Calendar c = null;
        String content = null;

        try {

            String id = syncItem.getKey().getKeyAsString();
            content = getContentFromSyncItem(syncItem);
            String contentType = syncItem.getType();

            c = convert(content, contentType);

            // Modifies the calendar, wraps it in sync information and uses it to
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
            log.error("SyncSource error updating a synchronization item.", e);
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
    public void removeSyncItem(SyncItemKey syncItemKey,
                               Timestamp   timestamp  ,
                               boolean     softDelete )
    throws SyncSourceException {

        try {

            if (!softDelete) {
                if (log.isTraceEnabled()) {
                    log.trace("PIMCalendarSyncSource remove the SyncItem "
                            + syncItemKey.getKeyAsString());
                }

                manager.removeItem(syncItemKey.getKeyAsString(), timestamp);
            }

        } catch (EntityException e) {
            log.error("Sync source error: could not delete item with key"
                     + syncItemKey, e);
            throw new SyncSourceException("Error deleting item. ", e);
        }
    }

    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {

        String id         = null;
        SyncItem syncItem = null;

        id = syncItemKey.getKeyAsString();
        if (log.isTraceEnabled()) {
            log.trace("PIMCalendarSyncSource get SyncItem from " + id);
        }

        try {
            CalendarWrapper cw;
            try {
                cw = manager.getItem(id);
            } catch (Exception e) {
                return null;
            }
            // Retrieves the calendar, wraps it in sync information and uses it
            // to create a new SyncItem which is the return value of this method
            syncItem = createSyncItem(id, cw.getCalendar(), SyncItemState.NEW);

        } catch (EntityException e) {
            throw new SyncSourceException("Error seeking SyncItem with ID: "
                                          + id, e);
        }

        return syncItem;
    }

    public boolean mergeSyncItems(SyncItemKey syncItemKey, SyncItem syncItem)
    throws SyncSourceException {
        try {
            Calendar calendar =
                convert(getContentFromSyncItem(syncItem), syncItem.getType());

            boolean clientUpdateRequired =
                manager.mergeItems(syncItemKey.getKeyAsString(),
                                   calendar                    ,
                                   syncItem.getTimestamp()
                );
            if (clientUpdateRequired) {
                syncItem = getSyncItemFromId(syncItemKey);
            }
            return clientUpdateRequired;

        } catch(EntityException e) {
            log.error("SyncSource error: a merge did not succeed.", e);
            throw new SyncSourceException("Error merging SyncItem with ID "
                                        + syncItemKey.getKeyAsString()
                                        + "with SyncItem " + syncItem,
                                          e);
        }
    }

    public void init() throws BeanInitializationException {
    }

    /**
     * Makes an array of SyncItemKey objects representing all new calendar
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        saveSyncTiming(since, to);

        try {
            List idList = manager.getNewItems(since, to);
            return extractKeyArrayFromIdList(idList);
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving new item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted calendar
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
            return extractKeyArrayFromIdList(idList);
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving updated item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted calendar
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     */
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        saveSyncTiming(since, to);

        try {
            List idList = manager.getDeletedItems(since, to);
            return extractKeyArrayFromIdList(idList);
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving deleted item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all calendar IDs.
     *
     * @return a SyncItemKey array
     */
    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {

        try {
            List idList = manager.getAllItems();
            return extractKeyArrayFromIdList(idList);

        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving all item keys. ", e);
        }
    }

    /**
     * Gets the status of the SyncItem with the given key.
     *
     * @param syncItemKey as a SyncItemKey object
     * @throws SyncSourceException
     * @return the status as a char
     */
    public char getSyncItemStateFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {

        String id = "N/A"; // default value for error tracking

        try {

            // Slow sync
            // @todo Implement, depending on a syncMode check

            // Fast sync
            id = syncItemKey.getKeyAsString();
            if (log.isTraceEnabled()) {
                log.trace("PIMCalendarSyncSource get SyncItem state from " + id);
            }
            char itemRawState = manager.getItemState(id, previousSyncTime);

            if (itemRawState == Def.PIM_STATE_UNCHANGED) {
                return SyncItemState.SYNCHRONIZED;
            } else {
                return itemRawState; // Def uses SyncItemState.* as constant
                // values for N, D and U states
            }
        } catch (EntityException e) {
            throw new SyncSourceException("Error getting the state of SyncItem "
                                        + "with ID " + id, e);
        }
    }

    //---------------------------------------------------------- Private methods

    /**
     * Extracts the content from a syncItem.
     *
     * @param syncItem
     * @return as a String object (same as
     *         PIMSyncSource#getContentFromSyncItem(String), but trimmed)
     */
    protected String getContentFromSyncItem(SyncItem syncItem) {

        String raw = super.getContentFromSyncItem(syncItem);

        return raw.trim();
    }

    private Calendar webCalendar2Calendar(String text, String vCalType)
    throws EntityException {

        try {

            ByteArrayInputStream buffer =
                new ByteArrayInputStream(text.getBytes());

            VCalendar vcalendar;
            String version;
            String charset;

            if (log.isTraceEnabled()) {
                log.trace("Converting: " + vCalType + " => Calendar ");
            }

            //
            // If funambol is not in the debug mode is not possible to print the
            // calendar's content because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    StringBuilder sb = new StringBuilder(text.length() + 11);
                    sb.append("\nINPUT = {").append(text).append('}');
                    log.trace(sb.toString());
                }
            }

            if (vCalType.equals(PIMSyncSource.TYPE[PIMSyncSource.VCAL])) {
                // vCalendar (1.0):
                XVCalendarParser parser = new XVCalendarParser(buffer);
                vcalendar = (VCalendar) parser.XVCalendar();
                version = "1.0";
                charset = BaseConverter.CHARSET_UTF7; // (versit spec)
            } else {
                // iCalendar (2.0):
                ICalendarParser parser = new ICalendarParser(buffer);
                vcalendar = (VCalendar) parser.ICalendar();
                version = "2.0";
                charset = BaseConverter.CHARSET_UTF8; // (RFC 2445)
            }
            if (deviceCharset != null) {
                charset = deviceCharset; // overrides the default character set
            }

            String retrievedVersion = null;
            if (vcalendar.getProperty("VERSION") != null) {
                retrievedVersion = vcalendar.getProperty("VERSION").getValue();
            }
            vcalendar.addProperty("VERSION", version);
            if (retrievedVersion == null) {
                if (log.isTraceEnabled()) {
                    log.trace("No version property was found in the vCal/iCal "
                            + "data: version set to " + version);
                }
            } else if (!retrievedVersion.equals(version)) {
                if (log.isTraceEnabled()) {
                    log.trace("The version in the data was "
                            + retrievedVersion
                            + " but it's been changed to "
                            + version);
                }
            }

            VCalendarConverter vcf =
                new VCalendarConverter(deviceTimeZone, charset, false);
            Calendar c = vcf.vcalendar2calendar(vcalendar);

            if (log.isTraceEnabled()) {
                log.trace("Conversion done.");
            }

            return c;

        } catch (Exception e) {
            throw new EntityException("Error converting " + vCalType
                                    + " to Calendar. ", e);
        }
    }

    private String calendar2webCalendar(Calendar calendar, String vCalType)
    throws EntityException {

        try{

            String charset;
            if (vCalType.equals(PIMSyncSource.TYPE[PIMSyncSource.VCAL])) {
                // vCalendar (1.0):
                charset = BaseConverter.CHARSET_UTF7; // (versit spec)
            } else {
                // iCalendar (2.0):
                charset = BaseConverter.CHARSET_UTF8; // (RFC 2445)
            }
            if (deviceCharset != null) {
                charset = deviceCharset; // overrides the default character set
            }

            VCalendarConverter vcf =
                    new VCalendarConverter(deviceTimeZone, charset, convertDateToLocal);

            VCalendar vcalendar;
            String vcal;

            if (log.isTraceEnabled()) {
                log.trace("Converting: Calendar => " + vCalType);
            }

            if (vCalType.equals(PIMSyncSource.TYPE[VCAL])) { // VCAL

                    vcalendar =
                        vcf.calendar2vcalendar(calendar, true); // text/x-vcalendar

            } else { // ICAL

                    vcalendar =
                        vcf.calendar2vcalendar(calendar, false); // text/calendar
            }

            VComponentWriter writer =
                    new VComponentWriter(VComponentWriter.NO_FOLDING);
            vcal = writer.toString(vcalendar);

            //
            // If funambol is not in the debug mode is not possible to print the
            // calendar's content because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    StringBuilder sb = new StringBuilder(vcal.length() + 12);
                    sb.append("\nOUTPUT = {").append(vcal).append('}');
                    log.trace(sb.toString());
                }
            }

            if (log.isTraceEnabled()) {
                log.trace("Conversion done.");
            }

            return vcal;

        } catch (Exception e){
            throw new EntityException("Error converting Calendar to "
                                    + vCalType, e);
        }
     }

    /**
     * Create a new SyncItem from a Calendar. The status is passed as
     * an argument.
     *
     * @param calendar the Calendar object representing the input information
     * @param status
     * @throws EntityException if the content type is wrong or any problem
     *                         occurs while creating a new SyncItem
     * @return a newly created SyncItem object
     */
    private SyncItem createSyncItem(String id, Calendar calendar, char status)
    throws EntityException {

        String contentType;
        if (rxContentType != null) { // device capabilities can be used
            if (TYPE_ANYSIF.equals(rxContentType)) {
                if (entityType.isAssignableFrom(Event.class)) {
                    contentType = TYPE[SIFE];
                } else if (entityType.isAssignableFrom(Task.class)) {
                    contentType = TYPE[SIFT];
                } else { // This should not happen
                   // Uses user-set default
                    contentType = getInfo().getPreferredType().getType(); 
                }
            } else {        
                contentType = rxContentType;
            }
            
        } else { // user-set default must be used
            contentType = getInfo().getPreferredType().getType();
        }

        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder(100);
            sb.append("PIMCalendarSyncSource - creating item with:")
              .append("\n> id: ").append(id)
              .append("\n> status: ").append(status)
              .append("\n> content-type: ").append(contentType);
            log.trace(sb.toString());
        }

        SyncItem syncItem = null;
        String stream = convert(calendar, contentType);

        try {
            syncItem = new SyncItemImpl(this, id, status);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        syncItem.setType(contentType);
        syncItem.setContent(stream.getBytes());
        if (log.isTraceEnabled()) {
            log.trace("PIMCalendarSyncSource created SyncItem");
        }
        return syncItem;
    }

    private SyncItemKey[] extractKeyArrayFromIdList(List idList) {

        SyncItemKey[] keyList = new SyncItemKey[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            keyList[i] = new SyncItemKey((String) idList.get(i));
        }
        return keyList;
    }

   /**
     * Converts a calendar in vCalendar/iCalendar, SIF-E or SIF-T format to a
     * Calendar object.
     *
     * @param content as a String
     * @param contentType
     * @throws EntityException if the contentType is wrong or the conversion
     *                         attempt doesn't succeed.
     * @return a Calendar object
     */
    private Calendar convert(String content, String contentType)
    throws EntityException {
        // Finds out which target type is required
        for (int i = 0; i < TYPE.length; i++) {
            if (contentType.equals(TYPE[i])) {

                // Uses the proper converter method
                switch(i) {
                    case VCAL:
                    case ICAL:
                        return webCalendar2Calendar(content, contentType);
                    case SIFE:
                    case SIFT:
                        return sif2Calendar(content, contentType);
                    default:
                        throw new EntityException("Can't make a Calendar "
                                                + "out of a " + TYPE[i] + "!");
                }
            }
        }
        throw new EntityException("Content type unknown: " + contentType);
    }

    /**
     * Converts a Calendar back to a streamable (vCalendar/iCalendar, SIF-E or
     * SIF-T) format.
     *
     * @param calendar
     * @param contentType
     * @throws EntityException if the contentType is wrong or the conversion
     *                         attempt doesn't succeed.
     * @return the result in the required format
     */
    private String convert(Calendar calendar, String contentType)
    throws EntityException {

        // Finds out which target type is required
        for (int i = 0; i < TYPE.length; i++) {
            if (contentType.equals(TYPE[i])) {

                // Uses the proper converter method
                switch(i) {
                    case VCAL:
                    case ICAL:
                        return calendar2webCalendar(calendar, contentType);
                    case SIFE:
                    case SIFT:
                        return calendar2sif(calendar, contentType);
                    default:
                        throw new EntityException("Can't make a " + TYPE[i]
                                                + "out of a Calendar!");
                }
            }
        }
        throw new EntityException("Content type unknown: " + contentType);
    }

    private Calendar sif2Calendar(String xml, String sifType)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Converting: " + sifType + " => Calendar ");
        }

        //
        // If funambol is not in the debug mode is not possible to print the
        // calendar's content because it contains sensitive data.
        //
        if (Configuration.getConfiguration().isDebugMode()) {
            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder(xml.length() + 11);
                sb.append("\nINPUT = {").append(xml).append('}');
                log.trace(sb.toString());
            }
        }

        ByteArrayInputStream buffer = null;
        Calendar calendar = null;
        try {
            calendar = new Calendar();
            buffer = new ByteArrayInputStream(xml.getBytes());
            if ((xml.getBytes()).length > 0) {
                    SIFCalendarParser parser = new SIFCalendarParser(buffer);
                    calendar = parser.parse();
            } else {
                throw new EntityException("No data");
            }
        } catch (EntityException e) {
            throw e;
        } catch (Exception e){
            throw new EntityException("Error converting " + sifType
                                    + " to Calendar. ", e);
        }

        if (log.isTraceEnabled()) {
            log.trace("Conversion done.");
        }
        return calendar;
    }

   private String calendar2sif(Calendar calendar, String sifType)
   throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Converting: Calendar => " + sifType);
        }

        String xml = null;
        BaseConverter c2xml;
        Object thing;

        try{
            if (sifType.equals(PIMSyncSource.TYPE[SIFE])) { // SIF-E
                c2xml = new CalendarToSIFE(deviceTimeZone, deviceCharset);
                thing = calendar;
                // NB: A CalendarToSIFE converts a Calendar into a SIF-E
            } else { // SIF-T
                c2xml = new TaskToSIFT(deviceTimeZone, deviceCharset);
                thing = calendar.getTask();
                // NB: A TaskToSIFT converts just a Task into a SIF-T
            }

            xml = c2xml.convert(thing);

            //
            // If funambol is not in the debug mode is not possible to print the
            // calendar's content because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    StringBuilder sb = new StringBuilder(xml + 12);
                    sb.append("\nOUTPUT = {").append(xml).append('}');
                    log.trace(sb.toString());
                }
            }

            if (log.isTraceEnabled()) {
                log.trace("Conversion done.");
            }

        } catch (Exception e){
            throw new EntityException("Error converting Calendar to " + sifType, e);
        }
        return xml;
     }

}
