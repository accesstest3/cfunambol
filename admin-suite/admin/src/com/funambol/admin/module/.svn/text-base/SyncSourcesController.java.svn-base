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
package com.funambol.admin.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceErrorDescriptor;
import com.funambol.framework.engine.source.SyncSourceInfo;
import com.funambol.framework.engine.transformer.DataTransformerManager;
import com.funambol.framework.filter.*;
import com.funambol.framework.server.Sync4jSource;

import com.funambol.server.config.ServerConfiguration;

import com.funambol.admin.AdminException;
import com.funambol.admin.main.BusinessDelegate;
import com.funambol.admin.main.SyncAdminController;
import com.funambol.admin.mo.SyncSourceManagementObject;
import com.funambol.admin.module.nodes.SyncSourceExceptionNode;
import com.funambol.admin.module.nodes.SyncSourceNode;
import com.funambol.admin.module.nodes.SyncTypeNode;
import com.funambol.admin.module.panels.DefaultSyncSourceConfigPanel;
import com.funambol.admin.module.panels.ShowSyncSourceExceptionPanel;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.ui.ManagementObjectPanel;
import com.funambol.admin.ui.SourceManagementPanel;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Manages all processes about the SyncSources.
 *
 * @version $Id: SyncSourcesController.java,v 1.15 2007-11-28 10:28:18 nichele Exp $
 */
