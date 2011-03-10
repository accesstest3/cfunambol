package com.funambol.tools.test;
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
import java.io.*;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.*;

/**
 * Contains some useful methods to work with Ant
 *
 * @version $Id: AntUtil.java,v 1.2 2007-11-28 10:52:31 nichele Exp $
 */
public class AntUtil {

    /**
     * Run specific ant target
     * @param project the ant Project
     * @param baseDir the dir contains the files to execute
     * @param buildFile the build.xml
     * @param targetName the name of the target to execute
     *
     * @throws java.lang.Exception
     */
    public static void runAntTarget(Project project,
                                    File    baseDir,
                                    String  buildFile,
                                    String  targetName)
    throws Exception {

        if (project == null) {
            throw new IllegalArgumentException("Project can not be null");
        }

        Ant antTask = null;
        if ( (new File(buildFile).exists()) ) {
            antTask = (Ant)project.createTask("ant");
            antTask.setAntfile(buildFile);
            antTask.setDir(baseDir);
        }

        antTask.setTarget(targetName);
        antTask.execute();
    }

}
