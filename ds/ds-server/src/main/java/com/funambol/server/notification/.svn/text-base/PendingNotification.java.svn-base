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

package com.funambol.server.notification;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a not delivered push notification message.
 *
 * @version $Id$
 */
public class PendingNotification {

    // ------------------------------------------------------------ Private data
    private long   id           = -1;
    private String userId       = null; // username
    private String deviceId     = null;
    private String syncSource   = null; // sync source uri
    private String contentType  = null; // content type of the sync source
    private int    syncType     = -1;
    private int    uimode       =  0; // user interaction mode
    private long   deliveryTime = -1; // timestamp of when the server has tried
                                      // to deliver the push notification
                                      // message last time

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new instance of PendingNotification
     */
    public PendingNotification() {
    }

    /**
     * Creates a new instance of PendingNotification given all information
     * about the pending notification.
     */
    public PendingNotification(long   id          ,
                               String userId      ,
                               String deviceId    ,
                               String syncSource  ,
                               String contentType ,
                               int    syncType    ,
                               int    uimode      ,
                               long   deliveryTime) {

        this.id           = id;
        this.userId       = userId;
        this.deviceId     = deviceId;
        this.syncSource   = syncSource;
        this.contentType  = contentType;
        this.syncType     = syncType;
        this.uimode       = uimode;
        this.deliveryTime = deliveryTime;
    }

    // ---------------------------------------------------------- Public methods

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSyncSource() {
        return syncSource;
    }

    public void setSyncSource(String syncSource) {
        this.syncSource = syncSource;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getSyncType() {
        return syncType;
    }

    public void setSyncType(int syncType) {
        this.syncType = syncType;
    }

    public int getUimode() {
        return uimode;
    }

    public void setUimode(int uimode) {
        this.uimode = uimode;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("id"          , id          )
          .append("userId"      , userId      )
          .append("deviceId"    , deviceId    )
          .append("syncSource"  , syncSource  )
          .append("contentType" , contentType )
          .append("syncType"    , syncType    )
          .append("uimode"      , uimode      )
          .append("deliveryTime", deliveryTime);
        return sb.toString();
    }
}
