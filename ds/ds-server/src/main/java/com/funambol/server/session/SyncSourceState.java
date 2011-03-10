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
package com.funambol.server.session;

import java.util.HashMap;

/**
 * This class keeps the state of the SyncSources under sync. For more details 
 * about SyncSource state management see the architecture document.
 *
 *
 */
public class SyncSourceState {
    
    // --------------------------------------------------------------- Constants
    
    public static final Byte STATE_IDLE       = new Byte((byte) 1);
    public static final Byte STATE_CONFIGURED = new Byte((byte) 2);
    public static final Byte STATE_SYNCING    = new Byte((byte) 3);
    public static final Byte STATE_COMMITTED  = new Byte((byte) 4);
    public static final Byte STATE_ERROR      = new Byte((byte)-1);
    
    public static final String STATE_NAME_IDLE       = "IDLE"      ;
    public static final String STATE_NAME_CONFIGURED = "CONFIGURED";
    public static final String STATE_NAME_SYNCING    = "SYNCING"   ;
    public static final String STATE_NAME_COMMITTED  = "COMMITTED" ;
    public static final String STATE_NAME_ERROR      = "ERROR"     ;
    public static final String STATE_NAME_UNKNOWN    = "UNKNOWN"   ;
    
    // ------------------------------------------------------------ Private data
    
    /**
     * All states are stored 
     *
     */
    private HashMap states;
    
    // ------------------------------------------------------------ Constructors
    
    /** Creates a new instance of SyncSourceState */
    public SyncSourceState() {
        reset();
    }
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Sets the state of the given SyncSource
     *
     * @param uri the SyncSource URI
     */
    public void setState(final String uri, Byte newState) {
        states.put(uri, newState);
    }
    
    /**
     * Returns the state of the given SyncSource. If the SyncSource is not in 
     * <i>states</i>, it throws an IllegalStateException.
     *
     * @param uri the SyncSource URI
     *
     * @return the SyncSource current state
     *
     * @throws IllegalStateException in the case uri is not found in <code>states</code>.
     */
    public Byte getState(final String uri) 
    throws IllegalStateException {
        Byte state = (Byte)states.get(uri);
        
        if (state == null) {
            throw new IllegalStateException("No state associated to the SyncSource " + uri);
        }
        
        return state;
    }
    
    /**
     * Returns the state of the given SyncSource as int. It simply calls 
     * <code>getState(uri)</code> and converts the result to int.
     *
     * @param uri the SyncSource URI
     *
     * @return the SyncSource current state
     *
     * @throws IllegalStateException in the case uri is not found in <code>states</code>.
     */
    public int getStateAsInt(final String uri) 
    throws IllegalStateException {
        Byte state = getState(uri);
        
        return state.intValue();
    }
    
    /**
     * Returns the state name of the given SyncSource. If the SyncSource is not 
     * in <i>states</i>, it throws an IllegalStateException.
     *
     * @param uri the SyncSource URI
     *
     * @return the SyncSource current state name
     */
    public String getStateName(final String uri) {
        String name;
        
        try {
            Byte state = getState(uri);
        
            if (STATE_IDLE.equals(state)) {
                name = STATE_NAME_IDLE;
            } else if (STATE_CONFIGURED.equals(state)) {
                name = STATE_NAME_CONFIGURED;
            } else if (STATE_SYNCING.equals(state)) {
                name = STATE_NAME_SYNCING;
            } else if (STATE_COMMITTED.equals(state)) {
                name = STATE_NAME_COMMITTED;
            } else {
                name = STATE_NAME_UNKNOWN;
            }
        } catch (IllegalStateException e) {
            name = STATE_NAME_UNKNOWN;
        }
        
        return name;
    }
    
    // ------------------------------------------------ State transition methods
    
    /**
     * Move state to IDLE
     *
     * @param uri the SyncSource URI
     */
    public void moveToIdle(String uri) {
        setState(uri, STATE_IDLE);
    }
    
    /**
     * Move state to CONFIGURED
     *
     * @param uri the SyncSource URI
     */
    public void moveToConfigured(String uri) {
        setState(uri, STATE_CONFIGURED);
    }
    
    /**
     * Move state to SYNCING
     *
     * @param uri the SyncSource URI
     */
    public void moveToSyncing(String uri) {
        setState(uri, STATE_SYNCING);
    }
    
    /**
     * Move state to COMMITTED
     *
     * @param uri the SyncSource URI
     */
    public void moveToCommitted(String uri) {
        setState(uri, STATE_COMMITTED);
    }
    
    /**
     * Move state to ERROR
     *
     * @param uri the SyncSource URI
     */
    public void moveToError(String uri) {
        setState(uri, STATE_ERROR);
    }
    
    // -------------------------------------------------- Querying state methods
    
    /**
     * Is the given SyncSource in the IDLE state?
     *
     * @param uri the SyncSource URI
     *
     * @return true if the given SyncSource is in the IDLE state, false otherwise
     *
     */
    public boolean isIdle(String uri) {
        return STATE_IDLE.equals(states.get(uri));
    }
    
    /**
     * Is the given SyncSource in the CONFIGURED state?
     *
     * @param uri the SyncSource URI
     *
     * @return true if the given SyncSource is in the CONFIGURED state, false otherwise
     *
     */
    public boolean isConfigured(String uri) {
        return STATE_CONFIGURED.equals(states.get(uri));
    }
    
    /**
     * Is the given SyncSource in the SYNCING state?
     *
     * @param uri the SyncSource URI
     *
     * @return true if the given SyncSource is in the SYNCING state, false otherwise
     *
     */
    public boolean isSyncing(String uri) {
        return STATE_SYNCING.equals(states.get(uri));
    }
    
    /**
     * Is the given SyncSource in the COMMITTED state?
     *
     * @param uri the SyncSource URI
     *
     * @return true if the given SyncSource is in the COMMITTED state, false otherwise
     *
     */
    public boolean isCommitted(String uri) {
        return STATE_COMMITTED.equals(states.get(uri));
    }
    
    /**
     * Is the given SyncSource in the ERROR state?
     *
     * @param uri the SyncSource URI
     *
     * @return true if the given SyncSource is in the ERROR state, false otherwise
     *
     */
    public boolean isError(String uri) {
        return STATE_ERROR.equals(states.get(uri));
    }
    
    
    // --------------------------------------------------------- Private methods
    
    /**
     * Resets all states
     *
     */
    private void reset() {
        this.states = new HashMap();
    }
    
}
