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
 * This is the base (and abstract) class for response commands
 *
 * @version $Id: ResponseCommand.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public abstract class ResponseCommand
extends ItemizedCommand
implements java.io.Serializable {

    // ---------------------------------------------------------- Protected data

    /**
     * Message reference
     */
    protected String msgRef;

    /**
     * Command reference
     */
    protected String cmdRef;

    /**
     * Target references
     */
    protected ArrayList targetRef = new ArrayList();

    /**
     * Source references
     */
    protected ArrayList sourceRef = new ArrayList();

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    protected ResponseCommand() {}

    /**
     * Creates a new ResponseCommand object.
     *
     * @param cmdID the command idendifier  - NOT NULL
     * @param msgRef message reference
     * @param cmdRef command reference - NOT NULL
     * @param targetRefs target references
     * @param sourceRefs source references
     * @param items command items
     *
     * @throws IllegalArgumentException if any of the NOT NULL parameter is null
     */
    public ResponseCommand(
        final CmdID cmdID     ,
        final String            msgRef    ,
        final String            cmdRef    ,
        final TargetRef[]       targetRefs,
        final SourceRef[]       sourceRefs,
        final Item[]            items     ) {
        super(cmdID, items);

        setCmdRef(cmdRef);

        this.msgRef = msgRef;

        setTargetRef(targetRefs);
        setSourceRef(sourceRefs);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the message reference
     *
     * @return the message reference
     *
     */
    public String getMsgRef() {
        return this.msgRef;
    }

    /**
     * Sets the message reference
     *
     * @param msgRef message reference
     */
    public void setMsgRef(String msgRef) {
        this.msgRef = msgRef;
    }

    /**
     * Returns the command reference
     *
     * @return the command reference
     *
     */
    public String getCmdRef() {
        return cmdRef;
    }

    /**
     * Sets the command reference
     *
     * @param cmdRef commandreference - NOT NULL
     *
     * @throws IllegalArgumentException if cmdRef is null
     */
    public void setCmdRef(String cmdRef) {
        if (cmdRef == null) {
            throw new IllegalArgumentException("cmdRef cannot be null");
        }
        this.cmdRef = cmdRef;
    }

    /**
     * Returns the target references
     *
     * @return the target references
     *
     */
    public ArrayList getTargetRef() {
        return this.targetRef;
    }

    /**
     * Sets the target references
     *
     * @param targetRefs target refrences
     */
    public void setTargetRef(TargetRef[] targetRefs) {
        if (targetRefs == null) {
            this.targetRef = null;
        } else {
            this.targetRef.clear();
            this.targetRef.addAll(Arrays.asList(targetRefs));
        }
    }

    /**
     * Returns the source references
     *
     * @return the source references
     *
     */
    public ArrayList getSourceRef() {
        return this.sourceRef;
    }

    /**
     * Sets the source references
     *
     * @param sourceRefs source refrences
     */
    public void setSourceRef(SourceRef[] sourceRefs) {
        if (sourceRefs == null) {
            this.sourceRef = null;
        } else {
            this.sourceRef.clear();
            this.sourceRef.addAll(Arrays.asList(sourceRefs));
        }
    }

    /**
     * Returns the command name. It must be redefined by subclasses.
     *
     * @return the command name
     */
    abstract public String getName();

}
