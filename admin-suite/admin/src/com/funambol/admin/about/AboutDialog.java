/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

package com.funambol.admin.about;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.openide.windows.WindowManager;

import com.funambol.admin.ui.GuiFactory;
import com.funambol.admin.ui.HREFJLabel;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.util.Constants;

/**
 * Shows the About Panel.
 *
 *
 * @version $Id: AboutDialog.java,v 1.7 2007-11-28 14:32:22 luigiafassina Exp $
 */
public class AboutDialog extends JDialog implements ActionListener, Constants {

    // --------------------------------------------------------------- Constants
    private static String displayName = Bundle.getMessage(Bundle.EXPLORER_TITLE) +
                                        "  v. " +
                                        Bundle.getMessage(Bundle.VERSION);

    // ------------------------------------------------------------ Private data
    private JButton    jbuttonOk ;
    private JLabel     jLabel1   ;
    private JLabel     jLabel2   ;
    private HREFJLabel jLabel3   ;
    private JTextPane  jTextPane ;
    private JPanel     jPanel1   ;

    // ------------------------------------------------------------ Construstors

    /**
     * Creates a new instance of AboutDialog
     */
    public AboutDialog() {
        super(WindowManager.getDefault().getMainWindow());
        try {
            init();
        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_CREATING + getClass().getName()) + e.getMessage());
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Create the panel
     * @throws Exception if error occures during creation of the panel
     */
    private void init() throws Exception {

        int hlab  = 18 ;

        jPanel1   = new JPanel()   ;
        jLabel1   = new JLabel()   ;
        jLabel2   = new JLabel()   ;
        jLabel3   = new HREFJLabel()   ;
        jTextPane = new JTextPane();
        jbuttonOk = new JButton()  ;

        // Set the characteristics for this dialog instance
        setTitle(Bundle.getMessage(Bundle.ABOUT_PANEL_NAME));
        setSize(372,480);
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        setResizable(false);
        setModal(true);
        setLocationRelativeTo(this.getParent());

        jPanel1.setLayout(null);
        jPanel1.setBackground(Color.WHITE);
        jPanel1.setForeground(Color.WHITE);
        jPanel1.setBounds(0, 0, 372, 453);
        getContentPane().add(jPanel1);

        URL url = this.getClass().getClassLoader().getResource(Constants.LOGO_IMAGE);
        JLabel lab = new JLabel(new ImageIcon(url));
        lab.setBounds(122, 6, 128, 128);
        jPanel1.add(lab);

        jLabel1.setBackground(Color.WHITE);
        jLabel1.setFont(GuiFactory.defaultTableHeaderFont);
        jLabel1.setText(displayName);
        jLabel1.setHorizontalAlignment(JLabel.CENTER);
        jLabel1.setBounds(76, 142, 220, hlab);
        jPanel1.add(jLabel1);

        jLabel2.setBackground(Color.WHITE);
        jLabel2.setFont(GuiFactory.defaultFont);
        jLabel2.setText(Bundle.getMessage(Bundle.ABOUT_COPYRIGHT));
        jLabel2.setHorizontalAlignment(JLabel.CENTER);
        jLabel2.setBounds(86, 167, 200, hlab);
        jPanel1.add(jLabel2);

        jLabel3.setBackground(Color.WHITE);
        jLabel3.setFont(GuiFactory.defaultFont);
        String label3Text = Bundle.getMessage(Bundle.ABOUT_URL);
        jLabel3.setText(label3Text);
        jLabel3.setUrl(Bundle.getMessage(Bundle.ABOUT_URL));
        jLabel3.setForeground(Color.blue);
        jLabel3.setHorizontalAlignment(JLabel.CENTER);
        Font label3Font = jLabel3.getFont();
        int label3Width = jLabel3.getFontMetrics(label3Font).stringWidth(label3Text);
        jLabel3.setBounds(140, 192, label3Width, hlab);
        jPanel1.add(jLabel3);

        jTextPane.setEditable(false);
        jTextPane.setBackground(Color.WHITE);
        jTextPane.setBounds(10, 216, 345, hlab + 170);
        StyledDocument doc = jTextPane.getStyledDocument();
        //  Set alignment to be centered for all paragraphs
        MutableAttributeSet standard = new SimpleAttributeSet();
        StyleConstants.setAlignment(standard, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(standard, 9);
        StyleConstants.setFontFamily(standard, "ARIAL");
        doc.setParagraphAttributes(0, 0, standard, true);
        jTextPane.setText(Bundle.getMessage(Bundle.ABOUT_LICENSE));
        jPanel1.add(jTextPane);

        jbuttonOk.setBackground(Color.WHITE);
        jbuttonOk.setFont(GuiFactory.defaultFont);
        jbuttonOk.setText("OK");
        jbuttonOk.setBounds(142, 410, 88, 24);
        jbuttonOk.addActionListener(this);
        jPanel1.add(jbuttonOk);
    }

    public void actionPerformed(ActionEvent evt) {
        removeAll();
        setVisible(false);
    }
}
