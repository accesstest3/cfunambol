/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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

import java.util.List;
import java.util.ArrayList;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represent a description of a sent command. In order to avoid to keep in memory
 * large information, this class MUST contain only the required information.
 *
 * @version $Id: CommandDescriptor.java,v 1.2 2007-11-28 11:15:38 nichele Exp $
 */
public class CommandDescriptor {

    // The message id of the message that contains the command
    private String msgId;

    // The command id
    private String cmdId;

    // The command name
    private String commandName;

    // The source uri relative at the command
    private String sourceUri;

    // If the command is an ItemizedCommand, contains the list of luid
    private List luids;

    // If the command is an ItemizedCommand, contains the list of guid
    private List guids;

    /**
     * Getter for property msgId
     * @return the msgId
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * Setter for property msgId
     * @param msgId the new value
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * Getter for property cmdId
     * @return the cmdId
     */
    public String getCmdId() {
        return cmdId;
    }

    /**
     * Setter for property cmdId
     * @param cmdId the new value
     */
    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    /**
     * Getter for property commandName
     * @return the commandName
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Setter for property commandName
     * @param commandName the new value
     */
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    /**
     * Getter for property sourceUri
     * @return the sourceUri
     */
    public String getSourceUri() {
        return sourceUri;
    }

    /**
     * Setter for property sourceUri
     * @param sourceUri the new value
     */
    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    /**
     * Getter for property luids
     * @return the luids
     */
    public List getLuids() {
        return luids;
    }

    /**
     * Setter for property luids
     * @param luids the new value
     */
    public void setLuids(List luids) {
        this.luids = luids;
    }

    /**
     * Getter for property guids
     * @return the guids
     */
    public List getGuids() {
        return guids;
    }

    /**
     * Setter for property guid
     * @param guid the new value
     */
    public void setGuids(List guids) {
        this.guids = guids;
    }

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new CommandDescriptor
     * @param msgId String
     * @param cmdId String
     * @param commandName String
     * @param sourceUri String
     * @param guids List
     * @param luids List
     */
    public CommandDescriptor(String msgId,
                             String cmdId,
                             String commandName,
                             String sourceUri,
                             List   guids,
                             List   luids) {

        this.msgId       = msgId;
        this.cmdId       = cmdId;
        this.commandName = commandName;
        this.sourceUri   = sourceUri;
        this.guids       = guids;
        this.luids       = luids;

    }

    /**
     * Adds a new luid
     * @param luid String
     */
    public void addLuid(String luid) {
        if (luids == null) {
            luids = new ArrayList();
        }
        if (!luids.contains(luid)) {
            luids.add(luid);
        }
    }

    /**
     * Add a new guid
     * @param guid String
     */
    public void addGuid(String guid) {
        if (guids == null) {
            guids = new ArrayList();
        }
        if (!guids.contains(guid)) {
            guids.add(guid);
        }
    }

    /**
     * Returns true if the given luid is contained in the luids list,
     * false otherwise
     * @param luid String
     * @return boolean
     */
    public boolean containsLuid(String luid) {
        if (luid == null || luid.length() == 0) {
            return true;
        }
        if (luids == null) {
            return false;
        }
        return luids.contains(luid);
    }

    /**
     * Returns true if the given guid is contained in the guids list,
     * false otherwise
     * @param guid String
     * @return boolean
     */
    public boolean containsGuid(String guid) {
        if (guid == null || guid.length() == 0) {
            return true;
        }

        if (guids == null) {
            return false;
        }
        return guids.contains(guid);
    }

    /**
     * Are there any guids ?
     * @return boolean
     */
    public boolean isGuidsEmpty() {
        if (guids == null || guids.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Are there any luids ?
     * @return boolean
     */
    public boolean isLuidsEmpty() {
        if (luids == null || luids.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns a String representation of this CommandDescriptor
     * @return a String representation of this CommandDescriptor
     */
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);

        sb.append("MsgId"      , msgId).
           append("CmdId"      , cmdId).
           append("CommandName", commandName).
           append("SourceUri"  , sourceUri).
           append("Guids"      , guids).
           append("Luids"      , luids);

        return sb.toString();
    }
}
