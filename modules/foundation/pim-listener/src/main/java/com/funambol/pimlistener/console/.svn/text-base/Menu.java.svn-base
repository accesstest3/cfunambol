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
package com.funambol.pimlistener.console;

import java.io.File;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.xml.DOMConfigurator;

import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.beans.BeanTool;

import com.funambol.pimlistener.registry.PimRegistryEntry;
import com.funambol.pimlistener.registry.PimRegistryEntryManager;
import com.funambol.pushlistener.service.config.PushListenerConfiguration;

import com.funambol.server.db.DataSourceContextHelper;


/**
 * Simple admin console
 *
 * @version $Id: Menu.java,v 1.19 2008-08-06 15:09:29 luigiafassina Exp $
 */
public class Menu {

    static {
        System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
    }

    // --------------------------------------------------------------- Constants
    private static final String MENU_BEAN_FILE =
        "com/funambol/pimlistener/console/Menu.xml";

    // -------------------------------------------------------------- Properties

    /**
     * Default taskBeanFile value
     */
    private String defaultTaskBeanFile = null;

    public String getDefaultTaskBeanFile() {
        return defaultTaskBeanFile;
    }

    public void setDefaultTaskBeanFile(String defaultTaskBeanFile) {
        this.defaultTaskBeanFile = defaultTaskBeanFile;
    }

    /**
     * Default period value (in minutes)
     */
    private int defaultPeriod;

    public int getDefaultPeriod() {
        return defaultPeriod;
    }

    public void setDefaultPeriod(int defaultPeriod) {
        this.defaultPeriod = defaultPeriod;
    }

    /**
     * Default psuhContacts value
     */
    private boolean defaultPushContacts = true;

    public boolean isDefaultPushContacts() {
        return defaultPushContacts;
    }

    public void setDefaultPushContacts(boolean defaultPushContacts) {
        this.defaultPushContacts = defaultPushContacts;
    }

    /**
     * Default pushCalendars value
     */
    private boolean defaultPushCalendars = true;

    public boolean isDefaultPushCalendars() {
        return defaultPushCalendars;
    }

    public void setDefaultPushCalendars(boolean defaultPushCalendars) {
        this.defaultPushCalendars = defaultPushCalendars;
    }

    /**
     * Default pushNotes value
     */
    private boolean defaultPushNotes = true;

    public boolean isDefaultPushNotes() {
        return defaultPushCalendars;
    }

    public void setDefaultPushNotes(boolean defaultPushNotes) {
        this.defaultPushNotes = defaultPushNotes;
    }

    /**
     * Default active value
     */
    private boolean defaultActive = true;

    public boolean isDefaultActive() {
        return defaultActive;
    }

    public void setDefaultActive(boolean defaultActive) {
        this.defaultActive = defaultActive;
    }

    // ------------------------------------------------------------ Private data

    /** Registry entry manage */
    private PimRegistryEntryManager manager = null;

    /**
     * Creates a new instace of Menu
     * @throws java.lang.Exception
     */
    public Menu() throws Exception {

        String configPath = PushListenerConfiguration.getConfigPath();
        String log4jFile = configPath + File.separator + "log4j-pimlistener.xml";

        DOMConfigurator.configureAndWatch(log4jFile, 30000); // 30 sec.

        DataSourceContextHelper.configureAndBindDataSourcesForSimpleUsage();

        Runtime.getRuntime()
               .addShutdownHook(new Thread() {
                public void run() {
                    DataSourceContextHelper.closeDataSources();
                }
            });

        manager = new PimRegistryEntryManager();
    }

    // ------------------------------------------------------------- Main method
    /**
     * Runs the admin console
     * @param args 
     */
    public static void main(String[] args) {

        Menu menu = null;

        try {
            String configPath = PushListenerConfiguration.getConfigPath();
            menu = (Menu)BeanTool.getBeanTool(configPath).getBeanInstance(MENU_BEAN_FILE);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }

        menu.show();
    }

