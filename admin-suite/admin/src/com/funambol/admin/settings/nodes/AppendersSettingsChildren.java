/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

package com.funambol.admin.settings.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import org.apache.log4j.Appender;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

/**
 * List of appenders.
 * @version $Id: AppendersSettingsChildren.java,v 1.4 2007-11-28 10:28:17 nichele Exp $
 */
public class AppendersSettingsChildren extends Children.Array {

    // ------------------------------------------------------------ Private Data

    // ---------------------------------------------------------- Public methods
   /**
     * Reload children
     */
    public void refreshChildren() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                remove(getNodes());

                Collection children = loadChildren();

                Node[] nodes = (Node[])children.toArray(new Node[children.size()]);

                add(nodes);
            }
        });

    }
    // ------------------------------------------------------- Protected methods

    //-------------------------------------------------------
    // Overrides org.openide.nodes.Children method
    //-------------------------------------------------------
    protected Collection initCollection() {
        return loadChildren();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Loads children
     *
     * @return a Collection object containing the loaded nodes
     */
    private Collection loadChildren() {
        ArrayList nodes = new ArrayList();

        try {
            java.util.Map appenders = ExplorerUtil.getSyncAdminController()
                                                  .getServerSettingsController()
                                                  .getAppenders();
            Iterator itAppenders = appenders.values().iterator();
            while (itAppenders.hasNext()) {
                nodes.add(new AppenderSettingsNode((Appender)itAppenders.next()));
            }

            //
            // Sorts the nodes by appender name
            //
            Collections.sort(nodes, new Comparator() {
                public int compare(Object o1, Object o2) {
                    AppenderSettingsNode a1 = (AppenderSettingsNode)o1;
                    AppenderSettingsNode a2 = (AppenderSettingsNode)o2;
                    String name1 = a1.getAppender().getName().toLowerCase();
                    String name2 = a2.getAppender().getName().toLowerCase();
                    return name1.compareTo(name2);
                }

                public boolean equals(Object o1, Object o2) {
                    AppenderSettingsNode a1 = (AppenderSettingsNode)o1;
                    AppenderSettingsNode a2 = (AppenderSettingsNode)o2;
                    String name1 = a1.getAppender().getName().toLowerCase();
                    String name2 = a2.getAppender().getName().toLowerCase();
                    return name1.equals(name2);
                }
            });

        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_LOADING_SERVER_APPENDERS), e);
        }
        return nodes;
    }
}
