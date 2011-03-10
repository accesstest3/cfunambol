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
package com.funambol.tools.test;

import java.net.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import org.jibx.runtime.*;

import com.funambol.framework.core.Constants;
import com.funambol.framework.core.SyncML;
import com.funambol.framework.core.RepresentationException;
import com.funambol.framework.core.Sync4jException;
import com.funambol.framework.tools.WBXMLTools;

/**
 *
 * @version $Id: HttpClientConnection.java,v 1.4 2008-02-25 14:37:29 luigiafassina Exp $
 *
 */
public final class HttpClientConnection {

    public static String LOG_NAME = "funambol.test.tools.PostSyncML";

    private static final Logger log = Logger.getLogger(LOG_NAME);

    //
    // todo : decide if HttpURLConnection is a good implementation strategy...
    // The other way to implement this would be to use a java.net.Socket
    // The problem with java.net.Socket is that I'd have to write alot
    // of low-level HTTP protocol code.  The advantage of java.net.Socket
    // is that I could explicitly set a Socket timeout value. Hmm....
    //
    // Also, another way to implement this class would be to use Apache's
    // HttpClient code:  http://jakarta.apache.org/commons/httpclient/
    //

    private final HttpURLConnection  conn;
    private       OutputStream       out;
    private final String             serverAddress;

    private       String             lastResponse;  // as string
    private       String             lastMessage;  // as string

    /**
     *
     * @param strServerAddress must be non-null
     *
     */
    public HttpClientConnection(final String strServerAddress)
    throws IOException
    {
        if (strServerAddress == null)
        {
            throw new NullPointerException("strServerAddress parameter is null");
        }

        serverAddress = strServerAddress;

        URL u = null;
        try
        {
            u = new URL(serverAddress);
        }
        catch (java.net.MalformedURLException ex)
        {
            throw new IllegalArgumentException(
                    "server address is not a valid URL");
        }

        lastMessage = lastResponse = null;

        Object obj = u.openConnection();
        if ((obj instanceof java.net.HttpURLConnection) == false)
        {
            // todo : throw exception here
        }
        conn = (HttpURLConnection) obj;
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setAllowUserInteraction(false);
        conn.setRequestMethod("POST");
        conn.setUseCaches(false);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestProperty("User-Agent", this.getClass().toString());
    }

    public void setPropsHeader(Properties propsHeader) {
        if (propsHeader != null && conn != null) {
            Enumeration en = propsHeader.propertyNames();
            while(en.hasMoreElements()) {
                String key = (String)en.nextElement();
                conn.setRequestProperty(key, propsHeader.getProperty(key));
            }

        }
    }

    public SyncML sendMessage(final SyncML msg) throws Exception {
        String syncML = marshallSyncML(msg);
        return sendMessage(syncML);
    }
    
    public String sendXMLMessage(final String msg)
    throws IOException, Sync4jException, RepresentationException {

        final byte[] yaData = msg.getBytes();

        conn.setRequestProperty("Content-Type", Constants.MIMETYPE_SYNCMLDS_XML);
        conn.setRequestProperty("Content-Length", "" + yaData.length);

        out = conn.getOutputStream();
        out.write(yaData);
        out.flush();

        InputStream in = conn.getInputStream();

        final int iResponseCode = conn.getResponseCode();

        if (iResponseCode != HttpURLConnection.HTTP_OK) {
            String error = "Response status: "
                         + iResponseCode
                         + ", Response message: "
                         + conn.getResponseMessage()
                         ;
            throw new IOException(error);
        }

        final String strResponseContentType = conn.getContentType();

        if (strResponseContentType == null)
        {
            throw new IOException("Content type: " + strResponseContentType);
        }

        if (strResponseContentType.equals(Constants.MIMETYPE_SYNCMLDS_XML) == false)
        {
            throw new IOException( "Content type: "
                                 + strResponseContentType
                                 + " (should be "
                                 + Constants.MIMETYPE_SYNCMLDS_XML
                                 + ")"
                                 );
        }

        lastMessage = msg;

        final int iResponseContentLength = conn.getContentLength();

        if (iResponseContentLength < 1)
        {
           throw new IOException("Response content length: " + iResponseContentLength);
        }

        final byte[] yaResponse = new byte[iResponseContentLength];

        int n = 0;
        int iBytesRead = 0;
        do
        {
            n = in.read(yaResponse,
                    iBytesRead,
                    yaResponse.length - iBytesRead);
            if (n > 0)
            {
                iBytesRead += n;
            }
        } while (n != -1);

        if (iBytesRead != iResponseContentLength)
        {
            // todo : throw exception - ?
        }

        lastResponse = new String(yaResponse);

        return lastResponse;
    }

