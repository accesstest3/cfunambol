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
package com.funambol.framework.core;

import java.util.*;

/**
 * Corresponds to the &lt;SyncBody&gt; element in the SyncML represent DTD
 *
 * @see SyncHdr
 *
 * @version $Id: SyncBody.java,v 1.3 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class SyncBody implements java.io.Serializable {
    // ------------------------------------------------------------ Private data
    private ArrayList commands = new ArrayList();
    private Boolean finalMsg;

    // ------------------------------------------------------------ Constructors

    /** For serialization purposes */
    protected SyncBody() {}

    /**
     * Create a new SyncBody object. The commands in <i>commands</i>
     * must be of the allowed types.
     *
     * @param commands The array elements must be an instance of one of these
     *                 classes: {@link Alert},  {@link Atomic}, {@link Copy},
     *                 {@link Exec}, {@link Get}, {@link Map}, {@link Put},
     *                 {@link Results}, {@link Search}, {@link Sequence},
     *                 {@link Status}, {@link Sync}, {@link Add}, {@link Move},
     *                 {@link Replace}, {@link Delete}
     * @param finalMsg is true if this is the final message that is being sent
     *
     */
    public SyncBody( final AbstractCommand[] commands, final boolean finalMsg) {

        setCommands(commands);
        this.finalMsg = (finalMsg) ? new Boolean(finalMsg) : null;
    }

    // ---------------------------------------------------------- Public methods

    /**
     *
     *  @return the return value is guaranteed to be non-null. Also,
     *          the elements of the array are guaranteed to be non-null.
     *
     */
    public ArrayList getCommands() {
        return this.commands;
    }

    /**
     * This get method always returns empty ArrayList. This is used
     * in the JiBX mapping in order to unmarshall commands that could
     * be followed by the tag &lt;Final/&gt;
     *
     * @return empty ArrayList
     */
    public ArrayList getEmptyCommands() {
        return new ArrayList();
    }

    /**
     * Add the list of commands to the global ArrayList of commands.
     * This add method was introduced because into SyncML message
     * the tag &lt;Final/&gt; could be inserted between the AbstractCommand
     * and not at the end of SyncBody.
     *
     * The given commands must be of the allowed types.
     *
     * @param commands the commands - NOT NULL and o the allowed types
     *
     * @throws IllegalArgumentException if the constraints are not met
     */
    public void addAllCommands(ArrayList commands) {
		if (commands == null) {
			return;
		}
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i) == null) {
                throw new IllegalArgumentException("commands[" + i +"] cannot be null");
            } else if (   (!(commands.get(i) instanceof Alert))
                       && (!(commands.get(i) instanceof Add))
                       && (!(commands.get(i) instanceof Atomic))
                       && (!(commands.get(i) instanceof Copy))
                       && (!(commands.get(i) instanceof Delete))
                       && (!(commands.get(i) instanceof Exec))
                       && (!(commands.get(i) instanceof Get))
                       && (!(commands.get(i) instanceof Map))
                       && (!(commands.get(i) instanceof Move))
                       && (!(commands.get(i) instanceof Put))
                       && (!(commands.get(i) instanceof Replace))
                       && (!(commands.get(i) instanceof Results))
                       && (!(commands.get(i) instanceof Search))
                       && (!(commands.get(i) instanceof Sequence))
                       && (!(commands.get(i) instanceof Status))
                       && (!(commands.get(i) instanceof Sync))) {
                throw new IllegalArgumentException(
                    "commands[" + i + "] cannot be a " + ((AbstractCommand)commands.get(i)).getName()
                );
            }
        }

        this.commands.addAll(commands);
    }

    /**
     * Sets the sequenced commands. The given commands must be of the allowed
     * types.
     *
     * @param commands the commands - NOT NULL and o the allowed types
     *
     * @throws IllegalArgumentException if the constraints are not met
     */
    public void setCommands(AbstractCommand[] commands) {
        if (commands == null) {
            throw new IllegalArgumentException("commands cannot be null");
        }
        this.commands.clear();
        addAllCommands(new ArrayList(Arrays.asList(commands)));
    }

    /**
     * Sets the message as final
     *
     * @param finalMsg the Boolean value of finalMsg property
     */
    public void setFinalMsg(Boolean finalMsg) {
        this.finalMsg = (finalMsg.booleanValue()) ? finalMsg : null;
    }

    /**
     * Gets the value of finalMsg property
     *
     * @return true if this is the final message being sent, otherwise false
     *
     */
    public boolean isFinalMsg() {
        return (finalMsg != null);
    }

    /**
     * Gets the value of finalMsg property
     *
     * @return true if this is the final message being sent, otherwise null
     *
     */
    public Boolean getFinalMsg() {
        if (finalMsg == null || !finalMsg.booleanValue()) {
            return null;
        }
        return finalMsg;
    }
}
