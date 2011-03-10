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

import java.beans.IntrospectionException;

import javax.swing.Action;

import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceErrorDescriptor;
import com.funambol.framework.server.Sync4jSourceType;

import com.funambol.admin.common.RefreshAction;
import com.funambol.admin.common.Refreshable;
import com.funambol.admin.module.SyncSourceEventListener;
import com.funambol.admin.module.actions.DeleteSyncSource;
import com.funambol.admin.module.actions.ShowSyncSourceException;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.util.Constants;
import com.funambol.framework.server.Sync4jConnector;
import com.funambol.framework.server.Sync4jModule;

import org.openide.nodes.BeanNode;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;

/**
 * Node associated to SyncSourceException.<br/>
 * With this node user can manage syncSource that have given error during the loading
 *
 * @version $Id: SyncSourceExceptionNode.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class SyncSourceExceptionNode extends BeanNode implements SyncSourceEventListener,
    Refreshable {

    // ------------------------------------------------------------ Private data

    /** Sync4jModule that contains the syncSourceException associated to this SyncSourceExceptionNode */
    private Sync4jModule Sync4jModule;

    /**
     * Returns property Sync4jModule
     */
    public Sync4jModule getSync4jModule() {
        return Sync4jModule;
    }

    /** Sync4jConnector that contains the syncSourceException associated to this SyncSourceExceptionNode */
    private Sync4jConnector Sync4jConnector;

    /**
     * Returns property Sync4jConnector
     */
    public Sync4jConnector getSync4jConnector() {
        return Sync4jConnector;
    }

    /** Sync4jSourceType of the syncSourceException associated to this SyncSourceExceptionNode */
    private Sync4jSourceType sync4jSourceType;

    /**
     * Returns property sync4jSourceType
     */
    public Sync4jSourceType getSync4jSourceType() {
        return sync4jSourceType;
    }

    /** SyncSourceError associated to this syncSourceExceptionNode */
    private SyncSourceErrorDescriptor syncSourceError = null;

    /**
     * Returns property syncSourceError
     */
    public SyncSourceErrorDescriptor getSyncSourceError() {
        return syncSourceError;
    }

    /** SourceUri associated to this syncSourceExceptionNode */
    private String sourceUri = null;

    /**
     * Returns property sourceUri
     */
    public String getSourceUri() {
        return sourceUri;
    }

    // ------------------------------------------------------------- Constructor

    /**
     * Constructs a new SyncSourceNode for a syncSource
     *
     * @param Sync4jModule the Sync4jModule that contains the syncSource associated to the node
     * @param connector the Sync4jConnector that contains the syncSource associated to the node
     * @param sync4jSourceType the sync4jSourceType of the syncSource associated to the node
     * @param syncSource the syncSource associated to the node
     * @throws IntrospectionException
     */
    public SyncSourceExceptionNode(Sync4jModule Sync4jModule, Sync4jConnector connector,
                                   Sync4jSourceType sync4jSourceType,
                                   SyncSourceErrorDescriptor syncSourceError) throws
        IntrospectionException {
        super(new Object());
        setIconBaseWithExtension(Constants.ICON_SYNCSOURCE_EXCEPTION_NODE);

        this.Sync4jModule = Sync4jModule;
        this.sync4jSourceType = sync4jSourceType;
        this.Sync4jConnector = connector;

        this.syncSourceError = syncSourceError;

        if (syncSourceError != null) {
            this.sourceUri = syncSourceError.getUri();
        } else {
            this.sourceUri = "";
        }

        setName(this.sourceUri);
        setDisplayName(this.sourceUri);

        // register this node as syncSourceEventListener
        ExplorerUtil.getSyncAdminController().getSyncSourcesController().addSyncSourceEventListener(this);
    }

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
            NodeAction.get(DeleteSyncSource.class),
            null,
            NodeAction.get(RefreshAction.class)
        };

    }

    //-------------------------------------------------------
    // Overrides org.openide.nodes.AbstractNode method
    //-------------------------------------------------------
    /**
     * Set action to perform on double click on this node
     * @return Action update syncSource action
     */
    public Action getPreferredAction() {
        return NodeAction.get(ShowSyncSourceException.class);
    }

    //---------------------------------------------------------
    // Overrides org.openide.nodes.AbstractNode method
    //---------------------------------------------------------
    /**
     * Returns a help context for the node
     * @return context help associated with this node
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Called when a new syncSource is added
     * @param syncSource the syncSource added
     */
    public void syncSourceAdded(SyncSource syncSource) {
    }

    /**
     * Called when a syncSource is updated
     * @param syncSource the syncSource updated
     */
    public void syncSourceUpdated(SyncSource syncSource) {
    }

    /**
     * Called when a syncSource is deleted
     * @param syncSource the syncSource deleted
     */
    public void syncSourceDeleted(String sourceUri) {
    }

    /**
     * Returns the moduleId of the Sync4jModule that contains
     * the syncSource associated to this node
     *
     * @return String the moduleId of the Sync4jModule that contains
     *                the syncSource associated to this node
     */
    public String getModuleId() {
        return Sync4jModule.getModuleId();
    }

    /**
     * Returns the connectorId of the Sync4jConnector that contains
     * the syncSource associated to this node
     *
     * @return String the connectorId of the Sync4jConnector that contains
     *                the syncSource associated to this node
     */
    public String getConnectorId() {
        return Sync4jConnector.getConnectorId();
    }

    /**
     * Returns the syncSourceTypeId of the SyncSourceType of the syncSource associated to this node
     *
     * @return String the syncSourceTypeId of the SyncSourceType of the syncSource associated to this node
     */
    public String getSyncSourceTypeId() {
        return sync4jSourceType.getSourceTypeId();
    }

    /**
     * Performs the refresh of the module node that contains this node
     */
    public void refresh() {
        // refresh parent
        ( (SyncTypeNode)this.getParentNode()).refresh();
    }

}
