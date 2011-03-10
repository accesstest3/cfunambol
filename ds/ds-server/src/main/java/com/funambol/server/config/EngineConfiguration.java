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

package com.funambol.server.config;

/**
 * This class is just a container of SyncServer engine configuration.
 *
 * @version $Id: EngineConfiguration.java,v 1.2 2008-06-30 14:27:18 nichele Exp $
 */
public class EngineConfiguration {

    // --------------------------------------------------------------- Constants
    public static final String DEFAULT_OFFICER =
            "com/funambol/server/security/UserProvisioningOfficer.xml";

    public static final String DEFAULT_INVENTORY =
            "com/funambol/server/inventory/PSDeviceInventory.xml";

    public static final String DEFAULT_ENGINE =
            "com/funambol/server/engine/Engine.xml";

    public static final String DEFAULT_STRATEGY =
            "com/funambol/server/engine/Strategy.xml";

    public static final String DEFAULT_STORE =
            "com/funambol/server/store/PersistentStoreManager.xml";

    public static final String DEFAULT_PIPELINE_MANAGER =
            "com/funambol/server/engine/pipeline/PipelineManager.xml";

    public static final String DEFAULT_USER_MANAGER =
            "com/funambol/server/admin/DBUserManager.xml";

    public static final String DEFAULT_DATA_TRANSFORMER_MANAGER =
            "com/funambol/server/engine/transformer/DataTransformerManager.xml";

    public static final String DEFAULT_LOGGING_CONFIGURATION =
            "com/funambol/server/logging/Logging.xml";

    public static final String DEFAULT_SESSION_HANDLER =
            "com.funambol.server.session.SyncSessionHandler";

    public static final String DEFAULT_UPDATE_DISCOVERY =
            "com/funambol/server/update/UpdateDiscovery.xml";

    public static final String DEFAULT_SMS_SERVICE =
            "com/funambol/server/sms/SMSService.xml";

    public static final String DEFAULT_PUSH_MANAGER =
            "com/funambol/server/notification/PushManager.xml";

    // ------------------------------------------------------------ Private data

    /**
     * The server URI
     */
    private String serverURI;

    /**
     * The SyncStrategy implementation to use
     */
    private String strategy = DEFAULT_STRATEGY;

    /**
     * The SyncEngine implementation to use
     */
    private String engine = DEFAULT_ENGINE;

    /**
     * The SessionHandler to use
     */
    private String sessionHandler = DEFAULT_SESSION_HANDLER;

    /**
     * The PersistentStore to use
     * @deprecated starting in v6, the stores are handled in separeted files
     *             stored in com/funambol/server/store directory
     */
    private String store = DEFAULT_STORE;

    /**
     * The SecurityOfficer to use
     */
    private String officer = DEFAULT_OFFICER;

    /**
     * The PipelineManager to use
     * @deprecated starting in v6, the synclets are handled in separeted files
     *             stored in com/funambol/server/engine/pipeline/input[output]
     *             directory
     */
    private String pipelineManager = DEFAULT_PIPELINE_MANAGER;

    /**
     * The UserManager to use
     */
    private String userManager = DEFAULT_USER_MANAGER;

    /**
     * The LoggingConfiguration to use
     * @deprecated starting in v6, the loggers are handled in separeted files
     *             stored in com/funambol/server/logger directory
     */
    private String loggingConfiguration = DEFAULT_LOGGING_CONFIGURATION;

    /**
     * The minimum MaxMsgSize allowed
     */
    private long minMaxMsgSize;

    /**
     * The DeviceInventory to use
     */
    private String deviceInventory = DEFAULT_INVENTORY;

    /**
     * The DataTransformerManager to use
     */
    private String dataTransformerManager = DEFAULT_DATA_TRANSFORMER_MANAGER;

    /**
     * The UpdateDiscovery to use
     */
    private String updateDiscovery = DEFAULT_UPDATE_DISCOVERY;

    /**
     * Is the check for updates enabled ?
     */
    private boolean checkForUpdates = true;

