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

package com.funambol.ctp.sender.driver;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.funambol.server.notification.sender.tcp.ctp.ChannelNotificationException;


/**
 * @version $Id: MainMenu.java,v 1.4 2007-11-28 11:26:15 nichele Exp $
 */
public class MainMenu {

    /**
     *
     */
    private CTPSenderDriver sender;

    // ------------------------------------------------------------ Constructors

    public MainMenu() throws ChannelNotificationException {
        sender = new CTPSenderDriver();
    }

    // -------------------------------------------------------------------- Main

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MainMenu mainMenu;
            mainMenu = new MainMenu();
            mainMenu.showMenu();
        } catch (ChannelNotificationException ex) {
            ex.printStackTrace();
        }
    }

    // ---------------------------------------------------------- Public methods

    public  void showMenu() {

        int value = -1;

        while (true) {

            System.out.println(" 0 connect channel");
            System.out.println(" 1 send notification for \"device01\" ");
            System.out.println(" 2 send notification for \"device02\" ");
            System.out.println(" 3 send notification for \"device03\" ");
            System.out.println(".....");
            System.out.println(" 9 disconnect channel");
            System.out.println("10 close channel");

            BufferedReader    indata  = new BufferedReader( new InputStreamReader(System.in) );

            try {
                value = Integer.parseInt(indata.readLine());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            switch (value) {

                case 0:
                    sender.start();
                    break;
                case 1:
                    sender.sendNotification("device01");
                    break;
                case 2:
                    sender.sendNotification("device02");
                    break;
                case 3:
                    sender.sendNotification("device03");
                    break;
                case 9:
                    sender.stop();
                    break;
                case 10:
                    break;
                default:
                    System.out.println("Unrecognized option");
            }
            if (value == 10) {
                break;
            }
        }
        sender.close();
    }
}
