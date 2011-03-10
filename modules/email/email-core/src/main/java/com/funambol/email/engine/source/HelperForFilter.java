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
package com.funambol.email.engine.source;

import com.funambol.email.exception.EntityException;
import com.funambol.email.model.EmailFilter;
import java.util.ArrayList;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.core.*;
import com.funambol.framework.engine.source.*;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.FieldClause;
import com.funambol.framework.filter.FilterClause;
import com.funambol.framework.filter.IDClause;
import com.funambol.framework.filter.LogicalClause;
import com.funambol.framework.filter.WhereClause;


/**
 * This class includes the methods to set the Filter Object
 *
 * @version $Id: HelperForFilter.java,v 1.2 2008-04-07 14:53:05 gbmiglia Exp $
 *
 */
public class HelperForFilter {

    // --------------------------------------------------------------- Constants

    /**     */
    protected static FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // ---------------------------------------------------------- Public Methods


    public static EmailFilter setFilter(SyncContext context,
                                        int inboxNumber,
                                        int sentNumber,
                                        boolean inboxActivation,
                                        boolean outboxActivation,
                                        boolean sentActivation,
                                        boolean draftActivation,
                                        boolean trashActivation)
    throws EntityException {

        EmailFilter filter = new EmailFilter();

        try {
            // id, time , size filter
            FilterClause filterClause = context.getFilterClause();
            if (filterClause != null) {
                if (log.isTraceEnabled()) {
                    log.trace("GET Filter clause!!");
                }
                // first clause is Logical
                LogicalClause logicalClause = filterClause.getClause();
                // get filter type
                boolean isexc = filterClause.isExclusive();
                filter = setFilter(filter, logicalClause, isexc);
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("NO Filter clause!!");
                }
            }

            // folder filter
            setFilterFolder(filter, inboxActivation, outboxActivation,
                            sentActivation, draftActivation, trashActivation);

            // max imap sent/trash/drafts emails
            filter.setMaxSentNum(sentNumber);

            // Max Number of the email in the inbox
            filter.setMaxInboxNum(inboxNumber);

            // set shortcut for Sent and Outbox folder
            filter.setIsOutboxActive(outboxActivation);
            filter.setIsSentActive(sentActivation);
                        
        } catch(Exception e) {
            throw new EntityException ("Error setting the all filters");
        }

