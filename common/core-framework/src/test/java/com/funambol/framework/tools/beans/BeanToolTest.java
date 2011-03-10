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

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 * BeanTool test cases
 * @version $Id: BeanToolTest.java 32009 2009-09-20 09:48:14Z nichele $
 */
public class BeanToolTest extends TestCase {

    private static final String BASE_TEST_DIR = "src/test/resources/data/com/funambol/framework/tools/BeanTool";
    public BeanToolTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SimpleNotCloneableBean.resetNumOfInstances();
        SimpleCloneableBean.resetNumOfInstances();
        PrivateAccessor.setField(BeanTool.class, "instances", new HashMap<URL, BeanTool>());
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of getBeanTool method, of class BeanTool.
     */
    public void testGetBeanTool() {
        BeanTool tool1 = BeanTool.getBeanTool(BASE_TEST_DIR + "/tool");
        BeanTool tool1_1 = BeanTool.getBeanTool(BASE_TEST_DIR + "/tool");

        BeanTool tool2 = BeanTool.getBeanTool(BASE_TEST_DIR + "/tool2");

        assertSame("The tools should be the same", tool1, tool1_1);
        assertNotSame("The tools should be different", tool1, tool2);
    }

    /**
     * Test of getNewBeanInstance method, of class BeanTool.
     */
    public void testGetNewBeanInstance() throws Exception {

        BeanTool tool = BeanTool.getBeanTool(BASE_TEST_DIR);

        SimpleCloneableBean o1 = (SimpleCloneableBean)tool.getNewBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o2 = (SimpleCloneableBean)tool.getNewBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o3 = (SimpleCloneableBean)tool.getNewBeanInstance("test/SimpleCloneableBean.xml");

        assertTrue("Instances must be different", o1 != o2);
        assertTrue("Instances must be different", o2 != o3);

        assertEquals("Wrong number of instances", 3, SimpleCloneableBean.getNumOfInstances());
        assertEquals("Wrong number of clones", 2, o1.getNumOfClones());

        File f = new File(BASE_TEST_DIR + "/test/SimpleCloneableBean.xml");

        // we need to sleep a bit in order to be sure the timestamp is different
        // from the one detected previously from BeanTool
        Thread.sleep(1000);

        f.setLastModified(System.currentTimeMillis());
        SimpleCloneableBean o4 = (SimpleCloneableBean)tool.getNewBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o5 = (SimpleCloneableBean)tool.getNewBeanInstance("test/SimpleCloneableBean.xml");

        assertTrue("Instances must be different", o1 != o4);
        assertTrue("Instances must be different", o4 != o5);

        assertEquals("Instances must be equal", o1, o2);
        assertEquals("Instances must be equal", o2, o3);
        assertEquals("Instances must be equal", o3, o4);
        assertEquals("Instances must be equal", o4, o5);

        assertEquals("Wrong number of instances", 5, SimpleCloneableBean.getNumOfInstances());
        assertEquals("Wrong number of clones", 1, o4.getNumOfClones());

        SimpleNotCloneableBean.resetNumOfInstances();

        Object o10 = tool.getNewBeanInstance("test/SimpleNotCloneableBean.xml");
        Object o11 = tool.getNewBeanInstance("test/SimpleNotCloneableBean.xml");
        Object o12 = tool.getNewBeanInstance("test/SimpleNotCloneableBean.xml");

        assertTrue("Instances must be different", o10 != o11);
        assertTrue("Instances must be different", o11 != o12);

        assertEquals("Wrong number of instances", 3, SimpleNotCloneableBean.getNumOfInstances());

        Object o = null;
        boolean found = true;
        try {
            o = tool.getNewBeanInstance("notExistingBean.xml");
        } catch (BeanNotFoundException e) {
            found = false;
        }
        assertFalse("Missing BeanNotFoundException exception", found);
    }

