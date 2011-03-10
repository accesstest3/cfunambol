/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
package com.funambol.server.sms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.MessageFormat;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.funambol.common.sms.core.BinarySMSMessage;
import com.funambol.common.sms.core.DeliveryDetail;
import com.funambol.common.sms.core.DeliveryStatus;
import com.funambol.common.sms.core.SMSMessage;
import com.funambol.common.sms.core.SMSRecipient;
import com.funambol.common.sms.core.SMSSender;
import com.funambol.common.sms.core.TextualSMSMessage;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.sms.SMSProvider;
import com.funambol.framework.sms.SMSProviderException;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.DbgTools;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorException;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

/**
 * Simple SMSProvider that logs SMSs in a DB table.
 * <br/>
 * The SMSs are logged in <i>fnbl_sms</i> (the name is configurable) that must
 * have the following structure:
 * <ul>
 * <li>id: long</li>
 * <li>time: bigint</li>
 * <li>sender: varchar(255)</li>
 * <li>recipient: varchar(255)</li>
 * <li>format: varchar(16)</li>
 * <li>udh: varchar(255)</li>
 * <li>body: text for postgresql and varchar(1024) for mysql</li>
 * </ul>
 *
 * @version $Id$
 */
public class DBSMSProvider  implements SMSProvider,LazyInitBean{

    private static final String SMS_SPACE = "smsprovider.id";

    private static final String INSERT_SMS =
            "insert into {0} (id, time, sender, recipient, format, udh, body) VALUES (?, ?, ?, ?, ?, ?, ?)";

    // -------------------------------------------------------------- Properties
    /** The JNDI name of the datasource to be used */
    private String jndiDataSourceName = "jdbc/fnblcore";

    public void setJndiDataSourceName(String jndiDataSourceName) {
        this.jndiDataSourceName = jndiDataSourceName;
    }

    public String getJndiDataSourceName() {
        return this.jndiDataSourceName;
    }

    /** The table where the SMSs are stored */
    private String tableName = "fnbl_sms";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    // ------------------------------------------------------------ Private data
    /** The used datasource */
    private DataSource dataSource = null;

    /** The DBIDGenerator used to generate the primary key value */
    private DBIDGenerator dbIDGenerator = null;

    /**
     * The used log
     */
    private FunambolLogger logger =
            FunambolLoggerFactory.getLogger("funambol.server.sms.db-sms-provider");

    private String insertQuery = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of DBSMSProvider
     */
    public DBSMSProvider() {

    }

    /**
     * Initializes the dao instance
     * @throws BeanInitializationException if an error occurss
     */
    public void init() throws BeanInitializationException {

        insertQuery = MessageFormat.format(INSERT_SMS, tableName);

        if (jndiDataSourceName == null) {
            throw new BeanInitializationException("Error initializing DBSMSProvider." +
                                                  ". The jndiDataSourceName must be not null");
        }

        try {
            dataSource = DataSourceTools.lookupDataSource(jndiDataSourceName);
        } catch (NamingException e) {
            throw new BeanInitializationException("Data source '" + jndiDataSourceName + "' not found", e);
        }

        dbIDGenerator =  DBIDGeneratorFactory.getDBIDGenerator(SMS_SPACE, dataSource);
    }


    public DeliveryDetail sendSMS(SMSRecipient recipient, SMSSender sender, TextualSMSMessage message)
    throws SMSProviderException {

        if (logger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sending textual message: ").append(message);
            sb.append(", to: ").append(recipient);
            sb.append(", from: ").append(sender);
            logger.trace(sb.toString());
        }

        long id = insertSMS(recipient, sender, message);
        return new DeliveryDetail(String.valueOf(id));
    }

    public DeliveryDetail sendSMS(SMSRecipient recipient, SMSSender sender, BinarySMSMessage message)
    throws SMSProviderException {

        if (logger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sending binary message: ").append(message);
            sb.append(", to: ").append(recipient);
            sb.append(", from: ").append(sender);
            logger.trace(sb.toString());
        }

        long id = insertSMS(recipient, sender, message);
        return new DeliveryDetail(String.valueOf(id));
    }

    public DeliveryStatus getDeliveryStatus(DeliveryDetail deliveryDetail)
    throws SMSProviderException {
        if (logger.isTraceEnabled()) {
            logger.trace("Get delivery status for: " + deliveryDetail);
        }
        return new DeliveryStatus(DeliveryStatus.DELIVERED);
    }

    // --------------------------------------------------------- Private methods

    private long insertSMS(SMSRecipient recipient, SMSSender sender, SMSMessage message)
    throws SMSProviderException {
        assert (dataSource != null);

        if (message == null) {
            throw new IllegalArgumentException("The given message must be not null");
        }
        if (sender == null) {
            throw new IllegalArgumentException("The given sender must be not null");
        }
        if (recipient == null) {
            throw new IllegalArgumentException("The given recipient must be not null");
        }

        Connection        conn = null;
        PreparedStatement stmt = null;
        long              id   = 0;
        
        try {
            id = dbIDGenerator.next();

            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(insertQuery);

            stmt.setLong  (1, id);
            stmt.setLong  (2, System.currentTimeMillis());
            stmt.setString(3, sender.getSender());
            stmt.setString(4, recipient.getRecipient());
            stmt.setString(5, (message instanceof TextualSMSMessage) ? "text" : "binary");

            String body = null;
            if (message instanceof TextualSMSMessage) {
                stmt.setNull(6, Types.VARCHAR);
                
                body = ((TextualSMSMessage)message).getText();
            } else {
                String wdp = DbgTools.bytesToHex(((BinarySMSMessage)message).getWdp());
                stmt.setString(6, wdp);

                String wsp =     DbgTools.bytesToHex(((BinarySMSMessage)message).getWsp());
                String content = DbgTools.bytesToHex(((BinarySMSMessage)message).getContent());
                
                body = wsp + content;
            }
            stmt.setString(7, body);

            int affectedRows = stmt.executeUpdate();

        } catch (DBIDGeneratorException ex) {
            throw new SMSProviderException("Error retrieving unique id storing the sms", ex);
        } catch (SQLException ex) {
            throw new SMSProviderException("Error storing sms", ex);
        } finally {
            DBTools.close(conn, stmt, null);
        }
        return id;
    }
}
