/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.pushlistener.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Simple tools to work with the MBean exposed by the pushlistener.
 * It provides a way to stop the pushlistener calling "stop" operation and a
 * way to see the pushlistener status getting the attribute "Status".
 *
 * @version $Id: ManagementTools.java,v 1.7 2007-11-28 11:14:43 nichele Exp $
 */
public class ManagementTools {

    // --------------------------------------------------------------- Constants
    /** Command to use to stop the PushListener */
    private static final String COMMAND_STOP   = "stop";

    /** Command to see the PushListener status */
    private static final String COMMAND_STATUS = "Status";

    /** Operation to call to stop the pushlistener */
    private static final String OPERATION_STOP   = "stop";

    /** Attribute representing the pushlistener status */
    private static final String ATTRIBUTE_STATUS = "Status";

    // ------------------------------------------------------------ Private data

    /** MBeanServerConnection */
    private MBeanServerConnection mbsc       = null;

    /** ObjectName to work with the mbean pushlistener */
    private ObjectName objectName = null;

    /** Host name */
    private String host = null;

    /** Port */
    private int port = 0;

    /** Username */
    private String userName = null;

    /** Password */
    private String password = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new ManagementTools
     * @param mbeanName the mbean name
     * @param host the host name
     * @param port the port number
     * @param userName the user name
     * @param password the password
     * @throws java.lang.Exception if an error occurs
     */
    public ManagementTools(String mbeanName,
                           String host,
                           int port, 
                           String userName, 
                           String password)
    throws Exception {

        this.host      = host;
        this.port      = port;
        this.userName  = userName;
        this.password  = password;

        objectName = new ObjectName(mbeanName);
        mbsc       = getMBeanServerConnection();
    }

    /**
     * Runs ManagementTools
     * @param args arguments
     */
    public static void main(String[] args) {
        int numArgs = args.length;
        if (args.length > 0) {
            if (args[0].equals("-h")    ||
                args[0].equals("-help") ||
                args[0].equals("-?")    ||
                args[0].equals("/?")     ) {

                usage();
                return;
            }
        }

        if (numArgs != 4 && numArgs != 6) {
                usage();
                return;
        }

        String mbeanName = args[0];
        
        String host  = args[1];
        String sPort = args[2];
        int    port  = 0;
        try {
            port = Integer.parseInt(sPort);
        } catch (NumberFormatException e) {
            System.out.println("Port number not valid.");
            System.exit(-1);
        }

        String command  = args[numArgs - 1];
        String password = null;
        String userName = null;
        if (numArgs == 6) {
            password = args[numArgs - 2];
            userName = args[numArgs - 3];
        }

        ManagementTools tools = null;
        try {
            tools = new ManagementTools(mbeanName, host, port, userName, password);
        } catch (IOException ex) {
            System.out.println("Unable to connect to " + host + ":" + port);
            System.exit(-1);
        } catch (Exception ex) {
            System.err.println("Error creating ManagementTools");
            ex.printStackTrace(System.err);
            System.exit(-1);
        }

        if (COMMAND_STOP.equalsIgnoreCase(command)) {
            tools.stop();
        } else if (COMMAND_STATUS.equalsIgnoreCase(command)) {
            tools.status();
        }

    }

    /**
     * Shows ManagementTools usage
     */
    private static void usage() {
        System.err.println("Usage: java com.funambol.pushlistener.service.ManagementTools " +
                           "mbeanName host port [username] [password] stop|status");
    }

    /**
     * Stops the PushListener calling "stop" operation
     */
    private void stop() {
        try {
            mbsc.invoke(objectName, OPERATION_STOP, null, null);
        } catch (java.rmi.UnmarshalException ue) {
            Throwable t = ue.getCause();
            if (t instanceof SocketException ||
                t instanceof java.io.EOFException) {
                // this means the pushlistener shut down
            } else {
                ue.printStackTrace(System.err);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Shows PushListener status (attribute "Status")
     */
    private void status() {
        try {
            String status = (String)mbsc.getAttribute(objectName, ATTRIBUTE_STATUS);
            System.out.println(status);

        }  catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Returns a MBeanServerConnection
     * @throws java.lang.Exception if an error occurs
     * @return MBeanServerConnection
     */
    private MBeanServerConnection getMBeanServerConnection()
    throws Exception {
        //
        // Create an RMI connector client and connect it to the RMI connector server
        //
        System.out.println("Connecting to: " + host + ":" + port);

        final String urlPath = "/jndi/rmi://" + host + ":" + port + "/jmxrmi";
        JMXServiceURL url = new JMXServiceURL("rmi", "", 0, urlPath);

        Map<String, String[]> env = new HashMap<String, String[]>();
        env.put(JMXConnector.CREDENTIALS,
                new String[] {userName, password});
        JMXConnector jmxc = JMXConnectorFactory.connect(url, env);
        //
        // Get an MBeanServerConnection
        //
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        return mbsc;
    }
}