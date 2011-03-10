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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.funambol.admin.main.SyncAdminController;

import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Rapresents the main panel shown when not application panels are visible.
 *
 *
 * @version $Id: MainPanel.java,v 1.8 2008-05-22 22:11:23 nichele Exp $
 */
public class MainPanel extends JPanel {

    private static String displayName = Bundle.getMessage(Bundle.EXPLORER_TITLE) +
                                        "  v." +
                                        Bundle.getMessage(Bundle.VERSION);
    // ------------------------------------------------------------ Private data
    private ImageIcon imageIcon = null;
    private Image image = null;
    private int imageWidth = 0;
    private int imageHeight = 0;

    // ------------------------------------------------------------ Construstors

    /**
     * Create new MainPanel
     */
    public MainPanel() {
        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING) + ": " + getClass().getName(), e);
        }
    }

    /** Paint panel */
    public void paint(Graphics g) {

        int width = getSize().width;
        int height = getSize().height;

        int posX = (width - imageWidth) / 2;
        int posY = (height - imageHeight) / 2;

        if (posX < 0) {
            posX = 0;
        }
        if (posY < 0) {
            posY = 0;
        }
        
        g.drawImage(image, posX, posY, this);
        Font font = new Font("ARIAL", Font.PLAIN, 16);

        g.setFont(font);
        int labelWidth = g.getFontMetrics().stringWidth(displayName); 
        
        int posLabelX = (width - labelWidth) / 2;
        if (posLabelX < 0) {
            posLabelX = 0;
        }
        
        g.drawString(displayName, posLabelX , posY + 165);
    }

    // --------------------------------------------------------- Private methods

    /** Init panel */
    private void init() throws Exception {
        this.setLayout(null);
        loadImge();

        this.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {

                if (e.getClickCount() == 20 && e.getX() <= 10 && e.getY() <= 10) {
                    //
                    // Easter egg !!
                    //
                    SyncAdminController controller = ExplorerUtil.getSyncAdminController();
                    controller.showAllNodes();
                }
            }

        });

    }

    /** Load image shown to the center of the panel */
    private void loadImge() {
        URL url = this.getClass().getClassLoader().getResource(Constants.MAIN_IMAGE);
        imageIcon = new ImageIcon(url);
        imageWidth = imageIcon.getIconWidth();
        imageHeight = imageIcon.getIconHeight();
        image = imageIcon.getImage();
    }

}
