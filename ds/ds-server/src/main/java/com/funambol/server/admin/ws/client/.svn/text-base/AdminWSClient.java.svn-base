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

package com.funambol.server.admin.ws.client;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.MimeHeaders;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

import com.funambol.framework.core.Alert;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.notification.Message;
import com.funambol.framework.security.AuthorizationResponse;
import com.funambol.framework.security.Credential;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.LastTimestamp;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanFactory;

import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.UnauthorizedException;
import com.funambol.server.config.ServerConfiguration;

/**
 * Simple client for some web services exposed by the ds server.
 *
 * @version $Id: AdminWSClient.java,v 1.1 2008-02-24 14:43:46 stefano_fornari Exp $
 */
public class AdminWSClient {

    // --------------------------------------------------------------- Constants

    private static final String FAULT_UNAUTHORIZED = "(401)Unauthorized";

    // ------------------------------------------------------------ Private data

    /** The Server information */
    private ServerInformation serverInformation = null;

    /** Logger used to track web services calls. */
    private FunambolLogger logger = null;

    /** Mapping between java types and qnames */
    private Map<Class, String> typesMapping    = null;

    // ------------------------------------------------------------ Constructors


    /**
     * Creates a new <code>AdminWSClient</code>, given web service end point
     * information.
     *
     * @param serverInformation web service end point information
     */
    public AdminWSClient(ServerInformation serverInformation) {
        this(serverInformation, null);
    }

    /**
     * Creates a new <code>AdminWSClient</code>, given web service end point
     * information and the name of a logger used by the client itself to track
     * web services calls.
     *
     * @param serverInformation web service end point information
     * @param loggerName the name of a logger used by the client itself to track
     * web services calls
     */
    public AdminWSClient(
            ServerInformation serverInformation,
            String loggerName) {
        this.serverInformation = serverInformation;
        initTypesMapping();

        if (loggerName != null && loggerName.length() != 0){
            this.logger = FunambolLoggerFactory.getLogger(loggerName);
        }
    }

