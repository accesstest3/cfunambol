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
package com.funambol.server.notification.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 * Sync4jAddressListener's test cases
 * @version $Id: Sync4jAddressListenerTest.java 34617 2010-05-26 10:15:21Z dimuro $
 */
public class Sync4jAddressListenerTest extends TestCase {

    private DeviceSimulator deviceSimulator = null;
    
    public Sync4jAddressListenerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        //
        // Stopping device simulator (to be sure the socket is closed)
        //
        if (deviceSimulator != null) {
            deviceSimulator.stop();
        }
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    
    /**
     * Test of getSocketAddress, of class Sync4jAddressListener.
     */
    public void testGetSocketAddress() throws Throwable {
        String data = "127.0.0.1:4745";
        SocketAddress address = (SocketAddress) PrivateAccessor.invoke(Sync4jAddressListener.class,
                "getSocketAddress",
                new Class[]{String.class},
                new Object[]{data});

        InetSocketAddress expectedAddress = new InetSocketAddress("127.0.0.1", 4745);
        assertEquals("Wrong SocketAddress", expectedAddress, address);

        data = "192.168.0.30";
        address = (SocketAddress) PrivateAccessor.invoke(Sync4jAddressListener.class,
                "getSocketAddress",
                new Class[]{String.class},
                new Object[]{data});

        expectedAddress = new InetSocketAddress("192.168.0.30", 745); // default port 745.
        assertEquals("Wrong SocketAddress", expectedAddress, address);

        data = "192.168.0.30:";
        address = (SocketAddress) PrivateAccessor.invoke(Sync4jAddressListener.class,
                "getSocketAddress",
                new Class[]{String.class},
                new Object[]{data});

        expectedAddress = new InetSocketAddress("192.168.0.30", 745); // default port 745.
        assertEquals("Wrong SocketAddress", expectedAddress, address);

        data = null;
        boolean exception = false;
        try {
            address = (SocketAddress) PrivateAccessor.invoke(Sync4jAddressListener.class,
                    "getSocketAddress",
                    new Class[]{String.class},
                    new Object[]{data});
        } catch (Throwable t) {
            if (t instanceof IllegalArgumentException) {
                exception = true;
            }
        }

        assertTrue("Expected an IllegalArgumentException with data=null", exception);

        data = "192.168.0.30:xyz";
        exception = false;
        try {
            address = (SocketAddress) PrivateAccessor.invoke(Sync4jAddressListener.class,
                    "getSocketAddress",
                    new Class[]{String.class},
                    new Object[]{data});
        } catch (Throwable t) {
            if (t instanceof IllegalArgumentException) {
                exception = true;
            }
        }

        assertTrue("Expected an IllegalArgumentException with data '" + data + "'", exception);

        data = "192.168.0.30.155:123";
        expectedAddress = new InetSocketAddress("192.168.0.30.155", 123);

        address = (SocketAddress) PrivateAccessor.invoke(Sync4jAddressListener.class,
                "getSocketAddress",
                new Class[]{String.class},
                new Object[]{data});

        assertEquals("Wrong SocketAddress", expectedAddress, address);

    }

    public void testSendMessage() throws Throwable {

        long expectedResponseTime = 0;
        startDeviceSimulator(5555, expectedResponseTime);

        SocketAddress address = new InetSocketAddress("localhost", 5555);
        //
        // the message ends with '\r' so that the DeviceSimulator can use reaadLine
        //
        byte[] message = "this is the message\r".getBytes();

        PrivateAccessor.invoke(Sync4jAddressListener.class,
                "sendMessage",
                new Class[]{SocketAddress.class, byte[].class},
                new Object[]{address, message});
    }

    public void testSendMessage_readtimeout() throws Throwable {

        long expectedResponseTime = 25 * 1000;
        startDeviceSimulator(5555, expectedResponseTime);

        SocketAddress address = new InetSocketAddress("localhost", 5555);
        //
        // the message ends with '\r' so that the DeviceSimulator can use reaadLine
        //
        byte[] message = "this is the message\r".getBytes();

        long time0 = System.currentTimeMillis();
        boolean readTimeout = false;
        try {
            PrivateAccessor.invoke(Sync4jAddressListener.class,
                    "sendMessage",
                    new Class[]{SocketAddress.class, byte[].class},
                    new Object[]{address, message});

        } catch (java.net.SocketTimeoutException e) {
            String msg = e.getMessage();
            if ("Read timed out".equals(msg)) {
                readTimeout = true;
            }
        }
        assertTrue("Expected a SocketTimeoutException", readTimeout);
        long exec = System.currentTimeMillis() - time0;
        assertTrue("Expected a SocketTimeoutException in about 20s (caught in " + exec + " ms)",
                   18 * 1000 < exec && exec < 35 * 1000); // unfortunalety on some systems the error can occur in 30 sec or more..
    }

    public void testSendMessage_unknown_host() throws Throwable {

        long expectedResponseTime = 25 * 1000;
        startDeviceSimulator(5555, expectedResponseTime);

        SocketAddress address = new InetSocketAddress("www.abcfde.rr", 4444);
        //
        // the message ends with '\r' so that the DeviceSimulator can use reaadLine
        //
        byte[] message = "this is the message\r".getBytes();
        
        boolean exc = false;
        try {
            PrivateAccessor.invoke(Sync4jAddressListener.class,
                    "sendMessage",
                    new Class[]{SocketAddress.class, byte[].class},
                    new Object[]{address, message});

        } catch (java.net.UnknownHostException e) {
            exc = true;
        }
        assertTrue("Expected a UnknownHostException", exc);
    }

    // --------------------------------------------------------- Private methods

    private void startDeviceSimulator(int port, long expectedResponseTime)
    throws Exception {
        deviceSimulator = new DeviceSimulator(port, expectedResponseTime);
        deviceSimulator.start();
        Thread.sleep(1000); // waiting a bit for the device simulator
    }
}

class DeviceSimulator {

    private int port = 4745; // default
    private long responseTime = 0;

    private Server server = null;
    private Thread serverThread = null;
    
    public DeviceSimulator(int p, long responseTime) {
        this.port = p;
        this.responseTime = responseTime;
    }

    public void start() {
        server = new Server(port, responseTime);
        serverThread = new Thread(server);
        serverThread.start();
    }

    public void stop() {
        server.stop();
        serverThread.stop();
    }

    class Server implements Runnable {

        private int port = 4745; // default
        private long responseTime = 0;

        public Server(int port, long responseTime) {
            this.port = port;
            this.responseTime = responseTime;
        }

        public void run() {
            Socket socket = null;
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                }
            }

            OutputStream os = null;
            try {
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String msg = br.readLine();

                Thread.sleep(responseTime);

                os = socket.getOutputStream();
                os.write("this is the response \r".getBytes());
            } catch (Exception ex) {
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException ex) {
                }
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        private void stop() {
            
        }
    }
}