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

import com.funambol.framework.config.ConfigurationException;
import com.funambol.framework.core.Sync4jException;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.Sync4jLoggerName;
import com.funambol.framework.server.SyncResponse;
import com.funambol.framework.server.error.ServerException;

import com.funambol.server.config.Configuration;
import com.funambol.server.engine.SyncAdapter;

/**
 * Implements a <i>SyncHolder</i> that instance a SyncAdapter to handle a
 * synchronization request.
 *
 * @version $Id: LocalSyncHolder.java,v 1.1.1.1 2008-02-21 23:36:08 stefano_fornari Exp $
 */
public class LocalSyncHolder implements SyncHolder {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data

    private SyncAdapter syncAdapter       = null;
    private long        creationTimestamp       ;
    private String      sessionId               ;

    // ------------------------------------------------------------ Constructors
    public LocalSyncHolder() throws ServerException {
        try {
            syncAdapter = new SyncAdapter(loadConfiguration());
        } catch (ConfigurationException e) {

        }
        creationTimestamp = System.currentTimeMillis();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Processes an incoming XML message.
     *
     * @param requestURI the uri of the request
     * @param requestData the SyncML request as an array of bytes
     * @param parameters SyncML request parameters
     * @param headers SyncML request headers
     *
     * @return the SyncML response as a <i>ISyncResponse</i> object
     *
     * @throws ServerException in case of a server error
     *
     */
    public SyncResponse processXMLMessage(final String requestURI,
                                          final byte[] requestData,
                                          final Map    parameters ,
                                          final Map    headers    )
    throws ServerException {
        return syncAdapter.processXMLMessage(requestURI, requestData, parameters, headers);
    }

    /** Processes an incoming WBXML message.
     *
     * @param requestURI the uri of the request
     * @param requestData the SyncML request as an array of bytes
     * @param parameters SyncML request parameters
     * @param headers SyncML request headers
     *
     * @return the SyncML response as a <i>ISyncResponse</i> object
     *
     * @throws ServerException in case of a server error
     *
     */
    public SyncResponse processWBXMLMessage(final String requestURI,
                                            final byte[] requestData,
                                            final Map    parameters ,
                                            final Map    headers    )

    throws ServerException {
        return syncAdapter.processWBXMLMessage(requestURI, requestData, parameters, headers);
    }

    public void setSessionId(String sessionId) throws Sync4jException {
        this.sessionId = sessionId;
        syncAdapter.beginSync(sessionId);
    }

    public String getSessionId() {
        return this.sessionId;
    }

    /** Called when the SyncHolder is not required any more. It gives the holder
     * an opportunity to release and clean up resources.
     *
     * @throws java.lang.Exception in case of error. The real exception is stored
     * in the cause.
     *
     */
    public void close() throws Exception {
        syncAdapter.endSync();
    }

    /**
     * Returns the creation timestamp (in milliseconds since midnight, January
     * 1, 1970 UTC).
     *
     * @see com.funambol.transport.http.server.SyncHolder
     */
    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    /**
     * Loads the configuration for the server.
     *
     * @throws ConfigurationException in case of errors.
     */
    private Configuration loadConfiguration()
    throws ConfigurationException {

        Configuration config = Configuration.getConfiguration();

        //
        // If config is null, being Configuration a singleton, only an exception
        // could have been happend.
        //
        if (config == null) {
            throw new ConfigurationException("Error loading configuration (see the log for details)!");
        }

        return config;
    }
}
