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

package com.funambol.framework.server;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents a Sync4j device.
 *
 * @version $Id: Sync4jDevice.java,v 1.3 2007-11-28 11:15:38 nichele Exp $
 */
public class Sync4jDevice implements Serializable {

    // --------------------------------------------------------------- Constants
    private static final String DEFAULT_CHARSET = "UTF-8";

    // ------------------------------------------------------------ Private data

    /**
     * The device id
     */
    private String deviceId;

    /**
     * Getter for property deviceId.
     *
     * @return Value of property deviceId.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Setter for property deviceId.
     *
     * @param deviceId New value of property deviceId.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * The device description
     */
    private String description;

    /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * The type
     */
    private String type;

    /**
     * Getter for property type.
     *
     * @return Value of property type.
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for property type.
     *
     * @param type New value of property type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * The client nonce
     */
    private byte[] clientNonce;

    /**
     * Gets the client_nonce property
     *
     * @return clientNonce the nonce that the server uses in order to authenticate
     *                     itself with the client
     */
    public byte[] getClientNonce() {
        if (this.clientNonce == null) {
            this.clientNonce = new byte[0];
        }
        return this.clientNonce;
    }

    /**
     * Sets the client_nonce property
     *
     * @param clientNonce the nonce that the server uses in order to authenticate
     *                    iteself with the client
     */
    public void setClientNonce(byte[] clientNonce) {
        if (clientNonce == null) {
            clientNonce = new byte[0];
        }
        this.clientNonce = clientNonce;
    }

    /**
     * The server nonce
     */
    private byte[] serverNonce;

    /**
     * Gets the server_nonce property
     *
     * @return serverNonce the nonce that the client uses in order to authenticate
     *                     iteself with the server
     */
    public byte[] getServerNonce() {
        if (this.serverNonce == null) {
            this.serverNonce = new byte[0];
        }
        return this.serverNonce;
    }

    /**
     * Sets the server_nonce property
     *
     * @param serverNonce the nonce that the client uses in order to authenticate
     *                    iteself with the server
     */
    public void setServerNonce(byte[] serverNonce) {
        if (serverNonce == null) {
            serverNonce = new byte[0];
        }
        this.serverNonce = serverNonce;
    }

    /**
     * The server password
     */
    private String serverPassword;

    /**
     * Gets the server password property
     *
     * @return serverPassword the server password for this device
     */
    public String getServerPassword() {
        return this.serverPassword;
    }

    /**
     * Sets the server password property
     *
     * @param serverPassword the server password for this device
     */
    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    /**
     * The device timezone
     */
    private String timeZone;

    /**
     * Sets the client device timeZone
     *
     * @param tz the client device timeZone
     */
    public void setTimeZone(String tz) {
        this.timeZone = tz;
    }

    /**
     * Gets the client device timeZone
     *
     * @return the client device timeZone
     */
    public String getTimeZone() {
        return timeZone;
    }

    /**
     * Convert date policy property. One between CONVERT_DATE, NO_CONVERT_DATE, UNSPECIFIED
     */
    private short  convertDatePolicy;

    /**
     * Sets the convertDatePolicy
     *
     * @param convertDatePolicy the convert date policy
     */
    public void setConvertDatePolicy(short convertDatePolicy) {
        switch (convertDatePolicy) {
            case ConvertDatePolicy.CONVERT_DATE:
                this.convertDatePolicy = ConvertDatePolicy.CONVERT_DATE;
                break;
            case ConvertDatePolicy.NO_CONVERT_DATE:
                this.convertDatePolicy = ConvertDatePolicy.NO_CONVERT_DATE;
                break;
            default:
                this.convertDatePolicy = ConvertDatePolicy.UNSPECIFIED;
        }
    }

    /**
     * Gets the convertDatePolicy
     *
     * @return the convertDatePolicy
     */
    public short getConvertDatePolicy() {
        return convertDatePolicy;
    }

    /**
     * Sets the convertDate
     *
     * @param convertDate the convert date
     * 
     * @deprecated Since v65, use convertDatePolicy
     */
    public void setConvertDate(boolean convertDate) {
        if (convertDate) {
            this.convertDatePolicy = ConvertDatePolicy.CONVERT_DATE;
        } else {
            this.convertDatePolicy = ConvertDatePolicy.NO_CONVERT_DATE;
        }
    }

    /**
     * Gets the convertDate
     *
     * @return the convertDate
     * @deprecated Since v65, use convertDatePolicy
     */
    public boolean getConvertDate() {
        if (convertDatePolicy == ConvertDatePolicy.CONVERT_DATE) {
            return true;
        }
        //
        // This is not really right, because it returns false also if the
        // policy is UNSPECIFIED but there are not other ways
        //
        return false;
    }

