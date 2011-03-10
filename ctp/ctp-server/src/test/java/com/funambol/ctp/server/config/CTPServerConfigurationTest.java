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

package com.funambol.ctp.server.config;

import junit.framework.*;

import java.io.File;

import com.funambol.framework.cluster.ClusterConfiguration;
import com.funambol.server.admin.ws.client.ServerInformation;

/**
 * CTPServer configuration test cases
 * @version $Id: CTPServerConfigurationTest.java,v 1.8 2007-11-28 11:26:16 nichele Exp $
 */
public class CTPServerConfigurationTest extends TestCase {

    public CTPServerConfigurationTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getConfigPath method, of class com.funambol.ctp.server.config.CTPServerConfiguration.
     */
    public void testGetConfigPath() {

        String expResult = "./src/test/resources" + File.separator + "config";
        String result = CTPServerConfiguration.getConfigPath();
        assertEquals(expResult, result);
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_Empty() {
        CTPServerConfigBean bean = new CTPServerConfigBean();
        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_valid() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.validate();
    }


    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_InvalidPort_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setPortNumber(-1);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_InvalidPort_2() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setPortNumber(0);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_ClientHeartBeat_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setClientHeartBeatExpectedTime(-10);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_AcceptorThreadPool_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setConnectionAcceptorThreadPoolSize(0);

        bean.validate();
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_AcceptorThreadPool_2() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setConnectionAcceptorThreadPoolSize(-11);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_ClusterConfig_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setClusterConfiguration(null);

        bean.validate();
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_ClusterConfig_2() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setClusterConfiguration(new ClusterConfiguration());
        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");

        bean.validate();
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_NotificationGroupConfigFileName_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setNotificationGroupConfigFileName((String)null);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_NotificationGroupConfigFileName_2() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setNotificationGroupConfigFileName("");

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_NotificationGroupName_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setNotificationGroupName((String)null);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_NotificationGroupName_2() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setNotificationGroupName((String)null);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_ReceiveBufferSize_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setReceiveBufferSize(0);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_ReceiveBufferSize_2() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setReceiveBufferSize(-1);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_MinaIdleTime_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setMinaIdleTime(-1);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_MinaIdleTime_2() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setMinaIdleTime(0);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_DSServerinformation_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setWSServerInformation((ServerInformation)null);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

    /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testValidateConfig_AuthenticationManager_1() {
        CTPServerConfigBean bean = getValidConfigBean();

        bean.setAuthenticationManager((String)null);

        try {
            bean.validate();
        } catch (CTPServerConfigurationException e) {
            return;
        }
        fail("A CTPServerConfigurationException was excepted because the bean was not valid");
    }

        /**
     * Test of validate method of class com.funambol.ctp.server.config.CTPServerConfigBean
     */
    public void testFixPath_1() {

        CTPServerConfigBean bean = getValidConfigBean();

        String originalNotificationConfigFile = bean.getNotificationGroupConfigFileName();
        String originalCTPServerConfigFile = bean.getClusterConfiguration().getConfigurationFile();
        
        bean.fixConfigPath("/config");

        
        String expectedNotificationGroup = File.separator +
                                           "config" +
                                           File.separator +
                                           originalNotificationConfigFile;

        String expectedCTPServerGroup = File.separator +
                                        "config" +
                                        File.separator +
                                        originalCTPServerConfigFile;

        assertEquals(bean.getNotificationGroupConfigFileName(), expectedNotificationGroup);
        assertEquals(bean.getClusterConfiguration().getConfigurationFile(), expectedCTPServerGroup);
    }



    /**
     * Returns a valid CTPServerConfigBean
     * @return a valid CTPServerConfigBean
     */
    private CTPServerConfigBean getValidConfigBean() {
        CTPServerConfigBean bean = new CTPServerConfigBean();
        bean.setPortNumber(4745);
        bean.setClientHeartBeatExpectedTime(100);
        bean.setConnectionAcceptorThreadPoolSize(1);
        bean.setClusterConfiguration(new ClusterConfiguration("ctpserver-jgroups.xml", 
                new File("src/test/resources/config/cluster-config-file.xml")));
        bean.setNotificationGroupConfigFileName("notification-jgroups.xml");
        bean.setNotificationGroupName("notification");
        bean.setReceiveBufferSize(64);
        bean.setMinaIdleTime(10);
        bean.setWSServerInformation(
            new ServerInformation("http://localhost:8080/funambol/services/admin",
                                  "admin",
                                  "admin"
            )
        );
        bean.setAuthenticationManager("com/funambol/ctp/server/authentication/AuthenticationManager.xml");
        return bean;
    }
}
