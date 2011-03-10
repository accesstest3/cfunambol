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
package com.funambol.server.notification;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.funambol.framework.core.Alert;
import com.funambol.framework.core.CmdID;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Source;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.LogContext;
import com.funambol.framework.logging.Sync4jLoggerName;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationEngine;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.NotificationNotSentException;
import com.funambol.framework.notification.builder.DSNotificationMessageBuilder;
import com.funambol.framework.notification.sender.Sender;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.PushFlowEvent;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.DbgTools;

import com.funambol.server.config.Configuration;

/**
 * This class is an implementation of NotificationEngine interface
 *
 * @see NotificationEngine
 *
 * @version $Id: NotificationEngineImpl.java,v 1.2 2008-03-04 22:13:00 stefano_fornari Exp $
 */
public class NotificationEngineImpl implements NotificationEngine {

    // --------------------------------------------------------------- Constants
    /**
     * Regular expression used to identify the notificable devices.
     * Note that a notification request for a not notificable devices is processed
     * and if the builder and the sender are ok, the notification message will be
     * delivered. But if an error occurs in the notification process, no
     * DS_PUSH_SENT_EXC event is logged.
     * When this regular expression will be configurable, it could be used really
     * to identify the devices that must not be processed and not just to avoid
     * DS_PUSH_SENT_EXC event.
     * At the moment just the WM plugin and the JAM client are notificable.
     */
    private String NOTIFICABLE_DEVICES_REG_EXPR =
        "fwm-[\\p{ASCII}]*|fjm-[\\p{ASCII}]*";

    // ------------------------------------------------------------ Private data
    private static final FunambolLogger log =
        FunambolLoggerFactory.getLogger(Sync4jLoggerName.SERVER_NOTIFICATION);
    private static final FunambolLogger logPush =
        FunambolLoggerFactory.getLogger("push");

    private PendingNotificationDAO pendingNotificationDAO = null;

    // ------------------------------------------------------------- Constructor
    public NotificationEngineImpl() {
        pendingNotificationDAO = new PendingNotificationDAO();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Sends a notification message to the given device.
     *
     * @param username the username. Note that this parameter is not actively
     *                 used at the moment; it's just used in the log and it
     *                 doesn't affect the behaviour.
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     */
    public void sendNotificationMessage(String  username,
                                        String  deviceId,
                                        Alert[] alerts  ,
                                        int     uimode  )
    throws NotificationException {

        LogContext.setUserName(username);
        LogContext.setDeviceId(deviceId);

        String sourceURIs = concatenateSourceURIs(alerts);
        LogContext.setSourceURI(sourceURIs);

        if (log.isTraceEnabled()) {
            log.trace("Processing push request");
        }

        try {

            processingNotificationRequest(username, deviceId, alerts, uimode);

        } finally {
            LogContext.setDeviceId(null);
            LogContext.setSourceURI(null);
            LogContext.setUserName(null);
        }
    }

    /**
     * Sends a notification message to the given device.
     *
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     *
     * @deprecated Since v71 use sendNotificationMessage(String username,
     *             String deviceId, Alert[] alerts, int uimode)
     */
    public void sendNotificationMessage(String  deviceId,
                                        Alert[] alerts  ,
                                        int     uimode  )
    throws NotificationException {
        processingNotificationRequest(deviceId, alerts, uimode);
    }

    /**
     * Sends a notification message to all devices of the principals with the
     * given username.
     *
     * @param username the username
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     */
    public void sendNotificationMessages(String  username,
                                         Alert[] alerts  ,
                                         int     uimode  )
    throws NotificationException {

        LogContext.setUserName(username);

        String sourceURIs = concatenateSourceURIs(alerts);
        LogContext.setSourceURI(sourceURIs);

        if (log.isTraceEnabled()) {
            log.trace("Processing push request");
        }

        //
        // Be carefully when you change this log because is used to notify the
        // ds notification processing event.
        //
        if (logPush.isTraceEnabled()) {
            logPush.trace(
                PushFlowEvent.createDSPushReqEvent(username,
                                                   null,
                                                   LogContext.getSessionId(),
                                                   sourceURIs,
                                                   "Processing push request")
            );
        }

        Clause clause = new WhereClause("username",
                                        new String[] {username},
                                        WhereClause.OPT_EQ,
                                        true);

        Sync4jPrincipal[] principals = null;
        try {
            principals =
                (Sync4jPrincipal[])Configuration.getConfiguration().getStore().read(new Sync4jPrincipal(), clause);

        } catch (PersistentStoreException e) {
            log.error("Error reading the devices that should be notified", e);

            //
            // Be carefully when you change this log because is used to notify
            // the ds notification processing event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createDSPushReqEventOnError(
                        username,
                        null,
                        LogContext.getSessionId(),
                        sourceURIs,
                        "Error reading the devices that should be notified"), e
                );
            }

