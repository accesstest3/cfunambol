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

import org.apache.commons.lang.builder.ToStringBuilder;
import java.io.Serializable;

/**
 * This class represents selection clause of a filter.
 *
 *
 * @version $Id: WhereClause.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 *
 */
public class WhereClause
implements Clause, Serializable {

    // -------------------------------------------------------- Public constants
	public static final String OPT_START_WITH = "START_WITH";
	public static final String OPT_END_WITH   = "END_WITH"  ;
	public static final String OPT_CONTAINS   = "CONTAINS"  ;
	public static final String OPT_EQ         = "EQ"        ;
	public static final String OPT_GT         = "GT"        ;
	public static final String OPT_LT 		  = "LT"        ;
	public static final String OPT_BETWEEN    = "BETWEEN"   ;
	public static final String OPT_GE         = "GE"        ;
	public static final String OPT_LE         = "LE"        ;

    // ------------------------------------------------------------ Private data

    private String   property;
    private String[] values;
    private String   operator;
    private boolean  caseSensitive;

    /**
     * The default constructor is not intended to be used.
     */
    public WhereClause() {
        this(null, null, null, false);
    }

    public WhereClause(String property, String[] values, String operator, boolean caseSensitive) {
        this.property      = property;
        this.values        = values;
        this.operator      = operator;
        this.caseSensitive = caseSensitive;
    }

    /** Getter for property property.
     * @return Value of property property.
     *
     */
    public String getProperty() {
        return property;
    }

    /** Setter for property property.
     * @param property New value of property property.
     *
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Getter for property values.
     * @return Value of property values.
     */
    public String[] getValues() {
        return values;
    }

    /**
     * Setter for property value.
     * @param value New value of property value.
     */
    public void setValues(String[] values) {
        this.values = values;
    }

    /** Getter for property operator.
     * @return Value of property operator.
     *
     */
    public String getOperator() {
        return operator;
    }

    /** Setter for property operator.
     * @param operator New value of property operator.
     *
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /** Getter for property caseSensitive.
     * @return Value of property caseSensitive.
     *
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /** Setter for property caseSensitive.
     * @param caseSensitive New value of property caseSensitive.
     *
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }


    // ---------------------------------------------------------- Public Methods

    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("operator:",  operator);
        sb.append("property:",  property);
        for (int i=0; i<values.length; i++) {
            sb.append("value:",  values[i]);
        }
        sb.append("caseSensitive:",  caseSensitive);

        return sb.toString();
    }
}
