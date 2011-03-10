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

import javax.swing.Action;

import org.apache.log4j.Appender;

import org.openide.nodes.BeanNode;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

import com.funambol.admin.common.RefreshAction;
import com.funambol.admin.common.Refreshable;
import com.funambol.admin.settings.actions.EditAppender;
import com.funambol.admin.util.Constants;

/**
 * Node associated to an appender.
 * @version $Id: AppenderSettingsNode.java,v 1.4 2007-11-28 10:28:17 nichele Exp $
 */
public class AppenderSettingsNode extends BeanNode
implements Refreshable {

    // ------------------------------------------------------------ Private data

    /**
     * The Appender object this node represents
     */
    private Appender appender;

    // ------------------------------------------------------------ Constructors

    /**
     * Create node with the given name
     * @param name node's name
     * @throws IntrospectionException
     */
    public AppenderSettingsNode(Appender appender)
    throws java.beans.IntrospectionException{
        super(appender);

        setIconBaseWithExtension(Constants.ICON_APPENDER_SETTINGS_NODE);

        this.appender = appender;
        this.setDisplayName(getDisplayName(appender));
    }

    // ---------------------------------------------------------- Public Methods

    /**
     *Returns false so the node can not be renamed.
     **/
    public boolean canRename() {
        return false;
    }

    /**
     * Returns the preferred action (edit).
     * @return the preferred action (edit).
     */
    public Action getPreferredAction() {
        return NodeAction.get(EditAppender.class);
    }
    /**
     * Define actions associated to right button of the mouse for this node.
     *
     * @return SystemAction[] actions associated to right mouse button
     */
    public Action[] getActions(boolean context) {
        return new Action[] {
                   NodeAction.get(EditAppender.class   ),
                   NodeAction.get(RefreshAction.class)
               };
    }

    //-----------------------------------------------
    // Overrides org.openide.nodes.AbstractNode method
    //-----------------------------------------------

    /**
     * Returns context help associated with this node
     * @return context help associated with this node
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Reload the children list
     */
    public void refresh() {
        ((AppendersSettingsNode)getParentNode()).refresh();
    }

    /**
     * Returns the logger configuration represented by this node.
     *
     * @return the logger configuration represented by this node.
     */
    public Appender getAppender() {
        return appender;
    }

    // -----------------------------------------------------------Private method

    /**
     * Returns the display name for the given appender adding a space to the all
     * uppercase characters contained in the name.
     * @param appender the appender
     * @return the display name of the given appender adding a space to the all
     *         uppercase characters contained in the name.
     */
    private String getDisplayName(Appender appender) {
        String name = appender.getName();
        StringBuffer sbDisplayName = new StringBuffer();
        int nameLen = name.length();
        for (int i=0; i<nameLen; i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                sbDisplayName.append(" ");
            }
            sbDisplayName.append(c);
        }
        return sbDisplayName.toString();
    }
}
