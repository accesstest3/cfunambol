/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.email.test;

/**
 * Constants used by tests.
 *
 * @version $Id: Constants.java,v 1.1 2008-03-25 11:28:19 gbmiglia Exp $
 */
public class Constants {
    
    // --------------------------------------------------------------- Constants
    
    /** Resources path system property name. */
    public static final String RESOURCE_PATH_PROPERTY_NAME = "test.resources.dir";
    
    /** 
     * Test emails directory (path is relative to the resource path, specified 
     * by the RESOURCE_PATH_PROPERTY_NAME system property). 
     */
    public static final String TEST_EMAIL_DIR = "test_emails";
    
    /** Expected emails directory. */
    public static final String TEST_EXPECTED_EMAIL_DIR = "expected_emails";    

    /** 
     * Mails generated by the email connector contains the following headers by 
     * default: <code>Content-Type: text/plain; charset=UTF-8</code> and 
     * <code>Content-Transfer-Encoding</code>.
     * This constant sotres the size of xsuch headers.
     *
     * @todo: investigate this constant value
     */
    public static final int DEFAULT_HEADER_SIZE = 100;
        
    // ------------------------------------------------------------ Constructors
    
    /** Creates a new instance of <code>Constants</code>. */
    private Constants() {
    }
    
}