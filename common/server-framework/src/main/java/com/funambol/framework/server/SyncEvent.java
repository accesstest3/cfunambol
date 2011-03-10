/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

/**
 * This class represents a logging event for start and end synchronization
 * events.
 *
 * @version $Id: SyncEvent.java 34823 2010-06-24 17:29:35Z luigiafassina $
 */
public class SyncEvent extends Event implements Serializable {

    // --------------------------------------------------------------- Constants
    private static final String ORIGINATOR = "DS-SERVICE";
    private static final String START_SYNC = "START_SYNC";
    private static final String END_SYNC   = "END_SYNC"  ;

    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of SyncEvent */
    public SyncEvent() {
    }

    /**
     * Creates a new instance of SyncEvent.
     *
     * @param eventType the event type
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param numAddedItems the number of items added during the sync
     * @param numDeletedItems the number of items deleted during the sync
     * @param numUpdatedItems the number of items updated during the sync
     * @param duration the event duration in milliseconds
     * @param message the error message
     * @param error is true if an error occurred
     */
    public SyncEvent(String  eventType      ,
                     String  userName       ,
                     String  deviceId       ,
                     String  sessionId      ,
                     String  syncType       ,
                     String  source         ,
                     int     numAddedItems  ,
                     int     numDeletedItems,
                     int     numUpdatedItems,
                     int     duration       ,
                     String  message        ,
                     boolean error          ) {

        super(null, eventType, null, userName, deviceId, sessionId, source,
              message, syncType, 0, numAddedItems, numDeletedItems,
              numUpdatedItems, duration, ORIGINATOR, null, error);
    }

    /**
     * Creates a new instance of SyncEvent.
     *
     * @param eventType the event type
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param numAddedItems the number of items added during the sync
     * @param numDeletedItems the number of items deleted during the sync
     * @param numUpdatedItems the number of items updated during the sync
     * @param message the error message
     * @param error is true if an error occurred
     */
    public SyncEvent(String  eventType      ,
                     String  userName       ,
                     String  deviceId       ,
                     String  sessionId      ,
                     String  syncType       ,
                     String  source         ,
                     int     numAddedItems  ,
                     int     numDeletedItems,
                     int     numUpdatedItems,
                     String  message        ,
                     boolean error          ) {

        this(eventType, userName, deviceId, sessionId, syncType, source,
              numAddedItems, numDeletedItems,
              numUpdatedItems, 0, message, error);
    }

    /**
     * Creates a new instance of SyncEvent in case of error.
     *
     * @param eventType the event type
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param message the error message
     * @param statusCode is statusCode bound to the event
     */
    public SyncEvent(String  eventType      ,
                     String  userName       ,
                     String  deviceId       ,
                     String  sessionId      ,
                     String  syncType       ,
                     String  source         ,
                     String  message        ,
                     String  statusCode) {

        super(null, eventType, null, userName, deviceId, sessionId, source,
              message, syncType, 0, 0, 0,
              0, 0, ORIGINATOR, statusCode, true);
    }

