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
package com.funambol.framework.server;

import java.util.*;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

import com.funambol.framework.security.Sync4jPrincipal;

/**
 * Represent the mappings of a principal for a syncsource.
 *
 * @version $Id: ClientMapping.java,v 1.3 2008-05-15 05:55:30 nichele Exp $
 */
public class ClientMapping {

    //Contain a Map of GUID keys and ClientMappingEntry values
    private Map<String, ClientMappingEntry> clientMapping =
        new LinkedHashMap<String, ClientMappingEntry>();

    //Contain the principal of the client device
    private Sync4jPrincipal principal = null;

    // the requested database
    private String dbURI;

    // Modified entries
    private List<String> modifiedKeys = new ArrayList<String>();

    // Added entries
    private List<String> addedKeys = new ArrayList<String>();

    // Deleted entries
    private List<String> deletedKeys = new ArrayList<String>();

    // Keep a map of the entry to delete when the status is received
    private Map<String, ClientMappingEntry> mappingToDelete =
        new LinkedHashMap<String, ClientMappingEntry>();

    //Transient keyword will disable serialisation for this member
    private transient FunambolLogger log = FunambolLoggerFactory.getLogger();

    // ------------------------------------------------------------- Contructors

    /**
     * Construct a client mapping to a device Id the mapping must be populate
     * with data from the persistence store by calling
     * PersistenceStoreManager.read.
     *
     * @param principal
     * @param dbURI
     */
    public ClientMapping(Sync4jPrincipal principal, String dbURI) {
        this.principal = principal;
        this.dbURI     = dbURI    ;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * @return true if item where added since the initialisation
     */
    public boolean isAdded() {
        return !addedKeys.isEmpty();
    }

    /**
     * @return true if the mapping where modified since initialization
     */
    public boolean isModified() {
        return !modifiedKeys.isEmpty();
    }

    /**
     * @return true if item where deleted since the initialisation
     */
    public boolean isDeleted() {
        return !deletedKeys.isEmpty();
    }

    /**
     * Initialize the client mapping with data from the Persistence Store
     * @param mapping A structure that contains a GUID like key and a
     * ClientMappingEntry as value.
     */
    public void initializeFromMapping(Map<String, ClientMappingEntry> mapping) {
        resetMapping();
        clientMapping.putAll(mapping);
    }

    /**
     * Get the deleted LUIDs from the client mapping
     * @return a String[] of LUIDs deleted
     */
    public String[] getDeletedLuids() {
        return (String[])deletedKeys.toArray(new String[deletedKeys.size()]);
    }

    /**
     * Get the modified LUIDs from the client mapping
     * @return a String[] of LUIDs modified
     */
    public String[] getModifiedLuids() {
        return (String[])modifiedKeys.toArray(new String[modifiedKeys.size()]);
    }

    /**
     * Get the added LUIDs from the client mapping
     * @return a String[] of LUIDs modified
     */
    public String[] getAddedLuids() {
        return (String[])addedKeys.toArray(new String[addedKeys.size()]);
    }

    /**
     * Returns the lastAnchor of the mapping identified with the given GUID
     * @param guid String
     * @return String
     */
    public String getLastAnchor(String guid) {
        ClientMappingEntry mappingEntry = getClientMappingEntry(guid);
        if (mappingEntry == null) {
            return null;
        }
        return mappingEntry.getLastAnchor();
    }

    /**
     * Get the current mapping.
     *
     * @return the current mapping as a List
     */
    public List<ClientMappingEntry> getMapping() {
        List ret = new ArrayList();
        ret.addAll(clientMapping.values());
        return ret;
    }

    /**
     * Returns a Map with the valid GUID-LUID mapping.
     *
     * @return a Map with the valid GUID-LUID mapping.
     */
    public Map<String, String> getValidMapping() {

        Map<String, String> result = new LinkedHashMap<String, String>();
        Iterator<ClientMappingEntry> itMappingEntry = 
            clientMapping.values().iterator();
        ClientMappingEntry mappingEntry   = null;

        while (itMappingEntry.hasNext()) {
            mappingEntry = (ClientMappingEntry)itMappingEntry.next();
            if (mappingEntry.isValid()) {
                result.put(mappingEntry.getGuid(), mappingEntry.getLuid());
            }
        }
        return result;
    }

    /**
     * Get the Client Device Id that correspond to that Client
     * @return The Device Id
     */
    public String getClientDeviceId() {
        return principal.getDeviceId();
    }

    /**
     * Get the principal
     *
     * @return the principal
     */
    public Sync4jPrincipal getPrincipal() {
        return principal;
    }

    /**
     * Get the database uri
     *
     * @return <i>dbURI</i>
     */
    public String getDbURI() {
        return dbURI;
    }

    /**
     * Add a mapping between a GUID and a LUID from the server
     * @param guid The GUID of the server item
     * @param luid The LUID of the client item
     */
    private void addMapping(String guid, String luid, String lastAnchor) {

        if (log.isTraceEnabled()) {
            log.trace("Adding mapping LUID-GUID '" + luid + "-" + guid + "' " +
                      "with lastAnchor '" + lastAnchor + "'");
        }

        //
        // if the same guid was already mapped to a luid, we have to remeber
        // the former one and remove it from the list of updated/deleted luids
        //
        ClientMappingEntry newEntry =
            new ClientMappingEntry(guid, luid, lastAnchor);
        addedKeys.add(luid);

        ClientMappingEntry oldMapping =
            (ClientMappingEntry)clientMapping.get(guid);

        if (oldMapping != null) {

            String oldLuid = oldMapping.getLuid();
            if (oldLuid != null) {
                removeDeletedKey(oldLuid);
                removeModifiedKey(oldLuid);
                removeAddedKey(oldLuid);
            }
            clientMapping.remove(guid);
        }
        clientMapping.put(guid, newEntry);
    }

    /**
     * Get the mapped GUID for the given LUID.
     * @param luid The LUID of the client item
     * @return The mapped value for the key in input
     */
    public String getMappedValueForLuid(String luid) {
        String result = null;

        Iterator           itMappingEntry = clientMapping.values().iterator();
        ClientMappingEntry mappingEntry   = null;

        while (itMappingEntry.hasNext()) {
            mappingEntry = (ClientMappingEntry)itMappingEntry.next();
            if (mappingEntry.getLuid().equals(luid)) {
                return mappingEntry.getGuid();
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("No mapping found for LUID: " + luid);
        }
        return result;
    }

    /**
     * Get the mapped LUID key for the given GUID value.
     * @param guid The GUID of the client item
     * @return The mapped value for the key in input
     */
    public String getMappedValueForGuid(String guid) {
        String luid = null;

        ClientMappingEntry cme = clientMapping.get(guid);
        if (cme == null) {
            if (log.isTraceEnabled()) {
                log.trace("No mapping found for GUID: " + guid);
            }
        } else {
            luid = cme.getLuid();
        }

        return luid;
    }

    /**
     * Remove a mapped values from the cache given a GUID
     * @param guid The GUID for the item from the client
     * @param isDiffered if true the mapping is just removed from the clientMapping,
     *                   if false the mapping is removed from the database
     */
    public void removeMappedValuesForGuid(String guid, boolean isDiffered) {

        if (isDiffered) {
            ClientMappingEntry entry = getClientMappingEntry(guid);

            if (entry != null) {
                if (!mappingToDelete.containsKey(guid)) {
                    mappingToDelete.put(guid, entry);
                }
            }
        } else {

            ClientMappingEntry entry = mappingToDelete.get(guid);
            if (entry != null) {
                //
                // There is a previous entry deleted with isDiffered = true
                //
                mappingToDelete.remove(guid);
                addDeletedKey(entry.getLuid());
            }
            //
            // BTW, we check is there is a mapping in the current clientMapping
            //
            entry = getClientMappingEntry(guid);
            if (entry != null) {
                clientMapping.remove(guid);
                addDeletedKey(entry.getLuid());
            }
        }
    }

    /**
     * Remove a mapped values given a GUID only if the mapping is already removed
     * with isDiffered = true.
     * @param guid The GUID for the item from the client
     */
    public void confirmRemoveMappedValuesForGuid(String guid) {

        ClientMappingEntry entry = mappingToDelete.get(guid);
        if (entry != null) {
            //
            // There is a previous entry deleted with isDiffered = true
            //
            addDeletedKey(entry.getLuid());
        }
    }

    /**
     * Updates a key value mapping. If the mapping does not exist, it calls
     * <code>addMapping(guid, luid)</code>, otherwise the existing mapping is
     * updated.
     *
     * @param guid The GUID value from the server item
     * @param luid The LUID value from the client item
     * @param lastAnchor The lastAnchor of the mapping
     */
    public void updateMapping(String guid, String luid, String lastAnchor) {

        if (log.isTraceEnabled()) {
            log.trace("Updating mapping LUID-GUID '" + luid + "-" + guid + "' " +
                      "with lastAnchor '" + lastAnchor + "'");
        }

        //
        // If new, just call addMapping
        //
        ClientMappingEntry oldMappingEntry = clientMapping.get(guid);
        if (oldMappingEntry == null) {
            if (log.isTraceEnabled()) {
                log.trace("The mapping is new.");
            }
            addMapping(guid, luid, lastAnchor);
            return;
        }

        //
        // if the same guid was already mapped to a luid, we have to remember
        // the former one and remove it from the list of added/updated/deleted luids
        //
        String oldLuid = oldMappingEntry.getLuid();
        modifiedKeys.add(luid);

        if (oldLuid != null && !oldLuid.equals(luid)) {
            removeDeletedKey(oldLuid);
            removeModifiedKey(oldLuid);
            removeAddedKey(oldLuid);
        }

        clientMapping.put(guid, new ClientMappingEntry(guid, luid, lastAnchor));

        //
        // If we add an entry that was considered deleted or added,
        // remove it from the list.
        //
        removeDeletedKey(luid);
        removeAddedKey(luid);
    }

    /**
     * Updates a key value mapping. If the mapping does not exist, it calls
     * <code>addMapping(guid, luid)</code>, otherwise the existing mapping is
     * updated.
     *
     * @param guid The GUID value from the server item
     * @param luid The LUID value from the client item
     * @param lastAnchor The last Anchor of the mapping
     */
    public void updateLastAnchor(String guid, String luid, String lastAnchor) {

        if (log.isTraceEnabled()) {
            log.trace("Updating mapping LUID-GUID '" + luid + "-" + guid + "' " +
                      "with lastAnchor '" + lastAnchor + "'");
        }

        ClientMappingEntry mappingEntry = clientMapping.get(guid);
        if (mappingEntry == null) {
            if (log.isTraceEnabled()) {
                log.trace("Mapping not found.");
            }
            return;
        }

        if (luid.equals(mappingEntry.getLuid())) {
            mappingEntry.setLastAnchor(lastAnchor);
            modifiedKeys.add(mappingEntry.getLuid());
        } else {
            if (log.isTraceEnabled()) {
                log.trace("There is a mapping with GUID '" + guid + "' but with " +
                          " different luid (" + mappingEntry.getLuid() + ")");
            }
        }
    }

    /**
     * Checks if the mapping with the given guid is valid.
     * @param guid the guid of the mapping to check
     * @return true if the mapping is valid, false if the mapping isn't valid or
     *         it doesn't exist.
     */
    public boolean isValid(String guid) {
        ClientMappingEntry mappingEntry = getClientMappingEntry(guid);
        if (mappingEntry == null) {
            return false;
        }
        return mappingEntry.isValid();
    }

    /**
     * Clears these mappings moving the existing and new mappings to deleted.
     * This is used for example to re-initialize the LUID-GUID mapping for a
     * slow sync.
     */
    public void clearMappings() {
        Iterator it = clientMapping.values().iterator();
        while (it.hasNext()) {
            deletedKeys.add(((ClientMappingEntry)it.next()).getLuid());
        }

        deletedKeys.addAll(modifiedKeys);

        clientMapping.clear();
    }

    @Override
    public String toString() {
         ToStringBuilder sb = new ToStringBuilder(this);

         sb.append("clientMapping", clientMapping);
         sb.append("addedKeys"    , addedKeys );
         sb.append("modifiedKeys" , modifiedKeys );
         sb.append("deletedKeys"  , deletedKeys  );

         return sb.toString();
    }

    /**
     * Resets the modified (added/updated/deleted) uids
     */
    public void resetModifiedKeys() {
        addedKeys.clear();
        modifiedKeys.clear();
        deletedKeys.clear();
    }

    /**
     * Get the current ClientMappingEntry for the given guid.
     *
     * @param guid the GUID to check
     * @return the current mapping ClientMappingEntry
     */
    public ClientMappingEntry getClientMappingEntry(String guid) {
        return clientMapping.get(guid);
    }

    /**
     * Get the current ClientMappingEntry for the given luid.
     *
     * @param luid the LUID to check
     * @return the current mapping ClientMappingEntry
     */
    public ClientMappingEntry getClientMappingEntryByLuid(String luid) {

        Iterator           itMappingEntry = clientMapping.values().iterator();
        ClientMappingEntry mappingEntry   = null;

        while (itMappingEntry.hasNext()) {
            mappingEntry = (ClientMappingEntry)itMappingEntry.next();
            if (mappingEntry.getLuid().equals(luid)) {
                return mappingEntry;
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("No mapping found for LUID: " + luid);
        }
        return null;

    }

    // --------------------------------------------------------- Private methods

    /**
     * Reset the content of the Client mapping list and the state
     * of the current mapping.
     */
    private void resetMapping() {
        //We clean the list
        if (!clientMapping.isEmpty()) {
            clientMapping.clear();
        }

        resetModifiedKeys();
    }

    /**
     * Remove LUID from the deleted entries
     * @param luid The LUID for the item from the client
     */
    private void removeDeletedKey(String luid) {
        if (deletedKeys.contains(luid)) {
            if (log.isTraceEnabled()) {
                log.trace("Removing deleted LUID '" + luid + "' from deletedKeys");
            }
            deletedKeys.remove(luid);
        }
    }

    /**
     * Remove LUID from the modified entries
     * @param luid The LUID for the item from the client
     */
    private void removeModifiedKey(String luid) {
        if (modifiedKeys.contains(luid)) {
            if (log.isTraceEnabled()) {
                log.trace("Removing modified LUID '" + luid + "' from modifiedKeys");
            }
            modifiedKeys.remove(luid);
        }
    }

    /**
     * Remove LUID from the added entries
     * @param luid The LUID for the item from the client
     */
    private void removeAddedKey(String luid) {
        if (addedKeys.contains(luid)) {
            if (log.isTraceEnabled()) {
                log.trace("Removing modified LUID '" + luid + "' from addedKeys");
            }
            addedKeys.remove(luid);
        }
    }

    /**
     * Add LUID into the deleted keys.
     * @param luid The LUID for the item from the client
     */
    private void addDeletedKey(String luid) {
        removeModifiedKey(luid);
        removeAddedKey(luid);
        
        deletedKeys.add(luid);
    }

}
