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

package com.funambol.ctp.client.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 *
 * @version $Id: MultiClient.java,v 1.2 2007-11-28 11:26:14 nichele Exp $
 */
public class MultiClient {

    // ------------------------------------------------------------ Private data

    private MinaClientManager client;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>Class</code>.
     */
    public MultiClient() throws UnknownHostException {
        client = new MinaClientManager();
    }

    // -------------------------------------------------------------------- Main

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            MultiClient multiClient;
            multiClient = new MultiClient();
            multiClient.showMenu();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public  void showMenu() {

        while (true) {

            System.out.println(" 0 open all connections");
            System.out.println(".....");
            System.out.println("10 close all connections");

            InputStreamReader iReader = new InputStreamReader(System.in);
            BufferedReader    indata  = new BufferedReader(iReader);

            int value = -1;
            try {
                value = Integer.parseInt(indata.readLine());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            switch (value) {
                case 0:
                    client.start();
                    break;
                case 10:
                    client.stop();
                    break;
                default:
                    System.out.println("Unrecognized option");
            }
            if (value == 10) {
                break;
            }
        }
    }

}
