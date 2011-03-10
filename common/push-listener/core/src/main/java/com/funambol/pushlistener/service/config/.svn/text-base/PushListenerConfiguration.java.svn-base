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

package com.funambol.pushlistener.service.config;

import java.io.File;

import org.apache.log4j.Logger;

import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanTool;

import com.funambol.pushlistener.service.*;

/**
 * Configuration class for a PushListener instance.
 * It implements singleton pattern so use PushListenerConfiguration.getInstance()
 * to obtain an instance.
 * @version $Id: PushListenerConfiguration.java,v 1.10 2008-04-22 08:19:11 nichele Exp $
 */
public class PushListenerConfiguration {

    // --------------------------------------------------------------- Constants

    /**
     * The default bean file name used to create an instance of a PushListenerConfiguration.
     * This bean file name is used if the system property funambol.pushlistener.config
     * is not specified
     */
    public static final String DEFAULT_PUSH_LISTENER_CONFIGURATION_BEAN_FILE =
        "com/funambol/pushlistener/PushListenerConfiguration.xml";

    /** The system property used to specify the config bean file name */
    private static final String CONFIG_BEAN_FILE_PROPERTY_NAME = "funambol.pushlistener.config.bean";
    
    /** The system property used to configure the config path */
    private static final String CONFIG_PATH_PROPERTY_NAME = "funambol.home";

    // ------------------------------------------------------------- Private data

    /** The logger */
    private final static Logger log = Logger.getLogger("funambol.pushlistener.config");

    /** The instance */
    private static PushListenerConfiguration instance;

    /** The configuration bean file name */
    private String configurationBeanFile = DEFAULT_PUSH_LISTENER_CONFIGURATION_BEAN_FILE;

    /**
     * The configBean object
     */
    private PushListenerConfigBean configBean;

    /**
     * Private constructor. An instance is created using a PushListenerConfigBean
     * obtained reading <CODE>PUSH_LISTENER_CONFIGURATION_BEAN</CODE>
     */
    private PushListenerConfiguration()  {
        String configBeanFile = System.getProperty(CONFIG_BEAN_FILE_PROPERTY_NAME);

        if (configBeanFile != null && !"".equals(configBeanFile)) {
            this.configurationBeanFile = configBeanFile;
        }
        try {

            configBean = (PushListenerConfigBean)
                BeanTool.getBeanTool(configPath).getNewBeanInstance(configurationBeanFile);

            configBean.fixConfigPath(configPath);
            
            configBean.validateConfigBean();            
            
        } catch (BeanException e) {
            throw new PushListenerConfigurationException(e);
        }

        if (log.isTraceEnabled()) {
            log.trace("PushListenerConfiguration object created reading: " +
                      configPath + "/" + configurationBeanFile);
        }
    }

    /**
     * The configuration path
     */
    private final static String configPath = getConfigPath();

    /**
     * Returns an instance of PushListenerConfiguration object
     * @return the configuration objet
     */
    public static synchronized PushListenerConfiguration getPushListenerConfiguration()  {

        if (instance == null) {
            instance = new PushListenerConfiguration();
        }

        return instance;
    }

    /**
     * Returns the configuration bean
     * @return the PushListenerConfigBean
     */
    public static PushListenerConfigBean getPushListenerConfigBean() {
        return getPushListenerConfiguration().getConfigBean();
    }

    /**
     * Returns the configuration path reading the system property
     * <code>CONFIG_PATH_PROPERTY_NAME</code>
     * @return the configuration path. CONFIG_PATH_PROPERTY_NAME + "/config"
     */
    public static String getConfigPath() {
        String path = System.getProperty(CONFIG_PATH_PROPERTY_NAME);
        if (path == null || path.length() == 0) {
            path = ".";
        }
        path = path + File.separator + "config";
        return path;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Returns the config bean object
     * @return the <CODE>PushListenerConfigBean</CODE> object
     */
    private PushListenerConfigBean getConfigBean() {
        return configBean;
    }
    
}
