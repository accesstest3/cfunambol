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
package com.funambol.email.mocks;

import com.funambol.email.engine.source.EmailSyncSource;
import com.funambol.email.exception.EntityException;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.SyncItemInfo;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author $Id: EmailSyncSourceMock.java,v 1.1 2008-03-25 11:28:20 gbmiglia Exp $
 */
public class EmailSyncSourceMock extends EmailSyncSource {
    
    // ------------------------------------------------------------ Constructors
    public EmailSyncSourceMock(){
        this.filter = new EmailFilter();        
    }

    // ---------------------------------------------------- Properties accessors
    public EmailFilter getFilter() {
        return filter;
    }
    
    public void setServerItems(Map<String, SyncItemInfo> serverItems){
        this.serverItems = serverItems;
    }    
    
    public void setLocalItems(Map<String, SyncItemInfo> localItems){
        this.localItems = localItems;
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Call the private method <code>setItemKeys</code> of <code>EmailSyncSource</code>.
     * @throws com.funambol.email.exception.EntityException
     */
    public void setItemKeys() throws EntityException {
        try {

            Class<?> c = Class.forName("com.funambol.email.engine.source.EmailSyncSource");
            Method[] allMethods = c.getDeclaredMethods();
            
            for (Method m : allMethods) {
                String methodName = m.getName();
                if (methodName.equals("setItemKeys")) {
                    m.setAccessible(true);
                    m.invoke(this, (Object[])null);
                    break;
                }
            }

        } catch (Exception e) {
        }        
    }
}
