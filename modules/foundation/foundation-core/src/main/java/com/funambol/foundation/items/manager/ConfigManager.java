/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
package com.funambol.foundation.items.manager;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.LogicalClause;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.ConfigItem;

import com.funambol.server.config.Configuration;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.util.Def;

/**
 * Manager for config items sync
 *
 * @version $Id: ConfigManager.java,v 1.1.1.1 2008-03-20 21:38:41 stefano_fornari Exp $
 */
public class ConfigManager {

    //------------------------------------------------------------- Private data

    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    private DeviceInventory deviceInventory = null;

    private Sync4jPrincipal principal = null;
    private String          username  = null;
    //------------------------------------------------------------- Constructors

    /**
     * Creates a new ConfigManager
     * @param principal the principal
     */
    public ConfigManager(Sync4jPrincipal principal) {
        if (log.isTraceEnabled()) {
            log.trace("Created new ConfigManager for principal " + principal);
        }
        this.principal = principal;
        this.username  = principal.getUsername();
        this.deviceInventory = Configuration.getConfiguration().getDeviceInventory();
    }

    //----------------------------------------------------------- Public methods

    /**
     * Sets (updates or adds) a config node.
     *
     * @param nodeURI the node uri
     * @param value the node value
     * @param ts the timestamp to use in the setting
     * @throws EntityException if an error occurs
     */
    public void setItem(String nodeURI,
                        String value,
                        Timestamp ts) throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Setting config with nodeURI " + nodeURI + " to '" + value + "'");
        }
        try {

            ConfigItem configItem =
                new ConfigItem(username, principal, nodeURI, value);

            deviceInventory.setConfigItem(configItem);

        } catch (Exception e) {
            throw new EntityException("Error setting item '" + nodeURI
                                    + "' with value '" + value + "'", e);
        }
    }

    /**
     * Gets the config item with the given nodeURI
     *
     * @param nodeURI the node uri
     * @return the config item with the given node uri
     * @throws EntityException if an error occurs
     */
    public ConfigItem getItem(String nodeURI) throws EntityException {
        try {
            ConfigItem c = new ConfigItem(username, principal, nodeURI, null);
            boolean found = deviceInventory.getConfigItem(c);
            if (!found) {
                if (log.isTraceEnabled()) {
                    log.trace("Item with nodeURI '" + nodeURI + "' not found");
                }

                return null;
            }

            if (log.isTraceEnabled()) {
                log.trace("Returning '" + c + "'");
            }
            return c;

        } catch (Exception e) {
            throw new EntityException("Error getting item with nodeURI '" + nodeURI + "'", e);
        }
    }

    /**
     * Returns all the configuration items
     * @return a list with all the configuration items
     * @throws EntityException if an error occurs
     */
    public List<ConfigItem> getAllItems() throws EntityException {
        try {

            List<ConfigItem> configItems =
                deviceInventory.getConfigItems(principal);

            return configItems;

        } catch (Exception e) {
            throw new EntityException("Error while getting all items", e);
        }
    }

    /**
     * Gets the config items with the given state and updated between since and to
     * @param state the state
     * @param since the since timestamp
     * @param to the to timestamp
     * @return a list with all the config items with the given state and updated
     *         between since and to
     * @throws EntityException if an error occurs
     */
    public List<ConfigItem> getItems(char state, Timestamp since, Timestamp to)
    throws EntityException {

        try {
            Clause clause = createNUDClause(state, since, to);

            return deviceInventory.getConfigItems(principal, clause);
        } catch (Exception e) {
            throw new EntityException("Error while getting items with state '" + state + "'", e);
        }

    }

    /**
     * Soft deletes the config item with the given node uri
     * @param nodeURI the node uri
     * @param ts the timestamp to use in the deletion
     * @throws EntityException if an error occurs
     */
    public void removeItem(String nodeURI, Timestamp ts)
    throws EntityException {

        try {
            ConfigItem c =
                new ConfigItem(username, principal, nodeURI, null, ts);
            deviceInventory.removeConfigItem(c);
        } catch (Exception e) {
            throw new EntityException("Error while deleting config item with nodeURI '"
                                      + nodeURI + "'", e);
        }

    }

    //---------------------------------------------------------- Private Methods

    /**
     * Creates a clause to read the config items for the principal used in the
     * constructor, with the given status and changed between since and to
     * @param status the status
     * @param since the since time
     * @param to the to time
     * @return the clause
     */
    private Clause createNUDClause(char status, Timestamp since, Timestamp to) {

        List<Clause> operands = new ArrayList(0);

        if (since != null) {
            String valueSince[] = new String[]{Long.toString(since.getTime())};
            operands.add(new WhereClause("last_update", valueSince, WhereClause.OPT_GT, true));
        }

        if (to != null) {
            String valueTo[] = new String[]{Long.toString(to.getTime())};
            operands.add(new WhereClause("last_update", valueTo, WhereClause.OPT_LT, true));
        }

        String valueStatus[] = new String[]{String.valueOf(status)};
        operands.add(new WhereClause("status", valueStatus, WhereClause.OPT_EQ, true));

        if (operands.size() == 1) {
            return operands.get(0);
        }
        
        Clause wcFinal =
                new LogicalClause(LogicalClause.OPT_AND, (Clause[])operands.toArray(new Clause[operands.size()]));

        return wcFinal;
    }
}
