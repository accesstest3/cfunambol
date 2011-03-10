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

package com.funambol.framework.filter;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang.builder.ToStringBuilder;
import com.funambol.framework.server.store.PreparedWhere;

/**
 * This class recognize the clause of type logical for the filter.
 * For logical clause mean AND, OR, NOT clause.
 *
 *
 *
 * @version $Id: LogicalClause.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 *
 */
public class LogicalClause implements Serializable, Clause {

    // --------------------------------------------------------------- Constants

    public static final String OPT_AND = "AND";
    public static final String OPT_OR  = "OR" ;
    public static final String OPT_NOT = "NOT";

    // ------------------------------------------------------------ Private data

    private String   operator;
    private Clause[] operands;

    // ------------------------------------------------------------ constructors

    /**
     * The default constructor is disabled. We do not want it to be used.
     */
    public LogicalClause() {}

    /**
     * Create a new LogicalClause object with the given operator and operands.
     * Operator must be one of
     * <ul>
     *   <li> AND
     *   <li> OR
     *   <li> NOT
     * </ul>
     *
     * operands must be of size 1 in the case operator is NOT and of size 2 in
     * the case operator is AND or OR.
     *
     * @param operator the operator
     * @param operands the operands - NOT NULL
     *
     * @throws IllegalArgumentException if the given values are not correct.
     * @throws IllegalArgumentException if any required parameter is null.
     */
    public LogicalClause(String operator, Clause[] operands) {
        if (!OPT_AND.equalsIgnoreCase(operator) &&
            !OPT_OR.equalsIgnoreCase(operator) &&
            !OPT_NOT.equalsIgnoreCase(operator)) {
            throw new IllegalArgumentException(
                "operator "                           +
                operator                              +
                " not supported; it must be one of (" +
                OPT_AND                               +
                ","                                   +
                OPT_OR                                +
                ","                                   +
                OPT_NOT                               +
                ")"
            );
        }
        if (operands == null) {
            throw new NullPointerException("operands is null!");
        }

        if (OPT_NOT.equalsIgnoreCase(operator)) {
            if (operands.length != 1) {
                throw new IllegalArgumentException(
                    "one and only one operand is required with " + OPT_NOT
                );
            }
        } else {
            if (operands.length < 2) {
                throw new IllegalArgumentException(
                    "two or more operands are required with " + OPT_AND + " or " + OPT_OR
                );
            }
        }

        this.operator = operator;
        this.operands = operands;
    }

    /** Getter for property operator.
     * @return Value of property operator.
     *
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Setter for property operator.
     * @param operator New value of property operator.
     */
    public void setOperator(java.lang.String operator) {
        this.operator = operator;
    }

    /** Getter for property operands.
     * @return Value of property operands.
     *
     */
    public Clause[] getOperands() {
        return operands;
    }

    /**
     * Setter for property operands.
     * @param operands New value of property operands.
     */
    public void setOperands(com.funambol.framework.filter.Clause[] operands) {
        this.operands = operands;
    }

    // ---------------------------------------------------------- Public Methods

    public boolean isUnaryOperator() {
        return OPT_NOT.equalsIgnoreCase(operator);
    }

    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("operator:",  operator);
        sb.append("operands:",  operands);

        return sb.toString();
    }

}
