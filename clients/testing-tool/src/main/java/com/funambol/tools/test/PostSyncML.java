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
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import junit.framework.ComparisonFailure;

import bsh.Interpreter;
import bsh.TargetError;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import org.jibx.runtime.*;

import org.vmguys.vmtools.ota.OtaUpdate;
import org.vmguys.vmtools.ota.UniqueId;
import org.vmguys.vmtools.utils.DomFactory;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tools.ant.Project;

import com.funambol.framework.core.*;
import com.funambol.framework.tools.IOTools;

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
 * @version $Id: PostSyncML.java,v 1.3 2008-02-25 14:37:29 luigiafassina Exp $
 */
public class PostSyncML {

    // --------------------------------------------------------------- Constants

    public static final String LOG_NAME = "funambol.test.tools.PostSyncML";

    public static final String FILE_ERROR     = "error"    ;
    public static final String FILE_RESPONSE  = "response" ;
    public static final String FILE_REFERENCE = "reference";

    // ------------------------------------------------------------ Private data
    private bsh.This bshThis = null;

    // ---------------------------------------------------------- Protected data
    protected String     nextURL      = null;
    protected String     initialURL   = null;
    protected String[]   msgs         = null;
    protected String[]   msgFiles     = null;
    protected String[]   ignoreXPaths = null;
    protected Properties propsHeader  = null;

    protected Project    antProject   = null;
    protected String     xmlBuildFile = null;

    protected File baseDir = null;
    protected File responseDir = null, referenceDir = null, errorDir = null;

    int sessionsDelay = 0;

    protected static final Logger log = Logger.getLogger(LOG_NAME);

    protected boolean preProcessResp = false;
    protected File responseProcessor = null;

    // ------------------------------------------------------------ Constructors

    public PostSyncML(String   initialURL   ,
                      File     baseDir      ,
                      int      sessionsDelay,
                      String[] ignoreXPaths ,
                      Project  antProject   ,
                      String   xmlBuildFile )
    throws IOException {

        this.baseDir      = baseDir;
        this.responseDir  = new File(baseDir, FILE_RESPONSE );
        this.referenceDir = new File(baseDir, FILE_REFERENCE);
        this.errorDir     = new File(baseDir, FILE_ERROR    );

        this.sessionsDelay = sessionsDelay;

        this.ignoreXPaths = ignoreXPaths;
        this.antProject   = antProject;
        this.xmlBuildFile = xmlBuildFile;
        this.initialURL   = initialURL;

        nextURL = initialURL;
    }

    //----------------------------------------------------------- Public methods
    /**
     * Load the files with the XML messages
     *
     * @param msgFiles the list of message files
     * @throws IOException if an error occurs
     */
    public void loadMsgFiles(String[] msgFiles) throws IOException {
        if ((msgFiles == null) || (msgFiles.length == 0)) {
            msgs     = new String[0];
            msgFiles = new String[0];
        }
        this.msgFiles = msgFiles;

        msgs = new String[msgFiles.length];

        for (int i=0; i<msgFiles.length; ++i) {
            msgs[i] = IOTools.readFileString(new File(baseDir, msgFiles[i]));
        }
    }

    /**
     * Execute the task start_test
     * You can use this task to execute the operation before running the test.
     */
    public void startTest() {
        try {
            log.info("Trying to execute target: start_test");
            AntUtil.runAntTarget(antProject, baseDir, xmlBuildFile, "start_test");
            log.info("Target start_test executed");

        } catch (Exception ex) {
            log.info("Error executing target start_test (" + ex + ")");
            log.throwing(this.getClass().getName(), "startTest", ex);
            //do nothing
        }
    }

    /**
     * Execute the task end_test
     * You can use this task to execute the operation after running the test
     * both if it is failed and if it is passed.
     */
    public void endTest() {
        try {
            log.info("Trying to execute target: end_test");
            AntUtil.runAntTarget(antProject, baseDir, xmlBuildFile, "end_test");
            log.info("Target end_test executed");
        } catch (Exception ex) {

            boolean isIn =
                containsException(ex.getCause(), ComparisonFailure.class.getName());

            if (isIn) {
                try {
                    log.info("Comparison Failure ");
                    saveEndTestError(
                        "Actual Report different than expected.\n"
                        + ex.getLocalizedMessage(),
                        new File(errorDir, "msg-end-test-error.xml"));
                } catch(IOException e) {
                }
            }

            log.info("Error executing target end_test (" + ex + ")");
            log.throwing(this.getClass().getName(), "endTest", ex);
            //do nothing
        }
    }

