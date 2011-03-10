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

package com.funambol.admin.ui;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.openide.windows.TopComponent;

/**
 * Represents a <code>TopComponent</code> where the application panels are shown.<br/>
 * Supplies methods for:
 * <ul>
 *    <li>Add new panel</li>
 *    <li>Remove panel: if remove the visible panel, the last panel added is shown.<br/>
 *        If there are not panels, the <code>MainPanel</code> is shown.
 *    </li>
 *    <li>Show panel</li>
 * </ul>
 *
 *
 * @version $Id: MainTopComponent.java,v 1.12 2007-11-28 10:28:18 nichele Exp $
 */

public class MainTopComponent extends TopComponent {

    // ------------------------------------------------------------ Private data

    /** List of the panels added */
    private Vector panelsList = null;

    /** Panel currently visible */
    private JComponent visiblePanel = null;

    /** Main panel shown when there are not panel to show */
    private MainPanel mainPanel = null;

    private JScrollPane scrollPane = new JScrollPane(
        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );

    private static final String MAIN_TOPCOMPONENT_NAME = "Admin";


    // ------------------------------------------------------------- Constructor

    /**
     * Create a new MainTopComponent with the default name
     * @param name name of the topComponent
     */
    public MainTopComponent() {
        this(MAIN_TOPCOMPONENT_NAME);
        this.setName(MAIN_TOPCOMPONENT_NAME);
        this.setDisplayName(MAIN_TOPCOMPONENT_NAME);
        PanelManager.init(this);
    }

    /**
     * Create a new MainTopComponent with the given name
     * @param name name of the topComponent
     */
    public MainTopComponent(String name) {
        init();
        panelsList = new Vector();
        this.setName(name);
        this.setDisplayName(name);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        add(scrollPane, BorderLayout.CENTER);

        mainPanel = new MainPanel();
        mainPanel.setName(name);

        addPanel(mainPanel);
        showPanel(mainPanel.getName());
    }

    /**
     * Called when the TopComponent is shown
     */
    public void componentShowing() {
        if (visiblePanel instanceof MainPanel) {
            this.setName(mainPanel.getName());
            this.setDisplayName(mainPanel.getName());
        }
    }

    /**
     * Add the given panel.
     * @param panelToAdd panel to add
     */
    public void addPanel(JPanel panelToAdd) {
        panelToAdd.setVisible(false);
        panelsList.add(panelToAdd);
    }

    /**
     * Remove the given panel.
     * @param panelToRemove panel to remove
     * @return the number of the panels in the lists
     */
    public int removePanel(JPanel panelToRemove) {
        panelsList.remove(panelToRemove);

        JPanel newVisiblePanel = null;

        if (panelToRemove.isVisible()) {
            if (panelsList.size() > 0) {
                newVisiblePanel = (JPanel)panelsList.lastElement();
            }
        }

        if (newVisiblePanel != null) {
            showPanel(newVisiblePanel);
        }

        return panelsList.size();
    }

    /**
     * Remove all panels
     */
    public void removeAllPanels() {
        Iterator itPanels = panelsList.iterator();

        JPanel panelToRemove = null;
        while (itPanels.hasNext()) {
            panelToRemove = (JPanel)itPanels.next();
            if (panelToRemove != mainPanel) {
                itPanels.remove();
                if (panelToRemove.isVisible()) {
                    panelToRemove.setVisible(false);
                }
            }
        }

        // show mainPanel
        showPanel(mainPanel);
    }

    /**
     * Shows panel with the given name.
     * @param name name of the panel to show
     */
    public void showPanel(String name) {
        JPanel newVisiblePanel = getPanel(name);
        if (newVisiblePanel != null) {
            showPanel(newVisiblePanel);
        }
    }

    /**
     * Shows the given panel
     * @param panel JPanel
     */
    public void showPanel(JPanel panel) {
        if (panel == null) {
            return ;
        }

        if (visiblePanel != null) {
            scrollPane.remove(visiblePanel);
        }

        setName(panel.getName());
        setDisplayName(panel.getName());

        panel.setVisible(true);

        //this check is done to avoid some refresh problem
        //when a panel is removed and the MainPanel is shown
        if(panel instanceof MainPanel){
            panel = new MainPanel();
        }
        scrollPane.setViewportView(panel);

        visiblePanel = panel;

    }

    /**
     * Returns the panel with the given name.
     * @param name name of the panel
     * @return panel with the given name
     */
    public JPanel getPanel(String name) {
        int numPanel = panelsList.size();
        String panelName  = null;
        JPanel panelFound = null;
        JPanel panel      = null;
        Object comp       = null;

        for (int i = 0; i < numPanel; i++) {
            comp = panelsList.elementAt(i);

            if (!(comp instanceof JPanel)) {
                continue;
            }
            panel = (JPanel)comp;
            panelName = panel.getName();

            if (name.equals(panelName)) {
                panelFound = panel;

                //
                // Remove the panel in order to re-add it at the end of the
                // panel's list. In this way, the last element is effectively
                // the last panel shown otherwise the Cancel button doesn't work
                // rightly. (see also removePanel())
                //
                panelsList.remove(i);
                panelsList.add(panel);

                break;
            }
        }

        return panelFound;
    }

    /**
     * Returns the number of the panels in the list.
     * @return number of the panels in the list
     */
    public int getNumPanels() {
        return panelsList.size();
    }

    /**
     * Overriden to explicitely set persistence type of FeedTopComponent
     * to PERSISTENCE_ALWAYS */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    //---------------------------------------------------------
    // Overrides org.openide.windows.TopComponent
    //---------------------------------------------------------

    /**
     * This method is called when top component is about to close.
     * Always return false.
     * @param ws the workspace on which we are about to close or null which means that component will be closed on all workspaces where it is opened
     * @param last <code>true</code> if this is last workspace where top component is opened, false otherwise
     * @return always return false
     */
    public boolean canClose() {
        return false;
    }

    // ------------------------------------------------------- Protected methods

    protected String preferredID() {
        return "mainpanel";
    }

    // --------------------------------------------------------- Private methods

    private void init() {
        this.setLayout(new BorderLayout());
    }

}
