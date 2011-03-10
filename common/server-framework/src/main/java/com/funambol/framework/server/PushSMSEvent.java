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

/**
 * This class represents a logging event for push and SMS events.
 *
 * @version $Id: PushSMSEvent.java 34107 2010-03-26 10:24:17Z filmac $
 */
public class PushSMSEvent extends Event {

    // --------------------------------------------------------------- Constants
    private static final String PUSH_COP          = "PUSH_COP"         ;
    private static final String PUSH_CLP          = "PUSH_CLP"         ;
    private static final String PUSH_SMS          = "PUSH_SMS"         ;
    private static final String OTA               = "OTA"              ;
    private static final String SMS_DOWNLOAD_LINK = "SMS_DOWNLOAD_LINK";

    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of PushSMSEvent */
    public PushSMSEvent() {
    }

    /**
     * Creates a new instance of PushSMSEvent.
     *
     * @param eventType the event type
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @param error is true if an error occurred
     */
    public PushSMSEvent(String  eventType ,
                        String  userName  ,
                        String  deviceId  ,
                        String  sessionId ,
                        String  originator,
                        String  message   ,
                        boolean error     ) {

        super(null, eventType, null, userName, deviceId, sessionId, null,
              message, null, 0, 0, 0, 0, 0, originator, null, error);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Creates an event of type PUSH_COP.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type PUSH_COP
     */
    public static Event createPushCOPEvent(String userName  ,
                                           String deviceId  ,
                                           String sessionId ,
                                           String originator,
                                           String message   ) {
        return new PushSMSEvent(PUSH_COP, userName, deviceId, sessionId,
                                originator, message, false);
    }

    /**
     * Creates an event of type PUSH_COP when an error occurred.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type PUSH_COP when an error occurred
     */
    public static Event createPushCOPEventOnError(String userName  ,
                                                  String deviceId  ,
                                                  String sessionId ,
                                                  String originator,
                                                  String message   ) {
        return new PushSMSEvent(PUSH_COP, userName, deviceId, sessionId,
                                originator, message, true);
    }

    /**
     * Creates an event of type PUSH_CLP.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type PUSH_CLP
     */
    public static Event createPushCLPEvent(String userName  ,
                                           String deviceId  ,
                                           String sessionId ,
                                           String originator,
                                           String message   ) {
        return new PushSMSEvent(PUSH_CLP, userName, deviceId, sessionId,
                                originator, message, false);
    }

    /**
     * Creates an event of type PUSH_CLP when an error occurred.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type PUSH_CLP when an error occurred
     */
    public static Event createPushCLPEventOnError(String userName  ,
                                                  String deviceId  ,
                                                  String sessionId ,
                                                  String originator,
                                                  String message   ) {
        return new PushSMSEvent(PUSH_CLP, userName, deviceId, sessionId,
                                originator, message, true);
    }

    /**
     * Creates an event of type PUSH_SMS.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type PUSH_SMS
     */
    public static Event createPushSMSEvent(String userName  ,
                                           String deviceId  ,
                                           String sessionId ,
                                           String originator,
                                           String message   ) {
        return new PushSMSEvent(PUSH_SMS, userName, deviceId, sessionId,
                                originator, message, false);
    }

    /**
     * Creates an event of type PUSH_SMS when an error occurred.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type PUSH_SMS when an error occurred
     */
    public static Event createPushSMSEventOnError(String userName  ,
                                                  String deviceId  ,
                                                  String sessionId ,
                                                  String originator,
                                                  String message   ) {
        return new PushSMSEvent(PUSH_SMS, userName, deviceId, sessionId,
                                originator, message, true);
    }

    /**
     * Creates an event of type OTA.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type OTA
     */
    public static Event createOTAEvent(String userName  ,
                                       String deviceId  ,
                                       String sessionId ,
                                       String originator,
                                       String message   ) {
        return new PushSMSEvent(OTA, userName, deviceId, sessionId, originator,
                                message, false);
    }

    /**
     * Creates an event of type OTA when an error occurred.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type OTA when an error occurred
     */
    public static Event createOTAEventOnError(String userName  ,
                                              String deviceId  ,
                                              String sessionId ,
                                              String originator,
                                              String message   ) {
        return new PushSMSEvent(OTA, userName, deviceId, sessionId, originator,
                                message, true);
    }

    /**
     * Creates an event of type SMS_DOWNLOAD_LINK.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type SMS_DOWNLOAD_LINK
     */
    public static Event createSMSDownloadLinkEvent(String userName  ,
                                                   String deviceId  ,
                                                   String sessionId ,
                                                   String originator,
                                                   String message   ) {
        return new PushSMSEvent(SMS_DOWNLOAD_LINK, userName, deviceId,
                                sessionId, originator, message, false);
    }

    /**
     * Creates an event of type SMS_DOWNLOAD_LINK when an error occurred.
     *
     * @param userName the user involved in the event
     * @param deviceId the device id involved in the event
     * @param sessionId the session id involved in the event
     * @param originator the component that produced the event
     * @param message the error message
     * @return the event of type SMS_DOWNLOAD_LINK when an error occurred
     */
    public static Event createSMSDownloadLinkEventOnError(String userName  ,
                                                          String deviceId  ,
                                                          String sessionId ,
                                                          String originator,
                                                          String message   ) {
        return new PushSMSEvent(SMS_DOWNLOAD_LINK, userName, deviceId,
                                sessionId, originator, message, true);
    }

   /**
    *
    * @return true if the event is related to a  cop activity,
    * false otherwise.
    */

    public boolean isCopEvent() {
        return PUSH_COP.equals(getEventType());
    }

   /**
    *
    * @return true if the event is related to a clp activities,
    * false otherwise.
    */

    public boolean isClpEvent() {
        return PUSH_CLP.equals(getEventType());
    }

   /**
    *
    * @return true if the event is related to a sms push activities,
    * false otherwise.
    */

    public boolean isPushSmsEvent() {
        return PUSH_SMS.equals(getEventType());
    }

   /**
    *
    * @return true if the event is related to ota activities,
    * false otherwise.
    */

    public boolean isOtaEvent() {
        return OTA.equals(getEventType());
    }

   /**
    *
    * @return true if the event is related to the sending of an sms containing
    * a download link, false otherwise
    */

    public boolean isSmsDownloadLinkEvent() {
        return SMS_DOWNLOAD_LINK.equals(getEventType());
    }
}
