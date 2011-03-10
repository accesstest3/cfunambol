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
package com.funambol.pimlistener.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.funambol.framework.core.Alert;
import com.funambol.framework.core.CmdID;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Target;
import com.funambol.framework.logging.LogContext;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.error.ServerException;

import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.UnauthorizedException;

import com.funambol.pushlistener.framework.ScheduledTask;
import com.funambol.pushlistener.framework.TaskConfiguration;
import com.funambol.pushlistener.framework.TaskException;
import com.funambol.pushlistener.service.registry.dao.DataAccessException;
import com.funambol.pushlistener.service.ws.NotificationWrapper;
import com.funambol.pushlistener.service.ws.UnexpectedException;

import com.funambol.pimlistener.registry.PimRegistryEntry;
import com.funambol.pimlistener.registry.dao.PimRegistryEntryDao;
import com.funambol.pimlistener.service.dao.PimListenerDao;

/**
 * This class implements the logic behind any single pim listener task.
 * <p>
 * At configuration time, the user to monitor is read from the pim registry entry.
 * <p>
 * At each execution, all principals of the configured user are read and any
 * principal is checked for something out of sync. If there is something out of
 * sync, a notification message is sent. In order to avoid to send more
 * notification messages for the same change, the last check time concept has
 * been introduced. If a change has been detected and a notification message is
 * already sent at the device, but the user decides to not sync, the listener
 * has not to send another notification for the same change. In this  case it's
 * not possible to use the last sync time, but the time of last check has to be
 * used as reference time to detect the changes. This time is -1 when the pim
 * listener starts (note: each time the pim listener configuration is changed
 * - by console or in the db - the task is re-created and so this time is -1).
 * This last check time is set always before reading the principals for the
 * given username and it is used, instead of the last sync time, when it is
 * greater than the last sync time.
 *
 * A device will be notified only if it has done at least once a
 * synchronization for the out of sync syncsource. For instance, if a contact is
 * changed but no contact syncsource has been synchronized, no notification
 * will be sent.
 *
 * <p>
 * Note that the task is configurable with a regular expression
 * (<CODE>excludedDevicesRegExp</CODE>) to detect the devices that must not be
 * notified.
 *
 * The implemented rules are:
 * <ui>
 *   <li>if pimRegistryEntry.isPushContact() is true:
 *     <ol>
 *       <li>Check if at least one contact syncsource has been synchronized. If
 *         there are no contact syncsource in sync, no notification will be sent
 *         (no check is performed).
 *       </li>
 *       <li>
 *         Has it synchronized with only '<I>card</I>' ? If yes, contacts are
 *         checked notifying, if needed, the source 'card'
 *       </li>
 *       <li>
 *         Has it synchronized with only '<I>scard</I>' ? If yes, contacts are
 *         checked notifying, if needed, the source 'scard'
 *       </li>
 *       <li>
 *         Has it synchronized with '<I>card</I>' and '<I>scard</I> ? If yes,
 *         contacts are checked notifying, if needed, the last synchronized
 *         syncsource
 *       </li>
 *     </ol>
 *   </li>
 *
 *   <br/>
 *   <li>if pimRegistryEntry.isPushCalendar() is true:
 *     <ol>
 *       <li>Check if at least one calendar syncsource has been synchronized. If
 *         there are no calendar syncsource in sync, no notification will be
 *         sent (no check is performed).
 *       </li>
 *       <li>
 *         Is '<I>cal</I>' the last syncsource used to sync ? If yes, events and
 *         tasks are checked notifying, if needed, the source '<I>cal</I>'
 *       </li>
 *       <li>
 *         Is '<I>cal</I>' the last syncsource used to sync ? If no, it's needed
 *         to know if the latest synchronized syncsource was a SIF source.
 *         If it is a SIF source, events and tasks are checked notifying, if
 *         needed, the sources '<I>scal</I>' and '<I>stask</I>'.
 *         Otherwise, events and tasks are checked notifying, if  needed, the
 *         sources '<I>event</I>' and '<I>task</I>'.
 *       </li>
 *     </ol>
 *   </li>
 *
 *   <br/>
 *   <li>if pimRegistryEntry.isPushNote() is true:
 *     <ol>
 *       <li>Check if at least one note syncsource has been synchronized. If
 *         there are no note syncsource in sync, no notification will be sent
 *         (no check is performed).
 *       </li>
 *       <li>
 *         Has it synchronized with only '<I>note</I>' ? If yes, notes are
 *         checked notifying, if needed, the source 'note'
 *       </li>
 *       <li>
 *         Has it synchronized with only '<I>snote</I>' ? If yes, notes are
 *         checked notifying, if needed, the source 'snote'
 *       </li>
 *       <li>
 *         Has it synchronized with '<I>note</I>' and '<I>snote</I> ? If yes,
 *         notes are checked notifying, if needed, the last synchronized
 *         syncsource
 *       </li>
 *     </ol>
 *   </li>
 * </ui>
 *
 * Those are the most important parameters:
 *
 * <ui>
 *   <li>
 *     <code>excludedDevicesRegExp:</code> the regular expression to detected
 *     excluded devices.
 *   </li>
 *   <br/>
 *   <li>
 *     <code>syncSourceContact:</code> the source uri of the syncsource used to
 *     sync contacts not in SIF format. Default value <i>card</i>.
 *   </li>
 *   <li>
 *     <CODE>syncSourceSIFContact:</CODE> the source uri of the syncsource used
 *     to sync contacts in SIF format. Default value <i>scard</i>.
 *   </li>
 *   <li>
 *     <CODE>syncSourceCalendar:</CODE> the source uri of the syncsource used to
 *     sync calendards (events and tasks) not in SIF format.
 *     Default value <i>cal</i>.
 *   </li>
 *   <li>
 *     <CODE>syncSourceEvent:</CODE> the source uri of the syncsource used to
 *     sync events not in SIF format. Default value <i>event</i>.
 *   </li>
 *   <li>
 *     <CODE>syncSourceSIFEvent:</CODE> the source uri of the syncsource used to
 *     sync events in SIF format. Default value <i>scal</i>.
 *   </li>
 *   <li>
 *     <CODE>syncSourceTask:</CODE> the source uri of the syncsource used to
 *     sync the tasks (todos) not in SIF format. Default value <i>task</i>.
 *   </li>
 *   <li>
 *     <CODE>syncSourceSIFTask:</CODE> the source uri of the syncsource used to
 *     sync the tasks (todos) in SIF format. Default value <i>stask</i>.
 *   </li>
 *   <li>
 *     <CODE>syncSourcePlainNote:</CODE> the source uri of the syncsource used
 *     to sync the notes not in SIF format. Default value <i>note</i>.
 *   </li>
 *   <li>
 *     <CODE>syncSourceSIFNote:</CODE> the source uri of the syncsource used to
 *     sync the notes on SIF format. Default value <i>snote</i>.
 *   </li>
 *   <br/>
 *   <li>
 *     <CODE>contentTypeContact:</CODE> the content type of contacts represented
 *     in VCard. Default value <i>card</i>.
 *   </li>
 *   <li>
 *     <CODE>contentTypeSIFContact:</CODE> the content type of contacts
 *     represented in SIF-C. Default value <i>text/x-s4j-sifc</i>.
 *   </li>
 *   <li>
 *     <CODE>contentTypeCalendar:</CODE> the content type of calendars
 *     represented in ICalendar. Default value <i>text/x-vcalendar</i>.
 *   </li>
 *   <li>
 *     <CODE>contentTypeEvent:</CODE> the content type of events represented in
 *     ICalendar. Default value <i>text/x-vcalendar</i>.
 *   </li>
 *   <li>
 *     <CODE>contentTypeSIFEvent:</CODE> the content type of events represented
 *     in SIF-E. Default value <i>text/x-s4j-sife</i>.
 *   </li>
 *   <li>
 *     <CODE>contentTypeTask:</CODE> the content type of tasks (todos)
 *     represented in ICalendar. Default value <i>text/x-vcalendar</i>.
 *   </li>
 *   <li>
 *     <CODE>contentTypeSIFTask:</CODE> the content type of tasks (todos)
 *     represented in SIF-T. Default value <i>text/x-s4j-sift</i>.
 *   </li>
 *   <li>
 *     <CODE>contentTypePlainNote:</CODE> the content type of notes represented
 *     in plain notes. Default value <i>text/plain</i>.
 *   </li>
 *   <li>
 *     <CODE>contentTypeSIFNote:</CODE> the content type of notes represented in
 *     SIF-N. Default value <i>text/x-s4j-sifn</i>.
 *   </li>
 * </ui>
 *
 * All parameters are configurable via xml file.
 *
 * @version $Id: PimTask.java,v 1.15 2008-06-19 10:06:52 piter_may Exp $
 */
