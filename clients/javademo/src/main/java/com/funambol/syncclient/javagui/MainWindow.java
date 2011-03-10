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
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.xml.sax.SAXException;

import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.converter.CalendarToSIFE;
import com.funambol.common.pim.converter.ContactToSIFC;
import com.funambol.common.pim.converter.ConverterException;
import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.sif.*;

import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.MD5;

import com.funambol.syncclient.common.DeviceTools;
import com.funambol.syncclient.common.SourceUtils;
import com.funambol.syncclient.common.logging.Logger;

import com.funambol.syncclient.javagui.logging.PanelHandler;

import com.funambol.syncclient.spdm.DMException;
import com.funambol.syncclient.spdm.ManagementNode;
import com.funambol.syncclient.spdm.SimpleDeviceManager;

import com.funambol.syncclient.spds.SyncManager;

import com.funambol.syncclient.spds.event.SyncEvent;
import com.funambol.syncclient.spds.event.SyncItemEvent;
import com.funambol.syncclient.spds.event.SyncSourceEvent;
import com.funambol.syncclient.spds.event.SyncStatusEvent;
import com.funambol.syncclient.spds.event.SyncTransportEvent;
import com.funambol.syncclient.spds.event.SyncListener;
import com.funambol.syncclient.spds.event.SyncItemListener;
import com.funambol.syncclient.spds.event.SyncSourceListener;
import com.funambol.syncclient.spds.event.SyncStatusListener;
import com.funambol.syncclient.spds.event.SyncTransportListener;

/**
 * The main window for the SyncClient Demo GUI.
 *
 * @version $Id: MainWindow.java,v 1.3 2008-06-25 13:20:59 testa Exp $
 */
