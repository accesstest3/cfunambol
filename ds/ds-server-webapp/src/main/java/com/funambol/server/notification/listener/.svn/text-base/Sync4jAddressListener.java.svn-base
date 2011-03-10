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
package com.funambol.server.notification.listener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.axis.utils.StringUtils;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.funambol.framework.core.AbstractCommand;
import com.funambol.framework.core.Alert;
import com.funambol.framework.core.Constants;
import com.funambol.framework.core.Cred;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.SyncBody;
import com.funambol.framework.core.SyncML;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.builder.DSNotificationMessageBuilder;
import com.funambol.framework.protocol.ProtocolException;
import com.funambol.framework.security.Officer;
import com.funambol.framework.security.SecurityConstants;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.error.ServerException;
import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.SyncMLUtil;
import com.funambol.framework.tools.WBXMLTools;

import com.funambol.server.admin.AdminManager;
import com.funambol.server.config.Configuration;
import com.funambol.server.notification.builder.DSNotificationMessageBuilderImpl;

/**
 * Role of this servlet is to receive an address notification message,
 * authenticate the device and if the authentication is successful, register
 * the given address in the DeviceInventory. Note that if the device is not
 * in the database, a new one will be created.
 *
 * @see com.funambol.framework.server.DeviceInventory
 *
 * @version $Id$
 */
public class Sync4jAddressListener extends HttpServlet {

    // --------------------------------------------------------------- Constants
    private static final String STP_RESOURSE =
            SecurityConstants.RESOURCE_SERVICE + "/stp";

    // ------------------------------------------------------------ Private data
    private static final FunambolLogger log =
        FunambolLoggerFactory.getLogger("funambol.server.notification.address-listener");

    private static final String SOURCEURI_IP     = "ip"    ;
    private static final String SOURCEURI_MSISDN = "msisdn";

    private static final int SIZE_INPUT_BUFFER   = 1024;

    /**
     * Should the address notification be accepted ?
     */
    private boolean acceptAddressNotification = true;

    /**
     * Is CTP available ?
     */
    private boolean ctpServerAvailable = true;

    // ---------------------------------------------------------- Public methods

