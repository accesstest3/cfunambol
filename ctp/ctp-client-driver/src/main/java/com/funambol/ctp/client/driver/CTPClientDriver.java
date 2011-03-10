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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.funambol.ctp.core.Auth;
import com.funambol.ctp.core.Message;
import com.funambol.ctp.core.Bye;
import com.funambol.ctp.core.Ready;
import com.funambol.ctp.filter.codec.CTPProtocolConstants;
import com.funambol.ctp.util.AuthenticationUtils;

import com.funambol.framework.tools.Base64;

/**
 *
 * @version $Id: CTPClientDriver.java,v 1.3 2007-11-28 11:26:15 nichele Exp $
 */
public class CTPClientDriver implements Storager {

    private MinaClient client = null;

    public Properties  prop = new Properties();
    private String dirPath = "config";
    private String propertyFileName = "ctpclient.properties";

    private final Logger logger = Logger.getLogger("funambol.ctp.client.driver");

    private String lastRequestUserid;

    /**
     *
     */
    private String server =   "";

    /**
     *
     */
    private String from =     "";

    // -------------------------------------------------------------

    public CTPClientDriver() throws UnknownHostException, IOException {
        readProperties();
        int port = 0;
        try {
          port = Integer.parseInt(prop.getProperty("PORT"));
        } catch (NumberFormatException ex) {
            port = 4745;
            logger.warn("Using default port number: " + port);
        }
        server = prop.getProperty("HOST");
        client = new MinaClient(InetAddress.getByName(server), port, this);
    }



    // -------------------------------------------------------------------------

    public void openConnection() {
        logger.debug("CTPClientDriver openConnection");
        client.connect();
    }

    public void sendAuthorization(String userid) {
        logger.debug("CTPClientDriver sendAuthorization for " + lastRequestUserid);

        lastRequestUserid = userid;

        String username = prop.getProperty("USERNAME_" + userid);
        String password = prop.getProperty("PASSWORD_" + userid);
        byte[] nonce    = Base64.decode(prop.getProperty("NONCE_" + userid).getBytes());
        String deviceid = prop.getProperty("DEVICEID_" + userid);
        if (username == null || password == null ||
            nonce == null || deviceid == null) {

            logger.warn("Unable to retrieve configuration for " + userid);
            return;
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Sending auth. request  with: '" +
                         username + "', '" +
                         password + "', '" +
                         new String(Base64.encode(nonce)) + "'");
        }
        String cred = AuthenticationUtils.createDigest(username, password, nonce);

        Auth auth = new Auth(deviceid, username, cred, from);
        Message message = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, auth);
        client.send(message);
    }

    public void sendReady() {
        logger.debug("CTPClientDriver sendReady");
        Ready ready = new Ready();
        Message message = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, ready);
        client.send(message);
    }

    public void sendBye() {
        logger.debug("CTPClientDriver sendBye");
        Bye bye = new Bye();
        Message message = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, bye);
        client.send(message);
    }

    public void closeConnection() {
        logger.debug("CTPClientDriver closeConnection");
        client.quit();
    }

    public void storeNonce(byte[] nonce) {
        prop.setProperty("NONCE_" + lastRequestUserid, new String(Base64.encode(nonce)));
        try {
            File propFile = new File(dirPath, propertyFileName);
            prop.store(new FileOutputStream(propFile), "CTPClient property file");
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // --------------------------------------------------------- Private Methods

    private void readProperties() {
        try {
            File propFile = new File(dirPath, propertyFileName);
            if (propFile.exists()) {
                prop.load(new FileInputStream(propFile));
            } else {
                prop.store(new FileOutputStream(propFile), "CTPClient property file");
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
