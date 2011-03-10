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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Hashtable;

/**
 * The configuration panel.
 *
 * @version $Id: Configuration.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class Configuration
extends Panel
implements ActionListener,
ConfigurationParameters {

    //---------------------------------------------------------------- Constants

    private final static String SYNC_NONE   = "none"    ;
    private final static String SYNC_TWOWAY = "two-way" ;

    //------------------------------------------------------------- Private data

    //
    // The window containing this panel
    //
    private MainWindow     mw              = null           ;

    private TextField      tf1             = null           ;
    private TextField      tf2             = null           ;
    private TextField      tf3             = null           ;
    private TextField      tf4             = null           ;
    private TextField      tf5             = null           ;
    private TextField      tf6             = null           ;

    //
    //Checkbox enable sync
    //
    private Checkbox       cbSyncContact   = null           ;
    private Checkbox       cbSyncCalendar  = null           ;

    //
    //Select message type, log level
    //
    private CheckboxGroup  cbgMessageType  = null           ;
    private CheckboxGroup  cbgLogLevel     = null           ;
    private Checkbox       cbMsgXML        = null           ;
    private Checkbox       cbMsgWBXML      = null           ;
    private Checkbox       cbLogNone       = null           ;
    private Checkbox       cbLogInfo       = null           ;
    private Checkbox       cbLogDebug      = null           ;

    private Language       ln              = new Language() ;

    //----------------------------------------------------------- Public methods

    /**
     * Creates the panel.
     *
     * @param mw the window containing this panel
     */
    public Configuration(MainWindow mw) {

        Panel  configPanel  = null ;
        Panel  buttonPanel  = null ;
        Panel  messagePanel = null ;
        Panel  logPanel     = null ;

        Button butOk        = null ;
        Button butCancel    = null ;

        Label lb1           = null ;
        Label lb2           = null ;
        Label lb3           = null ;
        Label lb4           = null ;
        Label lb5           = null ;
        Label lb6           = null ;
        Label lb7           = null ;
        Label lb8           = null ;
        Label lb10          = null ;
        Label lb11          = null ;

        this.mw = mw;

        setLayout(new BorderLayout());

        configPanel  = new Panel();
        configPanel.setLayout (new GridLayout(12, 2));

        messagePanel = new Panel ();
        messagePanel.setLayout(new GridLayout(1,  2));

        logPanel     = new Panel ();
        logPanel.setLayout    (new GridLayout(1,  3));

        lb1             = new Label (ln.getString ("sync_server_url"            ) ) ;
        lb2             = new Label (ln.getString ("username"                   ) ) ;
        lb3             = new Label (ln.getString ("password"                   ) ) ;
        lb4             = new Label (ln.getString ("remote_address_book"        ) ) ;
        lb5             = new Label (ln.getString ("remote_calendar"            ) ) ;
        lb6             = new Label (ln.getString ("synchronizing_address_book" ) ) ;
        lb7             = new Label (ln.getString ("synchronizing_calendar"     ) ) ;
        lb8             = new Label (ln.getString ("device_id"                  ) ) ;
        lb10            = new Label (ln.getString ("message_type"               ) ) ;
        lb11            = new Label (ln.getString ("log_level"                  ) ) ;

        tf1             = new TextField      () ;
        tf2             = new TextField      () ;
        tf3             = new TextField      () ;
        tf3.setEchoChar('*');
        tf4             = new TextField      () ;
        tf5             = new TextField      () ;
        tf6             = new TextField      () ;

        cbSyncContact   = new Checkbox       () ;
        cbSyncCalendar  = new Checkbox       () ;

        cbgMessageType  = new CheckboxGroup  () ;
        cbgLogLevel     = new CheckboxGroup  () ;

        cbMsgXML        = new Checkbox (ln.getString ("xml"   )  ,
                                        cbgMessageType ,
                                        true           );

        cbMsgWBXML      = new Checkbox (ln.getString ("wbxml" )  ,
                                        cbgMessageType ,
                                        false          );

        cbLogNone       = new Checkbox (ln.getString ("none"  )  ,
                                        cbgLogLevel    ,
                                        false          );

        cbLogInfo       = new Checkbox (ln.getString ("info"  )  ,
                                        cbgLogLevel    ,
                                        true           );

        cbLogDebug      = new Checkbox (ln.getString ("debug" )  ,
                                        cbgLogLevel    ,
                                        false          );

        configPanel.add  (lb1            ) ;
        configPanel.add  (tf1            ) ;
        configPanel.add  (lb2            ) ;
        configPanel.add  (tf2            ) ;
        configPanel.add  (lb3            ) ;
        configPanel.add  (tf3            ) ;
        configPanel.add  (lb4            ) ;
        configPanel.add  (tf4            ) ;
        configPanel.add  (lb5            ) ;
        configPanel.add  (tf5            ) ;
        configPanel.add  (lb6            ) ;
        configPanel.add  (cbSyncContact  ) ;
        configPanel.add  (lb7            ) ;
        configPanel.add  (cbSyncCalendar ) ;
        configPanel.add  (lb8            ) ;
        configPanel.add  (tf6            ) ;
        configPanel.add  (lb10           ) ;

        messagePanel.add (cbMsgXML       ) ;
        messagePanel.add (cbMsgWBXML     ) ;

        configPanel.add  (messagePanel   ) ;

        configPanel.add  (lb11           ) ;

        logPanel.add     (cbLogNone      ) ;
        logPanel.add     (cbLogInfo      ) ;
        logPanel.add     (cbLogDebug     ) ;

        configPanel.add  (logPanel       ) ;

        butOk = new Button          (ln.getString ("ok" )     ) ;
        butOk.setActionCommand      ("ok"     ) ;
        butOk.addActionListener     (this     ) ;

        butCancel = new Button      (ln.getString ("cancel" ) ) ;
        butCancel.setActionCommand  ("cancel" ) ;
        butCancel.addActionListener (this     ) ;

        buttonPanel = new Panel ();
        buttonPanel.setLayout   (new GridLayout(1, 2) ) ;
        buttonPanel.add         (butOk                ) ;
        buttonPanel.add         (butCancel            ) ;

        add(configPanel, BorderLayout.NORTH ) ;
        add(buttonPanel, BorderLayout.SOUTH ) ;

    }

    /**
     * Invoked when an action occurs (i.e. a button is pressed).
     *
     * @param evt the occurred action
     */
    public void actionPerformed(ActionEvent evt) {

        if (evt.getActionCommand().equals("ok"))            {
            mw.writeConfig (getFields());
            mw.show (KEY_CONTACTLIST);
        } else if (evt.getActionCommand().equals("cancel")) {
            mw.show (KEY_CONTACTLIST);
        }
    }

    //------------------------------------------------------- Protected methods

    /**
     * Sets the fields with some of the values contained
     * in the specified Hashtables.
     * It also sets the checkboxes.
     *
     * @param values DM values from DM_VALUE_PATH
     * @param xmlContactValues  DM values from contact source
     * @param xmlCalendarValues DM values from calendar source
     */
    protected void setAllFields(Hashtable values            ,
                                Hashtable xmlContactValues  ,
                                Hashtable xmlCalendarValues ) {

        String syncContact  = null ;
        String syncCalendar = null ;
        String messageType  = null ;
        String logLevel     = null ;

        tf1.setText ((String) values.get   (PARAM_SYNCMLURL)   ) ;
        tf2.setText ((String) values.get   (PARAM_USERNAME)    ) ;
        tf3.setText ((String) values.get   (PARAM_PASSWORD)    ) ;
        tf4.setText ((String) xmlContactValues.get
                    (PARAM_SOURCEURI)                          ) ;
        tf5.setText ((String) xmlCalendarValues.get
                    (PARAM_SOURCEURI)                          ) ;
        tf6.setText ((String) values.get   (PARAM_DEVICEID)    ) ;

        syncContact  = (String) xmlContactValues.get
                        (PARAM_SYNCMODE                        ) ;
        syncCalendar = (String) xmlCalendarValues.get
                        (PARAM_SYNCMODE                        ) ;
        messageType  = (String) values.get (PARAM_MESSAGETYPE  ) ;
        logLevel     = (String) values.get (PARAM_LOGLEVEL     ) ;

        if (SYNC_TWOWAY.equals(syncContact))          {
            cbSyncContact.setState (true );
        } else                                        {
            cbSyncContact.setState (false);
        }

        if (SYNC_TWOWAY.equals(syncCalendar))         {
            cbSyncCalendar.setState (true  );
        } else                                        {
            cbSyncCalendar.setState (false );
        }

        if (MESSAGE_XML.equals(messageType))          {
            cbMsgXML.setState   (true);
        } else if (MESSAGE_WBXML.equals(messageType)) {
            cbMsgWBXML.setState (true);
        }

        if (LOG_NONE.equals(logLevel))                {
            cbLogNone.setState  (true);
        } else if (LOG_INFO.equals(logLevel))         {
            cbLogInfo.setState  (true);
        } else if (LOG_DEBUG.equals (logLevel))       {
            cbLogDebug.setState (true);
        }

    }

    /**
     * Returns an hashtable containing the values specified in the textfields.
     * Note that this hashtable does not belong
     * to a single node of the management tree.
     *
     * @return a list of values
     */
    protected Hashtable getFields() {

        Hashtable tmpHashtable = new Hashtable();

        String syncContact  = null ;
        String syncCalendar = null ;
        String messageType  = null ;
        String logLevel     = null ;

        if (cbSyncContact.getState())      {
            syncContact = SYNC_TWOWAY ;
        } else                             {
            syncContact = SYNC_NONE   ;
        }

        if (cbSyncCalendar.getState())     {
            syncCalendar = SYNC_TWOWAY ;
        } else                             {
            syncCalendar = SYNC_NONE   ;
        }

        if (cbMsgXML.getState())           {
            messageType = MESSAGE_XML;
        } else if (cbMsgWBXML.getState ()) {
            messageType = MESSAGE_WBXML;
        }

        if (cbLogNone.getState())          {
            logLevel= LOG_NONE;
        } else if (cbLogInfo.getState ())  {
            logLevel= LOG_INFO;
        } else if (cbLogDebug.getState())  {
            logLevel= LOG_DEBUG;
        }

        tmpHashtable.put(PARAM_SYNCMLURL         , tf1.getText () ) ;
        tmpHashtable.put(PARAM_USERNAME          , tf2.getText () ) ;
        tmpHashtable.put(PARAM_PASSWORD          , tf3.getText () ) ;
        tmpHashtable.put(PARAM_SOURCEURICONTACT  , tf4.getText () ) ;
        tmpHashtable.put(PARAM_SOURCEURICALENDAR , tf5.getText () ) ;
        tmpHashtable.put(PARAM_DEVICEID          , tf6.getText () ) ;
        tmpHashtable.put(PARAM_SYNCCONTACT       , syncContact    ) ;
        tmpHashtable.put(PARAM_SYNCCALENDAR      , syncCalendar   ) ;
        tmpHashtable.put(PARAM_MESSAGETYPE       , messageType    ) ;
        tmpHashtable.put(PARAM_LOGLEVEL          , logLevel       ) ;

        return tmpHashtable;
    }

}
