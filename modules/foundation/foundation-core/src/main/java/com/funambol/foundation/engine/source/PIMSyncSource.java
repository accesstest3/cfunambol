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
package com.funambol.foundation.engine.source;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.items.manager.PIMEntityManager;
import com.funambol.foundation.util.Def;

import com.funambol.framework.core.AlertCode;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.AbstractSyncSource;
import com.funambol.framework.engine.source.MergeableSyncSource;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ConvertDatePolicy;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.tools.beans.LazyInitBean;


/**
 *
 * @version $Id: PIMSyncSource.java,v 1.1.1.1 2008-03-20 21:38:35 stefano_fornari Exp $
 */
public abstract class PIMSyncSource
extends AbstractSyncSource
implements MergeableSyncSource, Serializable, LazyInitBean {

    //---------------------------------------------------------------- Constants

    public static final int SIFC = 0; // To be used as index for SIF-Contact
    public static final int SIFE = 1; // To be used as index for SIF-Event
    public static final int SIFN = 2; // To be used as index for SIF-Note
    public static final int SIFT = 3; // To be used as index for SIF-Task
    public static final int VCARD = 4; // To be used as index for vCard
    public static final int VCAL = 5; // To be used as index for vCalendar
    public static final int ICAL = 6; // To be used as index for iCalendar
    public static final int VNOTE = 7; // To be used as index for vNote
    public static final int PLAINTEXT = 8; // To be used as index for plain/text notes
    public static final String[] TYPE = {
        "text/x-s4j-sifc",               // SIF-Contact
        "text/x-s4j-sife",               // SIF-Event
        "text/x-s4j-sifn",               // SIF-Note
        "text/x-s4j-sift",               // SIF-Task
        "text/x-vcard",                  // vCard
        "text/x-vcalendar",              // vCalendar (1.0)
        "text/calendar",                 // iCalendar (vCalendar 2.0)
        "text/x-vnote",                  // vNote
        "text/plain",                    // plain-text note
    };

    //----------------------------------------------------------- Protected data

    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    protected PIMEntityManager manager;
    protected Sync4jPrincipal principal;
    protected String userId;
    protected int syncMode;
    protected Timestamp lastSyncTime; // n-th synchronization
    protected Timestamp previousSyncTime; // (n - 1)-th synchronization
    protected TimeZone deviceTimeZone = null;
    protected String deviceTimeZoneDescription = null;
    protected String deviceCharset = null;
    protected boolean convertDateToLocal = false;
    protected boolean pushBackUncheckableItems = true;

    //----------------------------------------------------------- Public methods

    @Override
    public void beginSync(SyncContext context) {

        if (log.isTraceEnabled()) {
            log.trace("PIMSyncSource beginSync start");
        }

        principal = context.getPrincipal();
        userId = principal.getUsername();
        syncMode = context.getSyncMode();

        Sync4jDevice device = context.getPrincipal().getDevice();

        handleConvertDate(device);

        deviceCharset = device.getCharset();

        if (log.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder("Beginning sync with:");
            sb.append("\n> syncMode            : ").append(syncMode);
            sb.append("\n> principal           : ").append(principal);
            sb.append("\n> sourceQuery         : ").append(context.getSourceQuery());
            sb.append("\n> deviceTimeZoneDescr.: ").append(deviceTimeZoneDescription);
            sb.append("\n> deviceTimeZone      : ").append(deviceTimeZone);
            sb.append("\n> charset             : ").append(deviceCharset);

            log.trace(sb.toString());
        }

        if (syncMode == AlertCode.REFRESH_FROM_CLIENT) {
            if (log.isTraceEnabled()) {
                log.trace("Performing REFRESH_FROM_CLIENT (203)");
            }
            removeAllSyncItems(null);
        }

        log.trace("PIMSyncSource beginSync end");
    }

    @Override
    public void endSync() {
    }

    /**
     * Gets the status of the SyncItem with the given key.
     *
     * @param syncItemKey as a SyncItemKey object
     * @throws SyncSourceException
     * @return the status as a char
     */
    public char getSyncItemStateFromId(SyncItemKey syncItemKey)
            throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("PIMSyncSource getSyncItemStateFromId begin");
        }

        String id = "N/A"; // default value for error tracking

        try {

            // Slow sync
            // @todo Implement, depending on a syncMode check

            // Fast sync
            id = syncItemKey.getKeyAsString();
            char itemRawState = manager.getItemState(id, previousSyncTime);

            if (log.isTraceEnabled()) {
                log.trace("PIMSyncSource getSyncItemStateFromId end");
            }

            if (itemRawState == Def.PIM_STATE_UNCHANGED) {
                return SyncItemState.SYNCHRONIZED;
            } else {
                return itemRawState; // Def uses SyncItemState.* as constant
            // values for N, D and U states
            }
        } catch (EntityException ee) {
            throw new SyncSourceException("Error getting the state of SyncItem " + "with ID " + id, ee);
        }

    }

    /**
     * Not yet used. It just produces a log message.
     */
    public void setOperationStatus(String operation, int statusCode,
            SyncItemKey[] keys) {

        if (log.isTraceEnabled()) {
            StringBuilder message = new StringBuilder("Received status code '");
            message.append(statusCode).append("' for a '").append(operation).append("' command for the following items: ");

            for (int i = 0; i < keys.length; i++) {
                message.append("\n> ").append(keys[i].getKeyAsString());
            }

            log.trace(message.toString());
        }
    }

    //---------------------------------------------------------- Private methods
    /**
     * Extracts the content from a syncItem.
     *
     * @param syncItem
     * @return as a String object
     */
    protected String getContentFromSyncItem(SyncItem syncItem) {

        byte[] itemContent = syncItem.getContent();

        // Add content processing here, if needed

        return new String(itemContent == null ? new byte[0] : itemContent);
    }

    protected void saveSyncTiming(Timestamp since, Timestamp to) {

        if (log.isTraceEnabled()) {
            if (!since.equals(previousSyncTime)) {
                log.trace("PIMSyncSource sync timing updated to " + since + " / " + to);
            }
        }
        this.previousSyncTime = since;
        this.lastSyncTime = to;
    }

    protected void removeAllSyncItems(Timestamp t) {

        if (log.isTraceEnabled()) {
            log.trace("Perform REFRESH_FROM_CLIENT (203) for user " + userId);
        }

        try {
            manager.removeAllItems(t);
        } catch (EntityException ee) {

            if (log.isTraceEnabled()) {
                log.trace("Error while performing REFRESH_FROM_CLIENT (203).",
                        ee);
            }
        }
    }

    /**
     * Sets deviceTimeZone, deviceTimeZoneDescription and convertDateToLocal
     * according to the device properties and to the convertDate value following
     * these roles:
     * <ul>
     * <li>if device.getConvertDate is equal to Sync4jDevice.CONVERT_DATE, and the
     * timezone is not null, the dates are converted</li>
     * <li>if device.getConvertDate is equal to Sync4jDevice.UNSPECIFIED</li>,
     * the device doesn't support UTC (from its capabilities), and the
     * timezone is not null, the dates are converted</li>
     * <li>otherwise no conversion is performed</li>
     * </ul>
     * @param device the device
     */
    protected void handleConvertDate(Sync4jDevice device) {

        String timezone = device.getTimeZone();

        deviceTimeZoneDescription = null;
        deviceTimeZone = null;

        if (device.getConvertDatePolicy() == ConvertDatePolicy.CONVERT_DATE) {
            if (timezone != null && timezone.length() > 0) {
                deviceTimeZoneDescription = timezone;
                deviceTimeZone = TimeZone.getTimeZone(deviceTimeZoneDescription);
            }

            convertDateToLocal = true;

        } else if (device.getConvertDatePolicy() == ConvertDatePolicy.NO_CONVERT_DATE) {

            if (timezone != null && timezone.length() > 0) {
                deviceTimeZoneDescription = timezone;
                deviceTimeZone = TimeZone.getTimeZone(deviceTimeZoneDescription);
            }
            convertDateToLocal = false;

        } else if (device.getConvertDatePolicy() == ConvertDatePolicy.UNSPECIFIED) {
            if (timezone != null && timezone.length() > 0) {
                //
                // Checking the device capabilities...if the device doesn't support
                // UTC, we will convert the dates
                //
                boolean supportUTC = false;
                if (device.getCapabilities() != null) {
                    if (device.getCapabilities().getDevInf() != null) {
                        if (device.getCapabilities().getDevInf().getUTC() == null) {
                            supportUTC = false;

                            convertDateToLocal = true;

                        } else {
                            supportUTC =
                                    device.getCapabilities().getDevInf().getUTC().booleanValue();

                            convertDateToLocal = supportUTC ? false : true;
                        }
                    }
                }

                if (log.isTraceEnabled()) {
                    log.trace("Device '" + device.getDeviceId() + "' support UTC");
                }
                if (!supportUTC) {
                    deviceTimeZoneDescription = timezone;
                    deviceTimeZone = TimeZone.getTimeZone(deviceTimeZoneDescription);
                }
            }
        }
    }

    /**
     * Parses the given query string returning a map with all the specified
     * parameters
     * @param queryString the query string
     * @return a map with all the specified parameters.
     */
    protected Map getSourceQueryParameters(String queryString) {
        Map parameters = new HashMap();
        if (queryString == null || queryString.length() == 0) {
            return parameters;
        }

        String[] couples = null;
        if (queryString != null && queryString.length() != 0) {
            couples = queryString.split("&");
        }
        for (String couple : couples) {
            String[] values = null;
            String value = null;

            values = couple.split("=");
            if (values != null && values.length > 0) {
                if (values[0] == null || values[0].length() == 0) {
                    continue;
                }
                if (values.length == 2) {
                    try {
                        value = URLDecoder.decode(values[1], "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        //
                        // The value is wrong. We continue with the sync ignoring this parameter
                        //
                        if (log.isTraceEnabled()) {
                            log.trace("Error decoding the query string. Wrong value '" + values[1] +
                                    "' for parameter '" + values[0] + "'. It will be ignored");
                        }
                    }
                }
                parameters.put(values[0], value);
            }
        }
        return parameters;
    }
}