public class PimTask implements ScheduledTask {

    // --------------------------------------------------------------- Constants
    /**
     * This is the prefix used by the server to notify that a notification message
     * has not been delivered
     */
    private static final String NOTIFICATION_NOT_SENT_PREFIX =
        "[NotificationNotSentException]";

    // ------------------------------------------------------------ Private data

    /** Push listener configuration */
    private TaskConfiguration conf;

    /** Pim registry entry */
    private PimRegistryEntry pimRegistryEntry;

    /** The DAO to access to the PIM/DS Server data */
    private PimListenerDao dao = null;

    /** The DAO to access to pim registry */
    private PimRegistryEntryDao pimRegistryEntryDao = null;

    /** The logger */
    private Logger log = Logger.getLogger("funambol.pushlistener.pim");

    // -------------------------------------------------------------- Properties

    /** The regular expression to detected excluded devices*/
    private String excludedDevicesRegExp;

    public String getExcludedDevicesRegExp() {
        return excludedDevicesRegExp;
    }

    public void setExcludedDevicesRegExp(String excludedDevicesRegExp) {
       this.excludedDevicesRegExp = excludedDevicesRegExp;
    }

    /**
     * The syncSource to sync the contant in VCard format
     */
    private String syncSourceContact = "card";

    public String getSyncSourceContact() {
        return syncSourceContact;
    }

