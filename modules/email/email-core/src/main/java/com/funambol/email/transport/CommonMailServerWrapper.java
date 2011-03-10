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
package com.funambol.email.transport  ;


import java.util.Locale;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.util.Def;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.util.Utility;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import java.util.ArrayList;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


/**
 *
 * <p>
 * Create a connection with a Mail Server
 * </p>
 *
 * @version $Id: CommonMailServerWrapper.java,v 1.3 2008-05-18 15:12:46 nichele Exp $
 */
public abstract class CommonMailServerWrapper
        implements IMailServerWrapper {

    // --------------------------------------------------------------- Constants

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    /**     */
    private final boolean javaMailDebug = false;

    /**     */
    private final String keystoreFilename =
            System.getProperty("javax.net.ssl.trustStore");

    /**     */
    private final String keystorePassword =
            System.getProperty("javax.net.ssl.trustStorePassword");


    // -------------------------------------------------------------- Properties

    /**     */
    protected Session session = null;
    /**     */
    protected Store store ;
    /**     */
    protected DefaultFolder defaultFolder ;
    /**     */
    protected Folder mailDefaultFolder ;
    /**     */
    protected Locale locale;
    /**     */
    protected String serverType;
    /**     */
    protected String serverOut;
    /**     */
    protected String userAddress;
    /**     */
    protected String timeoutStore;
    /**     */
    protected String timeoutConnection;



    /**
     * opens the connection with the mail server.
     * Used by the sync session [EmailSyncSource.beginSync()] and by the
     * Inbox-Listener tool [MSTools.openConnection()].
     *
     * @param serverType String
     * @param outgoingServer SMPT address i.e. 192.168.0.1
     * @param outgoingPort 25 def.
     * @param smtpAuth boolean
     * @param incomingServer IMAP address i.e. 192.168.0.1
     * @param incomingPort 143 / 993 IMAP def.
     * @param df inbox, outbox, sent items,...
     * @param openInboxFolder
     * @param user String
     * @param password String
     * @param userOut
     * @param passwordOut
     * @param isSSLIn true/false
     * @param isSSLOut true/false
     * @param timeoutStore
     * @param timeoutConnection
     *
     * @throws EmailAccessException
     */
    public void openConnection(String  serverType,
            String  outgoingServer,
            String  outgoingPort,
            boolean smtpAuth,
            String  incomingServer,
            String  incomingPort,
            DefaultFolder df,
            boolean openInboxFolder,
            final String  user,
            final String  password,
            final String  userOut,
            final String  passwordOut,            
            boolean isSSLIn,
            boolean isSSLOut,
            String timeoutStore,
            String timeoutConnection,
            String sslSocketFactory)
            throws EmailAccessException {

        if (log.isTraceEnabled()) {
            log.trace("Connection Parameters ["+
                    "(service: "+this.getClass().getName()+ ") "+
                    "(out: " + outgoingServer + "/"+Utility.getMailServerIP(outgoingServer) + ") " +
                    "(port: " + outgoingPort + ") "+
                    "(smtpauth: " + smtpAuth + ") "+
                    "(in: " + incomingServer + "/" + Utility.getMailServerIP(incomingServer) + ") " +
                    "(port: " + incomingPort+") " +
                    "(user: " + user + " - userOut: " + userOut + ") (SSL in: " + isSSLIn + " - SSL out: " + isSSLIn + ") " +
                    "(sslSocketFactory: " + sslSocketFactory + ")]");
        }

        // set info
        this.userAddress = user;
        this.serverType  = serverType;
        this.serverOut   = outgoingServer;

        // containe for properties
        Properties props = new Properties();

        if (isSSLIn){
            this.setConnectionSSLIn(props, incomingServer, incomingPort,
                    timeoutStore, timeoutConnection, sslSocketFactory);
        } else {
            this.setConnectionNormalIn(props, incomingServer, incomingPort,
                    timeoutStore, timeoutConnection);
        }

        if (isSSLOut){
            this.setConnectionSSLOut(props, outgoingServer, outgoingPort, smtpAuth,
                    sslSocketFactory);
        } else {
            this.setConnectionNormalOut(props, outgoingServer, outgoingPort, smtpAuth);
        }

        long timeStart = System.currentTimeMillis();

        try{
            if (smtpAuth){
                
                Authenticator auth = new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userOut, passwordOut);
                    }
                };
                this.session = Session.getInstance(props, auth);
            } else {
                this.session = Session.getInstance(props, null);
            }
        } catch (Exception e) {
            log.error("Error Setting Authentication for Server: ", e);
            throw new EmailAccessException("Error Setting Authentication for Server (" + e.getMessage() + ")");
        }

        // @just for test
        this.session.setDebug(javaMailDebug);

        boolean isOpen = false;
        try {
            isOpen = singleOpenConnection(user, password);
        } catch (Exception e) {
            throw new EmailAccessException("Error connetting store: " + e.toString(), e);
        }

        if (isOpen){

            long timeStop = System.currentTimeMillis();
            if (log.isTraceEnabled()) {
                log.trace("Open Connection Execution Time (Success!): " + (timeStop - timeStart) + " ms");
            }

            try {
                this.mailDefaultFolder  = store.getDefaultFolder();
            } catch (Exception e) {
                log.error("Error getting default folder from store", e);
                throw new EmailAccessException("Error getting default folder from store", e);
            }

            openInboxFolder(openInboxFolder, df);

        } else {
            log.error("Error Opening Connection Store - Synchronization Interrupt");
        }

        if (log.isTraceEnabled()) {
            log.trace("Mail Server Connection opened!");
        }

    }

    /**
     * opens the connection with the mail server.
     * Used by the sync session [EmailSyncSource.beginSync()] and by the
     * Inbox-Listener tool [MSTools.openConnection()].
     *
     * @deprecated  As of version 6.5.11
     *
     * @param serverType String
     * @param outgoingServer SMPT address i.e. 192.168.0.1
     * @param outgoingPort 25 def.
     * @param smtpAuth boolean
     * @param incomingServer IMAP address i.e. 192.168.0.1
     * @param incomingPort 143 / 993 IMAP def.
     * @param df inbox, outbox, sent items,...
     * @param openInboxFolder
     * @param user String
     * @param password String
     * @param isSSLIn true/false
     * @param isSSLOut true/false
     * @param timeoutStore
     * @param timeoutConnection
     *
     * @throws EmailAccessException
     */
    public void openConnection(String  serverType,
            String  outgoingServer,
            String  outgoingPort,
            boolean smtpAuth,
            String  incomingServer,
            String  incomingPort,
            DefaultFolder df,
            boolean openInboxFolder,
            final String  user,
            final String  password,
            boolean isSSLIn,
            boolean isSSLOut,
            String timeoutStore,
            String timeoutConnection,
            String sslSocketFactory)
            throws EmailAccessException {
        
        openConnection(serverType,
            outgoingServer,
            outgoingPort,
            smtpAuth,
            incomingServer,
            incomingPort,
            df,
            openInboxFolder,
            user,
            password,
            null,
            null,            
            isSSLIn,
            isSSLOut,
            timeoutStore,
            timeoutConnection,
            sslSocketFactory);
    }

    /**
     *
     * @throws EmailAccessException
     */
    public abstract void openInboxFolder(boolean openInboxFolder,
            DefaultFolder df)
        throws EmailAccessException;
    /**
     *
     * @throws EmailAccessException
     */
    public abstract void releaseConnection()  throws EmailAccessException ;

    /**
     * this method verifies if the user/password are corrected.
     * Used by the EmailOfficer
     *
     * @param outgoingServer
     * @param outgoingPort
     * @param incomingServer
     * @param incomingPort
     * @param user
     * @param password
     * @param isSSLIn
     * @param timeoutStore
     * @param timeoutConnection
     * @throws EmailAccessException
     */
    public void checkUser(
            String outgoingServer,
            String outgoingPort,
            String incomingServer,
            String incomingPort,
            String user,
            String password,
            boolean isSSLIn,
            String timeoutStore,
            String timeoutConnection,
            String sslSocketFactory)
        throws EmailAccessException{

        if (log.isTraceEnabled()) {
            log.trace("\nCONNECTION PARAMs"+
                    "\nOutgoing         " + outgoingServer+
                    "("+Utility.getMailServerIP(outgoingServer)+")" +
                    "\nport             " + outgoingPort +
                    "\nIncoming         " + incomingServer+
                    "("+Utility.getMailServerIP(incomingServer)+")" +
                    "\nport             " + incomingPort +
                    "\nprotocol         " + this.getClass().getName() +
                    "\nuser             " + user +
                    "\nSSLIn            " + isSSLIn +
                    "\nsslSocketFactory " + sslSocketFactory);
        }

        // contains for properties
        Properties props = new Properties();

        if (isSSLIn){
            this.setConnectionSSLIn(props, incomingServer, incomingPort,
                    timeoutStore, timeoutConnection, sslSocketFactory);
        } else {
            this.setConnectionNormalIn(props, incomingServer, incomingPort,
                    timeoutStore, timeoutConnection);
        }

        try{
            this.session = Session.getInstance(props, null);
        } catch (Exception e) {
            throw new EmailAccessException("Error Setting Session for Server (" + e.getMessage() + ")", e);
        }

        // @just for test
        this.session.setDebug(javaMailDebug);

        long timeStart = System.currentTimeMillis();

        try{
            this.session = Session.getInstance(props, null);
        } catch (Exception e) {
            throw new EmailAccessException("Error Setting Session for Server (" + e.getMessage() + ")", e);
        }

        boolean isOpen = false;
        try {
            isOpen = singleOpenConnection(user, password);
        } catch (Exception e) {
            throw new EmailAccessException("Error guessing account: " + e.toString(), e);
        } finally {
            try {
                if (this.store != null && isOpen){
                    this.store.close();
                }
                this.store = null;
                this.session = null;
                props.clear();
            } catch (Exception e) {
                log.error("Error closing the store", e);
            }
        }

        long timeStop = System.currentTimeMillis();
        if (log.isTraceEnabled()) {
            log.trace("Check Account Execution Time (Success!): " + (timeStop - timeStart) + " ms");
        }


    }

    /**
     *
     * @param props Properties
     * @param incomingServer String
     * @param incomingPort String
     */
    protected abstract void setConnectionNormalIn(Properties props,
            String incomingServer,
            String incomingPort,
            String timeoutStore,
            String timeoutConnection);

    /**
     *
     * @param props Properties
     * @param outgoingServer String
     * @param outgoingPort String
     * @param smtpAuth boolean
     */
    protected abstract void setConnectionNormalOut(Properties props,
            String outgoingServer,
            String outgoingPort,
            boolean smtpAuth);
    /**
     *
     * @param props
     * @param incomingServer
     * @param incomingPort
     * @param provider
     * @param portalCheck
     * @param portalCheckTimeout
     */
    protected abstract void  setConnectionSSLIn(Properties props,
            String incomingServer,
            String incomingPort,
            String timeoutStore,
            String timeoutConnection,
            String sslSocketFactory);

    /**
     *
     * @param props Properties
     * @param outgoingServer String
     * @param outgoingPort String
     * @param smtpAuth boolean
     * @param provider Provider
     */
    protected abstract void  setConnectionSSLOut(Properties props,
            String outgoingServer,
            String outgoingPort,
            boolean smtpAuth,
            String sslSocketFactory);

    /**
     *
     *
     * @return Session
     */
    public Session getSession(){
        return this.session;
    }

    /**
     *
     * @return Store
     */
    public Store getStore(){
        return this.store;
    }

    /**
     *
     * @return Folder
     */
    public Folder getMailDefaultFolder(){
        return this.mailDefaultFolder;
    }

    /**
     *
     * @return DefaultFolder
     */
    public DefaultFolder getDefaultFolder(){
        return this.defaultFolder;
    }


    /**
     *
     *
     * @return Locale
     */
    public Locale getLocale(){
        return this.locale;
    }

    /**
     * used in the creation of the Message-ID
     * see IMAP-based  getEmailFromUID
     *
     * @return serverOut
     */
    public String getServerOut(){
        return this.serverOut;
    }

    /**
     * used in the creation of the Message-ID
     * see IMAP-based  getEmailFromUID
     *
     * @return String
     */
    public String getUserAddress(){
        return this.userAddress;
    }

    // --------------------------------------------------------- PRIVATE METHODS

    /**
     *
     *
     */
    private boolean singleOpenConnection(String user, String password)
      throws EmailAccessException {

        boolean isOpen = false;
        try {
            this.store = this.session.getStore();
            this.store.connect(user, password);
            isOpen = true;
        } catch (Exception e) {
            log.error("Error Opening Connection Store: ", e);
            throw new EmailAccessException("Error Opening Connection Store (" +
                    e.getMessage() + ")", e);
        }

        return isOpen;

    }

    //----------------------------------------------------------- Private Method

    /**
     * create a list with all the exception messages
     * @param t
     * @param listOfError
     */
    private void getExceptionList(Throwable t, ArrayList listOfError) {
        Throwable cause = t.getCause();
        if (cause != null) {
            listOfError.add(t);
            getExceptionList(cause, listOfError);
        } else {
            listOfError.add(t);
        }
    }

    /**
     * check the open connection error.
     * return true if there is a SSL exception
     * @param listOfError
     * @return true if theexception is related to SSL environment
     */
    private boolean hasSSLException(ArrayList listOfError) {

        // print result
        for (int i=0; i<listOfError.size(); i++){
            if (log.isTraceEnabled()) {
                log.trace("Error during Mail Server connection [" +
                        listOfError.get(i).getClass() + "]");
            }
            if (listOfError.get(i) instanceof javax.net.ssl.SSLException ||
                listOfError.get(i) instanceof javax.net.ssl.SSLKeyException ||
                listOfError.get(i) instanceof javax.net.ssl.SSLHandshakeException ||
                listOfError.get(i) instanceof java.security.cert.CertPathValidatorException ||
                listOfError.get(i) instanceof java.security.cert.CertificateExpiredException ||
                listOfError.get(i) instanceof sun.security.provider.certpath.SunCertPathBuilderException){
                return true;
            }

        }
        return false;
    }

}
