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
package com.funambol.server.engine;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URL;

import java.nio.charset.Charset;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.jibx.runtime.*;

import com.funambol.framework.core.*;
import com.funambol.framework.engine.pipeline.MessageProcessingContext;
import com.funambol.framework.engine.pipeline.PipelineManager;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.LogContext;
import com.funambol.framework.protocol.ProtocolException;
import com.funambol.framework.protocol.ProtocolUtil;
import com.funambol.framework.server.SyncResponse;
import com.funambol.framework.server.error.BadRequestException;
import com.funambol.framework.server.error.InvalidCredentialsException;
import com.funambol.framework.server.error.ServerException;
import com.funambol.framework.tools.SyncMLUtil;
import com.funambol.framework.tools.WBXMLSizeCalculator;
import com.funambol.framework.tools.WBXMLTools;
import com.funambol.framework.tools.XMLSizeCalculator;

import com.funambol.server.SyncMLCanonizer;
import com.funambol.server.config.Configuration;
import com.funambol.server.config.ConfigurationConstants;
import com.funambol.server.session.SessionHandler;

/**
 *  This class handles a synchronization request.
 *  <p>
 *  This server accepts synchronization requests addressed to the hostname
 *  indicated by the configuration property pointed by {CFG_SERVER_URI} (see
 *  Funambol.xml).
 *  <p>
 *
 *  LOG NAME: funambol.server
 *
 *  @version $Id: SyncAdapter.java,v 1.3 2008-05-22 19:54:33 nichele Exp $
 *
 */
