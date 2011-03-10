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
package com.funambol.email.transport;

import java.util.Locale;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;

import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.model.DefaultFolder;


/**
 *
 * <p>Interface for Mail Server Wrapper </p>
 *
 * @version $Id: IMailServerWrapper.java,v 1.1 2008-03-25 11:25:31 gbmiglia Exp $
 */
public interface IMailServerWrapper {


    /**
     * opens the connection with the mail server
     *
     *
     * @param serverType String
     * @param outgoingServer String
     * @param outgoingPort String
     * @param smtpAuth boolean
     * @param incomingServer String
     * @param incomingPort String
     * @param defaultFolder DefaultFolder
     * @param user String
     * @param password String
     * @param userOut String
     * @param passwordOut String
     * @param isSSLIn boolean
     * @param isSSLOut boolean
     * @throws EmailAccessException
     */
    public void openConnection(String  serverType,
            String  outgoingServer,
            String  outgoingPort,
            boolean smtpAuth,
            String  incomingServer,
            String  incomingPort,
            DefaultFolder defaultFolder,
            boolean openInboxFolder,
            String  user,
            String  password,
            String  userOut,
            String  passwordOut,            
            boolean isSSLIn,
            boolean isSSLOut,
            String timeoutStore,
            String timeoutConnection,
            String sslSocketFactory)
            throws EmailAccessException ;

    /**
     * opens the connection with the mail server
     * 
     * @deprecated  As of version 6.5.11
     *
     * @param serverType String
     * @param outgoingServer String
     * @param outgoingPort String
     * @param smtpAuth boolean
     * @param incomingServer String
     * @param incomingPort String
     * @param defaultFolder DefaultFolder
     * @param user String
     * @param password String
     * @param isSSLIn boolean
     * @param isSSLOut boolean
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
            throws EmailAccessException;    

    /**
     * closes the connection with the mail server
     *
     * @throws EmailAccessException
     */
    public void releaseConnection() throws EmailAccessException;


    /**
     *
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
     * @param sslSocketFactory
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
            throws EmailAccessException;

    /**
     *
     *
     * @return Session
     */
    public Session getSession();

    /**
     *
     * @return Store
     */
    public Store getStore();

    /**
     *
     * @return DefaultFolder
     */
    public DefaultFolder getDefaultFolder();


    /**
     *
     * @return Folder
     */
    public Folder getMailDefaultFolder();


    /**
     *
     *
     * @return Locale
     */
    public Locale getLocale();


}
