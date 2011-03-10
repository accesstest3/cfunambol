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
package com.funambol.framework.protocol;

import com.funambol.framework.core.*;
import com.funambol.framework.protocol.Flags;

/**
 * This is a base abstract class for classes that implement SyncML protocol
 * packages. A SyncML package is composed of a SyncHeader and a SyncBody which
 * follows the requirements that the protocol mandates.<br>
 * SyncPackage and its subclasses are designed to be used in two steps. First a
 * SyncPackage is created and checked for validity and compliancy with the
 * protocol. Than <i>getResponse()</i> can be used to get a response message for
 * the given request. During the request validation process some information about the
 * request message are cached into instance variables and used in <i>getResponse()</i>.<br>
 * <p>
 * Subclasses are required to override the following methods:
 * <ul>
 *  <li><i>checkHeaderRequirements()</i></li>
 *  <li><i>checkBodyRequirements()</i></li>
 *  <li><i>getResponse()</i></li>
 * </ul>
 * <p>
 * In addition, subclasses are required to call super(header, body) in their
 * constructors, so that the base package is constructed and validated.
 *
 *
 * @version $Id: SyncPackage.java,v 1.1.1.1 2008-02-21 23:35:39 stefano_fornari Exp $
 */
public abstract class SyncPackage
implements Flags {

    protected SyncHdr  syncHeader = null;
    protected SyncBody syncBody   = null;

    /**
     *  Creates a SyncPackage package.
     *
     *  @param syncHeader the header of the syncronization packet
     *  @param syncBody   the body of the syncronization packet
     */
    public SyncPackage(final SyncHdr  syncHeader,
                       final SyncBody syncBody  ) {
            this.syncHeader = syncHeader;
            this.syncBody   = syncBody  ;
    }

    // ------------------------------------------------------------------- Flags

    /**
     * Flags
     */
    private boolean[] flags = new boolean[HOW_MANY_FLAGS];

    /**
     * Sets the given flag to the given value. If flag is FLAG_ALL, all flags are
     * set; if flag is FLAG_ALL_RESPONSES_REQUIRED all response flags are set.
     *
     * @param flag the flag to be set
     * @param value the boolean value of the flag
     */
    public void setFlagValue(int flag, boolean value) {
        switch (flag) {
            case FLAG_ALL:
                for (int i=0; i<HOW_MANY_FLAGS; i++) {
                    flags[i] = value;
                }
                break;
            case FLAG_ALL_RESPONSES_REQUIRED:
                flags[FLAG_SYNC_RESPONSE_REQUIRED         ] =
                flags[FLAG_MODIFICATIONS_RESPONSE_REQUIRED] =
                flags[FLAG_SYNC_STATUS_REQUIRED           ] = value;
                break;
            default:
                flags[flag] = value;
        }
    }

    /**
     * Sets the given flag to true.
     *
     * @param flag the flag to be set
     */
    public void setFlag(int flag) {
        setFlagValue(flag, true);
    }

    /**
     * Sets the given flag to false.
     *
     * @param flag the flag to be set
     */
    public void unsetFlag(int flag) {
        setFlagValue(flag, false);
    }

    /**
     * Returns the value of the given flag
     *
     * @param flag the flag to be returned
     *
     * @return the flag value
     */
    public boolean isFlag(int flag) {
        return flags[flag];
    }

    // -------------------------------------------------------------- Properties

    /**
     * The command id generator to be used to get new command ids
     */
    protected CommandIdGenerator idGenerator = null;

    public CommandIdGenerator getIdGenerator() {
        return this.idGenerator;
    }

    public void setIdGenerator(CommandIdGenerator idGenerator) {
        if (idGenerator == null) {
            throw new NullPointerException("idGenerator cannot be null");
        }
        this.idGenerator = idGenerator;
    }

    /**
     * The session id of the session into which the message is processed
     */
    public String getSessionId() {
        return syncHeader.getSessionID().getSessionID();
    }

    /**
     * The used DTD version
     */
    public VerDTD getDTDVersion() {
        return syncHeader.getVerDTD();
    }

    /**
     * The used protocol version
     */
    public VerProto getProtocolVersion() {
        return syncHeader.getVerProto();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Checks that all requirements for the package are respected. It calls the
     * abstract methods <i>checkHeaderRequirements()</i> and <i>checkBodyRequirements()</i>.
     *
     * @throws ProtocolException
     */
    public void checkRequirements()
    throws ProtocolException
    {
        checkHeaderRequirements();
        checkBodyRequirements();
    }

    /**
     * Returns the header of the original message
     *
     * @return the header of the original message
     */
    public SyncHdr getSyncHeader() {
        return this.syncHeader;
    }

    /**
     * Returns the body of the original message
     *
     * @return the body of the original message
     */
    public SyncBody getSyncBody() {
        return this.syncBody;
    }

    // -------------------------------------------------------- Abstract methods

    /**
     * Checks that all requirements regarding the header of the initialization
     * packet are respected.
     *
     * @throws ProtocolException
     */
    abstract public void checkHeaderRequirements() throws ProtocolException;

    /**
     * Checks that all requirements regarding the body of the initialization
     * packet are respected.
     *
     * @throws ProtocolException
     */
    abstract public void checkBodyRequirements() throws ProtocolException;

    /**
     * Constructs a proper response message.<br>
     *
     * @param msgId the msg id of the response
     * @return the response message
     *
     * @throws ProtocolException in case of error or inconsistency
     */
    abstract public SyncML getResponseMessage(String msgId) throws ProtocolException;
}