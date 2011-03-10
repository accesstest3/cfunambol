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

package com.funambol.framework.server.store;

import java.io.Serializable;
import java.util.ArrayList;

import com.funambol.framework.filter.*;

/**
 * This class contains the methods to convert a Clause into a PreparedWhere.
 *
 * @version $Id: SQLHelperClause.java,v 1.2 2007-11-28 11:15:59 nichele Exp $
 *
 */
public class SQLHelperClause  {

    // --------------------------------------------------------------- Constants
    private static final String OPT_UPPER      = "upper("    ;
    private static final String OPT_CLOSE     = ")"                 ;
    private static final String OPT_UPPER_DB2 = "UPPER(CAST("       ;
    private static final String OPT_CLOSE_DB2 = " AS VARCHAR(255)))";
    private static final String PRODUCT_DB2   = "DB2"               ;

    // ------------------------------------------------------------ Private data
    final private String defaultUprOpen;
    final private String defaultUprClose;

    // ------------------------------------------------------------ Constructors
    public SQLHelperClause(String dbProductName) {

        if (dbProductName != null &&
            dbProductName.toUpperCase().indexOf(PRODUCT_DB2) != -1) {

            defaultUprOpen  = OPT_UPPER_DB2;
            defaultUprClose = OPT_CLOSE_DB2;
        } else {
            defaultUprOpen  = OPT_UPPER;
            defaultUprClose = OPT_CLOSE;
        }
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Creates a PreparedWhere from the given Clause
     *
     * @param clause the clause
     * @return a PreparedWhered related to the given Clause
     */
    public PreparedWhere getPreparedWhere(Clause clause) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ClassLoader clauseLoader = clause.getClass().getClassLoader();

        if (clause instanceof com.funambol.framework.filter.LogicalClause) {
            return getPreparedWhere( (LogicalClause)clause);
        } else if (clause instanceof com.funambol.framework.filter.LogicalClause) {
            return getPreparedWhere( (LogicalClause)clause);
        } else if (clause instanceof com.funambol.framework.filter.WhereClause) {
            return getPreparedWhere( (WhereClause)clause);
        } else if (clause instanceof com.funambol.framework.filter.AllClause) {
            return getPreparedWhere( (AllClause)clause);
        } else if (clause instanceof com.funambol.framework.filter.FieldClause) {
            //
            // The FieldClause aren't converted in PreparedWhere
            //
            return getEmptyPreparedWhere();
        }

        if (clause == null) {
            throw new IllegalStateException("Clause is null");
        }

        throw new IllegalStateException("Unknown clause: '" + clause.getClass().getName() + "'");
    }

    /**
     * Creates a PreparedWhere from the given LogicalClause
     *
     * @param clause the logical clause
     * @return a PreparedWhered related to the given LogicalClause
     */
    public PreparedWhere getPreparedWhere(LogicalClause clause) {

        StringBuffer sql = new StringBuffer();
        ArrayList parameters = new ArrayList();
        PreparedWhere pw = null;

        if (clause.isUnaryOperator()) {
            pw = getPreparedWhere(clause.getOperands()[0]);
            sql.append(clause.getOperator().toLowerCase()).append(pw.sql);
            for(int i=0; i<pw.parameters.length; ++i) {
                parameters.add(pw.parameters[i]);
            }
        } else {
            Clause[] operands = clause.getOperands();
            for (int i=0; i<operands.length; ++i) {
                pw = getPreparedWhere(operands[i]);

                if (i>0) {
                    sql.append(' ').append(clause.getOperator().toLowerCase()).append(' ');
                }
                sql.append(pw.sql);

                for(int j=0; j<pw.parameters.length; ++j) {
                    parameters.add(pw.parameters[j]);
                }
            }
        }

        PreparedWhere ret = new PreparedWhere();

        ret.sql = '(' + sql.toString() + ')';
        ret.parameters = parameters.toArray();

        return ret;

    }

    /**
     * Creates a PreparedWhere from the given WhereClause
     *
     * @param clause the where clause
     * @return a PreparedWhered related to the given WhereClause
     */
    public PreparedWhere getPreparedWhere(WhereClause clause) {
        String property = clause.getProperty();
        String operator = clause.getOperator();
        String[] values = clause.getValues();
        boolean caseSensitive = clause.isCaseSensitive();

        StringBuffer query = new StringBuffer();

        assert (values != null);

        PreparedWhere where = new PreparedWhere();
        where.parameters = new Object[values.length];

        String uprOpen     = null;
        String uprClose    = null;
        String uprProperty = null;

        if (!caseSensitive) {
            uprOpen     = this.defaultUprOpen;
            uprClose    = this.defaultUprClose;            
            uprProperty = " UPPER("+property+")";
        } else {
            uprOpen     = ""      ;
            uprClose    = ""      ;
            uprProperty = property;
        }

        if (WhereClause.OPT_START_WITH.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(" LIKE ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = values[0] + '%';
        } else if (WhereClause.OPT_END_WITH.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(" LIKE ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = '%' + values[0];
        } else if (WhereClause.OPT_CONTAINS.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(" LIKE ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = '%' + values[0] + '%';
        } else if (WhereClause.OPT_EQ.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(" = ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = values[0];
        } else if (WhereClause.OPT_GT.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(" > ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = values[0];
        } else if (WhereClause.OPT_LT.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(" < ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = values[0];
        } else if (WhereClause.OPT_BETWEEN.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(' ')
                .append(WhereClause.OPT_BETWEEN).append(' ').append(uprOpen)
                .append('?').append(uprClose)
                .append(" AND ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = values[0];
            where.parameters[1] = values[1];
        } else if (WhereClause.OPT_GE.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(" >= ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = values[0];
        } else if (WhereClause.OPT_LE.equalsIgnoreCase(operator)) {
            query.append(uprProperty).append(" <= ").append(uprOpen).append('?').append(uprClose);
            where.sql = query.toString();
            where.parameters[0] = values[0];
        }

        where.sql = '(' + where.sql + ')';
        return where;
    }


    /**
     * Creates a PreparedWhere from the given AllClause
     *
     * @param clause the where clause
     * @return a PreparedWhered related to the given AllClause
     */
    public PreparedWhere getPreparedWhere(AllClause clause) {
        PreparedWhere ret = new PreparedWhere();

        ret.sql = "";
        ret.parameters = new Object[0];

        return ret;
    }

    /**
     * Creates a empty PreparedWhere
     *
     * @return a empty PreparedWhere
     */
    public PreparedWhere getEmptyPreparedWhere() {
        PreparedWhere ret = new PreparedWhere();

        ret.sql = "";
        ret.parameters = new Object[0];

        return ret;
    }

}