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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Useful class to handle bean files. It's handled as singleton class with a factory
 * method to obtain a <CODE>BeanTool</CODE> instance to handle a given configPath.
 * Any <CODE>BeanTool</CODE> instance uses a <CODE>BeanCache</CODE> instance to
 * cache the read bean files.
 * @version $Id: BeanTool.java,v 1.4 2007-11-28 11:13:37 nichele Exp $
 */
public class BeanTool {

    /**
     * The BeanCache instance to cache the bean files
     */
    private BeanCache beanCache = null;

    /**
     * The path of a BeanTool instance
     */
    private String path = null;

    /**
     * the BeanTool instances
     */
    private static Map<URL, BeanTool> instances = new HashMap<URL, BeanTool>();

    /**
     * Creates a new instance of BeanTool to handle bean files in the given path
     * @param path the path where to read bean files
     */
    private BeanTool(String path) {
        this.path      = path;
        this.beanCache = new BeanCache(path);
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Returns a <CODE>BeanTool</CODE> instance to handle bean file in the given path
     * @param path the path where to read the bean files
     * @return a BeanTool instance
     */
    public static synchronized BeanTool getBeanTool(String path) {
        URL url = null;
        try {
            url = BeanCache.fixURL(path);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException(
                "Unable to create an URL object using the given configPath"
            );
        }
        BeanTool beanTool = instances.get(url);
        if (beanTool == null) {
            beanTool = new BeanTool(path);
            instances.put(url, beanTool);
        }
        return beanTool;
    }

    /**
     * Returns a new instance of the server bean identified by the given name.
     * @return the server bean instance (a new instance is created each time)
     * @param name the server bean name
     * @throws com.funambol.framework.tools.beans.BeanException if an error occurs
     */
    public Object getNewBeanInstance(String name)
    throws BeanException {

        if (!name.endsWith(".xml") && !name.toLowerCase().endsWith(".xml")) {
            Object bean = loadBeanAsClass(name);
            if (bean instanceof LazyInitBean) {
                ((LazyInitBean)bean).init();
            }
            return bean;
        }

        return beanCache.getNewBeanInstance(name);
    }

    /**
     * Returns a new instance of the server bean identified by the given name without
     * to initialize it.
     * @return the server bean instance (a new instance is created each time)
     * @param name the server bean name
     * @throws com.funambol.framework.tools.beans.BeanException if an error occurs
     */
    public Object getNoInitNewBeanInstance(String name)
    throws BeanException {
        
        if (!name.endsWith(".xml") && !name.toLowerCase().endsWith(".xml")) {
            return loadBeanAsClass(name);
        }

        return beanCache.getNoInitNewBeanInstance(name);
    }
    
    /**
     * Returns an instance of the server bean identified by the given name. Once
     * an instance of the object is created, it is stored in the cache for
     * future access. A new instance of the bean is created only when its config
     * file has changed.
     * @return the server bean instance (a new instance is created each time)
     * @param name the server bean name
     * @throws com.funambol.framework.tools.beans.BeanException if an error occurs
     */
    public Object getBeanInstance(String name)
    throws BeanException {

        if (!name.endsWith(".xml") && !name.toLowerCase().endsWith(".xml")) {
            Object bean = loadBeanAsClass(name);
            if (bean instanceof LazyInitBean) {
                ((LazyInitBean)bean).init();
            }
            return bean;
        }

        return beanCache.getBeanInstance(name);
    }

    /**
     * Returns a sorted array of String that contains the bean name of the bean files
     * under the given directory.
     * @return a sorted array of String that contains the bean name of the bean files
     *         under the given directory
     * @param directory the directory
     */
    public String[] getBeanNames(String directory) {

        File          dir   = new File(path, directory);
        File[]        files = dir.listFiles();
        if (files == null) {
            return new String[0];
        }
        int           i     = 0;
        StringBuilder sb    = null;
        List<String>  beanList = new ArrayList<String>();
        for (; i < files.length; ++i) {
            if (files[i].isFile() &&
                (files[i].getName().endsWith(".xml") || files[i].getName().endsWith(".XML"))) {
                sb = new StringBuilder(directory);
                sb.append(File.separator);
                sb.append(files[i].getName());
                beanList.add(sb.toString());
            }
        }

        Collections.sort(beanList);
        return (String[])beanList.toArray(new String[0]);
    }


    /**
     * Saves the given server bean and invalidates the cache entry if any.
     *
     * @param beanName the complete beanName
     * @param obj the bean instance
     *
     *
     * @throws BeanException in case of instantiation error
     */
    public void saveBeanInstance(String beanName, Object obj)
    throws BeanException {
        File beanFile = new File(path, beanName);
        BeanFactory.saveBeanInstance(obj, beanFile);
        beanCache.invalidate(beanName);
    }

    /**
     * Deletes the given server bean and invalidates the cache entry if any.
     *
     * @param beanName the complete beanName
     *
     */
    public void deleteBeanInstance(String beanName) {
        File beanFile = new File(path, beanName);
        beanFile.delete();
        beanCache.invalidate(beanName);
    }

    // --------------------------------------------------------- Private methods

    private Object loadBeanAsClass(String name) 
    throws BeanException {

        //
        // name seems not a bean file. Trying to use it as class name
        //
        Object bean = null;

        //
        // Search beanName as a class
        //
        try {
            bean = Class.forName(name).newInstance();
            return bean;
        }  catch (ClassNotFoundException e) {
            //
            // beanName does not represent a class!
            //
            throw new BeanNotFoundException("Unable to load " + name, e);
        } catch (Exception e) {
            //
            // Other exceptions are not good!
            //
            throw new BeanInstantiationException( "Error instantiating "
                                                + name
                                                , e
                                                );
        }
    }
}