    public void syncAndTest() throws IOException, TestFailedException {
        //
        // First of all clean up!
        //
        clean();

        SyncML response = null;
        String respURI  = null;

        File responseFile    = null;
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
            
            firstMessage = msgs[i].indexOf("<MsgID>1</MsgID>") != -1;

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
                        log.info("Comparison Failure ");
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

            //
            // Read ant properties
            //
            Properties prop = new Properties();

            File f = new File(baseDir, msgFiles[i] + ".properties");
            if (f.exists()) {
                prop.load(new FileInputStream(f));
            }

            String propertiesValues = prop.getProperty("replace");

            //
            // Replace value_N into message before sending response
            //
            if (propertiesValues != null) {
                StringTokenizer values = new StringTokenizer(propertiesValues, ",");
                int y = 1;
                while (values.hasMoreTokens()) {
                    String v = values.nextToken();
                    msgs[i] = msgs[i].replaceAll("VALUE_" + y, v);
                    y++;
                }
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
                Timestamp t = new Timestamp(System.currentTimeMillis());
                String message = "<!-- " + t.toString() + " -->\n" + e.getMessage();
                IOTools.writeFile(message, new File(errorDir, msgFiles[i]));
                throw new TestFailedException ("XML syntax error: " + e.getMessage(), e);
            } catch (Sync4jException e) {
                Timestamp t = new Timestamp(System.currentTimeMillis());
                String message = "<!-- " + t.toString() + " -->\n" + e.getMessage();
                IOTools.writeFile(message, new File(errorDir, msgFiles[i]));
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

                String xmlMsg = marshallSyncML(response);

                // Preprocess response message before comparing it with the
                // reference message
                if (preProcessResp) {
                    xmlMsg = preProcessResponse(responseProcessor, xmlMsg);
                }

                IOTools.writeFile(xmlMsg, responseFile);

            } catch(Exception e) {
                e.printStackTrace();
                throw new TestFailedException ("XML syntax error: " + e.getMessage(), e);
            }

            compare(msgFiles[i]);

            respURI = response.getSyncHdr().getRespURI();

            if (respURI != null) {
                nextURL = respURI;
            }
        }
    }

    // ------------------------------------------------------- Protected methods
    /**
     * Checks if the given diffs Element contains significant differences, so
     * that differences on any node excluding the ones specified by the
     * <i>ignoreXPaths</i> XPaths.
     *
     * @param diffs the differences to be inspected
     *
     * @return <i>true</i> if there is at least one difference in one of the not
     *         ignored XPaths, or <i>false</i> otherwise.
     */
    protected boolean checkDiffs(Element diffs) {
        List positions = diffs.getChildren("Position", diffs.getNamespace());

        Element position = null;

        Iterator i = positions.iterator();
        while(i.hasNext()) {
            position = (Element)i.next();

            if (!ignore(position.getAttributeValue("XPath"))) {
                //
                // This means a difference!
                //
                return true;
            }
        }

        return false;
    }

    /**
     * Preprocesses the response message using the given Processor (that mush
     * be a .bsh file)
     *
     * @param responseProcessor the .bsh processor
     * @param xmlResponse the response
     * @return the processed response
     */
    protected String preProcessResponse(File responseProcessor, String xmlResponse) {
        String newResponse = xmlResponse;

        try {
            Interpreter interpreter = new Interpreter();
            interpreter.eval(new FileReader(responseProcessor));
            
            Object obj = interpreter.eval(";return this;");
            bshThis = (bsh.This)obj;
            newResponse = exec("responseProcessor", xmlResponse);

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        
        return newResponse;
    }

    protected String marshallSyncML(SyncML syncML) throws Sync4jException {
        String msg = null;
        try {

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            IBindingFactory f = BindingDirectory.getFactory("binding", SyncML.class);
            IMarshallingContext c = f.createMarshallingContext();
            c.setIndent(0);
            c.marshalDocument(syncML, "UTF-8", null, bout);
            msg = new String(bout.toByteArray());

        } catch(Exception e) {
            e.printStackTrace();
            throw new Sync4jException(e);
        }
        return msg;
    }

    /**
    * Exec the given method calling it on the configured bsh file.
    * This version is useful for methods that returns a String and that have just
    * Strings as arguments.
     *
    * @param method the method to invoke
    * @param args the arguments of the method
    * @return the string returned by the invoked method
    * @throws java.lang.Throwable if an error occurs
    */
    protected String exec(String method, String... args) throws Throwable {
       Object o = null;

       try {
           o = bshThis.invokeMethod(method, args);
       } catch (TargetError e) {
           throw e.getTarget();
       }

       if (o == null) {
           return null;
       }
       if (o == bsh.Primitive.NULL) {
           return null;
       }
       return (String)o;
    }

    protected void existResponseProcessor() {
        responseProcessor = new File(baseDir, "responseProcessor.bsh");
        if (responseProcessor.exists()) {
            preProcessResp = true;
        } else {
            responseProcessor = new File(antProject.getBaseDir(), "responseProcessor.bsh");
            if (responseProcessor.exists()) {
                preProcessResp = true;
            }
        }
    }

    /**
     * Checks if the class name of the ROOT cause is equals the given class
     * name. If it is not equals, all the class name of the Throwable list will
     * be checked.
     *
     * @param th the Throwable list
     * @param className the class name of exception to find
     * @return true if the class name is found, false otherwise
     */
    protected boolean containsException(Throwable th, String className) {
        if (th == null) {
            return false;
        }
        Throwable thRootCause = ExceptionUtils.getRootCause(th);
        if (thRootCause != null &&
            className.equals(thRootCause.getClass().getName())) {
            return true;
        }

        List<Throwable> throwableList = ExceptionUtils.getThrowableList(th);
        if (throwableList != null) {
            for (Throwable throwable: throwableList) {
                String thClassName = throwable.getClass().getName();
                if (className.equals(thClassName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // --------------------------------------------------------- Private methods

    private SyncML postRequest(String request)
    throws IOException, Sync4jException, RepresentationException {
        HttpClientConnection syncMLConnection = new HttpClientConnection(nextURL);
        syncMLConnection.setPropsHeader(propsHeader);
        return syncMLConnection.sendMessage(request);
    }

    private void compare(String msgFile)
    throws IOException, TestFailedException {
        File responseFile  = new File(responseDir , msgFile);
        File referenceFile = new File(referenceDir, msgFile);

        SAXBuilder sb = new SAXBuilder();
        sb.setFactory(new DomFactory());

        try {
            Document response  = sb.build(responseFile );
            Document reference = sb.build(referenceFile);

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

        // returns only the file with .xml extension
        FileFilter filter = new SuffixFileFilter("xml");

        File[] files = responseDir.listFiles(filter);
        for (int i=0; ((files != null) && (i<files.length)); ++i) {
            if (!files[i].delete()) {
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
     * Checks if the given XPath is one of the ignored XPath
     *
     * @param xPath the xPath to check
     *
     * @return <i>true</i> is the xPath in the list of the ignored XPaths,
     *         <i>false</i> otherwise.
     */
    private boolean ignore(String xPath) {

        for (int i=0;
             ((xPath != null) && (ignoreXPaths != null) && (i<ignoreXPaths.length));
             ++i) {
            if (xPath.equals(ignoreXPaths[i])) {
                return true;
            }
        }

        return false;
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
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String m = "<!-- " + t.toString() + " -->\n";
        fos.write(m.getBytes());
        xmlo.output(diffs, fos);
        fos.close();
    }

    private void saveEndTestError(String cause, File file) throws IOException {

        FileOutputStream fos = new FileOutputStream(file);
        Timestamp t = new Timestamp(System.currentTimeMillis());
        String m = "<!-- " + t.toString() + " -->\n";
        fos.write(m.getBytes());
        fos.write(cause.getBytes());
        fos.close();
    }

    private static void syntax() {
        System.out.println("Syntax: " + (PostSyncML.class) + "<initial URL> <msg1> ... <msgN>");
    }

    // -------------------------------------------------------------------- Main

    public static void main(String args[])
    throws Exception {
        if(args.length < 2) {
            syntax();
        }

        String[] msgFiles = new String[args.length-1];

        System.arraycopy(args, 1, msgFiles, 0, msgFiles.length);

        PostSyncML postsyncml = new PostSyncML(args[0],
                                               new File("."),
                                               0,
                                               new String[0],
                                               null,
                                               null);
        postsyncml.loadMsgFiles(msgFiles);
        postsyncml.syncAndTest();
    }
}
