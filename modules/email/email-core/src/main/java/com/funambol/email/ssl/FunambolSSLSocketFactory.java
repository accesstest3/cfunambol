/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.email.ssl;

import com.funambol.email.util.Def;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * <code>SSLSocketFactory</code> which uses <code>FunambolTrustManager</code>.
 *
 * @author $Id: FunambolSSLSocketFactory.java,v 1.2 2008-05-13 10:09:53 gbmiglia Exp $
 */
public class FunambolSSLSocketFactory extends SSLSocketFactory {
    
    // ------------------------------------------------------------ Private data
    
    private SSLSocketFactory factory;
    
    /** Logger. */
    protected static FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Constructors
    
    public FunambolSSLSocketFactory() {
        
        if (log.isTraceEnabled()) {
            log.trace("Starting FunambolSSLSocketFactory creation...");
        }

        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init( null,
                    new TrustManager[] {new FunambolTrustManager()},
                    new java.security.SecureRandom());
            factory = (SSLSocketFactory) sslcontext.getSocketFactory();
            
        } catch(Exception e) {
            log.error("Error while creating FunambolSSLSocketFactory", e);
        }
    }
    
    // ---------------------------------------------------------- Public methods
    
    public static SocketFactory getDefault() {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory getDefault()");
        }
        return new FunambolSSLSocketFactory();
    }
    
    @Override
    public Socket createSocket( Socket socket, String s, int i, boolean flag)
    throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory createSocket(Socket, String, int, boolean)");
        }
        return factory.createSocket( socket, s, i, flag);
    }
    
    @Override
    public Socket createSocket( InetAddress inaddr, int i,
            InetAddress inaddr1, int j) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory createSocket(InetAddress, int, InetAddress, int)");
        }
        return factory.createSocket( inaddr, i, inaddr1, j);
    }
    
    @Override
    public Socket createSocket( InetAddress inaddr, int i)
    throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory createSocket(InetAddress, int)");
        }
        return factory.createSocket( inaddr, i);
    }
    
    @Override
    public Socket createSocket( String s, int i, InetAddress inaddr, int j)
    throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory createSocket(String, int, InetAddress, int)");
        }
        return factory.createSocket( s, i, inaddr, j);
    }
    
    @Override
    public Socket createSocket( String s, int i) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory createSocket(String, int)");
        }
        return factory.createSocket( s, i);
    }
    
    @Override
    public Socket createSocket() throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory createSocket()");
        }
        return factory.createSocket();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory getDefaultCipherSuites()");
        }
        return factory.getSupportedCipherSuites();
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        if (log.isTraceEnabled()) {
            log.trace("FunambolSSLSocketFactory getSupportedCipherSuites()");
        }
        return factory.getSupportedCipherSuites();
    }
}