    /**
     * Gets a description for the current convertDatePolicy value
     *
     * @return a description for the current convertDatePolicy value
     */
    public String getConvertDatePolicyDescription() {
        switch (convertDatePolicy) {
            case ConvertDatePolicy.CONVERT_DATE:
                return "CONVERT_DATE";
            case ConvertDatePolicy.NO_CONVERT_DATE:
                return "NO_CONVERT_DATE";
            default:
                return "UNSPECIFIED";
        }
    }

    /**
     * Device capabilities
     */
    private Capabilities capabilities ;

    /**
     * Sets the client device capabilities
     *
     * @param capabilities the client device capabilities
     */
    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * Gets the client device capabilities
     *
     * @return the client device capabilities
     */
    public Capabilities getCapabilities() {
        return capabilities;
    }

    /**
     * The device charset
     */
    private String charset;

    /**
     * Sets the charset
     *
     * @param charset the charset
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * Gets the charset
     *
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * The device address
     */
    private String address;

    /**
     * Sets the last known address for the device
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the last known address for the device.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * The device msisdn
     */
    private String msisdn;

    /**
     * Gets the last known MSISDN of the device
     * @return last known MSISDN of the device
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the last known MSISDN of the device
     * @param msisdn last known MSISDN of the device
     */
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    /**
     * The notification builder
     */
    private String notificationBuilder;

    /**
     * Sets the path of the configuration bean of the notification builder
     *
     * @param notificationBuilder the path of the notification builder
     */
    public void setNotificationBuilder(String notificationBuilder) {
        this.notificationBuilder = notificationBuilder;
    }

    /**
     * Gets the path of the configuration bean of the notification builder
     *
     * @return the path of the configuration bean of the notification builder
     */
    public String getNotificationBuilder() {
        return notificationBuilder;
    }

    /**
     * The notification sender
     */
    private String notificationSender ;


    /**
     * Sets the path of the configuration bean of the notification sender
     *
     * @param notificationSender the path of the configuration bean of the notification sender
     */
    public void setNotificationSender(String notificationSender) {
        this.notificationSender = notificationSender;
    }

    /**
     * Gets the path of the configuration bean of the notification sender
     *
     * @return the path of the configuration bean of the notification sender
     */
    public String getNotificationSender() {
        return notificationSender;
    }

    // Has the server caps already been sent to the client?
    private boolean sentServerCaps = false;
    public void setSentServerCaps(boolean sentServerCaps) {
        this.sentServerCaps = sentServerCaps;
    }

    public boolean isSentServerCaps() {
        return this.sentServerCaps;
    }

    // ------------------------------------------------------------- Costructors
    /**
     * Creates a new instance of Sync4jDevice
     */
    public Sync4jDevice() {
        this(null, null, null, null, null, null, null, ConvertDatePolicy.UNSPECIFIED, null);
    }

    /**
     * Creates a new instance of Sync4jDevice.
     *
     * @param deviceId the device identification
     */
    public Sync4jDevice(final String deviceId) {
        this(deviceId, null, null, null, null, null, null, ConvertDatePolicy.UNSPECIFIED, null);
    }

    /**
     * Creates a new instance of Sync4jDevice.
     *
     * @param deviceId the device identification
     * @param description the device description
     * @param type the device type
     *
     */
    public Sync4jDevice(final String deviceId   ,
                        final String description,
                        final String type       ) {
        this(deviceId, description, type, null, null, null, null, ConvertDatePolicy.UNSPECIFIED, null);

    }

    /**
     * Creates a new instance of Sync4jDevice.
     *
     * @param deviceId the device identification
     * @param description the device description
     * @param type the device type
     * @param clientNonce the next nonce sent by server for MD5 authentication
     * @param serverNonce the next nonce sent by client for MD5 authentication
     */
    public Sync4jDevice(final String deviceId   ,
                        final String description,
                        final String type       ,
                        final byte[] clientNonce ,
                        final byte[] serverNonce) {
        this(deviceId,
             description,
             type,
             clientNonce,
             serverNonce,
             null,
             null,
             ConvertDatePolicy.UNSPECIFIED,
             null);

    }

