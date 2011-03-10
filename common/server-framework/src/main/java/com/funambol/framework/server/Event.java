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

package com.funambol.framework.server;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class represents a logging event.
 *
 * @version $Id: Event.java,v 1.2 2008-02-25 14:24:27 luigiafassina Exp $
 */
public class Event implements Serializable {

    // ------------------------------------------------------------ Private data
    private Timestamp eventTime           = null;
    private String    eventType           = null;
    private String    loggerName          = null;
    private String    userName            = null;
    private String    deviceId            = null;
    private String    sessionId           = null;
    private String    source              = null;
    private String    message             = null;
    private String    syncType            = null;
    private int       numTransferredItems = 0   ;
    private int       numAddedItems       = 0   ;
    private int       numDeletedItems     = 0   ;
    private int       numUpdatedItems     = 0   ;
    private int       duration            = 0   ;
    private String    originator          = null;
    private String    statusCode          = null;
    private boolean   error               = false;

    // ------------------------------------------------------------ Constructors
    /** Creates a new empty instance of an Event */
    public Event() {
    }

    /**
     * Creates a new Event istance with the following data
     * @param eventTime it's the time when the Event object has been triggered
     * @param eventType it's a string describing the Event type
     * @param loggerName it's the name of the logger used to trigger the event
     * @param userName it's the name of the user the event refers to
     * @param deviceId it's the device id
     * @param sessionId it's the id of the session this Event is triggered within
     * @param source it's the uri of the sync source handling whom the Event has
     * been triggered
     * @param message it's a short description of this Event
     */
    public Event(Timestamp eventTime ,
                 String    eventType ,
                 String    loggerName,
                 String    userName  ,
                 String    deviceId  ,
                 String    sessionId ,
                 String    source    ,
                 String    message   ) {

        this.eventTime  = eventTime ;
        this.eventType  = eventType ;
        this.loggerName = loggerName;
        this.userName   = userName  ;
        this.deviceId   = deviceId  ;
        this.sessionId  = sessionId ;
        this.source     = source    ;
        this.message    = message   ;
    }

    /**
     * Creates a new Event istance with the following data
     * @param eventTime it's the time when the Event object has been triggered
     * @param eventType it's a string describing the Event type
     * @param loggerName it's the name of the logger used to trigger the event
     * @param userName it's the name of the user the event refers to
     * @param deviceId it's the device id
     * @param sessionId it's the id of the session this Event is triggered within
     * @param source it's the uri of the sync source handling whom the Event has
     * been triggered
     * @param message it's a short description of this Event
     * @param syncType it's a value describing the type of the performed sync
     * (200, 201 and so on)
     * @param numTransferredItems it's the total number of transferred items (both
     * client and server side)
     * @param numAddedItems it's the total number of added items (both
     * client and server side)
     * @param numDeletedItems it's the total number of deleted items (both client
     * and server side)
     * @param numUpdatedItems it's the total number of updated items
     * @param duration it's a long value representing how long the synchronization
     * process took.
     * @param originator it's the source component that caused this Event to be triggered
     * @param statusCode it's the statusCode that will be returned to the client
     * as status of the synchronization process if any
     * @param error it's a flag that is set to true if the Event represents an error
     * event and it's false otherwise.
     */
    public Event(Timestamp eventTime ,
                 String    eventType ,
                 String    loggerName,
                 String    userName  ,
                 String    deviceId  ,
                 String    sessionId ,
                 String    source    ,
                 String    message   ,
                 String    syncType  ,
                 int       numTransferredItems,
                 int       numAddedItems      ,
                 int       numDeletedItems    ,
                 int       numUpdatedItems    ,
                 int       duration           ,
                 String    originator         ,
                 String    statusCode         ,
                 boolean   error              ) {

        this.eventTime  = eventTime ;
        this.eventType  = eventType ;
        this.loggerName = loggerName;
        this.userName   = userName  ;
        this.deviceId   = deviceId  ;
        this.sessionId  = sessionId ;
        this.source     = source    ;
        this.message    = message   ;
        this.syncType   = syncType  ;
        this.numTransferredItems = numTransferredItems;
        this.numAddedItems       = numAddedItems      ;
        this.numDeletedItems     = numDeletedItems    ;
        this.numUpdatedItems     = numUpdatedItems    ;
        this.duration            = duration           ;
        this.originator          = originator         ;
        this.statusCode          = statusCode         ;
        this.error               = error              ;
    }