    /**
     * Init the servlet
     * @throws javax.servlet.ServletException if an error occurs
     */
    @Override
    public void init() throws ServletException {
        ServletConfig config = getServletConfig();

        String value = null;

        value = config.getInitParameter("accept-address-notification");
        if (!StringUtils.isEmpty(value)) {
            if ("true".equalsIgnoreCase(value)) {
                acceptAddressNotification = true;
            } else {
                acceptAddressNotification = false;
            }
        } else {
                acceptAddressNotification = true;
        }

        value = config.getInitParameter("ctpserver-available");
        if (!StringUtils.isEmpty(value)) {
            if ("true".equalsIgnoreCase(value)) {
                ctpServerAvailable = true;
            } else {
                ctpServerAvailable = false;
            }
        } else {
                ctpServerAvailable = true;
        }

        if (log.isTraceEnabled()) {
            log.trace("Initializing AddressListener servlet with accept-address-notification:" +
                     acceptAddressNotification +
                     ", ctpserver-available:" + ctpServerAvailable);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        if (log.isTraceEnabled()) {
            showHeaders(request);
        }

        if (log.isTraceEnabled()) {
            log.trace("Handling incoming request for address listener.");
        }

        response.setHeader("X-funambol-ds-server", getServerHeader());

        SyncML syncObj = null;

        try {
            syncObj = getSyncMLObject(request);
        } catch (Exception e) {
            log.error("Unable to parse the request", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        // The sensitive data are hidden if funambol is not in debug mode.
        //
        if (log.isTraceEnabled()) {
            log.trace("Processing message: " +
                SyncMLUtil.toXML(syncObj, Configuration.getConfiguration()
                                                       .isDebugMode()));
        }

        DeviceInventory devInv = (DeviceInventory)Configuration
                                                 .getConfiguration()
                                                 .getDeviceInventory();

        String deviceId = syncObj.getSyncHdr().getSource().getLocURI();
        Sync4jDevice device = new Sync4jDevice();
        device.setDeviceId(deviceId);
        boolean deviceFound = false;
        try {
            //
            //Get device without capabilities
            //
            deviceFound = devInv.getDevice(device, false);
        } catch (Exception e) {
            log.error("Error reading the device", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if (!deviceFound) {
            try {
                //
                // Creating a new device
                //
                devInv.setDevice(device);
            } catch (Exception e) {
                log.error("Error creating the device", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }

        AuthenticateResult authResult = authenticate(syncObj);
        int authCode = authResult.authCode;
        if (authCode != 200) {
            response.setStatus(authCode);
            return ;
        }

        String username = authResult.user.getUsername();

        //
        //This flag is used to tell if device's address or(and) msisdn
        //are specificated into Alert commands
        //
        boolean isAddressChangeMessage = false;

        boolean validIpAddressReceived = false;
        boolean msisdnReceived         = false;

        ArrayList<Item> items = new ArrayList<Item>();
        String  sourceURI = null;
        String  data      = null;

        Alert alert = getNotificationAlert(syncObj);
        if (alert == null) {
            log.error("Message sent to AddressListener is null or " +
                      "does not contain any Alert command");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        //
        //Update device with informations contains into Alert
        //
        if (alert.getItems() != null) {
            items    = alert.getItems();

            for (Item item : items) {

                if (item.getSource()             != null &&
                    item.getSource().getLocURI() != null &&
                    item.getData()               != null   ) {

                    sourceURI = item.getSource().getLocURI();

                    if (log.isTraceEnabled()) {
                        log.trace("Item source URI : "  + sourceURI);
                        log.trace("Item source DATA: "  + item.getData().getData());
                    }

                    if (sourceURI.equalsIgnoreCase(SOURCEURI_IP)) {
                        isAddressChangeMessage = true;

                        if (!acceptAddressNotification) {
                            if (log.isTraceEnabled()) {
                                log.trace("Ignoring address notification (accept-address-notification is false)");
                            }
                            //
                            // We delete the device address because the idea is that
                            // if accept-address-notification is false, the device
                            // should not be notified via STP
                            //
                            device.setAddress("");
                            continue;
                        }
                        data = item.getData().getData();
                        //
                        // Checks if the ip:port notified is reachable
                        // from the server
                        //
                        boolean validData = checkIP(data, username, deviceId);
                        if (!validData) {

                            if (log.isTraceEnabled()) {
                                log.trace("The received data (" + data + ") " +
                                         "is not a reachable address");
                            }

                            device.setAddress("");

                        } else {
                            if (log.isTraceEnabled()) {
                                log.trace("The received data (" + data + ") " +
                                          "is a reachable address");
                            }
                            device.setAddress(data);
                            validIpAddressReceived = true;
                        }

                    } else if (sourceURI.equalsIgnoreCase(SOURCEURI_MSISDN)) {
                        device.setMsisdn(item.getData().getData());
                        msisdnReceived         = true;
                    }
                }
            }
        }

        if (isAddressChangeMessage) {
            try {
                //
                // Reloading the device from the database in order
                // to update the most up-to-date version
                // (something, like the officer, can have changed it
                // in the db)
                //
                Sync4jDevice upToDateDevice = new Sync4jDevice(deviceId);
                devInv.getDevice(upToDateDevice, false); // read device without caps
                //
                // Setting the upToDateDevice according to the information received in the
                // addressNotification (these are contained in the current device
                // object)
                //
                upToDateDevice.setAddress(device.getAddress());
                //
                // We should overwrite the msisdn in the database only if we
                // received it
                //
                if (msisdnReceived) {
                    upToDateDevice.setMsisdn(device.getMsisdn());
                }

                if (log.isTraceEnabled()) {
                    log.trace("Storing " + upToDateDevice);
                }
                devInv.setDevice(upToDateDevice);

            } catch (DeviceInventoryException e) {

                log.error("Error processing the request", e);

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            if (validIpAddressReceived) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                //
                // We have received a valid Alert command (notified == true)
                // but the ip isn't valid and there isn't the msisdn.
                //
                // Set status = 420 (or = 421) if there is an ip address included
                // into Alert commands that is not reachable.
                //
                // The idea is that if the client receives a 421 it can start a
                // CTP connection, if it receives a 420 it should not start the
                // CTP connection.
                //
                // The v6 plug-ins are able to handle also the 421 (it checks just
                // for 200 so 421 is like 420) so no backward compatibility issue
                // will raise (but or course they are not able to do CTP).
                //
                // In other word 421 means: if you are able to start a ctp connection,
                // start it.
                //
                if (ctpServerAvailable) {
                    if (log.isTraceEnabled()) {
                        log.trace("Returning 421 as status code (ctpserver-available is true)");
                    }
                    response.setStatus(421);
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Returning 420 as status code (ctpserver-available is false)");
                    }
                    response.setStatus(420);
                }
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Bad Request");
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Authenticates a device
     *
     * @param credentials the credentials used for authentication
     *
     * @return the http status code to return to the client
     * @throws javax.servlet.ServletException if an error occurs
     */
    private AuthenticateResult authenticate(Cred credentials)
    throws ServletException {

        AuthenticateResult authResult = new AuthenticateResult();

        Officer officer = Configuration.getConfiguration().getOfficer();
        Sync4jUser user = officer.authenticateUser(credentials);

        authResult.user = user;

        if (user == null) {
            if (log.isTraceEnabled()) {
                log.trace("Device not authenticated! Invalid credentials.");
            }

            authResult.authCode = HttpServletResponse.SC_UNAUTHORIZED;
            return authResult;
        }

        authResult.authCode =
            authorize(user, credentials.getAuthentication().getDeviceId());
        return authResult;
    }

    /**
     * Authorizes the user for stp.
     *
     * @param user the user
     * @param deviceId the device id
     * @return the http status code to return to the client
     * @throws javax.servlet.ServletException if an error occurs
     */
    private int authorize(Sync4jUser user, String deviceId)
    throws ServletException {

        Sync4jPrincipal principal = new Sync4jPrincipal();
        principal.setUser(user);
        principal.setDeviceId(deviceId);
        try {
            Configuration.getConfiguration().getStore().read(principal);
        } catch (NotFoundException e) {
            if (log.isTraceEnabled()) {
                log.trace("Principal not found:" + principal);
            }

            return HttpServletResponse.SC_UNAUTHORIZED;

        } catch (PersistentStoreException e) {
            String msg = "Error reading principal: " + e.getMessage();
            throw new ServletException(msg, e);
        }
        Officer officer = Configuration.getConfiguration().getOfficer();
        Officer.AuthStatus authStatus = officer.authorize(principal, STP_RESOURSE);

        switch (officer.authorize(principal, STP_RESOURSE)) {
            case AUTHORIZED:
                if (log.isTraceEnabled()) {
                    log.trace("Device authorized");
                }
                return HttpServletResponse.SC_OK;
            case INVALID_RESOURCE:
                if (log.isTraceEnabled()) {
                    log.trace("Device not authenticated");
                }
                return HttpServletResponse.SC_UNAUTHORIZED;
            case NOT_AUTHORIZED:
                if (log.isTraceEnabled()) {
                    log.trace("Device not authorized (forbidden)");
                }
                return HttpServletResponse.SC_FORBIDDEN;
            case PAYMENT_REQUIRED:
                if (log.isTraceEnabled()) {
                    log.trace("Device not authorized (payment required)");
                }
                return HttpServletResponse.SC_PAYMENT_REQUIRED;
            case RESOURCE_NOT_AVAILABLE:
                if (log.isTraceEnabled()) {
                    log.trace("Device not authenticated");
                }
                return HttpServletResponse.SC_UNAUTHORIZED;
            default:
                if (log.isTraceEnabled()) {
                    log.trace("Device not authenticated");
                }
                return HttpServletResponse.SC_UNAUTHORIZED;
        }
    }

    /**
     * Converts a SyncML message into a SyncML object.
     *
     * @param msg the message to be converted
     * @return the SyncML object obtained from message
     * @throws ServerException if an error occurs
     */
    private SyncML convert(final String msg) throws ServerException {
        try {
            IBindingFactory f = BindingDirectory.getFactory("binding", SyncML.class);
            IUnmarshallingContext c = f.createUnmarshallingContext();
            return (SyncML) c.unmarshalDocument(new StringReader(msg));
        } catch (JiBXException e) {
            log.error("Error unmarshalling message", e);
            throw new ServerException(e);
        }
    }

    /**
     * Log the headers of the request.
     *
     * @param request HttpServletRequest
     */
    private void showHeaders(HttpServletRequest request) {
        Enumeration enumHeaders = request.getHeaderNames();
        StringBuffer sb = new StringBuffer("Http header: \n");
        String headerName  = null;
        String headerValue = null;
        while (enumHeaders.hasMoreElements()) {
            headerName = (String)enumHeaders.nextElement();
            headerValue = request.getHeader(headerName);
            sb.append("> ").append(headerName);
            sb.append(": ").append(headerValue).append('\n');
        }
        log.trace(sb.toString());
    }

    /**
     * Checks if the given data contains a valid address usable to send notification
     * messages via tcp. The given data must be in the form <code>address:port</code>
     * or just <code>address</code>. The address is tested sending a fake notification
     * message without alerts if there are not pending notifications. If in the
     * datastore there are already not delivered notification, then they are
     * collected in a message and sent instead of the fake message.
     *
     * @param data the address to test
     * @return boolean true if the device is reachable, false otherwise.
     */
    private boolean checkIP(String data, String username, String deviceId) {

        if (data == null) {
            return false;
        }

        AdminManager adminManager = new AdminManager();

        SocketAddress sockAddress = getSocketAddress(data);
        
        if (log.isTraceEnabled()) {
            log.trace("Checking if the address " + sockAddress + " is reachable sending a notification message");
        }

        Message message = null;
        boolean deletePendingNotification = true;
        byte[] messageToSend = null;
        try {

            //
            // Verifies in the datastore if there are some pending notification.
            // If there is at least one then create a Message with it, otherwise
            // send a fake message.
            //
            message =
                adminManager.getMessageFromPendingNotifications(username, deviceId);

            if (message != null) {
                messageToSend = message.getMessageContent();
            } else {
                deletePendingNotification = false;
                messageToSend = buildFakeNotificationMessage();
            }

        } catch (Exception ex) {
            //
            // If there is an exception in this phase, the eventually pending
            // notifications have not to be deleted from the datastore.
            //
            deletePendingNotification = false;

            //
            // Creating a really fake message. This was obtained calling the
            // builder offline
            //
            messageToSend = new byte[] {
                (byte)0xF6, (byte)0xE2, (byte)0xD3, (byte)0x51, (byte)0xE1,
                (byte)0xDC, (byte)0x51, (byte)0xA5, (byte)0x86, (byte)0x96,
                (byte)0xE3, (byte)0xC8, (byte)0x06, (byte)0x53, (byte)0x60,
                (byte)0xCF, (byte)0x03, (byte)0x08, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x66,
                (byte)0x75, (byte)0x6E, (byte)0x61, (byte)0x6D, (byte)0x62,
                (byte)0x6F, (byte)0x6C, (byte)0x00
            };
        }

        try {

            sendMessage(sockAddress, messageToSend);

            //
            // If the notification message is not a fake message and if there is
            // a response from client, then delete the pending notifications
            // from datastore since they are already sent.
            //
            if (deletePendingNotification) {
                adminManager.deletePendingNotifications(username,
                                                        deviceId,
                                                        message.getSyncSources()
                );
            }

            return true;

        } catch (Exception e) {

            deletePendingNotification = false;

            if (log.isTraceEnabled()) {
                log.trace("Error sending notification message to " + sockAddress, e);
            }

        } 
        return false;
    }

    /**
     * Creates a fake notifcation message using a
     * <code>DSNotificationMessageBuilderImpl</code>.
     * The fake message doesn't contains valid information and alert commands
     * but it is a well-format notification message.
     *
     * @return byte[] the fake message
     * @throws java.lang.Exception if an error occurs
     */
    private static byte[] buildFakeNotificationMessage() throws Exception {
        byte[]  fakeServerNonce   = new byte[] {0};
        String  fakeServerId      = "funambol";
        String  fakeServerPw      = "funambol";
        String  fakeDeviceId      = "device";
        int     fakeUIMode        = 0;
        String  fakeDeviceAddress = "";
        int     fakeSessionId     = 0;
        Alert[] fakeAlerts        = new Alert[0];

        DSNotificationMessageBuilder messageBuilder =
            new DSNotificationMessageBuilderImpl();

        Message fakeMessage = messageBuilder.buildMessage(fakeSessionId,
                                                          fakeDeviceId,
                                                          fakeDeviceAddress,
                                                          fakeServerId,
                                                          fakeServerPw,
                                                          fakeServerNonce,
                                                          fakeAlerts,
                                                          fakeUIMode,
                                                          1.2f);

        return fakeMessage.getMessageContent();
    }

    /**
     * Searches in the given SyncML message the alert command with data 745
     *
     * @return the first alert with data 745 or null if not found
     * @param message the syncml message
     */
    private Alert getNotificationAlert(SyncML message) {

        if (message == null) {
            return null;
        }
        SyncBody body = message.getSyncBody();
        if (body == null) {
            return null;
        }
        List<AbstractCommand> commands = body.getCommands();
        if (commands == null || commands.isEmpty()) {
            return null;
        }

        Alert   alert     = null;
        for (AbstractCommand command : commands) {
            if (!(command instanceof Alert)) {
                continue;
            }
            alert = (Alert)command;

            //
            //Check message data id - must be 745 to be processed
            //
            if (alert.getData() == 745) {
                return alert;
            }
        }
        return null;
    }

    /**
     * Reads the syncml contained in the received request
     *
     * @param request the request
     * @return the read SyncML message
     * @throws java.lang.Exception if an error occurs
     */
    private SyncML getSyncMLObject(HttpServletRequest request) throws Exception {
        //
        //Read the message
        //
        byte[] binaryData = null;
        String xmlData    = null;

        InputStream in = request.getInputStream();
        binaryData     = IOTools.readContent(in, SIZE_INPUT_BUFFER);

        String ipRequest = ((ServletRequest)request).getRemoteAddr();

        if (log.isTraceEnabled()) {
            log.trace("Remote address: " + ipRequest);
        }

        if (in != null) {
            in.close();
        }
        in = null;

        final String contentType = request.getContentType().split(";")[0];

        if (Constants.MIMETYPE_SYNCMLDS_WBXML.equals(contentType)) {

            //
            // Convert WBXML into XML
            //
            if (log.isTraceEnabled()) {
                log.trace("Converting message from wbxml to xml");
            }
            xmlData = WBXMLTools.wbxmlToXml(binaryData);

        } else if (Constants.MIMETYPE_SYNCMLDS_XML.equals(contentType)) {
            //
            //None conversion is necessary
            //
            xmlData = new String(binaryData);
        } else {
            throw new ProtocolException( "Mime type "
                                       + contentType
                                       + " nor supported or unknown" );
        }

        return convert(xmlData);
    }

    /**
     * Checks if the given syncml message contains valid credentials
     *
     * @param message the syncml message
     * @return 200 if the message is authenticated, 401 otherwise
     * @throws javax.servlet.ServletException if an error occurs
     */
    private AuthenticateResult authenticate(SyncML message)
    throws ServletException {
        AuthenticateResult authResult = new AuthenticateResult();

        //
        //Authenticate device
        //
        if (message.getSyncHdr().getCred() == null) {
            if (log.isTraceEnabled()) {
                log.trace("Device not authenticated. Credentials are missing.");
            }
            authResult.authCode = HttpServletResponse.SC_UNAUTHORIZED;
            return authResult;
        }

        Cred   cred     = message.getSyncHdr().getCred();
        String deviceId = message.getSyncHdr().getSource().getLocURI();

        cred.getAuthentication().setDeviceId(deviceId);

        return authenticate(cred);
    }

    /**
     * Returns the http header that will be set in the server response.
     *
     * @return the http header that will be set in the server response.
     *         <br><br>It is formed by:
     *         <br><code>model v.version</code>
     *         <br><br><I>E.g. 'DS Server v.6.5.0'
     */
    private String getServerHeader() {

        String serverVersion =
                Configuration.getConfiguration().getServerConfig().getServerInfo().getSwV();
        String serverModel =
                Configuration.getConfiguration().getServerConfig().getServerInfo().getMod();

        StringBuilder sb = new StringBuilder(32);
        if (serverModel != null && serverModel.length() > 0) {
            sb.append(serverModel).append(" ");
        }
        if (serverVersion != null && serverVersion.length() > 0) {
            sb.append("v.").append(serverVersion);
        }
        return sb.toString();
    }

    /**
     * Returns a SocketAddress built using the given data that must be in the form
     * hostname:port. If not specified, default port is 745 (the first version of the clients 
     * didn't send the port and they used 745).
     * <p>Valid data is:
     * <ul>
     * <li>192.168.0.30</li>
     * <li>192.168.0.30:</li>
     * <li>192.168.0.30:4745</li>
     * </ul>
     * @param data the address in the form hostname:port
     * @return the SocketAddress
     * @throws IllegalArgumentException if the data is not valid
     */
    private static SocketAddress getSocketAddress(String data) throws IllegalArgumentException {

        if (data == null) {
            throw new IllegalArgumentException("Address can not be null");
        }
        String address = null;
        int    port    = 745;

        int index = data.indexOf(':');
        if (index != -1) {
            address = data.substring(0, index);

            String sPort = null;
            if (index < (data.length() - 1)) {
                sPort = data.substring(index + 1);
                try {
                    port =  Integer.parseInt(sPort);
                } catch (NumberFormatException ne) {
                    throw new IllegalArgumentException("Port not valid (" + sPort + ")");
                }
            } else {
                port = 745;
            }
        } else {
            //
            // The data doesn't contain the port. The defualt one (745) is used
            //
            address = data;
        }


        return new InetSocketAddress(address, port);
    }

    /**
     * Sends the given byte[] to the given socketAddress
     * @param sockAddress the recipient
     * @param messageToSend the message to send
     * @throws Exception if an error occurs
     */
    private static void sendMessage(SocketAddress sockAddress, byte[] messageToSend)
    throws Exception {

        Socket           sock = null;
        DataOutputStream out  = null;
        BufferedReader   in   = null;

        try {

            //create a socket and connect to device
            sock = new Socket();
            sock.setSoTimeout(20000); // read timeout = 20 seconds
            sock.connect(sockAddress, 20000); // connection timeout = 20 seconds

            out = new DataOutputStream(sock.getOutputStream());

            //send message through socket
            out.write(messageToSend);

            //flsuh the message in stream
            out.flush();

            //read status response
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream())
                 );
            String response = in.readLine();

            if (log.isTraceEnabled()) {
                log.trace("Response from the client: " + response);
            }

        } finally {

            //close connection to device
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (sock != null) {
                    if (!sock.isClosed()) {
                        sock.close();
                    }
                }
            } catch (IOException e) {
                if (log.isTraceEnabled()) {
                    log.trace("Error closing connection (" + e.toString() + ")");
                }
            }
        }
    }

    // ------------------------------------------------------ AuthenticateResult
    /**
     * An Inner Class that represents the authenticate result and the
     * authenticated user.
     */
    class AuthenticateResult {
        int authCode;
        Sync4jUser user = null;
    }
}
