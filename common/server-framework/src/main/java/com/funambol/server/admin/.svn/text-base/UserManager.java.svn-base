/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

package com.funambol.server.admin;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.store.PersistentStoreException;

/**
 * A <i>UserManager</i> is a class that manager users and roles.
 *
 * @version $Id: UserManager.java,v 1.3 2008-05-15 05:31:07 nichele Exp $
 */
public interface UserManager {

    // --------------------------------------------------------------- Constants
    public static final String ROLE_ADMIN = "sync_administrator";	
    public static final String ROLE_USER  = "sync_user";	
    
    // ---------------------------------------------------------- Public methods
    public String[] getRoles() throws AdminException, PersistentStoreException;

    public Sync4jUser[] getUsers(Clause clause) throws AdminException, PersistentStoreException;

    public void getUser(Sync4jUser user) throws AdminException, PersistentStoreException;

    public void getUserRoles(Sync4jUser user) throws AdminException, PersistentStoreException;

    public void setUser(Sync4jUser user) throws AdminException, PersistentStoreException;

    public void insertUser(Sync4jUser user) throws AdminException, PersistentStoreException;

    public void deleteUser(Sync4jUser user) throws AdminException, PersistentStoreException;

    public int countUsers(Clause clause) throws AdminException, PersistentStoreException;
    
    public boolean isUniqueAdministrator(Sync4jUser user) throws AdminException, PersistentStoreException;
}
