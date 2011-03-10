/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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

package com.funambol.admin.main;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;

import com.funambol.admin.util.Log;

/**
 *
 * @version $Id: Sync4jClassLoader.java,v 1.6 2007-11-28 10:28:17 nichele Exp $
 */
public class Sync4jClassLoader
extends ClassLoader {

    // ------------------------------------------------------------ Private data

    private URL codeBaseURL;
    private HashMap cache;

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of Sync4jClassLoader */
    public Sync4jClassLoader(String codeBase)
    throws MalformedURLException {
        super();
        init(codeBase);
    }

    /** Creates a new instance of Sync4jClassLoader */
    public Sync4jClassLoader(String codeBase, ClassLoader parent)
    throws MalformedURLException {
        super(parent);
        init(codeBase);
    }

    // ---------------------------------------------------------- Public methods

    public Class loadClass(String className)
    throws ClassNotFoundException {
      return (loadClass(className, true));
    }

    public synchronized Class loadClass(String className, boolean resolveIt)
    throws ClassNotFoundException {
        Class   result;
        byte[]  classBytes;

        Log.debug("Loading " + className + ", (" + resolveIt + ")");

        //----- Check our local cache of classes
        result = (Class)cache.get(className);
        if (result != null) {
            Log.debug("Loaded from cache");
            return result;
        }

        //----- Check with the primordial class loader
        try {
            result = getParent().loadClass(className);
            Log.debug("Loaded from parent class loader");

            return result;
        } catch (ClassNotFoundException e) {
            //
            // Not found by parent class loader
            //
        }

        //----- Try to load it from preferred source
        // Note loadClassBytes() is an abstract method
        classBytes = loadClassBytes(className);
        if (classBytes == null) {
            throw new ClassNotFoundException();
        }

        //----- Define it (parse the class file)
        result = defineClass(className, classBytes, 0, classBytes.length);
        if (result == null) {
            throw new ClassFormatError();
        }

        //----- Resolve if necessary
        if (resolveIt) {
            resolveClass(result);
        }

        // Done
        cache.put(className, result);
        Log.debug("Downloaded from the server");

        return result;

    }

    // ------------------------------------------------------- Protected methods

    protected void init(String codeBase) throws MalformedURLException {
        codeBaseURL = new URL(codeBase);
        cache = new HashMap();
    }

    protected byte[] loadClassBytes(String className) {
        try {
            URL url = new URL(codeBaseURL.toString() + '/' + className);
            URLConnection conn = url.openConnection();
            Log.debug("Loading from: " + conn.getURL());

            InputStream is = conn.getInputStream();

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            int i = 0;
            byte[] buf = new byte[4096];
            while ((i=is.read(buf))>=0) {
                os.write(buf, 0, i);
            }
            is.close(); os.close();

            return os.toByteArray();
        } catch(Exception e) {
            Log.debug("Unable to load '" + className + "' from remote server: " + e.getMessage());

            return null;
        }

    }


}