    // --------------------------------------------------------- Private methods
    /**
     * Shows repetitively the menu
     */
    private void show() {

        try {

            // clean display
            for (int p = 0; p < 50; p++) {
                System.out.println("");
            }

            int option = -1;

            while (true) {
                option = showMenu();

                if (option == 0) {

                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the menu
     * @return the selected option
     */
    private int showMenu() {

        int value = -1;

        try {
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println("");
            System.out.println(" PIM LISTENER ENTRIES CONSOLE");
            System.out.println("");
            System.out.println(" 0    quit");
            System.out.println("");
            System.out.println(" 11   list entries");
            System.out.println(" 12   list entries by userName");
            System.out.println(" 14   get entry");
            System.out.println(" 15   enable entry");
            System.out.println(" 16   disable entry");
            System.out.println(" 17   insert entry");
            System.out.println(" 18   update entry");
            System.out.println(" 19   mark an entry as deleted");
            System.out.println("");

            value = MenuTools.readInteger();

            switch (value) {

                case 11:

                    listEntries();
                    break;

                case 12:

                    listEntriesByUserName();
                    break;

                case 14:

                    getEntry();
                    break;

                case 15:

                    enableEntry();
                    break;

                case 16:

                    disableEntry();
                    break;

                case 17:

                    insertEntry();
                    break;

                case 18:

                    updateEntry();
                    break;

                case 19:

                    markAsDeleted();
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Unrecognized option");
                    return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();

            return 0;
        }

        return value;
    }

    /**
     * Disables an entry
     * @throws java.lang.Exception 
     */
    private void disableEntry()
    throws Exception {

        long id = MenuTools.readLong("Entry id", 0, Long.MAX_VALUE);
        int updatedRows = manager.disableEntry(id);

        if (updatedRows > 0) {
            System.out.println("\nEntry '" + id + "' disabled");
        } else {
            System.out.println("Entry not found");
        }
    }

    /**
     * Enables an entry
     * @throws java.lang.Exception 
     */
    private void enableEntry()
    throws Exception {

        long id = MenuTools.readLong("Entry id", 0, Long.MAX_VALUE);
        int updatedRows = manager.enableEntry(id);

        if (updatedRows > 0) {
            System.out.println("\nEntry '" + id + "' enabled");
        } else {
            System.out.println("Entry not found");
        }
    }

    /**
     * Lists entries by userName
     * @throws java.lang.Exception 
     */
    private void listEntriesByUserName()
    throws Exception {

        System.out.print("UserName: ");
        String userName = MenuTools.readValue();

        List<PimRegistryEntry> entries  = manager.getEntriesByUserName(userName);
        System.out.println("\nEntries with user name '" + userName + "'");
        System.out.println("==============================================");

        if (entries == null || entries.size() == 0) {
            System.out.println("No entry found");
            return;
        }

        for (PimRegistryEntry entry : entries) {
            System.out.println(entry.toStringForCommandLine());
        }
    }

    /**
     * Gets entry by id
     * @throws java.lang.Exception 
     */
    private void getEntry()
    throws Exception {

        long id = MenuTools.readLong("Id", 0, Long.MAX_VALUE);

        PimRegistryEntry entry = manager.getEntryById(id);

        if (entry == null) {
            System.out.println("No entry found");
            return;
        }

        System.out.println("\nEntry");
        System.out.println("========");

        System.out.println(entry.toStringForCommandLine());

    }


    /**
     * Lists all entries
     * @throws java.lang.Exception 
     */
    private void listEntries()
    throws Exception {

        List<PimRegistryEntry> entries = manager.getEntries();
        System.out.println("\nEntries");
        System.out.println("========");

        if (entries == null || entries.size() == 0) {
            System.out.println("No entry found");
            return;
        }

        for (PimRegistryEntry entry : entries) {
            System.out.println(entry.toStringForCommandLine());
        }
    }

    /**
     * Inserts a new entry-
     *
     * @throws java.lang.Exception 
     */
    private void insertEntry() throws Exception {

        String userName = MenuTools.readNotEmptyValue("UserName");

        String taskBeanFile =
            MenuTools.readValue("Task bean file", defaultTaskBeanFile);

        int periodInMinutes = MenuTools.readInteger("Period (in minutes)",
                                                    1,
                                                    Integer.MAX_VALUE,
                                                    defaultPeriod);
        
        
        long period = (long)(60000L * periodInMinutes);

        boolean active = MenuTools.readBoolean("Activation", defaultActive);

        boolean pushContact =
            MenuTools.readBoolean("Push contacts", defaultPushContacts);
        boolean pushCalendar =
            MenuTools.readBoolean("Push calendars (events/tasks)",
                                  defaultPushCalendars);
        boolean pushNote =
            MenuTools.readBoolean("Push notes", defaultPushNotes);

        List<PimRegistryEntry> entries  = manager.getEntriesByUserName(userName);
        if (entries != null && !entries.isEmpty()) {
            System.out.println("An entry for this user already exists");
            return;
        }

        System.out.println("Creating new entry...");
        long id = manager.createEntry(userName,
                                      period,
                                      active,
                                      taskBeanFile,
                                      pushContact,
                                      pushCalendar,
                                      pushNote);
        System.out.println("New entry created with id: " + id);
    }

    /**
     * Updates an entry
     * @throws java.lang.Exception 
     */
    private void updateEntry()
    throws Exception {

        long id = MenuTools.readLong("Id Entry", 0, Long.MAX_VALUE);

        PimRegistryEntry entry = manager.getEntryById(id);

        if (entry == null){
            System.out.println("No entry found");
            return;
        }

        String userName = MenuTools.readValue("UserName", entry.getUserName());
        entry.setUserName(userName);

        String taskBeanFile = MenuTools.readValue("Task bean file",
                                                  entry.getTaskBeanFile());
        entry.setTaskBeanFile(taskBeanFile);

        int periodInMinutes  = MenuTools.readInteger("Period (in minutes)",
                                                     1,
                                                     Integer.MAX_VALUE,
                                                     (int)(entry.getPeriod() / 60000));

        long period = (long)(60000L * periodInMinutes);

        entry.setPeriod(period);

        boolean active = MenuTools.readBoolean("Activation", entry.getActive());
        entry.setActive(active);

        boolean pushContacts = MenuTools.readBoolean("Push contacts", entry.isPushContacts());
        entry.setPushContacts(pushContacts);

        boolean pushCalendars = MenuTools.readBoolean("Push calendars (events/tasks)", entry.isPushCalendars());
        entry.setPushCalendars(pushCalendars);

        boolean pushNotes = MenuTools.readBoolean("Push notes", entry.isPushNotes());
        entry.setPushNotes(pushNotes);

        System.out.println("Trying to update entry with id '" + id + "'");
        int updatedRows = manager.updateEntry(entry);
        if (updatedRows > 0) {
            System.out.println("Entry updated");
        } else {
            System.out.println("Entry not found");
        }
    }

    /**
     * Marks an entry as deleted
     * @throws java.lang.Exception 
     */
    private void markAsDeleted()
    throws Exception {

        long id = MenuTools.readLong("Id", 0, Long.MAX_VALUE);

        System.out.println("\nTrying to delete entry with id '" + id + "'");
        int updatedEntries = manager.markAsDeleted(id);
        if (updatedEntries > 0) {
            System.out.println("Entry '" + id + "' marked as deleted");
        } else {
            System.out.println("Entry '" + id + "' not found");
        }
    }

}