    /**
     * Just for test
     */
    public static void main(String[] args) {
        try {
            ServerInformation si = new ServerInformation("http://192.168.0.72:8080/funambol/services/admin", "admin", "sa");
            AdminWSClient client = new AdminWSClient(si, null);

            Credential cred = new Credential();
            cred.setAuthType(Credential.AUTH_TYPE_BASIC);
            cred.setDeviceId("ded");
            cred.setCredData("Z3Vlc3Q6Z3Vlc3RkZGQ=");
            AuthorizationResponse response = client.authorizeCredential(cred, "ciao");
            System.out.println("Response: " + response.getAuthStatus());
            System.out.println("Nonce: " + response.getNextNonce());

            String[] roles = client.getRoles();
            for (String role : roles) {
                System.out.println("role: " + role);
            }
            System.out.println("Server version: " + client.getServerVersion());

        } catch (AdminException ex) {
            ex.printStackTrace();
        }
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Checks if user is authorized to use the given resource.
     *
     * @param authCred the user credential
     * @param resource the resource to check
     *
     * @return the AuthorizeResponse with the result of the check
     *
     * @throws AdminException
     */
    public AuthorizationResponse authorizeCredential(Credential authCred,
                                                     String     resource)
    throws AdminException {
        return (AuthorizationResponse)invoke("authorizeCredential", new Object[] {authCred, resource});
    }

    /**
     * Returns the roles
     *
     * @throws AdminException if a error occurs
     * @return the roles
     */
    public String[] getRoles() throws AdminException {
        return (String[])invoke("getRoles", new Object[0]);
    }

    /** Calls getUsers method of the WS     */
    public Sync4jUser[] getUsers(Clause clause) throws AdminException {
        try {
            return (Sync4jUser[])invoke("getUsers", new Object[] { BeanFactory.marshal(clause) });
        } catch (BeanException t) {
            throw new AdminException("Error invoking getUsers", t);
        }
    }

    /** Calls addUser method of the WS     */
    public void addUser(Sync4jUser u) throws AdminException {
        invoke("addUser", new Object[] {u});
    }

    /** Calls setUser method of the WS     */
    public void setUser(Sync4jUser u) throws AdminException {
        invoke("setUser", new Object[] {u});
    }

    /** Calls deleteUser method of the WS     */
    public void deleteUser(String userName) throws AdminException {
        invoke("deleteUser", new Object[] {userName});
    }

    /** Calls countUsers method of the WS     */
    public int countUsers(Clause clause) throws AdminException {
        try {
            Integer i = (Integer)invoke("countUsers", new Object[] {BeanFactory.marshal(clause)});
            return i.intValue();
        } catch (BeanException t) {
            throw new AdminException("Error invoking countUsers", t);
        }
    }

    /** Calls getDevices method of the WS     */
    public Sync4jDevice[] getDevices(Clause clause)
    throws AdminException {
        try {
            return (Sync4jDevice[])invoke("getDevices", new Object[] { BeanFactory.marshal(clause) });
        } catch (BeanException t) {
            throw new AdminException("Error invoking getDevices", t);
        }
    }

    /** Calls getDevicesCapabilities method of the WS     */
    public Capabilities getDeviceCapabilities(String deviceId)
    throws AdminException {
        try {
            String       capsXML =
                (String)invoke("getDeviceCapabilities", new Object[] {deviceId});
            Capabilities caps    =
                (Capabilities)BeanFactory.unmarshal(capsXML);
            return caps;
        } catch (BeanException t) {
            throw new AdminException("Error invoking getDeviceCapabilities", t);
        }
    }

    /** Calls setDevicesCapabilities method of the WS     */
    public Long setDeviceCapabilities(Capabilities caps, String deviceId)
    throws AdminException {
        try {
            Long idCaps = (Long)invoke("setDeviceCapabilities", new Object[] {BeanFactory.marshal(caps), deviceId});
            return idCaps;

        } catch (BeanException t) {
            throw new AdminException("Error invoking setDeviceCapabilities", t);
        }
    }

    /** Calls addDevice method of the WS     */
    public String addDevice(Sync4jDevice d) throws AdminException {
        return (String)invoke("addDevice", new Object[] {d});
    }

    /** Calls setDevice method of the WS     */
    public void setDevice(Sync4jDevice d) throws AdminException {
            invoke("setDevice", new Object[] {d});
    }

    /** Calls deleteDevice method of the WS     */
    public void deleteDevice(String deviceId) throws AdminException {
        invoke("deleteDevice", new Object[] {deviceId});
    }

    /** Calls countDevices method of the WS     */
    public int countDevices(Clause clause) throws AdminException {
        try {
            Integer i = (Integer)invoke("countDevices", new Object[] { BeanFactory.marshal(clause) });
            return i.intValue();
        } catch (BeanException t) {
            throw new AdminException("Error invoking countDevices", t);
        }
    }

    /** Calls getPrincipals method of the WS     */
    public Sync4jPrincipal[] getPrincipals(Clause clause)
    throws AdminException {
        try {
            return (Sync4jPrincipal[])invoke("getPrincipals", new Object[] { BeanFactory.marshal(clause) });
        } catch (BeanException t) {
            throw new AdminException("Error invoking getPrincipals", t);
        }
    }

    /** Calls addPrincipal method of the WS     */
    public long addPrincipal(Sync4jPrincipal p) throws AdminException {

        Long l = (Long) invoke("addPrincipal", new Object[]{p});
        return l.longValue();

    }

    /** Calls deletePrincipal method of the WS     */
    public void deletePrincipal(long principalId) throws AdminException {
        invoke("deletePrincipal", new Object[] {new Long(principalId)});
    }

    /** Calls countPrincipals method of the WS     */
    public int countPrincipals(Clause clause) throws AdminException {
        try {
            Integer i = (Integer)invoke("countPrincipals", new Object[] { BeanFactory.marshal(clause) });
            return i.intValue();
        } catch (BeanException t) {
            throw new AdminException("Error invoking countPrincipals", t);
        }
    }

    /** Calls getLastTimestamps method of the WS     */
    public LastTimestamp[] getLastTimestamps(Clause clause) throws AdminException {
        try {
            return (LastTimestamp[])invoke("getLastTimestamps", new Object[] { BeanFactory.marshal(clause) });
        } catch (BeanException t) {
            throw new AdminException("Error invoking getLastTimestamps", t);
        }
    }

    /** Calls deleteLastTimestamp method of the WS     */
    public void deleteLastTimestamp(long principalId, String syncSourceId)
    throws AdminException {
            invoke("deleteLastTimestamp", new Object[] {new Long(principalId), syncSourceId});
    }

    /** Calls countLastTimestamps method of the WS     */
    public int countLastTimestamps(Clause clause) throws AdminException {
        try {
            Integer i = (Integer)invoke("countLastTimestamps", new Object[] { BeanFactory.marshal(clause) });
            return i.intValue();
        } catch (BeanException t) {
            throw new AdminException("Error invoking countLastTimestamps", t);
        }
    }

    /**
     * Returns the server version
     *
     * @return the server version
     *
     * @throws AdminException in case of errors
     */
    public String getServerVersion() throws AdminException {
        return (String)invoke("getServerVersion", new Object[0]);
    }

    /**
     * Returns the server configuration settings
     *
     * @return the server configuration settings
     *
     * @throws AdminException in case of errors
     */
    public ServerConfiguration getServerConfiguration() throws AdminException {
        ServerConfiguration serverConfig =
            (ServerConfiguration)invoke("getServerConfiguration", new Object[] {});
        return serverConfig;
    }

    /**
     * Saves the given server configuration on the server
     *
     * @param config the new server configuration
     *
     * @return the server configuration
     *
     * @throws AdminException in case of errors
     */
    public void setServerConfiguration(ServerConfiguration serverConfig)
    throws AdminException {
        invoke("setServerConfiguration", new Object[] {serverConfig});
    }

    /**
     * Sends a notification message to the given device.
     *
     * @param username the username. Note that this parameter is not actively used
     *                 at the moment; it's just used in the log and it doesn't affect
     *                 the behaviour.
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user intraction mode
     * @throws AdminException if an error occurs
     */
    public void sendNotificationMessage(String  username,
                                        String  deviceId,
                                        Alert[] alerts  ,
                                        int     uimode  )
    throws AdminException {
        try {
            invoke("sendNotificationMessage", new Object[] {username,
                                                            deviceId,
                                                            BeanFactory.marshal(alerts),
                                                            uimode});
        } catch (BeanException t) {
            throw new AdminException("Error invoking sendNotificationMessage", t);
        }
    }

    /**
     * Sends a notification message to the given device.
     *
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user intraction mode
     * @throws AdminException if an error occurs
     *
     * @deprecated Since v71 use sendNotificationMessage(String username,
     *             String deviceId, Alert[] alerts, int uimode)
     */
    public void sendNotificationMessage(String  deviceId,
                                        Alert[] alerts  ,
                                        int     uimode  )
    throws AdminException {
        try {
            invoke("sendNotificationMessage", new Object[] {deviceId,
                                                            BeanFactory.marshal(alerts),
                                                            uimode});
        } catch (BeanException t) {
            throw new AdminException("Error invoking sendNotificationMessage", t);
        }
    }

    /**
     * Sends a notification message to all devices of the principals with the
     * given username.
     *
     * @param username the username
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user intraction mode
     * @throws AdminException if an error occurs
     */
    public void sendNotificationMessages(String  username,
                                         Alert[] alerts  ,
                                         int     uimode  )
    throws AdminException {
        try {
            invoke("sendNotificationMessages",
                   new Object[] {username, BeanFactory.marshal(alerts), uimode});
        } catch (BeanException t) {
            throw new AdminException("Error invoking sendNotificationMessage", t);
        }
    }

    /**
     * Retrieves a notification message created with the pending notifications
     * stored in the datastore.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @return the notification message
     * @throws AdminException if an error occurs
     */
    public Message getMessageFromPendingNotifications(String username,
                                                      String deviceId)
    throws AdminException {
        Message message = (Message) invoke("getMessageFromPendingNotifications",
                                           new Object[] {username, deviceId});
        if (message != null) {
            //
            // The messageType is set to null because otherwise there is an
            // issue while sending it via web service. It is set to null on the
            // web service server side instead on the client side (here) it is
            // set back to the correct value, using the 'type' value.
            //
            message.setMessageTypeToType();
        }
        return message;
    }

    /**
     * Deletes the notification stored in the datastore after that they are
     * delivered rightly.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @param syncSources the array of SyncSources to notify
     * @throws AdminException if an error occurs
     */
    public void deletePendingNotifications(String   username   ,
                                           String   deviceId   ,
                                           String[] syncSources)
    throws AdminException {
        invoke("deletePendingNotifications",
               new Object[] {username, deviceId, syncSources});
    }

    /**
     * Invokes the given WS method with the given objects.
     *
     * @return the object returned by the invoked method
     * @param methodName the method to invoke
     * @param args the call arguments
     * @throws AdminException if an error occurs
     */
    public Object invoke(final String methodName, Object[] args)
    throws AdminException {
        Service  service = new Service();

        URL url = serverInformation.getUrl();
        String userName = serverInformation.getUsername();
        String password = serverInformation.getPassword();

        Call call = null;
        try {
           call = (Call) service.createCall();

           mapTypes(call);

           call.setTargetEndpointAddress(url);
           call.setOperationName(methodName);
           call.setUsername(userName);
           call.setPassword(password);

           Object o = call.invoke(args);

           logWSCallSessionId(call, logger);

           return o;

        } catch (AxisFault f) {

            logWSCallSessionId(call, logger);

            //
            // The exceptions are handled with the following roles:
            // 1. If there is a cause, we throw a SyncAdmin exception with
            //    CONNECTION_ERROR_MESSAGE. (Usually the cause is a connection exception).
            // 2. If the faultString is FAULT_UNAUTHORIZED we throw an UnauthorizedException
            // 3. If the faultCode is Server.userException we have to check if the exception
            //    is an Exception or a ServerException.
            //    If it is a Exception we get his message from the faultString and
            //    we throw a new Exception with this message.
            //    If it is a ServerException we throw an Exception with
            //    message UNEXPECTED_SERVER_ERROR.
            // 4. Otherwise we throw an Exception with message UNEXPECTED_ERROR.
            //
            String errorMessage = null;

            Throwable cause = f.getCause();

            if (cause != null) {
                throw new AdminException("Connection error", cause);
            }

            if (FAULT_UNAUTHORIZED.equals(f.getFaultString())) {

               throw new UnauthorizedException("User '" + userName+ "' not authorized");

           } else if (Constants.FAULT_SERVER_USER.equals(f.getFaultCode().getLocalPart())) {

               String faultString  = f.getFaultString();

               if (faultString != null) {
                   if (faultString.startsWith("com.funambol.server.admin.Exception:")) {
                       int len = "com.funambol.server.admin.Exception: ".length();
                       errorMessage = faultString.substring(len);
                   } else {
                       errorMessage = "Unexpected server error. Check the server log for details.";
                   }
               }

               throw new AdminException(errorMessage);

           } else {
               if (f.getMessage() != null && !f.getMessage().equals("")) {
                   throw new AdminException("Unexpected error: " +
                                            f.getMessage(), f);
               } else {
                   throw new AdminException("Unexpected error: ", f);
               }
           }

        } catch (Exception e) {
            logWSCallSessionId(call, logger);

            throw new AdminException("Unexpected error: " + e.getMessage(), e);
        }
    }

    // ------------------------------------------------------- Protected methods

    /**
     * Lets a subclass to add mappings between java types and qnames.
     * @param additionalTypesMapping
     */
    protected void addTypesMapping(Map<Class, String> additionalTypesMapping) {
        typesMapping.putAll(additionalTypesMapping);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Maps the java types to the serializer-deserializer to use (the AXIS
     * BeanSerializer/Deserializer).
     *
     * @param call the call on which perform the mappings
     */
    private void mapTypes(Call call) {

        if (typesMapping == null){
            return;
        }

        for (Map.Entry<Class, String> entry : typesMapping.entrySet()) {
            mapType(call, entry.getKey(), entry.getValue());
        }
    }

    /**
     * Maps a single type.
     *
     * @param call the Call object
     * @param type the class to map
     * @param qname the type QName
     *
     */
    private void mapType(Call call, final Class type, final String qname)  {

        QName q = new QName( "urn:BeanService",  qname);

        call.registerTypeMapping(
            type,
            q,
            new BeanSerializerFactory(type, q),
            new BeanDeserializerFactory(type, q)
        );

    }

    /**
     * Initializes typesMapping.
     */
    private void initTypesMapping (){
        if (typesMapping == null){
            typesMapping = new HashMap<Class, String>();

            typesMapping.put(com.funambol.framework.server.LastTimestamp.class, "LastTimestamp");
            typesMapping.put(com.funambol.framework.security.Sync4jPrincipal.class, "Sync4jPrincipal");
            typesMapping.put(com.funambol.framework.server.Sync4jUser.class, "Sync4jUser");
            typesMapping.put(com.funambol.framework.server.Sync4jDevice.class, "Sync4jDevice");
            typesMapping.put(com.funambol.framework.server.Sync4jModule.class, "Sync4jModule");
            typesMapping.put(com.funambol.framework.server.Sync4jConnector.class, "Sync4jConnector");
            typesMapping.put(com.funambol.framework.server.Sync4jSource.class, "Sync4jSource");
            typesMapping.put(com.funambol.framework.config.LoggingConfiguration.class, "Sync4jLoggingConfig");
            typesMapping.put(com.funambol.framework.config.LoggerConfiguration.class, "Sync4jLoggerConfig");
            typesMapping.put(com.funambol.server.config.ServerConfiguration.class, "ServerConfiguration");
            typesMapping.put(com.funambol.framework.core.DevInf.class, "DevInf");
            typesMapping.put(com.funambol.framework.core.VerDTD.class, "VerDTD");
            typesMapping.put(com.funambol.framework.core.DataStore.class, "DataStore");
            typesMapping.put(com.funambol.framework.core.CTCap.class, "CTCap");
            typesMapping.put(com.funambol.framework.core.Ext.class, "Ext");
            typesMapping.put(com.funambol.server.config.EngineConfiguration.class, "EngineConfiguration");
            typesMapping.put(com.funambol.framework.core.SourceRef.class, "SourceRef");
            typesMapping.put(com.funambol.framework.core.CTInfo.class, "CTInfo");
            typesMapping.put(com.funambol.framework.core.SyncCap.class, "SyncCap");
            typesMapping.put(com.funambol.framework.server.Capabilities.class, "Capabilities");
            typesMapping.put(com.funambol.framework.core.SyncType.class, "SyncType");
            typesMapping.put(com.funambol.framework.core.DSMem.class, "DSMem");
            typesMapping.put(com.funambol.framework.core.FilterCap.class, "FilterCap");
            typesMapping.put(com.funambol.framework.core.Property.class, "Property");
            typesMapping.put(com.funambol.framework.core.PropParam.class, "PropParam");
            typesMapping.put(com.funambol.server.update.Component.class, "Component");
            typesMapping.put(com.funambol.framework.security.AuthorizationResponse.class, "AuthorizationResponse");
            typesMapping.put(com.funambol.framework.security.Credential.class, "Credential");
            typesMapping.put(com.funambol.framework.notification.Message.class, "Message");
        }
    }

    /**
     * Logs the id of a web service call.
     * @param call web service call
     * @param logger logger to be used
     */
    private void logWSCallSessionId(Call call, FunambolLogger logger) {
        if (logger != null && logger.isTraceEnabled()) {

            if (call == null){
                return;
            }

            MimeHeaders headers = call.getResponseMessage().getMimeHeaders();

            if (headers != null && headers.getHeader("set-cookie") != null) {
                String setCookieHeader = headers.getHeader("set-cookie")[0];
                String jsessionId = setCookieHeader.substring(
                        setCookieHeader.indexOf("JSESSIONID=") + "JSESSIONID=".length(),
                        setCookieHeader.indexOf(';'));

                logger.trace("web service call session id: " + jsessionId);
            } else {
                logger.trace("session id not found");
            }
        }
    }
}