            throw new NotificationException("Error reading the devices that should be notified: " +
                                            e.getMessage());
        } finally {
            //
            // In this way the username is removed from the logging context in
            // any case (for instance a not handled exception like a
            // RuntimeException)
            //
            LogContext.setUserName(null);
            LogContext.setSourceURI(null);
        }

        //
        // Set username and sourceURI in the logging context
        //
        LogContext.setUserName(username);
        LogContext.setSourceURI(sourceURIs);

        if (principals == null || principals.length == 0) {
             if (log.isTraceEnabled()) {
                 log.trace("No principal found with username '" + username + "'");
                 log.trace("No devices to notify");
             }

            //
            // Be carefully when you change this log because is used to notify the
            // ds notification processing event.
            //
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createDSPushReqEventOnError(
                        username,
                        null,
                        LogContext.getSessionId(),
                        sourceURIs,
                        "No devices to notify")
                );
            }

            LogContext.setUserName(null);
            LogContext.setSourceURI(null);
            return;
        }

        int num = principals.length;
        //
        // We collect the notification result messages in order to log
        // (at the end) the result for every devices.
        // Note that the errors occurred trying to notify a not
        // notificable device are not tracked.
        //
        List<String> notificationResults = new ArrayList<String>();

        //
        // The notification will be in error if at least one notification
        // to a notificable device fails
        //
        boolean notificationInError = false;

        //
        // used to indicate that at least one notificable device has been found
        //
        boolean notificableDeviceFound = false;
        for (int i=0; i<num; i++) {
            String deviceId = null;
            boolean notificableDevice = false;
            try {
                deviceId = principals[i].getDeviceId();

                LogContext.setDeviceId(deviceId);

                notificableDevice = isANotificableDevice(deviceId);
                if (notificableDevice) {
                    notificableDeviceFound = true;
                }

                processingNotificationRequest(username, principals[i].getDeviceId(), alerts, uimode);

                //
                // Maybe the device was not recognized as notificable, but a notification
                // message was delivered so it is considered a notificable device.
                //
                notificableDeviceFound = true;
                notificationResults.add("    > " + principals[i].getDeviceId() + ": notified");
            } catch (Throwable e) {
                //
                // The errors that occur trying to notify a not
                // notificable device are not tracked.
                //
                if (notificableDevice) {
                    notificationResults.add("    > " + principals[i].getDeviceId() + ": " + e.getMessage());
                    notificationInError = true;
                }

            } finally {
                LogContext.setDeviceId(null);
            }
        }

        StringBuilder notificationResult = new StringBuilder();
        int cont = 0;
        for (String result : notificationResults) {
            if (cont++ != 0) {
                notificationResult.append("\r\n");
            }
            notificationResult.append(result);
        }

        //
        // Be carefully when you change this log because is used to notify the
        // ds notification delivering event.
        //
        if (notificableDeviceFound) {
            if (notificationInError) {
                if (logPush.isTraceEnabled()) {
                    logPush.trace(
                        PushFlowEvent.createDSPushSentEventOnError(
                            username,
                            null,
                            LogContext.getSessionId(),
                            null,
                            "Notification result:\r\n" + notificationResult.toString())
                    );
                }
            } else {
                if (logPush.isTraceEnabled()) {
                    logPush.trace(
                        PushFlowEvent.createDSPushSentEvent(
                            username,
                            null,
                            LogContext.getSessionId(),
                            null,
                            "Notification result:\r\n" + notificationResult.toString())
                    );
                }
            }
        } else {
            if (logPush.isTraceEnabled()) {
                logPush.trace(
                    PushFlowEvent.createDSPushSentEventOnError(
                        username,
                        null,
                        LogContext.getSessionId(),
                        null,
                        "No device to notify")
                );
            }
        }
        LogContext.setUserName(null);
        LogContext.setSourceURI(null);
    }

    /**
     * Retrieves a notification message created with the pending notifications
     * stored in the datastore.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @return the notification message
     * @throws NotificationException if an error occurs
     */
    public Message getMessageFromPendingNotifications(String username,
                                                      String deviceId)
    throws NotificationException {
        Message message = null;

        List<PendingNotification> pendingNotifications =
            getPendingNotifications(username, deviceId);

        message = buildMessageFromPendingNotifications(deviceId,
                                                       pendingNotifications);
        return message;
    }

    /**
     * Deletes the notification stored in the datastore after that they are
     * delivered rightly.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @param syncSources the array of SyncSources to notify
     * @throws NotificationException if an error occurs
     */
    public void deletePendingNotifications(String  username   ,
                                           String   deviceId   ,
                                           String[] syncSources)
    throws NotificationException {
        try {
            pendingNotificationDAO.deleteBySyncSources(username, deviceId, syncSources);
        } catch (PendingNotificationDBAccessException e) {
            log.error(e.getMessage(), e);
            throw new NotificationException(e.getMessage(), e);
        }
    }

    // --------------------------------------------------------- Private Methods
    /**
     * Calls, using reflection, the sendNotificationMessage according to the
     * previous sender interface (the one in v3) that declared
     * sendNotificationMessage(Sync4jDevice, byte[]).
     *
     * @param sender the sender
     * @param device the device
     * @param message the message to send
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     */
    private void handleOldSenderInstance(Sender sender, Sync4jDevice device, Message message)
    throws NotificationException {
        //
        // Using reflection to handle the old implementations
        // of the Sender
        //
        byte[] messageContent = message.getMessageContent();

        Method  method         = null;
        try {
            method = sender.getClass().getMethod("sendNotificationMessage",
                                                  new Class[] {Sync4jDevice.class, byte[].class}
                     );

            method.invoke(sender, new Object[] {device, messageContent});

        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (target instanceof NotificationException) {
               throw (NotificationException)target;
            }

            throw new NotificationException(
                "Error invoking 'sendNotificationMessage' method", target
            );
        } catch (Throwable e) {
            throw new NotificationException(
                "Error invoking 'sendNotificationMessage' method", e
            );

        }
    }

    /**
     * Calls, using reflection, the buildMessage according to the previous builder interface
     * (the one in v3) that declared:
     * <p><code>
     *    byte[] buildMessage(int     sessionId      ,
     *                        String  deviceAddress  ,
     *                        String  serverId       ,
     *                        String  serverPw       ,
     *                        byte[]  serverNonce    ,
     *                        Alert[] alerts         ,
     *                        int     uimode         ,
     *                        float   protocolVersion)
     * </code>
     *
     */
    private Message handleOldBuilderInstance(DSNotificationMessageBuilder builder,
                                             int     sessionId      ,
                                             String  deviceAddress  ,
                                             String  serverId       ,
                                             String  serverPw       ,
                                             byte[]  serverNonce    ,
                                             Alert[] alerts         ,
                                             int     uimode         ,
                                             float   protocolVersion)

    throws NotificationException {
        //
        // Using reflection to handle the old implementations
        // of the Builder
        //
        Method  method         = null;
        byte[]    messageContent = null;
        try {
            method = builder.getClass().getMethod("buildMessage",
                                                  new Class[] {
                                                      Integer.TYPE,
                                                      String.class,
                                                      String.class,
                                                      String.class,
                                                      byte[].class,
                                                      Alert[].class,
                                                      Integer.TYPE,
                                                      Float.TYPE
                                                  }
                     );

            messageContent = (byte[])method.invoke(builder,
                                                   new Object[] {
                                                       Integer.valueOf(sessionId),
                                                       deviceAddress,
                                                       serverId,
                                                       serverPw,
                                                       serverNonce,
                                                       alerts,
                                                       Integer.valueOf(uimode),
                                                       new Float(protocolVersion)
                                                   }
                               );

        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (target instanceof NotificationException) {
               throw (NotificationException)target;
            }

            throw new NotificationException(
                "Error invoking 'buildMessage' method", target
            );
        } catch (Throwable e) {
            throw new NotificationException(
                "Error invoking 'buildMessage' method", e
            );

        }
        return new Message(Message.Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE,
                           messageContent);
    }

    /**
     * Sends a notification message to the given device without setting anything
     * in the LogContext for logging purpose. If it is needed, it must be done
     * by the caller. The given alerts will be merged with the pending push
     * notifications stored in the datastore.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @param alerts   an array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     */
    private void processingNotificationRequest(String  username,
                                               String  deviceId,
                                               Alert[] alerts  ,
                                               int     uimode  )
    throws NotificationException {

        long deliveredTime = System.currentTimeMillis();

        //
        // If there are some pending push notifications, the notification message
        // must contain the new notifications and the old ones.
        //
        List<PendingNotification> pendingNotifications =
            getPendingNotifications(username, deviceId);
        Alert[] mergedAlerts =
            fixAlertsUsingPendingNotifications(alerts, pendingNotifications);

        Sync4jDevice device;
        try {
            device = getDeviceFromId(deviceId);
        } catch (Exception e) {
            throw new NotificationException(e.getMessage(), e);
        }

        Message message = buildMessage(device, mergedAlerts, uimode);

        Sender sender = null;
        String notificationSender = device.getNotificationSender();

        if (notificationSender == null || "".equals(notificationSender)) {
            if (log.isTraceEnabled()) {
                log.trace("No notification sender configured for '" +
                          deviceId + "'");
            }
            throw new NotificationException("No notification sender configured for '" + deviceId + "'");
        }

        try {
            sender = (Sender)
                Configuration.getConfiguration().getBeanInstanceByName(notificationSender);
        } catch (Exception e) {
            log.error("Error creating the sender '" + notificationSender +
                      "'", e);
            throw new NotificationException(e.getMessage());
        }

        try {
            //
            // Sending message
            //
            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder("Sending message '");
                sb.append(DbgTools.bytesToHex(message.getMessageContent()));
                sb.append("' to '").append(device).append("'");

                log.trace(sb.toString());
            }

            sender.sendNotificationMessage(device, message);
        } catch (java.lang.AbstractMethodError err) {
            if (log.isWarningEnabled()) {
                log.warn("The configured sender (" +
                         notificationSender + ") doesn't seem a valid sender." +
                         " Trying to use the v3 interface");
            }

            //
            // Using reflection to handle previous officer version (v3)
            //
            handleOldSenderInstance(sender, device, message);
        } catch (NotificationNotSentException e) {
            //
            // Since this is not an error, the exception message is logged just
            // a trace (if the server is configured at INFO, we don't want a lot
            // of "...message not sent since the device is not reachable" log
            // messages with level ERROR)
            //
            if (log.isTraceEnabled()) {
                log.trace("Notification message not sent since: " + e.getMessage());
            }

            //
            // If there is an error during the sending of the notification,
            // then the not delivered notification has to be stored in the
            // datastore.
            //
            addPendingNotification(username, deviceId, alerts, uimode, deliveredTime);

            throw new NotificationNotSentException("Notification message not sent since: " + e.getMessage());
        } catch (Throwable e) {
            log.error("Notification message not sent since: " + e.getMessage());

            //
            // If there is an error during the sending of the notification,
            // then the not delivered notification has to be stored in the
            // datastore.
            //
            addPendingNotification(username, deviceId, alerts, uimode, deliveredTime);

            throw new NotificationException("Notification message not sent since: " + e.getMessage());
        }

        try {
            // Deletes the delivered notification (that were pending)
            pendingNotificationDAO.deleteBySyncSources(username,
                                                       deviceId,
                                                       message.getSyncSources());
        } catch (PendingNotificationDBAccessException e) {
            log.error(e.getMessage(), e);
            throw new NotificationException(e.getMessage(), e);
        }
    }

    /**
     * Sends a notification message to the given device without setting anything
     * in the LogContext for logging purpose. If it is needed, it must be done
     * by the caller. This method should be called only if the username is null.
     * If the sender is not able to delivery the push notification, it will be
     * lost. In this case, if the NotificationEngine is not able to deliver the
     * push notification message, nothing must be stored in the push history and
     * the push is definitely lost.
     *
     * @param deviceId the device identifier
     * @param alerts   an array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     *
     * @deprecated Since v71 use processingNotificationRequest(String username,
     *             String deviceId, Alert[] alerts, int uimode)
     */
    private void processingNotificationRequest(String  deviceId,
                                               Alert[] alerts  ,
                                               int     uimode  )
    throws NotificationException {

        Sync4jDevice device;
        try {
            device = getDeviceFromId(deviceId);
        } catch (Exception e) {
            throw new NotificationException(e.getMessage(), e);
        }

        Message message = buildMessage(device, alerts, uimode);

        Sender sender = null;
        String notificationSender = device.getNotificationSender();

        if (notificationSender == null || "".equals(notificationSender)) {
            if (log.isTraceEnabled()) {
                log.trace("No notification sender configured for '" +
                          deviceId + "'");
            }
            throw new NotificationException("No notification sender configured for '" + deviceId + "'");
        }

        try {
            sender = (Sender)
                Configuration.getConfiguration().getBeanInstanceByName(notificationSender);
        } catch (Exception e) {
            log.error("Error creating the sender '" + notificationSender +
                      "'", e);
            throw new NotificationException(e.getMessage());
        }

        try {
            //
            // Sending message
            //
            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder("Sending message '");
                sb.append(DbgTools.bytesToHex(message.getMessageContent()));
                sb.append("' to '").append(device).append("'");

                log.trace(sb.toString());
            }

            sender.sendNotificationMessage(device, message);
        } catch (java.lang.AbstractMethodError err) {
            if (log.isWarningEnabled()) {
                log.warn("The configured sender (" +
                         notificationSender + ") doesn't seem a valid sender." +
                         " Trying to use the v3 interface");
            }

            //
            // Using reflection to handle previous officer version (v3)
            //
            handleOldSenderInstance(sender, device, message);
        } catch (NotificationNotSentException e) {
            //
            // Since this is not an error, the exception message is logged just
            // a trace (if the server is configured at INFO, we don't want a lot
            // of "...message not sent since the device is not reachable" log
            // messages with level ERROR)
            //
            if (log.isTraceEnabled()) {
                log.trace("Notification message not sent since: " + e.getMessage());
            }
            throw new NotificationNotSentException("Notification message not sent since: " + e.getMessage());
        } catch (Throwable e) {
            log.error("Notification message not sent since: " + e.getMessage());
            throw new NotificationException("Notification message not sent since: " + e.getMessage());
        }
    }

    /**
     * Creates a string concatenating the sourceuri contained in the given alert
     * array.
     *
     * @param alerts the alerts
     * @return a string obtained concatenating the sourceuri contained in the
     *         given alert array using '|' as separator.
     */
    private String concatenateSourceURIs(Alert[] alerts) {
        StringBuilder sb = new StringBuilder();
        int cont = 0;
        for (Alert alert: alerts) {
            if (cont++ > 0) {
                sb.append('|');
            }
            sb.append(((Item)alert.getItems().get(0)).getTarget().getLocURI());
        }
        return sb.toString();
    }

    /**
     * Creates an array of sourceuri contained in the given alert array.
     *
     * @param alerts the alerts
     * @return the array of the sourceuri contained in the given alert array
     */
    private String[] getSourceURIsList(Alert[] alerts) {
        List syncSources = new ArrayList();
        for (Alert alert: alerts) {
            for (Item item: (List<Item>)alert.getItems()) {
                if (item.getTarget() != null) {
                    syncSources.add(item.getTarget().getLocURI());
                } else {
                    syncSources.add(item.getSource().getLocURI());
                }
            }
        }
        return (String[])syncSources.toArray(new String[syncSources.size()]);
    }

    /**
     * Returns true if the given deviceId represents a notificable device, false
     * otherwise. The notificable devices are the one that match with
     * NOTIFICABLE_DEVICES_REG_EXPR.
     *
     * @param deviceId the device id
     * @return true if the given deviceId represents a notificable device, false
     *         otherwise
     */
    private boolean isANotificableDevice(String deviceId) {
        return Pattern.matches(NOTIFICABLE_DEVICES_REG_EXPR, deviceId);
    }

    /**
     * Retrieves the not delivered push notifications for the given username
     * and device.
     *
     * @param username the user identifier
     * @param deviceId the device identifier
     * @return the array of not delivered push notifications for the given
     *         username and deviceId
     * @throws NotificationException if an error occurs
     */
    private List<PendingNotification> getPendingNotifications(String username,
                                                              String deviceId)
    throws NotificationException {

        List<PendingNotification> pendingNotifications = null;

        try {
            pendingNotifications =
                pendingNotificationDAO.getByUsernameDevice(username, deviceId);
        } catch (PendingNotificationDBAccessException e) {
            log.error(e.getMessage(), e);
            throw new NotificationException(e.getMessage(), e);
        }
        return pendingNotifications;
    }

    /**
     * Stores not delivered notification.
     *
     * @param username the username. Note that this parameter is not actively
     *                 used at the moment; it's just used in the log and it
     *                 doesn't affect the behaviour.
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @param deliveredTime the time in which the sender has tried to send the
     *        notification
     */
    private void addPendingNotification(String  username     ,
                                        String  deviceId     ,
                                        Alert[] alerts       ,
                                        int     uimode       ,
                                        long    deliveredTime) {

        if (log.isTraceEnabled()) {
            log.trace("Storing the not delivered notification into datastore.");
        }

        PendingNotification notDeliveredNotification = new PendingNotification();
        notDeliveredNotification.setUserId(username);
        notDeliveredNotification.setDeviceId(deviceId);
        notDeliveredNotification.setUimode(uimode);
        notDeliveredNotification.setDeliveryTime(deliveredTime);

        for (Alert alert: alerts) {

            notDeliveredNotification.setSyncType(alert.getData());

            List<Item> items = alert.getItems();
            for (Item item: items) {
                notDeliveredNotification.setSyncSource(item.getSource().getLocURI());
                notDeliveredNotification.setContentType(item.getMeta().getType());

                try {
                    pendingNotificationDAO.add(notDeliveredNotification);
                } catch (PendingNotificationDBAccessException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Builds Message object from the pending push notifications stored in the
     * datastore.
     *
     * @param deviceId the device identifier
     * @param pendingNotifications the list of PendingNotification object
     * @return the message
     * @throws NotificationException if an error occurs
     */
    private Message buildMessageFromPendingNotifications(String deviceId     ,
                               List<PendingNotification> pendingNotifications)
    throws NotificationException {

        if (pendingNotifications == null || pendingNotifications.isEmpty()) {
            return null;
        }

        Message message = null;

        Sync4jDevice device;
        try {
            device = getDeviceFromId(deviceId);
        } catch (Exception e) {
            throw new NotificationException(e.getMessage(), e);
        }

        int uimode = 0;
        Alert[] alerts = new Alert[pendingNotifications.size()];
        PendingNotification pendingNotification = null;

        for (int i=0; i<pendingNotifications.size(); i++) {
            pendingNotification = pendingNotifications.get(i);
            alerts[i] = createAlertFromPendingNotification(pendingNotification);

            //
            // Gets the greatest user interaction mode
            //
            if (pendingNotification.getUimode() > uimode) {
                uimode = pendingNotification.getUimode();
            }
        }

        message = buildMessage(device, alerts, uimode);

        return message;
    }

    /**
     * Creates an Alert from the given pending notification.
     *
     * @param pendingNotification the pending notifications
     * @return an Alert from the pending notification
     */
    private Alert createAlertFromPendingNotification(PendingNotification pn) {

        Source source = new Source(pn.getSyncSource());
        Meta meta = new Meta();
        meta.setType(pn.getContentType());
        Item item = new Item(source, null, meta, null, false);

        return new Alert(new CmdID(0)    ,
                         false           ,
                         null            ,
                         pn.getSyncType(),
                         new Item[]{item});
    }

    /**
     * Returns an array of Alert that contains the Alerts for the new source to
     * sync and the Alerts for the pending notification. For instance, if there
     * is an Alert for 'mail' and there is a pending notification for 'card',
     * the final array of Alerts will contain both sources.
     *
     * @param alerts the list of Alert never sent
     * @param pendingNotifications the list of pending push notification
     * @return an array of merged Alert
     * @throws NotificationException if an error occurs
     */
    private Alert[] fixAlertsUsingPendingNotifications(
                                Alert[]                   alerts              ,
                                List<PendingNotification> pendingNotifications)
    throws NotificationException {

        List alertsList = new ArrayList();
        alertsList.addAll(Arrays.asList(alerts));

        try {
            if (pendingNotifications != null && !pendingNotifications.isEmpty()) {

                ArrayList<Item> items = null;
                String locUri = null;

                for (PendingNotification pendingNotification: pendingNotifications) {
                    String syncSource = pendingNotification.getSyncSource();
                    boolean found = false;

                    for (Alert alert: alerts) {
                        items = alert.getItems();

                        for (Item item: items) {
                            if (item.getTarget() != null) {
                                locUri = item.getTarget().getLocURI();
                            } else {
                                locUri = item.getSource().getLocURI();
                            }
                            if (syncSource.equalsIgnoreCase(locUri)) {
                                found = true;
                                continue;
                            }

                        }
                    }
                    if (!found) {
                        // create Alert for pending notification
                        Alert alert =
                            createAlertFromPendingNotification(pendingNotification);
                        alertsList.add(alert);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error merging push notifications", e);
            throw new NotificationException(
                "Error merging push notification: " + e.getMessage());
        }

        Alert[] mergedAlerts =
            (Alert[])alertsList.toArray(new Alert[alertsList.size()]);

        return mergedAlerts;
    }

    /**
     * Builds the Message with the given list of Alert.
     *
     * @param device the Sync4jDevice object
     * @param alerts an array of Alert command for the datastore to sync, used
     *               to create the binary message
     * @param uimode user interaction mode
     * @return the message
     * @throws NotificationException if an error occurs
     */
    private Message buildMessage(Sync4jDevice device,
                                 Alert[]      alerts,
                                 int          uimode)
    throws NotificationException {

        Message message = null;
        DSNotificationMessageBuilder builder = null;
        int sessionId = 0;

        String deviceId = device.getDeviceId();

        String notificationBuilder = device.getNotificationBuilder();
        if (notificationBuilder == null || "".equals(notificationBuilder)) {
            if (log.isTraceEnabled()) {
                log.trace("No notification builder configured for '" + deviceId + "'");
            }
            throw new NotificationException(
                "No notification builder configured for '" + deviceId + "'");
        }

        try {
            builder = (DSNotificationMessageBuilder)
                Configuration.getConfiguration().getBeanInstanceByName(notificationBuilder);
        } catch (Exception e) {
            log.error("Error creating the builder '" + notificationBuilder +
                      "'", e);
            throw new NotificationException(e.getMessage());
        }

        String serverId =
            Configuration.getConfiguration().getServerConfig().getServerInfo().getDevID();

        Float protocolVersion = new Float(1.2);

        // Builds message
        String deviceAddress = device.getAddress();
        String serverPWD     = device.getServerPassword();
        byte[] serverNonce   = device.getServerNonce();
        try {
            message = builder.buildMessage(sessionId                   ,
                                           deviceId                    ,
                                           deviceAddress               ,
                                           serverId                    ,
                                           serverPWD                   ,
                                           serverNonce                 ,
                                           alerts                      ,
                                           uimode                      ,
                                           protocolVersion.floatValue());
        } catch (java.lang.AbstractMethodError err) {
            if (log.isWarningEnabled()) {
                log.warn("The configured builder (" +
                         notificationBuilder + ") doesn't seem a valid builder." +
                         " Trying to use the v3 interface");
            }
            //
            // Using reflection to handle previous officer version (v3)
            //
            message = handleOldBuilderInstance(builder                     ,
                                               sessionId                   ,
                                               deviceAddress               ,
                                               serverId                    ,
                                               serverPWD                   ,
                                               serverNonce                 ,
                                               alerts                      ,
                                               uimode                      ,
                                               protocolVersion.floatValue());
        } catch (Throwable e) {
            log.error("Error creating notification message", e);
            throw new NotificationException(
                "Error creating notification message: " + e.getMessage());
        }

        //
        // Sets the SyncSources into message in order to handle delete
        // from the datastore the pending push notifications that are delivered.
        //
        message.setSyncSources(getSourceURIsList(alerts));

        return message;
    }

    /**
     * Returns the Sync4jDevice object after checking if the given deviceId
     * exists.
     *
     * @param deviceId the device identifier
     * @return the Sync4jDevice object for given device identifier
     * @throws Exception if an error occurs
     */
    private Sync4jDevice getDeviceFromId(String deviceId)
    throws Exception {

        Sync4jDevice device = new Sync4jDevice(deviceId);
        boolean deviceFound = false;
        try {
            if (device == null) {
                throw new IllegalArgumentException("The device must be not null");
            }
            deviceFound = Configuration.getConfiguration().getDeviceInventory().getDevice(device, false);
        } catch(DeviceInventoryException e) {
            log.error("Error reading the device '" + deviceId + "'", e);
            throw new Exception("Error reading device '" + deviceId + "'", e);
        }
        if (!deviceFound) {
            if (log.isTraceEnabled()) {
                log.trace("Device '" + deviceId + "' not found");
            }
            throw new Exception("Device '" + deviceId + "' not found");
        }
        return device;
    }
}