    /**
     * Creates a new instance of Sync4jDevice.
     *
     * @param deviceId          the device identification
     * @param description       the device description
     * @param type              the device type
     * @param clientNonce       the next nonce sent by server for MD5 authentication
     * @param serverNonce       the next nonce sent by client for MD5 authentication
     * @param serverPassword    the server password for this device
     * @param timeZone          the timezone for this device
     * @param convertDatePolicy must the date be converted in the device timezone ?
     * @param charset           the device charset
     */
    public Sync4jDevice(final String  deviceId   ,
                        final String  description,
                        final String  type       ,
                        final byte[]  clientNonce,
                        final byte[]  serverNonce,
                        final String  serverPassword,
                        final String  timeZone,
                        final short   convertDatePolicy,
                        final String  charset) {

        this.deviceId       = deviceId;
        this.description    = description;
        this.type           = type;
        this.clientNonce    = clientNonce;
        this.serverNonce    = serverNonce;
        this.serverPassword = serverPassword;
        this.timeZone       = timeZone;

        setConvertDatePolicy(convertDatePolicy);

        if (charset == null) {
            this.charset    = DEFAULT_CHARSET;
        } else {
            this.charset    = charset;
        }

        //
        // Default capabilities...
        //
        this.capabilities = new Capabilities();
    }

    /**
     * Creates a new instance of Sync4jDevice.
     *
     * @param deviceId the device identification
     * @param description the device description
     * @param type the device type
     * @param clientNonce    the next nonce sent by server for MD5 authentication
     * @param serverNonce    the next nonce sent by client for MD5 authentication
     * @param serverPassword the server password for this device
     * @param timeZone       the timezone for this device
     * @param convertDate    must the date be converted in the device timezone ?
     * @param charset        the device charset
     *
     * @deprecated Since v65, use the one with short convertDate
     */
    public Sync4jDevice(final String  deviceId   ,
                        final String  description,
                        final String  type       ,
                        final byte[]  clientNonce,
                        final byte[]  serverNonce,
                        final String  serverPassword,
                        final String  timeZone,
                        final boolean convertDate,
                        final String  charset) {

        this.deviceId       = deviceId;
        this.description    = description;
        this.type           = type;
        this.clientNonce    = clientNonce;
        this.serverNonce    = serverNonce;
        this.serverPassword = serverPassword;
        this.timeZone       = timeZone;

        if (convertDate) {
            this.convertDatePolicy = ConvertDatePolicy.CONVERT_DATE;
        } else {
            this.convertDatePolicy = ConvertDatePolicy.NO_CONVERT_DATE;
        }

        if (charset == null) {
            this.charset    = DEFAULT_CHARSET;
        } else {
            this.charset    = charset;
        }

        //
        // Default capabilities...
        //
        this.capabilities = new Capabilities();
    }

    /**
     * Creates a new instance of Sync4jDevice.
     *
     * @param deviceId          the device identification
     * @param description       the device description
     * @param type              the device type
     * @param clientNonce       the next nonce sent by server for MD5 authentication
     * @param serverNonce       the next nonce sent by client for MD5 authentication
     * @param serverPassword    the server password for this device
     * @param timeZone          the timezone for this device
     * @param convertDatePolicy must the date be converted in the device timezone ?
     * @param charset           the device charset
     * @param sentServerCaps has been the server caps already sent to the client?
     */
    public Sync4jDevice(final String  deviceId   ,
                        final String  description,
                        final String  type       ,
                        final byte[]  clientNonce,
                        final byte[]  serverNonce,
                        final String  serverPassword,
                        final String  timeZone,
                        final short   convertDatePolicy,
                        final String  charset,
                        final boolean sentServerCaps) {

        this.deviceId       = deviceId;
        this.description    = description;
        this.type           = type;
        this.clientNonce    = clientNonce;
        this.serverNonce    = serverNonce;
        this.serverPassword = serverPassword;
        this.timeZone       = timeZone;

        setConvertDatePolicy(convertDatePolicy);

        if (charset == null) {
            this.charset    = DEFAULT_CHARSET;
        } else {
            this.charset    = charset;
        }

        //
        // Default capabilities...
        //
        this.capabilities = new Capabilities();
        
        this.setSentServerCaps(sentServerCaps);
    }

    // ---------------------------------------------------------- Public methods
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);

         sb.append("deviceId",         deviceId);
         sb.append("description",      description);
         sb.append("type",             type);
         if (clientNonce != null) {
             sb.append("client_nonce", new String(clientNonce));
         }
         if (serverNonce != null) {
             sb.append("server_nonce", new String(serverNonce));
         }
         sb.append("serverPassword",      serverPassword);
         sb.append("timeZone",            timeZone);
         sb.append("convertDatePolicy",   getConvertDatePolicyDescription());
         sb.append("charset",             charset);
         sb.append("address",             address);
         sb.append("msisdn",              msisdn);
         sb.append("notificationBuilder", notificationBuilder);
         sb.append("notificationSender" , notificationSender);
         sb.append("sentServerCaps"     , sentServerCaps);
         return sb.toString();
    }

}
