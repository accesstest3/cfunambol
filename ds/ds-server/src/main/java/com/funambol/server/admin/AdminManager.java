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
package com.funambol.server.admin;

import java.beans.XMLEncoder;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Appender;

import com.funambol.framework.config.LoggerConfiguration;
import com.funambol.framework.config.LoggingConfiguration;
import com.funambol.framework.core.Alert;
import com.funambol.framework.core.Cred;
import com.funambol.framework.core.NextNonce;
import com.funambol.framework.core.Authentication;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceErrorDescriptor;
import com.funambol.framework.filter.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationNotSentException;
import com.funambol.framework.security.AuthorizationResponse;
import com.funambol.framework.security.AuthorizationStatus;
import com.funambol.framework.security.Credential;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.LastTimestamp;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jModule;
import com.funambol.framework.server.Sync4jSourceType;
import com.funambol.framework.server.Sync4jConnector;
import com.funambol.framework.server.Sync4jSource;
import com.funambol.framework.server.error.ServerException;
import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.security.Officer;
import com.funambol.framework.server.ClientMapping;
import com.funambol.framework.tools.MD5;
import com.funambol.framework.tools.beans.BeanFactory;
import com.funambol.framework.tools.beans.BeanNotFoundException;

import com.funambol.framework.tools.beans.BeanTool;
import com.funambol.server.config.Configuration;
import com.funambol.server.config.ConfigurationConstants;
import com.funambol.server.config.ServerConfiguration;
import com.funambol.server.notification.PushManager;
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
 * @version $Id: AdminManager.java,v 1.2 2008-03-04 22:13:00 stefano_fornari Exp $
 *
 */