public class SyncSourcesController
implements ActionListener {

    // --------------------------------------------------------------- Constants
    /** constant for search query identifing source uri */
    private static String PARAM_SOURCE_URI  = "uri";

    /** constant for search query identifing source name */
    private static String PARAM_SOURCE_NAME = "name";

    // ------------------------------------------------------------ Private data
    private BusinessDelegate businessDelegate = null;

    /** List of the <code>SyncSourceEventListener</code> */
    private Vector syncSourceEventListeners = null;

    /** List of the panels concerning to modules. Used during refresh */
    private ArrayList modulesPanelList = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new SyncSourceControlles with the given <code>BusinessDelegate</code>
     * @param businessDelegate BusinessDelegate to access to the business methods.
     */
    public SyncSourcesController(BusinessDelegate businessDelegate) {
        this.businessDelegate = businessDelegate;
        syncSourceEventListeners = new Vector();
        modulesPanelList = new ArrayList();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Adds a <code>SyncSourceEventListener</code> to receive syncSource events
     * @param syncSourceEventListener SyncSourceEventListener syncSoourceEventListener to add
     */
    public void addSyncSourceEventListener(SyncSourceEventListener syncSourceEventListener) {
        syncSourceEventListeners.add(syncSourceEventListener);
    }

    /**
     * Removes a <code>SyncSourceEventListener</code>
     * @param syncSourceEventListener SyncSourceEventListener syncSoourceEventListener to remove
     */
    public void removeSyncSourceEventListener(SyncSourceEventListener syncSourceEventListener) {
        syncSourceEventListeners.remove(syncSourceEventListener);
    }

    /**
     * Insert the given syncSource.
     *
     * @param mo the ManagementObject wrapping the SyncSource to instert
     *
     * @throws AdminException in case of errors
     */
    public void insertSyncSource(SyncSourceManagementObject mo)
    throws AdminException {
        String moduleId       = mo.getModuleId();
        String connectorId    = mo.getConnectorId();
        String sourceTypeId   = mo.getSourceTypeId();

        SyncSource syncSource = (SyncSource)mo.getObject();

        try {
            // check if sourceURI already exists
            String sourceUri = syncSource.getSourceURI();
            String name      = syncSource.getName();

            Clause clause = getClauseForSync4jSourceUriAndNameCheck(sourceUri, name);

            Sync4jSource[] sources = null;
            sources = businessDelegate.getSync4jSources(clause);

            if (sources.length > 0) {
                throw new AdminException(
                    MessageFormat.format(
                        Bundle.getMessage(Bundle.SYNC_SOURCE_MESSAGE_URI_OR_NAME_EXISTS),
                        new String[] {sourceUri, name}
                    )
                );
            }

            businessDelegate.addSource(
                moduleId, connectorId, sourceTypeId, syncSource
            );

            Log.info(Bundle.getMessage(Bundle.SYNC_SOURCE_MESSAGE_INSERT_OK));
            notifySyncSourceAdded(syncSource, moduleId, connectorId, sourceTypeId);

        } catch (AdminException e) {
            Log.error(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_HANDLING_SYNCSOURCE),
                    new String[] {Bundle.getMessage(Bundle.ADDING)}
                ), e
            );

            throw e;
        }

    }

    /**
     * Update the given syncSource.
     *
     * @param mo the ManagementObject wrapping the SyncSource to update
     *
     * @throws AdminException in case of errors
     */
    public void updateSyncSource(SyncSourceManagementObject mo)
    throws AdminException {
        String moduleId       = mo.getModuleId();
        String connectorId    = mo.getConnectorId();
        String sourceTypeId   = mo.getSourceTypeId();

        SyncSource syncSource = (SyncSource)mo.getObject();

        try {

            // check if the name is already used by another syncsource
            String sourceUri = syncSource.getSourceURI();
            String name      = syncSource.getName();

            Clause clause = getClauseForSync4jSourceNameCheck(sourceUri, name);

            Sync4jSource[] sources = null;
            sources = businessDelegate.getSync4jSources(clause);

            if (sources.length > 0) {

                throw new AdminException(
                    MessageFormat.format(
                        Bundle.getMessage(Bundle.SYNC_SOURCE_MESSAGE_NAME_EXISTS),
                        new String[] {name}
                    )
                );
            }


            businessDelegate.setSource( moduleId     ,
                                        connectorId  ,
                                        sourceTypeId ,
                                        syncSource   );

            Log.info(Bundle.getMessage(Bundle.SYNC_SOURCE_MESSAGE_UPDATED_OK));

            notifySyncSourceUpdated(syncSource, moduleId, connectorId, sourceTypeId);
        } catch (AdminException e) {
            Log.error(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_HANDLING_SYNCSOURCE),
                    new String[] {Bundle.getMessage(Bundle.UPDATING)}
                ), e
            );

            throw e;
        }
    }

    /**
     * Delete syncSource with the given sourceUri.
     *
     * @param syncTypeNode SyncTypeNode involved
     * @param sourceUri sourceUri of the syncSource to delete
     * @return boolean true if the syncSource is deleted, false otherwise
     */
    public boolean deleteSource(SyncTypeNode syncTypeNode, String sourceUri) {

        boolean syncSourceDeleted = false;

        if (!showConfirmDelete()) {
            return syncSourceDeleted;
        }

        String moduleDescription = syncTypeNode.getSync4jModule().getDescription();
        String connectorDescription = syncTypeNode.getSync4jConnector().getDescription();
        String sync4jSourceTypeDescription = syncTypeNode.getSync4jSourceType().getDescription();

        String moduleId = syncTypeNode.getModuleId();
        String connectorId = syncTypeNode.getConnectorId();
        String sourceTypeId = syncTypeNode.getSyncSourceTypeId();

        try {

            deleteTransformationsRequired(sourceUri);
            deleteSource(moduleId, connectorId, sourceTypeId, sourceUri);


            String panelName = moduleDescription + " - " + connectorDescription + " - " +
                               sync4jSourceTypeDescription + " - " + sourceUri;

            JPanel syncSourcePanel = PanelManager.getPanel(panelName);
            if (syncSourcePanel != null) {
                PanelManager.removePanel(syncSourcePanel);
            }

        } catch (AdminException e) {
            Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
        }

        return syncSourceDeleted;

    }

    /**
     * Delete the syncSource with the given sourceUri.
     *
     * @param moduleId module id of the syncSource to delete
     * @param connectorId connector id of the syncSource to delete
     * @param syncSourceTypeId syncSourceType id of the syncSource to delete
     * @param sourceUri source uri of the syncSource to delete
     * @throws AdminException if a error occurs during delete
     * @return boolean true if the syncCurce is deleted, false otherwise
     */
    private boolean deleteSource(String moduleId, String connectorId, String syncSourceTypeId,
                                 String sourceUri) throws AdminException {

        boolean syncSourceDeleted = false;

        businessDelegate.deleteSource(sourceUri);

        syncSourceDeleted = true;

        Log.info(Bundle.getMessage(Bundle.SYNC_SOURCE_MESSAGE_DELETED_OK));

        notifySyncSourceDeleted(sourceUri, moduleId, connectorId, syncSourceTypeId);

        return syncSourceDeleted;

    }

    /**
     * Delete the sync source wrapped by the given management object
     *
     * @param mo the ManagementObject representing the SyncSource to instert
     *
     * @throws AdminException in case of errors
     */
    public void deleteSyncSource(SyncSourceManagementObject mo)
    throws AdminException {

        if (!showConfirmDelete()) {
            return;
        }

        String moduleId       = mo.getModuleId();
        String connectorId    = mo.getConnectorId();
        String sourceTypeId   = mo.getSourceTypeId();

        SyncSource syncSource = (SyncSource)mo.getObject();

        try {
            String sourceUri = syncSource.getSourceURI();

            deleteSource(moduleId, connectorId, sourceTypeId, sourceUri);

            Log.info(Bundle.getMessage(Bundle.SYNC_SOURCE_MESSAGE_DELETED_OK));

            notifySyncSourceDeleted(sourceUri, moduleId, connectorId, sourceTypeId);

        } catch (AdminException e) {
            Log.error(
                MessageFormat.format(
                    Bundle.getMessage(Bundle.ERROR_HANDLING_SYNCSOURCE),
                    new String[] {Bundle.getMessage(Bundle.DELETING)}
                ), e
            );

            throw e;
        }
    }

    /**
     * Starts the process to create a new SyncSouce
     *
     * @param node SyncTypeNode involved in the process
     * @throws AdminException if a error occurs during the process
     */
    public void startInsertNewSourceProcess(SyncTypeNode node)
    throws AdminException {

        String moduleId = node.getSync4jModule().getModuleId();
        String moduleDescription = node.getSync4jModule().getDescription();

        String connectorId = node.getSync4jConnector().getConnectorId();
        String connectorDescription = node.getSync4jConnector().getDescription();

        String sourceTypeId = node.getSync4jSourceType().getSourceTypeId();
        String sourceTypeDescription = node.getSync4jSourceType().getDescription();

        SyncAdminController syncNode = ExplorerUtil.getSyncAdminController();

        // new syncSource
        String className = node.getSync4jSourceType().getClassName();
        Log.debug("Sync4jSource ClassName: " + className);

        Class syncSourceClass = null;
        SyncSource syncSource = null;

        ClassLoader cl = syncNode.getSync4jClassLoader();

        try {
            syncSourceClass = cl.loadClass(className);
            syncSource = (SyncSource)syncSourceClass.newInstance();
        } catch (Exception e) {
            String errorMessage = MessageFormat.format(
                Bundle.getMessage(Bundle.ERROR_LOADING_SYNCSOURCE_TYPE),
                new String[] {className}) + " [" + e.getMessage() + "]";

            NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);
            DialogDisplayer.getDefault().notify(desc);
            return;
        }

        Log.debug("ConfigClassInstance: " + syncSource);

        String panelName = moduleDescription
                         + " - "
                         + connectorDescription
                         + " - "
                         + sourceTypeDescription
                         ;

        SyncSourceManagementObject mo = new SyncSourceManagementObject(
                                                syncSource,
                                                moduleId,
                                                connectorId,
                                                sourceTypeId,
                                                null
                                        );

        String adminClassName = node.getSync4jSourceType().getAdminClass();
        Log.debug("admin class name: " + adminClassName);

        SourceManagementPanel panel =
            (SourceManagementPanel)PanelManager.getPanel(panelName);

        if (panel != null) {
            // load the panel with a 'not configured' syncSource
            panel.setManagementObject(mo);
            panel.updateForm();
            PanelManager.showPanel(panelName);
        } else {
            if ((adminClassName == null) || (adminClassName.trim().length() == 0)) {
                //
                // There is no configuration panel for this SyncSource... so
                // show the default configuration panel
                //
                panel = (SourceManagementPanel)PanelManager.getPanel(panelName);
                if (panel != null) {
                    PanelManager.showPanel(panelName);
                } else {

                    //
                    // If the SyncSource to configure has not the methods
                    // setSourceURI, setName and setInfo, then it's not possible
                    // to configure it.
                    //
                    if (!checkSyncSourceMethods(syncSource)) {
                        String errorMessage =
                            Bundle.getMessage(Bundle.SYNC_SOURCE_MESSAGE_INSERT_NOT_CONFIGURABLE);
                        Log.error(errorMessage);
                        NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);
                        DialogDisplayer.getDefault().notify(desc);
                        return;
                    }

                    panel = new DefaultSyncSourceConfigPanel(sourceTypeDescription);
                    panel.setName(panelName);
                }
            }

            if (adminClassName != null) {
                Class managementPanelClass = null;

                try {
                    managementPanelClass = cl.loadClass(adminClassName);
                    panel = (SourceManagementPanel)managementPanelClass.newInstance();
                } catch (Throwable e) {
                    String errorMessage = Bundle.getMessage(Bundle.ERROR_LOADING_CONFIG_PANEL);
                    Log.error(errorMessage + " (class name: " + adminClassName + ")");
                    Log.error(e);
                    NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);
                    DialogDisplayer.getDefault().notify(desc);
                    return;
                }
            }

            // add the panel to the list of the panels concerning to modules
            modulesPanelList.add(panel);
            panel.setState(SourceManagementPanel.STATE_INSERT);
            panel.setName(panelName);
            panel.setManagementObject(mo);
            panel.addActionListener(this);

            //
            // Tell the panel to rewrite its content
            //
            panel.updateForm();

            panel.validate();

            PanelManager.addPanel(panel);
        }
    }

    /**
     * Shows the exception relative to the given SyncSourceExceptionNode
     * @param node SyncSourceExceptionNode with the exception
     */
    public void showSyncSourceException(SyncSourceExceptionNode node)  {
        SyncSourceErrorDescriptor error = node.getSyncSourceError();

        String sourceUri = error.getUri();

        String moduleDescription = node.getSync4jModule().getDescription();

        String connectorDescription = node.getSync4jConnector().getDescription();

        String sync4jSourceTypeDescription = node.getSync4jSourceType().getDescription();

        SyncAdminController syncNode = ExplorerUtil.getSyncAdminController();

        String panelName = "ERROR - " + moduleDescription + " - " + connectorDescription + " - " +
                           sync4jSourceTypeDescription + " - " + sourceUri;

        JPanel panel = PanelManager.getPanel(panelName);
        if (panel != null) {
            ( (ShowSyncSourceExceptionPanel)panel).loadSyncSourceException(error);
            PanelManager.showPanel(panelName);
        } else {
            panel = new ShowSyncSourceExceptionPanel();
            panel.setName(panelName);
            ( (ShowSyncSourceExceptionPanel)panel).loadSyncSourceException(error);
            panel.validate();
            PanelManager.addPanel(panel);
        }
    }

    /**
     * Starts the update process of the syncSource of the given SyncSourceNode
     *
     * @param node SyncSourceNode relative to the syncSource to update
     * @throws AdminException if a errors occurs
     */
    public void startUpdateSourceProcess(SyncSourceNode node)
    throws AdminException {

        SyncSource syncSource = node.getSyncSource();
        Log.debug("SyncSource instance: " + syncSource);

        ClassLoader classLoader = syncSource.getClass().getClassLoader();

        String sourceUri = syncSource.getSourceURI();

        String moduleId = node.getSync4jModule().getModuleId();
        String moduleDescription = node.getSync4jModule().getDescription();

        String connectorId = node.getSync4jConnector().getConnectorId();
        String connectorDescription = node.getSync4jConnector().getDescription();

        String sourceTypeId = node.getSync4jSourceType().getSourceTypeId();
        String sourceTypeDescription = node.getSync4jSourceType().getDescription();

        String panelName = moduleDescription
                         + " - "
                         + connectorDescription
                         + " - "
                         + sourceTypeDescription
                         + " - "
                         + sourceUri
                         ;

        SourceManagementPanel panel =
            (SourceManagementPanel)PanelManager.getPanel(panelName);

        String transformationsRequired = getTransformationsRequired(sourceUri);

        SyncSourceManagementObject mo = new SyncSourceManagementObject(
                                                syncSource,
                                                moduleId,
                                                connectorId,
                                                sourceTypeId,
                                                transformationsRequired
                                        );

        String adminClassName = node.getSync4jSourceType().getAdminClass();
        Log.debug("admin class name: " + adminClassName);

        if (panel != null) {
            panel.setManagementObject(mo);
            panel.updateForm();
            PanelManager.showPanel(panelName);
        } else {

            if ((adminClassName == null) || (adminClassName.trim().length() == 0)) {
                //
                // There is no configuration panel for this SyncSource... so
                // show the default configuration panel
                //
                panel = (SourceManagementPanel)PanelManager.getPanel(panelName);
                if (panel != null) {
                    PanelManager.showPanel(panelName);
                } else {

                    //
                    // If the SyncSource to configure has not the methods
                    // setSourceURI, setName and setInfo, then it's not possible
                    // to configure it.
                    //
                    if (!checkSyncSourceMethods(syncSource)) {
                        String errorMessage =
                            Bundle.getMessage(Bundle.SYNC_SOURCE_MESSAGE_NOT_CONFIGURABLE);
                        Log.error(errorMessage);
                        NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);
                        DialogDisplayer.getDefault().notify(desc);
                        return;
                    }

                    panel = new DefaultSyncSourceConfigPanel(sourceTypeDescription);
                    panel.setName(panelName);
                }
            }

            if (adminClassName != null) {
                Class managementPanelClass = null;

                try {
                    managementPanelClass = classLoader.loadClass(adminClassName);
                    panel = (SourceManagementPanel)managementPanelClass.newInstance();
                } catch (Exception e) {
                    String errorMessage = Bundle.getMessage(Bundle.ERROR_LOADING_CONFIG_PANEL);
                    Log.error(errorMessage + " (class name:" + adminClassName + ")");
                    Log.error(e);
                    NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);
                    DialogDisplayer.getDefault().notify(desc);
                    return;
                }
            }

            // add the panel to the list of the panels concerning to modules
            modulesPanelList.add(panel);

            panel.setState(SourceManagementPanel.STATE_UPDATE);

            panel.setManagementObject(mo);
            panel.setName(panelName);
            panel.addActionListener(this);

            panel.updateForm();

            // FINAL OPERATION with new TopComponent to permit a correct
            // visualization in the DAG
            panel.validate();

            PanelManager.addPanel(panel);
        }
    }

    /**
     * Called during the refresh of the modules.<br/>
     * Remove all panel of the modules to the MainTopComponent
     */
    public void refreshModules() {
        // remove all panel concerning to modules

        Iterator it = modulesPanelList.iterator();
        while (it.hasNext()) {
            PanelManager.removePanel( (JPanel)it.next());
            it.remove();
        }

    }

    // ------------------------------------------- ActionListener implementation

    /**
     * Invoked when an action occurs.
     *
     * @param e the ActionEvent
     */
     public void actionPerformed(ActionEvent event) {
         Object o = event.getSource();

         if ((o == null) || !(o instanceof ManagementObjectPanel)) {
             Log.error(Bundle.getMessage(Bundle.ERROR_INVALID_ACTION_EVENT));
             return;
         }

         SourceManagementPanel syncSourcepanel = (SourceManagementPanel)o;
         SyncSourceManagementObject mo =
             (SyncSourceManagementObject)syncSourcepanel.getManagementObject();

         SyncSource syncSource = null;
         try {
             switch (event.getID()) {
                 case ManagementObjectPanel.ACTION_EVENT_INSERT:

                     insertSyncSource(mo);

                     syncSource = (SyncSource)mo.getObject();
                     setTransformationsRequired(syncSource.getSourceURI(),
                                                mo.getTransformationsRequired());

                     syncSourcepanel.setName(syncSourcepanel.getName() +
                                             " - " +
                                             syncSource.getSourceURI());

                     syncSourcepanel.setState(SourceManagementPanel.STATE_UPDATE);

                     syncSourcepanel.updateForm(); // so that the panel has the opportunity to reflect
                                     // the new state
                     PanelManager.showPanel(syncSourcepanel.getName());

                     break;

                 case ManagementObjectPanel.ACTION_EVENT_UPDATE:
                     updateSyncSource(mo);

                     syncSource = (SyncSource)mo.getObject();

                     setTransformationsRequired(syncSource.getSourceURI(),
                                                mo.getTransformationsRequired());

                     break;

                 case ManagementObjectPanel.ACTION_EVENT_DELETE:
                     deleteSyncSource(mo);
                     syncSource = (SyncSource)mo.getObject();
                     deleteTransformationsRequired(syncSource.getSourceURI());

                     PanelManager.removePanel(syncSourcepanel);
                     modulesPanelList.remove(syncSourcepanel);
                     break;
             }
         } catch (AdminException e) {
             syncSourcepanel.notifyError(e);
         }

     }

    // --------------------------------------------------------- Private Methods

    /**
     * Sets on the server the given transformationsRequired for the given sourceUri
     * @param sourceUri the source uri
     * @param transformationsRequired the transformations required
     */
    private void setTransformationsRequired(String sourceUri, String transformationsRequired)
    throws AdminException {
        ServerConfiguration serverConfig = businessDelegate.getServerConfiguration();
        String dataTransformerManagerBean =
            serverConfig.getEngineConfiguration().getDataTransformerManager();

        DataTransformerManager transformerManager =
            (DataTransformerManager)businessDelegate.getServerBean(dataTransformerManagerBean);

        transformerManager.getSourceUriTrasformationsRequired().put(sourceUri, transformationsRequired);

        businessDelegate.setServerBean(dataTransformerManagerBean, transformerManager);
    }

    /**
     * Deletes on the server the transformationsRequired for the given sourceUri
     * @param sourceUri the source uri
     */
    private void deleteTransformationsRequired(String sourceUri)
    throws AdminException {
        ServerConfiguration serverConfig = businessDelegate.getServerConfiguration();
        String dataTransformerManagerBean =
            serverConfig.getEngineConfiguration().getDataTransformerManager();

        DataTransformerManager transformerManager =
            (DataTransformerManager)businessDelegate.getServerBean(dataTransformerManagerBean);

        transformerManager.getSourceUriTrasformationsRequired().remove(sourceUri);

        businessDelegate.setServerBean(dataTransformerManagerBean, transformerManager);
    }

    /**
     * Gets from the server the transformationsRequired for the given sourceUri
     * @param sourceUri the source uri
     */
    private String getTransformationsRequired(String sourceUri)
    throws AdminException {
        ServerConfiguration serverConfig = businessDelegate.getServerConfiguration();

        String dataTransformerManagerBean =
            serverConfig.getEngineConfiguration().getDataTransformerManager();

        DataTransformerManager transformerManager =
            (DataTransformerManager)businessDelegate.getServerBean(dataTransformerManagerBean);

        String transformationsRequired =
            (String)transformerManager.getSourceUriTrasformationsRequired().get(sourceUri);

        return transformationsRequired;

    }

    /**
     * Notify all syncSourceEventListeners when a new syncSource is added
     * @param syncSourceAdded SyncSource syncSource added
     */
    private void notifySyncSourceAdded(SyncSource syncSourceAdded, String moduleId,
                                       String connectorId, String sourceTypeId) {
        int numListeners = syncSourceEventListeners.size();
        SyncSourceEventListener listener = null;

        for (int i = 0; i < numListeners; i++) {
            listener = (SyncSourceEventListener)syncSourceEventListeners.elementAt(i);
            if (listener.getModuleId().equals(moduleId)) {
                if (listener.getConnectorId().equals(connectorId)) {
                    if (listener.getSyncSourceTypeId().equals(sourceTypeId)) {
                        listener.syncSourceAdded(syncSourceAdded);
                    }
                }
            }

        }
    }

    /**
     * Notify all syncSourceEventListeners when a syncSource is updated
     * @param syncSourceAdded SyncSource syncSource updated
     */
    private void notifySyncSourceUpdated(SyncSource syncSourceUpdated, String moduleId,
                                         String connectorId, String sourceTypeId) {
        int numListeners = syncSourceEventListeners.size();
        SyncSourceEventListener listener = null;

        for (int i = 0; i < numListeners; i++) {
            listener = (SyncSourceEventListener)syncSourceEventListeners.elementAt(i);
            if (listener.getModuleId().equals(moduleId)) {
                if (listener.getConnectorId().equals(connectorId)) {
                    if (listener.getSyncSourceTypeId().equals(sourceTypeId)) {
                        listener.syncSourceUpdated(syncSourceUpdated);
                    }
                }
            }
        }

    }

    /**
     * Notify all syncSourceEventListeners when a new syncSource is deleted
     * @param syncSourceAdded SyncSource syncSource deleted
     */
    private void notifySyncSourceDeleted(String sourceUri, String moduleId,
        String connectorId, String sourceTypeId) {
        int numListeners = syncSourceEventListeners.size();
        SyncSourceEventListener listener = null;

        for (int i = 0; i < numListeners; i++) {
            listener = (SyncSourceEventListener)syncSourceEventListeners.elementAt(i);
            if (listener.getModuleId().equals(moduleId)) {
                if (listener.getConnectorId().equals(connectorId)) {
                    if (listener.getSyncSourceTypeId().equals(sourceTypeId)) {
                        listener.syncSourceDeleted(sourceUri);
                    }
                }
            }

        }
    }

    /**
     * Display a notification message to ask user if he wants to delete value of
     * the selected syncsource.
     */
    private boolean showConfirmDelete() {
        boolean ret = false;
        // prepare notification message
        NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation(
            Bundle.getMessage(Bundle.SYNC_SOURCE_QUESTION_DELETE),
            Bundle.getMessage(Bundle.LABEL_BUTTON_DELETE),
            NotifyDescriptor.OK_CANCEL_OPTION);
        Object nnd = DialogDisplayer.getDefault().notify(nd);
        // ok , so delete user
        if (nd.getValue() == NotifyDescriptor.OK_OPTION) {
            ret = true;
        }
        return ret;
    }

    /**
     * Creates and returns the clause for the sync4jsource search
     * @param id String the value for the create clause
     * @return Clause
     */
    private Clause getClauseForSync4jSourceUriAndNameCheck(String uri, String name) {

        List clauses = new ArrayList();

        if (uri == null && name == null) {
            return null;
        }

        Clause uriClause  = null;
        Clause nameClause = null;

        if (uri != null) {
            uriClause = new WhereClause(PARAM_SOURCE_URI,
                new String[] {uri},
                WhereClause.OPT_EQ,
                false);
            clauses.add(uriClause);
        }

        if (name != null) {
            nameClause = new WhereClause(PARAM_SOURCE_NAME,
                new String[] {name},
                WhereClause.OPT_EQ,
                false);
            clauses.add(nameClause);
        }

        return new LogicalClause(LogicalClause.OPT_OR,
            (Clause[])clauses.toArray(new Clause[0]));
    }

    /**
     * Creates and returns the clause for the sync4jsource name check.
     * The clauses checks if there are some syncsource with the same name but with
     * different uri
     * @param uri the source uri
     * @param name the source name
     * @return Clause
     */
    private Clause getClauseForSync4jSourceNameCheck(String uri, String name) {

        List clauses = new ArrayList();

        if (uri == null && name == null) {
            return null;
        }

        Clause uriClause  = null;
        Clause nameClause = null;

        if (uri != null) {
            uriClause = new WhereClause(PARAM_SOURCE_URI,
                new String[] {uri},
                WhereClause.OPT_EQ,
                false);
            uriClause = new LogicalClause(LogicalClause.OPT_NOT, new Clause[] {uriClause});
            clauses.add(uriClause);
        }

        if (name != null) {
            nameClause = new WhereClause(PARAM_SOURCE_NAME,
                new String[] {name},
                WhereClause.OPT_EQ,
                false);
            clauses.add(nameClause);
        }

        return new LogicalClause(LogicalClause.OPT_AND,
            (Clause[])clauses.toArray(new Clause[0]));
    }

    /**
    * Checks if the SyncSource to configure has the methods
    * <code>setSourceURI</code>, <code>setName</code> and <code>setInfo</code>
    * since without them it's not possible to configure it.
    *
    * @param syncSource the SyncSource to configure
    * @return true if the methods exist, false otherwise.
    */
    private boolean checkSyncSourceMethods(SyncSource syncSource) {
        Class c = syncSource.getClass();

         try {
             Method setSourceURI =
                 c.getMethod("setSourceURI", new Class[]{String.class});
             Method setName =
                 c.getMethod("setName", new Class[]{String.class});
             Method setInfo =
                 c.getMethod("setInfo", new Class[]{SyncSourceInfo.class});

         } catch(NoSuchMethodException e) {
             return false;
         }
        return true;
     }

}