        /**
     * Creates a new instance of SyncEvent.
     *
     * @param eventType the event type
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param status the success status code for the sync
     * @param numAddedItems the number of items added during the sync
     * @param numDeletedItems the number of items deleted during the sync
     * @param numUpdatedItems the number of items updated during the sync
     * @param duration the event duration in milliseconds
     * @param message the error message
     * @param error is true if an error occurred
     */
    public SyncEvent(String  eventType      ,
                     String  userName       ,
                     String  deviceId       ,
                     String  sessionId      ,
                     String  syncType       ,
                     String  source         ,
                     String  status         ,
                     int     numAddedItems  ,
                     int     numDeletedItems,
                     int     numUpdatedItems,
                     int     duration       ,
                     String  message        ,
                     boolean error          ) {

        super(null, eventType, null, userName, deviceId, sessionId, source,
              message, syncType, 0, numAddedItems, numDeletedItems,
              numUpdatedItems, duration, ORIGINATOR, status, error);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Creates an event of type START_SYNC.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param message the error message
     * @return the event of type START_SYNC
     */
    public static Event createStartSyncEvent(String userName ,
                                             String deviceId ,
                                             String sessionId,
                                             String syncType ,
                                             String source   ,
                                             String message  ) {
        return new SyncEvent(START_SYNC, userName, deviceId, sessionId,
                             syncType, source, 0, 0, 0, 0, message, false);
    }

    /**
     * Creates an event of type START_SYNC when an error occurred.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param message the error message
     * @param statusCode it's the error status code returned
     * @return the event of type START_SYNC when an error occurred
     */
    public static Event createStartSyncEventOnError(String userName ,
                                                    String deviceId ,
                                                    String sessionId,
                                                    String syncType ,
                                                    String source   ,
                                                    String message  ,
                                                    String statusCode) {
        return new SyncEvent(START_SYNC, userName, deviceId, sessionId,
                             syncType, source, message, statusCode);
    }

    /**
     * Creates an event of type END_SYNC.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param status the status code of the sync
     * @param numAddedItems the number of items added during the sync
     * @param numDeletedItems the number of items deleted during the sync
     * @param numUpdatedItems the number of items updated during the sync
     * @param duration the event duration in milliseconds
     * @param message the error message
     * @return the event of type END_SYNC
     */
    public static Event createEndSyncEvent(String userName       ,
                                           String deviceId       ,
                                           String sessionId      ,
                                           String syncType       ,
                                           String source         ,
                                           String status         ,
                                           int    numAddedItems  ,
                                           int    numDeletedItems,
                                           int    numUpdatedItems,
                                           int    duration       ,
                                           String message        ) {
        return new SyncEvent(END_SYNC, userName, deviceId, sessionId, syncType,
                             source, status,numAddedItems, numDeletedItems,
                             numUpdatedItems, duration, message, false);
    }

    /**
     * Creates an event of type END_SYNC.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param status the status code of the sync
     * @param numAddedItems the number of items added during the sync
     * @param numDeletedItems the number of items deleted during the sync
     * @param numUpdatedItems the number of items updated during the sync
     * @param message the error message
     * @return the event of type END_SYNC
     */
    public static Event createEndSyncEvent(String userName       ,
                                           String deviceId       ,
                                           String sessionId      ,
                                           String syncType       ,
                                           String status         ,
                                           String source         ,
                                           int    numAddedItems  ,
                                           int    numDeletedItems,
                                           int    numUpdatedItems,
                                           String message        ) {
        return createEndSyncEvent(userName, deviceId, sessionId, syncType,
                             status,source, numAddedItems, numDeletedItems,
                             numUpdatedItems, 0, message);
    }

    /**
     * Creates an event of type END_SYNC when an error occurred.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param message the error message
     * @return the event of type END_SYNC when an error occurred
     */
    public static Event createEndSyncEventOnError(String userName ,
                                                  String deviceId ,
                                                  String sessionId,
                                                  String syncType ,
                                                  String source   ,
                                                  String message  ) {
        return new SyncEvent(END_SYNC, userName, deviceId, sessionId, syncType,
                             source, 0, 0, 0, 0, message, true);
    }


    /**
     * Creates an event of type END_SYNC when a sync ends with error.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param syncType the synchronization type
     * @param source the source involved in the sync
     * @param message the error message
     * @param statusCode the status code of the error
     * @return the event of type END_SYNC
     */
    public static Event createEndSyncEventOnError(String userName ,
                                               String deviceId ,
                                               String sessionId,
                                               String syncType ,
                                               String source   ,
                                               String message,
                                               String statusCode) {
       return new SyncEvent(END_SYNC,
                            userName,
                            deviceId,
                            sessionId,
                            syncType,
                            source,
                            message,
                            statusCode);
     }


    /**
     *
     * @return true if the event is triggered when a sync process ends,
     * false otherwise.
     */
    public boolean isEndSyncEvent() {
        return END_SYNC.equals(getEventType());
    }

    /**
     *
     * @return true if the event is triggered when a sync process starts,
     * false otherwise.
     */
    public boolean isStartSyncEvent() {
        return START_SYNC.equals(getEventType());
    }
}
