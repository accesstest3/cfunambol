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
package com.funambol.admin.main;

import com.funambol.server.admin.UnauthorizedException;
import java.util.Hashtable;
import java.util.Map;

import org.apache.log4j.Appender;

import com.funambol.framework.config.LoggerConfiguration;
import com.funambol.framework.config.LoggingConfiguration;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.security.Sync4jPrincipal;

import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.LastTimestamp;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.Sync4jModule;
import com.funambol.framework.server.Sync4jSource;
import com.funambol.framework.tools.beans.BeanFactory;

import com.funambol.server.admin.ws.client.AdminWSClient;
import com.funambol.server.admin.ws.client.ServerInformation;
import com.funambol.server.config.ServerConfiguration;
import com.funambol.server.update.Component;

import com.funambol.admin.AdminException;
import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.util.Log;
import com.funambol.admin.util.ErrorManager;

/**
 * Manages the interaction with the business WS.
 *
 * @version $Id: BusinessDelegate.java,v 1.16 2007-11-28 10:28:17 nichele Exp $
 */
public class BusinessDelegate {

    // ------------------------------------------------------------ Private data

    /** Configuration of the syncServer */
    private AdminConfig config = null;

    /** Cache of the user's roles */
    private Hashtable rolesCache = null;

    /** The class loader to use to remotely download server side classes */
    private ClassLoader cl = null;

    /** Bean used to access server web services */
    private AdminWSClient adminWSClient = null;

    // ---------------------------------------------------------- Public Methods

    /**
     * Initializes this <code>BusinessDelegate</code> object.
     *
     * @param cl the Funambol data synchronization server class loader
     *           (used to download classes from the Funambol data
     *            synchronization server).
     * @param serverConfiguration HostConfiguration of the syncServer
     *
     * @throws AdminException if an error occurs
     */
    public void init(ClassLoader cl) throws AdminException {
        Log.debug("Initializing BusinessDelegate (1)");

        this.config = AdminConfig.getAdminConfig();

        ServerInformation serverInformation = new ServerInformation(
                config.getWSEndpoint(),
                config.getUser(),
                config.getPassword()
                );

        this.adminWSClient = new AdminWSClient(serverInformation, null);

        this.cl = cl;
    }

    /**
     * Returns the roles of the users.
     *
     * @param useCache if true uses the results in cache (if there are), otherwise calls always the WS method.
     * @throws AdminException if a error occurs
     * @return Hashtable that contains the roles
     */
    public Hashtable getRoles(boolean useCache) throws AdminException {
        String[] roles = null;

        if (!useCache || rolesCache == null) {

            roles = getRolesFromServer();

            rolesCache = new Hashtable();

            // store roles in cache
            int numRoles = roles.length;

            String roleId;
            String roleDescription;
            int index = -1;
            for (int i = 0; i < numRoles; i++) {
                index = roles[i].indexOf(" ");
                roleId = roles[i].substring(0, index);
                roleDescription = roles[i].substring(index + 1);
                rolesCache.put(roleId, roleDescription);
            }
        }

        return rolesCache;
    }