public class SyncAdapter
implements ConfigurationConstants {

    // ------------------------------------------------------- Private constants
    protected static final String CONFIG_SYNCML_CANONIZER
    = "com/funambol/server/SyncMLCanonizer.xml";

    protected static final String PARAM_SESSION_ID = "sid";

    protected static final String HEADER_CONTENT_TYPE = "content-type";

    protected static final String HEADER_X_FUNAMBOL_CLIENT_REMOTE_ADDRESS =
            "X-funambol-client-remote-address";

    // ------------------------------------------------------------ Private data

    protected transient FunambolLogger log        = null;

    protected SessionHandler sessionHandler       = null;

    protected Configuration config                = null;

    protected PipelineManager pipelineManager     = null;

    protected MessageProcessingContext mpc        = null;

    protected SyncMLCanonizer syncMLCanonizer     = null;


    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new SyncAdapter.
     *
     * @param config the configuration object
     */
    public SyncAdapter(Configuration config) {
        super();

        log = FunambolLoggerFactory.getLogger("server");

        this.config = config;

        sessionHandler  = config.getSessionHandler();
        pipelineManager = config.getPipelineManager();

        try {
            syncMLCanonizer = (SyncMLCanonizer)config.getBeanInstanceByName(CONFIG_SYNCML_CANONIZER, true);
        } catch (Exception e) {
            log.error("Error creating the syncMLCanonizer", e);
            new Sync4jException("Error "
                                + e.getClass().getName()
                                + " creating the syncMLCanonizer: "
                                + e.getMessage()
                                ).printStackTrace();
        }

        mpc = new MessageProcessingContext();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Tells the SyncAdapter that a new Synchronizatin session is starting
     *
     * @param sessionId the session id
     */
    public void beginSync(String sessionId) {
        mpc.setSessionProperty(mpc.PROPERTY_SESSION_ID, sessionId);
        sessionHandler.beginSession(sessionId);
    }

    /**
     * Finalizes the sync session
     */
    public void endSync() {

        sessionHandler.endSession();

        log = null;
    }

    /**
     * process the incoming XML sync message
     *
     * @param requestURL the url of the request
     * @param msg must be non-null
     * @param parameters request parameters (at transport level)
     * @param headers request headers (at transport level)
     *
     */
    public SyncResponse processXMLMessage( final String requestURL,
                                           final byte[] msg       ,
                                           final Map    parameters,
                                           final Map    headers   )
    throws ServerException {
        SyncResponse response  = null;
        String       inMessage = null;
        SyncML       syncMLOut = null;
        String       charset   = null;

        // We check first the prolog for the encoding.
        charset = getCharSetOfProlog(msg);
        if (charset == null) { // If not in the prolog, use mime-type.
            charset = getCharSet(headers);
            if (charset == null) { // If still not defined default to UTF-8.
                charset = "UTF-8";
                if (log.isTraceEnabled()) {
                    log.trace("Charset (default): " + charset);
                }
            } else { // charset was defined in mime-type.
                if (log.isTraceEnabled()) {
                    log.trace("Charset from mime-type: " + charset);
                }
            }
        } else { // Charset was defined in the prolog.
            if (log.isTraceEnabled()) {
                log.trace("Charset of prolog: " + charset);
            }
        }

        charset = charset.toUpperCase();

        try {
            inMessage = new String(msg, charset);
        } catch (UnsupportedEncodingException e) {
            // if we have a not known encoding try the default.
            // FIXME, maybe better to bail out.
            inMessage = new String(msg);
        }

        syncMLOut = processMessage(requestURL, inMessage, parameters, headers);
        
        byte[] out = convert(syncMLOut, charset);

        response = new Sync4jResponse(out, syncMLOut);

        //
        // Clears the map of the request properties (it has scope request)
        //
        mpc.getRequestProperties().clear();

        return response;
    }

    /**
     * process the incoming WBXML sync message
     *
     * @param requestURL the url of the request
     * @param msg must be non-null
     * @param parameters request parameters (at transport level)
     * @param headers request headers (at transport level)
     *
     */
    public SyncResponse processWBXMLMessage( final String requestURL,
                                             final byte[] msg       ,
                                             final Map    parameters,
                                             final Map    headers   )
    throws ServerException {
        SyncResponse response = null;
        String inMessage = null;
        SyncML syncMLOut = null;
        String charset =  getCharSet(headers);

        //
        // Convert WBXML to XML, and then pass to processXMLMessage
        //
        if (log.isTraceEnabled()) {
            log.trace("Converting message from wbxml to xml");
            log.trace("Charset: " + charset + " (null, means wbxml defined)");
        }

        try {
            inMessage = WBXMLTools.wbxmlToXml(msg, charset);
        } catch (Sync4jException e) {
            throw new ServerException(e);
        }

        processMessage(requestURL, inMessage, parameters, headers);
        
        byte[] out = null;
        try {
            //
            // The marshalling is doing directly into WBXMLTools
            // because that method is calling in the other code too
            //
            out = WBXMLTools.toWBXML(syncMLOut);

        } catch(Exception e) {

            log.error("Error processing the WBXML message", e);

            throw new ServerException(e);
        }

        response = new Sync4jResponse(out, syncMLOut);

        //
        // Clears the map of the request properties (it has scope request)
        //
        mpc.getRequestProperties().clear();

        return response;
    }

    /**
     * Returns the charset encoding if it was found in the prolog.
     * @param msg the message in which the prolog is searched.
     * @returns the charset if specified otherwise null.
     */
    private String getCharSetOfProlog(byte[] msg) {
        // Check if we have a start prolog, "<?xml".
        if ((msg[0] == '<') && (msg[1] == '?') && (msg[2] == 'x') && (msg[3] == 'm') && (msg[4] == 'l')) {
            // Now start looking for the end of the prolog.
            int length = msg.length - 1; // substract one for the double byte check.
            for (int i = 5; i < length; i++) {
                 if ((msg[i] == '?') && (msg[i+1] == '>')) {
                     // We found the end of the prolog.
                     String prolog = new String(msg, 0, i+1);
                     if (log.isTraceEnabled()) {
                         log.trace("Message prolog: " + prolog);
                     }
                     // Search for encoding keyword.
                     int p = prolog.indexOf("encoding");
                     // First double quote after the encoding.
                     int q1 = prolog.indexOf('"', p);
                     // Second double quote after the encoding.
                     int q2 = prolog.indexOf('"', q1+1);
                     return prolog.substring(q1+1, q2);
                 }
            }
        }
        return null; // Was not defined
    }

    /**
     * Returns the charset encoding based on the header information.
     * @param headers the collection of headers of the request.
     * @returns the charset if specified otherwise null.
     */
    private String getCharSet(Map headers) {
        String contentType = (String)headers.get(HEADER_CONTENT_TYPE);
        if (log.isTraceEnabled()) {
            log.trace("Message has content type: " + contentType);
        }
        if (contentType != null) {
            // Check if we have a charset at all.
            int pointer = contentType.indexOf(';');
            if (pointer > 0) {
                // Now strip of the 'charset=' by searching of the first '='.
                // Only one is allowed anyway.
                pointer = contentType.indexOf('=', pointer);
                if (pointer > 0) {
                    String result = contentType.substring(pointer+1);
                    result = result.trim();
                    if (result !=  null) {
                        if (result.length() > 0) {
                            if (result.charAt(0) == '"') {
                                if (result.charAt(result.length() - 1) == '"') {
                                    result = result.substring(1, result.length() - 1);
                                }
                            }
                        }
                    }

                    //
                    // If the charset is not supported, then the UTF-8 is used.
                    //
                    if (!Charset.isSupported(result.trim())) {
                        result = "UTF-8";
                    }

                    // Just trim whitespace incase the client is not polite.
                    return result.trim();
                }
            }
        }
        return null; // Was not defined
    }

    /**
     * Used to process a status information as needed by the client object (i.e.
     * errors or success).
     *
     * @param statusCode the status code
     * @param statusMessage additional descriptive message
     *
     * @see com.funambol.framework.core.StatusCode for valid status codes.
     */
    public SyncResponse processStatusCode(int statusCode, String info){
        if (statusCode != StatusCode.OK) {
            sessionHandler.abort(statusCode);
        }
        return null;
    }

    // --------------------------------------------------------- private methods

    private SyncML processMessage(String requestURL,
                                  String inMessage,
                                  Map    parameters,
                                  Map    headers   )
    throws ServerException {

        SyncML syncMLIn, syncMLOut = null;

        //
        // If funambol is not in the debug mode is not possible to print the
        // message's content because it could contain sensitive data.
        //
        if (config.isDebugMode()) {
            if (log.isTraceEnabled()) {
                log.trace("Message to translate into the SyncML object:\n" +
                          inMessage);
            }
        }

        inMessage = syncMLCanonizer.canonizeInput(inMessage);

        syncMLIn = convert(inMessage);

        //
        // Initialize the device ID from the client request
        //
        String clientDeviceId = syncMLIn.getSyncHdr().getSource().getLocURI();

        //
        // Set the device id in the LogContext in order to
        // have this information in the log
        //
        LogContext.setDeviceId(clientDeviceId);
        setThreadName(clientDeviceId);

        if (syncMLIn.getSyncHdr().getCred() != null) {
            String clientAddress = (String)headers.get(HEADER_X_FUNAMBOL_CLIENT_REMOTE_ADDRESS);
            Authentication auth = syncMLIn.getSyncHdr().getCred().getAuthentication();
            if (auth != null) {
                auth.setClientAddress(clientAddress);
            }
        }

        mpc.setRequestProperty(MessageProcessingContext.PROPERTY_REQUEST_PARAMETERS, parameters);
        mpc.setRequestProperty(MessageProcessingContext.PROPERTY_REQUEST_HEADERS   , headers   );

        if (pipelineManager != null) {
            if (log.isTraceEnabled()) {
                log.trace("Calling input pipeline");
            }
            pipelineManager.preProcessMessage(mpc, syncMLIn);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Pipeline manager is null...preprocess message not performed");
            }
        }

        syncMLOut = processInputMessage(syncMLIn);

        try {
            setRespURI(requestURL                                   ,
                       syncMLIn.getSyncHdr().getTarget().getLocURI(),
                       syncMLOut                                    ,
                       (String) mpc.getSessionProperty(mpc.PROPERTY_SESSION_ID));
        } catch (Exception ex) {
            throw new ServerException("Error setting the respURI (" +
                                      ex.getMessage() + ")",
                                      ex);
        }
        checkRespURISize(syncMLOut.getSyncHdr().getRespURI());

        if (pipelineManager != null) {
            if (log.isTraceEnabled()) {
                log.trace("Calling output pipeline");
            }
            pipelineManager.postProcessMessage(mpc, syncMLOut);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Pipeline manager is null...postprocess message not performed");
            }
        }

        // The sensitive data are hidden if funambol is not in debug mode.
        //
        if (log.isTraceEnabled()) {
            log.trace("Outgoing message: " +
                      SyncMLUtil.toXML(syncMLOut, config.isDebugMode()));
        }

        return syncMLOut;
    }

    /**
     * Processes the input SyncML message after convertion to objects. See class
     * description for more information.
     *
     * @return the response message
     *
     * @throws ServerException in case of errors
     */
    private SyncML processInputMessage(final SyncML syncMLIn) throws ServerException {
        try {
            try {
                sessionHandler.setSizeCalculator(
                    getSizeCalculator()
                );
                return sessionHandler.processMessage(syncMLIn, mpc);
            } catch (InvalidCredentialsException e) {
                return sessionHandler.processError(syncMLIn, e);
            } catch (ServerException e) {
                return sessionHandler.processError(syncMLIn, e);
            } catch (ProtocolException e) {
                return sessionHandler.processError(syncMLIn,
                                       new BadRequestException(e.getMessage()));
            }

        } catch (Sync4jException e1) {
            //
            // This can be due only to processError
            //
            throw new ServerException(e1);
        }
    }

    /**
     * Converts the given SyncML message into a <i>SyncML</i> object.
     *
     * @param msg the SyncML message
     *
     * @return the corresponding SyncML message
     *
     * @throws ServerException in case of translating errors
     */
    private SyncML convert(final String msg) throws ServerException {
        try {
            IBindingFactory f = BindingDirectory.getFactory("binding", SyncML.class);
            IUnmarshallingContext c = f.createUnmarshallingContext();

            Object syncML = c.unmarshalDocument(new StringReader(msg));

            return (SyncML)syncML;
        } catch(JiBXException e) {
            log.error("Error unmarshalling message", e);
            throw new ServerException(e);
        }
    }

    /**
     * Converts a <i>SyncML</i> object into the corresponding SyncML message (as
     * String).
     *
     * @param msg the SyncML object
     *
     * @return the corresponding SyncML message
     *
     * @throws ServerException in case of translation errors
     */
    private byte[] convert(final SyncML msg, String charset)
    throws ServerException {
        try {
            if (log.isTraceEnabled()) {
                log.trace("Creating response with charset: " + charset);
            }

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            IBindingFactory f = BindingDirectory.getFactory("binding", SyncML.class);
            IMarshallingContext c = f.createMarshallingContext();
            c.setIndent(0);
            c.marshalDocument(msg, charset, null, bout);

            //
            // Turn the output message in the canonical form and return it
            //
            return syncMLCanonizer.canonizeOutput(bout.toString(charset)).getBytes(charset);

        } catch(Exception e) {
            log.error("Error converting the message", e);

            throw new ServerException(e);
        }
    }

    /**
     * Set the RespURI in the given message from the server configured
     * serverURI.
     *
     * @param requestURL the url of the request
     * @param targetURI the target url of the request
     * @param msg the message into wich set the RespURI
     * @param sessionId the session id
     *
     * @throws Exception if an error occurs.
     */
    private void setRespURI(String requestURL     ,
                            String targetURI      ,
                            SyncML msg            ,
                            final String sessionId)
    throws Exception {

        String responseURI = null;
        String configuredServerURI =
            config.getServerConfig().getEngineConfiguration().getServerURI();

        if (StringUtils.isNotEmpty(configuredServerURI)) {
            if (log.isTraceEnabled()) {
                log.trace("Using the configured server uri to create the RespURI");
            }

            // Create StringBuffer with estimated size 128, in order to
            // avoid growing. Default size 16 is to short most likely.
            StringBuffer sbRespURI = new StringBuffer(128);

            sbRespURI.append(config.getServerConfig().getEngineConfiguration().
                             getServerURI());
            sbRespURI.append(";jsessionid=").append(sessionId);

            responseURI = sbRespURI.toString();
        } else {

            //
            // Use the Target URI to build the serverURI or, if this is a
            // malformed URL, use the requestURL
            //
            try {
                URL url = new URL(targetURI);
                requestURL = targetURI;
            } catch(MalformedURLException e) {
                //do nothing: uses the given requestURL
                if (log.isTraceEnabled()) {
                    log.trace("The target URI is a malformed URL");
                }
            }

            responseURI = getResponseUri(requestURL, sessionId);
            if (log.isTraceEnabled()) {
                log.trace("Using the request url to create the RespURI");
            }
        }

        msg.getSyncHdr().setRespURI(responseURI);
    }

    /**
     * Checks if the RespURI element is going to be bigger than the space we
     * reserved for it. In this case we trace a worning in the log. Note that
     * not necessarily a RespURI bigger than the save space will result in a
     * message bigger then the maxmsgsize, therefore a warning looks the more
     * appropriate level at which logging it.
     *
     * @param uri the resp URI
     */
    private void checkRespURISize(String uri) {
        //
        // The worse case is using XML: <RespURI></RespURI> -> 20 chars
        //
        final long s = uri.length() + 20;
        final long h = getSizeCalculator().getRespURIOverhead();


        if (log.isWarningEnabled() && (h < s)) {
            log.warn("The RespURI element size ("
                     + s
                     + ") exeeds the reserved hoverhead ("
                     + h
                     + ')'
                     );
        }
    }

    /**
     * Returns the MessageSizeCalculator to be used accordingly to the message
     * content type (retrieved by the processing context)
     *
     * @return
     */
    private MessageSizeCalculator getSizeCalculator() {
        Map headers = (Map)mpc.getRequestProperty(mpc.PROPERTY_REQUEST_HEADERS);

        String contentType = (String)headers.get(HEADER_CONTENT_TYPE);
        if (contentType != null) {
            contentType = contentType.split(";")[0];
        }
        MessageSizeCalculator calculator = null;

        if ((contentType != null) && Constants.MIMETYPE_SYNCMLDS_WBXML.equals(contentType)) {
            calculator = new WBXMLSizeCalculator();
        } else {
            calculator = new XMLSizeCalculator();
        }

        return calculator;
    }

    /**
     * Creates the RespURI using the given requestURL and the sessionID.
     * The responseURI is created using the same protocol, host, port and path of
     * the request.
     * @param requestURL the request url
     * @param sessionId the sessionId
     * @return String the responseURI. It is created use the same protocol, host,
     *                port and path of the request.
     * @throws MalformedURLException if an error occurs.
     */
    private String getResponseUri(String requestURL,
                                  String sessionId)
    throws MalformedURLException {

        URL url = new URL(requestURL);

        String protocol     = url.getProtocol();
        String server       = url.getHost();
        int    port         = url.getPort();
        String path         = url.getPath();
        int indexJsessionid = path.indexOf("jsessionid");

        //
        // Checks if the path contains a jsessionid. If so, we have to remove it.
        //
        if (indexJsessionid != -1) {
            path = path.substring(0, indexJsessionid);
            if (path.endsWith(";")) {
                path = path.substring(0, path.length() - 1);
            }
        }

        // Create StringBuffer with estimated size 128, in order to
        // avoid growing. Default size 16 is to short most likely.
        StringBuffer respURI = new StringBuffer(128);
        respURI.append(protocol).append("://").append(server);
        if (port != -1) {
            respURI.append(':').append(port);
        }
        respURI.append(path).append(";jsessionid=").append(sessionId);

        return respURI.toString();
    }


    /**
     * Sets the thread name based on the current name and the current device id
     * @param threadName the current thread name
     * @param deviceId the deviceId
     * @return the new thread name =  threadName [deviceId] []
     */
    private static void setThreadName(String deviceId) {
        String threadName = Thread.currentThread().getName();
        if (threadName == null) {
            return;
        }
        if (threadName.indexOf('[') != -1) {
            // nothing to do. Thread name has been already modified to contained
            // context information
            return;
        }

        if (deviceId == null) {
            deviceId = "";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(threadName);
        sb.append(" [").append(deviceId).append(']');

        // username not available here
        sb.append(" []");
        
        Thread.currentThread().setName(sb.toString());
    }

    // ------------------------------------------------------------ SyncResponse

    private static class Sync4jResponse implements SyncResponse {
        private byte[] msg;
        private boolean completed;

        /**
         * Create a new Sync4jResponse.
         *
         * @param msg the marshalled response
         * @param syncML the SyncML object
         * @param resultMimeType the mime type
         *
         */
        private Sync4jResponse(final byte[]  msg,
                               final SyncML  syncML) {
            this.msg            = msg;
            this.completed = (syncML.isLastMessage() &&
                             ProtocolUtil.noMoreResponse(syncML) &&
                             (ProtocolUtil.getStatusChal(syncML) == null));
        }

        public byte[] getMessage() {
            return this.msg;
        }

        /**
         * Is this message the last message allowed for the current session?
         *
         * @return true if yes, false otherwise
         */
        public boolean isCompleted() {
            return completed;
        }
    }
}