    /**
     * The SMSService to use
     */
    private String smsService = DEFAULT_SMS_SERVICE;

    /**
     * The Push Manager to use
     */
    private String pushManager = DEFAULT_PUSH_MANAGER;

    // ---------------------------------------------------------- Public methods
    
    /**
     * Getter for property serverURI.
     * @return Value of property serverURI.
     */
    public String getServerURI() {
        return serverURI;
    }

    /**
     * Setter for property serverURI.
     * @param serverURI New value of property serverURI.
     */
    public void setServerURI(String serverURI) {
        this.serverURI = serverURI;
    }

    /**
     * Getter for property strategy.
     * @return Value of property strategy.
     */
    public String getStrategy() {
        return strategy;
    }

    /**
     * Setter for property strategy.
     * @param strategy New value of property strategy.
     */
    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    /**
     * Getter for property engine.
     * @return Value of property engine.
     */
    public String getEngine() {
        return engine;
    }

    /**
     * Setter for property engine.
     * @param strategy New value of property engine.
     */
    public void setEngine(String engine) {
        this.engine = engine;
    }

    /**
     * Getter for property sessionHandler.
     * @return Value of property sessionHandler.
     */
    public String getSessionHandler() {
        return sessionHandler;
    }

    /**
     * Setter for property sessionHandler.
     * @param sessionHandler New value of property sessionHandler.
     */
    public void setSessionHandler(String sessionHandler) {
        this.sessionHandler = sessionHandler;
    }

    /**
     * Getter for property store.
     * @return Value of property store.
     * @deprecated starting in v6, the stores are handled in separeted files
     *             stored in com/funambol/server/store directory
     */
    public String getStore() {
        return store;
    }

    /**
     * Setter for property store.
     * @param store New value of property store.
     * @deprecated starting in v6, the stores are handled in separeted files
     *             stored in com/funambol/server/store directory
     */
    public void setStore(String store) {
        this.store = store;
    }

    /**
     * Getter for property officer.
     * @return Value of property officer.
     */
    public String getOfficer() {
        return officer;
    }

    /**
     * Setter for property officer.
     * @param officer New value of property officer.
     */
    public void setOfficer(String officer) {
        this.officer = officer;
    }

    /**
     * Getter for property pipelineManager.
     * @return Value of property pipelineManager.
     * @deprecated starting in v6, the synclets are handled in separeted files
     *             stored in com/funambol/server/engine/pipeline/input[output]
     *             directory
     */
    public String getPipelineManager() {
        return pipelineManager;
    }

    /**
     * Setter for property pipelineManager.
     * @param pipelineManager New value of property pipelineManager.
     * @deprecated starting in v6, the synclets are handled in separeted files
     *             stored in com/funambol/server/engine/pipeline/input[output]
     *             directory
     */
    public void setPipelineManager(String pipelineManager) {
        this.pipelineManager = pipelineManager;
    }

    /**
     * Getter for property userManager.
     * @return Value of property userManager.
     */
    public String getUserManager() {
        return userManager;
    }

    /**
     * Setter for property userManager.
     * @param userManager New value of property userManager.
     */
    public void setUserManager(String userManager) {
        this.userManager = userManager;
    }

    /**
     * Getter for property loggingConfiguration.
     * @return Value of property loggingConfiguration.
     * @deprecated starting in v6, the loggers are handled in separeted files
     *             stored in com/funambol/server/logger directory
     */
    public String getLoggingConfiguration() {
        return loggingConfiguration;
    }

    /**
     * Setter for property loggingConfiguration.
     * @param loggingConfiguration the new value of property loggingConfiguration.
     * @deprecated starting in v6, the loggers are handled in separeted files
     *             stored in com/funambol/server/logger directory
     */
    public void setLoggingConfiguration(String loggingConfiguration) {
        this.loggingConfiguration = loggingConfiguration;
    }