    public void setSyncSourceContact(String syncSourceContact) {
        this.syncSourceContact = syncSourceContact;
    }

    /**
     * The syncSource to sync the contant in SIF-C format
     */
    private String syncSourceSIFContact = "scard";

    public String getSyncSourceSIFContact() {
        return syncSourceSIFContact;
    }

    public void setSyncSourceSIFContact(String syncSourceSIFContact) {
        this.syncSourceSIFContact = syncSourceSIFContact;
    }

    /**
     * The syncSource to sync the calendars (events and todos) in ICal format
     */
    private String syncSourceCalendar = "cal";

    public String getSyncSourceCalendar() {
        return syncSourceCalendar;
    }

    public void setSyncSourceCalendar(String syncSourceCalendar) {
        this.syncSourceCalendar = syncSourceCalendar;
    }

    /**
     * The syncSource to sync the events in ICal format
     */
    private String syncSourceEvent = "event";

    public String getSyncSourceEvent() {
        return syncSourceEvent;
    }

    public void setSyncSourceEvent(String syncSourceEvent) {
        this.syncSourceEvent = syncSourceEvent;
    }

    /**
     * The syncSource to sync the events in SIF-E format
     */
    private String syncSourceSIFEvent = "scal";

    public String getSyncSourceSIFEvent() {
        return syncSourceSIFEvent;
    }

    public void setSyncSourceSIFEvent(String syncSourceSIFEvent) {
        this.syncSourceSIFEvent = syncSourceSIFEvent;
    }

    /**
     * The syncSource to sync the todos in ICal format
     */
    private String syncSourceTask = "task";

    public String getSyncSourceTask() {
        return syncSourceTask;
    }

    public void setSyncSourceTask(String syncSourceTask) {
        this.syncSourceTask = syncSourceTask;
    }

    /**
     * The syncSource to sync the todos in SIF-T format
     */
    private String syncSourceSIFTask = "stask";

    public String getSyncSourceSIFTask() {
        return syncSourceSIFTask;
    }

    public void setSyncSourceSIFTask(String syncSourceSIFTask) {
        this.syncSourceSIFTask = syncSourceSIFTask;
    }

    /**
     * The syncSource to sync the notes in plain format
     */
    private String syncSourcePlainNote = "note";

    public String getSyncSourcePlainNote() {
        return syncSourcePlainNote;
    }

    public void setSyncSourcePlainNote(String syncSourcePlainNote) {
        this.syncSourcePlainNote = syncSourcePlainNote;
    }

    /**
     * The syncSource to sync the notes in SIF-N format
     */
    private String syncSourceSIFNote = "snote";

    public String getSyncSourceSIFNote() {
        return syncSourceSIFNote;
    }

    public void setSyncSourceSIFNote(String syncSourceSIFNote) {
        this.syncSourceSIFNote = syncSourceSIFNote;
    }

    /**
     * The content type of contacts represented in VCard
     */
    private String contentTypeContact    = "text/x-vcard";

    public String getContentTypeContact() {
        return contentTypeContact;
    }

    public void setContentTypeContact(String contentTypeContact) {
        this.contentTypeContact = contentTypeContact;
    }

    /**
     * The content type of contacts represented in SIF-C
     */
    private String contentTypeSIFContact = "text/x-s4j-sifc";

    public String getContentTypeSIFContact() {
        return contentTypeSIFContact;
    }

    public void setContentTypeSIFContact(String contentTypeSIFContact) {
        this.contentTypeSIFContact = contentTypeSIFContact;
    }

    /**
     * The content type of calendars represented in ICalendar
     */
    private String contentTypeCalendar = "text/x-vcalendar";

