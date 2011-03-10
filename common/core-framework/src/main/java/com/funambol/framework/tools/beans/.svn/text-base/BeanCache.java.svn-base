/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

package com.funambol.framework.tools.beans;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.funambol.framework.tools.IOTools;

/**
 * This class implements a cache of bean objects. Objects are stored in
 * the cache associated with their last modification timestamp. A new instance
 * of the object will be returned only if its server bean is newer  of the one
 * loaded.
 * The bean files are read from <code>configPath<code>.
 *
 * It also provides cached access to singleton instances. In case of singleton
 * istances, a new instance of a server bean is returned only if its
 * configuration has changed; otherwise the last created instance is returned.
 *
 * @version $Id: BeanCache.java,v 1.2 2007-11-28 11:13:37 nichele Exp $
 */
public class BeanCache {
    // ------------------------------------------------------------ Private data

    /**
     * The hash map containing the cached server beans. Map keys will be the
     * server bean names; the entry will be the corresponding ServerBeanEntry
     * objects.
     */
    private Map<String, ServerBeanEntry> cache;

    /**
     * This is the directory where the beans will be read
     */
    private String configPath;

    /**
     * The URL that represents the config path
     */
    private URL configPathURL;

    /**
     * Read-write locks allows maximal concurrency
     */
    private final ReentrantReadWriteLock lock;
    private final Lock rLock;
    private final Lock wLock;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new BeanCache with the given configuration path
     * @param configPath the config path
     */
    public BeanCache(String configPath) {
        this.cache = new HashMap<String, ServerBeanEntry>();

        this.lock = new ReentrantReadWriteLock();
        this.rLock = lock.readLock();
        this.wLock = lock.writeLock();

        this.configPath    = configPath;
        try {
            this.configPathURL = fixURL(configPath);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(
                "Unable to create an URL object using the given configPath"
            );
        }
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns a new instance of the server bean identified by the given name.
     * A new instance is created each time,but if the bean is a CloneableBean,
     * the new instance is created cloning the cached object
     * @return the server bean instance (a new instance is created each time)
     * @param name the server bean name
     * @throws BeanException in case of errors
     */
    public Object getNewBeanInstance(String name)
    throws BeanException {
        
        ServerBeanEntry entry = null;

        rLock.lock();
        entry = cache.get(name);
        rLock.unlock();

        //
        // We first check if the bean is in the configuration path with
        // the given name. If so, the file is the configuration to use,
        // but we need to read it only if recently modified.
        // If the file is not found, the bean represents a class or
        // something else in the classpath that we do not have to deal with.
        //
        File configFile = new File(configPath, name);

        if (!configFile.exists()) {
            throw new BeanNotFoundException("Unable to load bean from: " + configFile.getAbsolutePath());
        }

        if ((entry == null) || (entry.ts != configFile.lastModified())) {
            wLock.lock();
            //
            // Recheck state because another thread might have acquired
            // write lock and changed state before we did.
            //
            entry = cache.get(name);
            if ((entry == null) || (entry.ts != configFile.lastModified())) {
                try {
                    entry = new ServerBeanEntry(configFile);
                    //
                    // In any case an instace must be created in order to use it
                    // to check if the bean is cloneable and, in such case, this
                    // instance is used to create the new ones.
                    //
                    entry.instance =
                            BeanFactory.getBeanInstanceFromConfig(entry.config);

                    cache.put(name, entry);

                    return entry.instance;
                    
                } catch (BeanException e) {
                    throw e;
                } catch (Exception e) {
                    throw new BeanException("Error in creating an instance of " + name, e);
                } finally {
                    wLock.unlock();
                }
            } else {
                wLock.unlock();
            }
        }

        if (entry.instance instanceof CloneableBean) {
            //
            // Trying cloning the instance in cache
            //
            Object newInstance = null;
            try {
                newInstance = ((CloneableBean)(entry.instance)).cloneBean();
                return newInstance;
            } catch (CloneBeanException e) {
                // the object seems not clonable...creating a new instance
            }
        }

        //
        // No way to clone the instance in cache....
        //
        return BeanFactory.getBeanInstanceFromConfig(entry.config);
    }

    /**
     * Returns a new instance of the server bean identified by the given name without
     * to initialize it.
     * A new instance is created each time even if the bean is a CloneableBean
     * (since we need a not initialized instace)
     * @return the server bean instance (a new instance is created each time)
     * @param name the server bean name
     * @throws BeanException in case of errors
     */
    public Object getNoInitNewBeanInstance(String name)
            throws BeanException {
        ServerBeanEntry entry = null;

        rLock.lock();
        entry = cache.get(name);
        rLock.unlock();

        //
        // We first check if the bean is in the configuration path with
        // the given name. If so, the file is the configuration to use,
        // but we need to read it only if recently modified.
        // If the file is not found, the bean represents a class or
        // something else in the classpath that we do not have to deal with.
        //
        File configFile = new File(configPath, name);

        if (!configFile.exists()) {
            throw new BeanNotFoundException("Unable to load bean from: " + configFile.getAbsolutePath());
        }

        if ((entry == null) || (entry.ts != configFile.lastModified())) {

            wLock.lock();
            //
            // Recheck state because another thread might have acquired
            // write lock and changed state before we did.
            //
            entry = cache.get(name);
            if ((entry == null) || (entry.ts != configFile.lastModified())) {

                try {
                    entry = new ServerBeanEntry(configFile);
                    //
                    // In any case an instace must be created in order to use it
                    // to check if the bean is cloneable and, in such case, this
                    // instance is used to create the new ones.
                    // BTW, in this method, the instance will not be used.
                    // Any time a new object is created since we need to return
                    // a not initialized instace.
                    // Why do we create entry.instance ? Since in any case the
                    // 'entry' object must be created correclty since it is used
                    // also by other methods.
                    //
                    entry.instance =
                            BeanFactory.getBeanInstanceFromConfig(entry.config);

                    cache.put(name, entry);
                } catch (BeanException e) {
                    throw e;
                } catch (Exception e) {
                    throw new BeanException("Error in creating an instance of " + name, e);
                } finally {
                    wLock.unlock();
                }
            } else {
                wLock.unlock();
            }
        }

        return BeanFactory.getNoInitBeanInstanceFromConfig(entry.config);
    }

    /**
     * Returns an instance of the server bean identified by the given name. Once
     * an instance of the object is created, it is stored in the cache for
     * future access. A new instance of the bean is created only when its config
     * file has changed.
     *
     *
     * @return the server bean instance (a new instance is created each time)
     * @param name the server bean name
     * @throws BeanException in case of errors
     */
    public Object getBeanInstance(String name)
    throws BeanException {
        
        ServerBeanEntry entry = null;

        rLock.lock();
        entry = cache.get(name);
        rLock.unlock();

        //
        // We first check if the bean is in the configuration path with
        // the given name. If so, the file is the configuration to use,
        // but we need to read it only if recently modified.
        // If the file is not found, the bean represents a class or
        // something else in the classpath that we do not have to deal with.
        //
        File configFile = new File(configPath, name);

        if (!configFile.exists()) {
            throw new BeanNotFoundException("Unable to load bean from: " + configFile.getAbsolutePath());
        }

        if ((entry == null) || (entry.ts != configFile.lastModified())) {
            
            wLock.lock();
            //
            // Recheck state because another thread might have acquired
            // write lock and changed state before we did.
            //
            entry = cache.get(name);
            
            if ((entry == null) || (entry.ts != configFile.lastModified())) {

                try {
                    entry = new ServerBeanEntry(configFile);
                    entry.instance =
                            BeanFactory.getBeanInstanceFromConfig(entry.config);
                    cache.put(name, entry);
                } catch (BeanException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new BeanException("Error in creating an instance of " + name, e);
                } finally {
                    wLock.unlock();
                }
            } else {
                wLock.unlock();
            }
        }

        return entry.instance;

    }

    /**
     * Invalidates the entire cache
     */
    public void invalidate() {
        wLock.lock();
        try {
            cache.clear();
        } finally {
            wLock.unlock();
        }
    }

    /**
     * Invalidates the given entry
     *
     * @param name the bean name to invalidate
     */
    public void invalidate(String name) {
        wLock.lock();
        try {
            cache.remove(name);
        } finally {
            wLock.unlock();
        }
    }

    /**
     * Returns the URL that represents the configPath
     * @return the URL that represents the configPath
     */
    public URL getConfigPathURL() {
        return configPathURL;
    }
    // ------------------------------------------------------- Protected methods
    /**
     * Checks if the given string is an accepted URL (starting with http://,
     * https:// or file://). If yes, a new URL object representing the given
     * url is returned; otherwise, the given string is considered a file name
     * and a new URL is obtained calling File.toURL().
     * @param s the string to check
     * @return the corresponding URL if the string represents a URL or the
     *         fixed URL if the string is a pathname/filename
     * @throws MalformedURLException if an error occurs
     */
    protected static URL fixURL(final String s)
    throws MalformedURLException {

        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            //
            // This is not a URL, let's consider it just a file
            //
        }

        try {
            return new File(new File(s).getCanonicalPath()).toURL();
        } catch (IOException e) {
            throw new MalformedURLException("Unable to convert" + s + " to a URL");
        }
    }

    // --------------------------------------------------------- Private classes

    /**
     * This represents an entry in the cache. It associates a server bean name
     * to:
     * <ul>
     * <li>ts -> the timestamp of when the configuration file was last read</li>
     * <li>config -> the configuration file content
     * <li>instance -> the singleton instance associated to this server bean
     *
     */
    class ServerBeanEntry {

        /**
         * file timestamp
         */
        public long ts;
        public String config;
        public Object instance;

        /**
         * Creates a new ServerBeanEntry from the given file
         *
         * @param file the bean file - NOT NULL
         */
        public ServerBeanEntry(File file) {
            ts = file.lastModified();
            try {
                config = IOTools.readFileString(file);
            } catch (Exception e) {
                //
                // just an easy way to let the caller know, even if it is not
                // very elegant.... :)
                //
                config = e.getMessage();
            }
        }
    }
}