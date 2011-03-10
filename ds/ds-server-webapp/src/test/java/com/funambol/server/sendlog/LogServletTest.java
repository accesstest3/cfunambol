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

package com.funambol.server.sendlog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Test cases for LogServlet.
 *
 * @version $Id: LogServletTest.java 36097 2010-10-21 09:55:23Z luigiafassina $
 */
public class LogServletTest extends TestCase implements Constants {
    // --------------------------------------------------------------- Constants
    private static final String VALID_CRED =
        "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
    private static final String VALID_DEVICEID = "fwm-456654456";

    private LogServlet              instance = null;
    private MockHttpServletRequest  request  = null;
    private MockHttpServletResponse response = null;
    // ------------------------------------------------------------ Constructors
    public LogServletTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        instance = new LogServlet();

        request = new MockHttpServletRequest();
        request.setMethod("POST");
        response = new MockHttpServletResponse();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    public void testValidateMandatoryHeaders_NullCred() throws Throwable {
        Boolean result = (Boolean)PrivateAccessor.invoke(
            instance,
            "validateMandatoryHeaders",
            new Class[] {HttpServletRequest.class, HttpServletResponse.class},
            new Object[]{request, response}
        );
        assertFalse("Wrong boolean returned when cred is null", result);
        assertEquals("Wrong status code returned when cred is null",
                     HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("Wrong error message returned when cred is null",
                     MISSING_AUTHORIZATION_HEADER_ERRMSG, 
                     response.getErrorMessage());
    }

    public void testValidateMandatoryHeaders_MalformedCred() throws Throwable {
        request.addHeader(HTTP_BASIC_AUTH_HEADER, "Not Basic credential");

        Boolean result = (Boolean)PrivateAccessor.invoke(
            instance,
            "validateMandatoryHeaders",
            new Class[] {HttpServletRequest.class, HttpServletResponse.class},
            new Object[]{request, response}
        );
        assertFalse("Wrong boolean returned when cred is malformed", result);
        assertEquals("Wrong status code returned when cred is malformed",
                     HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("Wrong error message returned when cred is malformed",
                     AUTHORIZATION_BAD_FORMAT_ERRMSG, 
                     response.getErrorMessage());
    }

    public void testValidateMandatoryHeaders_NullDeviceId() throws Throwable {
        request.addHeader(HTTP_BASIC_AUTH_HEADER, VALID_CRED);

        Boolean result = (Boolean)PrivateAccessor.invoke(
            instance,
            "validateMandatoryHeaders",
            new Class[] {HttpServletRequest.class, HttpServletResponse.class},
            new Object[]{request, response}
        );
        assertFalse("Wrong boolean returned when deviceid is null", result);
        assertEquals("Wrong status code returned when deviceid is null",
                     HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("Wrong error message returned when deviceid is null",
                     MISSING_DEVICEID_HEADER_ERRMSG, 
                     response.getErrorMessage());
    }

    public void testValidateMandatoryHeaders_NullContentType() throws Throwable {
        request.addHeader(HTTP_BASIC_AUTH_HEADER, VALID_CRED);
        request.addHeader(DEVICE_HEADER, VALID_DEVICEID);

        Boolean result = (Boolean)PrivateAccessor.invoke(
            instance,
            "validateMandatoryHeaders",
            new Class[] {HttpServletRequest.class, HttpServletResponse.class},
            new Object[]{request, response}
        );
        assertFalse("Wrong boolean returned when Content-Type is null", result);
        assertEquals("Wrong status code returned when Content-Type is null",
                     HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("Wrong error message returned when Content-Type is null",
                     MISSING_CONTENT_TYPE_HEADER_ERRMSG,
                     response.getErrorMessage());
    }

    public void testValidateMandatoryHeaders_NotValidContentType() throws Throwable {
        request.addHeader(HTTP_BASIC_AUTH_HEADER, VALID_CRED);
        request.addHeader(DEVICE_HEADER, VALID_DEVICEID);
        request.addHeader(CONTENT_TYPE_HEADER, "unknown");

        Boolean result = (Boolean)PrivateAccessor.invoke(
            instance,
            "validateMandatoryHeaders",
            new Class[] {HttpServletRequest.class, HttpServletResponse.class},
            new Object[]{request, response}
        );
        assertFalse("Wrong boolean returned when Content-Type is not valid", result);
        assertEquals("Wrong status code returned when Content-Type is not valid",
                     HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
        assertEquals("Wrong error message returned when Content-Type is not valid",
                     CONTENT_TYPE_NOT_VALID_ERRMSG, response.getErrorMessage());
    }

    public void testValidateMandatoryHeaders_AllValid_1() throws Throwable {
        request.addHeader(HTTP_BASIC_AUTH_HEADER, VALID_CRED);
        request.addHeader(DEVICE_HEADER, VALID_DEVICEID);
        request.addHeader(CONTENT_TYPE_HEADER, "text/plain");

        Boolean result = (Boolean)PrivateAccessor.invoke(
            instance,
            "validateMandatoryHeaders",
            new Class[] {HttpServletRequest.class, HttpServletResponse.class},
            new Object[]{request, response}
        );
        assertTrue("Wrong boolean returned when all mandatory headers", result);
        assertEquals("Wrong status code returned when cred is valid",
                     HttpServletResponse.SC_OK, response.getStatus());

    }

    public void testValidateMandatoryHeaders_AllValid_2() throws Throwable {
        request.addHeader(HTTP_BASIC_AUTH_HEADER, VALID_CRED);
        request.addHeader(DEVICE_HEADER, VALID_DEVICEID);
        request.addHeader(CONTENT_TYPE_HEADER, "application/x-tar");

        Boolean result = (Boolean)PrivateAccessor.invoke(
            instance,
            "validateMandatoryHeaders",
            new Class[] {HttpServletRequest.class, HttpServletResponse.class},
            new Object[]{request, response}
        );
        assertTrue("Wrong boolean returned when all mandatory headers", result);
        assertEquals("Wrong status code returned when cred is valid",
                     HttpServletResponse.SC_OK, response.getStatus());
    }
}
