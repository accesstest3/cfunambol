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
package com.funambol.framework.tools.beans;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

import java.io.*;

import com.funambol.framework.tools.IOTools;

/**
 * This is a JavaBeans factory. At the moment it has a very simple
 * implementation: it loads the class from a serialized file at runtime.
 *
 * @version $Id: BeanFactory.java,v 1.2 2007-11-28 11:13:37 nichele Exp $
 */
public class BeanFactory {

    protected BeanFactory() { }

    /**
     * Creates an instance of the given bean. The bean is first search as a
     * class. If not found as class, it is searched as a resource. There isn't a
     * lazy initialization.
     *
     * @param       beanName    name of the bean (for now is also the file name,
     *                          without .ser, into which the bean has been serialized)
     *                          NOT NULL
     *
     * @return      a new instance of the serialized bean
     *
     * @throws      BeanInstantiationException if the bean cannot be istantiated
     * @throws      BeanInitializationException if an initialization error occurred
     * @throws      BeanNotFoundException if the bean does not exist
     */
    public static Object getNoInitBeanInstance(File beanName)
    throws BeanInstantiationException,
           BeanInitializationException,
           BeanNotFoundException {

        Object bean = null;

        InputStream is = null;
        try {
            is = new FileInputStream(beanName);
        } catch (FileNotFoundException ex) {
            throw new BeanNotFoundException(beanName.getAbsolutePath());
        }

        BeanExceptionListener exceptionListener = new BeanExceptionListener();

        XMLDecoder d = new XMLDecoder(is, null, exceptionListener);
        bean = d.readObject();
        d.close();

        if (exceptionListener.exceptionThrown()) {
            Throwable t = exceptionListener.getException();

            if (t.getCause() != null) {
                t = t.getCause();
            }
            throw new BeanInstantiationException( "Error instantiating "
                                                + beanName
                                                , t
                                                );
        }

        return bean;
    }

    /**
     * Creates an instance of the given bean. The bean is first search as a
     * class. If not found as class, it is searched as a resource. There isn't a
     * lazy initialization.
     *
     * @param       beanName    name of the bean (for now is also the file name,
     *                          without .ser, into which the bean has been serialized)
     *                          NOT NULL
     *
     * @return      a new instance of the serialized bean
     *
     * @throws      BeanInstantiationException if the bean cannot be istantiated
     * @throws      BeanInitializationException if an initialization error occurred
     * @throws      BeanNotFoundException if the bean does not exist
     */
    public static Object getNoInitBeanInstance(String beanName)
    throws BeanInstantiationException,
           BeanInitializationException,
           BeanNotFoundException {

           return getNoInitBeanInstance(new File(beanName));
    }

    /**
     * Creates an instance of the given bean. The bean is first search as a
     * class. If not found as class, it is searched as a resource.
     *
     * @param       beanName    name of the bean (for now is also the file name,
     *                          without .ser, into which the bean has been serialized)
     *                          NOT NULL
     *
     * @return      a new instance of the serialized bean
     *
     * @throws      BeanInstantiationException if the bean cannot be istantiated
     * @throws      BeanInitializationException if an initialization error occurred
     * @throws      BeanNotFoundException if the bean does not exist
     */
    public static Object getBeanInstance(String beanName)
    throws BeanInstantiationException,
           BeanInitializationException,
           BeanNotFoundException {

        Object bean = null;

        bean = getNoInitBeanInstance(beanName);

        if (bean instanceof LazyInitBean) {
            ((LazyInitBean)bean).init();
        }

        return bean;
    }

    /**
     * Creates an instance of the given bean. This version deserializes the bean
     * from the given file.
     *
     * @param       beanFile    the filename of the serialized file
     *
     * @return      a new instance of the serialized bean
     *
     * @throws      BeanInstantiationException if the bean cannot be istantiated
     * @throws      BeanInitializationException if an initialization error occurred
     * @throws      BeanNotFoundException if the bean does not exist
     */
    public static Object getBeanInstance(File beanFile)
    throws BeanInstantiationException,
           BeanInitializationException,
           BeanNotFoundException {

        Object bean = getNoInitBeanInstance(beanFile);

        if (bean instanceof LazyInitBean) {
            ((LazyInitBean)bean).init();
        }
        return bean;
    }

    /**
     * Creates an instance of the given bean. <code>config</code> is the server
     * bean  configuration.
     *
     * @param       beanConfig  configuration of the server bean to create - NOT NULL
     *
     * @return      a new instance of the serialized bean
     *
     * @throws      BeanInstantiationException if the bean cannot be istantiated
     * @throws      BeanInitializationException if an initialization error occurred
     * @throws      BeanNotFoundException if the bean does not exist
     */
    public static Object getBeanInstanceFromConfig(String beanConfig)
    throws BeanInstantiationException,
           BeanInitializationException,
           BeanException {

        Object bean = null;

        bean = unmarshal(beanConfig);

        if (bean instanceof LazyInitBean) {
            ((LazyInitBean)bean).init();
        }

        return bean;
    }

