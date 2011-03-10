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

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.ComparisonFailure;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.vmguys.vmtools.ota.OtaUpdate;
import org.vmguys.vmtools.ota.UniqueId;
import org.vmguys.vmtools.utils.DomFactory;

import org.apache.tools.ant.Project;

import com.funambol.framework.core.*;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.WBXMLTools;
import org.apache.commons.io.filefilter.SuffixFileFilter;

/**
 * This is a driver for running tests at the protocol level.
 * It requires a directory structure like this:
 * <pre>
 *  {test code}
 *    <i>msg1.xml</i>
 *    <i>msg2.xml</i>
 *    <i>...</i>
 *    reference
 *      <i>msg1.xml</i>
 *      <i>msg2.xml</i>
 *      <i>...</i>
 *    response
 *      <i>msg1.xml</i>
 *      <i>msg2.xml</i>
 *      <i>...</i>
 * </pre>
 * <i>test code</i> is the code name of the test to be performed (i.e. WOSI0001);
 * msg{N}.xml are the messages that the client has to send to the server. For
 * each message sent, the response is stored in the <i>response</i> directory.
 * <p>
 * The directory <i>reference</i> contains the expected response messages used
 * by the comparison tool to identify the differences between the returned
 * messages and the expected values.
 * <p>
 * This class is designe to work as a standalone program. An Ant task is
 * developed as well; @see com.funambol.test.tools.ant.PostSyncMLTask .
 * <p>
 * <b>Syntax</b>
 * <pre>
 * com.funambol.test.tools.PostSyncML {initial URL} {file msg1} ... {file msgN}
 *
 * where:
 *
 * {initial URL}: the URL the first request has to be sent to (the others depend
 *                by the RespURI element in the response.
 * {file msg1} .. {file msgN}: the messages to send to server. They are sent in
 *                             the order they appear on the command line.
 * </pre>
 *
 * @version $Id: PostWBXMLSyncML.java,v 1.2 2007-11-28 10:52:31 nichele Exp $
 */
public class PostWBXMLSyncML extends PostSyncML {

    // --------------------------------------------------------------- Constants

    public static final String LOG_NAME = "funambol.test.tools.PostWBXMLSyncML";

    // ------------------------------------------------------------ Private data

    private byte[][]           msgs         = null;

    private static final Logger log = Logger.getLogger(LOG_NAME);

    // ------------------------------------------------------------ Constructors

    public PostWBXMLSyncML(String  initialURL    ,
                           File     baseDir      ,
                           int      sessionsDelay,
                           String[] ignoreXPaths ,
                           Project  antProject   ,
                           String   xmlBuildFile )
    throws IOException {
        super(initialURL, 
              baseDir,
              sessionsDelay, 
              ignoreXPaths, 
              antProject, 
              xmlBuildFile);
    }

    //----------------------------------------------------------- Public methods

    /**
     * Load the files with the XML messages
     *
     */
    @Override
    public void loadMsgFiles(String[] msgFiles) throws IOException {

        if ((msgFiles == null) || (msgFiles.length == 0)) {
            msgs = new byte[0][];
            msgFiles = new String[0];
        }

        this.msgFiles = msgFiles;

        msgs = new byte[msgFiles.length][];

        for (int i=0; i<msgFiles.length; ++i) {
            msgs[i] = IOTools.readFileBytes(new File(baseDir, msgFiles[i]));
        }
    }

    @Override
    public void syncAndTest() throws IOException, TestFailedException {
        //
        // First of all clean up!
        //
        clean();

        SyncML  response = null;
        String  respURI  = null;

        File    responseFile = null;
        boolean firstMessage = false;

        File fb = new File(baseDir, "header.properties");
        if (fb.exists()) {
            propsHeader = new Properties();
            propsHeader.load(new FileInputStream(fb));
        }

        existResponseProcessor();

        int msgCount = 0;
        for (int i=0; i<msgs.length; ++i) {
            msgCount++;

            try {
                firstMessage = isFirstMessage(msgs[i]);
            } catch (Sync4jException ex) {
                throw new TestFailedException("Error converting the wbxml message in xml",
                                              ex);
            }

            if (firstMessage) {
                try {
                    Thread.currentThread().sleep(sessionsDelay);
                } catch (InterruptedException ex) {
                }
            }

                try {
                log.info("Trying to execute target: " + msgFiles[i]);
                AntUtil.runAntTarget(antProject, baseDir, xmlBuildFile, msgFiles[i]);
                log.info("Target " + msgFiles[i] + " executed");
            } catch (Exception ex) {

                boolean isIn =
                    containsException(ex.getCause(), ComparisonFailure.class.getName());

                if (isIn) {
                    try {
                        saveEndTestError(
                            "Actual Report different than expected.\n"
                            + ex.getLocalizedMessage(),
                            new File(errorDir, "msg-end-test-error.xml"));
                    } catch(IOException e) {
                    }
                }
                log.info("Error executing target " + msgFiles[i] + " (" + ex + ")");
                //do nothing
            }

            File header = new File(baseDir, "header" + msgCount + ".properties");

            if (header.exists()) {
                propsHeader = new Properties();
                propsHeader.load(new FileInputStream(header));
            }

            log.info("Sending " + msgFiles[i]);

            if (firstMessage) {
                //
                // It is a first message so we can set the
                // respURI to empty string
                //
                log.info("Message with id 1. Start new session");

                nextURL = initialURL;
            }

            try {
                response = postRequest(msgs[i]);
            } catch (RepresentationException e) {
                IOTools.writeFile(e.getMessage(), new File(errorDir, msgFiles[i]));
                throw new TestFailedException ("XML syntax error: " + e.getMessage(), e);
            } catch (Sync4jException e) {
                IOTools.writeFile(e.getMessage(), new File(errorDir, msgFiles[i]));
                throw new TestFailedException ("XML syntax error: " + e.getMessage(), e);
            }

            //
            // Write the messages responded by the server, than read the reference
            // and make the comparison (excluding the XPaths specified by
            // ignoreXPaths
            //
            responseFile = new File(responseDir, msgFiles[i]);
            log.info("Writing the response into " + responseFile);

            try {

                // Preprocess response message before comparing it with the
                // reference message
                if (preProcessResp) {
                    String xmlMsg = marshallSyncML(response);
                    xmlMsg = preProcessResponse(responseProcessor, xmlMsg);
                    IOTools.writeFile(WBXMLTools.toWBXML(xmlMsg), responseFile);
                } else {
                    IOTools.writeFile(WBXMLTools.toWBXML(response), responseFile);
                }

                compare(msgFiles[i]);
            } catch (Sync4jException e) {
                IOTools.writeFile(e.getMessage(), new File(errorDir, msgFiles[i]));
                throw new TestFailedException ("WBXML error: " + e.getMessage(), e);
            }

            respURI = response.getSyncHdr().getRespURI();

            if (respURI != null) {
                nextURL = respURI;
            }
        }
    }

