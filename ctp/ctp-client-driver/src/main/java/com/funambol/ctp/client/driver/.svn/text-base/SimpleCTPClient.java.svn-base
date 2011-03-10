/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

import com.funambol.ctp.core.Auth;
import com.funambol.ctp.core.GenericCommand;
import com.funambol.ctp.core.Message;
import com.funambol.ctp.core.Ok;
import com.funambol.ctp.core.Ready;
import com.funambol.ctp.filter.codec.CTPProtocolConstants;
import com.funambol.ctp.util.AuthenticationUtils;
import com.funambol.framework.tools.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @version $Id: SimpleCTPClient.java,v 1.2 2008-05-25 12:07:57 nichele Exp $
 */
public class SimpleCTPClient implements Storager {

    private MinaClient client = null;

    private final Logger logger = Logger.getLogger("funambol.ctp.client.driver");
    
    private Properties prop = new Properties();
    
    private String server   = null;
    private int    port     = 0;
    private String deviceId = null;
    private String username = null;
    private String password = null;
    
    public static void main(String[] args) {
        if (args == null || args.length != 5) {
            System.err.println("Usage: java com.funambol.ctp.client.driver.SimpleCTPClient server port deviceId username password");
            System.exit(-1);
        }
        String server = args[0];
        int port = Integer.parseInt(args[1]);
        String deviceId = args[2];
        String username = args[3];
        String password = args[4];
        
        SimpleCTPClient ctpClient = new SimpleCTPClient(server, port, deviceId, username, password);
        ctpClient.start();
    }

    public SimpleCTPClient(String server, int port, String deviceId, String username, String password) {
        this.server   = server;
        this.port     = port;
        this.deviceId = deviceId;
        this.username = username;
        this.password = password;
        
        readProperties();
        
        try {
            client = new MinaClient(InetAddress.getByName(server), port, this);
        } catch (UnknownHostException ex) {
            logger.error("Error creating the MinaClient", ex);
        }
    }

    public void storeNonce(byte[] nonce) {
        
        prop.setProperty("nonce", new String(Base64.encode(nonce)));
        try {
            File propFile = new File(".", username + ".properties");
            prop.store(new FileOutputStream(propFile), "SimpleCTPClient property file");
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void start() {
        logger.info("Starting...");
        client.connect();
        
        if (!sendAuthorization()) {
            //
            // Retry...
            //
            if (!sendAuthorization()) {
                System.err.println("Not authenticated");
                System.exit(-1);                
            }
        }        
        while (true) {
            try {
                sendReady();
                logger.info("Waiting notification message");
                Thread.sleep(5 * 60000); //  5 minutes
            } catch (InterruptedException ex) {
                logger.error("Error waiting notification message", ex);
            }
        }
        
    }
    
    public boolean sendAuthorization() {
        byte[] nonce = null;
        String sNonce = prop.getProperty("nonce");
        
        if (sNonce == null) {
            nonce = new byte[0];
        } else {
            nonce = Base64.decode(sNonce.getBytes());
        }

        logger.info("Sending auth. request  with: '" +
                    username + "', '" +
                    password + "', '" +
                    new String(Base64.encode(nonce)) + "'");

        String cred = AuthenticationUtils.createDigest(username, password, nonce);

        Auth auth = new Auth(deviceId, username, cred, "");
        Message message = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, auth);
        GenericCommand response = client.sendAndWait(message);

        if (response instanceof Ok) {
            return true;
        }
        return false;
    }

    public void sendReady() {
        Ready ready = new Ready();
        Message message = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, ready);
        GenericCommand response = client.sendAndWait(message);
    }

    // --------------------------------------------------------- Private Methods

    private void readProperties() {
        try {
            File propFile = new File(".", username + ".properties");
            if (propFile.exists()) {
                prop.load(new FileInputStream(propFile));
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }    
}
