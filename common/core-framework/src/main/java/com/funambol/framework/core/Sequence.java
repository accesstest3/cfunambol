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
 * This class represents the &lt;Sequence&gt; tag as defined by the SyncML
 * representation specifications.
 * A sequence can contain the following commands: Add, Replace, Delete, Copy,
 * Atomic, Map, Move, Sync, Get, Alert, Exec.
 *
 * @version $Id: Sequence.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Sequence
extends AbstractCommand
implements java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public static String COMMAND_NAME = "Sequence";

    // ------------------------------------------------------------ Private data

    private ArrayList commands = new ArrayList();

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    protected Sequence() {}

    /**
     * Create a new Sequence object. The commands in <i>commands</i>
     * must be of the allowed types.
     *
     * @param cmdID command identifier - NOT NULL
     * @param noResp is &lt;NoREsponse/&gt; required?
     * @param meta meta information
     * @param commands the sequenced commands - NOT NULL
     *
     * @throws java.lang.IllegalArgumentException is any of the parameters is invalid
     *
     */
    public Sequence(
               final CmdID             cmdID     ,
               final boolean           noResp    ,
               final Meta              meta      ,
               final AbstractCommand[] commands  ) {
        super(cmdID, noResp);

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
     * Sets the sequenced commands. The given commands must be of the allowed
     * types.
     *
     * @param commands the commands - NOT NULL and o the allawed types
     *
     * @throws IllegalArgumentException if the constraints are not met
     */
    public void setCommands(AbstractCommand[] commands) {
        if (commands == null) {
            throw new IllegalArgumentException("commands cannot be null");
        }

        for (int i = 0; i < commands.length; i++) {
            if (commands[i] == null) {
                throw new IllegalArgumentException("commands[" + i +"] cannot be null");
            } else if (   (!(commands[i] instanceof Add))
                       && (!(commands[i] instanceof Alert))
                       && (!(commands[i] instanceof Atomic))
                       && (!(commands[i] instanceof Copy))
                       && (!(commands[i] instanceof Delete))
                       && (!(commands[i] instanceof Exec))
                       && (!(commands[i] instanceof Get))
                       && (!(commands[i] instanceof Map))
                       && (!(commands[i] instanceof Move))
                       && (!(commands[i] instanceof Replace))
                       && (!(commands[i] instanceof Sync))) {
                throw new IllegalArgumentException(
                    "commands[" + i + "] cannot be a " + commands[i].getName()
                );
            }
        }
        this.commands.clear();
        this.commands.addAll(Arrays.asList(commands));
    }

    /**
     * Returns the command name
     *
     * @return the command name
     */
    public String getName() {
        return Sequence.COMMAND_NAME;
    }
}
