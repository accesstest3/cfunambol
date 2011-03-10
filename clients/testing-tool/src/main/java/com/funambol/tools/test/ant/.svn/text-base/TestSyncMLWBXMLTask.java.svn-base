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
package com.funambol.tools.test.ant;

import java.io.*;
import java.util.ArrayList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;

import com.funambol.tools.test.TestFailedException;
import com.funambol.tools.test.PostWBXMLSyncML;

/**
 * This is an Ant task that executes the protocol level tests.
 * <p>
 * @see com.funambol.tools.test.PostSyncML
 * <p
 * It can be used in the following manner:
 *
 * <pre>
 * &lt;task name="test-syncml" ...&gt;
 *   &lt;testsyncml url="{initial URL}" test="{test name}"&gt;
 *     &lt;patternset&gt;
          &lt;exclude name="/SyncML/SyncHdr/SessionID/self::node()[1]"/&gt;
          &lt;exclude name="/SyncML/SyncHdr/RespURI/self::node()[1]"/&gt;
        &lt;/patternset&gt;
 *   &lt;/testsyncml&gt;
 * &lt;/task&gt;
 * </pre>
 *
 * @version $Id: TestSyncMLWBXMLTask.java,v 1.2 2007-11-28 10:52:31 nichele Exp $
 */
public class TestSyncMLWBXMLTask extends TestSyncMLBaseTask {

    // -------------------------------------------------------------- Properties

    // ----------------------------------------------------- Task Implementation

    public void execute() throws BuildException {
        //
        // make sure we don't have an illegal set of options
        //
        validateAttributes();

        //
        // Deal with the specified XPath to exclude from the comparison of
        // the XML messages. They are specified through one or more inner
        // <patternset> elements.
        //
        ArrayList allExcludeXPaths = new ArrayList();

        PatternSet ps = null;
        String[] xpaths = null;
        for (int i = 0; i < xpathPatterns.size(); i++) {
            ps = (PatternSet) xpathPatterns.get(i);
            xpaths = ps.getExcludePatterns(getProject());
            for (int j = 0; j < xpaths.length; ++j) {
                allExcludeXPaths.add(xpaths[j]);
            }
        }  // next i

        log("Initial URL: "    + url             );
        log("Test: "           + test            );
        log("Ignored XPaths: " + allExcludeXPaths);

        xpaths = (String[])allExcludeXPaths.toArray(new String[0]);

        PostWBXMLSyncML post = null;
        try {
            post = new PostWBXMLSyncML(
                url                    ,
                new File(basedir, test),
                sessionsDelay          ,
                xpaths                 ,
                this.getProject()      ,
                basedir + File.separator + test + File.separator + "build.xml"
            );

            //
            // We execute the operations specified into start_test target before
            // loading the wbxml messages to be able to modify these files (for example
            // we should have the possibility to change the EOL of these files)
            //
            post.startTest();

        } catch (IOException e) {
            log(e.getMessage(), Project.MSG_ERR);
            throw new BuildException("Error executing PostSyncMLTask", e);
        }

        //
        // Get the files
        //
        File dir = new File(basedir, test);
        String[] msgFiles = dir.list(new TestMessageFileFilter("wbxml"));

        //
        // If there is no files means that the test directory not exist or
        // there is no test message to execute
        //
        if (msgFiles == null) {
            log("No message file found in " + dir.getAbsolutePath());
            return;
        }

        msgFiles = removeBuildFile(msgFiles);
        msgFiles = ordersAndFilterFiles(msgFiles);

        try {

            //
            // Now we can load the messages files
            //
            post.loadMsgFiles(msgFiles);

            //
            // Execute the test
            //
            post.syncAndTest();

            log("Test " + test + " passed!");
        } catch (IOException e) {
            log(e.getMessage(), Project.MSG_ERR);
            throw new BuildException("Error executing PostSyncMLTask", e);
        } catch (TestFailedException e) {
            log("Test " + test + " failed: " + e.getMessage(), Project.MSG_INFO);
        } finally {
            post.endTest();
        }
    }

}
