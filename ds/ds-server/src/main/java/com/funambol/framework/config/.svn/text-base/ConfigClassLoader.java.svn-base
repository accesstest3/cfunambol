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
package com.funambol.framework.config;

import java.net.*;

/**
 * This is the class loader to be used to read configuration information and
 * server-side JavaBeans persisted as XML streams.
 *
 *
 * @version $Id: ConfigClassLoader.java,v 1.1.1.1 2008-02-21 23:35:37 stefano_fornari Exp $
 */
public class ConfigClassLoader extends java.net.URLClassLoader {

    /**
     * Creates a new instance of ConfigClassLoader.
     */
    public ConfigClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public String toString() {
        URL[] urls = getURLs();

        StringBuffer sb = new StringBuffer(getClass().getName());
        sb.append(" for: ");
        for (int i=0; ((urls != null) && (i<urls.length)); ++i) {
            sb.append(urls[i].toString()).append(' ');
        }

        return sb.toString();
    }
}
