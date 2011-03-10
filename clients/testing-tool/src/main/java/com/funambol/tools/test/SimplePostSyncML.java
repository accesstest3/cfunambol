/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.tools.test;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.funambol.framework.core.*;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.SyncMLUtil;

/**
 * This class post a one or more SyncML messages to the given URL writing the
 * response in the same directory of the request message.
 *
 * @version $Id: SimplePostSyncML.java,v 1.4 2008-06-04 09:55:56 nichele Exp $
 */
public class SimplePostSyncML {

    // --------------------------------------------------------------- Constants

    public static String LOG_NAME = "test";

    // ------------------------------------------------------------ Private data

    private String             nextURL      = null;
    private String[]           msgs         = null;
    private String[]           msgFiles     = null;

    private static final Logger log = Logger.getLogger(LOG_NAME);

    // ------------------------------------------------------------ Constructors

    public SimplePostSyncML(String   initialURL  ,
                            String[] msgFiles    )
    throws IOException {
        if ((msgFiles == null) || (msgFiles.length == 0)) {
            msgs = new String[0];
        }

        msgs = new String[msgFiles.length];

        this.msgFiles = msgFiles;
        for (int i=0; i<msgFiles.length; ++i) {
            msgs[i] = IOTools.readFileString(new File(msgFiles[i]));
        }

        nextURL = initialURL;
    }

    //----------------------------------------------------------- Public methods

    public void start() throws IOException, TestFailedException {
        SyncML response = null;
        String  respURI  = null;

        File responseFile = null;
        for (int i=0; i<msgs.length; ++i) {

            log.info("Sending " + msgFiles[i]);

            try {
                response = postRequest(msgs[i]);
            } catch (RepresentationException e) {
                throw new TestFailedException ("XML syntax error: " + e.getMessage(), e);
            } catch (Sync4jException e) {
                throw new TestFailedException ("XML syntax error: " + e.getMessage(), e);
            }

            //
            // Write the messages responded by the server, than read the reference
            // and make the comparison (excluding the XPaths specified by
            // ignoreXPaths
            //
            responseFile = new File(msgFiles[i] + ".out");
            log.info("Writing the response into " + responseFile);

            try {

                IOTools.writeFile(SyncMLUtil.toXML(response), responseFile);

            } catch(Exception e) {
                throw new TestFailedException ("XML syntax error: " + e.getMessage(), e);
            }

            respURI = response.getSyncHdr().getRespURI();

            if (respURI != null) {
                nextURL = respURI;
            }
        }
    }

    // --------------------------------------------------------- Private methods

    private SyncML postRequest(String request)
    throws IOException, Sync4jException, RepresentationException {
        HttpClientConnection syncMLConnection = new HttpClientConnection(nextURL);
        return syncMLConnection.sendMessage(request);
    }

    private static void syntax() {
        System.out.println("Syntax: " + PostSyncML.class.getName() + " <initial URL> <msg1> ... <msgN>");
    }

    // -------------------------------------------------------------------- Main

    public static void main(String args[])
    throws Exception {
        if(args.length < 2) {
            syntax();
            return;
        }

        String[] msgFiles = new String[args.length-1];

        System.arraycopy(args, 1, msgFiles, 0, msgFiles.length);

        SimplePostSyncML postsyncml = new SimplePostSyncML(args[0], msgFiles);
        postsyncml.start();
    }
}