    /**
     * Getter for property minMaxMsgSize.
     * @return Value of property minMaxMsgSize.
     */
    public long getMinMaxMsgSize() {
        return minMaxMsgSize;
    }

    /**
     * Setter for property minMaxMsgSize.
     * @param minMaxMsgSize New value of property minMaxMsgSize.
     */
    public void setMinMaxMsgSize(long minMaxMsgSize) {
        this.minMaxMsgSize = minMaxMsgSize;
    }


    /**
     * Getter for property deviceInventory.
     * @return Value of property deviceInventory.
     */
    public String getDeviceInventory() {
        return deviceInventory;
    }

    /**
     * Setter for property deviceInventory.
     * @param deviceInventory New value of property deviceInventory.
     */
    public void setDeviceInventory(String deviceInventory) {
        this.deviceInventory = deviceInventory;
    }

    /**
     * Getter for property dataTransformerManager.
     * @return Value of property dataTransformerManager.
     */
    public String getDataTransformerManager() {
        return dataTransformerManager;
    }

    /**
     * Setter for property dataTransformerManager.
     * @param dataTransformerManager New value of property dataTransformerManager.
     */
    public void setDataTransformerManager(String dataTransformerManager) {
        this.dataTransformerManager = dataTransformerManager;
    }

    /**
     * Getter for property updateDiscovery.
     * @return Value of property updateDiscovery.
     */
    public String getUpdateDiscovery() {
        return updateDiscovery;
    }

    /**
     * Setter for property updateDiscovery.
     * @param newUpdateDiscovery New value of property updateDiscovery.
     */
    public void setUpdateDiscovery(String newUpdateDiscovery) {
        this.updateDiscovery = newUpdateDiscovery;
    }

    /**
     * Getter for property checkForUpdates.
     * @return Value of property checkForUpdates.
     */
    public boolean getCheckForUpdates() {
        return checkForUpdates;
    }

    /**
     * Setter for property checkForUpdates.
     * @param checkForUpdates New value of property checkForUpdates.
     */
    public void setCheckForUpdates(boolean checkForUpdates) {
        this.checkForUpdates = checkForUpdates;
    }

    /**
     * Getter for property smsService.
     * @return value of property smsService.
     */
    public String getSmsService() {
        return smsService;
    }

    /**
     * Setter for property smsService.
     * @param smsService new value of property smsService.
     */
    public void setSmsService(String smsService) {
        this.smsService = smsService;
    }

    /**
     * Getter for property pushManager.
     * @return value of property pushManager.
     */
    public String getPushManager() {
        return pushManager;
    }

    /**
     * Setter for property pushManager.
     * @param pushManager new value of property pushManager.
     */
    public void setPushManager(String pushManager) {
        this.pushManager = pushManager;
    }

    /**
     * String representation for debug purposes
     *
     * @return the string representation of this object
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(128);

        buf.append(getClass().getName())
           .append('@')
           .append(hashCode())
           .append("{\n")
           .append("serverURI: ").append(serverURI).append('\n')
           .append("minMaxMsgSize: ").append(minMaxMsgSize).append('\n')
           .append("strategy: ").append(strategy).append('\n')
           .append("engine: ").append(engine).append('\n')
           .append("sessionHandler: ").append(sessionHandler).append('\n')
           .append("store: ").append(store).append('\n')
           .append("userManager: ").append(userManager).append('\n')
           .append("officer: ").append(officer).append('\n')
           .append("pipelineManager: ").append(pipelineManager).append('\n')
           .append("dataTransformerManager: ").append(dataTransformerManager).append('\n')
           .append("deviceInventory: ").append(deviceInventory).append('\n')
           .append("smsService: ").append(smsService).append('\n')
           .append("pushManager: ").append(pushManager).append('\n')
           .append("loggingConfiguration: ").append(loggingConfiguration).append('\n')
           .append("updateDiscovery: ").append(updateDiscovery).append('\n')
           .append("checkForUpdates: ").append(checkForUpdates).append('\n')
           .append('}');

        return buf.toString();
    }

 }
