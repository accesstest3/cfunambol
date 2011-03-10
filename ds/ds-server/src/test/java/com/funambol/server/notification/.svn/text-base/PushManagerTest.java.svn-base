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
package com.funambol.server.notification;

import com.funambol.framework.core.Alert;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationEngine;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.NotificationNotSentException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import junit.framework.TestCase;

/**
 *
 */
public class PushManagerTest extends TestCase {

    public PushManagerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // ------------------------------------------------------------------- Tests

    /**
     * Test of synchronouslySendNotificationMessage method, of class PushManager.
     * @throws Exception If an error occurs
     */
    public void testSynchronouslySendNotificationMessage() throws Exception {

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessage(String username,String deviceId, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        incrementMessagesSent();
                    }
                    public void validate() {
                        assertEquals(1, totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }
        };

        String username = "johndoe";
        String deviceId = "IMEI-1234567890";
        Alert[] alerts = new Alert[]{new Alert()};
        int uimode = 0;

        instance.synchronouslySendNotificationMessage(username, deviceId, alerts, uimode);

        mockNotificationEngine.validate();
    }

    /**
     * Test of asynchronouslySendNotificationMessage method, of class PushManager.
     * @throws Exception If an error occurs
     */
    public void testAsynchronouslySendNotificationMessage() throws Exception {

        final int sendingTimeSeconds = 2;

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessage(String username, String deviceId, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        idleFor(sendingTimeSeconds);
                        incrementMessagesSent();
                    }

                    public void validate() {
                        assertEquals(1, totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }
        };

        String username = "johndoe";
        String deviceId = "IMEI-1234567890";
        Alert[] alerts = new Alert[]{new Alert()};
        int uimode = 0;

        Future<?> future = instance.asynchronouslySendNotificationMessage(username, deviceId, alerts, uimode);

        waitingDeliveryTermination(future, sendingTimeSeconds);

        mockNotificationEngine.validate();
    }

    /**
     * Test of asynchronouslySendNotificationMessage method, of class PushManager.
     * @throws Exception If an error occurs
     */
    public void testAsynchronouslySendNotificationMessage_SendMoreThanOneMessage() throws Exception {

        final int messagesToSend = 100;
        final int sendingTimeSeconds = 2;

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessage(String username, String deviceId, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        idleFor(sendingTimeSeconds);
                        incrementMessagesSent();
                    }

                    public void validate() {
                        assertEquals("All messages have to be sent",messagesToSend, totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }
        };

        Future<?>[] futures = new Future<?>[messagesToSend];
        for (int i = 0; i < messagesToSend; ++i) {
            String username = "johndoe";
            String deviceId = "IMEI-1234567890";
            Alert[] alerts = new Alert[]{new Alert()};
            int uimode = 0;
            futures[i] = instance.asynchronouslySendNotificationMessage(username, deviceId, alerts, uimode);
        }

        Thread.sleep(100);

        // the notification messages deliveries are just started so they are all in progress
        assertEquals(Math.min(instance.getCoreThreadPoolSize(), messagesToSend), instance.getCurrentPushInProgressCount());
        assertEquals(Math.max(0, messagesToSend - instance.getCoreThreadPoolSize()), instance.getQueuedPushCount());

        waitingDeliveryTermination(futures, sendingTimeSeconds);
        assertEquals(0, instance.getCurrentPushInProgressCount());

        mockNotificationEngine.validate();
    }

    /**
     * Test of asynchronouslySendNotificationMessage method, of class PushManager.
     * The allowed threads and the queue capacity is reached before the first
     * request is fulfilled. The exceeding requests are rejected by the
     * MyRejectedExecutionHandler
     * @throws Exception If an error occurs
     */
    public void testAsynchronouslySendNotificationMessage_SmalQueue_SendMoreThanTheMaxPoolSize() throws Exception {

        final int sendingTimeSeconds = 2;

        final int messagesToSend = 100;

        final int queueCapacity =   10;
        final int corePoolSize =    10;
        final int maximumPoolSize = 30;

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessage(String username, String deviceId, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        idleFor(sendingTimeSeconds);
                        incrementMessagesSent();
                    }
                    // the messages actually sent are equal to the sum of the
                    // maximumPoolSize and the queueCapacity. All other messages
                    // are discarded
                    public void validate() {
                        assertEquals(Math.min(messagesToSend, maximumPoolSize + queueCapacity), totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }
        };

        instance.setCoreThreadPoolSize(corePoolSize);
        instance.setMaximumThreadPoolSize(maximumPoolSize);
        instance.setQueueCapacity(queueCapacity);
        instance.init();

        int rejectedMessages = 0;

        Future<?>[] futures = new Future<?>[messagesToSend];
        for (int i = 0; i < messagesToSend; ++i) {
            try {
                String username = "johndoe";
                String deviceId = "IMEI-1234567890";
                Alert[] alerts = new Alert[]{new Alert()};
                int uimode = 0;
                futures[i] = instance.asynchronouslySendNotificationMessage(
                        username, deviceId, alerts, uimode);
            } catch (RejectedExecutionException ex) {
                futures[i] = null;
                ++rejectedMessages;
            }
        }

        waitingDeliveryTermination(futures, sendingTimeSeconds);
        assertEquals(0, instance.getCurrentPushInProgressCount());

        mockNotificationEngine.validate();
        assertEquals(Math.max(0, messagesToSend-maximumPoolSize-queueCapacity), rejectedMessages);
        int numThreads = Math.max(
                Math.min(corePoolSize, messagesToSend),
                Math.min(maximumPoolSize, messagesToSend-queueCapacity));
        assertEquals(numThreads, instance.getThreadPoolSize());
        assertEquals(maximumPoolSize, numThreads);
    }