    /** Calls getUsers method of the WS     */
    public Sync4jUser[] getUsers(Clause clause)
    throws AdminException {
        try {
            return this.adminWSClient.getUsers(clause);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls addUser method of the WS     */
    public void addUser(Sync4jUser u) throws AdminException {
        try {
            this.adminWSClient.addUser(u);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls setUser method of the WS     */
    public void setUser(Sync4jUser u) throws AdminException {
        try {
            this.adminWSClient.setUser(u);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls deleteUser method of the WS     */
    public void deleteUser(String userName) throws AdminException {
        try {
            this.adminWSClient.deleteUser(userName);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls countUsers method of the WS     */
    public int countUsers(Clause clause) throws AdminException {
        try {
            return this.adminWSClient.countUsers(clause);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls getDevices method of the WS     */
    public Sync4jDevice[] getDevices(Clause clause)
    throws AdminException {
        try {
            return this.adminWSClient.getDevices(clause);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls getDevicesCapabilities method of the WS     */
    public Capabilities getDeviceCapabilities(String deviceId)
    throws AdminException {
        try {
            return this.adminWSClient.getDeviceCapabilities(deviceId);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls setDevicesCapabilities method of the WS     */
    public Long setDeviceCapabilities(Capabilities caps, String deviceId)
    throws AdminException {
        try {
            Long idCaps = this.adminWSClient.setDeviceCapabilities(caps, deviceId);
            return idCaps;

        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls addDevice method of the WS     */
    public String addDevice(Sync4jDevice d)
        throws AdminException {
        try {
            return this.adminWSClient.addDevice(d);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls setDevice method of the WS     */
    public void setDevice(Sync4jDevice d)
        throws AdminException {
        try {
            this.adminWSClient.setDevice(d);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls deleteDevice method of the WS     */
    public void deleteDevice(String deviceId)
        throws AdminException {
        try {
            this.adminWSClient.deleteDevice(deviceId);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls countDevices method of the WS     */
    public int countDevices(Clause clause)
    throws AdminException {
        try {
            return this.adminWSClient.countDevices(clause);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls getPrincipals method of the WS     */
    public Sync4jPrincipal[] getPrincipals(Clause clause)
    throws AdminException {
        try {
            return this.adminWSClient.getPrincipals(clause);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls addPrincipal method of the WS     */
    public long addPrincipal(Sync4jPrincipal p) throws AdminException {
        try {
            return this.adminWSClient.addPrincipal(p);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls deletePrincipal method of the WS     */
    public void deletePrincipal(long principalId)
        throws AdminException {
        try {
            this.adminWSClient.deletePrincipal(principalId);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls countPrincipals method of the WS     */
    public int countPrincipals(Clause clause)
    throws AdminException {
        try {
            return this.adminWSClient.countPrincipals(clause);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls getLastTimestamps method of the WS     */
    public LastTimestamp[] getLastTimestamps(Clause clause)
    throws AdminException {
        try {
            return this.adminWSClient.getLastTimestamps(clause);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls deleteLastTimestamp method of the WS     */
    public void deleteLastTimestamp(long principalId, String syncSourceId)
        throws AdminException {
        try {
            this.adminWSClient.deleteLastTimestamp(principalId, syncSourceId);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls countLastTimestamps method of the WS     */
    public int countLastTimestamps(Clause clause)
    throws AdminException {
        try {
            return this.adminWSClient.countLastTimestamps(clause);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls getModulesName method of the WS     */
    public Sync4jModule[] getModulesName()
        throws AdminException {
        try {
            return (Sync4jModule[])adminWSClient.invoke("getModulesName", new Object[] {});
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }


    /** getSync4jSource method of the WS     */
    public Sync4jSource[] getSync4jSources(Clause clause)
        throws AdminException {
        try {
            return (Sync4jSource[])adminWSClient.invoke("getSync4jSources", new Object[] { BeanFactory.marshal(clause)});
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls getModule method of the WS     */
    public Sync4jModule getModule(String moduleId)
        throws AdminException {
        try {
            return (Sync4jModule)BeanFactory.unmarshal(cl, (String)adminWSClient.invoke("getModule", new Object[] { moduleId }));
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls getSyncSourceClasses method of the WS     */
    public String[] getSyncSourceClasses(String[] ssTypeIds)
        throws AdminException {
        try {
            String[] classes = (String[])adminWSClient.invoke("getSyncSourceClasses", new Object[] {ssTypeIds});
            return classes;
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls addSource method of the WS     */
    public void addSource(String moduleId,
                          String connectorId,
                          String sourceTypeId,
                          SyncSource source)
    throws AdminException {
        try {
            adminWSClient.invoke("addSource", new Object[] { moduleId, connectorId, sourceTypeId, BeanFactory.marshal(cl, source) });
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls setSource method of the WS     */
    public void setSource(String moduleId, String connectorId, String sourceTypeId, SyncSource source)
        throws AdminException {
        try {
            adminWSClient.invoke("setSource", new Object[] { moduleId, connectorId, sourceTypeId, BeanFactory.marshal(cl, source) });
        }  catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls deleteSource method of the WS     */
    public void deleteSource(String sourceUri)
        throws AdminException {
        try {
            adminWSClient.invoke("deleteSource", new Object[] {sourceUri});
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /**
     * Returns the server version
     *
     * @return the server version
     *
     * @throws AdminException in case of errors
     */
    public String getServerVersion()
        throws AdminException {
        try {
            return this.adminWSClient.getServerVersion();
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /**
     * Returns the latest ds server update
     *
     * @return the latest ds server update
     *
     * @throws AdminException in case of errors
     */
    public Component getLatestDSServerUpdate()
        throws AdminException {
        try {
            return (Component)adminWSClient.invoke("getLatestDSServerUpdate", new Object[0]);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /**
     * Attempts to connect to the server WS.
     *
     * @throws UnauthorizedException if un'authorized is returned by the server
     */
    public void login() throws AdminException, UnauthorizedException {
        try {
            this.adminWSClient.invoke("login", new Object[] { config.getUser() });
        } catch (UnauthorizedException e){
            throw e;
        } catch (com.funambol.server.admin.AdminException e){
            throw new AdminException(null, e);
        }
    }

    /**
     * Returns the server logging settings
     *
     * @return the server logging settings
     *
     * @throws AdminException in case of errors
     */
    public LoggingConfiguration getLoggingSettings() throws AdminException {
        try {
            return (LoggingConfiguration)
                adminWSClient.invoke("getLoggingConfiguration", new Object[] {});
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /**
     * Returns the server logger cofiguration objects
     *
     * @return the server logger cofiguration objects
     *
     * @throws AdminException in case of errors
     */
    public LoggerConfiguration[] getLoggers()
    throws AdminException {
        try {
            return (LoggerConfiguration[])
                adminWSClient.invoke("getLoggers", new Object[] {});
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /**
     * Returns a map with all available <code>Appender</code> objects
     * on the server.
     *
     * @return a map with all available <code>Appender</code>
     *         objects on the server.
     *
     * @throws AdminException in case of errors
     */
    public Map getAppenders()
    throws AdminException {
        try {
            String appendersXML =
                (String)adminWSClient.invoke("getAppenders", new Object[] {});
            Map appenders       =
                (Map)BeanFactory.unmarshal(cl, appendersXML);
            return appenders;
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }

    }

    /**
     * Sets on the server the given appender
     * @param appender the appender to set
     */
    public void setAppender(Appender appender)
    throws AdminException {
        try {
            adminWSClient.invoke("setAppender",
                      new Object[] {BeanFactory.marshal(cl, appender) });
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }


    /**
     * Returns the class name of the management panel to configure an appender
     * with the given class name.
     *
     * @param the appender class name
     * @return the class name of the management panel
     */
    public String getAppenderManagementPanel(String appenderClassName)
    throws AdminException {
        try {
            String appenderManagementPanel =
                (String)adminWSClient.invoke("getAppenderManagementPanel",
                                  new Object[] {appenderClassName});
            return appenderManagementPanel;
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /**
     * Saves the given logging settings on the server
     *
     * @param config the new logging settings
     *
     * @throws AdminException in case of errors
     */
    public void setLoggingSettings(LoggingConfiguration config)
    throws AdminException {
        try {
            adminWSClient.invoke("setLoggingConfiguration", new Object[] {config});
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /**
     * Saves the given logger configuration on the server
     *
     * @param config the logger configuration to save
     * @throws AdminException in case of errors
     */
    public void setLoggerConfiguration(LoggerConfiguration config)
    throws AdminException {
        try {
            adminWSClient.invoke("setLoggerConfiguration",
                      new Object[] {BeanFactory.marshal(cl, config) });
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /**
     * Returns the server configuration settings
     *
     * @return the server configuration settings
     *
     * @throws AdminException in case of errors
     */
    public ServerConfiguration getServerConfiguration() throws AdminException {
        try {
            ServerConfiguration serverConfig = this.adminWSClient.getServerConfiguration();
            return serverConfig;
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
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
        try {
            this.adminWSClient.setServerConfiguration(serverConfig);
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }


    /** Calls getServerBean method of the WS     */
    public Object getServerBean(String bean) throws AdminException {
        try {
            return BeanFactory.unmarshal(cl, (String)adminWSClient.invoke("getServerBean", new Object[] { bean }));
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    /** Calls setServerBean method of the WS     */
    public void setServerBean(String bean, Object obj) throws AdminException {
        try {
            adminWSClient.invoke("setServerBean", new Object[] { bean, BeanFactory.marshal(cl, obj) });
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }

    // --------------------------------------------------------- Private methods

    /** Calls getRoles method of the WS */
    private String[] getRolesFromServer() throws AdminException {
        try {
            return this.adminWSClient.getRoles();
        } catch (Throwable t) {
            throw ErrorManager.manageException(t);
        }
    }



}