        return filter;

    }

    // --------------------------------------------------------- Private Methods

    /**
     * set the filter
     * <p/>
     * todo should be a recursive algorithm.
     * At the moment I do a fix parser (only 2 level)
     *
     * @param logicalClause LogicalClause
     */
    private static EmailFilter setFilter(EmailFilter filter,
                                         LogicalClause logicalClause,
                                         boolean isexc)
      throws EntityException {

        try {
            Clause[] operands = logicalClause.getOperands();

            // set the filter type
            if (isexc){
                filter.setFilterType(Def.FILTER_TYPE_EXC);
            } else {
                filter.setFilterType(Def.FILTER_TYPE_INC);
            }

            //
            for (int i = 0; i < operands.length; i++) {
                if (operands[i] instanceof IDClause) {
                    IDClause tmp = (IDClause) operands[i];
                    setFilterID(filter, tmp);
                } else if (operands[i] instanceof WhereClause) {
                    WhereClause tmp = (WhereClause) operands[i];
                    setFilterTime(filter, tmp);
                } else if (operands[i] instanceof FieldClause) {
                    FieldClause tmp = (FieldClause) operands[i];
                    setFilterSize(filter, tmp);
                } else if (operands[i] instanceof LogicalClause) {
                    LogicalClause tmp = (LogicalClause) operands[i];
                    setFilter(filter, tmp, isexc);
                }
            }
        } catch(Exception e) {
            throw new EntityException ("Error setting the main filters");
        }

        return filter;
    }

    /**
     * set the time filter
     *
     * @param clause WhereClause
     */
    private static void setFilterTime(EmailFilter filter, WhereClause clause)
     throws EntityException {

        try {
            String property = clause.getProperty();
            String operator = clause.getOperator();
            if (property.equalsIgnoreCase(Def.FILTER_TIME_FIELD)) {
                String[] values = clause.getValues();
                String value = values[0];
                if (value != null) {
                    if (value.equalsIgnoreCase("ALL")) {
                        filter.setTime(null);
                    } else {
                        filter.setTime(Utility.UtcToDate(value));
                        filter.setTimeClause(operator);
                    }
                }
            }
        } catch(Exception e) {
            throw new EntityException ("Error setting the Time filter");
        }

    }

    /**
     * set the time filter
     *
     * @param icIdFilter IDClause
     */
    private static void setFilterID(EmailFilter filter, IDClause icIdFilter)
     throws EntityException {
        try {
            String property = icIdFilter.getProperty();
            String operator = icIdFilter.getOperator();
            String[] values = icIdFilter.getValues();
            String value = values[0];
            filter.setId(value);
        } catch(Exception e) {
            throw new EntityException ("Error setting the ID filter");
        }

    }

    /**
     * set the time filter
     */
    private static void setFilterFolder(EmailFilter filter,
                                        boolean inboxActivation,
                                        boolean outboxActivation,
                                        boolean sentActivation,
                                        boolean draftActivation,
                                        boolean trashActivation)
       throws EntityException {
        try {
            int f = 0;
            if (inboxActivation) {
                f = f | Def.FILTER_FOLDER_I;
            }
            if (outboxActivation) {
                f = f | Def.FILTER_FOLDER_IO;
            }
            if (sentActivation) {
                f = f | Def.FILTER_FOLDER_IOS;
            }
            if (draftActivation) {
                f = f | Def.FILTER_FOLDER_IOSD;
            }
            if (trashActivation) {
                f = f | Def.FILTER_FOLDER_IOSDT;
            }
            if (f == 0) {
                filter.setFolder(Def.FILTER_FOLDER_IO);
            } else {
                filter.setFolder(f);
            }
        } catch(Exception e) {
            throw new EntityException ("Error setting the folder filter");
        }

    }

    /**
     *
     * set the time filter
     *
     * @param fcSizeFilter FieldClause
     */
    private static void setFilterSize(EmailFilter filter,
                                      FieldClause fcSizeFilter)
     throws EntityException {

        try {
            Property property = fcSizeFilter.getProperty();
            String moduleName;
            ArrayList types;
            int maxSize;

            moduleName = property.getPropName();
            types = property.getPropParams();
            maxSize = property.getMaxSize();

            String typeName;
            int tempSizeFilter = 0;

            boolean isAttachFieldSet = false;

            if (moduleName.equalsIgnoreCase(Def.FILTER_SIZE_LABEL_HEADER)) {
                if (types.size() == 0) {
                    tempSizeFilter = tempSizeFilter | Def.FILTER_SIZE_H;
                } else {
                    for (int i = 0; i < types.size(); i++) {
                        typeName = ((PropParam) types.get(i)).getParamName();
                        if (!isAttachFieldSet) {
                            if (typeName.equalsIgnoreCase(Def.FILTER_SIZE_LABEL_BODY)) {
                                tempSizeFilter = tempSizeFilter | Def.FILTER_SIZE_H_B;
                                if (maxSize > 0) {
                                    tempSizeFilter = tempSizeFilter | Def.FILTER_SIZE_H_B_PERC;
                                    filter.setNumBytes(maxSize);
                                }
                            }
                        }
                        if (typeName.equalsIgnoreCase(Def.FILTER_SIZE_LABEL_ATTACH)) {
                            tempSizeFilter = tempSizeFilter | Def.FILTER_SIZE_H_B_A;
                            if (maxSize > 0) {
                                tempSizeFilter = tempSizeFilter | Def.FILTER_SIZE_H_B_A_PERC;
                                filter.setNumBytes(maxSize);
                            }
                            isAttachFieldSet = true;
                        }
                    }
                }
            }

            if (tempSizeFilter == 0) {
                filter.setSize(1);
            } else {
                filter.setSize(tempSizeFilter);
            }
        } catch(Exception e) {
            throw new EntityException ("Error setting the size filter");
        }

   }

}

