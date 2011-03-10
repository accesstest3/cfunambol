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

import java.util.*;

import com.funambol.framework.core.*;
import com.funambol.framework.database.Database;
import com.funambol.framework.filter.*;
import com.funambol.framework.tools.*;

import com.funambol.server.tools.*;

/**
 *
 * @version $Id: ProtocolUtil.java,v 1.1.1.1 2008-02-21 23:35:38 stefano_fornari Exp $
 */
public class ProtocolUtil extends CoreUtil {

    
    /**
     * Creates and returns and AlertCommand for the synchronization of the
     * given database.
     *
     * @param id the command id - NULL
     * @param noResponse
     * @param credential - NULL
     * @param db the database to be synchronized - NOT NULL
     *
     * @return the AlertCommand object
     */
    public static Alert createAlertCommand(CmdID    id        ,
                                           boolean  noResponse,
                                           Cred     credential,
                                           Database db        ) {
        Item[] items = new Item[1];
        Anchor serverAnchor = db.getServerAnchor();
        Meta meta = new Meta();
        meta.setAnchor(serverAnchor);
        items[0] = new Item(db.getSource(),
                            db.getTarget(),
                            meta          ,
                            null          ,  //data
                            false         ); //MoreData

        return new Alert(
                   id            ,
                   noResponse    ,
                   credential    ,
                   db.getMethod(),
                   items
               );
    }

    /**
     * Extracts the FilterClause from the given item
     * @param item Item
     * @throw ProtocolException
     * @return Filter
     */
    public static FilterClause extractFilterClauseFromItem(Item item)
        throws ProtocolException {

        if (item == null) {
            return null;
        }

        Target target = item.getTarget();
        if (target == null) {
            return null;
        }

        Filter filter = target.getFilter();
        if (filter == null) {
            return null;
        }

        FilterClause filterClause = new FilterClause();

        if (FilterType.TYPE_INCLUSIVE.equalsIgnoreCase(filter.getFilterType())) {
            filterClause.setInclusive(true);
        }

        //
        // Create the clause for the records filter
        //
        Clause recordClause  = null;
        Item   record        = filter.getRecord();

        if (record != null) {
            String type = record.getMeta().getType();
            if (!"syncml:filtertype-cgi".equalsIgnoreCase(type)) {
                throw new ProtocolException("Unsupported grammar '" + type + "' in filter");
            }
            String query = record.getData().getData();

            try {
                recordClause = CGIHelperClause.cgiStringToClause(query);
            } catch (ParseException ex) {
                throw new ProtocolException("Invalid cgi query string", ex);
            }

        }

        if (recordClause == null) {
            recordClause = new AllClause();
        }

        //
        // Create the clause for the fields filter
        //
        Item field  = filter.getField();
        Clause fieldsClauses = null;
        if (field != null) {
            List properties = field.getData().getProperties();
            if (properties != null && properties.size() != 0) {
                if (properties.size() == 1) {
                    FieldClause fieldClause = new FieldClause();
                    fieldClause.setProperty( (Property)properties.get(0));
                    fieldsClauses = fieldClause;
                } else {
                    int numFieldClause = properties.size();
                    FieldClause[] tmp = new FieldClause[numFieldClause];
                    for (int i=0; i<numFieldClause; i++) {
                        tmp[i] = new FieldClause((Property)properties.get(i));
                    }
                    fieldsClauses = new LogicalClause(LogicalClause.OPT_AND,
                                                      tmp);
                }
            }
        }
        if (fieldsClauses == null) {
            fieldsClauses = new AllClause();
        }

        LogicalClause filterLogicalClause;
        filterLogicalClause = new LogicalClause(LogicalClause.OPT_AND,
                                                new Clause[] {
                                                    fieldsClauses,
                                                    recordClause
                                                });

        filterClause.setClause(filterLogicalClause);
        return filterClause;
    }

    /**
     * Generate the next nonce for MD5 authentication
     * The nextNonce for the session has a number format;
     * the nextNonce for the chal element is encoding b64
     *
     * @return NextNonce a new NextNonce object
     */
    public static NextNonce generateNextNonce() {
       return new NextNonce(MD5.getNextNonce());
    }

    /**
     * Returns the header status code of the given message, if specified.<br<
     * The header status code is the first status in the message body. The
     * first message of PCK1 has not any header status code. In this case -1 is
     * returned.
     *
     * @param msg the SyncML message object
     *
     * @return the header status code or -1 if the given message does not
     *         containno any header status command.
     */
    public static int getHeaderStatusCode(SyncML msg) {
        ArrayList cmdList = msg.getSyncBody().getCommands();

        cmdList = filterCommands(
                      (AbstractCommand[])cmdList.toArray(new AbstractCommand[cmdList.size()]),
                      Status.class,
                      new CmdID("1")
                  );

        if (cmdList.size() == 0) {
            return -1;
        }

        return ((Status)cmdList.get(0)).getStatusCode();
    }
}