    // --------------------------------------------------------- Private methods

    private SyncML postRequest(byte[] request)
    throws IOException, Sync4jException, RepresentationException {
        HttpClientConnection syncMLConnection = new HttpClientConnection(nextURL);
        syncMLConnection.setPropsHeader(propsHeader);
        return syncMLConnection.sendWBXMLMessage(request);
    }

    private void compare(String msgFile)
    throws IOException, Sync4jException, TestFailedException {
        File responseFile  = new File(responseDir , msgFile);
        File referenceFile = new File(referenceDir, msgFile);

        //build reference/response from XML strings, NOT from the files (which contain WBXML)
        String responseXml = WBXMLConverter.readWbxmlAsXml(responseFile);
        String referenceXml = WBXMLConverter.readWbxmlAsXml(referenceFile);

        StringReader responseReader = new StringReader(responseXml);
        StringReader referenceReader = new StringReader(referenceXml);

        SAXBuilder sb = new SAXBuilder();
        sb.setFactory(new DomFactory());

        try {
            Document response  = sb.build(responseReader);
            Document reference = sb.build(referenceReader);

            OtaUpdate update = new OtaUpdate(false);

            UniqueId id = new UniqueId("SyncMLTest", msgFile);
            Element diffs = update.generateDiffs(response.getRootElement() ,
                                                 reference.getRootElement(),
                                                 id                        );

            if (log.isLoggable(Level.FINEST)) {
                saveDiffs(diffs, new File(errorDir, msgFile + ".dbg"));
            }

            if (checkDiffs(diffs)) {
                saveDiffs(diffs, new File(errorDir, msgFile));

                throw new TestFailedException( "Test failed on "
                                             + msgFile
                                             + ". Diff file saved in "
                                             + new File(errorDir, msgFile)
                                             );
            }
        } catch (JDOMException e) {
            IOTools.writeFile(e.getMessage(), new File(errorDir, msgFile));
            throw new TestFailedException("Test failed on "
                                             + msgFile
                                             + ": "
                                             + e.getMessage()
                                             + ". Error message saved in "
                                             + new File(errorDir, msgFile)
                                             );
        }
    }

    /**
     * Removes old files from the working directories
     */
    private void clean() {

        // returns only the file with .wbxml extension
        FileFilter filter = new SuffixFileFilter("wbxml");

        File[] files = responseDir.listFiles(filter);

        for (int i=0; ((files != null) && (i<files.length)); ++i) {
            if(!files[i].delete()) {
                System.out.println("Unable to delete '" + files[i].getName()
                                   + "' in response dir");
            }
        }

        files = errorDir.listFiles(filter);
        for (int i=0; ((files != null) && (i<files.length)); ++i) {
            if(!files[i].delete()) {
                System.out.println("Unable to delete '" + files[i].getName()
                                   + "' in error dir");
            }
        }
    }


    /**
     * Saves the given diff element to the given file
     *
     * @param diffs the diff element
     * @param file the file to save into
     */
    private void saveDiffs(Element diffs, File file) throws IOException {
        XMLOutputter xmlo = new XMLOutputter("  ", true);
        xmlo.setTextNormalize(true);

        FileOutputStream fos = new FileOutputStream(file);
        xmlo.output(diffs, fos);
        fos.close();
    }

    private void saveEndTestError(String cause, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(cause.getBytes());
        fos.close();
    }

    private static void syntax() {
        System.out.println("Syntax: " + (PostWBXMLSyncML.class) + "<initial URL> <msg1> ... <msgN>");
    }


    /**
     * Returns true if the given message has <MsgId> = 1
     * @param msg byte[]
     * @return boolean
     */
    private boolean isFirstMessage(byte[] msg) throws Sync4jException {
        String xml = WBXMLTools.wbxmlToXml(msg);
        if (xml != null && xml.indexOf("<MsgID>1</MsgID>") != -1) {
            return true;
        }
        return false;
    }
    // -------------------------------------------------------------------- Main

    public static void main(String args[])
    throws Exception {
        if(args.length < 2) {
            syntax();
        }

        String[] msgFiles = new String[args.length-1];

        System.arraycopy(args, 1, msgFiles, 0, msgFiles.length);

        PostWBXMLSyncML postsyncml = new PostWBXMLSyncML(args[0],
                                                         new File("."),
                                                         0,
                                                         new String[0],
                                                         null,
                                                         null);
        postsyncml.loadMsgFiles(msgFiles);
        postsyncml.syncAndTest();
    }
}
