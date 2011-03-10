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

import java.awt.Button;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.text.ParseException;

import java.util.List;
import java.util.Iterator;

import javax.swing.JTextField;

import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.contact.Email;
import com.funambol.common.pim.contact.Note;
import com.funambol.common.pim.contact.Phone;
import com.funambol.common.pim.contact.Title;
import com.funambol.common.pim.contact.WebPage;
import com.funambol.common.pim.common.*;

/**
 * A form containing all the fields concerning a contact.
 * This form will be a subpanel for ContactModify and ContactNew
 *
 * @version $Id: ContactForm.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class ContactForm 
extends Panel
implements ActionListener, MouseListener, ConfigurationParameters {

    //---------------------------------------------------------------- Constants

    private static final String TK_MOBILEPHONE    = "MobileTelephoneNumber"   ;
    private static final String TK_HOMEPHONE      = "HomeTelephoneNumber"     ;
    private static final String TK_HOME2PHONE     = "Home2TelephoneNumber"    ;
    private static final String TK_HOMEFAX        = "HomeFaxNumber"           ;
    private static final String TK_OTHERPHONE     = "OtherTelephoneNumber"    ;
    private static final String TK_BUSINESSPHONE  = "BusinessTelephoneNumber" ;
    private static final String TK_BUSINESS2PHONE = "Business2TelephoneNumber";
    private static final String TK_BUSINESSFAX    = "BusinessFaxNumber"       ;
    private static final String TK_PAGERNUMBER    = "PagerNumber"             ;
    private static final String TK_EMAIL1         = "Email1Address"           ;
    private static final String TK_EMAIL2         = "Email2Address"           ;
    private static final String TK_EMAIL3         = "Email3Address"           ;

    //------------------------------------------------------------- Private data

    //
    // The window containing this panel
    //
    private MainWindow mw = null;

    private Language ln = new Language();

    //
    // The currently displayed tab (general,personal,business)
    //
    private String currentTab = null;

    private JTextField tf2  = null;
    private JTextField tf3  = null;
    private JTextField tf4  = null;
    private JTextField tf5  = null;
    private JTextField tf6  = null;
    private JTextField tf7  = null;
    private JTextField tf11 = null;
    private JTextField tf13 = null;
    private JTextField tf14 = null;
    private JTextField tf15 = null;
    private JTextField tf16 = null;
    private JTextField tf17 = null;
    private JTextField tf19 = null;
    private JTextField tf20 = null;
    private JTextField tf22 = null;
    private JTextField tf23 = null;
    private JTextField tf24 = null;
    private JTextField tf25 = null;
    private JTextField tf31 = null;
    private JTextField tf32 = null;
    private JTextField tf33 = null;
    private JTextField tf34 = null;
    private JTextField tf35 = null;
    private JTextField tf36 = null;
    private JTextField tf37 = null;
    private JTextField tf38 = null;
    private JTextField tf39 = null;
    private JTextField tf41 = null;
    private JTextField tf42 = null;
    private JTextField tf45 = null;
    private JTextField tf46 = null;
    private JTextField tf47 = null;
    private JTextField tf48 = null;
    private JTextField tf49 = null;
    private JTextField tf50 = null;
    private JTextField tf51 = null;

    private Button        butGeneral         = null;
    private Button        butPersonal        = null;
    private Button        butBusiness        = null;
    private CardLayout    cardLayout         = null;
    private Panel         fieldPanel         = null;
    private ScrollPane    scrollGeneralPane  = null;
    private ScrollPane    scrollPersonalPane = null;
    private ScrollPane    scrollBusinessPane = null;

    //----------------------------------------------------------- Public methods

    public String getBirthdayDate() {
        return tf19.getText();
    }

    /**
     * Creates the form subpanel.
     *
     * @param mw the window containing this panel
     */
    public ContactForm(MainWindow mw) {

        Panel generalPanel  = null;
        Panel personalPanel = null;
        Panel businessPanel = null;

        //
        // Generic Labels
        //
        Label lb2  = null;
        Label lb3  = null;
        Label lb4  = null;
        Label lb5  = null;
        Label lb6  = null;
        Label lb7  = null;
        Label lb11 = null;
        Label lb51 = null;

        //
        // Personal Labels
        //
        Label lb13 = null;
        Label lb14 = null;
        Label lb15 = null;
        Label lb16 = null;
        Label lb17 = null;
        Label lb19 = null;
        Label lb20 = null;
        Label lb22 = null;
        Label lb23 = null;
        Label lb24 = null;
        Label lb25 = null;

        //
        // Business Labels
        //
        Label lb31 = null;
        Label lb32 = null;
        Label lb33 = null;
        Label lb34 = null;
        Label lb35 = null;
        Label lb36 = null;
        Label lb37 = null;
        Label lb38 = null;
        Label lb39 = null;
        Label lb41 = null;
        Label lb42 = null;
        Label lb45 = null;
        Label lb46 = null;
        Label lb47 = null;
        Label lb48 = null;
        Label lb49 = null;
        Label lb50 = null;

        this.mw = mw;

        Panel buttonMenu = new Panel();
        buttonMenu.setLayout(new GridLayout(1,3));

        butGeneral = new Button      (ln.getString("general") );
        butGeneral.setActionCommand  ("general"               );
        butGeneral.addActionListener (this                    );
        butGeneral.setEnabled        (false                   );
        butPersonal = new Button     (ln.getString("personal"));
        butPersonal.setActionCommand ("personal"              );
        butPersonal.addActionListener(this                    );
        butBusiness = new Button     (ln.getString("business"));
        butBusiness.setActionCommand ("business"              );
        butBusiness.addActionListener(this                    );

        buttonMenu.add(butGeneral );
        buttonMenu.add(butPersonal);
        buttonMenu.add(butBusiness);

        int x1  = 0;
        int x2  = 220;
        int y   = 0;
        int dy  = 22;
        int h   = 22;
        int w   = 210;
        
        //
        // Generic Textfields
        //
        tf2  = new JTextField(); 
        tf2.setBackground(java.awt.SystemColor.window);
        tf3  = new JTextField();
        tf3.setBackground(java.awt.SystemColor.window);
        tf4  = new JTextField(); 
        tf4.setBackground(java.awt.SystemColor.window);
        tf5  = new JTextField(); 
        tf5.setBackground(java.awt.SystemColor.window);
        tf51 = new JTextField(); 
        tf51.setBackground(java.awt.SystemColor.window);
        tf6  = new JTextField(); 
        tf6.setBackground(java.awt.SystemColor.window);
        tf7  = new JTextField();
        tf7.setBackground(java.awt.SystemColor.window);
        tf11 = new JTextField(); 
        tf11.setBackground(java.awt.SystemColor.window);
        
        tf2.addMouseListener (this);
        tf3.addMouseListener (this);
        tf4.addMouseListener (this);
        tf5.addMouseListener (this);
        tf51.addMouseListener(this);
        tf6.addMouseListener (this);
        tf7.addMouseListener (this);
        tf11.addMouseListener(this);        

        //
        // Personal Textfields
        //        
        tf13 = new JTextField();
        tf13.setBackground(java.awt.SystemColor.window);      
        tf14 = new JTextField();
        tf14.setBackground(java.awt.SystemColor.window);      
        tf15 = new JTextField();
        tf15.setBackground(java.awt.SystemColor.window);
        tf16 = new JTextField();
        tf16.setBackground(java.awt.SystemColor.window);
        tf17 = new JTextField();
        tf17.setBackground(java.awt.SystemColor.window);
        tf19 = new JTextField();
        tf19.setBackground(java.awt.SystemColor.window);
        tf20 = new JTextField();
        tf20.setBackground(java.awt.SystemColor.window);
        tf22 = new JTextField();
        tf22.setBackground(java.awt.SystemColor.window);
        tf23 = new JTextField();
        tf23.setBackground(java.awt.SystemColor.window);
        tf24 = new JTextField();
        tf24.setBackground(java.awt.SystemColor.window);
        tf25 = new JTextField();
        tf25.setBackground(java.awt.SystemColor.window);
        
        tf13.addMouseListener(this);
        tf14.addMouseListener(this);
        tf15.addMouseListener(this);
        tf16.addMouseListener(this);
        tf17.addMouseListener(this);
        tf19.addMouseListener(this);
        tf20.addMouseListener(this);
        tf22.addMouseListener(this);
        tf23.addMouseListener(this);
        tf24.addMouseListener(this);
        tf25.addMouseListener(this);

        //
        // Business Textfields
        //    
        tf31 = new JTextField();
        tf31.setBackground(java.awt.SystemColor.window);
        tf32 = new JTextField();
        tf32.setBackground(java.awt.SystemColor.window);
        tf33 = new JTextField();
        tf33.setBackground(java.awt.SystemColor.window);
        tf34 = new JTextField();
        tf34.setBackground(java.awt.SystemColor.window);
        tf35 = new JTextField();
        tf35.setBackground(java.awt.SystemColor.window);
        tf36 = new JTextField();
        tf36.setBackground(java.awt.SystemColor.window);
        tf37 = new JTextField();
        tf37.setBackground(java.awt.SystemColor.window);
        tf38 = new JTextField();
        tf38.setBackground(java.awt.SystemColor.window);
        tf39 = new JTextField();
        tf39.setBackground(java.awt.SystemColor.window);
        tf41 = new JTextField();
        tf41.setBackground(java.awt.SystemColor.window);
        tf42 = new JTextField();
        tf42.setBackground(java.awt.SystemColor.window);
        tf45 = new JTextField();
        tf45.setBackground(java.awt.SystemColor.window);
        tf46 = new JTextField();
        tf46.setBackground(java.awt.SystemColor.window);
        tf47 = new JTextField();
        tf47.setBackground(java.awt.SystemColor.window);
        tf48 = new JTextField();
        tf48.setBackground(java.awt.SystemColor.window);
        tf49 = new JTextField();
        tf49.setBackground(java.awt.SystemColor.window);
        tf50 = new JTextField();
        tf50.setBackground(java.awt.SystemColor.window);

        tf31.addMouseListener(this);
        tf32.addMouseListener(this);
        tf33.addMouseListener(this);
        tf34.addMouseListener(this);
        tf35.addMouseListener(this);
        tf36.addMouseListener(this);
        tf37.addMouseListener(this);
        tf38.addMouseListener(this);
        tf39.addMouseListener(this);
        tf41.addMouseListener(this);
        tf42.addMouseListener(this);
        tf45.addMouseListener(this);
        tf46.addMouseListener(this);
        tf47.addMouseListener(this);
        tf48.addMouseListener(this);
        tf49.addMouseListener(this);
        tf50.addMouseListener(this);

        //
        // General Labels
        //
        lb2  = new Label(ln.getString("title"          ));        
        lb3  = new Label(ln.getString("first_name"     ));       
        lb4  = new Label(ln.getString("middle_name"    ));        
        lb5  = new Label(ln.getString("last_name"      ));
        lb51 = new Label(ln.getString("display_name"   ));
        lb6  = new Label(ln.getString("suffix"         ));        
        lb7  = new Label(ln.getString("nickname"       ));
        lb11 = new Label(ln.getString("note"           ));

        //
        // Personal Labels
        //        
        lb13 = new Label(ln.getString("street"         ));        
        lb14 = new Label(ln.getString("city"           ));        
        lb15 = new Label(ln.getString("state"          ));        
        lb16 = new Label(ln.getString("postal_code"    ));        
        lb17 = new Label(ln.getString("country"        ));       
        lb19 = new Label(ln.getString("birthday"       ));        
        lb20 = new Label(ln.getString("cell_phone"     ));       
        lb22 = new Label(ln.getString("home_phone"     ));        
        lb23 = new Label(ln.getString("home_phone2"    ));       
        lb24 = new Label(ln.getString("home_fax"       ));        
        lb25 = new Label(ln.getString("gen_phone"      ));

        //
        // Business Labels
        //
        lb31 = new Label(ln.getString("street"         ));
        lb32 = new Label(ln.getString("city"           ));
        lb33 = new Label(ln.getString("state"          ));
        lb34 = new Label(ln.getString("postal_code"    ));
        lb35 = new Label(ln.getString("country"        ));
        lb36 = new Label(ln.getString("role"           ));
        lb37 = new Label(ln.getString("job_title"      ));
        lb38 = new Label(ln.getString("company"        ));
        lb39 = new Label(ln.getString("department"     ));
        lb41 = new Label(ln.getString("business_phone" ));
        lb42 = new Label(ln.getString("business_phone2"));
        lb45 = new Label(ln.getString("business_fax"   ));
        lb46 = new Label(ln.getString("pager_number"   ));
        lb47 = new Label(ln.getString("email1"         ));
        lb48 = new Label(ln.getString("email2"         ));
        lb49 = new Label(ln.getString("email3"         ));
        lb50 = new Label(ln.getString("webpage"        ));
        
        setNextFocusableProcedure();

        //
        // Generic panel
        //

        //Set bounds
        lb2.setBounds(x1, y, w, h);
        tf2.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb3.setBounds(x1, y, w, h);
        tf3.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb4.setBounds(x1, y, w, h);
        tf4.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb5.setBounds(x1, y, w, h);
        tf5.setBounds(x2, y, w, h);

        y += dy;
        
        lb51.setBounds(x1, y, w, h);
        tf51.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb6.setBounds(x1, y, w, h);
        tf6.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb7.setBounds(x1, y, w, h);
        tf7.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb11.setBounds(x1, y, w, h);
        tf11.setBounds(x2, y, w, h);

        
        generalPanel = new Panel();
        generalPanel.setLayout(null);

        generalPanel.add(lb2 );
        generalPanel.add(tf2 );
        generalPanel.add(lb3 );
        generalPanel.add(tf3 );
        generalPanel.add(lb4 );
        generalPanel.add(tf4 );
        generalPanel.add(lb5 );
        generalPanel.add(tf5 );
        generalPanel.add(lb51);
        generalPanel.add(tf51);
        generalPanel.add(lb6 );
        generalPanel.add(tf6 );
        generalPanel.add(lb7 );
        generalPanel.add(tf7 );
        generalPanel.add(lb11);
        generalPanel.add(tf11);

        //
        // Personal panel
        //
        
        // Set bounds
        y = 0;
        
        lb13.setBounds(x1, y, w, h);
        tf13.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb14.setBounds(x1, y, w, h);
        tf14.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb15.setBounds(x1, y, w, h);
        tf15.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb16.setBounds(x1, y, w, h);
        tf16.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb17.setBounds(x1, y, w, h);
        tf17.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb19.setBounds(x1, y, w, h);
        tf19.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb20.setBounds(x1, y, w, h);
        tf20.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb22.setBounds(x1, y, w, h);
        tf22.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb23.setBounds(x1, y, w, h);
        tf23.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb24.setBounds(x1, y, w, h);
        tf24.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb25.setBounds(x1, y, w, h);
        tf25.setBounds(x2, y, w, h);
        
        personalPanel = new Panel();
        personalPanel.setLayout(null);

        personalPanel.add(lb13);
        personalPanel.add(tf13);
        personalPanel.add(lb14);
        personalPanel.add(tf14);
        personalPanel.add(lb15);
        personalPanel.add(tf15);
        personalPanel.add(lb16);
        personalPanel.add(tf16);
        personalPanel.add(lb17);
        personalPanel.add(tf17);
        personalPanel.add(lb19);
        personalPanel.add(tf19);
        personalPanel.add(lb20);
        personalPanel.add(tf20);
        personalPanel.add(lb22);
        personalPanel.add(tf22);
        personalPanel.add(lb23);
        personalPanel.add(tf23);
        personalPanel.add(lb24);
        personalPanel.add(tf24);        
        personalPanel.add(lb25);
        personalPanel.add(tf25);
        //
        // Business panel
        //
        
        // Set Bounds
        y = 0;
        
        lb31.setBounds(x1, y, w, h);
        tf31.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb32.setBounds(x1, y, w, h);
        tf32.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb33.setBounds(x1, y, w, h);
        tf33.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb34.setBounds(x1, y, w, h);
        tf34.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb35.setBounds(x1, y, w, h);
        tf35.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb36.setBounds(x1, y, w, h);
        tf36.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb37.setBounds(x1, y, w, h);
        tf37.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb38.setBounds(x1, y, w, h);
        tf38.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb39.setBounds(x1, y, w, h);
        tf39.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb41.setBounds(x1, y, w, h);
        tf41.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb42.setBounds(x1, y, w, h);
        tf42.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb45.setBounds(x1, y, w, h);
        tf45.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb46.setBounds(x1, y, w, h);
        tf46.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb47.setBounds(x1, y, w, h);
        tf47.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb48.setBounds(x1, y, w, h);
        tf48.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb49.setBounds(x1, y, w, h);
        tf49.setBounds(x2, y, w, h);
        
        y += dy;
        
        lb50.setBounds(x1, y, w, h);
        tf50.setBounds(x2, y, w, h);
        
        businessPanel = new Panel();
        businessPanel.setLayout(null);

        businessPanel.add(lb31);
        businessPanel.add(tf31);
        businessPanel.add(lb32);
        businessPanel.add(tf32);
        businessPanel.add(lb33);
        businessPanel.add(tf33);
        businessPanel.add(lb34);
        businessPanel.add(tf34);
        businessPanel.add(lb35);
        businessPanel.add(tf35);
        businessPanel.add(lb36);
        businessPanel.add(tf36);
        businessPanel.add(lb37);
        businessPanel.add(tf37);
        businessPanel.add(lb38);
        businessPanel.add(tf38);
        businessPanel.add(lb39);
        businessPanel.add(tf39);
        businessPanel.add(lb41);
        businessPanel.add(tf41);
        businessPanel.add(lb42);
        businessPanel.add(tf42);
        businessPanel.add(lb45);
        businessPanel.add(tf45);
        businessPanel.add(lb46);
        businessPanel.add(tf46);
        businessPanel.add(lb47);
        businessPanel.add(tf47);
        businessPanel.add(lb48);
        businessPanel.add(tf48);
        businessPanel.add(lb49);
        businessPanel.add(tf49);
        businessPanel.add(lb50);
        businessPanel.add(tf50);

        scrollGeneralPane  = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
        scrollGeneralPane.add(generalPanel  );

        scrollPersonalPane = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
        scrollPersonalPane.add(personalPanel);

        scrollBusinessPane = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
        scrollBusinessPane.add(businessPanel);

        fieldPanel = new Panel();
        fieldPanel.setSize(100,115);
        cardLayout = new CardLayout();
        fieldPanel.setLayout(cardLayout);

        fieldPanel.add(scrollGeneralPane , "general" );
        fieldPanel.add(scrollPersonalPane, "personal");
        fieldPanel.add(scrollBusinessPane, "business");

        cardLayout.show(fieldPanel, "general");
        currentTab = "general";

        setLayout(new BorderLayout());

        add (buttonMenu, BorderLayout.NORTH );
        add (fieldPanel, BorderLayout.CENTER);
    }

    /**
     * Allows the contact form to open with the general tab
     */
    public void setInitialTab() {
        butGeneral.setEnabled (false);
        butPersonal.setEnabled(true );
        butBusiness.setEnabled(true );
        cardLayout.show(fieldPanel,"general");
        currentTab = "general";
    }
    
	/**
     * Invoked when an action occurs (i.e. a button is pressed).
     *
     * @param evt the occurred action
     */
    public void actionPerformed(ActionEvent evt) {

        if (evt.getActionCommand().equals("general"))  {
            butGeneral.setEnabled (false);
            butPersonal.setEnabled(true );
            butBusiness.setEnabled(true );
            cardLayout.show(fieldPanel,"general");
            currentTab = "general";
        } else if (evt.getActionCommand().equals("personal")) {
            butGeneral.setEnabled (true );
            butPersonal.setEnabled(false);
            butBusiness.setEnabled(true );
            cardLayout.show(fieldPanel,"personal");
            currentTab = "personal";
        } else if (evt.getActionCommand().equals("business")) {
            butGeneral.setEnabled (true );
            butPersonal.setEnabled(true );
            butBusiness.setEnabled(false);
            cardLayout.show(fieldPanel,"business");
            currentTab = "business";
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) 
     * on a component.
     * This method adjusts the scrollpanes so that the selected textfield is 
     * placed on top of the pane.
     *
     * @param evt the occurred event
     */
    public void mouseClicked(MouseEvent evt) {
        //do nothing
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * Not used.
     *
     * @param evt the occurred event
     */
    public void mousePressed(MouseEvent evt) {
        // do nothing
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * Not used.
     *
     * @param evt the occurred event
     */
    public void mouseReleased(MouseEvent evt) {
        // do nothing       
    }

    /**
     * Invoked when the mouse enters a component.
     * Not used.
     *
     * @param evt the occurred event
     */
    public void mouseEntered(MouseEvent evt) {
        // do nothing
    }

    /**
     * Invoked when the mouse exits a component.
     * Not used.
     *
     * @param evt the occurred event
     */
    public void mouseExited(MouseEvent evt) {
        // do nothing
    }

    //-------------------------------------------------------- Protected methods

    /**
     * Sets the fields with the values contained in the specified Contact object.
     * Used to display the old value of contact
     *
     * @param contact the Contact object which is the source of the values
     */
    protected void setFields(Contact contact) {

        //
        // General
        //
        tf2.setText (contact.getName().getSalutation().getPropertyValueAsString());
        tf3.setText (contact.getName().getFirstName().getPropertyValueAsString());
        tf4.setText (contact.getName().getMiddleName().getPropertyValueAsString());
        tf5.setText (contact.getName().getLastName().getPropertyValueAsString());
        tf51.setText(contact.getName().getDisplayName().getPropertyValueAsString());
        tf6.setText (contact.getName().getSuffix().getPropertyValueAsString());
        tf7.setText (contact.getName().getNickname().getPropertyValueAsString());

        List notes = contact.getNotes();
        if (notes != null && notes.size() > 0) {
            Note tmpNote = (Note)notes.get(0);
            tf11.setText(tmpNote.getPropertyValueAsString());
        } else {
            tf11.setText("");
        }

        //
        // Personal
        //
        tf13.setText(contact.getPersonalDetail().getAddress()
                            .getStreet().getPropertyValueAsString());
        tf14.setText(contact.getPersonalDetail().getAddress()
                            .getCity().getPropertyValueAsString());
        tf15.setText(contact.getPersonalDetail().getAddress()
                            .getState().getPropertyValueAsString());
        tf16.setText(contact.getPersonalDetail().getAddress()
                            .getPostalCode().getPropertyValueAsString());
        tf17.setText(contact.getPersonalDetail().getAddress()
                            .getCountry().getPropertyValueAsString());

        String birthday = "";
        try {
            birthday = FieldsHelper.convertInDayFormatFrom(
                (String)contact.getPersonalDetail().getBirthday()
            );
        } catch (ParseException e) {
            //ignored but we should handle
        }

        if (birthday.length() > 10) {
            birthday = birthday.substring(0, 10);
        }

        //
        // 01/01/4501 is the dummy date of outlook...
        //
        if (birthday.indexOf("01/01/4501") != -1) {
            birthday = "";
        }

        tf19.setText(birthday);

        List phones = contact.getPersonalDetail().getPhones();

        tf20.setText("");
        tf22.setText("");
        tf23.setText("");
        tf24.setText("");
        tf25.setText("");

        if (phones != null) {
            Iterator itPhones = phones.iterator();
            while(itPhones.hasNext()) {
                Phone phone = (Phone)itPhones.next();
                if (phone.getPhoneType() == null) {
                    continue;
                }
                if (phone.getPhoneType().equals(TK_MOBILEPHONE)) {
                    tf20.setText(phone.getPropertyValueAsString());
                } else if (phone.getPhoneType().equals(TK_HOMEPHONE)) {
                    tf22.setText(phone.getPropertyValueAsString());
                } else if (phone.getPhoneType().equals(TK_HOME2PHONE)) {
                    tf23.setText(phone.getPropertyValueAsString());
                } else if (phone.getPhoneType().equals(TK_HOMEFAX)) {
                    tf24.setText(phone.getPropertyValueAsString());
                } else if (phone.getPhoneType().equals(TK_OTHERPHONE)) {
                    tf25.setText(phone.getPropertyValueAsString());
                }
            }
        }

        //
        // Business
        //
        tf31.setText(contact.getBusinessDetail()
                             .getAddress().getStreet().getPropertyValueAsString());
        tf32.setText(contact.getBusinessDetail()
                             .getAddress().getCity().getPropertyValueAsString());
        tf33.setText(contact.getBusinessDetail()
                             .getAddress().getState().getPropertyValueAsString());
        tf34.setText(contact.getBusinessDetail()
                             .getAddress().getPostalCode().getPropertyValueAsString());
        tf35.setText(contact.getBusinessDetail()
                             .getAddress().getCountry().getPropertyValueAsString());
        tf36.setText(contact.getBusinessDetail()
                             .getRole().getPropertyValueAsString());
        tf38.setText(contact.getBusinessDetail()
                             .getCompany().getPropertyValueAsString());
        tf39.setText(contact.getBusinessDetail()
                             .getDepartment().getPropertyValueAsString());

        List titles = contact.getBusinessDetail().getTitles();
        if (titles != null && titles.size() > 0) {
            Title title = (Title)titles.get(0);
            tf37.setText(title.getPropertyValueAsString());
        } else {
            tf37.setText("");
        }

        List phonesBus = contact.getBusinessDetail().getPhones();

        tf41.setText("");
        tf42.setText("");
        tf45.setText("");
        tf46.setText("");

        if (phonesBus != null) {
            Iterator itPhones = phonesBus.iterator();
            while(itPhones.hasNext()) {
                Phone phone = (Phone)itPhones.next();
                if (phone.getPhoneType() == null) {
                    continue;
                }
                if (phone.getPhoneType().equals(TK_BUSINESSPHONE)) {
                    tf41.setText(phone.getPropertyValueAsString());
                } else if (phone.getPhoneType().equals(TK_BUSINESS2PHONE)) {
                    tf42.setText(phone.getPropertyValueAsString());
                } else if (phone.getPhoneType().equals(TK_BUSINESSFAX)) {
                    tf45.setText(phone.getPropertyValueAsString());
                } else if (phone.getPhoneType().equals(TK_PAGERNUMBER)) {
                    tf46.setText(phone.getPropertyValueAsString());
                }
            }
        }

        tf47.setText("");
        tf48.setText("");
        tf49.setText("");

        List emails = contact.getPersonalDetail().getEmails();

        if (emails != null) {
            Iterator itEmails = emails.iterator();
            while(itEmails.hasNext()) {
                Email email = (Email)itEmails.next();
                if (email.getEmailType() == null) {
                    continue;
                }
                if (email.getEmailType().equals(TK_EMAIL1)) {
                    tf47.setText(email.getPropertyValueAsString());
                } else if (email.getEmailType().equals(TK_EMAIL2)) {
                    tf48.setText(email.getPropertyValueAsString());
                }
            }
        }

        List emailsBus = contact.getBusinessDetail().getEmails();

        if (emailsBus != null) {
            Iterator itEmails = emailsBus.iterator();
            while(itEmails.hasNext()) {
                Email email = (Email)itEmails.next();
                if (email.getEmailType() == null) {
                    continue;
                }
                if (email.getEmailType().equals(TK_EMAIL3)) {
                    tf49.setText(email.getPropertyValueAsString());
                }
            }
        }

        List webpages = contact.getBusinessDetail().getWebPages();
        if (webpages != null && webpages.size() > 0) {
            WebPage webpage = (WebPage)webpages.get(0);
            tf50.setText(webpage.getPropertyValueAsString());
        } else {
            tf50.setText("");
        }
    }

    /**
     * Returns a new Contact object containing the informations set in the 
     * textfields.
     * This method also builds the LABELs from the provided addresses,
     * when at least street, city, state, postal code and country are specified.
     *
     * @return the Contact object containing the informations set in the form
     */
    protected Contact getFields() {

        Contact contact = new Contact();

        //
        // General
        //
        contact.getName().getSalutation ().setPropertyValue(tf2.getText ());
        contact.getName().getFirstName  ().setPropertyValue(tf3.getText ());
        contact.getName().getMiddleName ().setPropertyValue(tf4.getText ());
        contact.getName().getLastName   ().setPropertyValue(tf5.getText ());
        contact.getName().getDisplayName().setPropertyValue(tf51.getText());
        contact.getName().getSuffix     ().setPropertyValue(tf6.getText ());
        contact.getName().getNickname   ().setPropertyValue(tf7.getText ());

        Note tmpNote = new Note();
        tmpNote.setPropertyValue(tf11.getText());
        tmpNote.setNoteType("Body");
        contact.addNote(tmpNote);

        //
        // Personal
        //
        contact.getPersonalDetail().getAddress()
               .getStreet().setPropertyValue(tf13.getText());
        contact.getPersonalDetail().getAddress()
               .getCity().setPropertyValue(tf14.getText());
        contact.getPersonalDetail().getAddress()
               .getState().setPropertyValue(tf15.getText());
        contact.getPersonalDetail().getAddress()
               .getPostalCode().setPropertyValue(tf16.getText());
        contact.getPersonalDetail().getAddress()
               .getCountry().setPropertyValue(tf17.getText());
               
        try {
            contact.getPersonalDetail().setBirthday(
                FieldsHelper.convertInDayFormatTo(tf19.getText())
            );
        } catch (ParseException e) {
            //we should handle better
            contact.getPersonalDetail().setBirthday("");
        }

        Phone tmpPhone = new Phone();
        tmpPhone.setPropertyValue(tf20.getText());
        tmpPhone.setPhoneType(TK_MOBILEPHONE);
        contact.getPersonalDetail().addPhone(tmpPhone);

        Phone tmpPhoneHome1 = new Phone();
        tmpPhoneHome1.setPropertyValue(tf22.getText());
        tmpPhoneHome1.setPhoneType(TK_HOMEPHONE);
        contact.getPersonalDetail().addPhone(tmpPhoneHome1);

        Phone tmpPhoneHome2 = new Phone();
        tmpPhoneHome2.setPropertyValue(tf23.getText());
        tmpPhoneHome2.setPhoneType(TK_HOME2PHONE);
        contact.getPersonalDetail().addPhone(tmpPhoneHome2);

        Phone tmpPhoneHomeFax = new Phone();
        tmpPhoneHomeFax.setPropertyValue(tf24.getText());
        tmpPhoneHomeFax.setPhoneType(TK_HOMEFAX);
        contact.getPersonalDetail().addPhone(tmpPhoneHomeFax);

        Phone tmpPhoneOther = new Phone();
        tmpPhoneOther.setPropertyValue(tf25.getText());
        tmpPhoneOther.setPhoneType(TK_OTHERPHONE);
        contact.getPersonalDetail().addPhone(tmpPhoneOther);

        //
        // Business
        //
        contact.getBusinessDetail().getAddress()
               .getStreet().setPropertyValue(tf31.getText());
        contact.getBusinessDetail().getAddress()
               .getCity().setPropertyValue(tf32.getText());
        contact.getBusinessDetail().getAddress()
               .getState().setPropertyValue(tf33.getText());
        contact.getBusinessDetail().getAddress()
               .getPostalCode().setPropertyValue(tf34.getText());
        contact.getBusinessDetail().getAddress()
               .getCountry().setPropertyValue(tf35.getText());
        contact.getBusinessDetail()
               .getRole().setPropertyValue(tf36.getText());
        contact.getBusinessDetail()
               .getCompany().setPropertyValue(tf38.getText());
        contact.getBusinessDetail()
               .getDepartment().setPropertyValue(tf39.getText());
        
        Title tmpTitle = new Title();
        tmpTitle.setPropertyValue(tf37.getText());
        tmpTitle.setTitleType("JobTitle");
        contact.getBusinessDetail().addTitle(tmpTitle);

        Phone tmpPhoneBusines = new Phone();
        tmpPhoneBusines.setPropertyValue(tf41.getText());
        tmpPhoneBusines.setPhoneType(TK_BUSINESSPHONE);
        contact.getBusinessDetail().addPhone(tmpPhoneBusines);

        Phone tmpPhoneBusiness2 = new Phone();
        tmpPhoneBusiness2.setPropertyValue(tf42.getText());
        tmpPhoneBusiness2.setPhoneType(TK_BUSINESS2PHONE);
        contact.getBusinessDetail().addPhone(tmpPhoneBusiness2);

        Phone tmpPhoneBusinessFax = new Phone();
        tmpPhoneBusinessFax.setPropertyValue(tf45.getText());
        tmpPhoneBusinessFax.setPhoneType(TK_BUSINESSFAX);
        contact.getBusinessDetail().addPhone(tmpPhoneBusinessFax);

        Phone tmpPager = new Phone();
        tmpPager.setPropertyValue(tf46.getText());
        tmpPager.setPhoneType(TK_PAGERNUMBER);
        contact.getBusinessDetail().addPhone(tmpPager);

        Email tmpEmail = new Email();
        tmpEmail.setPropertyValue(tf47.getText());
        tmpEmail.setEmailType(TK_EMAIL1);
        contact.getPersonalDetail().addEmail(tmpEmail);

        Email tmpEmail2 = new Email();
        tmpEmail2.setPropertyValue(tf48.getText());
        tmpEmail2.setEmailType(TK_EMAIL2);
        contact.getPersonalDetail().addEmail(tmpEmail2);

        Email tmpEmail3 = new Email();
        tmpEmail3.setPropertyValue(tf49.getText());
        tmpEmail3.setEmailType(TK_EMAIL3);
        contact.getBusinessDetail().addEmail(tmpEmail3);

        WebPage tmpWebPage = new WebPage();
        tmpWebPage.setPropertyValue(tf50.getText());
        tmpWebPage.setWebPageType("BusinessWebPage");
        contact.getBusinessDetail().addWebPage(tmpWebPage);

        //
        // Personal Label
        //
        StringBuffer tmpPersonalLabel = new StringBuffer("");

        //
        // Street
        //
        tmpPersonalLabel.append(tf13.getText()).append("\r\n");
        //
        // City
        //
        tmpPersonalLabel.append(tf14.getText()).append(", ");
        //
        // State
        //
        tmpPersonalLabel.append(tf15.getText()).append(" ");
        //
        // PostalCode
        //
        tmpPersonalLabel.append(tf16.getText()).append("\r\n");
        //
        // Country
        //
        tmpPersonalLabel.append(tf17.getText());
        contact.getPersonalDetail()
               .getAddress()
               .getLabel().setPropertyValue(tmpPersonalLabel.toString());

        //
        // Business Label(all fields are mandatory except POBox and RoomNumber)
        //
        StringBuffer tmpBusinessLabel = new StringBuffer("");

        //
        // Street
        //
        tmpBusinessLabel.append(tf31.getText()).append("\r\n");
        //
        // City
        //
        tmpBusinessLabel.append(tf32.getText()).append(", ");
        //
        // State
        //
        tmpBusinessLabel.append(tf33.getText()).append(" ");
        //
        // PostalCode
        //
        tmpBusinessLabel.append(tf34.getText()).append("\r\n");
        //
        // Country
        //
        tmpBusinessLabel.append(tf35.getText());
        contact.getBusinessDetail()
               .getAddress()
               .getLabel().setPropertyValue(tmpBusinessLabel.toString());

        return contact;
    }

    /**
     * Sets all the textfields to an empty string.
     */
    protected void blankFields() {

        tf2.setText ("");
        tf3.setText ("");
        tf4.setText ("");
        tf5.setText ("");
        tf51.setText("");
        tf6.setText ("");
        tf7.setText ("");
        tf11.setText("");
        tf13.setText("");
        tf14.setText("");
        tf15.setText("");
        tf16.setText("");
        tf17.setText("");
        tf19.setText("");
        tf20.setText("");
        tf22.setText("");
        tf23.setText("");
        tf24.setText("");
        tf25.setText("");
        tf31.setText("");
        tf32.setText("");
        tf33.setText("");
        tf34.setText("");
        tf35.setText("");
        tf36.setText("");
        tf37.setText("");
        tf38.setText("");
        tf39.setText("");
        tf41.setText("");
        tf42.setText("");
        tf45.setText("");
        tf46.setText("");
        tf47.setText("");
        tf48.setText("");
        tf49.setText("");
        tf50.setText("");
    }
    
	/**
     * Generates the order that the JTextField are cycled.
     */   
    protected void setNextFocusableProcedure() {
        tf2.setNextFocusableComponent(tf3);
        tf3.setNextFocusableComponent(tf4);
        tf4.setNextFocusableComponent(tf5);
        tf5.setNextFocusableComponent(tf51);
        tf51.setNextFocusableComponent(tf6);
        tf6.setNextFocusableComponent (tf7);
        tf7.setNextFocusableComponent (tf11);
        tf11.setNextFocusableComponent(tf2);
        
        tf13.setNextFocusableComponent(tf14);
        tf14.setNextFocusableComponent(tf15);
        tf15.setNextFocusableComponent(tf16);
        tf16.setNextFocusableComponent(tf17);
        tf17.setNextFocusableComponent(tf19);
        tf19.setNextFocusableComponent(tf20);
        tf20.setNextFocusableComponent(tf22);
        tf22.setNextFocusableComponent(tf23);
        tf23.setNextFocusableComponent(tf24);
        tf24.setNextFocusableComponent(tf25);
        tf25.setNextFocusableComponent(tf13);
        
        tf31.setNextFocusableComponent(tf32);
        tf32.setNextFocusableComponent(tf33);
        tf33.setNextFocusableComponent(tf34);
        tf34.setNextFocusableComponent(tf35);
        tf35.setNextFocusableComponent(tf36);
        tf36.setNextFocusableComponent(tf37);
        tf37.setNextFocusableComponent(tf38);
        tf38.setNextFocusableComponent(tf39);
        tf39.setNextFocusableComponent(tf41);
        tf41.setNextFocusableComponent(tf42);
        tf42.setNextFocusableComponent(tf45);
        tf45.setNextFocusableComponent(tf46);
        tf46.setNextFocusableComponent(tf47);
        tf47.setNextFocusableComponent(tf48);
        tf48.setNextFocusableComponent(tf49);
        tf49.setNextFocusableComponent(tf50);
        tf50.setNextFocusableComponent(tf31);		
	}
    
}
