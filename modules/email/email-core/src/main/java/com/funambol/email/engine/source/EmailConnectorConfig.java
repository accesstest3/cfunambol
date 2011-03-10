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
package com.funambol.email.engine.source;


import com.funambol.server.config.Configuration;
import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanInstantiationException;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.BeanNotFoundException;

import com.funambol.email.exception.EmailConfigException;


/**
 * This class loads the parameter from the XML configuration file
 *
 * @version $Id: EmailConnectorConfig.java,v 1.3 2008-09-05 14:48:01 testa Exp $
 */
public class EmailConnectorConfig {

    // --------------------------------------------------------------- CONSTANTS

    /**     */
    public final static String beanName = "email/email/FunambolEmailConnector.xml";

    // -------------------------------------------------------------- PROPERTIES

    /**
     * datasource
     */
    private String dataSource;

    /**
     *
     */
    private boolean saveOnlyHeader;
    
    /** Store timeout. */
    private String timeoutStore;
    
    /** Connection timeout. */
    private String timeoutConnection;
    
    /** Push registry table name (eg: <code>fnbl_email_push_registry</code>). */
    private String pushRegistryTableName;
    
    /** must ssl certificates be validated ? */
    private boolean checkCertificates;

    /**
     * In case a Mail server error occours, a mail with size lower than 
     * mailSizeThresholdForCopy can be copyed otherwise has to be treated 
     * in a different way avoiding to download the whole email. 
     */
    private int mailSizeThresholdForCopy = 102400;

    //------------------------------------------------------------- CONSTRUCTORS

    /**
     *
     */
    public EmailConnectorConfig() {
    }

    //----------------------------------------------------------- PUBLIC METHODS

    /**
     * @return EmailConnectorConfig
     * @throws EmailConfigException
     */
    public static EmailConnectorConfig getConfigInstance()
    throws EmailConfigException {

        EmailConnectorConfig emailConf;
        try {
            emailConf =
                    (EmailConnectorConfig) Configuration.getConfiguration().getBeanInstanceByName(beanName);
        } catch (BeanInstantiationException bie) {
            throw new EmailConfigException(bie.getMessage(), bie);
        } catch (BeanInitializationException bie) {
            throw new EmailConfigException(bie.getMessage(), bie);
        } catch (BeanNotFoundException bie) {
            throw new EmailConfigException(bie.getMessage(), bie);
        } catch (BeanException be) {
            throw new EmailConfigException(be.getMessage(), be);
        } catch (Exception e) {
            throw new EmailConfigException(e.getMessage(), e);
        }

        return emailConf;

    }

    // ---------------------------------------------------- Properties accessors
    
    /**
     * @param _dataSource String
     */
    public void setDataSource(String _dataSource) {
        this.dataSource = _dataSource;
    }

    /**
     * @return dataSource
     */
    public String getDataSource() {
        return this.dataSource;
    }

    /**
     * @param _saveOnlyHeader String
     */
    public void setSaveOnlyHeader(boolean _saveOnlyHeader) {
        this.saveOnlyHeader = _saveOnlyHeader;
    }

    /**
     * @return saveOnlyHeader
     */
    public boolean getSaveOnlyHeader() {
        return this.saveOnlyHeader;
    }

    public void setPushRegistryTableName(String pushRegistryTableName) {
        this.pushRegistryTableName = pushRegistryTableName;
    }
    
    public String getPushRegistryTableName() {
        return pushRegistryTableName;
    }

    public void setTimeoutStore(String timeoutStore) {
        this.timeoutStore = timeoutStore;
    }

    public String getTimeoutStore() {
        return timeoutStore;
    }

    public void setTimeoutConnection(String timeoutConnection) {
        this.timeoutConnection = timeoutConnection;
    }

    public String getTimeoutConnection() {
        return timeoutConnection;
    }
    
    public void setCheckCertificates(boolean checkCertificates) {
        this.checkCertificates = checkCertificates;
    }

    public boolean getCheckCertificates() {
        return checkCertificates;
    }

    public int getMailSizeThresholdForCopy() {
        return mailSizeThresholdForCopy;
    }

    public void setMailSizeThresholdForCopy(int mailSizeThresholdForCopy) {
        this.mailSizeThresholdForCopy = mailSizeThresholdForCopy;
    }
}
