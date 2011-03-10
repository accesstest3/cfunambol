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
package com.funambol.admin.module.panels;

import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.funambol.framework.engine.source.SyncSourceErrorDescriptor;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Panel used to show the error message associated with a SyncSourceException
 *
 * @version $Id: ShowSyncSourceExceptionPanel.java,v 1.8 2007-11-28 10:28:19 nichele Exp $
 */
public class ShowSyncSourceExceptionPanel extends JPanel implements Serializable {

    // ------------------------------------------------------------ Private data
    /** label for the panel's name */
    private JLabel panelName = null;

    /** border to evidence the title of the panel */
    private TitledBorder titledBorder1;

    private JLabel errorMessageLabel = new JLabel();

    // ------------------------------------------------------------ Constructors

    /**
     * Create a new ShowSyncSourceExceptionPanel
     */
    public ShowSyncSourceExceptionPanel() {
        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + getClass().getName(), e);
        }
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Set the errorMessageLabel with the message of the given exception
     *
     * @param syncSourceException SyncSourceException
     */
    public void loadSyncSourceException(SyncSourceErrorDescriptor syncSourceException) {
        String errorMessage = syncSourceException.getDescription();
        Log.debug("Error message: " + errorMessage);
        Log.error(syncSourceException.getFullStackTrace());

        if (errorMessage != null) {
            errorMessage="<html>"+errorMessage.replaceFirst(":",":<br>")+"</html>";            
            errorMessageLabel.setText(errorMessage);
        } else {
            errorMessageLabel.setText(Bundle.getMessage(Bundle.
                SYNC_SOURCE_EXCEPTION_NO_MESSAGE_AVAILABLE));
        }

    }

    // --------------------------------------------------------- Private methods
    /**
     * Creates the panel
     */
    private void init() {
        // set layout
        this.setLayout(null);

        // set properties of label, position and border
        //  referred to the title of the panel
        titledBorder1 = new TitledBorder("");

        ImageIcon icon = createImageIcon(Constants.ICON_SYNCSOURCE_EXCEPTION_NODE);

        panelName = new JLabel(icon, JLabel.LEFT);
        panelName.setFont(GuiFactory.titlePanelFont);
        panelName.setText(Bundle.getMessage(Bundle.SHOW_SYNCSOURCE_EXCEPTION_TITLE));
        panelName.setBounds(new Rectangle(14, 5, 316, 28));
        panelName.setAlignmentX(SwingConstants.CENTER);
        panelName.setBorder(titledBorder1);

        errorMessageLabel.setFont(GuiFactory.defaultFont);
        errorMessageLabel.setBounds(new Rectangle(14, 53, 500, 48));

        this.add(panelName, null);
        this.add(errorMessageLabel, null);
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    private ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = this.getClass().getClassLoader().getResource(path);

        if (imgURL == null) {
            // try gif image
            path = path + ".gif";
            imgURL = this.getClass().getClassLoader().getResource(path);
        }

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            return null;
        }
    }

}
