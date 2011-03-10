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

package com.funambol.pimlistener.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.funambol.pushlistener.framework.ServiceDescriptor;
import com.funambol.pushlistener.service.PushListener;
import com.funambol.pushlistener.service.config.PushListenerConfiguration;

import com.funambol.server.db.DataSourceContextHelper;

/**
 * Starting class for the PimListener
 * @version $Id: PimListener.java,v 1.7 2008-05-18 16:05:04 nichele Exp $
 */
public class PimListener extends PushListener {

    // --------------------------------------------------------------- Constants

    private static final String POM_PROPERTIES_FILE =
            "META-INF/maven/funambol/pim-listener/pom.properties";

    private static final String VERSION = initVersion();

    // ------------------------------------------------------------ Private data

    /** The logger */
    private final static Logger log = Logger.getLogger("funambol.pimlistener");

    // ------------------------------------------------------------- Constructor
    public PimListener() {

        super(new ServiceDescriptor("Funambol PIM Listener",
                                    VERSION,
                                    "com.funambol.pimlistener:type=PIMListener"));

    }

    public static void main(String[] args) {

        String configPath = PushListenerConfiguration.getConfigPath();
        String log4jFile = configPath + File.separator + "log4j-pimlistener.xml";

        DOMConfigurator.configureAndWatch(log4jFile, 30000); // 30 sec.

        if (log.isInfoEnabled()) {
            log.info("Starting PIMListener");
        }

        try {
            DataSourceContextHelper.configureAndBindDataSources();
        } catch (Exception ex) {
            log.error("Error initializing the datasources", ex);
            System.exit(-1);
        }

        PimListener pimListener = new PimListener();

        try {
            pimListener.init();
        } catch (Exception ex) {
            log.error("Error initializing the PIMListener", ex);
            pimListener.shutdown();
            System.exit(-1);
        }

        try {
            pimListener.start();
        } catch (Exception ex) {
            log.error("Error starting the PIMListener", ex);
            pimListener.shutdown();
            System.exit(-1);
        }


    }

    // --------------------------------------------------------- Private methods
    /**
     * Sets the version reading POM_PROPERTIES_FILE
     * @return the version reading POM_PROPERTIES_FILE
     */
    private static String initVersion() {
        try {
            Properties properties = new Properties();

            InputStream resourceAsStream =
                    ClassLoader.getSystemResourceAsStream(POM_PROPERTIES_FILE);

            if ( resourceAsStream == null ) {
                return "unknown";
            }

            properties.load( resourceAsStream );
            return properties.getProperty( "version", "unknown");
        } catch (IOException e ) {
            return "unknown";
        }
    }
}
