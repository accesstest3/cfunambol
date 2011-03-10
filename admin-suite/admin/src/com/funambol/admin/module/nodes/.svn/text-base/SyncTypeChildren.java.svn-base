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

import javax.swing.SwingUtilities;

import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceErrorDescriptor;
import com.funambol.framework.server.Sync4jSourceType;

import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.framework.server.Sync4jConnector;
import com.funambol.framework.server.Sync4jModule;

import org.openide.nodes.Children;
import org.openide.nodes.Children.Array;
import org.openide.nodes.Node;

/**
 * List of children of a containing node.
 * This is a simple Children.Array that loads on addNotify. <br/>
 * Rapresents a list of SyncSource.
 * 
 * @version $Id: SyncTypeChildren.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class SyncTypeChildren extends Children.Array {

    // ------------------------------------------------------------ Private data

    /** Sync4jModule associated with this node */
    private Sync4jModule Sync4jModule;

    /** Sets Sync4jModule */
    public void setSync4jModule(Sync4jModule Sync4jModule) {
        this.Sync4jModule = Sync4jModule;
    }

    /** Sync4jConnector associated with this node */
    private Sync4jConnector Sync4jConnector;

    /** Sets Sync4jModule */
    public void setSync4jConnector(Sync4jConnector Sync4jConnector) {
        this.Sync4jConnector = Sync4jConnector;
    }

    /** Sync4jSourceType associated with this node */
    private Sync4jSourceType sync4jSourceType;

    /** Sets Sync4jModule */
    public void setSync4jSourceType(Sync4jSourceType sync4jSourceType) {
        this.sync4jSourceType = sync4jSourceType;
    }

    // ------------------------------------------------------- Protected Methods

    //-------------------------------------------------------
    // Overrides org.openide.nodes.Children method
    //-------------------------------------------------------

    /**
     * Called when children are first asked for nodes
     */
    protected void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loadChildren();
            }
        });
    }

    /**
     * Add a new child for the new syncSource.
     *
     * @param syncSource new syncSource
     */
    protected void addSyncSource(SyncSource syncSource) {
        try {
            Node node = new SyncSourceNode(Sync4jModule, Sync4jConnector, sync4jSourceType,
                                           syncSource);

            this.add(new Node[] {node});
        } catch (IntrospectionException e) {
            Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
        }
    }

    /**
     * Remove the child associated with the syncSource identified with the given sourceUri.
     *
     * @param sourceUri sourceUri of the syncSource to remove
     */
    protected void removeSyncSource(String sourceUri) {
        Node node = this.findChild(sourceUri);
        if (node != null) {
            Node[] nodesToRemove = new Node[] {node};
            this.remove(nodesToRemove);
        }
    }

    // --------------------------------------------------------- Private Methods
    /**
     * Load children.
     * Add all syncSource and all syncSourceException.
     */
    private void loadChildren() {

        SyncSource[] syncSources = null;

        syncSources = sync4jSourceType.getSyncSources();

        if (syncSources != null) {
            int numSource = syncSources.length;
            Node[] nodes = new Node[numSource];

            for (int i = 0; i < numSource; i++) {
                try {
                    nodes[i] = new SyncSourceNode(Sync4jModule, Sync4jConnector, sync4jSourceType,
                                                  syncSources[i]);
                } catch (IntrospectionException e) {
                    Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
                }
            }
            this.add(nodes);
        }

        SyncSourceErrorDescriptor[] syncSourceException = sync4jSourceType.getSyncSourcesFailed();
        if (syncSourceException != null) {
            int numSourceException = syncSourceException.length;
            Node[] nodes = new Node[numSourceException];

            for (int i = 0; i < numSourceException; i++) {
                try {
                    nodes[i] = new SyncSourceExceptionNode(Sync4jModule, Sync4jConnector,
                        sync4jSourceType,
                        syncSourceException[i]);
                } catch (IntrospectionException e) {
                    Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
                }
            }
            this.add(nodes);
        }

        Log.debug("Found " + syncSources.length + " syncSource");
        Log.debug("Found " + sync4jSourceType.getSyncSourcesFailed().length + " exception");

    }

}
