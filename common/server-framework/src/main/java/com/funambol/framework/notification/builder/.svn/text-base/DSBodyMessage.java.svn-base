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
package com.funambol.framework.notification.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.funambol.framework.core.Alert;
import com.funambol.framework.core.Item;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.Sync4jLoggerName;

import com.funambol.framework.notification.NotificationException;


/**
 * This class is used to generate the body of a DS notification message
 *
 * @version  $Id: DSBodyMessage.java,v 1.2 2007-11-28 11:15:50 nichele Exp $
 */
public class DSBodyMessage implements BodyMessage {

    // --------------------------------------------------------------- Constants
    private static final int NUM_BIT_NUMBER_SYNCS = 4 ;
    private static final int NUM_BIT_FUTURE_USE   = 4 ;
    private static final int NUM_BIT_ALERT_DATA   = 4 ;
    private static final int NUM_BIT_CONTENT_TYPE = 24;
    private static final int NUM_BIT_URI_LENGTH   = 8 ;

    private static final int CT_ID_TEXT_PLAIN      = 3;
    private static final int CT_ID_TEXT_XVCALENDAR = 6;
    private static final int CT_ID_TEXT_XVCARD     = 7;
    private static final int CT_ID_OMADS_EMAIL     = 0x0306;

    private static final String CT_DESCR_TEXT_PLAIN      = "text/plain"      ;
    private static final String CT_DESCR_TEXT_XVCALENDAR = "text/x-vcalendar";
    private static final String CT_DESCR_TEXT_XVCARD     = "text/x-vcard"    ;
    private static final String CT_DESCR_OMADS_EMAIL     = "application/vnd.omads-email+xml";

    // ------------------------------------------------------------ Private data
    private static final Map<String, Integer> contentTypeCodes =
        new HashMap<String, Integer>();

    static {
        contentTypeCodes.put(CT_DESCR_TEXT_PLAIN     , CT_ID_TEXT_PLAIN);
        contentTypeCodes.put(CT_DESCR_TEXT_XVCALENDAR, CT_ID_TEXT_XVCALENDAR);
        contentTypeCodes.put(CT_DESCR_TEXT_XVCARD    , CT_ID_TEXT_XVCARD);
        contentTypeCodes.put(CT_DESCR_OMADS_EMAIL    , CT_ID_OMADS_EMAIL);
    }

    private FunambolLogger log    = null;
    private Alert[]        alerts = null;


    // ------------------------------------------------------------- Constructor
    public DSBodyMessage(Alert[] alerts) {
        log = FunambolLoggerFactory.getLogger(Sync4jLoggerName.SERVER_NOTIFICATION);
        this.alerts = alerts;
    }

    // ---------------------------------------------------------- Public Methods
    /**
     * Gets the array of alerts of the message body
     *
     * @return array of alerts
     */
    public Alert[] getAlerts() {
        return this.alerts;
    }

    /**
     * Sets the array of the alerts of the message body
     *
     * @param alerts
     */
    public void setAlerts(Alert[] alerts) {
        this.alerts = alerts;
    }