    /**
     * Creates an instance of the given bean. <code>config</code> is the server
     * bean  configuration.
     *
     * @param       beanConfig  configuration of the server bean to create - NOT NULL
     *
     * @return      a new instance of the serialized bean
     *
     * @throws      BeanInstantiationException if the bean cannot be istantiated
     * @throws      BeanInitializationException if an initialization error occurred
     * @throws      BeanNotFoundException if the bean does not exist
     */
    public static Object getNoInitBeanInstanceFromConfig(String beanConfig)
    throws BeanInstantiationException,
           BeanInitializationException,
           BeanException {

        Object bean = null;

        bean = unmarshal(beanConfig);

        return bean;
    }

    /**
     * Returns the server bean configuration if there is any. If the bean name
     * does not represents a configuration but an instance, null is returned.
     *
     * @param       beanName    name of the bean - NOT NULL
     *
     * @return      the configuration file if found, NULL otherwise
     *
     * @throws      BeanInstantiationException if the bean cannot be istantiated
     * @throws      BeanInitializationException if an initialization error occurred
     * @throws      BeanNotFoundException if the bean does not exist
     */
    public static String getBeanConfig(String beanName)
    throws BeanInstantiationException,
           BeanInitializationException,
           BeanNotFoundException {

        try {
            return IOTools.readFileString(beanName);
        } catch (IOException e) {
            throw new BeanNotFoundException("The bean could not be read: " + e.toString());
        } 
    }

    /**
     * Serialize the given object in the give file using XMLEncoder.
     *
     * @param obj the object to be serialized
     * @param file the file into wich serialize the object
     *
     * @throws BeanException in case of errors
     */
    public static void saveBeanInstance(Object obj, File file)
    throws BeanException {

        File parent = file.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        XMLEncoder encoder = null;

        try {
            encoder = new XMLEncoder(new FileOutputStream(file));
            encoder.writeObject(obj);
        } catch (IOException e) {
            String msg = "Bean saving (" + file + ") failed: " + e.toString();
            throw new BeanException(msg, e);
        } finally {
            if (encoder != null) {
                encoder.close();
            }
        }
    }

    /**
     * Marshals the given object into XML using <i>java.beans.XMLEncoder</i>.
     *
     * @param o the object to marshal
     *
     * @return the XML form of the object
     *
     * @throws BeanException in case of marshalling errors
     */
    public static String marshal(Object o)
    throws BeanException {
        return marshal(null, o);
    }

    /**
     * Marshals the given object into XML using <i>java.beans.XMLEncoder</i>.
     *
     * @param loader the classloader to use
     * @param o the object to marshal
     *
     * @return the XML form of the object
     *
     * @throws BeanException in case of marshalling errors
     */
    public static String marshal(ClassLoader classLoader, Object o)
    throws BeanException {
        XMLEncoder enc = null;
        ClassLoader oldClassLoader = null;

        if (classLoader != null) {
            oldClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
        }

        BeanExceptionListener exceptionListener = new BeanExceptionListener();

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            enc = new XMLEncoder(os);

            enc.writeObject(o);

            enc.close(); enc = null; // we need to do this here to get the string

            if (exceptionListener.exceptionThrown()) {
                Throwable t = exceptionListener.getException();
                throw new BeanException(t.toString(), t);
            }

            return os.toString();
        } finally {
            if (enc != null) {
                enc.close();
            }

            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }
    }

    /**
     * Unmarshals the given serialized object into the corresponding instance
     * using <i>java.beans.XMLDecoder</i>.
     *
     * @param classLoader the class loader to use (null for the standard one)
     * @param xml XML serialized form
     *
     * @return the unmarshalled instance
     *
     * @throws BeanException in case of unmarshalling errors
     */
    public static Object unmarshal(ClassLoader classLoader, String xml)
    throws BeanException {
        XMLDecoder dec = null;
        ClassLoader oldClassLoader = null;

        if (classLoader != null) {
            oldClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
        }

        BeanExceptionListener exceptionListener = new BeanExceptionListener();

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
            Object ret = null;

            dec = new XMLDecoder(is);
            dec.setExceptionListener(exceptionListener);
            ret = dec.readObject();
            dec.close(); dec = null;

            if (exceptionListener.exceptionThrown()) {
                Throwable t = exceptionListener.getException();

                if (t.getCause() != null) {
                    t = t.getCause();
                }
                throw new BeanInstantiationException("Error creating bean", t);
            }

            return ret;
        } finally {
            if (dec != null) {
                dec.close();
            }

            if (classLoader != null) {
                Thread.currentThread().setContextClassLoader(oldClassLoader);
            }
        }
    }

    /**
     * The same as <i>unmarshal(null, xml)</i>
     *
     * @param xml XML serialized form
     *
     * @return the unmarshalled instance
     *
     * @throws BeanException in case of unmarshalling errors
     */
    public static Object unmarshal(String xml)
    throws BeanException {
        return unmarshal(null, xml);
    }

    /**
     * Creates a bean instance given the class name and serialize it in the
     * specified file. The object is serialized in XML.
     * <p>
     * Syntax:<br>
     * com.funambol.framework.tools.beans.BeanFactory <i>class name</i> </i>file name</i>
     **/
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Syntax: com.funambol.framework.tools.beans.BeanFactory <class name> <file name>");
            return;
        }

        saveBeanInstance(getBeanInstance(args[0]), new File(args[1]));
    }
}
