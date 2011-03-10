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

package com.funambol.ctp.server.filter;

import com.funambol.ctp.server.config.CTPServerConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;

/**
 * Dump all incoming and outgoing messages.
 *
 * @version $Id: DumperFilter.java,v 1.2 2007-11-28 11:26:15 nichele Exp $
 */
public class DumperFilter extends IoFilterAdapter {

    /**
     * The output stream where incoming messages are dumped
     */
    FileOutputStream inDumpStream;

    /**
     * The output stream where outgoing messages are dumped
     */
    FileOutputStream outDumpStream;

    /**
     * The name of the directory where the dump files are stored
     */
    private String dumpDirName = "data";

    /**
     * The name of the file where the dump of all incoming messages are dumped
     */
    private String inDumpFileName = "inMessages.dmp";

    /**
     * The name of the file where the dump of all incoming messages are dumped
     */
    private String outDumpFileName = "outMessages.dmp";

    /**
     *
     */
    private File dumpDir;


    /**
     * Creates a new instance.
     */
    public DumperFilter()  {

        dumpDir = new File(dumpDirName);
        if (!dumpDir.exists()) {
            dumpDir.mkdir();
        }
    }

    /**
     * Dump the received message
     */
    public void messageReceived(NextFilter nextFilter, IoSession session,
            Object message) {

        try {
            String remoteAddress = session.getRemoteAddress().toString();
            remoteAddress = remoteAddress.replace("[\\/:*?\"<>|]","_");
            File inFile = new File(dumpDir, inDumpFileName + "_" + remoteAddress);
            if (!inFile.exists()) {
                inFile.createNewFile();
            }
            inDumpStream = new FileOutputStream(inFile , true);

            if (message instanceof ByteBuffer) {

                ByteBuffer byteBuffer = (ByteBuffer)message;

                byte[] msg = new byte[byteBuffer.limit()];
                System.arraycopy(byteBuffer.array(), 0, msg, 0, byteBuffer.limit());

//                inDumpStream.write((new Date().toString().getBytes()));
//                inDumpStream.write(">>>".getBytes());
                inDumpStream.write(msg);
//                inDumpStream.write("<<<".getBytes());
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        nextFilter.messageReceived(session, message);
    }

    public void messageSent(NextFilter nextFilter, IoSession session,
            Object message) {
        try {
            String remoteAddress = session.getRemoteAddress().toString().substring(1);
            remoteAddress = remoteAddress.replace(':','_');
            File outFile = new File(dumpDir, outDumpFileName + "_" + remoteAddress);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            outDumpStream = new FileOutputStream(outFile , true);

            if (outDumpStream != null) {
                if (message instanceof ByteBuffer) {

                    ByteBuffer byteBuffer = (ByteBuffer)message;

                    byte[] msg = new byte[byteBuffer.limit()];
                    System.arraycopy(byteBuffer.array(), 0, msg, 0, byteBuffer.limit());
//                    outDumpStream.write((new Date().toString().getBytes()));
//                    outDumpStream.write(">>>".getBytes());
                    outDumpStream.write(msg);
//                    outDumpStream.write("<<<".getBytes());
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        nextFilter.messageSent(session, message);
    }
}