    /**
     * Transforms the alerts into the byte array that is send to the client
     *
     * @return byte representation of the alerts
     * @throws NotificationException
     */
    public byte[] getBytes() throws NotificationException {
        if(this.alerts == null) {
            throw new NotificationException("No alerts to build the message body");
        }

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BitOutputStream       bout    = new BitOutputStream(byteOut);
        try {
            //get the number of syncs in the body
            int syncNr = 0;
            for (int i = 0; i < this.alerts.length; i++) {
                ArrayList items = this.alerts[i].getItems();
                for (int j = 0; j < items.size(); j++) {
                    syncNr++;
                }
            }
            if (log.isTraceEnabled()) {
                log.trace("Number of sync items:" + syncNr);
            }
            int[] intRepresentation = getIntBinaryRepresentation(syncNr, NUM_BIT_NUMBER_SYNCS);
            for (int i = 0; i < NUM_BIT_NUMBER_SYNCS; i++) {
                bout.writeBit(intRepresentation[i]);
            }

            //put 0000 in the body - future use
            for (int i = 0; i < NUM_BIT_FUTURE_USE; i++) {
                bout.writeBit(0);
            }

            //for each sync info from each alert
            for (int i = 0; i < this.alerts.length; i++) {
                Alert alert = this.alerts[i];
                ArrayList items = this.alerts[i].getItems();
                for (int j = 0; j < items.size(); j++) {
                    Item item = (Item) items.get(j);
                    //put in the body syncType -200
                    if ((alert.getData()<206) || (alert.getData()>210)) {
                        throw new NotificationException("Invalid alert data type "+alert.getData());
                    }
                    intRepresentation = null;
                    intRepresentation = getIntBinaryRepresentation(alert.getData() - 200, NUM_BIT_ALERT_DATA);
                    for (int k = 0; k < NUM_BIT_ALERT_DATA; k++) {
                        bout.writeBit(intRepresentation[k]);
                    }
                    //put 0000 -future use
                    for (int k = 0; k < NUM_BIT_FUTURE_USE; k++) {
                        bout.writeBit(0);
                    }
                    //content type - map between string and value !!!!!
                    int contentTypeCode = 0;
                    if ((item.getMeta() != null) && (item.getMeta().getType() != null)) {
                        String type = item.getMeta().getType();
                        Integer tmp = contentTypeCodes.get(type);
                        if (tmp != null) {
                            contentTypeCode = tmp;
                        }
                    }
                    intRepresentation = null;
                    intRepresentation = getIntBinaryRepresentation(contentTypeCode, NUM_BIT_CONTENT_TYPE);
                    for (int k = 0; k < NUM_BIT_CONTENT_TYPE; k++) {
                        bout.writeBit(intRepresentation[k]);
                    }

                    //
                    // SyncSource: length + uri
                    //
                    String syncSourceURI = null;

                    if (item.getSource() != null) {
                        //
                        // Trying to use item' source
                        //
                        syncSourceURI = item.getSource().getLocURI();
                    } else if (item.getTarget() != null) {
                        //
                        // Trying to use item' target
                        //
                        syncSourceURI = item.getTarget().getLocURI();
                    } else {
                        throw new NotificationException("The Alert command n. " + i + " doesn't contain an item with source or target uri");
                    }

                    if (log.isTraceEnabled()) {
                        log.trace("SourceURI: " + syncSourceURI);
                    }

                    // syncsource uri length
                    intRepresentation = null;
                    intRepresentation = getIntBinaryRepresentation(syncSourceURI.length(), NUM_BIT_URI_LENGTH);
                    for (int k = 0; k < NUM_BIT_URI_LENGTH; k++) {
                        bout.writeBit(intRepresentation[k]);
                    }

                    //syncsource uri
                    byte[] uri = syncSourceURI.getBytes();
                    for (int k = 0; k < uri.length; k++) {
                        //transform each byte of the server uri string into an 8 bit number
                        intRepresentation = getIntBinaryRepresentation(uri[k], 8);
                        //write the bit representation of each byte of the uri in the bit output stream
                        for (int jj = 0; jj < 8; jj++) {
                            bout.writeBit(intRepresentation[jj]);
                        }
                    }
                }
            }
            bout.close();
        } catch (IOException e) {
            throw new NotificationException("Error during body computing", e);
        }

        return byteOut.toByteArray();
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Convert a string as 1010011 into int array with '0' or '1' element
     *
     * @param binaryRepresentation the binary representation string
     * @return int[] the int array with '0' or '1' element
     */
    private static int[] converteBinaryRepresentation(String binaryRepresentation) {
        char[] c = binaryRepresentation.toCharArray();
        int[] intRepresentation = new int[c.length];

        for (int i = 0; i < c.length; i++) {
            intRepresentation[i] = Integer.parseInt(String.valueOf(c[i]));
        }
        return intRepresentation;
    }

    /**
     * Given a integer, returns a int array with '0' or '1' elements correspondent to
     * the binary representation of the integer. If the binary representation is smaller
     * that n bit then the binary representation is padding
     *
     * @param value the integer value
     * @param n     the length of the binary representation
     * @return int[] the binary representation
     */
    private int[] getIntBinaryRepresentation(int value, int n) {
        String sBinaryValue = Integer.toBinaryString(value);
        sBinaryValue = paddingString(sBinaryValue, n, '0', true);

        int[] intRepresentation = converteBinaryRepresentation(sBinaryValue);

        return intRepresentation;
    }

    /**
     * Pad a string S with a size of N with char C on the left (True) or on the
     * right(False)
     *
     * @param s           the string to paddind
     * @param n           int the size
     * @param c           char the char used to padding
     * @param paddingLeft indicates if padding on the left (true) or on the
     *                    right (false)
     * @return the string after padding
     */
    private static String paddingString(String s, int n, char c, boolean paddingLeft) {
        StringBuffer str = new StringBuffer(s);
        int strLength = str.length();
        if (n > 0 && n > strLength) {
            for (int i = 0; i <= n; i++) {
                if (paddingLeft) {
                    if (i < n - strLength) {
                        str.insert(0, c);
                    }
                } else {
                    if (i > strLength) {
                        str.append(c);
                    }
                }
            }
        }
        return str.toString();
    }

}
