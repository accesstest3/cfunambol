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

import java.io.File;
import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Ant;
import org.apache.tools.ant.types.PatternSet;


/**
 * This is the base class for Ant task that executes the protocol level tests.
 *
 *
 * @version $Id: TestSyncMLBaseTask.java,v 1.2 2007-11-28 10:52:31 nichele Exp $
 */
public abstract class TestSyncMLBaseTask extends org.apache.tools.ant.Task {

    // -------------------------------------------------------------- Properties

    protected String url = null;
    public void setUrl(String url) {
        this.url = url;
    }

    protected String test = null;
    public void setTest(String test) {
        this.test = test;
    }

    protected String basedir = null;
    public void setBasedir(String basedir) {
        this.basedir = basedir;
    }

    protected int sessionsDelay = 0;
    public void setSessionsDelay(int sessionsDelay) {
        this.sessionsDelay = sessionsDelay;
    }

    protected ArrayList xpathPatterns = new ArrayList();

    public void addPatternset(PatternSet set) {
        xpathPatterns.add(set);
    }

    // ----------------------------------------------------- Task Implementation

    // ------------------------------------------------------- Protected methods

    protected void validateAttributes() throws BuildException {
        try {
            new java.net.URL(url);
        } catch (Exception e) {
            new BuildException("Malformed url exception: " + url);
        }
    }

    protected Ant loadAntTask(String test) {
        Ant antTask = null;
        if ( (new File(basedir + File.separator + test + File.separator + "build.xml").exists()) ) {
            antTask = (Ant)getProject().createTask("ant");
            antTask.setAntfile(basedir+ File.separator + test + File.separator + "build.xml");
            antTask.setDir(new File(basedir, test));
        }
        return antTask;
    }

    protected String[] removeBuildFile(String[] filesList) {
        List newFilesList = new ArrayList();
        for (int i=0; i<filesList.length; i++) {

            log("called removeBuildFile ["+i+"]");
            log("filesList[i]: " + filesList[i]);

            if (!filesList[i].equalsIgnoreCase("build.xml")) {
                newFilesList.add(filesList[i]);
            }
        }
        return (String[])newFilesList.toArray(new String[0]);
    }

    /**
     * Orders and filters list of files. Valid name file is msgNNNNN.xml where
     * NNNNN is a positive integer.
     *
     * @param files String[]
     * @return String[]
     */
    protected static String[] ordersAndFilterFiles(String[] files) {
        int numFiles = files.length;

        TreeMap map = new TreeMap();

        String fileName = null;
        int index = -1;

        int numValidFiles = 0;

        for (int i = 0; i < numFiles; i++) {
            fileName = files[i];

            index = getIndexOfFile(fileName);

            if (index == -1) {
                continue;
            }
            numValidFiles++;
            map.put(new Integer(index), fileName);
        }

        String[] newFilesList = new String[numValidFiles];

        Iterator it = map.keySet().iterator();
        int i = 0;

        while (it.hasNext()) {
            newFilesList[i++] = (String) (map.get(it.next()));
        }

        return newFilesList;
    }

    /**
     * Given a fileName, if it is msgNNNNN.xml returns NNNNN, otherwise return -1
     * @param fileName String
     * @return int
     */
    protected static int getIndexOfFile(String fileName) {

        int indexMsg = fileName.indexOf("msg");

        if (indexMsg == -1) {
            return -1;
        }

        int indexExtension = fileName.lastIndexOf('.');

        if (indexExtension == -1) {
            return -1;
        }

        int indexOfFile = -1;

        try {
            indexOfFile = Integer.parseInt(fileName.substring(3, indexExtension));
        } catch (NumberFormatException ex) {
            return -1;
        }

        return indexOfFile;
    }


    /**
     * Simple filter that accepts just msgXXXX file with the given extension
     */
    class TestMessageFileFilter implements FilenameFilter {

        private String extension = null;

        public TestMessageFileFilter(String extension) {
            this.extension = extension;
        }

        public boolean accept(File dir, String name) {
            File f = new File(dir, name);
            if (f.isDirectory()) {
                return false;
            }

            if (name.startsWith("msg") &&
                name.endsWith("." + extension) ) {
                return true;
            }
            return false;
        }

    }
}

