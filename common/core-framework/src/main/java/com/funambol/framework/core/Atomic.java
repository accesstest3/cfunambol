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
 * Corresponds to the &lt;Atomic&gt; tag in the SyncML represent DTD
 *
 * @version $Id: Atomic.java,v 1.6 2007/06/15 14:43:35 luigiafassina Exp $
 */
public class Atomic
extends AbstractCommand
implements java.io.Serializable {

    // --------------------------------------------------------------- Constants
    public static String COMMAND_NAME = "Atomic";

    // ------------------------------------------------------------ Private Data
    private ArrayList commands = new ArrayList();

    // ------------------------------------------------------------ Constructors

    /** For serialization purposes */
    protected Atomic(){}

    /**
     * Creates a new Atomic object with the given command identifier, noResponse,
     * meta and an array of abstract command
     *
     * @param cmdID the command identifier - NOT NULL
     * @param noResp is true if no response is required
     * @param meta the meta data
     * @param commands an array of abstract command - NOT NULL
     */
    public Atomic(final CmdID cmdID,
                  final boolean noResp,
                  final Meta meta,
                  final AbstractCommand[] commands) {
        super(cmdID);

        this.noResp  = (noResp) ? new Boolean(noResp) : null;
        setMeta(meta);
        setCommands(commands);

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets an array of AbstractCommand
     *
     * @return an array of command objects
     */
    public ArrayList getCommands() {
        return this.commands;
    }

    /**
     * Sets an array of AbstractCommand
     *
     * @param commands the array of AbstractCommand
     *
     */
    public void setCommands(AbstractCommand[] commands) {
        if (commands == null || commands.length == 0) {
            throw new IllegalArgumentException("commands cannot be null");
        }

        for (int i=0; i<commands.length; i++) {
            if ((!(commands[i] instanceof Add))
            &&  (!(commands[i] instanceof Alert))
             && (!(commands[i] instanceof Delete))
             && (!(commands[i] instanceof Copy))
             && (!(commands[i] instanceof Exec))
             && (!(commands[i] instanceof Get))
             && (!(commands[i] instanceof Map))
             && (!(commands[i] instanceof Move))
             && (!(commands[i] instanceof Replace))
             && (!(commands[i] instanceof Sequence))
             && (!(commands[i] instanceof Sync))) {

                throw new IllegalArgumentException(
                                    "illegal nested command: " + commands[i]);
            }
        }
        this.commands.clear();
        this.commands.addAll(Arrays.asList(commands));
    }

    /**
     * Gets the command name property
     *
     * @return the command name property
     */
    public String getName() {
        return Atomic.COMMAND_NAME;
    }
}
