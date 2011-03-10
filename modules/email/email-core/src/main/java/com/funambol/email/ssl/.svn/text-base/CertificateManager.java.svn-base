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


import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.util.Def;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * <p>CertificateManager class</p>
 *
 * @version $Id: CertificateManager.java,v 1.1 2008-03-25 11:28:18 gbmiglia Exp $
 */
public class CertificateManager {

    // --------------------------------------------------------------- Constants

    /**     */
    protected static FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    /**
     *
     */
    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    // ---------------------------------------------------------- Public Methods

    /**
     * download the certificate from a given mail server
     */
    public static synchronized void downloadSSLCertificate(String host,
            int port,
            int timeout,
            String keystoreFilename,
            String keystorePassword)
     throws EmailAccessException {

        try {

            if (log.isTraceEnabled()) {
                log.trace("Start automatic certificate installation procedure!");
            }

            //  1 ....
            InputStream in = new FileInputStream(keystoreFilename);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, keystorePassword.toCharArray());
            in.close();


            //  2 ....
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];

            FunambolTrustManager tm = new FunambolTrustManager(defaultTrustManager);
            context.init(null, new TrustManager[] {tm}, null);

            SSLSocketFactory factory = context.getSocketFactory();

            SSLSocket socket = (SSLSocket)factory.createSocket(host, port);

            socket.setSoTimeout(timeout);

            // 3 ... open connection
            try {
                if (log.isTraceEnabled()) {
                    log.trace("Starting SSL handshake..." + host);
                }
                socket.startHandshake();
                socket.close();
                if (log.isTraceEnabled()) {
                    log.trace("Certificate is already trusted");
                }
                return;
            } catch (SSLException e) {
                if (log.isTraceEnabled()) {
                    log.trace("Certificate will be downloaded because there is " +
                            "the a connection error [" + e.getMessage() + "]");
                }
                // do nothing
            }

            // 4 ... get certificate chain
            X509Certificate[] chain = tm.getChain();
            if (chain == null) {
                if (log.isTraceEnabled()) {
                    log.trace("Could not obtain server certificate chain");
                }
                return;
            }

            // 5 ... save all certificates into the keystore
            String alias = null;
            OutputStream out = null;
            for (int i = 0; i < chain.length; i++) {
                X509Certificate cert = chain[i];
                alias = host + "-" + (i + 1);
                ks.setCertificateEntry(alias, cert);
                out = new FileOutputStream(keystoreFilename);
                ks.store(out, keystorePassword.toCharArray());
                out.close();
                if (log.isTraceEnabled()) {
                    log.trace("Certificate with alias '" + alias + "' syccesfully imported!");
                }
            }

        } catch (IOException e){
            throw new EmailAccessException("IO Error",e);
        } catch (NoSuchAlgorithmException e){
            throw new EmailAccessException("No Such Algorithm Error",e);
        } catch (KeyStoreException e){
            throw new EmailAccessException("Key Store Error",e);
        } catch (KeyManagementException e){
            throw new EmailAccessException("Key Management Error",e);
        } catch (CertificateEncodingException e){
            throw new EmailAccessException("Certificate Encoding Error",e);
        } catch (Exception e){
            throw new EmailAccessException("Generic Error",e);
        }


    }



    // --------------------------------------------------------- Private Methods

    /**
     *
     */
    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }

}