    public SyncML sendMessage(final String msg)
    throws IOException, Sync4jException, RepresentationException {


        lastResponse = sendXMLMessage(msg);

        SyncML syncML = null;
        try {

            syncML = unmarshallSyncML(lastResponse);

        } catch(Exception e) {
            throw new Sync4jException(e);
        }

        return syncML;
    }

    public SyncML sendWBXMLMessage(final byte[] msg)
    throws IOException, Sync4jException, RepresentationException
    {
        final byte[] yaData = msg;

        conn.setRequestProperty("Content-Type", Constants.MIMETYPE_SYNCMLDS_WBXML);
        conn.setRequestProperty("Content-Length", "" + yaData.length);

        out = conn.getOutputStream();
        out.write(yaData);
        out.flush();

        InputStream in = conn.getInputStream();

        final int iResponseCode = conn.getResponseCode();

        if (iResponseCode != HttpURLConnection.HTTP_OK)
        {
            String error = "Response status: "
                         + iResponseCode
                         + ", Response message: "
                         + conn.getResponseMessage()
                         ;
            throw new IOException(error);
        }

        final String strResponseContentType = conn.getContentType();

        if (strResponseContentType == null)
        {
            throw new IOException("Content type: " + strResponseContentType);
        }

        if (strResponseContentType.equals(Constants.MIMETYPE_SYNCMLDS_WBXML) == false)
        {
            throw new IOException( "Content type: "
                                 + strResponseContentType
                                 + " (should be "
                                 + Constants.MIMETYPE_SYNCMLDS_WBXML
                                 + ")"
                                 );
        }

        final int iResponseContentLength = conn.getContentLength();

        if (iResponseContentLength < 1)
        {
           throw new IOException("Response content length: " + iResponseContentLength);
        }

        final byte[] yaResponse = new byte[iResponseContentLength];

        int n = 0;
        int iBytesRead = 0;
        do
        {
            n = in.read(yaResponse,
                    iBytesRead,
                    yaResponse.length - iBytesRead);
            if (n > 0)
            {
                iBytesRead += n;
            }
        } while (n != -1);

        if (iBytesRead != iResponseContentLength)
        {
            // todo : throw exception - ?
        }

        //
        //TRN - convert yaResponse from WBXML to XML string, then create the Message
        //
        String xmlResponse = WBXMLTools.wbxmlToXml(yaResponse);

        SyncML syncML = null;
        try {

            syncML = unmarshallSyncML(xmlResponse);

        } catch(Exception e) {
            throw new Sync4jException(e);
        }

        return syncML;
    }

    public void close()
    {
        if (conn != null)
        {
            conn.disconnect();
            try
            {
                out.close();
            }
            catch (java.io.IOException ex)
            {
                // ignore exception
            }
        }
    }

    public String toString()
    {
        return serverAddress;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastResponse() {
        return lastResponse;
    }

    private SyncML unmarshallSyncML(String response) throws Sync4jException {

        SyncML syncML = null;
        try {
            IBindingFactory f = BindingDirectory.getFactory("binding", SyncML.class);
            IUnmarshallingContext c = f.createUnmarshallingContext();

            syncML = (SyncML)c.unmarshalDocument(new ByteArrayInputStream(response.getBytes()), null);

        } catch(org.jibx.runtime.JiBXException e) {
            e.printStackTrace();
            throw new Sync4jException(e);
        } catch(Exception e) {
            e.printStackTrace();
            throw new Sync4jException(e);
        }
        return syncML;
    }

    private String marshallSyncML(SyncML syncML) throws Sync4jException {
        String msg = null;
        try {

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            IBindingFactory f = BindingDirectory.getFactory("binding", SyncML.class);
            IMarshallingContext c = f.createMarshallingContext();
            c.setIndent(0);
            c.marshalDocument(syncML, "UTF-8", null, bout);

            msg = new String(bout.toByteArray());

        } catch(org.jibx.runtime.JiBXException e) {
            e.printStackTrace();
            throw new Sync4jException(e);
        } catch(Exception e) {
            e.printStackTrace();
            throw new Sync4jException(e);
        }
        return msg;
    }
}
