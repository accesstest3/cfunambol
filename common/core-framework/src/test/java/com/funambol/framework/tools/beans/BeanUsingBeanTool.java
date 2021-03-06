/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.framework.tools.beans;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 * This is a simple bean that use BeanCache to load another bean during initialization.
 * It's used to test if the beancache can be called during from a bean that is initilized
 * by the cache itself (to test locks).
 *
 * @version $Id: BeanUsingBeanTool.java 32009 2009-09-20 09:48:14Z nichele $
 */
public class BeanUsingBeanTool implements LazyInitBean {

    protected String prop1 = null;
    protected String prop2 = null;

    protected boolean initialized = false;
    
    public BeanUsingBeanTool() {
    }
    
    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

    public String getProp2() {
        return prop2;
    }

    public void setProp2(String prop2) {
        this.prop2 = prop2;
    }
    
    @Override
    public boolean equals(Object o) {
        if (! (o instanceof BeanUsingBeanTool)) {
            return false;
        }
        BeanUsingBeanTool clBean = (BeanUsingBeanTool)o;
        return (StringUtils.equals(prop1, clBean.prop1) && 
                StringUtils.equals(prop2, clBean.prop2) &&
                initialized == clBean.initialized);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        if (prop1 != null) {
            hash += prop1.hashCode();
        }
        if (prop2 != null) {
            hash += prop2.hashCode();
        }
        hash += initialized ? 0 : 1;
        return hash;
    }

    public void init() throws BeanInitializationException {
        BeanTool beanTool = BeanTool.getBeanTool("src/test/resources/data/com/funambol/framework/tools/BeanTool");
        try {
            beanTool.getBeanInstance("test/SimpleString.xml");
        } catch (BeanException ex) {
            throw new BeanInitializationException("", ex);
        }
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