    /**
     * Test of asynchronouslySendNotificationMessage method, of class PushManager.
     * The allowed threads and the queue capacity is reached before the first
     * request is fulfilled. The exceeding requests are rejected by the
     * MyRejectedExecutionHandler
     * @throws Exception If an error occurs
     */
    public void testAsynchronouslySendNotificationMessage_SmalQueue_SendMoreThanTheQueueCapacityButLessThanTheMaxPoolSize() throws Exception {

        final int sendingTimeSeconds = 2;

        final int messagesToSend =  30;

        final int queueCapacity =   10;
        final int corePoolSize =    10;
        final int maximumPoolSize = 30;

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessage(String username, String deviceId, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        idleFor(sendingTimeSeconds);
                        incrementMessagesSent();
                    }
                    // the messages actually sent are equal to the sum of the
                    // maximumPoolSize and the queueCapacity. All other messages
                    // are discarded
                    public void validate() {
                        assertEquals(Math.min(messagesToSend, maximumPoolSize + queueCapacity), totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }

        };

        instance.setCoreThreadPoolSize(corePoolSize);
        instance.setMaximumThreadPoolSize(maximumPoolSize);
        instance.setQueueCapacity(queueCapacity);
        instance.init();

        int rejectedMessages = 0;

        Future<?>[] futures = new Future<?>[messagesToSend];
        for (int i = 0; i < messagesToSend; ++i) {
            try {
                String username = "johndoe";
                String deviceId = "IMEI-1234567890";
                Alert[] alerts = new Alert[]{new Alert()};
                int uimode = 0;
                futures[i] = instance.asynchronouslySendNotificationMessage(
                        username, deviceId, alerts, uimode);
            } catch (RejectedExecutionException ex) {
                futures[i] = null;
                ++rejectedMessages;
            }
        }

        waitingDeliveryTermination(futures, sendingTimeSeconds);
        assertEquals(0, instance.getCurrentPushInProgressCount());

        mockNotificationEngine.validate();
        assertEquals(Math.max(0, messagesToSend-maximumPoolSize-queueCapacity), rejectedMessages);
        int numThreads = Math.max(
                Math.min(corePoolSize, messagesToSend),
                Math.min(maximumPoolSize, messagesToSend-queueCapacity));
        assertEquals(numThreads, instance.getThreadPoolSize());
        assertEquals(messagesToSend-queueCapacity, numThreads);
    }