public class MainWindow
extends Frame
implements ConfigurationParameters,
           SyncItemListener       ,
           SyncListener           ,
           SyncSourceListener     ,
           SyncStatusListener     ,
           SyncTransportListener  {

    // --- Folders Tree ---
    //
    // + (root)
    // |+ config
    // ||+ spds
    // |||+ sources
    // ||||- contact.properties
    // ||||- calendar.properties
    // |||
    // |||- syncml.properties
    // ||
    // ||+ xml
    // |||- init.xml
    // |||- map.xml
    // |||- mod.xml
    // ||
    // ||- application.properties
    // |
    // |+ db
    //  |+ contacts
    //  |+ calendar

    //---------------------------------------------------------------- Constants

    public static final String DIR_CONTACTS           = "contacts"             ;
    public static final String DIR_CALENDAR           = "calendar"             ;
    public static final String DM_VALUE_PATH          = "spds/syncml"          ;
    public static final String DM_VALUE_CONTACT_PATH  = "spds/sources/contact" ;
    public static final String DM_VALUE_CALENDAR_PATH = "spds/sources/calendar";
    public static final String PROP_CHARSET           = "spds.charset"         ;
    public static final String PROP_WD                = "wd"                   ;
    public static final String DATE_FORMAT            = "yyyyMMdd'T'HHmmss"    ;
    public static final String ROOT_DIRECTORY         = "config"               ;
    public static final String VALUE_UTF8             = "UTF8"                 ;
    public static final String XML_VERSION =
        "<?xml version=\"1.0\" encoding=\"" +
        System.getProperty("file.encoding") +
        "\"?>";

    //----------------------------------------------------------- Protected data

    /**
     * A list of Contact objects
     */
    protected Vector contacts;

    /**
     * A list of Calendar objects
     */
    protected Vector calendars;

    //------------------------------------------------------------- Private data

    /**
     * DM root node
     */
    private ManagementNode rootNode = null;

    /**
     * DM values from DM_VALUE_PATH
     */
    private Hashtable syncmlValues = null;

    /**
     * DM values from DM_VALUE_CONTACT_PATH
     */
    private Hashtable xmlContactValues = null;

    /**
     * DM values from DM_VALUE_CALENDAR_PATH
     */
    private Hashtable xmlCalendarValues = null;

    /**
     * Current index in the contacts Vector
     */
    private Integer currentIndex = null;

    /**
     * The root directory
     */
    private String rootDirectory = null;

    /**
     * The source directory
     */
    private String sourceDirectory = "db";

    private CardLayout     cardLayout      = null;
    private Panel          mainPanel       = null;
    private DemoMenuBar    menubar         = null;
    private CalendarNew    calNew          = null;
    private CalendarList   calList         = null;
    private CalendarModify calendarModify  = null;
    private ContactNew     contNew         = null;
    private ContactList    contList        = null;
    private ContactModify  contactModify   = null;

    private PanelHandler  panelHandler = null;
    private Configuration config       = null;

    private Language ln     =  new Language();
    private Logger   logger =  new Logger  ();

    private ContactToSIFC  xmlContactConverter  = new ContactToSIFC(null, null);
    private CalendarToSIFE xmlCalendarConverter = new CalendarToSIFE(null, null);

    //----------------------------------------------------------- Public methods

    /**
     * Creates a new main window. This constructor also initializes the logger,
     * the DeviceManager and fetches synchronization and contacts informations
     * from the filesystem
     */
    public MainWindow() {

        super();
        setTitle(ln.getString("funambol_javagui"));

        Image icon = Toolkit.getDefaultToolkit().getImage(FRAME_ICONNAME);

        setIconImage(icon);

        Panel      bottomPanel = null;
        Properties props       = null;

        contacts  = new Vector();
        calendars = new Vector();

        rootDirectory = System.getProperty(PROP_WD);

        props = System.getProperties();
        if (System.getProperty(SimpleDeviceManager.PROP_DM_DIR_BASE) == null) {
            props.put(SimpleDeviceManager.PROP_DM_DIR_BASE,
                      buildPath(rootDirectory, ROOT_DIRECTORY));
            System.setProperties(props);
        }
        props = System.getProperties();
        props.put(PROP_CHARSET, VALUE_UTF8);
        System.setProperties(props);

        sourceDirectory = buildPath(rootDirectory,sourceDirectory);

        //
        // --- Device Manager ---
        //
        rootNode = SimpleDeviceManager.getDeviceManager().getManagementTree();

        checkDeviceID();

        loadConfiguration();

        //
        // --- Layout Creation ---
        //
        int width  = 440;
        int height = 510;
        setSize(width, height);

        this.setResizable(false);

        // Get the current screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setLocation((int)(screenSize.getWidth () - width ) / 2,
                    (int)(screenSize.getHeight() - height) / 2);

        setLayout(new BorderLayout());
        mainPanel = new Panel();

        //
        // A little trick to add blank space on the bottom of the window
        //
        bottomPanel = new Panel();
        bottomPanel.setSize(240, 5);

        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        menubar  = new DemoMenuBar(this);
        contList = new ContactList(this);

        refresh();

        contactModify  = new ContactModify (this);
        calendarModify = new CalendarModify(this);
        contNew        = new ContactNew    (this);
        panelHandler   = new PanelHandler  (this);
        config         = new Configuration (this);

        calList = new CalendarList(this);
        calNew  = new CalendarNew (this);

        //
        // Add a menu and all the components to the card layout
        //
        setMenuBar(menubar);

        mainPanel.add(contList      , KEY_CONTACTLIST   );
        mainPanel.add(contactModify , KEY_CONTACTMODIFY );
        mainPanel.add(calendarModify, KEY_CALENDARMODIFY);
        mainPanel.add(contNew       , KEY_CONTACTNEW    );
        mainPanel.add(panelHandler  , KEY_SYNC          );
        mainPanel.add(config        , KEY_CONFIG        );
        mainPanel.add(calList       , KEY_CALENDARLIST  );
        mainPanel.add(calNew        , KEY_CALENDARNEW   );

        add(mainPanel  ,BorderLayout.CENTER);
        add(bottomPanel,BorderLayout.SOUTH );

        // Default card
        cardLayout.show(mainPanel, KEY_CONTACTLIST);

        // Add the listeners
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                exit();
            }
        } );
    }

    /**
     * Refreshes the contact list reading the local vCard files
     */
    public void refresh() {
        contacts = getContacts();
        contacts = sortContacts(contacts);
        contList.fillList();
        show(KEY_CONTACTLIST);
    }


    /**
     * Notify Synchronization begin
     *
     * @param event
     */
    public void syncBegin(SyncEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug("SyncEvent - Sync begin - date: " + event.getDate());
        }
    }

    /**
     * Notify Synchronization end
     *
     * @param event
     */
    public void syncEnd(SyncEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug("SyncEvent - Sync end - date: " + event.getDate());
        }
    }

    /**
     * Notify send initialization message
     *
     * @param event
     */
    public void sendInitialization(SyncEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug("SyncEvent - Send initialization - date: " +
                         event.getDate()                            );
        }
    }

    /**
     * Notify send modification message
     *
     * @param event
     */
    public void sendModification(SyncEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug("SyncEvent - Send modification - date: " +
                         event.getDate()                          );
        }
    }

    /**
     * Notify send finalization message
     *
     * @param event
     */
    public void sendFinalization(SyncEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug("SyncEvent - Send finalization - date: " +
                         event.getDate()                          );
        }
    }

    /**
     * Notify that the engine encountered a not blocking error
     *
     * @param event
     */
    public void syncError(SyncEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug("SyncEvent - Sync error - date: " +
                         event.getDate    ()               +
                         " - message: "                    +
                         event.getMessage ()               +
                         " - cause: "                      +
                         event.getCause().getMessage()     );
        }
    }

    /**
     * Notify a syncSource begin synchronization
     *
     * @param event
     */
    public void syncBegin(SyncSourceEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug("SyncSourceEvent - Sync begin - date: " +
                         event.getDate      ()                   +
                         " - sourceUri: "                        +
                         event.getSourceUri ()                   +
                         " - sync mode: "                        +
                         event.getSyncMode  ()                   );
        }
    }

    /**
     * Notify a syncSource end synchronization
     *
     * @param event
     */
    public void syncEnd(SyncSourceEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug("SyncSourceEvent - Sync end - date: " +
                         event.getDate      ()                 +
                         " - sourceUri: "                      +
                         event.getSourceUri ()                 +
                         " - sync mode: "                      +
                         event.getSyncMode  ()                 );
        }
    }

    /**
     * Notify SyncStransport begin send data
     *
     * @param event
     */
    public void sendDataBegin(SyncTransportEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncTransportEvent - Send data begin - data length: " +
                 event.getData()                                       );
        }
    }

    /**
     * Notify SyncStransport end send data
     *
     * @param event
     */
    public void sendDataEnd(SyncTransportEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncTransportEvent - Send data end - data length: " +
                 event.getData()                                      );
        }
    }

    /**
     * Notify SyncStransport begin receive data
     *
     * @param event
     */
    public void receiveDataBegin(SyncTransportEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncTransportEvent - Receive data begin - data length: " +
                 event.getData()                                           );
        }
    }

    /**
     * Notify SyncStransport receiving data
     *
     * @param event
     */
    public void dataReceived(SyncTransportEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncTransportEvent - Data received - data length: " +
                 event.getData()                                      );
        }
    }

    /**
     * Notify SyncStransport end receive data
     *
     * @param event
     */
    public void receiveDataEnd(SyncTransportEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncTransportEvent - Received data end - data length: " +
                 event.getData()                                          );
        }
    }

    /**
     * Notify an item added by the server
     *
     * @param event
     */
    public void itemAddedByServer(SyncItemEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncItemEvent - Item added by server - sourceUri: " +
                 event.getSourceUri()                                 +
                 " - key: "                                           +
                 event.getItemKey().getKeyAsString()                  );
        }
    }

    /**
     * Notify an item deleted by the server
     *
     * @param event
     */
    public void itemDeletedByServer(SyncItemEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncItemEvent - Item deleted by server - sourceUri: " +
                 event.getSourceUri()                                   +
                 " - key: "                                             +
                 event.getItemKey().getKeyAsString()                    );
        }
    }

    /**
     * Notify an item updated by the server
     *
     * @param event
     */
    public void itemUpdatedByServer(SyncItemEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncItemEvent - Item updated by server - sourceUri: " +
                 event.getSourceUri()                                   +
                 " - key: "                                             +
                 event.getItemKey().getKeyAsString()                   );
        }
    }

    /**
     * Notify an item added by the client
     *
     * @param event
     */
    public void itemAddedByClient(SyncItemEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncItemEvent - Item added by client  - sourceUri: " +
                 event.getSourceUri()                                  +
                 " - key: "                                            +
                 event.getItemKey().getKeyAsString()                   );
        }
    }

    /**
     * Notify an item delete by the client
     *
     * @param event
     */
    public void itemDeletedByClient(SyncItemEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncItemEvent - Item deleted by client - sourceUri: " +
                 event.getSourceUri()                                  +
                 " - key: "                                            +
                 event.getItemKey().getKeyAsString()                   );
        }
    }

    /**
     * Notify an item updated by the client
     *
     * @param event
     */
    public void itemUpdatedByClient(SyncItemEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncItemEvent - Item updated by client - sourceUri: " +
                 event.getSourceUri()                                  +
                 " - key: "                                            +
                 event.getItemKey().getKeyAsString()                   );
        }
    }

    /**
     * Notify a status received from the server
     *
     * @param event
     */
    public void statusReceived (SyncStatusEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncStatusEvent - Received status - command: " +
                 event.getCommand()                              +
                 " - status: "                                   +
                 event.getStatusCode()                           +
                 " - sourceUri: "                                +
                 event.getSourceUri()                            +
                 " - key: "                                      +
                 event.getItemKey().getKeyAsString()             );
        }
    }

    /**
     * Notify create a status to send to the server
     *
     * @param event
     */
    public void statusToSend (SyncStatusEvent event) {
        if (logger.isLoggable(Logger.DEBUG)) {
            logger.debug
                ("SyncStatusEvent - Status to send - command: " +
                 event.getCommand()                             +
                 " - status: "                                  +
                 event.getStatusCode()                          +
                 " - sourceUri: "                               +
                 event.getSourceUri()                           +
                 " - key: "                                     +
                 event.getItemKey().getKeyAsString()            );
        }
    }

    //-------------------------------------------------------- Protected methods

    /**
     * Closes the window and stops the application
     */
    protected void exit() {

        //
        // hide the Frame
        //
        setVisible(false);

        dispose();

        //
        // tell windowing system to free resources
        //
        if (logger.isLoggable(Logger.INFO)) {
            logger.info(ln.getString("logging_stopped"));
        }

        System.exit(0);
    }

    /**
     * Manages the contact synchronization, based on the configuration
     * parameters provided by the DeviceManager
     */
    public void synchronize() {

        checkDeviceID();

        panelHandler.setButton(false);

        try {

            if (logger.isLoggable(Logger.INFO)) {
                logger.info(ln.getString("setting_up_sync_manager"));
            }

            Logger.setHandler(panelHandler);
            SyncManager syncManager = SyncManager.getSyncManager("");

            syncManager.addSyncItemListener     (this);
            syncManager.addSyncListener         (this);
            syncManager.addSyncSourceListener   (this);
            syncManager.addSyncStatusListener   (this);
            syncManager.addSyncTransportListener(this);

            if (logger.isLoggable(Logger.INFO)) {
                logger.info(ln.getString("synchronizing"));
            }

            syncManager.sync();

            if (logger.isLoggable(Logger.INFO)) {
                logger.info(ln.getString("done"));
            }

            panelHandler.setButton(true);

        } catch (Exception e) {
            if (e.getMessage()==null) {

                if (logger.isLoggable(Logger.INFO)) {
                    logger.info(ln.getString("sync_exception") +
                                " "                            +
                                e                              );
                }

            } else {
                if (logger.isLoggable(Logger.INFO)) {
                    logger.info(e.getMessage());
                }
            }

            if (logger.isLoggable(Logger.INFO)) {
                logger.info(this.getClass().getName() +
                            " synchronize"            +
                            e.getMessage()            );
            }

            panelHandler.setButton(true);
        }
    }

    /**
     * Refreshes the calendar list reading the local calendar files
     */
    protected void refreshCalendar() {
        calendars = getCalendars();
        calList.fillList();
    }

    /**
     * Refreshes the contact list reading the local contact files
     */
    protected void refreshContact() {

        contacts = getContacts() ;
        contacts = sortContacts(contacts);
        contList.fillList();
    }

    /**
     * Shows the specified card in the CardLayout.
     * When showing the contact modification card, the method fills all the fields.
     * When showing the configuration card, the method fills all the fields.
     * When showing the new contact card, the method blanks all the fields.
     * When showing the synchronization card, the method starts a new synchronization.
     *
     * @param card the card to be shown
     */
    protected void show(String card) {

        cardLayout.show(mainPanel,card);

        if (card.equals(KEY_CALENDARLIST)) {

            refreshCalendar();

        } else if (card.equals(KEY_CALENDARMODIFY)) {

            calendarModify.fillFields(
                ((DemoCalendar)calendars.elementAt(
                    getCurrentIndex().intValue())
                ).getCalendar()
            );

        } else if (card.equals(KEY_CALENDARNEW)) {

            calNew.blankFields();

        } else if (card.equals(KEY_CONFIG)) {
            checkDeviceID();
            loadConfiguration();
            config.setAllFields(syncmlValues     ,
                                xmlContactValues ,
                                xmlCalendarValues);

        } else if (card.equals(KEY_CONTACTLIST)) {

            refreshContact();

        } if (card.equals(KEY_CONTACTMODIFY)) {

            contactModify.fillFields(
                ((DemoContact)contacts.elementAt(
                    getCurrentIndex().intValue())
                ).getContact()
            );

        } else if (card.equals(KEY_CONTACTNEW)) {

            contNew.blankFields();

        } else if (card.equals(KEY_SYNC)) {

            synchronize();

        }
    }

    /**
     * Returns a vector of Contact elements containing the list of contacts
     * found in the sourceDirectory
     *
     * @return a Vector of Contacts, or an empty vector if no contacts are found
     */
    protected Vector getContacts() {

        try {

            File    curFile       = null;
            Vector  existingFiles = null;
            Vector  contacts      = new Vector();
            Contact contact       = null;

            existingFiles = getExistingFiles(DIR_CONTACTS);

            for (int i=0, l = existingFiles.size(); i < l; i++) {

                curFile = (File)existingFiles.elementAt(i);

                try {

                    if (logger.isLoggable(Logger.DEBUG)) {
                        logger.debug(ln.getString("parsing_file") +
                                     " "                          +
                                     curFile                      );
                    }

                    SIFCParser xmlParser =
                        new SIFCParser(new FileInputStream(curFile));
                    contact = (Contact)xmlParser.parse();
                    contacts.addElement
                        (new DemoContact(curFile.getName(),contact));

                } catch (SAXException e) {

                    if (logger.isLoggable(Logger.DEBUG)) {
                        logger.debug(ln.getString("error_sax_exception") +
                                     " "                                 +
                                     curFile                             +
                                     ": "                                +
                                     e.getMessage()                      );
                    }
                }
            }
            return contacts;

        } catch (IOException e) {
            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_reading_files") +
                             " "                                 +
                             e.getMessage()                      );
            }
        }

        return new Vector();
    }

    /**
     * Returns a vector of Calendar elements containing the list of calendar
     * found in the sourceDirectory
     *
     * @return a Vector of Calendar, or an empty vector if no calendars are found
     */
    protected Vector getCalendars() {

        try {

            File     curFile       = null;
            Vector   existingFiles = null;
            Vector   calendars     = new Vector();
            Calendar calendar      = null;

            existingFiles = getExistingFiles(DIR_CALENDAR);

            for (int i=0, l = existingFiles.size(); i < l; i++) {

                curFile = (File)existingFiles.elementAt(i);

                try {

                    if (logger.isLoggable(Logger.DEBUG)) {
                        logger.debug(ln.getString("parsing_file") +
                                     " "                          +
                                     curFile                      );
                    }

                    SIFCalendarParser xmlParser =
                        new SIFCalendarParser(new FileInputStream(curFile));
                    calendar = (Calendar)xmlParser.parse();
                    calendars.addElement
                        (new DemoCalendar(curFile.getName(), calendar));
                } catch (SAXException e) {

                    if (logger.isLoggable(Logger.DEBUG)) {
                        logger.debug(ln.getString("error_sax_exception") +
                                     " "                                 +
                                      curFile                            +
                                     ": "                                +
                                     e.getMessage()                      );
                    }
                }
            }

            return calendars;

        } catch (IOException e) {
            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_reading_files") +
                             " "                                 +
                             e.getMessage()                      );
            }
        }

        return new Vector();
    }

    /**
     * Overwrites the indexth contact with the specified contact
     *
     * @param contact the new contact
     * @param index the index of the contact to be overwritten
     */
    protected void writeModContact(Contact contact, int index) {

        File             tmpFile         = null;
        File             f               = null;
        FileOutputStream out             = null;
        PrintStream      ps              = null;

        String           output          = null;

        HashMap          hashMapFromFile = null;
        HashMap          hashMap         = null;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

            contact.setRevision(sdf.format(new Date()));

             //
            // Conversion into XML. Modified way
            //
            output = xmlContactConverter.convert(contact);

            f = new File(buildPath(sourceDirectory, DIR_CONTACTS),
                         ((DemoContact)contacts.elementAt(
                              getCurrentIndex().intValue())
                         ).getFilename()
                        );

            hashMapFromFile = SourceUtils.xmlToHashMap(new FileInputStream(f));

            hashMap = SourceUtils.xmlToHashMap(output);

            hashMapFromFile.putAll(hashMap);

            //
            // Delete old file
            //
            f.delete();

            //
            // Modify the contacts Vector
            //
            ((DemoContact)contacts.elementAt(index)).setContact(contact);

            contList.fillList();

            //
            // Write new file (same name as the old one)
            //
            tmpFile = new File(buildPath(sourceDirectory,DIR_CONTACTS),
                               ((DemoContact)contacts.elementAt(index)
                               ).getFilename()
                              );

            out = new FileOutputStream(tmpFile);
            ps  = new PrintStream     (out    );

            String value = SourceUtils.hashMapToXml(hashMapFromFile);

            if (value.indexOf(XML_VERSION) != -1) {
                ps.print(value);
            } else {
                ps.print(XML_VERSION + value);
            }

        } catch (IOException e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_updating_file") +
                             " "                                 +
                             e.getMessage()                      );
            }

        } catch (Exception exc) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_updating_file") +
                             " "                                 +
                             exc.getMessage()                    );
            }

        } finally {
            if (ps != null) {
                try { ps.close();  } catch (Exception e) { }
                ps = null;
            }
            if (out != null) {
                try { out.close(); } catch (Exception e) { }
                out = null;
            }
        }
    }

    /**
     * Creates a new contact which will be inserted in the contacts list
     *
     * @param contact the new contact
     */
    protected void writeNewContact(Contact contact) {

        SimpleDateFormat sdf      = null;
        Date             tmpDate  = null;

        String           fileName = null;

        String           output   = null;

        File             tmpFile  = null;
        FileOutputStream out      = null;
        PrintStream      ps       = null;

        try {

            sdf     = new SimpleDateFormat(DATE_FORMAT);
            tmpDate = new Date();
            contact.setRevision(sdf.format(tmpDate));

            fileName = String.valueOf(tmpDate.getTime());

            //
            // When the user creates a new Contact, set the "File as" with
            // last name + ", " + first name
            //
            String lastname  = contact.getName().getLastName().getPropertyValueAsString();
            String firstname = contact.getName().getFirstName().getPropertyValueAsString();
            String display_name = contact.getName().getDisplayName().getPropertyValueAsString();

            if (display_name == null || display_name.equals("")) {
                StringBuffer dn = new StringBuffer("");

                if (lastname != null && !lastname.equals("")) {
                    dn.append(lastname);
                }
                if (firstname != null && !firstname.equals("")) {
                    if (dn.length() > 0) {
                        dn.append(", ");
                    }
                    dn.append(firstname);
                }

                if (dn.length() == 0) {
                    dn.append("Unknown");
                }
                contact.getName().getDisplayName().setPropertyValue(dn.toString());
            }

            //
            // Conversion into XML. Modified way
            //
            output = xmlContactConverter.convert(contact);

            //
            // Add to the contacts Vector
            //
            contacts.addElement(new DemoContact(fileName, contact));

            contList.fillList();

            //
            // Write to filesystem
            //
            tmpFile = new File(buildPath(sourceDirectory, DIR_CONTACTS),
                               fileName                                );

            try {
                out = new FileOutputStream(tmpFile);
            } catch (IOException e) {
                (new File(buildPath(sourceDirectory, DIR_CONTACTS))).mkdirs(); ;
                out = new FileOutputStream(tmpFile);
            }
            ps  = new PrintStream     (out    );

            //
            // work around about SyncConnectorDB, ID tag must be in message
            //
            output = output.substring(0, output.indexOf("</contact>")) +
                     "<id>"                                            +
                     fileName                                          +
                     "</id>"                                           +
                     output.substring(   output.indexOf("</contact>")  );

            ps.print(output);

        } catch (IOException e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_creating_file") +
                             " "                                 +
                             e.getMessage()                      );
            }
        } catch (ConverterException e) {
            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_creating_file") +
                             " "                                 +
                             e.getMessage()                      );
            }
        } finally {
            if (ps != null) {
                try { ps.close();  } catch (Exception e) { }
                ps = null;
            }
            if (out != null) {
                try { out.close(); } catch (Exception e) { }
                out = null;
            }
        }
    }

    /**
     * Deletes the currently selected contact
     */
    protected void deleteContact() {

        try {
            new File(buildPath(sourceDirectory,DIR_CONTACTS),
                     ((DemoContact)contacts.elementAt(
                         getCurrentIndex().intValue())
                     ).getFilename()
                    ).delete();

            refresh();
        } catch (NullPointerException e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_deleting_file") +
                             " "                                 +
                             e.getMessage()                      );
            }
        }
    }

    /**
     * Deletes the currently selected calendar
     */
    protected void deleteCalendar() {
        try {
            new File(buildPath(sourceDirectory,DIR_CALENDAR),
                     ((DemoCalendar) calendars.elementAt(
                          getCurrentIndex().intValue())
                     ).getFilename()
                    ).delete();
            refreshCalendar();
        } catch (NullPointerException e) {
            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_deleting_file") +
                             " "                                 +
                             e.getMessage()                      );
            }
        }
    }

    /**
     * Creates a new contact which will be inserted in the contacts list
     *
     * @param calendar the new contact
     */
    protected void writeNewCalendar(Calendar calendar) {

        Date             tmpDate  = null;

        String           output   = null;
        String           fileName = null;

        File             tmpFile  = null;
        FileOutputStream out      = null;
        PrintStream      ps       = null;

        try {

            tmpDate = new Date();

            fileName = String.valueOf(tmpDate.getTime());

            //
            // Conversion into XML. Modified way
            //
            output = xmlCalendarConverter.convert(calendar);

            //
            // Add to the calendars Vector
            //
            calendars.addElement(new DemoCalendar(fileName, calendar));
            calList.fillList();

            //
            // Write to filesystem
            //
            tmpFile = new File(buildPath(sourceDirectory, DIR_CALENDAR),
                               fileName                               );

            try {
                out = new FileOutputStream(tmpFile);
            } catch (IOException e) {
                (new File(buildPath(sourceDirectory, DIR_CALENDAR))).mkdirs(); ;
                out = new FileOutputStream(tmpFile);
            }
            ps  = new PrintStream     (out    );

            //
            // work around about SyncConnectorDB, ID tag must be in message
            //
            output = output.substring(0, output.indexOf("</appointment>")) +
                     "<id>"                                                +
                     fileName                                              +
                     "</id>"                                               +
                     output.substring(   output.indexOf("</appointment>")  );

            ps.print(output);

        }  catch (IOException e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_creating_file") +
                             " "                                 +
                             e.getMessage()                      );
            }
        } catch (ConverterException e) {
            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_creating_file") +
                             " "                                 +
                             e.getMessage()                      );
            }
        }  finally {
            if (ps != null) {
                try { ps.close();  } catch (Exception e) { }
                ps = null;
            }
            if (out != null) {
                try { out.close(); } catch (Exception e) { }
                out = null;
            }
        }
    }

    /**
     * Overwrites the indexth contact with the specified contact
     *
     * @param calendar the new contact
     * @param index the index of the contact to be overwritten
     */
    protected void writeModCalendar(Calendar calendar, int index) {

        String           output          = null;

        HashMap          hashMapFromFile = null;
        HashMap          hashMap         = null;

        File             f               = null;

        File             tmpFile         = null;
        FileOutputStream out             = null;
        PrintStream      ps              = null;

        try {

            //
            // Conversion into XML. Modified way
            //
            output = xmlCalendarConverter.convert(calendar);

            f = new File(buildPath(sourceDirectory, DIR_CALENDAR),
                         ((DemoCalendar) calendars.elementAt(
                              getCurrentIndex().intValue())
                         ).getFilename()
                        );

            hashMapFromFile = SourceUtils.xmlToHashMap(IOTools.readFileString(f));

            hashMap = SourceUtils.xmlToHashMap(output);

            hashMapFromFile.putAll(hashMap);

            //
            // Delete old file(
            //
            f.delete();

            //
            // Modify the contacts Vector
            //
            ((DemoCalendar)calendars.elementAt(index)).setCalendar(calendar);
            contList.fillList();

            //
            // Write new file (same name as the old one)
            //
            tmpFile = new File(buildPath(sourceDirectory,DIR_CALENDAR),
                               ((DemoCalendar)calendars.elementAt(index)).getFilename()
                              );

            out = new FileOutputStream(tmpFile);
            ps =  new PrintStream     (out    );

            String value = SourceUtils.hashMapToXml(hashMapFromFile);

            if (value.indexOf(XML_VERSION) != -1) {
                ps.print(value);
            } else {
                ps.print(XML_VERSION + value);
            }

        } catch (IOException e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_updating_file") +
                             " "                                 +
                             e.getMessage()                      );
        }

        } catch (Exception e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("error_updating_file") +
                             " "                                 +
                             e.getMessage()                      );
            }

        } finally {
            if (ps != null) {
                try { ps.close  (); } catch (Exception e) { }
                ps = null;
            }
            if (out != null) {
                try { out.close (); } catch (Exception e) { }
                out = null;
            }
        }
    }

    /**
     * Saves the specified configuration parameters by passing them to the
     * DeviceManager
     *
     * @param values a list of configuration values
     */
    protected void writeConfig(Hashtable values) {

        try {

            if (logger.isLoggable(Logger.INFO)) {
                logger.info(ln.getString("writing_configuration"));
            }

            syncmlValues.put(PARAM_SYNCMLURL  , values.get(PARAM_SYNCMLURL  ));
            syncmlValues.put(PARAM_USERNAME   , values.get(PARAM_USERNAME   ));
            syncmlValues.put(PARAM_PASSWORD   , values.get(PARAM_PASSWORD   ));
            syncmlValues.put(PARAM_DEVICEID   , values.get(PARAM_DEVICEID   ));
            syncmlValues.put(PARAM_MESSAGETYPE, values.get(PARAM_MESSAGETYPE));
            syncmlValues.put(PARAM_LOGLEVEL   , values.get(PARAM_LOGLEVEL   ));

            xmlCalendarValues.put(PARAM_SYNCSOURCEURI,
                                  values.get(PARAM_SOURCEURICALENDAR));
            xmlCalendarValues.put(PARAM_SYNCMODE,
                                  values.get(PARAM_SYNCCALENDAR     ));

            xmlContactValues.put(PARAM_SYNCSOURCEURI,
                                 values.get(PARAM_SOURCEURICONTACT));
            xmlContactValues.put(PARAM_SYNCMODE,
                                 values.get(PARAM_SYNCCONTACT     ));

            rootNode.setValue(DM_VALUE_PATH              ,
                              PARAM_TARGETLOCALURI       ,
                              values.get(PARAM_SYNCMLURL));
            rootNode.setValue(DM_VALUE_PATH              ,
                              PARAM_SYNCMLURL            ,
                              values.get(PARAM_SYNCMLURL));
            rootNode.setValue(DM_VALUE_PATH             ,
                              PARAM_USERNAME            ,
                              values.get(PARAM_USERNAME));
            rootNode.setValue(DM_VALUE_PATH             ,
                              PARAM_PASSWORD            ,
                              values.get(PARAM_PASSWORD));
            rootNode.setValue(DM_VALUE_CONTACT_PATH          ,
                              PARAM_SYNCSOURCEURI            ,
                              values.get(PARAM_SOURCEURICONTACT  ));
            rootNode.setValue(DM_VALUE_CONTACT_PATH        ,
                              PARAM_SYNCMODE               ,
                              values.get(PARAM_SYNCCONTACT));
            rootNode.setValue(DM_VALUE_CALENDAR_PATH             ,
                              PARAM_SYNCSOURCEURI                ,
                              values.get(PARAM_SOURCEURICALENDAR));
            rootNode.setValue(DM_VALUE_CALENDAR_PATH        ,
                              PARAM_SYNCMODE                ,
                              values.get(PARAM_SYNCCALENDAR));
            rootNode.setValue(DM_VALUE_PATH             ,
                              PARAM_DEVICEID            ,
                              values.get(PARAM_DEVICEID));
            rootNode.setValue(DM_VALUE_PATH                ,
                              PARAM_MESSAGETYPE            ,
                              values.get(PARAM_MESSAGETYPE));
            rootNode.setValue(DM_VALUE_PATH             ,
                              PARAM_LOGLEVEL            ,
                              values.get(PARAM_LOGLEVEL));

        } catch (DMException e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("device_manager_error") +
                             " "                                  +
                             e.getMessage()                       );
            }

        } catch (Exception e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("unhandled_exception") +
                             " "                                 +
                             e.toString()                        );
            }
        }

        checkDeviceID();
    }

    /**
     * Returns the index of the currently selected contact
     *
     * @return the index of the currently selected contact
     */
    protected Integer getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Sets the current index, thus selecting a contact
     *
     * @param index the value the current index should be set to
     */
    protected void setCurrentIndex(Integer index) {
        currentIndex = index;
    }

    //---------------------------------------------------------- Private methods

    /**
     * Returns a string representing the path of the specified dir
     * concatenating it with the specified source directory
     *
     * @param sourceDirectory the source directory
     * @param dir the directory
     * @return the directory's path
     */
    private String buildPath(String sourceDirectory, String dir) {
        return sourceDirectory+"/"+dir;
    }

    /**
     * Returns a vector of File elements containing the list of files in the
     * sourceDirectory
     *
     * @return a list of files, or an empty vector if the directory is empty
     */
    private Vector getExistingFiles(String dir) throws IOException {
        Vector ret = new Vector();

        //File[] files = new File(buildPath(sourceDirectory,dir)).listFiles();
        String[] fileNames = new File(buildPath(sourceDirectory,dir)).list();
        if (fileNames != null) {
            for (int i = 0; i<fileNames.length; ++i) {
                ret.addElement(new File(buildPath(sourceDirectory,dir),
                               fileNames[i])                          );
            }
        }

        return ret;
    }

    /**
     * Sorts contacts
     *
     * @param contacts to sort
     * @return sorthed contacts
     */
    private Vector sortContacts(Vector contacts) {

        ContactComparator contactComparator = null;
        DemoContact[]     dcs               = null;

        if (!(contacts.size () > 0)) {
             return contacts;
        }

        dcs = new DemoContact[contacts.size()];

        for (int i=0, l = contacts.size(); i < l; i++) {
            dcs[i] = (DemoContact)contacts.elementAt(i);
        }

        contactComparator = new ContactComparator();

        Arrays.sort(dcs, contactComparator);

        contacts = new Vector();
        for (int i=0, l = dcs.length; i < l; i++) {
            contacts.add(dcs[i]);
        }

        return contacts;
    }

    /**
     * Checks if the device id is not null or empty. If so, a new device id will
     * be created and stored in the dm tree
     */
    private void checkDeviceID() {
        String deviceID = null;
        try {
            deviceID = (String)rootNode.getNodeValue(DM_VALUE_PATH, PARAM_DEVICEID);
            if (deviceID == null || deviceID.length() == 0) {
                deviceID = DeviceTools.createDeviceID(PREFIX_DEVICE_ID, DEFAULT_DEVICE_ID);
                rootNode.setValue(DM_VALUE_PATH, PARAM_DEVICEID, deviceID);
            }
        } catch (DMException e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("cannot_find_devicemanager_values") +
                             " "                                              +
                             e.getMessage()                                   );
            }
        }
    }

    /**
     * Loads the DM tree
     */
    private void loadConfiguration() {
        try {
            syncmlValues      = rootNode.getNodeValues(DM_VALUE_PATH         );
            xmlContactValues  = rootNode.getNodeValues(DM_VALUE_CONTACT_PATH );
            xmlCalendarValues = rootNode.getNodeValues(DM_VALUE_CALENDAR_PATH);
        } catch (DMException e) {

            if (logger.isLoggable(Logger.DEBUG)) {
                logger.debug(ln.getString("cannot_find_devicemanager_values") +
                             " "                                              +
                             e.getMessage()                                   );
            }
        }
    }

}
