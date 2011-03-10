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

/**
 * This class represents the &lt;Status&gt; tag as defined by the SyncML
 * representation specifications.
 *
 *  @version $Id: Status.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Status
extends ResponseCommand
implements java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public static String COMMAND_NAME = "Status";

    // ------------------------------------------------------------ Private data

    private Chal   chal;
    private Data   data;
    private String cmd ;

    // ------------------------------------------------------------ Constructors

    /**
     * For serialiazaion purposes
     */
    protected Status() {}

    /**
     * Creates a new Status object.
     *
     * @param cmdID command identifier - NOT NULL
     * @param msgRef message reference
     * @param cmdRef command reference - NOT NULL
     * @param cmd command - NOT NULL
     * @param targetRefs target references. If null
     * @param sourceRefs source references. If null
     * @param cred authentication credentials
     * @param chal authentication challenge
     * @param data status data - NOT NULL
     * @param items command items - NOT NULL
     *
     * @throws IllegalArgumentException if any NOT NULL argument is null
     *
     */
    public Status(
               final CmdID       cmdID     ,
               final String      msgRef    ,
               final String      cmdRef    ,
               final String      cmd       ,
               final TargetRef[] targetRefs,
               final SourceRef[] sourceRefs,
               final Cred        cred      ,
               final Chal        chal      ,
               final Data        data      ,
               final Item[]      items     ) {
        super(
            cmdID,
            msgRef,
            cmdRef,
            targetRefs,
            sourceRefs,
            items
        );

        setCred(cred);
        setData(data);
        setCmd(cmd);

        this.chal = chal;
    }

    /**
     * The same as the previous constructor, but accepting <i>targetRefs</i>
     * and <i>sourceRefs</i> as String instead of String[].
     *
     * @param cmdID command identifier - NOT NULL
     * @param msgRef message reference
     * @param cmdRef command reference - NOT NULL
     * @param cmd command - NOT NULL
     * @param targetRef target references. If null a TargetRef[0] is used
     * @param sourceRef source references. If null a SourceRef[0] is used
     * @param cred authentication credentials
     * @param chal authentication challenge
     * @param data status data - NOT NULL
     * @param items command items - NOT NULL
     *
     * @throws IllegalArgumentException if any NOT NULL argument is null
     *
     */
    public Status(
               final CmdID     cmdID    ,
               final String    msgRef   ,
               final String    cmdRef   ,
               final String    cmd      ,
               final TargetRef targetRef,
               final SourceRef sourceRef,
               final Cred      cred     ,
               final Chal      chal     ,
               final Data      data     ,
               final Item[]    items    ) {
        this(
            cmdID,
            msgRef,
            cmdRef,
            cmd,
            (targetRef == null) ? null : new TargetRef[] { targetRef },
            (sourceRef == null) ? null : new SourceRef[] { sourceRef },
            cred,
            chal,
            data,
            items
        );
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the chal element
     *
     * @return the chal element
     *
     */
    public Chal getChal() {
        return chal;
    }

    /**
     * Sets the chal element
     *
     * @param chal the new chal
     */
    public void setChal(Chal chal) {
        this.chal = chal;
    }

    /**
     * Returns the status data
     *
     * @return the status data
     *
     */
    public Data getData() {
        return data;
    }

    /**
     * Sets the status data
     *
     * @param data the new data
     *
     * @throws IllegalArgumentException if data is null
     */
    public void setData(Data data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        this.data = data;
    }

    /**
     * Returns the cmd element
     *
     * @return the cmd element
     */
    public String getCmd() {
        return cmd;
    }

    /**
     * Sets the cmd element
     *
     * @param cmd the new cmd element - NOT NULL
     *
     * @throws IllegalArgumentException if cmd is null
     */
    public void setCmd(String cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("cmd cannot be null");
        }
        this.cmd = cmd;
    }

    /**
     * Returns the status code as int
     *
     * @return the status code as int
     */
    public int getStatusCode() {
        return Integer.parseInt(data.getData());
    }

    /**
     * Returns the command name
     *
     * @return the command name
     */
    public String getName() {
        return Status.COMMAND_NAME;
    }
}
