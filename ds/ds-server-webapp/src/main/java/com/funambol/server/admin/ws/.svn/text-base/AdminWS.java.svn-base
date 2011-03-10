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
package com.funambol.server.admin.ws;

import java.util.List;

import org.apache.log4j.Appender;

import com.funambol.framework.config.*;
import com.funambol.framework.core.*;

import com.funambol.framework.engine.source.*;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.logging.*;
import com.funambol.framework.notification.Message;
import com.funambol.framework.security.*;
import com.funambol.framework.server.*;
import com.funambol.framework.server.error.*;
import com.funambol.framework.tools.beans.*;

import com.funambol.server.admin.*;
import com.funambol.server.config.*;
import com.funambol.server.update.Component;

/**
 * This is the session enterprise java bean that handles the administration
 * console. It is designed to be a stateless session bean.
 * <p>
 *  This server accepts the requests addressed to the hostname
 *  indicated by the configuration property pointed by {CONFIG_SERVER_URI} (see
 *  Funambol.xml).
 * <p>
 * AdminBean uses the system property funambol.configpath to set the base of the
 * config path.
 *
 * @version $Id$
 */
public class AdminWS {

    // ------------------------------------------------------------ Private data
    /** The admin manager */
    private AdminManager             admin;

    /** The logger */
    private transient FunambolLogger log = FunambolLoggerFactory.getLogger();

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new AdminWS instance. This will initialize <i>admin</i>
     *
     * @throws ServerException if the AdminManager cannot be created.
     *
     */
    public AdminWS() {
        admin = new AdminManager();
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Checks if user is authorized to use the given resource.
     *
     * @param authCred the user credential
     * @param resource the resource to check
     *
     * @return the AuthorizeResponse with the result of the check
     */
    public AuthorizationResponse authorizeCredential(Credential authCred,
                                                     String     resource)
    throws ServerException {
        try {

            return admin.authorizeCredential(authCred, resource);

        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * This method is only used to check credentials. Being this WS stateless,
     * its methods are always called on request and each of them has to pass
     * authentication. However, for instance the SyncAdmin, has a login panel
     * that is displayed before any access to one of the other WS methods. In
     * order to provide to the user an immediate feedback, the SyncAdmin (or any
     * other client can just call <i>login()</i>.
     *
     * @param username administrator username
     */
    public void login(String username) {
        if (log.isInfoEnabled()) {
            log.info("New administrative session for " + username);
        }
    }

    /**
     * Read the list of roles available.
     *
     * @return names of roles available
     *
     * @throws ServerException
     * @throws AdminException
     */
    public String[] getRoles()
    throws ServerException, AdminException {
        return admin.getRoles();
    }

    /**
     * Read all users that satisfy the parameter of search.
     *
     * @param clause array of conditions for the query
     *
     * @return array of Sync4jUser
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Sync4jUser[] getUsers(String clause)
    throws ServerException, AdminException {
        try {
            return admin.getUsers((Clause)BeanFactory.unmarshal(clause));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Adds a new user and the assigned role to it
     *
     * @param user the user to add
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void addUser(Sync4jUser user)
    throws ServerException, AdminException {
        try {
            admin.addUser(user);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Update the information of the specific user
     *
     * @param user the user with informations updated
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setUser(Sync4jUser user)
    throws ServerException, AdminException {
        try {
            admin.setUser(user);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Delete the user
     *
     * @param userName the name of user to delete
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void deleteUser(String userName)
    throws ServerException, AdminException {
        try {
            admin.deleteUser(userName);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Count the number of users that satisfy the specif clauses.
     *
     * @param clause the conditions of the search
     *
     * @return number of users
     *
     * @throws ServerException
     * @throws AdminException
     */
    public int countUsers(String clause)
    throws ServerException, AdminException {
        try {
            return admin.countUsers((Clause)BeanFactory.unmarshal(clause));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Read a list of device that satisfy the specific conditions.
     *
     * @param clauses the conditions foe search informations.
     *
     * @return array of device
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Sync4jDevice[] getDevices(String clauses)
    throws ServerException, AdminException {
        try {
            return admin.getDevices((Clause)BeanFactory.unmarshal(clauses));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Adds a new device.
     *
     * @param d the new device
     *
     * @return the device id
     *
     * @throws ServerException
     * @throws AdminException
     */
    public String addDevice(Sync4jDevice d)
    throws ServerException, AdminException {
        try {
            return admin.addDevice(d);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }


    /**
     * Retrieves the device with the given id.
     *
     * @param id the device id
     *
     * @return the retrieved device or null if no devices were found
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Sync4jDevice getDevice(String deviceId)
    throws ServerException, AdminException {
        try {
            return admin.getDevice(deviceId);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Read a device's capabilities for the specific device.
     *
     * @param deviceId the identifier of device
     *
     * @return device's capabilities into xml format
     *
     * @throws ServerException
     * @throws AdminException
     */
    public String getDeviceCapabilities(String deviceId)
    throws ServerException, AdminException {

        try {
            Capabilities caps    = admin.getDeviceCapabilities(deviceId);
            String       capsXML = BeanFactory.marshal(caps);
            return capsXML;
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Set a device's capabilities for a specific device.
     *
     * @param caps     the capabilities to insert
     * @param deviceId the identifier of the device
     *
     * @return the identifier of the inserted caoabilities
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Long setDeviceCapabilities(String caps, String deviceId)
    throws ServerException, AdminException {

        try {

            Capabilities capsObj = (Capabilities)BeanFactory.unmarshal(caps);

            admin.setDeviceCapabilities(capsObj, deviceId);

            return capsObj.getId();

        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Update the information of specific device.
     *
     * @param d the device to update
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setDevice(Sync4jDevice d)
    throws ServerException, AdminException {
        try {
            admin.setDevice(d);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Delete the specific device.
     *
     * @param deviceId the device id that identifies the device
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void deleteDevice(String deviceId)
    throws ServerException, AdminException{
        try {
            admin.deleteDevice(deviceId);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Count the number of device that satisfy the specific clauses.
     *
     * @param clauses the specific conditions for search device
     *
     * @return the number of device finds
     *
     * @throws ServerException
     * @throws AdminException
     */
    public int countDevices(String clauses)
    throws ServerException, AdminException {
        try {
            return admin.countDevices((Clause)BeanFactory.unmarshal(clauses));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }

    }

    /**
     * Read all principal that satisfy the clauses.
     *
     * @param clauses the specific conditions for search principal
     *
     * @return array of principal
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Sync4jPrincipal[] getPrincipals(String clauses)
    throws ServerException, AdminException {
        try {
            return admin.getPrincipals((Clause)BeanFactory.unmarshal(clauses));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Adds a new principal.
     *
     * @param p the new principal
     *
     * @return the principal id
     *
     * @throws ServerException
     * @throws AdminException
     */
    public long addPrincipal(Sync4jPrincipal p)
    throws ServerException, AdminException {
        try {
            return admin.addPrincipal(p);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Delete the specific principal.
     *
     * @param principalId the principal id that identifies the principal
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void deletePrincipal(long principalId)
    throws ServerException, AdminException {
        try {
            admin.deletePrincipal(principalId);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Count the number of principal that satisfy the specific clauses.
     *
     * @param clauses the specific conditions for search principal
     *
     * @return the number of principal finds
     *
     * @throws ServerException
     * @throws AdminException
     */
    public int countPrincipals(String clauses)
    throws ServerException, AdminException {
        try {
            return admin.countPrincipals((Clause)BeanFactory.unmarshal(clauses));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Read all last timestamp that satisfy the clauses.
     *
     * @param clauses the specific conditions for search last timestamps
     *
     * @return array of last timestamp
     *
     * @throws ServerException
     * @throws AdminException
     */
    public LastTimestamp[] getLastTimestamps(String clauses)
    throws ServerException, AdminException {
        try {
            return admin.getLastTimestamps((Clause)BeanFactory.unmarshal(clauses));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Delete the specific last timestamp.
     *
     * @param principalId the principal id that identifies the principal
     * @param sourceId the source id that identifies the principal
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void deleteLastTimestamp(long principalId, String sourceId)
    throws ServerException, AdminException {
        try {
            admin.deleteLastTimestamp(principalId, sourceId);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Count the number of last timestamps that satisfy the specific clauses.
     *
     * @param clauses the specific conditions for search last timestamp
     *
     * @return the number of last timestamps finds
     *
     * @throws ServerException
     * @throws AdminException
     */
    public int countLastTimestamps(String clauses)
    throws ServerException, AdminException {
        try {
            return admin.countLastTimestamps((Clause)BeanFactory.unmarshal(clauses));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Read all modules information
     *
     * @return an array with the modules information (empty if no objects are found)
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Sync4jModule[] getModulesName()
    throws ServerException, AdminException {
        try {
            return admin.getModulesName();
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Read the information of the specific module
     *
     * @param moduleId the module id that identifies the module to search
     *
     * @return the relative information to the module
     *
     * @throws ServerException
     * @throws AdminException
     */
    public String getModule(String moduleId)
    throws ServerException, AdminException {
        try {
            return BeanFactory.marshal(admin.getModule(moduleId));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Adds a new source into datastore and create a relative file xml with configuration.
     * The source must have a defined source type.
     * The source type must refer to a connector.
     * The connector must refer to a module.
     *
     * @param moduleId the module id
     * @param connectorId the connector id
     * @param sourceTypeId the source type id
     * @param source the information of the new source
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void addSource(String moduleId, String connectorId, String sourceTypeId, String source)
    throws ServerException, AdminException {
        try {
            admin.addSource(moduleId, connectorId, sourceTypeId, (SyncSource)BeanFactory.unmarshal(source));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }


    /**
     * Read a list of syncSource that satisfy the specific conditions.
     *
     * @param clauses the conditions to search informations.
     *
     * @return array of syncSource
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Sync4jSource[] getSync4jSources(String clauses)
    throws ServerException, AdminException {
        try {
            return admin.getSync4jSources((Clause)BeanFactory.unmarshal(clauses));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Returns an array with the classes relative to the given source types
     *
     * @param sourceTypesId of source types identifier
     *
     * @return array of class names
     *
     * @throws ServerException
     * @throws AdminException
     */
    public String[] getSyncSourceClasses(String[] sourceTypesId)
    throws ServerException, AdminException {
        try {
            return admin.getSyncSourceClasses(sourceTypesId);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Update a specific source into datastore and create a relative file xml with configuration.
     * The source must have a defined source type.
     * The source type must refer to a connector.
     * The connector must refer to a module.
     *
     * @param moduleId the module id
     * @param connectorId the connector id
     * @param sourceTypeId the source type id
     * @param source the information of the new source
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setSource(String moduleId, String connectorId, String sourceTypeId, String source)
    throws ServerException, AdminException{
        try {
            admin.setSource(moduleId, connectorId, sourceTypeId, (SyncSource)BeanFactory.unmarshal(source));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Delete a specific source and the relative file of configuration.
     *
     * @param sourceUri the uri that identifies the source
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void deleteSource(String sourceUri)
    throws ServerException, AdminException {
        try {
            admin.deleteSource(sourceUri);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Returns the available <code>LoggerConfiguration</code>
     *
     * @return the available <code>LoggerConfiguration</code>
     *
     * @throws ServerException, AdminException
     */
    public LoggerConfiguration[] getLoggers() throws ServerException, AdminException {

        try {
            return admin.getLoggers();
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Returns a xml serialization of a map with all available
     * <code>Appender</code> objects on the server.
     *
     * @return a xml serialization of a map with all available
     *         <code>Appender</code> objects on the server.
     *
     * @throws ServerException, AdminException
     */
    public String getAppenders() throws ServerException, AdminException {

        try {
            return BeanFactory.marshal(admin.getAppenders());
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Sets the given appender
     * @param appender the appender to set
     */
    public void setAppender(String appenderBean)
    throws ServerException, AdminException {

        try {
            admin.setAppender(
                (Appender)BeanFactory.unmarshal(appenderBean)
            );
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
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
    throws ServerException, AdminException {

        try {
            return admin.getAppenderManagementPanel(appenderClassName);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Saves and apply the new logging configuration
     *
     * @param config the new logging configuration
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setLoggingConfiguration(LoggingConfiguration config)
    throws ServerException, AdminException {
        try {
            admin.setLoggingConfiguration(config);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Saves and apply the new logger configuration
     *
     * @param config the new logger configuration
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setLoggerConfiguration(String loggerBean)
    throws ServerException, AdminException {

        try {
            admin.setLoggerConfiguration(
                (LoggerConfiguration)BeanFactory.unmarshal(loggerBean)
            );
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Returns the server configuration
     *
     * @return the server configuration as a <i>ServerConfiguration</i>
     *         object.
     *
     * @throws ServerException, AdminException
     */
    public ServerConfiguration getServerConfiguration()
    throws ServerException, AdminException {
        try {
            return admin.getServerConfiguration();
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Saves and apply the new server configuration
     *
     * @param config the new server configuration
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setServerConfiguration(ServerConfiguration config)
    throws ServerException, AdminException {
        try {
            admin.setServerConfiguration(config);
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }


    /**
     * Returns the server version.
     *
     * @return the server version
     *
     */
    public String getServerVersion(String version) {
        return admin.getServerVersion();
    }

    /**
     * Returns the server bean with the given name. If the bean does not exist,
     * am AdminException is thrown.
     *
     * @param bean the server bean
     *
     * @throws ServerException, AdminException
     */
    public String getServerBean(String bean)
    throws ServerException, AdminException {
        try {
            return BeanFactory.marshal(admin.getServerBean(bean));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Sets the server bean with the given name.
     *
     * @param bean the server bean
     * @param obj the bean instance
     *
     * @throws ServerException, AdminException
     */
    public void setServerBean(String bean, String obj)
    throws ServerException, AdminException {
        try {
            admin.setServerBean(bean, BeanFactory.unmarshal(obj));
        } catch (AdminException e) {
            log.error("Server error", e);
            throw e;
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Sends a notification message to the given device.
     *
     * @param username the username
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user intraction mode
     * @throws ServerException if an error occurs
     */
    public void sendNotificationMessage(String  username,
                                        String  deviceId,
                                        String  alerts,
                                        Integer uimode)
    throws ServerException {

        try {

            admin.sendNotificationMessage(username,
                                          deviceId,
                                          (Alert[])BeanFactory.unmarshal(alerts),
                                          uimode.intValue());

        } catch (ServerException e) {
            //
            // There are not reasons to log here the exceptions since
            // it should be already handled/logged in the AdminManager or in the
            // component that really serves the request
            //
            throw e;
        } catch (Throwable e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Sends a notification message to the given device.
     *
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user intraction mode
     *
     * @throws ServerException if an error occurs
     *
     * @deprecated Since v71 use sendNotificationMessage(String username,
     *             String deviceId, Alert[] alerts, int uimode)
     */
    public void sendNotificationMessage(String  deviceId,
                                        String  alerts,
                                        Integer uimode)
    throws ServerException {

        try {

            admin.sendNotificationMessage(deviceId,
                                          (Alert[])BeanFactory.unmarshal(alerts),
                                          uimode.intValue());

        } catch (ServerException e) {
            //
            // There are not reasons to log here the exceptions since
            // it should be already handled/logged in the AdminManager or in the
            // component that really serves the request
            //
            throw e;
        } catch (Throwable e) {
            throw new ServerException(e.getMessage(), e);
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
     * @throws ServerException if an error occurs
     */
    public void sendNotificationMessages(String  username,
                                         String  alerts,
                                         Integer uimode)
    throws ServerException {

        try {

            admin.sendNotificationMessages(username,
                                           (Alert[])BeanFactory.unmarshal(alerts),
                                           uimode.intValue());
        } catch (ServerException e) {
            //
            // There are not reasons to log here the exceptions since
            // it should be already handled/logged in the AdminManager or in the
            // component that really serves the request
            //
            throw e;
        } catch (Throwable e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves a notification message created with the pending notifications
     * stored in the datastore.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @return the notification message
     * @throws ServerException if an error occurs
     */
    public Message getMessageFromPendingNotifications(String username,
                                                      String deviceId)
    throws ServerException {
        try {

            Message message =
                admin.getMessageFromPendingNotifications(username, deviceId);
            if (message != null) {
                //
                // The messageType is set to null because otherwise there is an
                // issue while sending it via web service. It is set to null
                // here instead on the web service client side it is set back
                // to the correct value, using the 'type' value.
                //
                message.setMessageType(null);
            }
            return message;
        } catch (ServerException e) {
            //
            // There are not reasons to log here the exceptions since
            // it should be already handled/logged in the AdminManager or in the
            // component that really serves the request
            //
            throw e;
        } catch (Throwable e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Deletes the notification stored in the datastore after that they are
     * delivered rightly.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @param syncSources the array of SyncSources to notify
     * @throws ServerException if an error occurs
     */
    public void deletePendingNotifications(String   username   ,
                                           String   deviceId   ,
                                           String[] syncSources)
    throws ServerException {
        try {
            admin.deletePendingNotifications(username, deviceId, syncSources);
        } catch (ServerException e) {
            //
            // There are not reasons to log here the exceptions since
            // it should be already handled/logged in the AdminManager or in the
            // component that really serves the request
            //
            throw e;
        } catch (Throwable e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Returns the latest DS Server available update.
     *
     * @return the latest DS Server available update component. null if no
     *         updates are available
     * @throws ServerException if an error occurs
     */
    public Component getLatestDSServerUpdate() throws ServerException {
        try {
            return admin.getLatestDSServerUpdate();
        } catch (ServerException e) {
            log.error("Server error", e);
            throw e;
        } catch (Throwable e) {
            log.error("Server error", e);
            throw new ServerException(e.getMessage(), e);
        }
    }
}
