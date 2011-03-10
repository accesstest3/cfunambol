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
package com.funambol.server.session;

import com.funambol.framework.core.Constants;
import com.funambol.framework.tools.beans.BeanException;
import java.security.Principal;

import java.util.Date;
import java.util.Map;

import com.funambol.server.engine.*;

import com.funambol.framework.core.Cred;
import com.funambol.framework.core.DevInf;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.core.VerDTD;
import com.funambol.framework.database.Database;
import com.funambol.framework.engine.source.MemorySyncSource;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.filter.FilterClause;
import com.funambol.framework.logging.LogContext;
import com.funambol.framework.protocol.CommandIdGenerator;
import com.funambol.framework.security.AbstractOfficer;
import com.funambol.framework.security.Officer;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.SyncTimestamp;
import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.server.config.Configuration;
import com.funambol.server.config.ServerConfiguration;
import com.funambol.server.inventory.PSDeviceInventory;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * This class is a mock for the Sync4jEngine.
 * Pay attention if you decide to remove/add methods,
 * check that all tests using this class continue to work.
 *
 * @version $Id: Sync4jEngineMock.java 33301 2010-01-15 14:40:44Z luigiafassina $
 */
public class Sync4jEngineMock extends Sync4jEngine {

    private Sync4jUser fakeUser;
    private Configuration configuration;
    private boolean failAuthentication = false;
    private String  sessionId = null;


    /**
     * Create a new Sync4jEngineMock with the given configuration, user,
     * sessionId and authentication flag.
     *
     * @param configuration is the configuration
     * @param fakeUser is the user this engine will return when a
     * @param failAuthentication is the flag that decides if the authentication fails
     * @param sessionId is the id of the session (will be set inside the LogContext)
     */
    public Sync4jEngineMock(Configuration configuration, Sync4jUser fakeUser, String sessionId,boolean failAuthentication) {
        this.configuration      = configuration;
        this.fakeUser           = fakeUser;
        this.failAuthentication = failAuthentication;
        this.sessionId          =sessionId;
    }
    /**
     * Create a new Sync4jEngineMock with the given configuration, user and
     * authentication flag.
     * Using this constructor, the resulting object will authenticate user.
     *
     * @param configuration is the configuration
     * @param fakeUser is the user this engine will return when a
     * @param sessionId is the id of the session (will be set inside the LogContext)
     */
    public Sync4jEngineMock(Configuration configuration, Sync4jUser fakeUser, String sessionId) {
        this(configuration,fakeUser,sessionId,false);
    }

    //  FAKE EMPTY METHODS
    public boolean readDevice(Sync4jDevice device) {
        return true;
    }

    public void storeDevice(Sync4jDevice device) {
    }

    public void setSyncMLVerProto(String syncMLVerProto) {
    }

    public void setSyncTimestamp(Date syncTimestamp) {
    }

    public void setPrincipal(Sync4jPrincipal principal) {
    }

    public void setCommandIdGenerator(CommandIdGenerator cmdIdGenerator) {
    }

    public void logout(Sync4jUser user, Cred credentials) {
    }

    public void setFilter(String sourceUri, FilterClause filter) {
    }

    public void setDbs(Map dbs) {
    }

    public void setDbs(Database[] dbs) {
    }

    public void storeMappings() {
    }

    public void sync(Sync4jPrincipal principal) throws Sync4jException {
    }

    public void endSync()  {
    }

    public void readPrincipal(Sync4jPrincipal principal)
    throws PersistentStoreException {
    }

    public boolean readDeviceWithoutCapabilities(Sync4jDevice device)  {
         return true;
    }

    // OVERRIDEN METHODS
    public Sync4jUser login(Cred credentials) {
        if (!isFailAuthentication()) {
            LogContext.setUserName(getFakeUser().getUsername());
            LogContext.setSessionId(sessionId);
            return getFakeUser();
        } else {
            LogContext.setUserName(null);
            LogContext.setSessionId(null);
            return null;
        }
    }

    public Officer getOfficer() {
        return new AbstractOfficer() {

            public String getClientAuth() {
                return Cred.AUTH_TYPE_BASIC;

            }

            @Override
            public Sync4jUser authenticateUser(Cred credentials) {
                return getFakeUser();
            }
        };
    }

    public Officer.AuthStatus authorizeSession(Principal principal, String sessionId) {
        return Officer.AuthStatus.AUTHORIZED;
    }

    public void prepareDatabases(Sync4jPrincipal principal,
            Database[] dbs,
            SyncTimestamp next) {
    }

    public SyncSource getClientSource(String name) {
        return new MemorySyncSource("cal", "cal");
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public DeviceInventory getDeviceInventory() {
        return new PSDeviceInventory();
    }

    @Override
    public DevInf getServerCapabilities(VerDTD verDTD) {
        DevInf devInf = null;
        try {
            //
            // every time a new instance of the serverInfo is created
            // because for ver dtd 1.0 the UTC, SupportLargeObjects and
            // SupportNumberOfChanges must be set on false
            // in order not to be included in the response message
            //
            devInf = ((ServerConfiguration) configuration
                .getBeanInstanceByName(BEAN_SERVER_CONFIGURATION, false))
                .getServerInfo();
            devInf.setDataStores(new ArrayList(0));
            devInf.setVerDTD(verDTD);
            if (Constants.DTD_1_0.equals(verDTD)) {
                //
                // set on false the values for:
                //     <UTC>
                //     <SupportLargeObjs>
                //     <SupportNumberOfChanges>
                // if the vers dtd is 1.0
                //
                devInf.setUTC(Boolean.FALSE);
                devInf.setSupportLargeObjs(Boolean.FALSE);
                devInf.setSupportNumberOfChanges(Boolean.FALSE);
            }
        } catch (Exception e) {
        }
        return devInf;
    }

    // METHOD ADDED TO THE MOCK

    /**
     * @return the fakeUser
     */
    public Sync4jUser getFakeUser() {
        return fakeUser;
    }

    /**
     * @param fakeUser the fakeUser to set
     */
    public void setFakeUser(Sync4jUser fakeUser) {
        this.fakeUser = fakeUser;
    }

    /**
     * @return the failAuthentication
     */
    public boolean isFailAuthentication() {
        return failAuthentication;
    }

    /**
     * @param failAuthentication the failAuthentication to set
     */
    public void setFailAuthentication(boolean failAuthentication) {
        this.failAuthentication = failAuthentication;
    }
}
