/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2003-2007 Funambol, Inc.
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

package com.funambol.syncclient.javagui.logging;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.funambol.syncclient.common.logging.Handler;

import com.funambol.syncclient.javagui.Language;
import com.funambol.syncclient.javagui.MainWindow;

/**
 * The synchronization panel.
 *
 *
 *
 * @version $Id: PanelHandler.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class PanelHandler extends Panel
implements ActionListener, Handler  {

    //---------------------------------------------------------- Constants

    //---------------------------------------------------------- Private data

    //
    // The window containing this panel
    //
    private MainWindow mw      = null           ;

    private TextArea   syncLog = null           ;
    private Button     butOk   = null           ;

    private Language   ln      = new Language() ;

    //---------------------------------------------------------- Public methods


    public void printMessage(String s) {
        log(s);
    }

    /**
     * Creates the panel.
     *
     * @param mw the window containing this panel
     */
    public PanelHandler(MainWindow mw) {

        this.mw = mw;

        setLayout(new BorderLayout());

        syncLog = new TextArea();
        syncLog.setEditable(false);

        butOk = new Button      (ln.getString ("ok") ) ;
        butOk.setActionCommand  ("ok"                ) ;
        butOk.addActionListener (this                ) ;
        butOk.setEnabled        (false               ) ;

        add (syncLog , BorderLayout.CENTER ) ;
        add (butOk   , BorderLayout.SOUTH  ) ;

    }

    /**
     * Invoked when an action occurs (i.e. a button is pressed).
     *
     * @param evt the occurred action
     */
    public void actionPerformed(ActionEvent evt) {
        if (evt.getActionCommand().equals("ok")) {
            syncLog.setText("");
            mw.refresh();
        }
    }

    /**
     * Sets the "Ok" button to the specified value
     *
     * @param value the value to set the button to
     */
    public void setButton(boolean value) {
        butOk.setEnabled(value);
    }

    //---------------------------------------------------------- Protected methods

    /**
     * Shows in the log window the specified message
     *
     * @param msg the message to be shown
     */
    protected void log(String msg) {
        syncLog.append(msg + "\n") ;
    }

    //---------------------------------------------------------- Private methods

}
