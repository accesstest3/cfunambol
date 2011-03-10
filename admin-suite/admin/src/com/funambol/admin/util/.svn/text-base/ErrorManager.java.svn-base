/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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

package com.funambol.admin.util;

import com.funambol.admin.UnauthorizedException;
import java.rmi.ConnectException;

import com.funambol.admin.main.SyncAdminController;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.AdminException;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Manages the exception.
 *
 *
 * @version $Id: ErrorManager.java,v 1.7 2007-11-28 10:28:17 nichele Exp $
 */
public class ErrorManager {

    // ---------------------------------------------------------- Public Methods

    /**
     * Manages AdminException.
     * <br/>
     * Logs the exception and throws it.
     *
     * @param mex exception to manage
     * @throws AdminException re-throws the given exception
     */
    public static void manageAdminException(AdminException mex) throws
        AdminException {
        Log.error(mex);
        throw mex;
    }

    /**
     * Manages generic throwable.
     * <br/>
     * Logs the message and notifies the throwable to the user.
     *
     * @param g Throwable throwable to manage
     * @return an AdminException builds on the given Throwable
     */
    public static AdminException manageException(Throwable g) {

        if (g.getMessage() != null && !g.getMessage().equals("")) {
            Log.debug(g.getMessage());
        }
        
        if (g instanceof UnauthorizedException) {
            //            
            // a message error is displayed
            //
            NotifyDescriptor desc = new NotifyDescriptor.Message(Bundle.getMessage(Bundle.AUTHENTICATION_FAILED_ERROR_MESSAGE));
            DialogDisplayer.getDefault().notify(desc);
            
            //
            // the login panel is displayed because user is no more authorized 
            //
            SyncAdminController syncAdminController = new SyncAdminController();            
            ExplorerUtil.setSyncAdminController(syncAdminController); 
            ExplorerUtil.getSyncAdminController().startLoginProcess();
            
            return (AdminException)g;
        } else if (g instanceof AdminException) {
            NotifyDescriptor desc = new NotifyDescriptor.Message(g.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(desc);
            return (AdminException)g;
        } else {
            org.openide.ErrorManager.getDefault().notify(g);
            return new AdminException("Exception", g);
        }

    }

    /**
     * Manages ConnectException.
     * <br/>
     * Logs it, notifies the exception to the user and restart the login process.
     * @param cex managed exception
     */
    public static void manageConnectException(ConnectException cex) {

        String errorMessage = null;
        if (errorMessage == null) {
            errorMessage = Bundle.getMessage(Bundle.CONNECT_EXCEPTION_ERROR_MESSAGE);
        }
        Log.debug("ConnectException. Try to reconnect (" + cex.getMessage() + ")");

        // notify exception to the user
        NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);
        DialogDisplayer.getDefault().notify(desc);

        // restart login process
        SyncAdminController syncNode = ExplorerUtil.getSyncAdminController();
        
        syncNode.startLoginProcess();
    }

}