    /**
     * Test of asynchronouslySendNotificationMessage method, of class PushManager.
     * The allowed threads and the queue capacity is reached before the first
     * request is fulfilled. The exceeding requests are rejected by the
     * MyRejectedExecutionHandler
     * @throws Exception If an error occurs
     */
    public void testAsynchronouslySendNotificationMessage_SmalQueue_SendLessThanTheQueueCapacity() throws Exception {

        // All messages are sent befor the first is fulfilled
        final int sendingTimeSeconds = 2;

        // the number of messages to send is greater than the corePoolSize but
        // less than the corePoolSize plus the queueCapacity so no new threads
        // are added
        final int messagesToSend =  18;

        final int queueCapacity =   10;
        final int corePoolSize =    10;
        final int maximumPoolSize = 30;

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessage(String username, String deviceId, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        idleFor(sendingTimeSeconds);
                        incrementMessagesSent();
                    }
                    // the messages actually sent are equal to the sum of the
                    // maximumPoolSize and the queueCapacity. All other messages
                    // are discarded
                    public void validate() {
                        assertEquals(Math.min(messagesToSend, maximumPoolSize + queueCapacity), totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }

        };

        instance.setCoreThreadPoolSize(corePoolSize);
        instance.setMaximumThreadPoolSize(maximumPoolSize);
        instance.setQueueCapacity(queueCapacity);
        instance.init();

        int rejectedMessages = 0;

        Future<?>[] futures = new Future<?>[messagesToSend];
        for (int i = 0; i < messagesToSend; ++i) {
            try {
                String username = "johndoe";
                String deviceId = "IMEI-1234567890";
                Alert[] alerts = new Alert[]{new Alert()};
                int uimode = 0;
                futures[i] = instance.asynchronouslySendNotificationMessage(
                        username, deviceId, alerts, uimode);
            } catch (RejectedExecutionException ex) {
                futures[i] = null;
                ++rejectedMessages;
            }
        }

        waitingDeliveryTermination(futures, sendingTimeSeconds);
        assertEquals(0, instance.getCurrentPushInProgressCount());

        mockNotificationEngine.validate();
        assertEquals(Math.max(0, messagesToSend-maximumPoolSize-queueCapacity), rejectedMessages);
        // every new message to send increase the number of threads until the
        // corePoolSize is reached.
        // every message exceeding the corePoolSize is enqueued until the
        // queueCapacity is reached.
        //
        int numThreads = Math.max(
                Math.min(corePoolSize, messagesToSend),
                Math.min(maximumPoolSize, messagesToSend-queueCapacity));
        assertEquals(numThreads, instance.getThreadPoolSize());
        assertEquals(corePoolSize, numThreads);
    }

    public void testAsynchronouslySendNotificationMessage_SmalQueue_SendLessThanTheCorePoolSize() throws Exception {

        // All messages are sent befor the first is fulfilled
        final int sendingTimeSeconds = 2;

        // the number of messages to send is greater than the corePoolSize but
        // less than the corePoolSize plus the queueCapacity so no new threads
        // are added
        final int messagesToSend =   8;

        final int queueCapacity =   10;
        final int corePoolSize =    10;
        final int maximumPoolSize = 30;

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessage(String username, String deviceId, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        idleFor(sendingTimeSeconds);
                        incrementMessagesSent();
                    }
                    // the messages actually sent are equal to the sum of the
                    // maximumPoolSize and the queueCapacity. All other messages
                    // are discarded
                    public void validate() {
                        assertEquals(Math.min(messagesToSend, maximumPoolSize + queueCapacity), totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }

        };

        instance.setCoreThreadPoolSize(corePoolSize);
        instance.setMaximumThreadPoolSize(maximumPoolSize);
        instance.setQueueCapacity(queueCapacity);
        instance.init();

        int rejectedMessages = 0;

        Future<?>[] futures = new Future<?>[messagesToSend];
        for (int i = 0; i < messagesToSend; ++i) {
            try {
                String username = "johndoe";
                String deviceId = "IMEI-1234567890";
                Alert[] alerts = new Alert[]{new Alert()};
                int uimode = 0;
                futures[i] = instance.asynchronouslySendNotificationMessage(
                        username, deviceId, alerts, uimode);
            } catch (RejectedExecutionException ex) {
                futures[i] = null;
                ++rejectedMessages;
            }
        }

        waitingDeliveryTermination(futures, sendingTimeSeconds);
        assertEquals(0, instance.getCurrentPushInProgressCount());

        mockNotificationEngine.validate();
        assertEquals(Math.max(0, messagesToSend-maximumPoolSize-queueCapacity), rejectedMessages);
        // every new message to send increase the number of threads until the
        // corePoolSize is reached.
        // every message exceeding the corePoolSize is enqueued until the
        // queueCapacity is reached.
        //
        int numThreads = Math.max(
                Math.min(corePoolSize, messagesToSend),
                Math.min(maximumPoolSize, messagesToSend-queueCapacity));
        assertEquals(numThreads, instance.getThreadPoolSize());
        assertEquals(messagesToSend, numThreads);
    }

    /**
     * Test of synchronouslySendNotificationMessages method, of class PushManager.
     * @throws Exception If an error occurs
     */
    public void testSynchronouslySendNotificationMessages() throws Exception {

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessages(String username, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        incrementMessagesSent();
                    }
                    public void validate() {
                        assertEquals(1, totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }
        };

        String username = "johndoe";
        Alert[] alerts = new Alert[]{new Alert()};
        int uimode = 0;

        instance.synchronouslySendNotificationMessages(username, alerts, uimode);

        mockNotificationEngine.validate();
    }

