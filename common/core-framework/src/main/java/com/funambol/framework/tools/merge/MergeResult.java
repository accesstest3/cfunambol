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
package com.funambol.framework.tools.merge;

import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * @version $Id: MergeResult.java,v 1.3 2007-11-29 15:16:00 nichele Exp $
 */
public class MergeResult {

    private Map<String, String> propertiesA;
    private Map<String, String> propertiesB;

    private boolean setA = false;
    private boolean setB = false;

    private String prefix = null;

    /**
     * Initializes the internal maps.
     */
    private void init() {
        propertiesA = new LinkedHashMap<String, String>();
        propertiesB = new LinkedHashMap<String, String>();
    }

    public MergeResult() {
        init();
    }

    public MergeResult(String prefix) {
        init();

        this.prefix = prefix;
    }

    public MergeResult(boolean setA, boolean setB) {
        init();

        this.setA = setA;
        this.setB = setB;
    }

    public boolean isSetARequired() {
        return (propertiesA.size() > 0) || setA;
    }

    public void addPropertyA(String prop) {
        propertiesA.put(prop, null);
    }

    public void addPropertyA(String prop, String text) {
        propertiesA.put(prop, text);
    }

    public void addPropertyB(String prop) {
        propertiesB.put(prop, null);
    }

    public void addPropertyB(String prop, String text) {
        propertiesB.put(prop, text);
    }

    public boolean isSetBRequired() {
        return (propertiesB.size() > 0) || setB;
    }

    public void addMergeResult(MergeResult otherResult) {
        addMergeResult(otherResult, otherResult.prefix);
    }

    public void addMergeResult(MergeResult otherResult, String newPrefix) {

        if (otherResult.prefix != null) {
            newPrefix = otherResult.prefix;
        }

        if (otherResult.setA) {
            setA = true;
        }
        if (otherResult.setB) {
            setB = true;
        }

        Iterator<String> itA     = otherResult.propertiesA.keySet().iterator();
        StringBuffer sbA = null;
        String key       = null;
        while (itA.hasNext()) {
            key = itA.next();
            sbA = new StringBuffer();
            sbA.append(newPrefix).append('/').append(key);
            addPropertyA(sbA.toString(), otherResult.propertiesA.get(key));
        }

        Iterator itB     = otherResult.propertiesB.keySet().iterator();
        StringBuffer sbB = null;

        while (itB.hasNext()) {
            key = (String)itB.next();
            sbB = new StringBuffer();
            sbB.append(newPrefix).append('/').append(key);
            addPropertyB(sbB.toString(), otherResult.propertiesB.get(key));
        }
    }

    public Map getPropertiesA() {
        return propertiesA;
    }
    
    public Map getPropertiesB() {
        return propertiesB;
    }
    
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<SetA: ").append(setA).append(", SetB: ").append(setB);
        sb.append(", [ListA: ");

        Iterator<String> itA = propertiesA.keySet().iterator();
        int cont     = 0;
        String key   = null;
        String value = null;

        while (itA.hasNext()) {
            key = itA.next();
            value = propertiesA.get(key);
            if (cont != 0) {
                sb.append(", ");
            }
            sb.append(prefix).append('/').append(key);
            sb.append('(').append(value).append(')');
            cont++;
        }
        sb.append(']');

        sb.append(", [ListB: ");

        Iterator<String> itB = propertiesB.keySet().iterator();
        cont = 0;
        while (itB.hasNext()) {
            key = itB.next();
            value = propertiesB.get(key);

            if (cont != 0) {
                sb.append(", ");
            }
            sb.append(prefix).append('/').append(key);
            sb.append('(').append(value).append(')');
            cont++;
        }
        sb.append("]>");

        return sb.toString();
    }
    
    
    
}
