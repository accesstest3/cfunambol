/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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


package com.funambol.framework.tools;

import java.io.File;

/**
 * This interface defines all the methods that are called when a file
 * Implementing this interface, a class is able to hanlde events triggered
 * when a file/directory is added to an archive or skipped.
 * In this way, further actions can be accomplished (i.e. moving the just archived
 * files, and so on)
 *
 * @version $Id$
 */
public interface ArchivationEventHandler {

    /**
     * this method is called every time a directory is added to the zip file
     * @param path the path containing the just added directory
     * @param sourceFile the file rapresenting the directory that has been added
     */
    public void directoryAdded(String path, File sourceFile);

    /**
     * this method is called every time a file is added to the zip file
     * @param path is the path containing the just added file
     * @param sourceFile the file rapresenting the file that has been added
     * @param size the size of the just copied file
     */

    public void fileAdded(String path, File sourceFile, long size);

    /**
     * this method is called every tume a directory is skipped and not added to the zip
     * file
     * @param path the path containing the just added directory
     * @param sourceFile the file object rapresenting the directory that has been skipped
     */

    public void directorySkipped(String path, File sourceFile);

    /**
     * this method is called every tume a file is skipped and not added to the zip
     * file
     * @param path the path containing the just added directory
     * @param sourceFile the file object rapresenting the file that has been skipped
     */
    public void fileSkipped(String path, File sourceFile);

    

}