public class AdminManager
implements ConfigurationConstants, java.io.Serializable {
    // ------------------------------------------------------- Private constants

    /** This role is used only for authentication purposes */
    private static final String ROLE_SPECIAL = "special_sync_admin";

    // ------------------------------------------------------------ Private data

    private transient FunambolLogger log = null;

    private Configuration          config             = null;
    private PersistentStore        ps                 = null;
    private UserManager            userManager        = null;
    private DeviceInventory        deviceInventory    = null;
    private PushManager            pushManager        = null;
    // ------------------------------------------------------------ Constructors

    public AdminManager() {

        log = FunambolLoggerFactory.getLogger("admin");

        try {
            config             = Configuration.getConfiguration();
            ps                 = (PersistentStore)config.getStore();
            userManager        = (UserManager)config.getUserManager();
            deviceInventory    = (DeviceInventory)config.getDeviceInventory();
            pushManager        = (PushManager)config.getPushManager();
        } catch (Throwable t) {
            log.error("Unable to create the admin manager", t);
        }
    }

    // ---------------------------------------------------------- Public methods

    public Configuration getConfig() {
        return config;
    }

    /**
     * Checks if user is authorized to use the given resource.
     *
     * @param authCred the user credential
     * @param resource the resource to check
     *
     * @return the AuthorizeResponse with the result of the check
     *
     * @throws ServerException
     * @throws AdminException
     */
    public AuthorizationResponse authorizeCredential(Credential authCred,
                                                     String     resource)
    throws ServerException, AdminException {
        AuthorizationResponse authResponse = new AuthorizationResponse();

        //
        // Create object Cred with the information contained in the authCred
        //
        Authentication authentication = new Authentication();
        authentication.setDeviceId(authCred.getDeviceId());
        String type = authCred.getAuthType();
        if (Credential.AUTH_TYPE_BASIC.equals(type)) {
            authentication.setType(Cred.AUTH_TYPE_BASIC);
        } else if (Credential.AUTH_TYPE_MD5_1_0.equals(type)) {
            authentication.setType(Cred.AUTH_TYPE_MD5);
            authentication.setSyncMLVerProto("1.0");
        } else if (Credential.AUTH_TYPE_MD5_1_1.equals(type)) {
            authentication.setType(Cred.AUTH_TYPE_MD5);
            authentication.setSyncMLVerProto("1.1");
        }
        //
        // The setting of data depends on authetication type so it must be done
        // after that.
        //
        authentication.setData(authCred.getCredData());
        authentication.setUsername(authCred.getUsername());

        Cred credentials = new Cred(authentication);

        Officer officer = config.getOfficer();

        if (Cred.AUTH_TYPE_MD5.equals(credentials.getType())) {
            //
            // Read the stored nonce, if exists, and save it in the credentials
            // in order to use it during authentication
            //
            Sync4jDevice device =
                new Sync4jDevice(credentials.getAuthentication().getDeviceId());
            try {
                config.getDeviceInventory().getDevice(device);
            } catch (DeviceInventoryException e) {
                String msg = "Error reading device: " + e.getMessage();
                log.error(msg, e);
                throw new ServerException(msg, e);
            }

            //
            // This is the nonce used in this authentication phase
            //
            credentials.getAuthentication().setNextNonce(
                new NextNonce(device.getClientNonce()));

            //
            // Generate a new nonce to use it in the next authentication
            //
            byte[] nextNonce = MD5.getNextNonce();
            device.setClientNonce(nextNonce);
            try {
                config.getDeviceInventory().setDevice(device);
            } catch (DeviceInventoryException e) {
                String msg = "Error setting device: " + e.getMessage();
                log.error(msg, e);
                throw new ServerException(msg, e);
            }

            authResponse.setNextNonce(nextNonce);
        }

        Sync4jUser user = officer.authenticateUser(credentials);
        if (user != null) {
            Sync4jPrincipal principal = new Sync4jPrincipal();
            principal.setUser(user);
            principal.setDeviceId(credentials.getAuthentication().getDeviceId());
            try {
                config.getStore().read(principal);
            } catch (NotFoundException e) {
                if (log.isTraceEnabled()) {
                    log.trace("Principal not found:" + principal);
                }

                authResponse.setAuthStatus(AuthorizationStatus.NOT_AUTHENTICATED);
                return authResponse;

            } catch (PersistentStoreException e) {
                String msg = "Error reading principal: " + e.getMessage();
                log.error(msg, e);
                throw new ServerException(msg, e);
            }

            switch (officer.authorize(principal, resource)) {
                case AUTHORIZED:
                    authResponse.setAuthStatus(AuthorizationStatus.AUTHORIZED);
                    break;
                case INVALID_RESOURCE:
                    authResponse.setAuthStatus(AuthorizationStatus.INVALID_RESOURCE);
                    break;
                case NOT_AUTHORIZED:
                    authResponse.setAuthStatus(AuthorizationStatus.NOT_AUTHORIZED);
                    break;
                case PAYMENT_REQUIRED:
                    authResponse.setAuthStatus(AuthorizationStatus.PAYMENT_REQUIRED);
                    break;
                case RESOURCE_NOT_AVAILABLE:
                    authResponse.setAuthStatus(AuthorizationStatus.RESOURCE_NOT_AVAILABLE);
                    break;
                default:
                    authResponse.setAuthStatus(AuthorizationStatus.NOT_AUTHENTICATED);
            }

        } else {
            authResponse.setAuthStatus(AuthorizationStatus.NOT_AUTHENTICATED);
        }

        return authResponse;
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
        String[] roles = null;

        try {

            roles = userManager.getRoles();

            if (roles != null) {
                List l = Arrays.asList(roles);
                Vector v = new Vector(l);
                int size = v.size();
                for (int i=0; i<size; i++) {
                    String role = (String)v.get(i);
                    if (role.startsWith(ROLE_SPECIAL)) {
                        v.remove(i);
                        size = v.size();
                    }
                }
                roles = (String[])v.toArray(new String[0]);
            }

        } catch (PersistentStoreException e) {
            String msg = "Error reading roles: " + e.getMessage();
            log.error(msg, e);
            throw new ServerException(msg, e);
        }
        return roles;
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
    public Sync4jUser[] getUsers(Clause clause)
    throws ServerException, AdminException {
        Sync4jUser[] users = null;
        try {

            users = userManager.getUsers(clause);

            for (int i=0; (users != null) && i<users.length; i++) {
                userManager.getUserRoles(users[i]);
            }

        } catch (PersistentStoreException e) {
            String msg = "Error reading Users: " + e.getMessage();
            log.error(msg, e);
            throw new ServerException(msg, e);
        }

        return users;
    }

    /**
     * Adds a new user and the assigned role to it
     *
     * @param user the user to insert
     *
     * @return the username
     *
     * @throws ServerException
     * @throws AdminException
     */
    public String  addUser(Sync4jUser user)
    throws ServerException, AdminException {
        try {

            userManager.insertUser(user);

            return user.getUsername();

        } catch (PersistentStoreException e) {
            String msg = "Error inserting User: " + e.getMessage();
            log.error(msg, e);
            throw new ServerException(msg, e);
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

            boolean isAdministrator = false;

            if (user.getRoles() != null) {
                for(int i=0; i<user.getRoles().length; i++){
                    if (user.getRoles()[i].equals(userManager.ROLE_ADMIN)){
                        isAdministrator = true;
                        break;
                    }
                }
            }

            if (!isAdministrator &&
                userManager.isUniqueAdministrator(user)){
                throw new AdminException("You cannot change the role of this user.\n" +
                        "You must have at least one 'Administrator' user for the Funambol server.");
            }

            userManager.setUser(user);
        } catch (PersistentStoreException e) {
            String msg = "Error updating User: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
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
            Sync4jUser user = new Sync4jUser(userName,null,null,null,null,null);
            userManager.deleteUser(user);

        } catch (PersistentStoreException e) {
            String msg = "Error deleting User: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
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
    public int countUsers(Clause clause)
    throws ServerException, AdminException {
        int n = 0;

        try {

            n = userManager.countUsers(clause);

        } catch (PersistentStoreException e) {
            String msg = "Error counting users: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return n;
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
    public Sync4jDevice[] getDevices(Clause clauses)
    throws ServerException, AdminException {
        Sync4jDevice[] devices = null;

        try {

            devices = (Sync4jDevice[])deviceInventory.queryDevices(clauses);

        } catch (DeviceInventoryException e) {
            String msg = "Error reading devices: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return devices;
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

            deviceInventory.setDevice(d);

        } catch (DeviceInventoryException e) {
            String msg = "Error adding device: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return d.getDeviceId();
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
    public Sync4jDevice getDevice(final String id)
    throws ServerException, AdminException {

        try {

            Sync4jDevice d = new Sync4jDevice(id);

            deviceInventory.getDevice(d);

            return d;

        } catch (DeviceInventoryException e) {
            String msg = "Error retrieving device: " + id;
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
    }

    /**
     * Retrieves the device's capabilities for the given device.
     *
     * @param deviceId the identifier of device
     *
     * @return the retrieved device's capabilities
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Capabilities getDeviceCapabilities(String deviceId)
    throws ServerException, AdminException {

        Sync4jDevice device = new Sync4jDevice(deviceId);
        try {
            deviceInventory.getDevice(device, true);
        } catch (DeviceInventoryException e) {
            String msg = "Error getting the capabilities for deviceId '" + deviceId + "' (" + e.getMessage() + ")";

            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return device.getCapabilities();
    }

    /**
     * Save the device's capabilities for the given device.
     *
     * @param caps     the Capabilities to insert
     * @param deviceId the identifier of device
     *
     * @return the identifier of inserted capabilities
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Long setDeviceCapabilities(Capabilities caps, String deviceId)
    throws ServerException, AdminException {
        try {

            //
            // Remove existing capabilities
            //
            if (null != caps.getId()) {
                deviceInventory.removeCapabilities(caps);
            }

            deviceInventory.setCapabilities(caps);

            //
            // Update the identifier of capabilities into device datastore
            //
            deviceInventory.setDeviceIdCaps(deviceId, caps.getId());

            return caps.getId();

        } catch (DeviceInventoryException e) {
            String msg = "Error setting the capabilities for deviceId '"
                       + deviceId + "' (" + e.getMessage() + ")";

            log.error(msg, e);

            throw new ServerException(msg, e);
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

            deviceInventory.setDevice(d);

        } catch (DeviceInventoryException e) {
            String msg = "Error updating device: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
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
            Sync4jDevice sd = new Sync4jDevice(deviceId);
            deviceInventory.removeDevice(sd);

        } catch (DeviceInventoryException e) {
            String msg = "Error deleting device: " + e.getMessage();

            log.error(msg, e);

            throw new ServerException(msg, e);
        }
    }

    /**
     * Count the number of device that satisfy the specific clauses.
     *
     * @param clauses the specific conditions for counting devices
     *
     * @return the number of device finds
     *
     * @throws ServerException
     * @throws AdminException
     */
    public int countDevices(Clause clauses)
    throws ServerException, AdminException {
        int n = 0;

        try {

            n = deviceInventory.countDevices(clauses);

        } catch (DeviceInventoryException e) {
            String msg = "Error counting devices: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return n;
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
    public Sync4jPrincipal[] getPrincipals(Clause clauses)
    throws ServerException, AdminException {
        Sync4jPrincipal[] principals = null;

        try {

            principals = (Sync4jPrincipal[])ps.read(new Sync4jPrincipal(-1, null, null),
                                                    clauses);

        } catch (PersistentStoreException e) {
            String msg = "Error reading principals: " + e.getMessage();

            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return principals;
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

            ps.store(p);

        } catch (PersistentStoreException e) {
            String msg = "Error adding rincipal: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return p.getId();
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
            Sync4jPrincipal sp = new Sync4jPrincipal(principalId, null, null);
            ps.delete(sp);

        } catch (PersistentStoreException e) {
            String msg = "Error deleting principal: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
    }

    /**
     * Count the number of principal that satisfy the specific clauses.
     *
     * @param clauses the specific conditions for counting principals
     *
     * @return the number of principal finds
     *
     * @throws ServerException
     * @throws AdminException
     */
    public int countPrincipals(Clause clauses)
    throws ServerException, AdminException {
        int n = 0;

        try {

            n = ps.count(new Sync4jPrincipal(-1, null, null),
                         clauses);

        } catch (PersistentStoreException e) {
            String msg = "Error counting principals: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return n;
    }

    /**
     * Read all last timestamps that satisfy the clauses.
     *
     * @param clauses the specific conditions for search last timestamps
     *
     * @return array of last timestamp
     *
     * @throws ServerException
     * @throws AdminException
     */
    public LastTimestamp[] getLastTimestamps(Clause clauses)
    throws ServerException, AdminException {
        LastTimestamp[] lastTimestamps = null;

        try {

            lastTimestamps = (LastTimestamp[])ps.read(new LastTimestamp(-1, null),
                                                      clauses);

        } catch (PersistentStoreException e) {
            String msg = "Error reading last timestamps: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return lastTimestamps;
    }

    /**
     * Delete the specific last timestamps.
     *
     * @param principalId the principal id that identifies the  principal
     * @param sourceId the source id that identifies the  source
     * @throws ServerException
     * @throws AdminException
     */
    public void deleteLastTimestamp(long principalId, String sourceId)
    throws ServerException, AdminException {
        try {
            LastTimestamp lt = new LastTimestamp(principalId, sourceId);
            ps.delete(lt);
        } catch (PersistentStoreException e) {
            String msg = "Error deleting last timestamp: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
    }

    /**
     * Count the number of timestamps that satisfy the specific clauses.
     *
     * @param clauses the specific conditions for counting last timestamps
     *
     * @return the number of last timestamps finds
     *
     * @throws ServerException
     * @throws AdminException
     */
    public int countLastTimestamps(Clause clauses)
    throws ServerException, AdminException {
        int n = 0;

        try {
            n = ps.count(new LastTimestamp(-1, null), clauses);

        } catch (PersistentStoreException e) {
            String msg = "Error counting last timestamps: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return n;
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
        Sync4jModule[] modules = null;

        try {

            modules = (Sync4jModule[])ps.read(Sync4jModule.class);

        } catch (PersistentStoreException e) {
            String msg = "Error reading modules: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }

        return modules;
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
    public Sync4jModule getModule(String moduleId)
    throws ServerException, AdminException {
        Sync4jModule module = null;
        BeanTool beanTool = BeanTool.getBeanTool(Configuration.getConfigPath());
        try {

            module = new Sync4jModule(moduleId, null, null);
            ps.read(module);

            //for each SyncConnector read the SyncSourceType and
            //for each SyncSourceType read the SyncSource
            Sync4jConnector[] syncConnectors = module.getConnectors();
            for (int i=0; (syncConnectors != null) && i<syncConnectors.length; i++) {
                Sync4jConnector sc = syncConnectors[i];
                Sync4jSourceType[] syncSourceTypes = sc.getSourceTypes();

                for (int y=0; (syncSourceTypes != null) && y<syncSourceTypes.length; y++) {
                    Sync4jSource[] sync4jSources = (Sync4jSource[])ps.read(syncSourceTypes[y], null);

                    ArrayList syncSources = new ArrayList();
                    ArrayList syncSourcesFailed = new ArrayList();

                    for (int z=0; z<sync4jSources.length; z++) {

                        try {
                            SyncSource syncSource = (SyncSource)beanTool.getNoInitNewBeanInstance(
                                sync4jSources[z].getConfig()
                            );

                            syncSources.add(syncSource);

                        } catch (Exception e) {
                            Throwable t = e.getCause();
                            if (t == null) {
                                t = e;
                            }

                            log.error("Error instantiating '" +sync4jSources[z].getUri() + "'", t);

                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            String stackTrace = sw.toString();

                            String className=e.getClass().getName();
                            String description = (e.getMessage() != null) ?
                                                (className + ": " + e.getMessage()) :
                                                className;

                            syncSourcesFailed.add(
                            new SyncSourceErrorDescriptor(
                            sync4jSources[z].getUri(),
                            sync4jSources[z].getConfig(),
                                description,
                                stackTrace
                            ));
                        }
                    }

                    SyncSource[] syncSourcesOK = (SyncSource[])syncSources.toArray(new SyncSource[syncSources.size()]);
                    syncSourceTypes[y].setSyncSources(syncSourcesOK);

                    SyncSourceErrorDescriptor[] syncSourcesNO = (SyncSourceErrorDescriptor[])syncSourcesFailed.toArray(new SyncSourceErrorDescriptor[syncSourcesFailed.size()]);
                    syncSourceTypes[y].setSyncSourcesFailed(syncSourcesNO);
                }
            }

        } catch (PersistentStoreException e) {
            String msg = "Error getting module: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }

        return module;
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
    public void addSource(String moduleId, String connectorId, String sourceTypeId, SyncSource source)
    throws ServerException, AdminException {
        String uri = source.getSourceURI();
        String sourceName = source.getName();

        String configFile = moduleId
                          + "/"
                          + connectorId
                          + "/"
                          + sourceTypeId
                          ;

        Sync4jSource s4j = new Sync4jSource(
                                   uri,
                                   configFile
                                   + "/"
                                   + sourceName
                                   + ".xml",
                                   sourceTypeId,
                                   sourceName);

        //
        //checking for the existance of the source before inserting
        //
        Sync4jSource existSource[] = null;
        try {

            WhereClause[] wc = new WhereClause[2];
            String value[] = new String[1];
            value[0] = uri;
            wc[0] = new WhereClause("uri",value, WhereClause.OPT_EQ, false);

            value = new String[1];
            value[0] = sourceName;
            wc[1] = new WhereClause("name",value, WhereClause.OPT_EQ, false);
            LogicalClause lc = new LogicalClause(LogicalClause.OPT_OR, wc);

            existSource = (Sync4jSource[])ps.read(s4j, lc);

        } catch (PersistentStoreException e) {
            String msg = "Error reading sources existing: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }

        if (existSource == null || existSource.length == 0) {
            try {

                ps.store(s4j);

            } catch (PersistentStoreException e) {
                String msg = "Error adding the SyncSource: " + e.getMessage();
                log.error(msg, e);

                throw new ServerException(msg, e);
            }
        } else {
            String msg = "A SyncSource with URI "
            + uri + " or with Name " + sourceName
            + " is already present.";

            throw new AdminException(msg);
        }

        try {

            String path = config.getConfigPath() + "/" + configFile;
            if (path.startsWith("file:")) {
                path = path.substring(6);
            }

            File f = new File(path);
            f.mkdirs();

            XMLEncoder encoder = null;
            encoder = new XMLEncoder(new FileOutputStream(path + "/" + sourceName + ".xml"));
            encoder.writeObject((Object)source);
            encoder.flush();
            encoder.close();

        } catch(FileNotFoundException e) {
            String msg = "Error storing the SyncSource on file system: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
    }

    /**
     * Read a list of syncSource that satisfy the specific conditions.
     *
     * @param clauses the conditions foe search informations.
     *
     * @return array of syncSource
     *
     * @throws ServerException
     * @throws AdminException
     */
    public Sync4jSource[] getSync4jSources(Clause clauses)
    throws ServerException, AdminException {
        Sync4jSource[] sources = null;

        try {

            sources = (Sync4jSource[])ps.read(new Sync4jSource(), clauses);

        } catch (PersistentStoreException e) {
            String msg = "Error reading sources: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }
        return sources;
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
        int size = sourceTypesId.length;
        String[] classes = new String[size];

        Sync4jSourceType sourceType = null;
        for (int i=0; i<size; i++) {
            try {
                sourceType = new Sync4jSourceType(sourceTypesId[i], null, null, null);
                ps.read(sourceType);
                classes[i] = sourceType.getClassName();
            } catch (Exception e) {
                String msg = "Error loading class for '" + sourceType + "': " + e.toString();
                log.error(msg, e);

                throw new ServerException(msg, e);
            }
        }
        return classes;
    }

    /**
     * Updates a specific source into datastore and the relative file xml with
     * configuration.
     * The source must have a defined source type.
     * The source type must refer to a connector.
     * The connector must refer to a module.
     * The file xml must exist.
     *
     * @param moduleId the module id
     * @param connectorId the connector id
     * @param sourceTypeId the source type id
     * @param source the information of the new source
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setSource(String     moduleId    ,
                          String     connectorId ,
                          String     sourceTypeId,
                          SyncSource source      )
    throws ServerException, AdminException{
        String uri = source.getSourceURI();
        String sourceName = source.getName();

        Sync4jSource s4j = new Sync4jSource(uri,null,sourceTypeId,null);

        try {

            ps.read(s4j);

        } catch (PersistentStoreException e) {
            String msg = "Error reading sources existing: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }

        s4j.setSourceName(sourceName);

        try {

            ps.store(s4j);

        } catch (PersistentStoreException e) {
            String msg = "Error storing SyncSource: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }

        try {
            String path = config.getConfigPath() + File.separator
                        + s4j.getConfig();

            if (path.startsWith("file:")) {
                path = path.substring(6);
            }

            XMLEncoder encoder = null;
            encoder = new XMLEncoder(new FileOutputStream(path));
            encoder.writeObject((Object)source);
            encoder.flush();
            encoder.close();

        } catch(FileNotFoundException e) {
            String msg = "Error storing SyncSource: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
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
    throws ServerException, AdminException{
        Sync4jSource s4j = new Sync4jSource(sourceUri,null,null,null);
        try {

            ps.read(s4j);

        } catch (PersistentStoreException e) {
            String msg = "Error reading source: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }

        try {
            ps.delete(s4j);

        } catch (PersistentStoreException e) {
            String msg = "Error deleting SyncSource: " + e.getMessage();
            log.error(msg, e);

            throw new ServerException(msg, e);
        }

        String path = config.getConfigPath() + "/" + s4j.getConfig();
        if (path.startsWith("file:")) {
            path = path.substring(6);
        }

        File f = new File(path);
        f.delete();
    }

    /**
     * Returns an array with all available <code>LoggerConfiguration</code> objects
     * on the server.
     *
     * @return an array with all available <code>LoggerConfiguration</code>
     *         objects on the server.
     *
     * @throws ServerException, AdminException
     */
    public LoggerConfiguration[] getLoggers()
    throws ServerException, AdminException {
        return config.getLoggers();
    }

    /**
     * Returns a map with all available <code>Appender</code> objects
     * on the server.
     *
     * @return a map with all available <code>Appender</code>
     *         objects on the server.
     *
     * @throws ServerException, AdminException
     */
    public Map getAppenders()
    throws ServerException, AdminException {
        return config.getAppenders();
    }

    /**
     * Sets the given appender
     * @param appender the appender to set
     */
    public void setAppender(Appender appender)
    throws ServerException, AdminException {
        config.setAppender(appender);
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
        return config.getAppenderManagementPanel(appenderClassName);
    }

    /**
     * Saves and apply the new logging configuration
     *
     * @param settings the new logging configuration
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setLoggingConfiguration(LoggingConfiguration settings)
    throws ServerException, AdminException {
        File f = new File(config.getConfigPath(),
                          config.getServerConfig().getEngineConfiguration().getLoggingConfiguration());

        try {
            BeanFactory.saveBeanInstance(settings, f);
            config.configureLogging();
        } catch (Exception e) {
            throw new ServerException(e.getMessage(), e);
        }

    }

    /**
     * Saves and apply the new logger configuration
     *
     * @param loggerConfig the new logger configuration
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setLoggerConfiguration(LoggerConfiguration loggerConfig)
    throws ServerException, AdminException {

        try {
            config.setLoggerConfiguration(loggerConfig);
        } catch (Exception e) {
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
        return config.getServerConfig();
    }

    /**
     * Saves and apply the new server configuration
     *
     * @param serverConfig the new server configuration
     *
     * @throws ServerException
     * @throws AdminException
     */
    public void setServerConfiguration(ServerConfiguration serverConfig)
        throws ServerException, AdminException {

        try {
            config.setServerConfiguration(serverConfig);
        } catch (Exception e) {
            throw new ServerException(e.getMessage(), e);
        }
    }


    /**
     * Returns the server version.
     *
     * @return the server version
     *
     */
    public String getServerVersion() {
        return config.getServerConfig().getServerInfo().getSwV();
    }

    /**
     * Returns the server bean with the given name. If the bean does not exist,
     * am AdminException is thrown.
     *
     * @param bean the server bean
     *
     * @throws ServerException, AdminException
     */
    public Object getServerBean(String bean)
    throws ServerException, AdminException {
        try {
            return Configuration.getConfiguration().getBeanInstanceByName(bean);
        } catch (BeanNotFoundException e) {
            throw new AdminException(e.getMessage());
        } catch (Exception e) {
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
    public void setServerBean(String bean, Object obj)
    throws ServerException, AdminException {
        try {
            Configuration.getConfiguration().setBeanInstance(bean, obj);
        } catch (Exception e) {
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * NOTIFICATION ENGINE
     */

    /**
     * Sends a notification message to the given device.
     *
     * @param username the user name
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws ServerException if an error occurs
     */
    public void sendNotificationMessage(String  username,
                                        String  deviceId,
                                        Alert[] alerts  ,
                                        int     uimode  )
    throws ServerException {

        try {
            pushManager.synchronouslySendNotificationMessage(username, deviceId, alerts, uimode);
        } catch (NotificationNotSentException e) {
            //
            // We add [NotificationNotSentException] as prefix to the message since
            // this is not really an exception and in this way the caller (like
            // the pim-listener) can detect this case and handle it in a different
            // way (for instance it can log the exception not as an ERROR but just
            // as a TRACE message). This is a sort of workdaround but at the moment
            // is the only way to distinguish a real expection and a
            // NotificationNotSentException
            //
            throw new ServerException("[NotificationNotSentException] " + e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     * Sends a notification message to the given device.
     *
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws ServerException if an error occurs
     *
     * @deprecated Since v71 use sendNotificationMessage(String username,
     *             String deviceId, Alert[] alerts, int uimode)
     */
    public void sendNotificationMessage(String  deviceId,
                                        Alert[] alerts  ,
                                        int     uimode  )
    throws ServerException {

        try {
            pushManager.synchronouslySendNotificationMessage(deviceId, alerts, uimode);
        } catch (NotificationNotSentException e) {
            //
            // We add [NotificationNotSentException] as prefix to the message since
            // this is not really an exception and in this way the caller (like
            // the pim-listener) can detect this case and handle it in a different
            // way (for instance it can log the exception not as an ERROR but just
            // as a TRACE message). This is a sort of workdaround but at the moment
            // is the only way to distinguish a real expection and a
            // NotificationNotSentException
            //
            throw new ServerException("[NotificationNotSentException] " + e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
     * @param uimode   user interaction mode
     * @throws ServerException if an error occurs
     */
    public void sendNotificationMessages(String  username,
                                         Alert[] alerts  ,
                                         int     uimode  )
    throws ServerException {

        try {
            pushManager.synchronouslySendNotificationMessages(username, alerts, uimode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
            return pushManager.getMessageFromPendingNotifications(username, deviceId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
            pushManager.deletePendingNotifications(username, deviceId, syncSources);
        } catch(Exception e) {
            log.error(e.getMessage(), e);
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
            if (Configuration.getConfiguration().getUpdateDiscovery() != null) {
                return Configuration.getConfiguration().getUpdateDiscovery().getLatestDSServerUpdate();
            }
        } catch (Exception e) {
            throw new ServerException(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Returns the client mapping
     * @param deviceId the device id for which the client mappings belongs
     * @param userId the user id for which the client mappings belongs
     * @param sourceURI the sourecURI for which the client mappings belongs
     * @return the client mapping
     * @throws ServerException if an error occurs
     */
    public ClientMapping getClientMapping(String deviceId, String userId,
        String sourceURI) throws ServerException, AdminException {

        Sync4jPrincipal principal = getPrincipal(deviceId, userId);

        ClientMapping clientMapping = getClientMapping(principal, sourceURI);
        return clientMapping;
    }


    /**
     * Returns the client mapping
     * @param principal the principal for which the client mappings belongs
     * @param sourceURI the sourecURI for which the client mappings belongs
     * @return the client mapping
     * @throws ServerException if an error occurs
     */
    public ClientMapping getClientMapping(Sync4jPrincipal principal,
        String sourceURI) throws ServerException {

        try {
            ClientMapping clientMapping = new ClientMapping(principal, sourceURI);
            ps.read(clientMapping);
            return clientMapping;
        } catch (PersistentStoreException ex) {
            throw new ServerException("Error retrieving mapping from persistent store", ex);
        }
    }


    /**
     * Returns the principal corresponding to the deviceId and the UserId
     * @param deviceId the device id
     * @param userId the user id
     * @return the principal corresponding to the deviceId and the UserId
     * @throws ServerException if an error occurs
     * @throws AdminException If the principal has not been found.
     */
    public Sync4jPrincipal getPrincipal(String deviceId, String userId)
        throws ServerException, AdminException {

        Sync4jPrincipal[] principals = getPrincipals(
            new WhereClause(
            "username",
            new String[]{userId},
            WhereClause.OPT_EQ,
            true));

        for (Sync4jPrincipal principal : principals) {
            if (principal.getDeviceId().equalsIgnoreCase(deviceId)) {
                return principal;
            }
        }

        throw new AdminException("Unable to retrieve principal for user='"
            + userId + "', device='" + deviceId + "'");
    }

}
