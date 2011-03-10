/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.framework.cluster;

import java.io.File;
import junit.framework.TestCase;

/**
 * Test cases for ClusterConfiguration class
 * @version $Id: ClusterConfigurationTest.java 36594 2011-02-01 10:25:03Z nichele $
 */
public class ClusterConfigurationTest extends TestCase {

    public ClusterConfigurationTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }


    /**
     * Test of validate method of class ClusterConfiguration
     */
    public void testValidateConfig_1() {
        try {
            ClusterConfiguration bean = getValidClusterConfiguration_1();
            bean.validateConfiguration();
        } catch (ClusterException ex) {
            fail("Error checking a valid configuration (" + ex + ")");
        }
    }

    /**
     * Test of validate method of class ClusterConfiguration
     */
    public void testValidateConfig_2() {
        try {
            ClusterConfiguration bean = getValidClusterConfiguration_2();
            bean.validateConfiguration();
        } catch (ClusterException ex) {
            fail("Error checking a valid configuration (" + ex + ")");
        }
    }

    /**
     * Test of validate method of class ClusterConfiguration
     */
    public void testValidateConfig_3() {
        try {
            ClusterConfiguration bean = getValidClusterConfiguration_3();
            bean.validateConfiguration();
        } catch (ClusterException ex) {
            fail("Error checking a valid configuration (" + ex + ")");
        }
    }

    /**
     * Test of validate method of class ClusterConfiguration
     */
    public void testValidateConfig_4() {
        try {
            ClusterConfiguration bean = getValidClusterConfiguration_4();
            bean.validateConfiguration();
        } catch (ClusterException ex) {
            fail("Error checking a valid configuration (" + ex + ")");
        }
    }

    /**
     * Test of validate method of class ClusterConfiguration
     */
    public void testValidateConfig_InvalidClusterName() {
        ClusterConfiguration bean = getValidClusterConfiguration_1();

        bean.setClusterName(null);

        try {
            bean.validateConfiguration();
        } catch (ClusterException e) {
            return;
        }
        fail("A ClusterException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class ClusterConfiguration
     */
    public void testValidateConfig_NoPropNoFile() {
        ClusterConfiguration bean = getValidClusterConfiguration_1();

        bean.setConfigurationFile(null);
        bean.setProperties(null);

        try {
            bean.validateConfiguration();
        } catch (ClusterException e) {
            return;
        }
        fail("A ClusterException was excepted because the bean was not valid");
    }

    // --------------------------------------------------------- Private methods
    /**
     * Returns a valid ClusterConfiguration
     * @return a valid ClusterConfiguration
     */
    private ClusterConfiguration getValidClusterConfiguration_1() {
        ClusterConfiguration bean = new ClusterConfiguration();
        bean.setClusterName("test-cluster");
        bean.setConfigurationFile("src/test/data/cluster-config-file.xml");
        return bean;
    }

    /**
     * Returns a valid ClusterConfiguration
     * @return a valid ClusterConfiguration
     */
    private ClusterConfiguration getValidClusterConfiguration_2() {
        ClusterConfiguration bean = new ClusterConfiguration();
        bean.setClusterName("test-cluster");
        bean.setProperties("properties list");
        return bean;
    }

    /**
     * Returns a valid ClusterConfiguration
     * @return a valid ClusterConfiguration
     */
    private ClusterConfiguration getValidClusterConfiguration_3() {
        ClusterConfiguration bean = new ClusterConfiguration("test-cluster", "properties");
        return bean;
    }

    /**
     * Returns a valid ClusterConfiguration
     * @return a valid ClusterConfiguration
     */
    private ClusterConfiguration getValidClusterConfiguration_4() {
        ClusterConfiguration bean = 
            new ClusterConfiguration("test-cluster", new File("src/test/data/cluster-config-file.xml"));
        return bean;
    }
}
