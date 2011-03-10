/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.server.config;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.funambol.framework.server.plugin.Plugin;
import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.server.plugin.DummyPlugin;
import com.funambol.server.plugin.PluginHandler;

/**
 * Configuration test cases
 * @version $Id: ConfigurationTest.java,v 1.2 2008-06-05 08:15:46 nichele Exp $
 */
public class ConfigurationTest extends TestCase {
    
    private String funambolHome = System.getProperty("funambol.home");
    private File runtimePluginDir =
        new File(Configuration.getConfigPath() + "/com/funambol/server/plugin");
    
    // ------------------------------------------------------------- Construstor
    public ConfigurationTest(String testName) {
        super(testName);
    }            
    static {
        try {
            System.setProperty("java.naming.factory.initial",
                               "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        File initialPlugin = new File(funambolHome,
            "com/funambol/server/config/Configuration/plugin/initial-plugins");
        
        FileUtils.forceMkdir(runtimePluginDir);
        FileUtils.cleanDirectory(runtimePluginDir);
        FileUtils.copyDirectory(initialPlugin, runtimePluginDir, new WildcardFileFilter("*.xml"));
        
        //
        // This is required since if the plugin xml files used in the tests are 
        // downloaded from SVN repository, they have the same timestamp so the changes
        // are not correctly detected by the DirectoryMonitor. In this way we are sure
        // the timestamp is different (touch uses the current timestamp and of course the
        // files - in src/test/data/com/funambol/server/config/Configuration/pluging/test-1 -
        // are downloaded previously)
        //
        File[] files = runtimePluginDir.listFiles();
        for (File file : files) {
            FileUtils.touch(file);
        }
        
        Configuration.getConfiguration();    
    }

    @Override
    protected void tearDown() throws Exception {
        Configuration.getConfiguration().release();
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of plugin handling
     * @throws Exception
     */
    public void testPlugin_1() throws Exception {
        
        Configuration.getConfiguration().initializeEngineComponents();
                
        Boolean b = 
            (Boolean)PrivateAccessor.getField(Configuration.getConfiguration(), "engineComponentsInitialized");        
        
        assertTrue("The engine components should be initialized", b.booleanValue());
        
        PluginHandler pluginHandler = 
            (PluginHandler)PrivateAccessor.getField(Configuration.getConfiguration(), "pluginHandler");
        
        Map<String, Plugin> plugins = 
            (Map<String, Plugin>)PrivateAccessor.getField(pluginHandler, "plugins");
        
        assertEquals("wrong expected plugin number:", 2, plugins.size());
        
        //
        // Deleting plugin 3
        //
        FileUtils.forceDelete(new File(Configuration.getConfigPath() + "/com/funambol/server/plugin/dummy-plugin-3.xml"));
        File dirTest1 = new File(funambolHome, "com/funambol/server/config/Configuration/plugin/test-1");
                
        FileUtils.copyDirectory(dirTest1, runtimePluginDir, new WildcardFileFilter("*.xml"));
        
        
        // Waiting for automatic change detection
        Thread.sleep(15 * 1000);     
        
        pluginHandler = 
            (PluginHandler)PrivateAccessor.getField(Configuration.getConfiguration(), "pluginHandler");
        plugins = (Map<String, Plugin>)PrivateAccessor.getField(pluginHandler, "plugins");
        
        assertEquals("wrong expected plugin number:", 2, plugins.size());  
        
        System.out.println("Plugins: " + plugins);
        assertNotNull(plugins.get("dummy-plugin-1.xml"));
        assertNotNull(plugins.get("dummy-plugin-5.xml"));
        
        Plugin plugin1 = plugins.get("dummy-plugin-1.xml");
        assertEquals("Wrong plugin name", "test-plugin-1-updated", ((DummyPlugin)plugin1).getName());
    }
    
    /**
     * Test of plugin handling
     * @throws Exception
     */
    public void testPlugin_2() throws Exception {     
        
        Boolean b = 
            (Boolean)PrivateAccessor.getField(Configuration.getConfiguration(), "engineComponentsInitialized");        
        
        assertFalse("The engine components should not be initialized", b.booleanValue());
        
        //
        // engine components not initialized
        //
        PluginHandler pluginHandler = 
            (PluginHandler)PrivateAccessor.getField(Configuration.getConfiguration(), "pluginHandler");
        
        assertNull(pluginHandler);
                
        //
        // Touching a plugin file, nothing should happen
        //
        File f = new File(Configuration.getConfigPath() + "/com/funambol/server/plugin/dummy-plugin-1.xml");
        if (!f.isFile()) {
            fail("Missing file: " + f);
        }
        assertTrue("Unable to set the last-modified time of the file", 
                   f.setLastModified(System.currentTimeMillis()));
        // Waiting for automatic change detection
        Thread.sleep(15 * 1000);     
        
        //
        // engine components not initialized
        //
        pluginHandler = 
            (PluginHandler)PrivateAccessor.getField(Configuration.getConfiguration(), "pluginHandler");
        
        assertNull(pluginHandler);        
    }    

}