    /**
     * Creates a new Event istance with the following data
     * @param eventTime it's the time when the Event object has been triggered
     * @param eventType it's a string describing the Event type
     * @param loggerName it's the name of the logger used to trigger the event
     * @param userName it's the name of the user the event refers to
     * @param deviceId it's the device id
     * @param sessionId it's the id of the session this Event is triggered within
     * @param source it's the uri of sync source handling whom the Event has been
     * triggered
     * @param message it's a short description of this Event
     * @param syncType it's a value describing the type of the performed sync
     * (200,201, and so on)
     * @param numTransferredItems it's the total number of transferred items (both
     * client and server side)
     * @param numAddedItems it's the total number of added items (both
     * client and server side)
     * @param numDeletedItems it's the total number of deleted items (both client
     * and server side)
     * @param numUpdatedItems it's the total number of updated items
     * @param originator it's the source component that caused this Event to be
     * triggered
     * @param statusCode it's the statusCode that will be returned to the client
     * as status of the synchronization process if any
     * @param error it's a flag that is set to true if the Event represents an error
     * event and it's false otherwise.
     */
    public Event(Timestamp eventTime ,
                 String    eventType ,
                 String    loggerName,
                 String    userName  ,
                 String    deviceId  ,
                 String    sessionId ,
                 String    source    ,
                 String    message   ,
                 String    syncType  ,
                 int       numTransferredItems,
                 int       numAddedItems      ,
                 int       numDeletedItems    ,
                 int       numUpdatedItems    ,
                 String    originator         ,
                 String    statusCode         ,
                 boolean   error              ) {

        this.eventTime  = eventTime ;
        this.eventType  = eventType ;
        this.loggerName = loggerName;
        this.userName   = userName  ;
        this.deviceId   = deviceId  ;
        this.sessionId  = sessionId ;
        this.source     = source    ;
        this.message    = message   ;
        this.syncType   = syncType  ;
        this.numTransferredItems = numTransferredItems;
        this.numAddedItems       = numAddedItems      ;
        this.numDeletedItems     = numDeletedItems    ;
        this.numUpdatedItems     = numUpdatedItems    ;
        this.originator          = originator         ;
        this.statusCode          = statusCode         ;
        this.error               = error              ;
    }
    // ---------------------------------------------------------- Public methods
    /**
     * Gets the time when the event occurred.
     *
     * @return the time when the event occurred
     */
    public Timestamp getEventTime() {
        return eventTime;
    }

    /**
     * Sets the time when the event occurred.
     *
     * @param eventTime the time when the event occurred
     */
    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * Gets the event type.
     *
     * @return the event type
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the event type.
     *
     * @param eventType the event type
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the logger name.
     *
     * @return the logger name
     */
    public String getLoggerName() {
        return loggerName;
    }

    /**
     * Sets the logger name.
     *
     * @param loggerName the logger name
     */
    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    /**
     * Gets the username involved in the event.
     *
     * @return the username involved in the event
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the username involved in the event.
     *
     * @param userName the username involved in the event
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the device id involved in the event (if any).
     *
     * @return the device id involved in the event
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device id involved in the event.
     *
     * @param deviceId the device id involved in the event
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the session id involved in the event (if any).
     *
     * @return the session id involved in the event
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session id involved in the event.
     *
     * @param sessionId the session id involved in the event
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the uri of the sync source involved in the event (if any).
     *
     * @return the uri of the sync source involved in the event
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the uri of the sync source involved in the event.
     *
     * @param source the uri of the sync source involved in the event
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the log message.
     *
     * @return the log message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the log message.
     *
     * @param message the log message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the synchronization type (e.i SLOW)
     *
     * @return the synchronization type
     */
    public String getSyncType() {
        return syncType;
    }

