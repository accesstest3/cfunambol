/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.transport.http.server;

import java.util.Map;

import javax.servlet.http.*;

import com.funambol.framework.server.SyncResponse;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.server.error.ServerException;
import com.funambol.framework.server.error.NotImplementedException;
import com.funambol.framework.protocol.ProtocolException;


/**
 * This is an interface of sync holder. A sync holder hides the implementation
 * of the real service provider. Thus the provider can be developed as a local
 * EJB, a remote EJB or a simple local object.
 *
 *
 * @version $Id: SyncHolder.java,v 1.1.1.1 2008-02-21 23:36:09 stefano_fornari Exp $
 */
public interface SyncHolder {
    // --------------------------------------------------------------- Constants

    // ---------------------------------------------------------- Public methods

    public void setSessionId(String sessionId) throws Sync4jException;

    public String getSessionId();

    /**
     * Processes an incoming XML message.
     *
     * @param requestURI the uri of the request
     * @param msg the SyncML request as stream of bytes
     * @param parameters SyncML request parameters
     * @param headers SyncML request headers
     *
     * @return the SyncML response as a <i>ISyncResponse</i> object
     *
     * @throws ServerException in case of a server error
     *
     */
    public SyncResponse processXMLMessage(final String requestURI ,
                                          final byte[] msg        ,
                                          final Map    parameters ,
                                          final Map    headers    )
    throws NotImplementedException, ProtocolException, ServerException;

    /**
     * Processes an incoming WBXML message.
     *
     * @param requestURI the uri of the request
     * @param msg the SyncML request as stream of bytes
     * @param parameters SyncML request parameters
     * @param headers SyncML request headers
     *
     * @return the SyncML response as a <i>ISyncResponse</i> object
     *
     * @throws ServerException in case of a server error
     *
     */
    public SyncResponse processWBXMLMessage(final String requestURI ,
                                            final byte[] msg        ,
                                            final Map    parameters ,
                                            final Map    headers    )
    throws NotImplementedException, ProtocolException, ServerException;

    /**
     * Called when the SyncHolder is not required any more. It gives the holder
     * an opportunity to do clean up and releaseing of resources.
     *
     * @throws java.lang.Exception in case of error. The real exception is stored
     * in the cause.
     */
    public void close() throws Exception;

    /**
     * Returns the creation timestamp (in milliseconds since midnight, January
     * 1, 1970 UTC).
     */
    public long getCreationTimestamp();
}