    /**
     * Test of asynchronouslySendNotificationMessages method, of class PushManager.
     * @throws Exception If an error occurs
     */
    public void testAsynchronouslySendNotificationMessages() throws Exception {
        final int sendingTimeSeconds = 2;

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessages(String username, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        idleFor(sendingTimeSeconds);
                        incrementMessagesSent();
                    }
                    public void validate() {
                        assertEquals(1, totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }
        };

        String username = "johndoe";
        Alert[] alerts = new Alert[]{new Alert()};
        int uimode = 0;

        Future<?> future = instance.asynchronouslySendNotificationMessages(username, alerts, uimode);

        waitingDeliveryTermination(future, sendingTimeSeconds);

        mockNotificationEngine.validate();
    }

    /**
     * Test of asynchronouslySendNotificationMessage method, of class PushManager.
     * The allowed threads and the queue capacity is reached before the first
     * request is fulfilled. The exceeding requests are rejected by the
     * MyRejectedExecutionHandler
     * @throws Exception If an error occurs
     */
    public void testAsynchronouslySendNotificationMessages_SmalQueue_SendMoreThanOneMessage() throws Exception {

        final int messagesToSend = 100;
        final int sendingTimeSeconds = 2;

        final int queueCapacity = 7;
        final int corePoolSize = 1;
        final int maximumPoolSize = 20;

        final MockHelperNotificationEngine mockNotificationEngine =
                new MockHelperNotificationEngine() {
                    @Override
                    public void sendNotificationMessages(String username, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
                        idleFor(sendingTimeSeconds);
                        incrementMessagesSent();
                    }
                    // the messages actually sent are equal to the sum of the
                    // maximumPoolSize and the queueCapacity. All other messages
                    // are discarded
                    public void validate() {
                        assertEquals(maximumPoolSize + queueCapacity, totalMessageSent);
                    }
                };

        PushManager instance = new PushManager() {
            @Override
            protected NotificationEngine createNotificationEngine() {
                return mockNotificationEngine;
            }

        };

        instance.setCoreThreadPoolSize(corePoolSize);
        instance.setMaximumThreadPoolSize(maximumPoolSize);
        instance.setQueueCapacity(queueCapacity);
        instance.init();

        int rejectedMessages = 0;

        Future<?>[] futures = new Future<?>[messagesToSend];
        for (int i = 0; i < messagesToSend; ++i) {
            try {
                String username = "johndoe";
                Alert[] alerts = new Alert[]{new Alert()};
                int uimode = 0;
                futures[i] = instance.asynchronouslySendNotificationMessages(
                        username, alerts, uimode);
            } catch (RejectedExecutionException ex) {
                futures[i] = null;
                ++rejectedMessages;
            }
        }

        waitingDeliveryTermination(futures, sendingTimeSeconds);

        assertEquals(0, instance.getCurrentPushInProgressCount());
        mockNotificationEngine.validate();
        assertEquals(messagesToSend-maximumPoolSize-queueCapacity, rejectedMessages);
    }

    // --------------------------------------------------------- Private Methods

    private void waitingDeliveryTermination(Future<?> future, int sendingTimeSeconds) throws Exception {
        if (future != null && !future.isDone()) {
            future.get(sendingTimeSeconds + 1, PushManager.DEFAULT_TIME_UNIT);
        }
    }

    private void waitingDeliveryTermination(Future<?>[] futures, int sendingTimeSeconds) throws Exception {
        for (int i = 0; i < futures.length; ++i) {
            waitingDeliveryTermination(futures[i], sendingTimeSeconds);
        }
        Thread.sleep(100);
    }


    // ----------------------------------------------------- Private Inner Class
    private abstract class MockHelperNotificationEngine implements NotificationEngine {

        abstract public void validate();

        public void sendNotificationMessage(String deviceId, Alert[] alerts, int uimode) throws NotificationException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void sendNotificationMessage(String username, String deviceId, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void sendNotificationMessages(String username, Alert[] alerts, int uimode) throws NotificationNotSentException, NotificationException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Message getMessageFromPendingNotifications(String username, String deviceId) throws NotificationException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void deletePendingNotifications(String username, String deviceId, String[] syncSources) throws NotificationException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void idleFor(int sendingTimeSeconds) throws NotificationException {
            try {
                Thread.sleep(sendingTimeSeconds * 1000);
            } catch (InterruptedException ex) {
                throw new NotificationException("", ex);
            }
        }

        protected int totalMessageSent = 0;
        synchronized protected void incrementMessagesSent() {
            ++totalMessageSent;
        }
    }
}
