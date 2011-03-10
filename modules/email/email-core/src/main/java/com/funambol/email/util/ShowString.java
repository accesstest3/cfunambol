/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.email.util;

import java.awt.*;
import java.awt.event.WindowEvent;
import javax.swing.JScrollPane;

/**
 * NOTE: used for testing purpose
 *
 * @version $Id: ShowString.java,v 1.1 2008-03-25 11:25:34 gbmiglia Exp $
 */
public class ShowString extends Frame {

    FontMetrics fontM;
    String outString;

    public ShowString(String target, String title) {

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame_windowClosing(e);
            }
        }
        );

        setTitle(title);
        outString = target;


        Font font = new Font("Monospaced", Font.PLAIN, 16);
        fontM = getFontMetrics(font);
        setFont(font);

        int size = 0;
        for (int i = 0; i < outString.length(); i++) {
            size += fontM.charWidth(outString.charAt(i));
        }
        size += 24;

        setSize(400, 400);
        setLocation(getSize().width/2, getSize().height/2);
        setVisible(true);
        setResizable(true);

    }

    public void paint(Graphics g) {
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;
        g.drawString(outString, x + 6, y + fontM.getAscent() + 14);
    }

    public void frame_windowClosing(WindowEvent e) {
        System.exit(-1);
    }

}
