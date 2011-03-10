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
package com.funambol.syncclient.javagui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.funambol.syncclient.common.gui.HREFJLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * The about window.
 *
 * @version $Id: About.java,v 1.2 2007-12-22 14:00:19 nichele Exp $
 */
public class About
extends JDialog
implements ActionListener, WindowListener, ConfigurationParameters {

    // ------------------------------------------------------------ Private data

    //
    // The window containing this panel
    //
    private MainWindow mw = null;
    private Language   ln = new Language();

    private JButton    btOk         ;
    private JLabel     versionText  ;
    private HREFJLabel funambolLink ;
    private JLabel     copyrightText;
    private JLabel     licenseText  ;
    private JPanel     jPanel1      ;

    // ---------------------------------------------------------- Public methods
    /** Creates new form JDialog */
    public About(MainWindow mainWindow) {
        super(mainWindow);
        this.mw = mainWindow;
        initComponents();
    }

    private void initComponents() {
        final int H = 18;

        jPanel1       = new JPanel()    ;
        versionText   = new JLabel()    ;
        funambolLink  = new HREFJLabel();
        copyrightText = new JLabel()    ;
        licenseText   = new JLabel()    ;
        btOk          = new JButton()   ;

        //
        // Set up the window.
        //
        getContentPane().setLayout(null);

        setTitle(ln.getString("about_funambol"));
        setFont(FONT);

        setBackground(Color.WHITE);
        setForeground(Color.WHITE);

        setName("aboutDialog");
        setResizable(false);
        setSize(400,468);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        jPanel1.setLayout(null);
        jPanel1.setBackground(Color.WHITE);
        jPanel1.setForeground(Color.WHITE);
        jPanel1.setBounds(0, 0, 400, 453);
        getContentPane().add(jPanel1);

        JLabel lab = new JLabel(new ImageIcon(FRAME_LOGONAME));
        lab.setBounds(122, 6, 128, 128);
        jPanel1.add(lab);

        versionText.setBackground(Color.WHITE);
        versionText.setFont(FONT_BOLD);
        versionText.setText(ln.getString("sync_client_pim"));
        versionText.setHorizontalAlignment(JLabel.CENTER);
        versionText.setBounds(94, 130, 212, H);
        jPanel1.add(versionText);
        
        copyrightText.setBackground(Color.WHITE);
        copyrightText.setFont(FONT);
        copyrightText.setText(ln.getString("copyright"));
        copyrightText.setBounds(90, 150, 220, H*2);
        jPanel1.add(copyrightText);

        String urlLabel = ln.getString("copyright_url_label");
        funambolLink.setBackground(Color.WHITE);
        funambolLink.setFont(FONT);
        funambolLink.setText(urlLabel);
        funambolLink.setUrl(ln.getString("copyright_url"));
        funambolLink.setHorizontalAlignment(JLabel.CENTER);
        int width = funambolLink.getFontMetrics(FONT).stringWidth(urlLabel);
        funambolLink.setBounds((400-width)/2, 192, width, H);
        jPanel1.add(funambolLink);

        licenseText.setBackground(Color.WHITE);
        licenseText.setFont(FONT9);
        licenseText.setBounds(12, 216, 371, H + 170);
        licenseText.setText(ln.getString("about_license"));
        jPanel1.add(licenseText);

        btOk.setText(ln.getString("ok"));
        btOk.setFont(FONT);
        btOk.setForeground(Color.BLACK);
        btOk.setActionCommand("ok");
        btOk.addActionListener(this);
        btOk.setBounds(156, 410, 88, 24);
        btOk.addActionListener(this);
        jPanel1.add(btOk);

        addWindowListener(this);
    }

    public void actionPerformed(ActionEvent evt) {

        if (evt.getActionCommand().equals("ok")) {
            mw.setEnabled(true);
            setVisible(false);
        }
    }

    public void windowClosing(WindowEvent evt) {
        mw.setEnabled(true);
        setVisible(false);
    }

    public void windowActivated  (WindowEvent e) {}

    public void windowClosed     (WindowEvent e) {}

    public void windowDeactivated(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}

    public void windowIconified  (WindowEvent e) {}

    public void windowOpened     (WindowEvent e) {}

}
