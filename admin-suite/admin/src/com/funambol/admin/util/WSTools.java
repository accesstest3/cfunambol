/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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
package com.funambol.admin.util;

import java.net.URL;
import java.net.MalformedURLException;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.funambol.admin.AdminException;
import com.funambol.admin.UnauthorizedException;

/**
 * This class groups some utility methods for WS invoking and handling
 *
 * @version $Id: WSTools.java,v 1.7 2007-11-28 10:28:17 nichele Exp $
 */
public class WSTools {
    // --------------------------------------------------------------- Constants

    public static final String FAULT_UNAUTHORIZED = "(401)Unauthorized";

    public static final String FAULT_DATA_STORE   = "DataStoreException";

    // ------------------------------------------------------------ Private data

    /**
     * The WS endpoint
     */
    private URL endpoint;

    /**
     * Username for authentication
     */
    private String username;

    /**
     * Password for authentication
     */
    private String password;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new WSTools with the given endpoint
     *
     * @param endpoint the endpoint
     */
    public WSTools(String endpoint, String username, String password)
    throws AdminException {
        try {
            this.endpoint = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new AdminException("Incorrect admin URL '" + endpoint + "'");
        }
        this.username = username;
        this.password = password;
    }

    /**
     * Invokes the given WS method with the given objects.
     *
     * @param methodName the method to invoke
     * @param args the call arguments
     *
     * @return the object returned by the invoked method
     *
     * @throws AdminExcheption in case of SyncAdmin or administration errors
     */
    public Object invoke(final String methodName, Object[] args)
        throws AdminException {
        Service  service = new Service();

        Log.debug("Invoking " + methodName + " on " + endpoint);

        try {
           Call call = (Call) service.createCall();

           mapTypes(call);

           call.setTargetEndpointAddress(endpoint);
           call.setOperationName(methodName);
           call.setUsername(username);
           call.setPassword(password);

           return call.invoke(args);
        } catch (AxisFault f) {

            //
            // The exceptions are handled with the following roles:
            // 1. If there is a cause, we throw a SyncAdmin exception with
            //    CONNECTION_ERROR_MESSAGE. (Usually the cause is a connection exception).
            // 2. If the faultString is FAULT_UNAUTHORIZED we throw an UnauthorizedException
            // 3. If the faultCode is Server.userException we have to check if the exception
            //    is an AdminException or a ServerException.
            //    If it is a AdminException we get his message from the faultString and
            //    we throw a new AdminException with this message.
            //    If it is a ServerException we throw an AdminException with
            //    message UNEXPECTED_SERVER_ERROR.
            // 4. Otherwise we throw an AdminException with message UNEXPECTED_ERROR.
            //
            String errorMessage = null;

            if (Log.isDebugEnabled()) {
                Log.error(f);
            }

            Throwable cause = f.getCause();
            if (cause != null) {
                Log.debug("Cause: " + f.getCause());
            }

            if (cause != null) {
                throw new AdminException(Bundle.getMessage(Bundle.CONNECTION_ERROR_MESSAGE), cause);
            }

            if (FAULT_UNAUTHORIZED.equals(f.getFaultString())) {

               throw new UnauthorizedException(username);

           } else if (org.apache.axis.Constants.FAULT_SERVER_USER.equals(f.getFaultCode().getLocalPart())) {

               String faultString  = f.getFaultString();

               if (faultString != null) {
                   if (faultString.startsWith("com.funambol.server.admin.AdminException:")) {
                       int len = "com.funambol.server.admin.AdminException: ".length();
                       errorMessage = faultString.substring(len);
                       Log.debug("errorMessage: " + errorMessage);
                   } else {
                       errorMessage = Bundle.getMessage(Bundle.UNEXPECTED_SERVER_ERROR);
                   }
               }

               throw new AdminException(errorMessage);

           } else {
               if (f.getMessage() != null && !f.getMessage().equals("")) {
                   throw new AdminException(Bundle.getMessage(Bundle.UNEXPECTED_ERROR) + ": " +
                                            f.getMessage(), f);
               } else {
                   throw new AdminException(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), f);
               }
           }

        } catch (Exception e) {

            if (Log.isDebugEnabled()) {
                Log.error(e);
            }

            throw new AdminException(Bundle.getMessage(Bundle.UNEXPECTED_ERROR) + ": " + e.getMessage(), e);
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Maps the java types to the serializer-deserializer to use (the AXIS
     * BeanSerializer/Deserializer).
     * The following types are mapped:
     * <ul>
     *  <li>com.funambol.framework.security.Sync4jPrincipal</li>
     *  <li>com.funambol.framework.server.Sync4jUser</li>
     *  <li>com.funambol.framework.server.Sync4jDevice</li>
     *  <li>com.funambol.framework.server.Sync4jModule</li>
     *  <li>com.funambol.framework.config.LoggingConfiguration</li>
     *  <li>com.funambol.framework.config.LoggerConfiguration</li>
     *  <li>com.funambol.framework.server.LastTimestamp</li>
     *  <li>com.funambol.framework.server.Sync4jConnector</li>
     *  <li>com.funambol.framework.server.Sync4jSource</li>
     *  <li>com.funambol.server.config.ServerConfiguration</li>
     *  <li>com.funambol.framework.core.DevInf</li>
     *  <li>com.funambol.framework.core.VerDTD</li>
     *  <li>com.funambol.framework.core.DataStore</li>
     *  <li>com.funambol.framework.core.CTCap</li>
     *  <li>com.funambol.framework.core.Ext</li>
     *  <li>com.funambol.server.config.EngineConfiguration</li>
     *  <li>com.funambol.framework.core.SourceRef</li>
     *  <li>com.funambol.framework.core.CTInfo</li>
     *  <li>com.funambol.framework.core.SyncCap</li>
     *  <li>com.funambol.framework.server.Capabilities</li>
     *  <li>com.funambol.framework.core.SyncType</li>
     *  <li>com.funambol.framework.core.DSMem</li>
     *  <li>com.funambol.framework.core.FilterCap</li>
     *  <li>com.funambol.framework.core.Property</li>
     *  <li>com.funambol.framework.core.PropParam</li>
     *  <li>com.funambol.server.update.Component</li>
     * </ul>
     *
     * @param call the call on which perform the mappings
     *
     */
    private void mapTypes(Call call) {
        mapType(
            call,
            com.funambol.framework.server.LastTimestamp.class,
            "LastTimestamp"
        );        
        mapType(
            call,
            com.funambol.framework.security.Sync4jPrincipal.class,
            "Sync4jPrincipal"
        );
        mapType(
            call,
            com.funambol.framework.server.Sync4jUser.class,
            "Sync4jUser"
        );
        mapType(
            call,
            com.funambol.framework.server.Sync4jDevice.class,
            "Sync4jDevice"
        );
        mapType(
            call,
            com.funambol.framework.server.Sync4jModule.class,
            "Sync4jModule"
        );
        mapType(
            call,
            com.funambol.framework.server.Sync4jConnector.class,
            "Sync4jConnector"
        );
        mapType(
            call,
            com.funambol.framework.server.Sync4jSource.class,
            "Sync4jSource"
        );
        mapType(
            call,
            com.funambol.framework.config.LoggingConfiguration.class,
            "Sync4jLoggingConfig"
        );
        mapType(
            call,
            com.funambol.framework.config.LoggerConfiguration.class,
            "Sync4jLoggerConfig"
        );
        mapType(
            call,
            com.funambol.server.config.ServerConfiguration.class,
            "ServerConfiguration"
        );
        mapType(
            call,
            com.funambol.framework.core.DevInf.class,
            "DevInf"
        );
        mapType(
            call,
            com.funambol.framework.core.VerDTD.class,
            "VerDTD"
        );
        mapType(
            call,
            com.funambol.framework.core.DataStore.class,
            "DataStore"
        );
        mapType(
            call,
            com.funambol.framework.core.CTCap.class,
            "CTCap"
        );
        mapType(
            call,
            com.funambol.framework.core.Ext.class,
            "Ext"
        );
        mapType(
            call,
            com.funambol.server.config.EngineConfiguration.class,
            "EngineConfiguration"
        );
        mapType(
            call,
            com.funambol.framework.core.SourceRef.class,
            "SourceRef"
        );
        mapType(
            call,
            com.funambol.framework.core.CTInfo.class,
            "CTInfo"
        );
        mapType(
            call,
            com.funambol.framework.core.SyncCap.class,
            "SyncCap"
        );
        mapType(
            call,
            com.funambol.framework.server.Capabilities.class,
            "Capabilities"
        );
        mapType(
            call,
            com.funambol.framework.core.SyncType.class,
            "SyncType"
        );
        mapType(
            call,
            com.funambol.framework.core.DSMem.class,
            "DSMem"
        );
        mapType(
            call,
            com.funambol.framework.core.FilterCap.class,
            "FilterCap"
        );
        mapType(
            call,
            com.funambol.framework.core.Property.class,
            "Property"
        );
        mapType(
            call,
            com.funambol.framework.core.PropParam.class,
            "PropParam"
        );
        mapType(
            call,
            com.funambol.server.update.Component.class,
            "Component"
        );
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
            new org.apache.axis.encoding.ser.BeanSerializerFactory(type, q),
            new org.apache.axis.encoding.ser.BeanDeserializerFactory(type, q)
        );
    }
}
