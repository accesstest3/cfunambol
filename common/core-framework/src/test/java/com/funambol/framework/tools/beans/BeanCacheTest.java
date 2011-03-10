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

import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.beans.BeanCache.ServerBeanEntry;
import java.io.File;
import java.util.Map;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 * BeanCache's test cases
 * @version $Id: BeanCacheTest.java 34632 2010-05-27 13:11:25Z dimuro $
 */
public class BeanCacheTest extends TestCase {
    
    public BeanCacheTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SimpleNotCloneableBean.resetNumOfInstances();
        SimpleCloneableBean.resetNumOfInstances();
    }


    // -------------------------------------------------------------- Test cases

    /**
     * Test of getBeanInstance method, of class BeanCache.
     */
    public void testGetBeanInstance() throws Exception {

        BeanCache cache = new BeanCache("src/test/resources/data/com/funambol/framework/tools/BeanCache");
        SimpleCloneableBean o1 = (SimpleCloneableBean)cache.getBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o2 = (SimpleCloneableBean)cache.getBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o3 = (SimpleCloneableBean)cache.getBeanInstance("test/SimpleCloneableBean.xml");

        assertTrue("Instances must be the same", o1 == o2);
        assertTrue("Instances must be the same", o2 == o3);

        File f = new File("src/test/resources/data/com/funambol/framework/tools/BeanCache/test/SimpleCloneableBean.xml");

        // we need to sleep a bit in order to be sure the timestamp is different
        // from the one detected previously from BeanCache since some filesystems round
        // the file last update time
        Thread.sleep(2000);
        
        f.setLastModified(System.currentTimeMillis());
        SimpleCloneableBean o4 = (SimpleCloneableBean)cache.getBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o5 = (SimpleCloneableBean)cache.getBeanInstance("test/SimpleCloneableBean.xml");

        assertTrue("Instances must be different", o1 != o4);
        assertTrue("Instances must be the same", o4 == o5);

        assertEquals("Instances must be equal", o1, o2);
        assertEquals("Instances must be equal", o2, o3);
        assertEquals("Instances must be equal", o3, o4);
        assertEquals("Instances must be equal", o4, o5);

        assertEquals("Wrong number of instances", 2, SimpleCloneableBean.getNumOfInstances());
        assertEquals("Wrong number of clones", 0, o1.getNumOfClones());

        Object o10 = cache.getBeanInstance("test/SimpleNotCloneableBean.xml");
        Object o11 = cache.getBeanInstance("test/SimpleNotCloneableBean.xml");
        Object o12 = cache.getBeanInstance("test/SimpleNotCloneableBean.xml");

        assertTrue("Instances must be the same", o10 == o11);
        assertTrue("Instances must be the same", o11 == o12);

        assertEquals("", 1, SimpleNotCloneableBean.getNumOfInstances());
    }

    /**
     * Test of getBeanInstance method, of class BeanCache.
     */
    public void testGetNewBeanInstance() throws Exception {
        SimpleCloneableBean.resetNumOfInstances();

        BeanCache cache = new BeanCache("src/test/resources/data/com/funambol/framework/tools/BeanCache");
        SimpleCloneableBean o1 = (SimpleCloneableBean)cache.getNewBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o2 = (SimpleCloneableBean)cache.getNewBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o3 = (SimpleCloneableBean)cache.getNewBeanInstance("test/SimpleCloneableBean.xml");

        assertTrue("Instances must be different", o1 != o2);
        assertTrue("Instances must be different", o2 != o3);

        assertEquals("Wrong number of instances", 3, SimpleCloneableBean.getNumOfInstances());
        assertEquals("Wrong number of clones", 2, o1.getNumOfClones());

        File f = new File("src/test/resources/data/com/funambol/framework/tools/BeanCache/test/SimpleCloneableBean.xml");
        
        // we need to sleep a bit in order to be sure the timestamp is different 
        // from the one detected previously from BeanCache since some filesystems round
        // the file last update time
        Thread.sleep(2000);

        f.setLastModified(System.currentTimeMillis());
        SimpleCloneableBean o4 = (SimpleCloneableBean)cache.getNewBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o5 = (SimpleCloneableBean)cache.getNewBeanInstance("test/SimpleCloneableBean.xml");

        assertTrue("Instances must be different", o1 != o4);
        assertTrue("Instances must be different", o4 != o5);

        assertEquals("Instances must be equal", o1, o2);
        assertEquals("Instances must be equal", o2, o3);
        assertEquals("Instances must be equal", o3, o4);
        assertEquals("Instances must be equal", o4, o5);

        assertEquals("Wrong number of instances", 5, SimpleCloneableBean.getNumOfInstances());
        assertEquals("Wrong number of clones", 1, o4.getNumOfClones());

        SimpleNotCloneableBean.resetNumOfInstances();
        
        Object o10 = cache.getNewBeanInstance("test/SimpleNotCloneableBean.xml");
        Object o11 = cache.getNewBeanInstance("test/SimpleNotCloneableBean.xml");
        Object o12 = cache.getNewBeanInstance("test/SimpleNotCloneableBean.xml");

        assertTrue("Instances must be different", o10 != o11);
        assertTrue("Instances must be different", o11 != o12);

        assertEquals("Wrong number of instances", 3, SimpleNotCloneableBean.getNumOfInstances());
    }
    
    /**
     * Test of invalidate method, of class BeanCache.
     */
    public void testInvalidate_0args() throws Exception {
        BeanCache beanCache = new BeanCache("src/test/resources/data/com/funambol/framework/tools/BeanCache");
        SimpleCloneableBean o1 = (SimpleCloneableBean)beanCache.getNewBeanInstance("test/SimpleCloneableBean.xml");
        SimpleNotCloneableBean o2 = (SimpleNotCloneableBean)beanCache.getNewBeanInstance("test/SimpleNotCloneableBean.xml");

        beanCache.invalidate();

        Map cache = (Map)PrivateAccessor.getField(beanCache, "cache");
        assertEquals("Wrong cache size", 0, cache.size());
    }

    /**
     * Test of invalidate method, of class BeanCache.
     */
    public void testInvalidate_String() throws Exception {
        BeanCache beanCache = new BeanCache("src/test/resources/data/com/funambol/framework/tools/BeanCache");
        SimpleCloneableBean o1 = (SimpleCloneableBean)beanCache.getNewBeanInstance("test/SimpleCloneableBean.xml");
        SimpleNotCloneableBean o2 = (SimpleNotCloneableBean)beanCache.getNewBeanInstance("test/SimpleNotCloneableBean.xml");

        beanCache.invalidate("test/NotExistingBean.xml");
        Map cache0 = (Map)PrivateAccessor.getField(beanCache, "cache");
        assertEquals("Wrong cache size", 2, cache0.size());

        beanCache.invalidate("test/SimpleCloneableBean.xml");
        Map cache1 = (Map)PrivateAccessor.getField(beanCache, "cache");
        assertEquals("Wrong cache size", 1, cache1.size());

        beanCache.invalidate("test/SimpleNotCloneableBean.xml");
        Map cache2 = (Map)PrivateAccessor.getField(beanCache, "cache");
        assertEquals("Wrong cache size", 0, cache2.size());
    }
    
    /**
     * Test of getNoInitNewBeanInstance method, of class BeanCache.
     */
    public void testGetNoInitNewBeanInstance() throws Exception {
        BeanCache beanCache = new BeanCache("src/test/resources/data/com/funambol/framework/tools/BeanCache");

        String bean = "test/SimpleLazyInitBean.xml";
        Object o = beanCache.getNoInitNewBeanInstance(bean);
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);

        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;
        assertFalse("The bean should not be initialized", lazyBean.isInitialized());

        bean = "test/SimpleLazyInitCloneableBean.xml";
        o = beanCache.getNoInitNewBeanInstance(bean);
        assertTrue("Wrong object class", o instanceof SimpleLazyInitCloneableBean);

        SimpleLazyInitCloneableBean lazyCloneableBean = (SimpleLazyInitCloneableBean)o;
        assertFalse("The bean should not be initialized", lazyCloneableBean.isInitialized());

        Map<String, BeanCache.ServerBeanEntry> cache = 
                (Map<String, ServerBeanEntry>) PrivateAccessor.getField(beanCache, "cache");

        assertEquals("Wrong number of bean in cache", 2, cache.size());
        ServerBeanEntry entry1 = cache.get("test/SimpleLazyInitBean.xml");
        assertNotNull("entry must be not null", entry1);
        assertNotNull("entry.ts must be not null", entry1.ts);
        assertNotNull("entry.instace must be not null", entry1.instance);
        assertNotNull("entry.config must be not null", entry1.config);
        assertEquals(
            "Wrong config",
            IOTools.readFileString("src/test/resources/data/com/funambol/framework/tools/BeanCache/test/SimpleLazyInitBean.xml"),
            entry1.config);
        assertTrue("entry.instance must be initialized", ((SimpleLazyInitBean)entry1.instance).initialized);

        ServerBeanEntry entry2 = cache.get("test/SimpleLazyInitCloneableBean.xml");
        assertNotNull("entry must be not null", entry2);
        assertNotNull("entry.ts must be not null", entry2.ts);
        assertNotNull("entry.instace must be not null", entry2.instance);
        assertNotNull("entry.config must be not null", entry2.config);
        assertEquals(
            "Wrong config",
            IOTools.readFileString("src/test/resources/data/com/funambol/framework/tools/BeanCache/test/SimpleLazyInitCloneableBean.xml"),
            entry2.config);
        assertTrue("entry.instance must be initialized", ((SimpleLazyInitCloneableBean)entry2.instance).initialized);
    }
}
