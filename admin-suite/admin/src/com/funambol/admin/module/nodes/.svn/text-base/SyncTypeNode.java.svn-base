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
package com.funambol.admin.module.nodes;

import javax.swing.Action;

import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.server.Sync4jSourceType;

import com.funambol.admin.common.RefreshAction;
import com.funambol.admin.common.Refreshable;
import com.funambol.admin.module.SyncSourceEventListener;
import com.funambol.admin.module.actions.InsertSyncSource;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.util.Constants;
import com.funambol.framework.server.Sync4jConnector;
import com.funambol.framework.server.Sync4jModule;

import org.openide.nodes.AbstractNode;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;

/**
 * Node associated to a Sync4jSourceType.
 *
 * @version $Id: SyncTypeNode.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class SyncTypeNode extends AbstractNode implements SyncSourceEventListener, Refreshable {

    // ------------------------------------------------------------ Private data

    /** Sync4jModule associated with this node */
    private Sync4jModule Sync4jModule;

    /**
     * Returns Sync4jModule
     */
    public Sync4jModule getSync4jModule() {
        return Sync4jModule;
    }

    /** Sync4jConnector associated with this node */
    private Sync4jConnector Sync4jConnector;

    /**
     * Returns Sync4jConnector
     */
    public Sync4jConnector getSync4jConnector() {
        return Sync4jConnector;
    }

    /** Sync4jSourceType associated with this node */
    private Sync4jSourceType sync4jSourceType;

    /**
     * Returns sync4jSourceType
     */
    public Sync4jSourceType getSync4jSourceType() {
        return sync4jSourceType;
    }

    // ------------------------------------------------------------- Constructor

    /**
     * Create a new node.
     *
     * @param Sync4jModule module associated with this node
     * @param Sync4jConnector connector associated with this node
     * @param sync4jSourceType sync4jSourceType associated with this node
     */
    public SyncTypeNode(Sync4jModule Sync4jModule,
                        Sync4jConnector Sync4jConnector, Sync4jSourceType sync4jSourceType) {
        super(new SyncTypeChildren());

        this.Sync4jModule = Sync4jModule;
        this.Sync4jConnector = Sync4jConnector;
        this.sync4jSourceType = sync4jSourceType;

        ( (SyncTypeChildren) (this.getChildren())).setSync4jConnector(Sync4jConnector);
        ( (SyncTypeChildren) (this.getChildren())).setSync4jSourceType(sync4jSourceType);
        ( (SyncTypeChildren) (this.getChildren())).setSync4jModule(Sync4jModule);

        setIconBaseWithExtension(Constants.ICON_SYNCTYPE_NODE);
        String syncTypeName = sync4jSourceType.getDescription();
        setName(syncTypeName);
        setDisplayName(syncTypeName);

        // register this node as syncSourceEventListener
        ExplorerUtil.getSyncAdminController().getSyncSourcesController().addSyncSourceEventListener(this);
    }

    // ---------------------------------------------------------- Public Methods

    //-------------------------------------------------------
    // Overrides org.openide.nodes.AbstractNode method
    //-------------------------------------------------------
    /**
     * Define actions associated to right button of the mouse for this node.
     *
     * @return SystemAction[] actions associated to right mouse button
     */
    public Action[] getActions(boolean context) {
        return new SystemAction[] {
            NodeAction.get(InsertSyncSource.class),
            null,
            NodeAction.get(RefreshAction.class)

        };
    }

    /**
     * Set action to perform on double click on this node
     * @return <code>InsertSyncSource</code> action
     */
    public Action getPreferredAction() {
        return NodeAction.get(InsertSyncSource.class);
    }

    //---------------------------------------------------------
    // Overrides org.openide.nodes.AbstractNode method
    //---------------------------------------------------------

    /**
     * Returns context help associated with this node
     * @return context help associated with this node
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Called when a new syncSource is add
     *
     * @param syncSource the syncSource added
     */
    public void syncSourceAdded(SyncSource syncSource) {
        ( (SyncTypeChildren) (this.getChildren())).addSyncSource(syncSource);
    }

    /**
     * Called when a syncSource is updated
     *
     * @param syncSource the syncSource updated
     */
    public void syncSourceUpdated(SyncSource syncSource) {
    }

    /**
     * Called when a syncSource is deleted
     *
     * @param syncSource the syncSource deleted
     */
    public void syncSourceDeleted(String sourceUri) {
        ( (SyncTypeChildren) (this.getChildren())).removeSyncSource(sourceUri);
    }

    /**
     * Returns the id of the module associated with this node
     * @return the id of the module associated with this node
     */
    public String getModuleId() {
        return Sync4jModule.getModuleId();
    }

    /**
     * Returns the id of the connector associated with this node
     * @return the id of the connector associated with this node
     */
    public String getConnectorId() {
        return Sync4jConnector.getConnectorId();
    }

    /**
     * Returns the id of the syncSourceType associated with this node
     * @return the id of the syncSourceType associated with this node
     */
    public String getSyncSourceTypeId() {
        return sync4jSourceType.getSourceTypeId();
    }

    /**
     * Refresh modules list
     */
    public void refresh() {
        // refresh parent
        ( (ConnectorNode)this.getParentNode()).refresh();
    }

}