    /**
     * Sets the synchronization type
     *
     * @param syncType the synchronization type to set
     */
    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    /**
     * Gets the number of items transferred during the sync
     *
     * @return the number of items transferred during the sync
     */
    public int getNumTransferredItems() {
        return numTransferredItems;
    }

    /**
     * Sets the number of items transferred during the sync
     *
     * @param numTransferredItems the number of items transferred during the
     *                            sync to set
     */
    public void setNumTransferredItems(int numTransferredItems) {
        this.numTransferredItems = numTransferredItems;
    }

    /**
     * Gets the number of items added during the sync
     *
     * @return the number of items added during the sync
     */
    public int getNumAddedItems() {
        return numAddedItems;
    }

    /**
     * Sets the number of items added during the sync
     *
     * @param numAddedItems the number of items added during the sync to set
     */
    public void setNumAddedItems(int numAddedItems) {
        this.numAddedItems = numAddedItems;
    }

    /**
     * Gets the number of items deleted during the sync
     *
     * @return the number of items deleted during the sync
     */
    public int getNumDeletedItems() {
        return numDeletedItems;
    }

    /**
     * Sets the number of items deleted during the sync
     *
     * @param numDeletedItems the number of items deleted during the sync to set
     */
    public void setNumDeletedItems(int numDeletedItems) {
        this.numDeletedItems = numDeletedItems;
    }

    /**
     * Gets the number of items updated during the sync
     *
     * @return the number of items updated during the sync
     */
    public int getNumUpdatedItems() {
        return numUpdatedItems;
    }

    /**
     * Sets the number of items updated during the sync
     *
     * @param numUpdatedItems the number of items updated during the sync to set
     */
    public void setNumUpdatedItems(int numUpdatedItems) {
        this.numUpdatedItems = numUpdatedItems;
    }

    /**
     * Gets the event duration in milliseconds
     *
     * @return the event duration in milliseconds
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Sets the number of items updated during the sync
     *
     * @param duration the event duration in milliseconds
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Gets the originator's name that has triggered the event
     *
     * @return the originator's name
     */
    public String getOriginator() {
        return originator;
    }

    /**
     * Sets the originator's name that has triggered the event
     *
     * @param originator the originator's name to set
     */
    public void setOriginator(String originator) {
        this.originator = originator;
    }

    /**
     * Gets the code of the error or warning event
     * @deprecated in favor of getStatusCode since version 8.7
     * @return the code of the error or warning event
     */
    public String getExceptionCode() {
        return statusCode;
    }

    /**
     * Sets the code of the error or warning event
     * @deprecated in favor of setStatusCode since version 8.7
     * @param exceptionCode the code of the error or warning event to set
     */
    public void setExceptionCode(String exceptionCode) {
        this.statusCode = exceptionCode;
    }

    /**
     * @return the error
     */
    public boolean isError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(boolean error) {
        this.error = error;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[userName=").append(userName);
        sb.append(", deviceId="  ).append(deviceId)
          .append(", sessionId=" ).append(sessionId)
          .append(", source="    ).append(source)
          .append(", loggerName=").append(loggerName)
          .append(", eventTime=" ).append(eventTime)
          .append(", eventType=" ).append(eventType)
          .append(", message="   ).append(message)
          .append(", syncType=").append(syncType)
          .append(", numTransferredItems=").append(numTransferredItems)
          .append(", numAddedItems=").append(numAddedItems)
          .append(", numDeletedItems=").append(numDeletedItems)
          .append(", numUpdatedItems=").append(numUpdatedItems)
          .append(", originator=").append(originator)
          .append(", statusCode=").append(statusCode)
          .append(", error=").append(error)
          .append(']');

        return sb.toString();
    }

    /**
     * Gets the status code bound to this event if any.
     * The status code is the value that will be returned to the client as
     * result of the sync process.
     * This property is not available to any event object.
     * 
     * @return the status code bound to the sync during which this event has been
     * generated
     */
    public String getStatusCode() {
        return statusCode   ;
    }

    /**
     * Sets the status code of this event object
     *
     * @param statusCode the code of the error or warning event to set
     */
    public void setStatusCode(String statusCode) {
        this.statusCode    = statusCode;
    }


}
