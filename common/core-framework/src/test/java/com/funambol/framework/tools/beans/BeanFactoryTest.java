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
import java.io.File;
import junit.framework.TestCase;

/**
 * BeanFactory test cases
 * @version $Id£
 */
public class BeanFactoryTest extends TestCase {

    private static final String BASE_DIR = "src/test/resources/data/com/funambol/framework/tools/BeanFactory";

    public BeanFactoryTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of getNoInitBeanInstance method, of class BeanFactory.
     */
    public void testGetNoInitBeanInstance() throws Exception {
        String bean = BASE_DIR + "/" + "beans/SimpleLazyInitBean.xml";
        Object o = BeanFactory.getNoInitBeanInstance(bean);
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);

        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;

        assertFalse("The bean should not be initialized", lazyBean.isInitialized());
    }

    /**
     * Test of getNoInitBeanInstance method, of class BeanFactory.
     */
    public void testGetNoInitBeanInstance_with_file() throws Exception {
        String bean = BASE_DIR + "/" + "beans/SimpleLazyInitBean.xml";
        Object o = BeanFactory.getNoInitBeanInstance(new File(bean));
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);

        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;

        assertFalse("The bean should not be initialized", lazyBean.isInitialized());
    }

    /**
     * Test of getBeanInstance method, of class BeanFactory.
     */
    public void testGetBeanInstance_String() throws Exception {
        String bean = BASE_DIR + "/" + "beans/SimpleLazyInitBean.xml";
        Object o = BeanFactory.getBeanInstance(bean);
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);
        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;
        assertTrue("The bean should be initialized", lazyBean.isInitialized());


        bean = BASE_DIR + "/" + "beans/SimpleCloneableBean.xml";
        o = BeanFactory.getBeanInstance(bean);
        assertTrue("Wrong object class", o instanceof SimpleCloneableBean);
    }

    /**
     * Test of getBeanInstance method, of class BeanFactory.
     */
    public void testGetBeanInstance_File() throws Exception {
        File bean = new File(BASE_DIR + "/" + "beans/SimpleLazyInitBean.xml");
        Object o = BeanFactory.getBeanInstance(bean);
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);
        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;
        assertTrue("The bean should be initialized", lazyBean.isInitialized());


        bean = new File(BASE_DIR + "/" + "beans/SimpleCloneableBean.xml");
        o = BeanFactory.getBeanInstance(bean);
        assertTrue("Wrong object class", o instanceof SimpleCloneableBean);
    }

    /**
     * Test of getBeanInstanceFromConfig method, of class BeanFactory.
     */
    public void testGetBeanInstanceFromConfig() throws Exception {
        File bean = new File(BASE_DIR + "/" + "beans/SimpleLazyInitBean.xml");
        String config = IOTools.readFileString(bean);
        
        Object o = BeanFactory.getBeanInstanceFromConfig(config);
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);
        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;
        assertTrue("The bean should be initialized", lazyBean.isInitialized());


        bean = new File(BASE_DIR + "/" + "beans/SimpleCloneableBean.xml");
        config = IOTools.readFileString(bean);
        o = BeanFactory.getBeanInstanceFromConfig(config);
        assertTrue("Wrong object class", o instanceof SimpleCloneableBean);
    }

    /**
     * Test of getBeanConfig method, of class BeanFactory.
     */
    public void testGetBeanConfig() throws Exception {
        String bean = BASE_DIR + "/" + "beans/SimpleLazyInitBean.xml";
        String s = BeanFactory.getBeanConfig(bean);

        assertEquals("Wrong config file content", IOTools.readFileString(bean), s);
    }

    /**
     * Test of saveBeanInstance method, of class BeanFactory.
     */
    public void testSaveBeanInstance() throws Exception {
        //
        // In order to verify that the bean is saved, we create a new bean

        String bean = BASE_DIR + "/" + "beans/runtimetest/TestSave.xml";
        SimpleLazyInitBean object= new SimpleLazyInitBean();
        object.setProp1("prop1");
        object.setProp2("prop2");
        object.init();
        
        BeanFactory.saveBeanInstance(object, new File(bean));

        File f = new File(bean);
        assertTrue("The object has not been saved", f.isFile());

        String content = IOTools.readFileString(f);
        f.delete();


        Object newInstance = BeanFactory.getBeanInstanceFromConfig(content);
        assertTrue("The saved bean seems different from the expected one", object.equals(newInstance));

    }

    /**
     * Test of marshal method, of class BeanFactory.
     */
    public void testMarshal_And_Unmarshal() throws Exception {
        //
        // Not the best way to test marshal and unmarshal but it's pretty safe
        //
        SimpleCloneableBean bean = new SimpleCloneableBean();
        bean.prop1 = "prop1_value";
        bean.prop2 = "prop2_value";
        String xml = BeanFactory.marshal(bean);
        SimpleCloneableBean bean2 = (SimpleCloneableBean) BeanFactory.unmarshal(xml);
        assertTrue("Marshall and unmarshall produced different values", bean.equals(bean2));
    }

    /**
     * Test of getNoInitBeanInstance method, of class BeanFactory.
     */
    public void testGetNoInitBeanInstance_File() throws Exception {
        String bean = BASE_DIR + "/" + "beans/SimpleLazyInitBean.xml";
        Object o = BeanFactory.getNoInitBeanInstance(new File(bean));
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);

        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;

        assertFalse("The bean should not be initialized", lazyBean.isInitialized());
    }

    /**
     * Test of getNoInitBeanInstanceFromConfig method, of class BeanFactory.
     */
    public void testGetNoInitBeanInstanceFromConfig() throws Exception {
        String bean = BASE_DIR + "/" + "beans/SimpleLazyInitBean.xml";
        String config = IOTools.readFileString(bean);
        Object o = BeanFactory.getNoInitBeanInstanceFromConfig(config);
        assertTrue("Wrong object class", o instanceof SimpleLazyInitBean);
        SimpleLazyInitBean lazyBean = (SimpleLazyInitBean)o;
        assertFalse("The bean should not be initialized", lazyBean.isInitialized());
    }

}