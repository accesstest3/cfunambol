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
package com.funambol.pushlistener.service.ws;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

import org.apache.log4j.Logger;

import com.funambol.pushlistener.service.ws.WSServerInformation;

/**
 * This class groups some utility methods for WS invoking and handling
 * @deprecated From version 7.1.0 use {@link #com.funambol.server.admin.ws.client.AdminWSClient}
 * @version $Id: WSTools.java,v 1.6 2008-02-25 15:46:01 luigiafassina Exp $
 */
@Deprecated
public class WSTools {
    // --------------------------------------------------------------- Constants

    public static final String FAULT_UNAUTHORIZED = "(401)Unauthorized";

    public static final String FAULT_DATA_STORE   = "DataStoreException";

    // ------------------------------------------------------------ Private data

    /** The logger */
    private static Logger log = Logger.getLogger("funambol.pushlistener.ws");

    /** The WSServer information */
    private WSServerInformation serverInformation = null;
    // ------------------------------------------------------------ Constructors


    /**
     * Creates a new WSTools with the given server information
     *
     * @param serverInformation the server information
     */
    public WSTools(WSServerInformation serverInformation) {
        this.serverInformation = serverInformation;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Invokes the given WS method with the given objects.
     * @return the object returned by the invoked method
     * @param methodName the method to invoke
     * @param args the call arguments
     * @throws com.funambol.pushlistener.service.ws.UnexpectedException if an unexpected
     *         error occurs performing a WS call
     * @throws com.funambol.pushlistener.service.ws.ServerException if the server
     *         returns a com.funambol.server.admin.ServerException processing the
     *         WS call
     * @throws com.funambol.pushlistener.service.ws.AdminException if the server
     *         returns a com.funambol.server.admin.AdminException processing the
     *         WS call
     * @throws com.funambol.pushlistener.service.ws.UnauthorizedException if the
     *         server returns an unauthorized error
     */
    public Object invoke(final String methodName, Object[] args)
    throws UnexpectedException, ServerException, AdminException, UnauthorizedException {
        Service  service = new Service();

        if (log.isTraceEnabled()) {
            log.trace("Invoking " + methodName + " on " + serverInformation.getUrl());
        }

        URL url = serverInformation.getUrl();
        String userName = serverInformation.getUsername();
        String password = serverInformation.getPassword();

        try {
           Call call = (Call) service.createCall();

           mapTypes(call);

           call.setTargetEndpointAddress(url);
           call.setOperationName(methodName);
           call.setUsername(userName);
           call.setPassword(password);

           return call.invoke(args);

        } catch (AxisFault f) {

            //
            // The exceptions are handled with the following roles:
            // 1. If there is a cause, we throw an UnexpectedException exception with
            //    the cause.
            // 2. If the faultString is FAULT_UNAUTHORIZED we throw an UnauthorizedException
            // 3. If the faultCode is Server.userException we have to check if the exception
            //    is an com.funambol.server.admin.AdminException or a
            //    com.funambol.framework.server.error.ServerException.
            //    If it is a com.funambol.server.admin.AdminException, a 
            //    com.funambol.pushlistener.service.ws.AdminException is thrown
            //    using the same message.
            //    If it is a com.funambol.framework.server.error.ServerException, a 
            //    com.funambol.pushlistener.service.ws.ServerException is thrown
            //    using the same message.
            // 4. Otherwise a com.funambol.pushlistener.service.ws.UnexpectedException
            //    is thrown.
            //
            String errorMessage = null;

            Throwable cause = f.getCause();

            if (cause != null) {
                throw new UnexpectedException(cause);
            }

            if (FAULT_UNAUTHORIZED.equals(f.getFaultString())) {

               throw new UnauthorizedException("User '" + userName+ "' not authorized");

            } else if (Constants.FAULT_SERVER_USER.equals(f.getFaultCode().getLocalPart())) {

                String faultString  = f.getFaultString();

                if (faultString != null) {
                    if (faultString.startsWith("com.funambol.server.admin.AdminException:")) {
                        int len = "com.funambol.server.admin.AdminException: ".length();
                        errorMessage = faultString.substring(len);
                        throw new AdminException(errorMessage);
                    } else if (faultString.startsWith("com.funambol.framework.server.error.ServerException:")) {
                        int len = "com.funambol.framework.server.error.ServerException: ".length();
                        errorMessage = faultString.substring(len);
                        throw new ServerException(errorMessage);
                    } else {
                        throw new UnexpectedException(faultString);
                    }
                }
            }

            if (f.getMessage() != null && !f.getMessage().equals("")) {
                throw new UnexpectedException(f.getMessage(), f);
            } else {
                throw new UnexpectedException(f);
            }

        } catch (Exception e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
    }


    // --------------------------------------------------------- Private methods

    /**
     * Maps the java types to the serializer-deserializer to use (the AXIS
     * BeanSerializer/Deserializer).
     * No types are mapped at the moment
     *
     * @param call the call on which perform the mappings
     *
     */
    private void mapTypes(Call call) {
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

}
