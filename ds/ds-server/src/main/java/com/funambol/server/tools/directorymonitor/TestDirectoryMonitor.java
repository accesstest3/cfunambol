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

package com.funambol.server.tools.directorymonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Simple test class for the DirectoryMonitor
 *
 * @version $Id: TestDirectoryMonitor.java,v 1.1.1.1 2008-02-21 23:36:02 stefano_fornari Exp $
 */
public class TestDirectoryMonitor implements FileChangeListener {

    private DirectoryMonitor fileMonitor = null;

    public TestDirectoryMonitor(long scanPeriod) {

        fileMonitor = new DirectoryMonitor(new File("config"), scanPeriod);

    }

    public void runMonitor() {
        fileMonitor.runMonitor();
    }

    public static void main(String[] args) throws Exception {
        TestDirectoryMonitor testFileMonitor = new TestDirectoryMonitor(10000);
        testFileMonitor.fileMonitor.addFileChangeListener(testFileMonitor);
        testFileMonitor.menu();
   }

   public void menu() {
       while (true) {
           try {

               int value = 0;
               value = mainMenu();

               switch (value) {
               case 0:
                    System.out.println("quit");
                    System.exit(1);
                   break;
               case 1:
                   run();
                   break;
               case 2:
                   enable();
                   break;
               case 3:
                   disable();
                   break;
               case 4:
                   reset();
                   break;
               default:
                   System.out.println("error");
               }
           } catch (java.lang.Exception e) {
               e.printStackTrace();
           }
       }
   }


   public void run() {
       fileMonitor.runMonitor();
   }

   public void enable() {
       fileMonitor.enable();
   }

   public void disable() {
       fileMonitor.disable();
   }

   public void reset() {
       fileMonitor.reset();
   }

    public int mainMenu()
    {
      String option = null;
      int value = -1;

      try {
         System.out.println("");
         System.out.println("");
         System.out.println(" FileSystemMonitor");
         System.out.println("");
         System.out.println(" 0   quit");
         System.out.println(" 1   run");
         System.out.println(" 2   enable");
         System.out.println(" 3   disable");
         System.out.println(" 4   reset");
         System.out.println(" ");

         value = readInteger();

        } catch(java.lang.Exception e){
          e.printStackTrace();
          return 0;
        }
        return value;
   }

   public static int readInteger() throws Exception {

       InputStreamReader iReader = new InputStreamReader(System.in);
       BufferedReader indata     = new BufferedReader(iReader);

       int value = -1;
       String temp = null;
       temp = indata.readLine();
       value = Integer.parseInt(temp);

       return value;
  }

    public void fileChange(FileChangeEvent fileChangeEvent) {
        System.out.println("" + System.currentTimeMillis() + " -- event (" +
                           (fileChangeEvent.getFile().isFile() ? "file" : "") +
                           (fileChangeEvent.getFile().isDirectory() ? "directory" : "") +
                           "): " + fileChangeEvent);

    }

}
