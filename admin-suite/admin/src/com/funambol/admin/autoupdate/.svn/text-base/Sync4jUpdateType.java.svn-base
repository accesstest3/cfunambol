/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.admin.autoupdate;

import java.io.IOException;

import java.net.URL;

import java.text.MessageFormat;

import org.netbeans.modules.autoupdate.XMLAutoupdateType;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * This AutoupdateType object will pickup the URL to which ask for an update
 * from the URL inserted into the login pane. The default URL will just be
 * ignored.
 *
 * This class will be used instead of the standard XMLUpdateType in the 
 * layer file au.settings.
 *
 * @version $Id: Sync4jUpdateType.java,v 1.7 2007-11-28 10:28:19 nichele Exp $
 */
public class Sync4jUpdateType extends XMLAutoupdateType {
    
    
    public Sync4jUpdateType(URL url, String displayName, String url_key, Boolean enabled) {
        super ( url, displayName, url_key, enabled );
    }
    
    /**
     * Here we create the AutoupdateType object. We try to replace the hostname
     * and port placeholders based on the host information gathered in the login
     * panel. If such information is not present yet, we default to the 
     * values for the bundled.
     *
     * @param fo the layer object
     *
     * @return the newly created AutoupdateType object
     */
    public static Sync4jUpdateType createSync4jUpdateType(FileObject fo) 
    throws IOException {
        URL url;
        
        Object o = fo.getAttribute("url");
        
        Log.debug("Creating the Funambol autoupdate type with base URL: " + o);
        
        AdminConfig config = AdminConfig.getAdminConfig();
        
        if (o instanceof String) {
            String[] args = {
                config.getHostName(),
                config.getServerPort()
            };
            
            MessageFormat mf = new MessageFormat((String)o);
            url = new URL(mf.format(args));
        } else {
            throw new IllegalArgumentException("'url' must be a String!");
        }
        
        Log.info("Checking " + url + " for updates.");
        Log.info("If a new version of the administration tool is found, an update icon will appear in the right bottom corner of the window.");

        // get display name from data node
        String name = DataObject.find(fo).getNodeDelegate().getDisplayName();
        
        Boolean en = (Boolean)fo.getAttribute("enabled");
        
        return new Sync4jUpdateType( url, name, null, en );        
    }
    
    /** @return human presentable name */
    public String displayName() {
        return Bundle.getMessage(Constants.AUTO_UPDATE_TYPE_NAME);
    }
    
    protected String getDefaultURL() {
        return "http://localhost:8080/funambol/updates.xml";        
    }
    
    protected URL modifyURL (URL original) {
        return original;
    }
    
}