    public String getContentTypeCalendar() {
        return contentTypeCalendar;
    }

    public void setContentTypeCalendar(String contentTypeCalendar) {
        this.contentTypeCalendar = contentTypeCalendar;
    }

    /**
     * The content type of events represented in ICalendar
     */
    private String contentTypeEvent = "text/x-vcalendar";

    public String getContentTypeEvent() {
        return contentTypeEvent;
    }

    public void setContentTypeEvent(String contentTypeEvent) {
        this.contentTypeEvent = contentTypeEvent;
    }

    /**
     * The content type of events represented in SIF-E
     */
    private String contentTypeSIFEvent = "text/x-s4j-sife";

    public String getContentTypeSIFEvent() {
        return contentTypeSIFEvent;
    }

    public void setContentTypeSIFEvent(String contentTypeSIFEvent) {
        this.contentTypeSIFEvent = contentTypeSIFEvent;
    }

    /**
     * The content type of tasks (todos) represented in ICalendar
     */
    private String contentTypeTask = "text/x-vcalendar";

    public String getContentTypeTask() {
        return contentTypeTask;
    }

    public void setContentTypeTask(String contentTypeTask) {
        this.contentTypeTask = contentTypeTask;
    }

    /**
     * The content type of tasks (todos) represented in SIF-T
     */
    private String contentTypeSIFTask = "text/x-s4j-sift";

    public String getContentTypeSIFTask() {
        return contentTypeSIFTask;
    }

    public void setContentTypeSIFTask(String contentTypeSIFTask) {
        this.contentTypeSIFTask = contentTypeSIFTask;
    }

    /**
     * The content type of notes represented in plain text
     */
    private String contentTypePlainNote = "text/plain";

    public String getContentTypePlainNote() {
        return contentTypePlainNote;
    }

    public void setContentTypePlainNote(String contentTypePlainNote) {
        this.contentTypePlainNote = contentTypePlainNote;
    }

    /**
     * The content type of notes represented in SIF-N
     */
    private String contentTypeSIFNote = "text/x-s4j-sifn";

    public String getContentTypeSIFNote() {
        return contentTypeSIFNote;
    }

    public void setContentTypeSIFNote(String contentTypeSIFNote) {
        this.contentTypeSIFNote = contentTypeSIFNote;
    }

    /** Time of the last check */
    private long timeLastCheck = -1;

