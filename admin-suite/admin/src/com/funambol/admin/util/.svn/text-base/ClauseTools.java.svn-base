/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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

package com.funambol.admin.util;

import com.funambol.framework.filter.*;

/**
 * Support class to create the correct clause from the values inserted in a
 * search form.
 *
 * @version $Id: ClauseTools.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class ClauseTools {

    // ------------------------------------------------------------- Constants

    /** All searchs are case insensitive */
    private static final boolean CASE_SENSITIVE = false;

    private static final String LABEL_OPERATOR_START_WITH = "Start with";
    private static final String LABEL_OPERATOR_END_WITH = "End with";
    private static final String LABEL_OPERATOR_CONTAINS = "Contain";
    private static final String LABEL_OPERATOR_EXACTLY = "Exactly";

    /**
     * Returns a correct clause in agreement with the given parameters.
     *
     * @param params list of parameters
     * @param operators list of operators
     * @param values list of values
     * @return correct clause in agreement with the given parameters
     */
    public static Clause createClause(String[] params, String[] operators, String[] values) {

        int size = values.length;

        if (size == 0) {
            return new AllClause();
        }

        if (size == 1) {
            return new WhereClause(params[0], new String[] {values[0]}
                                   , operators[0], CASE_SENSITIVE);
        }

        WhereClause[] clauses = new WhereClause[params.length];

        for (int i = 0; i < size; ++i) {

            clauses[i] = new WhereClause(params[i], new String[] {values[i]}
                                         , operators[i], CASE_SENSITIVE);
        }

        return new LogicalClause(LogicalClause.OPT_AND, clauses);
    }

    /**
     * Change value of the string received into a correct value for the query
     * @param operatorView parameter to change
     * @return correct value of thew operator for the query
     */
    public static String operatorViewToOperatorValue(String operatorView) {
        String operatorValue = null;

        if (operatorView.equals(LABEL_OPERATOR_START_WITH)) {
            operatorValue = WhereClause.OPT_START_WITH;
        }
        if (operatorView.equals(LABEL_OPERATOR_END_WITH)) {
            operatorValue = WhereClause.OPT_END_WITH;
        }
        if (operatorView.equals(LABEL_OPERATOR_CONTAINS)) {
            operatorValue = WhereClause.OPT_CONTAINS;
        }
        if (operatorView.equals(LABEL_OPERATOR_EXACTLY)) {
            operatorValue = WhereClause.OPT_EQ;
        }
        return operatorValue;
    }

    /**
     * Returns the available operators
     * @return the available operators
     */
    public static String[] getAvailableOperators() {
        return new String[] {
            LABEL_OPERATOR_START_WITH, LABEL_OPERATOR_END_WITH, LABEL_OPERATOR_CONTAINS,
            LABEL_OPERATOR_EXACTLY
        };
    }

}
