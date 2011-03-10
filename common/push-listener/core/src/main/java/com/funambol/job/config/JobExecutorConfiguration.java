/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2010 Funambol, Inc.
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

package com.funambol.job.config;

import java.io.File;

import org.apache.log4j.Logger;

import com.funambol.framework.tools.beans.BeanTool;

/**
 * Configuration class for a JobExecutorListener instance.
 * It implements singleton pattern so use JobExecutorConfiguration.getInstance()
 * to obtain an instance.
* @version $Id: JobExecutorConfiguration.java 35930 2010-09-08 11:25:14Z onufryk $ 
*/
public class JobExecutorConfiguration {
    // --------------------------------------------------------------- Constants
    /**
     * The bean file name used to create an instance of a JobExecutorConfiguration
     */
    public static final String JOB_EXECUTOR_CONFIGURATION_BEAN_FILE =
            "com/funambol/job/JobExecutorConfiguration.xml";

    /** The system property used to configure the config path */
    public static final String CONFIG_PATH_PROPERTY_NAME = "funambol.home";

    // ------------------------------------------------------------- Private data

    /** The logger */
   private final static Logger log = Logger.getLogger("funambol.pushlistener.jobexecutor");

    /** The instance */
    private static JobExecutorConfiguration instance;

    /**
     * The configBean object
     */
    private JobExecutorConfigBean configBean;

    /**
     * The configuration path
     */
    private final static String configPath = getConfigPath();

    /**
     * Private constructor. An instance is created using a JobExecutorConfigBean
     * obtained reading <CODE>JOB_EXECUTOR_CONFIGURATION_BEAN</CODE>
     */
    private JobExecutorConfiguration()  {
        try {
            if (log.isTraceEnabled()) {
                log.trace("Starting configuration");
            }
            configBean = (JobExecutorConfigBean)BeanTool.getBeanTool(configPath).
                    getNewBeanInstance(JOB_EXECUTOR_CONFIGURATION_BEAN_FILE);

        } catch (Exception e) {
            log.error(e);
            throw new JobExecutorConfigurationException(JOB_EXECUTOR_CONFIGURATION_BEAN_FILE, e);
        }

        try {
            configBean.fixConfigPath(configPath);
            configBean.validateConfigBean();
        } catch (JobExecutorConfigurationException e) {
            log.error(e);
            throw new JobExecutorConfigurationException("Invalid configuration file "
                    + JOB_EXECUTOR_CONFIGURATION_BEAN_FILE + " (" + e.getMessage()+ ")", e);
        }

        if (log.isTraceEnabled()) {
            log.trace("JobExecutorConfiguration object created reading: " +
                    configPath + File.separator  + JOB_EXECUTOR_CONFIGURATION_BEAN_FILE);
        }
    }

    /**
     * Returns an instance of JobExecutorConfiguration object
     * @return the JobExecutorConfiguration
     */
    public static synchronized JobExecutorConfiguration getJobExecutorConfiguration()  {

        if (instance == null) {
            instance = new JobExecutorConfiguration();
        }

        return instance;
    }

    /**
     * Returns the configuration bean
     *
     * @return the JobExecutorConfigBean
     */
    public static JobExecutorConfigBean getJobExecutorConfigBean() {
        return getJobExecutorConfiguration().configBean;
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

}

