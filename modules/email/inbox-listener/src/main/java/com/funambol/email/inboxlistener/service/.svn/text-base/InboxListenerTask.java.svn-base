/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.email.inboxlistener.service;

import com.funambol.pushlistener.framework.TaskConfiguration;
import org.apache.log4j.Logger;

import com.funambol.email.admin.dao.WSDAO;
import com.funambol.email.exception.AccountNotFoundException;
import com.funambol.pushlistener.service.config.PushListenerConfiguration;

import com.funambol.pushlistener.framework.Task;
import com.funambol.pushlistener.framework.TaskException;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.inboxlistener.dbdao.InboxListenerDAO;
import com.funambol.email.inboxlistener.engine.IMailServerAccountProvider;
import com.funambol.email.inboxlistener.engine.InboxListenerManager;
import com.funambol.email.inboxlistener.engine.ProviderFactory;
import com.funambol.email.inboxlistener.plugin.parser.KeyAccount;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.util.Def;
import com.funambol.framework.logging.LogContext;
import com.funambol.server.admin.ws.client.ServerInformation;

/**
 * @version $Id: InboxListenerTask.java,v 1.4 2008-05-18 15:01:51 nichele Exp $
 */
public class InboxListenerTask implements Task {

    // --------------------------------------------------------------- Constants
    /** Name of the property used to store the ms_login in the MDC context */
    private static final String LOG_MDC_LOGIN  = "ms_login";

    /** The logger */
    private Logger log = Logger.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Private data

    /**
     *
     */
    public InboxListenerTask(){
    }

    // -------------------------------------------------------------- Properties



    /**
     * startup notification
     */
    private boolean startupNotification = false;

    public boolean getStartupNotification() {
        return startupNotification;
    }

    public void setStartupNotification(boolean _startupNotification) {
        this.startupNotification = _startupNotification;
    }

    /**
     * notification just for new email
     */
    private boolean justNewNotification = false;

    public boolean getJustNewNotification() {
        return justNewNotification;
    }

    public void setJustNewNotification(boolean _justNewNotification) {
        this.justNewNotification = _justNewNotification;
    }

    /**
     * saveSubject
     */
    private boolean saveSubject = false;

    public boolean getSaveSubject() {
        return saveSubject;
    }

    public void setSaveSubject(boolean _saveSubject) {
        this.saveSubject = _saveSubject;
    }

    /**
     * saveSender
     */
    private boolean saveSender = false;

    public boolean getSaveSender() {
        return saveSender;
    }

    public void setSaveSender(boolean _saveSender) {
        this.saveSender = _saveSender;
    }

    /**
     * The syncSource to sync the email
     */
    private String syncSourceEmail = "mail";

    public String getSyncSourceEmail() {
        return syncSourceEmail;
    }

    public void setSyncSourceEmail(String _syncSourceEmail) {
        this.syncSourceEmail = _syncSourceEmail;
    }

    /**
     * The content type of emails
     */
    private String contentTypeEmail = "application/vnd.omads-email+xml";

    public String getContentTypeEmail() {
        return contentTypeEmail;
    }

    public void setContentTypeContact(String _contentTypeEmail) {
        this.contentTypeEmail = _contentTypeEmail;
    }

    /**
     * Mail server access object timeout
     */
    private String timeoutStore;

    public void setTimeoutStore(String timeoutStore) {
        this.timeoutStore = timeoutStore;
    }

    public String getTimeoutStore() {
        return timeoutStore;
    }
    
    /**
     * Mail server connection timeout.
     */
    private String timeoutConnection;

    public void setTimeoutConnection(String timeoutConnection) {
        this.timeoutConnection = timeoutConnection;
    }

    public String getTimeoutConnection() {
        return timeoutConnection;
    }
    
    /**
     * Account registry table name.
     */
    private String pushRegistryTableName;

    public void setPushRegistryTableName(String pushRegistryTableName) {
        this.pushRegistryTableName = pushRegistryTableName;
    }

    public String getPushRegistryTableName() {
        return pushRegistryTableName;
    }
            
    /**
     * The period
     */
    private long period = 60000;

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long _period) {
        this.period = _period;
    }

    private KeyAccount key = null;

    public void setKey(KeyAccount _key) {
        this.key = _key;
    }
    
    /**
     * must ssl certificates be validated ?
     */
    private boolean checkCertificates;
     
    public void setCheckCertificates(boolean checkCertificates) {
        this.checkCertificates = checkCertificates;
    }

    // ---------------------------------------------------------- Public Methods
    
    /**
     * This method implements the algorithm that has to be performed at each
     * execution of this task.
     * @throws com.funambol.pushlistener.framework.TaskException if an error occurs
     */
    public void execute() throws TaskException {

        MailServerAccount msa;

        try {

            IMailServerAccountProvider provider = ProviderFactory.getProvider(
                    key, pushRegistryTableName);

            msa = provider.getAccount(key);

            if (msa != null){
                
                InboxListenerDAO ildao = 
                        new InboxListenerDAO(pushRegistryTableName);
                
                // gets user bound to msa (ds-server db direct access; no webservice).
                boolean dsUserExists = ildao.dsUserExists(msa.getUsername());
                
                // if user exists then perform the task as usual.
                // if user does not exist then delete account and exit.
                if (!dsUserExists){                    
                    WSDAO WSDao = getWSDao();
                    WSDao.deleteAccount(msa.getId());                    
                    return;
                }

                InboxListenerManager ilmanager = new InboxListenerManager(ildao);
                ilmanager.init(msa, saveSubject, saveSender, 
                        timeoutStore, timeoutConnection, this.checkCertificates);
                        
                LogContext.setUserName(msa.getUsername());
                LogContext.set(LOG_MDC_LOGIN, msa.getMsLogin());
                        
                ilmanager.engine(msa, 
                                 this.justNewNotification,
                                 this.syncSourceEmail, 
                                 this.contentTypeEmail);
            }

        } catch (AccountNotFoundException ex) {
            if (log.isTraceEnabled()) {
                log.trace("Account not found with key: " + key.toString() + "", ex);
            }
        } catch (InboxListenerException e){
            log.error("Error performing inboxlistener task execution", e);
        } catch (Exception e){
            log.error("Error performing inboxlistener task execution", e);
        } finally {
            LogContext.removeUserName();
            LogContext.remove(LOG_MDC_LOGIN);
        }
       
    }


    @Override
    public boolean equals(Object o) {
        if (! (o instanceof InboxListenerTask)) {
            return false;
        }
        return key.equals(((InboxListenerTask)o).key);
    }

    @Override
    public int hashCode() {
        return this.key != null ? this.key.hashCode() : 0;
    }

    // --------------------------------------------------------- Private methods
    
    private WSDAO getWSDao() throws InboxListenerException{
        
        String endPoint = PushListenerConfiguration.getPushListenerConfiguration()
                                            .getPushListenerConfigBean()
                                            .getServerInformation()
                                            .getUrl().toString();
        String user     = PushListenerConfiguration.getPushListenerConfiguration()
                                            .getPushListenerConfigBean()
                                            .getServerInformation()
                                            .getUsername();
        String pwd      = PushListenerConfiguration.getPushListenerConfiguration()
                                            .getPushListenerConfigBean()
                                            .getServerInformation()
                                            .getPassword();
        WSDAO WSDao = new WSDAO(
                new ServerInformation(endPoint, user, pwd), Def.LOGGER_NAME);
        
        return WSDao;
    }

    public void configure(TaskConfiguration tc) throws TaskException {
       //do nothing
    }
}
