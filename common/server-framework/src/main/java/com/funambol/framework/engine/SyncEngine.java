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
package com.funambol.framework.engine;

import com.funambol.framework.core.Chal;
import com.funambol.framework.core.Cred;
import com.funambol.framework.core.DevInf;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.ItemizedCommand;
import com.funambol.framework.core.ModificationCommand;
import com.funambol.framework.core.Status;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.core.VerDTD;
import com.funambol.framework.database.Database;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.filter.FilterClause;
import com.funambol.framework.protocol.CommandIdGenerator;
import com.funambol.framework.security.Officer;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ClientMapping;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.SyncTimestamp;
import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import java.security.Principal;
import java.util.Date;
import java.util.List;

/**
 * A synchronization engine represents a mechanism used to drive the syncrhonization
 * process.
 * <i>SyncEngine</i> represents the Context partecipant of the Strategy pattern.<br>
 * It is a sort of factory and manager for the starategy object referenced by
 * the property <i>strategy</i> (that implementing classes must provide).<p>
 * <i>SyncEngine</i> concentrate all the implementation specific information
 * regarding strategies, sources, databases, services, etc. It is the point of
 * contact between the synchronization, protocol and server services.
 * <p>
 * When a synchronization process must take place, the <i>SyncEngine</i> will
 * pass the control to the configured strategy, which has the responsability of
 * query item sources in order to define which synchronization operations are
 * required. Then the synchronization operations are applied to the sources.
 *
 * @see SyncStrategy
 *
 * @version $Id: SyncEngine.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public interface SyncEngine {

    /**
     * Get the underlying strategy
     *
     * @return the underlying strategy
     */
    public SyncStrategy getStrategy();

    /**
     * Set the synchronization strategy to be used
     */
    public void setStrategy(SyncStrategy strategy);

    /**
     * Fires and manages the synchronization process.
     *
     * @throws Sync4jException in case of error
     */
    public void sync(Sync4jPrincipal principal) throws Sync4jException;

    /**
     *
     */
    //public void endSync();

    /**
     * Closes all synchronization.
     *
     * @throws Sync4jException in case of error
     */
    public void endSync() throws Sync4jException;

    /**
     *
     * @param cmdIdGenerator
     */
    public void setCommandIdGenerator(CommandIdGenerator cmdIdGenerator) ;


    /**
     *
     * @return
     */
    //public Configuration getConfiguration();


    /**
     *
     * @return
     */
    public Database[] getDbs();

    /**
     *
     * @param dbs
     */
    public void setDbs(Database[] dbs);

    /**
     *
     * @param principal
     * @param dbs
     * @param next
     */
    public void prepareDatabases(Sync4jPrincipal principal, Database[] dbs, SyncTimestamp next);

    /**
     *
     */
    public Officer getOfficer() ;

    /**
     *
     * @param syncTimestamp
     */
    public void setSyncTimestamp(Date syncTimestamp);

    /**
     *
     * @param device
     * @return
     * @throws DeviceInventoryException
     */
    public boolean readDevice(Sync4jDevice device) throws DeviceInventoryException;

    /**
     * 
     * @param device
     */
    public void storeDevice(Sync4jDevice device);


    /**
     *
     * @return
     */
    public DeviceInventory getDeviceInventory();


    /**
     * 
     * @param syncMLVerProto
     */
    public void setSyncMLVerProto(String syncMLVerProto);

    /**
     * 
     * @param principal
     */
    public void setPrincipal(Sync4jPrincipal principal);


    /**
     *
     * @return
     */
    public PersistentStore getStore();

    /**
     *
     * @param chal
     * @param device
     * @return
     */
    public Cred getServerCredentials(Chal chal, Sync4jDevice device);

    /**
     *
     */
    public void storeMappings();

    /**
     *
     * @param name
     * @return
     */
    public SyncSource getClientSource(String name);

    /**
     *
     * @param uri
     * @return
     */
    public int getClientSourceStatus(String uri);

    /**
     *
     * @param uri
     * @return
     */
    public String getClientStatusMessage(String uri);

    /**
     * 
     * @param source
     */
    public void addClientSource(SyncSource source);

    /**
     *
     * @param sourceUri
     * @param filter
     */
    public void setFilter(String sourceUri, FilterClause filter);

    /**
     *
     * @param verDTD
     * @return
     */
    public DevInf getServerCapabilities(VerDTD verDTD);


    /**
     *
     * @return
     */
    public List getClientSources();
   

    /**
     *
     * @param isLastMessage
     */
    public void setLastMessage(final boolean isLastMessage);


    /**
     *
     * @return
     */
    public boolean isLastMessage();


    /**
     *
     * @param msgId
     * @return
     */
    public Status[] getModificationsStatusCommands(String msgId);
   

    /**
     *
     * @param uri
     * @return
     */
    public SyncOperation[] getSyncOperations(String uri);

    /**
     *
     * @param operations
     * @param uri
     * @return
     */
    public ItemizedCommand[] operationsToCommands(SyncOperation[] operations, String uri, String mimeType);


    /**
     *
     * @param uri
     */
    public void resetSyncOperations(String uri);
 

    /**
     *
     * @param sourceUri
     * @return
     */
    public String getLastAnchor(String sourceUri);
    

    /**
     *
     * @param principal
     * @param uri
     * @param slow
     * @return
     */
    public ClientMapping getMapping(Sync4jPrincipal principal, String uri, boolean slow);


    /**
     *
     * @param uri
     * @return
     */
    public ClientMapping getMapping(String uri);


    /**
     *
     * @param syncSource
     * @param cmd
     * @param state
     * @param timestamp
     * @return
     */
    public SyncItem[] itemsToSyncItems(SyncSource syncSource, ModificationCommand cmd, char state, long timestamp);


    /**
     *
     * @param credentials
     * @return
     */
    public Sync4jUser login(Cred credentials);


    /**
     *
     * @param user
     * @param credentials
     */
    public void logout(Sync4jUser user, Cred credentials);
   
    /**
     *
     * @param principal
     * @throws PersistentStoreException
     */
    public void readPrincipal(Sync4jPrincipal principal) throws PersistentStoreException;


    /**
     *
     * @param principal
     * @param sessionId
     * @return
     */
    public Officer.AuthStatus authorizeSession(Principal principal, String sessionId);
    

    /**
     *
     * @param mimeType
     * @param sourceUri
     * @param item
     * @throws com.funambol.framework.core.Sync4jException
     */
    public void completeItemInfo(String mimeType, String sourceUri, Item item)
            throws Sync4jException;

    /**
     *
     * @param status
     * @param sourceUri
     */
    public void handleReceivedStatus(Status status, String sourceUri);
   
    /**
     *
     */
    public void suspendSynchronization();
    
    /**
     *
     */
    public void commit(int currentState, SyncTimestamp nextTimestamp, long id);

}