    // ------------------------------------------------------------- Constructor
    public PimTask() throws PimListenerException {
        dao = new PimListenerDao();
        pimRegistryEntryDao = new PimRegistryEntryDao();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Passes the general configuration to this pim listener task, that is the
     * configuration parameters read from the table 
     * <code>fnbl_push_listener_registry</code>.
     *
     * @param conf this push listener configuration
     * @throws com.funambol.pushlistener.framework.TaskException if an error occurs
     */
    public void configure(TaskConfiguration conf) throws TaskException {
        this.conf = conf;
        long id = conf.getId();
        try {
            pimRegistryEntry = pimRegistryEntryDao.getEntryById(id);
            if (pimRegistryEntry == null) {
                log.error("Pim registry entry with id '" + id + "' not found");
            }
        } catch (DataAccessException ex) {
            throw new TaskException(
                "Unable to load the pim listener configuration with id: " + id, ex
            );
        }
    }

    /**
     * This method implements the algorithm that has to be performed at each
     * execution of this pim task.
     */
    public void execute() {

        if (pimRegistryEntry == null) {
            log.error("Unable to perform pim task with id '" + conf.getId() +
                      "' (the pim listener configuration is null)");
            return;
        }

        LogContext.setUserName(pimRegistryEntry.getUserName());

        long checkTime = System.currentTimeMillis();

        String deviceId = null;
        List<Sync4jPrincipal> principals = null;
        boolean cleanMDC = false;
        try {
            //
            // Reads all the principals for this user
            // (that is: reads all the devices for this user)
            //
            principals =
                dao.readPrincipalsByUserName(pimRegistryEntry.getUserName());

        } catch (Throwable ex) {
            cleanMDC = true;
            log.error("Error reading the principals", ex);
            return;
        } finally {
            if (cleanMDC) {
                LogContext.removeDeviceId();
                LogContext.removeSourceURI();
            }
        }

        try {

            List<String> sourcesToNotify = null;

            //
            // For each device, checks if there is at least one item (for each
            // kind of item this user has to monitor) such that its last
            // modification time is greater than the "last sync time" for the
            // device.
            //
            // "Last sync time" is the value of the column "start_sync" of table
            // fnbl_last_sync.
            //
            for (Sync4jPrincipal principal : principals) {
                deviceId = principal.getDeviceId();

                LogContext.setDeviceId(deviceId);

                if (Pattern.matches(excludedDevicesRegExp, deviceId)) {
                    if (log.isTraceEnabled()) {
                        log.trace("Device '" + deviceId +
                                  "' is an excluded device. Skipping it");
                    }
                    continue;
                }
                try {

                    sourcesToNotify = getSourcesToNotify(principal);

                    if (sourcesToNotify != null && !sourcesToNotify.isEmpty()) {
                        //
                        // Sends notification to the device corresponding to this
                        // principal for the syncsources specified in
                        // sourcesToNotify.
                        //
                        notifyPrincipal(principal, sourcesToNotify);

                    } else {
                        if (log.isTraceEnabled()) {
                            log.trace("Nothing to notify for '"+principal+"'");
                        }
                    }

                } catch (Throwable e) {
                    //
                    // Ignoring the exception since other devices could be
                    // notified
                    //
                    log.error("Error checking principal", e);
                }
            }

        } finally {
            LogContext.removeDeviceId();
            LogContext.removeSourceURI();

            //
            // What happens if we change the way of pim listener to work ?
            // When we enable the push contacts we should use the last check
            // time specified for the contact's syncsources otherwise we
            // could lose some changes between the last sync time and the last
            // check time if this is next to the sync. If we use a different
            // check time for each type of syncsource (one for contacts, one for
            // calendars and one for notes), probably we will not have this
            // issue.
            //
            // It's not needed to have one checkTime for every syncsource since
            // when the pim listener configuration is changed, a new instance of
            // PimTask is created.
            //
            timeLastCheck = checkTime;
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Checks if the given principal must be notified for some sources out of
     * sync.
     *
     * @param principal the principal to check
     * @return the list of SyncSources out of sync to notify
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    private List<String> getSourcesToNotify(Sync4jPrincipal principal)
    throws PimListenerException {

        List<String> sourcesToNotify = new ArrayList<String>();

        Map<String, Long> pimLastSyncs = dao.readPimLastSync(principal);

        //
        // Empty map means that no syncs have been performed, so no notification
        // has to be sent for the given principal.
        //
        if (!pimLastSyncs.isEmpty()) {

            if (pimRegistryEntry.isPushContacts()) {
                String contactSyncSourceToNotify =
                        handleContactSyncSources(principal, pimLastSyncs);
                if (contactSyncSourceToNotify != null) {
                    sourcesToNotify.add(contactSyncSourceToNotify);
                }
            }

            if (pimRegistryEntry.isPushCalendars()) {
                //
                // we can have more than one syncsources (e.g. task + event)
                //
                List<String> calendarSyncSourcesToNotify =
                        handleCalendarSyncSources(principal, pimLastSyncs);
                sourcesToNotify.addAll(calendarSyncSourcesToNotify);
            }

            if (pimRegistryEntry.isPushNotes()) {
                String noteSyncSourceToNotify =
                        handleNoteSyncSources(principal, pimLastSyncs);
                if (noteSyncSourceToNotify != null) {
                    sourcesToNotify.add(noteSyncSourceToNotify);
                }
            }
        }

        LogContext.removeSourceURI();

        return sourcesToNotify;
    }

    /**
     * Returns the contact syncsource out of sync. Returns null if the contact
     * syncsources have never been synchronized by the given principal or if
     * there is not a syncource out of sync. If the device has synchronized both
     * contact syncsources (e.g. 'card' and 'scard'), then the last syncsource
     * used in the sync will be returned.
     *
     * @param principal the principal to check
     * @param pimLastSyncs the map of syncsources and last sync times
     * @return the contact syncsource out of sync (or null)
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    private String handleContactSyncSources(Sync4jPrincipal   principal   ,
                                            Map<String, Long> pimLastSyncs)
    throws PimListenerException {

        String whichSyncSource = null;

        try {
            long lastSyncTime = -1;
            long referenceTime = -1;
            Long startSyncCard = new Long(-1), startSyncSCard = new Long(-1);

            if (pimLastSyncs.containsKey(syncSourceContact)) {
                startSyncCard = pimLastSyncs.get(syncSourceContact);
            }
            if (pimLastSyncs.containsKey(syncSourceSIFContact)) {
                startSyncSCard = pimLastSyncs.get(syncSourceSIFContact);
            }

            if (startSyncCard == -1 && startSyncSCard == -1) {
                //
                // do nothing, since contact syncsources have never been sync
                //
                if (log.isTraceEnabled()) {
                    log.trace("Contacts have never been " +
                              "synchronized by principal '" + principal.getId()
                              + "'");
                }
            } else {
                //
                // At least one value is not -1
                //
                if (startSyncCard > startSyncSCard) {
                    lastSyncTime = startSyncCard;
                    whichSyncSource = syncSourceContact;
                } else {
                    lastSyncTime = startSyncSCard;
                    whichSyncSource = syncSourceSIFContact;
                }

                if (timeLastCheck < lastSyncTime) {
                    referenceTime = lastSyncTime;
                } else {
                    referenceTime = timeLastCheck;
                }

                LogContext.setSourceURI(whichSyncSource);

                if (dao.existContactsOutOfSync(principal.getUsername(), referenceTime)) {
                    return whichSyncSource;
                }
            }
        } catch(PimListenerException e) {
            throw e;
        }
        //
        // nothing to notify
        //
        return null;
    }

    /**
     * Returns the list of calendar syncsources out of sync (we could have both
     * 'scal' and 'stask' syncsources to notify). Returns an empty array if the
     * calendar syncsources have never been synchronized by the given principal
     * or if there are no syncsources out of sync.
     *
     * @param principal the principal to check
     * @param pimLastSyncs the map of syncsources and last sync times
     * @return the list of calendar syncsources out of sync
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    private List<String> handleCalendarSyncSources(Sync4jPrincipal principal,
                                                   Map<String, Long> pimLastSyncs)
    throws PimListenerException {

        List<String> outOfSyncSyncSources = new ArrayList<String>();

        try {
            long lastSyncTime  = -1;
            long referenceTime = -1;

            Long startSyncCal      = new Long(-1),
                 startSyncEvent    = new Long(-1),
                 startSyncTask     = new Long(-1),
                 startSyncSIFEvent = new Long(-1),
                 startSyncSIFTask  = new Long(-1);

            if (pimLastSyncs.containsKey(syncSourceCalendar)) {
                startSyncCal = pimLastSyncs.get(syncSourceCalendar);
            }
            if (pimLastSyncs.containsKey(syncSourceEvent)) {
                startSyncEvent = pimLastSyncs.get(syncSourceEvent);
            }
            if (pimLastSyncs.containsKey(syncSourceTask)) {
                startSyncTask = pimLastSyncs.get(syncSourceTask);
            }
            if (pimLastSyncs.containsKey(syncSourceSIFEvent)) {
                startSyncSIFEvent = pimLastSyncs.get(syncSourceSIFEvent);
            }
            if (pimLastSyncs.containsKey(syncSourceSIFTask)) {
                startSyncSIFTask = pimLastSyncs.get(syncSourceSIFTask);
            }

            if (startSyncCal   == -1 && startSyncEvent  == -1 &&
                startSyncTask  == -1 && startSyncSIFEvent == -1 &&
                startSyncSIFTask == -1                            ) {
                //
                // do nothing, since calendar syncsources have never been sync
                //
                if (log.isTraceEnabled()) {
                    log.trace("Calendars have never been " +
                              "synchronized by principal '"+principal.getId()+"'");
                }
            } else {
                //
                // At least one value is not -1
                //

                //
                // If 'cal' is the last syncsource synchronized, it is not need
                // to check the other ones (even those in sif format) since
                // with this syncsource both appointment and task are
                // synchronized.
                //
                if (startSyncCal >= startSyncEvent    &&
                    startSyncCal >= startSyncSIFEvent &&
                    startSyncCal >= startSyncTask     &&
                    startSyncCal >= startSyncSIFTask    ) {

                    lastSyncTime = startSyncCal;

                    if (timeLastCheck < lastSyncTime) {
                        referenceTime = lastSyncTime;
                    } else {
                        referenceTime = timeLastCheck;
                    }

                    LogContext.setSourceURI(syncSourceCalendar);

                    if (dao.existCalendarsOutOfSync(principal.getUsername(), referenceTime)) {
                        outOfSyncSyncSources.add(syncSourceCalendar);
                    }

                } else {

                    boolean isSIF =
                        checkSIFCalendarSyncSources(startSyncSIFEvent,
                                                    startSyncSIFTask ,
                                                    startSyncEvent   ,
                                                    startSyncTask    );

                    if (isSIF) {
                        //
                        // we need to check scal and stask
                        //
                        if (startSyncSIFEvent != -1) {

                            if (timeLastCheck < startSyncSIFEvent) {
                                referenceTime = startSyncSIFEvent;
                            } else {
                                referenceTime = timeLastCheck;
                            }

                            LogContext.setSourceURI(syncSourceSIFEvent);

                            if (dao.existEventsOutOfSync(principal.getUsername(), referenceTime)) {
                                outOfSyncSyncSources.add(syncSourceSIFEvent);
                            }
                        }
                        if (startSyncSIFTask != -1) {
                            if (timeLastCheck < startSyncSIFTask) {
                                referenceTime = startSyncSIFTask;
                            } else {
                                referenceTime = timeLastCheck;
                            }

                            LogContext.setSourceURI(syncSourceSIFTask);

                            if (dao.existTasksOutOfSync(principal.getUsername(), referenceTime)) {
                                outOfSyncSyncSources.add(syncSourceSIFTask);
                            }
                        }
                    } else {
                        //
                        // we need to check event and task
                        //
                        if (startSyncEvent != -1) {

                            if (timeLastCheck < startSyncEvent) {
                                referenceTime = startSyncEvent;
                            } else {
                                referenceTime = timeLastCheck;
                            }

                            LogContext.setSourceURI(syncSourceEvent);

                            if (dao.existEventsOutOfSync(principal.getUsername(), referenceTime)) {
                                outOfSyncSyncSources.add(syncSourceEvent);
                            }
                        }
                        if (startSyncTask != -1) {
                            if (timeLastCheck < startSyncTask) {
                                referenceTime = startSyncTask;
                            } else {
                                referenceTime = timeLastCheck;
                            }

                            LogContext.setSourceURI(syncSourceTask);

                            if (dao.existTasksOutOfSync(principal.getUsername(), referenceTime)) {
                                outOfSyncSyncSources.add(syncSourceTask);
                            }
                        }
                    }
                }
            }
        } catch(PimListenerException e) {
            throw e;
        }

        return outOfSyncSyncSources;
    }

    /**
     * Returns <code>true</code> if the latest synchronized syncsource was a
     * SIF source.
     *
     * @param sifEventLastSync sifevent last sync time
     * @param sifTaskLastSync siftask last sync time
     * @param eventLastSync event last sync time
     * @param taskLastSync task last sync time
     * @return true if the latest synchronized syncsource was a SIF source
     */
    private boolean checkSIFCalendarSyncSources(long sifEventLastSync,
                                                long sifTaskLastSync,
                                                long eventLastSync,
                                                long taskLastSync) {

        if (sifEventLastSync > eventLastSync && sifEventLastSync > taskLastSync) {
            return true;
        }

        if (eventLastSync > sifEventLastSync && eventLastSync > sifTaskLastSync) {
            return false;
        }

        if (sifTaskLastSync > eventLastSync && sifTaskLastSync > taskLastSync) {
            return true;
        }

        if (taskLastSync > sifEventLastSync && taskLastSync > sifTaskLastSync) {
            return false;
        }

        return false;
    }

    /**
     * Returns the note syncsource out of sync. Returns null if the note
     * syncsources have never been synchronized by the given principal or if
     * there is not a syncource out of sync. If the device has synchronized both
     * note syncsources (e.g. 'note' and 'snote'), then the last syncsource used
     * in the sync will be returned.
     *
     * @param principal the principal to check
     * @param pimLastSyncs the map of syncsources and last sync times
     * @return the note syncsource out of sync (or null)
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    private String handleNoteSyncSources(Sync4jPrincipal principal,
                                         Map<String, Long> pimLastSyncs)
    throws PimListenerException {

        String whichSyncSource = null;

        try {
            long lastSyncTime = -1;
            long referenceTime = -1;
            Long startSyncNote = new Long(-1), startSyncSNote = new Long(-1);

            if (pimLastSyncs.containsKey(getSyncSourcePlainNote())) {
                startSyncNote = pimLastSyncs.get(getSyncSourcePlainNote());
            }
            if (pimLastSyncs.containsKey(getSyncSourceSIFNote())) {
                startSyncSNote = pimLastSyncs.get(getSyncSourceSIFNote());
            }

            if (startSyncNote == -1 && startSyncSNote == -1) {
                //
                // do nothing, since note syncsources have never been sync
                //
                if (log.isTraceEnabled()) {
                    log.trace("Notes have never been " +
                              "synchronized by principal '"+principal.getId()+"'");
                }
            } else {
                //
                // At least one value is not -1
                //
                if (startSyncNote > startSyncSNote) {
                    lastSyncTime = startSyncNote.longValue();
                    whichSyncSource = getSyncSourcePlainNote();
                } else {
                    lastSyncTime = startSyncSNote.longValue();
                    whichSyncSource = getSyncSourceSIFNote();
                }

                if (timeLastCheck < lastSyncTime) {
                    referenceTime = lastSyncTime;
                } else {
                    referenceTime = timeLastCheck;
                }

                LogContext.setSourceURI(whichSyncSource);

                if (dao.existNotesOutOfSync(principal.getUsername(), referenceTime)) {
                    return whichSyncSource;
                }
            }
        } catch(PimListenerException e) {
            throw e;
        }
        //
        // nothing to notify
        //
        return null;
    }

    /**
     * Sends notification to the device corresponding to the given principal.
     *
     * @param principal principal to notify
     * @param sourcesToNotify the list of syncsources to notify
     */
    private void notifyPrincipal(Sync4jPrincipal principal      ,
                                 List<String>    sourcesToNotify) {

        //
        // Array of Alert objects, one for each syncsource to notify
        //
        ArrayList<Alert> alerts = new ArrayList<Alert>();
        String contentType = null;

        for (String syncSource : sourcesToNotify) {

            if (syncSourceContact.equals(syncSource)) {
                contentType = contentTypeContact;
            } else if (syncSourceSIFContact.equals(syncSource)) {
                contentType = contentTypeSIFContact;
            } else if (syncSourceCalendar.equals(syncSource)) {
                contentType = contentTypeCalendar;
            } else if (syncSourceEvent.equals(syncSource)) {
                contentType = contentTypeCalendar;
            } else if (syncSourceSIFEvent.equals(syncSource)) {
                contentType = contentTypeSIFEvent;
            } else if (syncSourceTask.equals(syncSource)) {
                contentType = contentTypeTask;
            } else if (syncSourceSIFTask.equals(syncSource)) {
                contentType = contentTypeSIFTask;
            } else if (getSyncSourcePlainNote().equals(syncSource)) {
                contentType = contentTypePlainNote;
            } else if (getSyncSourceSIFNote().equals(syncSource)) {
                contentType = contentTypeSIFNote;
            }

            if (log.isTraceEnabled()){
                log.trace("Notifying principal '" + principal + "' for '" +
                          syncSource + "' [" + contentType + "]");
            }

            Target target = new Target(syncSource);
            Meta meta = new Meta();
            meta.setType(contentType);
            Item item = new Item(null, target, meta, null, false);

            Alert alert = new Alert(
                    new CmdID(0),
                    false,
                    null,
                    206,
                    new Item[]{item});

            alerts.add(alert);
        }

        Alert[] alertArray = (Alert[])alerts.toArray(new Alert[alerts.size()]);
        try {
            NotificationWrapper.getInstance()
                               .notifyDevice(principal.getUsername(),
                                             principal.getDeviceId(),
                                             alertArray);
        } catch (UnauthorizedException ex) {
            //
            // This exception is logged to "ERROR" since we are interesting in the
            // issue also if the pim-listener log is set to INFO and since most likely
            // it is  a configuration issue. The stacktrace is not logged since
            // is pretty useless.
            //
            log.error("Unauthorized exception calling the ds-server notification method: " + ex.getMessage());
        } catch (AdminException ex) {
            //
            // The AdminException is used from the server to notify readable error
            // message to the caller. Not sure if this exception should be logged
            // to error...
            //
            log.error(ex);
        } catch (ServerException ex) {
            //
            // The server exception is used from the server to notify any issue
            // (not necessarily error) that occurs in sending notification message.
            // Since it is not an error in the pim-listener, it is logged to "TRACE"
            //
            // Moreover, when the server doesn't deliver the notification message
            // a server exception is thrown with "[NotificationNotSentException]"
            // as prefix to the message. In this way we are able to handle this case
            // in a different way (see AdminManager.sendNotificationMessage in the
            // server code)
            //
            String message = ex.getMessage();
            if (message != null && message.startsWith(NOTIFICATION_NOT_SENT_PREFIX)) {
                if (log.isTraceEnabled()) {
                    log.trace(message.substring(NOTIFICATION_NOT_SENT_PREFIX.length()).trim());
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Server exception calling the ds-server " +
                        "notification method (check the server log for details): " +
                        ex.getMessage());
                }
            }
        } catch (UnexpectedException ex) {
            //
            // Since we don't know the error, this exception is logged to "ERROR"
            //
            log.error("Unexpected error calling the ds-server notification method", ex);
        } catch (Throwable ex) {
            //
            // Since we don't know the error, this exception is logged to "ERROR"
            //
            log.error("Error calling the ds-server notification method", ex);
        }
    }

    /**
     * Returns the period of this ScheduledTask.
     *
     * @return the period
     */
    public long getPeriod() {
        return conf.getPeriod();
    }

    /**
     * Called to shutdown the task. By now, nothing to do.
     */
    public void shutdown() {
        //
        // Nothing to do
        //
    }
}