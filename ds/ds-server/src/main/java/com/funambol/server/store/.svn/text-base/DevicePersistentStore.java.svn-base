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
package com.funambol.server.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Map;

import com.funambol.framework.filter.AllClause;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.ConvertDatePolicy;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.store.*;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.DBTools;


/**
 * This is the store for device information in the Sync4j engine.
 * Currently it persistes the following classes:
 * <ul>
 * <li>com.funambol.server.engine.Sync4jDevice</li>
 * </ul>
 *
 * @version $Id: DevicePersistentStore.java,v 1.2 2008-05-22 10:52:12 nichele Exp $
 *
 */
public class DevicePersistentStore
extends BasePersistentStore
implements PersistentStore, java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public  static final String NS_DEVICE               = "device";

    public static final int SQL_SELECT_ALL_DEVICES      = 0;
    public static final int SQL_GET_DEVICE              = 1;
    public static final int SQL_INSERT_DEVICE           = 2;
    public static final int SQL_UPDATE_DEVICE           = 3;
    public static final int SQL_DELETE_DEVICE           = 4;
    public static final int SQL_COUNT_DEVICES           = 5;
    public static final int SQL_UPDATE_DEVICE_ID_CAPS   = 6;
    public static final int SQL_UPDATE_SENT_SERVER_CAPS = 7;

    // ------------------------------------------------------------ Private data

    // -------------------------------------------------------------- Properties

    private String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    /**
     * The default notification builder to set creating a new device
     */
    private String defaultNotificationBuilder;

    public String getDefaultNotificationBuilder() {
        return defaultNotificationBuilder;
    }

    public void setDefaultNotificationBuilder(String defaultNotificationBuilder) {
        this.defaultNotificationBuilder = defaultNotificationBuilder;
    }

    /**
     * The default notification sender to set creating a new device
     */
    private String defaultNotificationSender;

    public String getDefaultNotificationSender() {
        return defaultNotificationSender;
    }

    public void setDefaultNotificationSender(String defaultNotificationSender) {
        this.defaultNotificationSender = defaultNotificationSender;
    }

    // ------------------------------------------------------------ Constructors

    // ---------------------------------------------------------- Public methods

    /**
     * Delete the device from the data store.
     *
     * @param o The object that must be a device for this method
     *
     * @throws PersistentException in case of error deleting the device
     */
    public boolean delete(Object o) throws PersistentStoreException {
        if (o instanceof Sync4jDevice) {
            Sync4jDevice d = (Sync4jDevice) o;

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = coreDataSource.getConnection();

                stmt = conn.prepareStatement(sql[SQL_DELETE_DEVICE]);
                stmt.setString(1, d.getDeviceId());
                stmt.executeUpdate();

                return true;
            } catch (SQLException e) {
                throw new PersistentStoreException("Error deleting the device " + d, e);
            } catch (Exception e) {
                throw new PersistentStoreException("Error deleting the device " + d, e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
        }
        return false;
    }

    public boolean store(Object o, String operation)
    throws PersistentStoreException {
        return false;
    }

    /**
     * Insert or Update the device from the data store.
     *
     * @param o The object that must be a device for this method
     *
     * @throws PersistentException in case of error storing the device
     */
    public boolean store(Object o) throws PersistentStoreException {
        if (o instanceof Sync4jDevice) {
            Sync4jDevice d = (Sync4jDevice) o;

            Connection conn        = null;
            PreparedStatement stmt = null;
            int n = 0;

            try {
                if (d.getDeviceId() == null || d.getDeviceId().equals("")) {
                    throw new PersistentStoreException("The device id must be not null");
                }
                conn = coreDataSource.getConnection();

                if (d.getDeviceId() != null && !d.getDeviceId().equals("")) {
                    stmt = conn.prepareStatement(sql[SQL_UPDATE_DEVICE]);

                    stmt.setString(1, d.getDescription());
                    stmt.setString(2, d.getType());

                    byte[] nonce = d.getClientNonce();
                    stmt.setString(3, ((nonce != null) ? new String(Base64.encode(nonce)) : null));

                    nonce = d.getServerNonce();
                    stmt.setString(4, ((nonce != null) ? new String(Base64.encode(nonce)) : null));

                    stmt.setString(5, d.getServerPassword());
                    stmt.setString(6, d.getTimeZone());

                    stmt.setString(7, getDBValueForConvertDatePolicy(d.getConvertDatePolicy()));

                    stmt.setString(8, d.getCharset());

                    if ((d.getCapabilities() != null) &&
                        (d.getCapabilities().getId() != null)) {
                        stmt.setInt(9, d.getCapabilities().getId().intValue());
                    } else {
                        stmt.setNull(9, java.sql.Types.INTEGER);
                    }
                    stmt.setString(10, d.getAddress());
                    stmt.setString(11, d.getMsisdn());
                    stmt.setString(12, d.getNotificationBuilder());
                    stmt.setString(13, d.getNotificationSender());
                    stmt.setString(14, d.getDeviceId());

                    n = stmt.executeUpdate();
                    stmt.close();
                    stmt = null;
                }

                if (n == 0) {
                    //
                    // Setting default builder
                    //
                    if (d.getNotificationBuilder() == null) {
                        d.setNotificationBuilder(defaultNotificationBuilder);
                    }
                    //
                    // Setting default sender
                    //
                    if (d.getNotificationSender() == null) {
                        d.setNotificationSender(defaultNotificationSender);
                    }
                    //
                    // The first time!!!
                    //
                    stmt = conn.prepareStatement(sql[SQL_INSERT_DEVICE]);
                    stmt.setString(1, d.getDeviceId());
                    stmt.setString(2, d.getDescription());
                    stmt.setString(3, d.getType());

                    byte[] nonce = d.getClientNonce();
                    stmt.setString(4, ((nonce != null) ? new String(Base64.encode(nonce)) : null));

                    nonce = d.getServerNonce();
                    stmt.setString(5, ((nonce != null) ? new String(Base64.encode(nonce)) : null));

                    stmt.setString(6, d.getServerPassword());

                    if ((d.getCapabilities() != null) &&
                        (d.getCapabilities().getId() != null)) {
                        stmt.setInt(7, d.getCapabilities().getId().intValue());
                    } else {
                        stmt.setNull(7, java.sql.Types.INTEGER);
                    }
                    stmt.setString(8,  d.getTimeZone());
                    stmt.setString(9,  getDBValueForConvertDatePolicy(d.getConvertDatePolicy()));
                    stmt.setString(10, d.getCharset());
                    stmt.setString(11, d.getAddress());
                    stmt.setString(12, d.getMsisdn());
                    stmt.setString(13, d.getNotificationBuilder());
                    stmt.setString(14, d.getNotificationSender());
                    stmt.setBoolean(15, false); // sent_server_caps

                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new PersistentStoreException("Error storing the device " + d, e);
            } catch (Exception e) {
                throw new PersistentStoreException("Error storing the device " + d, e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
            return true;
        }
        return false;
    }

    /**
     * Update the capabilities identifier of the device with the specific
     * identifier.
     *
     * @param deviceId the identifier of the device
     * @param capsId   the identifier of the capabilities
     *
     * @throws PersistentStoreException in case of error storing the device
     */
    public void store(String deviceId, Long capsId)
    throws PersistentStoreException {
        Connection conn        = null;
        PreparedStatement stmt = null;

        try {
            conn = coreDataSource.getConnection();
            stmt = conn.prepareStatement(sql[SQL_UPDATE_DEVICE_ID_CAPS]);
            stmt.setLong  (1, capsId.longValue());
            stmt.setString(2, deviceId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new PersistentStoreException(
                "Error storing the identifier of capabilities (" + capsId + ") "
                + "for device " + deviceId, e);
        } catch (Exception e) {
            throw new PersistentStoreException(
                "Error storing the identifier of capabilities (" + capsId + ") "
                + "for device " + deviceId, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    /**
     * Update the capabilities identifier of the device with the specific
     * identifier.
     *
     * @param deviceId the identifier of the device
     * @param sentServerCaps are the server capabilities arelady sent to the client?
     *
     * @throws PersistentStoreException in case of error storing the device
     */
    public void store(String deviceId, boolean sentServerCaps)
    throws PersistentStoreException {
        Connection conn        = null;
        PreparedStatement stmt = null;

        try {
            conn = coreDataSource.getConnection();
            stmt = conn.prepareStatement(sql[SQL_UPDATE_SENT_SERVER_CAPS]);
            stmt.setBoolean(1, sentServerCaps);
            stmt.setString (2, deviceId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new PersistentStoreException(
                "Error updating the flag about the server caps for device " + deviceId, e);
        } catch (Exception e) {
            throw new PersistentStoreException(
                "Error updating the flag about the server caps for device " + deviceId, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    /**
     * Read the device from the data store.
     *
     * @param o The object that must be a device for this method
     * @throws PersistentStoreException in case of error reading the device
     */
    public boolean read(Object o) throws PersistentStoreException {
        if (o instanceof Sync4jDevice) {
            Sync4jDevice d = (Sync4jDevice) o;

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);

                stmt = conn.prepareStatement(sql[SQL_GET_DEVICE]);
                stmt.setString(1, d.getDeviceId());
                rs = stmt.executeQuery();

                if (rs.next() == false) {
                    throw new NotFoundException("Device not found for '" + d.getDeviceId() + "'");
                }

                d.setDescription(trimEnd(rs.getString(1)));
                d.setType(trimEnd(rs.getString(2)));
                String value = trimEnd(rs.getString(3));
                if (value == null) {
                    d.setClientNonce(null);
                } else if (value.equals("")) {
                    d.setClientNonce(new byte[0]);
                } else {
                    d.setClientNonce(Base64.decode(value.getBytes()));
                }
                value = trimEnd(rs.getString(4));
                if (value == null) {
                    d.setServerNonce(null);
                } else if (value.equals("")) {
                    d.setServerNonce(new byte[0]);
                } else {
                    d.setServerNonce(Base64.decode(value.getBytes()));
                }
                d.setServerPassword(trimEnd(rs.getString(5)));
                d.setTimeZone(trimEnd(rs.getString(6)));

                d.setConvertDatePolicy(getConvertDatePolicyFromDBValue(rs.getString(7)));

                d.setCharset(trimEnd(rs.getString(8)));
                String capsId = trimEnd(rs.getString(9));
                if (capsId != null) {
                    Capabilities caps = new Capabilities();
                    caps.setId(new Long(capsId));
                    d.setCapabilities(caps);
                } else {
                    d.setCapabilities(null);
                }
                d.setAddress(trimEnd(rs.getString(10)));
                d.setMsisdn(trimEnd(rs.getString(11)));
                d.setNotificationBuilder(trimEnd(rs.getString(12)));
                d.setNotificationSender(trimEnd(rs.getString(13)));
                d.setSentServerCaps(rs.getBoolean(14));

            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading the device " + d, e);
            } catch (NotFoundException e) {
                 throw e;
            } catch (Exception e) {
                throw new PersistentStoreException("Error reading the device " + d, e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
            return true;
        }
        return false;
    }

    public Object[] read(Class objClass) throws PersistentStoreException {
        return null;
    }

    /**
      * Read all informations
      * @param o whichever object Sync
      * @param clause condition where for select
      *
      * @return Object[] array of object
     */
    public Object[] read(Object o, Clause clause)
    throws PersistentStoreException  {
        if (o instanceof Sync4jDevice) {

            Connection conn        = null;
            PreparedStatement stmt = null;
            ResultSet rs           = null;

            ArrayList ret = new ArrayList();

            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);

                String query = sql[SQL_SELECT_ALL_DEVICES];
                if ( (clause != null) && (! (clause instanceof AllClause))) {
                    PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                    if (where.sql.length() > 0) {
                        query += " where " + where.sql;
                    }
                }
                stmt = conn.prepareStatement(query);
                if ( (clause != null) && (! (clause instanceof AllClause))) {
                    PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                    for (int i = 0; i < where.parameters.length; ++i) {
                        stmt.setObject(i + 1, where.parameters[i]);
                    }
                }
                rs = stmt.executeQuery();

                while (rs.next()) {
                    Sync4jDevice d = new Sync4jDevice();
                    d.setDeviceId   (trimEnd(rs.getString(1)));
                    d.setDescription(trimEnd(rs.getString(2)));
                    d.setType       (trimEnd(rs.getString(3)));

                    String value = trimEnd(rs.getString(4));
                    if (value == null) {
                        d.setClientNonce(null);
                    } else if (value.equals("")) {
                        d.setClientNonce(new byte[0]);
                    } else {
                        d.setClientNonce(Base64.decode(value.getBytes()));
                    }

                    value = trimEnd(rs.getString(5));
                    if (value == null) {
                        d.setServerNonce(null);
                    } else if (value.equals("")) {
                        d.setServerNonce(new byte[0]);
                    } else {
                        d.setServerNonce(Base64.decode(value.getBytes()));
                    }
                    d.setServerPassword   (trimEnd(rs.getString(6)));
                    d.setTimeZone         (trimEnd(rs.getString(7)));
                    d.setConvertDatePolicy(getConvertDatePolicyFromDBValue(rs.getString(8)));
                    d.setCharset          (trimEnd(rs.getString(9)));

                    String capsId = trimEnd(rs.getString(10));
                    if (capsId != null) {
                        Capabilities caps = new Capabilities();
                        caps.setId(new Long(capsId));
                        d.setCapabilities(caps);
                    } else {
                        d.setCapabilities(null);
                    }
                    d.setAddress            (trimEnd(rs.getString(11)));
                    d.setMsisdn             (trimEnd(rs.getString(12)));
                    d.setNotificationBuilder(trimEnd(rs.getString(13)));
                    d.setNotificationSender (trimEnd(rs.getString(14)));

                    d.setSentServerCaps(rs.getBoolean(15));

                    ret.add(d);
                }
                Sync4jDevice[] array = new Sync4jDevice[ret.size()];
                for (int i = 0; i < ret.size(); i++) {
                    array[i] = (Sync4jDevice)ret.get(i);
                }
                return array;
            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading devices", e);
            } catch (Exception e) {
                throw new PersistentStoreException("Error reading devices", e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
        }
        return null;

    }

    /**
     * Select the number of devices that satisfy the conditions specified in input.
     *
     * @param clause The additional where clause.
     * @return int number of devices
     */
    public int count(Object o, Clause clause) throws PersistentStoreException {
        if (o instanceof Sync4jDevice) {
            Connection conn        = null;
            PreparedStatement stmt = null;
            ResultSet rs           = null;

            int n = -1;

            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);
                
                String query = sql[SQL_COUNT_DEVICES];
                if ( (clause != null) && (! (clause instanceof AllClause))) {
                    PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                    if (where.sql.length() > 0) {
                        query += " where " + where.sql;
                    }
                }
                stmt = conn.prepareStatement(query);
                if ( (clause != null) && (! (clause instanceof AllClause))) {
                    PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                    for (int i = 0; i < where.parameters.length; ++i) {
                        stmt.setObject(i + 1, where.parameters[i]);
                    }
                }
                rs = stmt.executeQuery();

                while (rs.next()) {
                    n = rs.getInt(1);
                }
                return n;
            } catch (SQLException e) {
                throw new PersistentStoreException("Error counting devices ", e);
            } catch (Exception e) {
                throw new PersistentStoreException("Error counting devices ", e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
        }
        return -1;

    }

    /**
     * Read all informations
     *
     * @param clause condition where for select
     * @return Object[] array of object
     */
    public Sync4jDevice[] read(Clause clause) throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        ArrayList ret = new ArrayList();

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            String query = sql[SQL_SELECT_ALL_DEVICES];

            if ((clause != null) && (!(clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if (where.sql.length() > 0) {
                    query += " where " + where.sql;
                }
            }
            stmt = conn.prepareStatement(query);
            if ((clause != null) && (!(clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                for (int i = 0; i < where.parameters.length; ++i) {
                    stmt.setObject(i+1, where.parameters[i]);
                }
            }
            rs = stmt.executeQuery();

            Sync4jDevice d = null;

            while (rs.next()) {
                d = new Sync4jDevice();
                d.setDeviceId(trimEnd(rs.getString(1)));
                d.setDescription(trimEnd(rs.getString(2)));
                d.setType(trimEnd(rs.getString(3)));

                String value = trimEnd(rs.getString(4));
                if (value == null) {
                    d.setClientNonce(null);
                } else if (value.equals("")) {
                    d.setClientNonce(new byte[0]);
                } else {
                    d.setClientNonce(Base64.decode(value.getBytes()));
                }

                value = trimEnd(rs.getString(5));
                if (value == null) {
                    d.setServerNonce(null);
                } else if (value.equals("")) {
                    d.setServerNonce(new byte[0]);
                } else {
                    d.setServerNonce(Base64.decode(value.getBytes()));
                }
                d.setServerPassword   (trimEnd(rs.getString(6)));
                d.setTimeZone         (trimEnd(rs.getString(7)));
                d.setConvertDatePolicy(getConvertDatePolicyFromDBValue(rs.getString(8)));
                d.setCharset          (trimEnd(rs.getString(9)));

                String capsId = trimEnd(rs.getString(10));
                if (capsId != null) {
                    Capabilities caps = new Capabilities();
                    caps.setId(new Long(capsId));
                    d.setCapabilities(caps);
                } else {
                    d.setCapabilities(null);
                }
                d.setAddress            (trimEnd(rs.getString(11)));
                d.setMsisdn             (trimEnd(rs.getString(12)));
                d.setNotificationBuilder(trimEnd(rs.getString(13)));
                d.setNotificationSender (trimEnd(rs.getString(14)));

                d.setSentServerCaps(rs.getBoolean(15));

                ret.add(d);
            }
            Sync4jDevice[] array = new Sync4jDevice[ret.size()];
            for (int i = 0; i < ret.size(); i++) {
                array[i] = (Sync4jDevice) ret.get(i);
            }
            return array;
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading devices", e);
        } catch (Exception e) {
            throw new PersistentStoreException("Error reading devices", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }

    }


    /**
     * Select the number of devices that satisfy the conditions specified in input.
     *
     * @param clause The additional where clause.
     * @return int number of devices
     * @throws PersistentStoreException if an error occurs
     */
    public int count(Clause clause) throws PersistentStoreException {
        Connection conn        = null;
        PreparedStatement stmt = null;
        ResultSet rs           = null;
        int n                  = -1;

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);
            
            String query = sql[SQL_COUNT_DEVICES];
            if ( (clause != null) && (! (clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if (where.sql.length() > 0) {
                    query += " where " + where.sql;
                }
            }
            stmt = conn.prepareStatement(query);
            if ( (clause != null) && (! (clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                for (int i = 0; i < where.parameters.length; ++i) {
                    stmt.setObject(i + 1, where.parameters[i]);
                }
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                n = rs.getInt(1);
            }
            return n;
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading count devices ", e);
        } catch (Exception e) {
            throw new PersistentStoreException("Error reading count devices ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }

    }

    //---------------------------------------------------------- Private Methods

    /**
     * Removes the trailing whitespace of a string
     *
     * @param s the string to be processed
     * @return a copy of the string without the trailing whitespace
     */
    private String trimEnd(String s) {
        if(s != null) {
        int i=s.length()-1;
            for(i=s.length()-1; i>=0; i--) {
                if (s.charAt(i) != ' ') {
                break;
            }
        }
        return s.substring(0,i+1);
        } else {
            return null;
        }
    }

    /**
     * Returns the value that must be inserted in the DB for the convert date
     * property according to the given short
     * @param convertDate the convertDate short value
     * @return the value that must be inserted in the DB for the convert date
     * property according to the given short
     */
    private String getDBValueForConvertDatePolicy(short convertDate) {
        switch (convertDate) {
            case ConvertDatePolicy.CONVERT_DATE:
                return "Y";
            case ConvertDatePolicy.NO_CONVERT_DATE:
                return "N";
            default:
                return "";
        }
    }

    /**
     * Returns the short value for the convert date property according to the given
     * dbValue
     * @param dbValue the value in the db
     * @return the short value for the convert date property according to the given
     * dbValue
     */
    private short getConvertDatePolicyFromDBValue(String dbValue) {
        if ("Y".equalsIgnoreCase(dbValue)) {
            return ConvertDatePolicy.CONVERT_DATE;
        } else if ("N".equalsIgnoreCase(dbValue)) {
            return ConvertDatePolicy.NO_CONVERT_DATE;
        } else {
            return ConvertDatePolicy.UNSPECIFIED;
        }

    }
}