    /**
     * Test of getNoInitNewBeanInstance method, of class BeanTool.
     */
    public void testGetNoInitNewBeanInstance() throws Exception {
        String bean = "/test/SimpleLazyInitBean.XML";
        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);
        Object o = beanTool.getNoInitNewBeanInstance(bean);
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);

        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;

        assertFalse("The bean should be not initialized", lazyBean.isInitialized());

        boolean found = true;
        try {
            o = beanTool.getNoInitNewBeanInstance("notExistingBean.xml");
        } catch (BeanNotFoundException e) {
            found = false;
        }
        assertFalse("Missing BeanNotFoundException exception", found);
    }

    /**
     * Test of getBeanInstance method, of class BeanTool.
     */
    public void testGetNoInitNewBeanInstance_with_ClassName() throws Exception {

        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);

        Object o1 = (String)beanTool.getBeanInstance("java.lang.String");
        Object o2 = (String)beanTool.getBeanInstance("java.lang.String");
        SimpleCloneableBean cloneableBean  = (SimpleCloneableBean)beanTool.getNoInitNewBeanInstance("com.funambol.framework.tools.beans.SimpleCloneableBean");
        SimpleCloneableBean cloneableBean2 = (SimpleCloneableBean)beanTool.getNoInitNewBeanInstance("com.funambol.framework.tools.beans.SimpleCloneableBean");

        SimpleLazyInitCloneableBean lazyBean = (SimpleLazyInitCloneableBean)beanTool.getNoInitNewBeanInstance("com.funambol.framework.tools.beans.SimpleLazyInitCloneableBean");
        SimpleLazyInitCloneableBean lazyBean2 = (SimpleLazyInitCloneableBean)beanTool.getNoInitNewBeanInstance("com.funambol.framework.tools.beans.SimpleLazyInitCloneableBean");

        assertNotSame("Instances must be different", o1, o2);
        assertNotSame("Instances must be different", cloneableBean, cloneableBean2);
        assertNotSame("Instances must be different", lazyBean, lazyBean2);

        assertTrue("No clone expected", cloneableBean.getNumOfClones() == 0);
        assertTrue("No clone expected", cloneableBean2.getNumOfClones() == 0);

        assertFalse("Instance should be not initialized", lazyBean.isInitialized());
        assertFalse("Instance should be not initialized", lazyBean2.isInitialized());

        Object o = null;
        boolean found = true;
        try {
            o = beanTool.getNoInitNewBeanInstance("not.existing.class.Name");
        } catch (BeanNotFoundException e) {
            found = false;
        }
        assertFalse("Expected BeanNotFoundException exception", found);
    }

    /**
     * Test of getBeanInstance method, of class BeanTool.
     */
    public void testGetBeanInstance() throws Exception {

        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);

        SimpleCloneableBean o1 = (SimpleCloneableBean)beanTool.getBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o2 = (SimpleCloneableBean)beanTool.getBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o3 = (SimpleCloneableBean)beanTool.getBeanInstance("test/SimpleCloneableBean.xml");

        assertTrue("Instances must be the same", o1 == o2);
        assertTrue("Instances must be the same", o2 == o3);

        File f = new File(BASE_TEST_DIR + "/test/SimpleCloneableBean.xml");

        // we need to sleep a bit in order to be sure the timestamp is different
        // from the one detected previously from BeanTool
        Thread.sleep(1000);
        f.setLastModified(System.currentTimeMillis());
        
        SimpleCloneableBean o4 = (SimpleCloneableBean)beanTool.getBeanInstance("test/SimpleCloneableBean.xml");
        SimpleCloneableBean o5 = (SimpleCloneableBean)beanTool.getBeanInstance("test/SimpleCloneableBean.xml");

        assertTrue("Instances must be different", o1 != o4);
        assertTrue("Instances must be the same", o4 == o5);

        assertEquals("Instances must be equal", o1, o2);
        assertEquals("Instances must be equal", o2, o3);
        assertEquals("Instances must be equal", o3, o4);
        assertEquals("Instances must be equal", o4, o5);

        assertEquals("Wrong number of instances", 2, SimpleCloneableBean.getNumOfInstances());
        assertEquals("Wrong number of clones", 0, o1.getNumOfClones());

        Object o10 = beanTool.getBeanInstance("test/SimpleNotCloneableBean.xml");
        Object o11 = beanTool.getBeanInstance("test/SimpleNotCloneableBean.xml");
        Object o12 = beanTool.getBeanInstance("test/SimpleNotCloneableBean.xml");

        assertTrue("Instances must be the same", o10 == o11);
        assertTrue("Instances must be the same", o11 == o12);

        assertEquals("Wrong number of instances", 1, SimpleNotCloneableBean.getNumOfInstances());

        Object o = null;
        boolean found = true;
        try {
            o = beanTool.getBeanInstance("notExistingBean.xml");
        } catch (BeanNotFoundException e) {
            found = false;
        }
        assertFalse("Missing BeanNotFoundException exception", found);
    }

    /**
     * Test of getBeanInstance method, of class BeanTool.
     */
    public void testGetBeanInstance_with_ClassName() throws Exception {

        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);

        Object o1 = (String)beanTool.getBeanInstance("java.lang.String");
        Object o2 = (String)beanTool.getBeanInstance("java.lang.String");
        SimpleCloneableBean cloneableBean  = (SimpleCloneableBean)beanTool.getBeanInstance("com.funambol.framework.tools.beans.SimpleCloneableBean");
        SimpleCloneableBean cloneableBean2 = (SimpleCloneableBean)beanTool.getBeanInstance("com.funambol.framework.tools.beans.SimpleCloneableBean");
        
        SimpleLazyInitCloneableBean lazyBean = (SimpleLazyInitCloneableBean)beanTool.getBeanInstance("com.funambol.framework.tools.beans.SimpleLazyInitCloneableBean");
        SimpleLazyInitCloneableBean lazyBean2 = (SimpleLazyInitCloneableBean)beanTool.getBeanInstance("com.funambol.framework.tools.beans.SimpleLazyInitCloneableBean");

        assertNotSame("Instances must be different", o1, o2);
        assertNotSame("Instances must be different", cloneableBean, cloneableBean2);
        assertNotSame("Instances must be different", lazyBean, lazyBean2);

        assertTrue("No clone expected", cloneableBean.getNumOfClones() == 0);
        assertTrue("No clone expected", cloneableBean2.getNumOfClones() == 0);

        assertTrue("Instance should be initialized", lazyBean.isInitialized());
        assertTrue("Instance should be initialized", lazyBean2.isInitialized());

        Object o = null;
        boolean found = true;
        try {
            o = beanTool.getBeanInstance("not.existing.class.Name");
        } catch (BeanNotFoundException e) {
            found = false;
        }
        assertFalse("Expected BeanNotFoundException exception", found);
    }

    /**
     * Test of getBeanInstance method, of class BeanTool.
     */
    public void testGetNewBeanInstance_with_ClassName() throws Exception {

        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);

        Object o1 = (String)beanTool.getBeanInstance("java.lang.String");
        Object o2 = (String)beanTool.getBeanInstance("java.lang.String");
        SimpleCloneableBean cloneableBean  = (SimpleCloneableBean)beanTool.getNewBeanInstance("com.funambol.framework.tools.beans.SimpleCloneableBean");
        SimpleCloneableBean cloneableBean2 = (SimpleCloneableBean)beanTool.getNewBeanInstance("com.funambol.framework.tools.beans.SimpleCloneableBean");

        SimpleLazyInitCloneableBean lazyBean = (SimpleLazyInitCloneableBean)beanTool.getNewBeanInstance("com.funambol.framework.tools.beans.SimpleLazyInitCloneableBean");
        SimpleLazyInitCloneableBean lazyBean2 = (SimpleLazyInitCloneableBean)beanTool.getNewBeanInstance("com.funambol.framework.tools.beans.SimpleLazyInitCloneableBean");

        assertNotSame("Instances must be different", o1, o2);
        assertNotSame("Instances must be different", cloneableBean, cloneableBean2);
        assertNotSame("Instances must be different", lazyBean, lazyBean2);

        assertTrue("No clone expected", cloneableBean.getNumOfClones() == 0);
        assertTrue("No clone expected", cloneableBean2.getNumOfClones() == 0);

        assertTrue("Instance should be initialized", lazyBean.isInitialized());
        assertTrue("Instance should be initialized", lazyBean2.isInitialized());

        Object o = null;
        boolean found = true;
        try {
            o = beanTool.getNewBeanInstance("not.existing.class.Name");
        } catch (BeanNotFoundException e) {
            found = false;
        }
        assertFalse("Expected BeanNotFoundException exception", found);
    }

    /**
     * Test of getBeanNames method, of class BeanTool.
     */
    public void testGetBeanNames() {
        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);
        String[] beans = beanTool.getBeanNames("test");
        
        assertEquals("Wrong number of beans", 5, beans.length);
        
        // array should be already sorted
        assertEquals("wrong bean", "test" + File.separator + "BeanUsingBeanTool.xml", beans[0]);
        assertEquals("wrong bean", "test" + File.separator + "SimpleCloneableBean.xml", beans[1]);
        assertEquals("wrong bean", "test" + File.separator + "SimpleLazyInitBean.XML", beans[2]);
        assertEquals("wrong bean", "test" + File.separator + "SimpleNotCloneableBean.xml", beans[3]);
        assertEquals("wrong bean", "test" + File.separator + "SimpleString.xml", beans[4]);

        beans = beanTool.getBeanNames("");
        assertEquals("Wrong number of beans", 0, beans.length);

        BeanTool beanToolNotExisting = BeanTool.getBeanTool(BASE_TEST_DIR + "/NOT_EXISTING");
        beans = beanToolNotExisting.getBeanNames("");
        assertEquals("Wrong number of beans", 0, beans.length);

    }

    /**
     * Test of saveBeanInstance method, of class BeanTool.
     */
    public void testSaveBeanInstance() throws BeanException {
        //
        // Not the best way to test save and delete but it's pretty safe
        //
        SimpleCloneableBean bean = new SimpleCloneableBean();
        bean.prop1 = "prop1_value";
        bean.prop2 = "prop2_value";

        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);
        beanTool.saveBeanInstance("test/saving/bean.xml", bean);
        
        SimpleCloneableBean bean2 = (SimpleCloneableBean) beanTool.getNewBeanInstance("test/saving/bean.xml");
        assertTrue("Reading saved bean produced different bean", bean.equals(bean2));
    }

    /**
     * Test of deleteBeanInstance method, of class BeanTool.
     */
    public void testDeleteBeanInstance() throws BeanException {
        //
        // Not the best way to test save and delete but it's pretty safe
        //
        SimpleCloneableBean bean = new SimpleCloneableBean();
        bean.prop1 = "prop1_value";
        bean.prop2 = "prop2_value";

        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);
        beanTool.saveBeanInstance("test/saving/bean.xml", bean);

        assertTrue("Missing bean file", new File(BASE_TEST_DIR,"test/saving/bean.xml").isFile());

        beanTool.deleteBeanInstance("test/saving/bean.xml");
        assertFalse("Bean file should be missing", new File(BASE_TEST_DIR,"test/saving/bean.xml").isFile());
    }


    public void testBeanUsingBeanTool() throws BeanException {
        //
        // This test is to vefiry if the cache can be called recursively
        // (BeanUsingBeanTool uses BeanTool that uses the cache)
        //
        BeanTool beanTool = BeanTool.getBeanTool(BASE_TEST_DIR);
        BeanUsingBeanTool o = (BeanUsingBeanTool) beanTool.getBeanInstance("test/BeanUsingBeanTool.xml");
        assertTrue("BeanUsingBeanTool should be initialized", o.isInitialized());
    }
}
