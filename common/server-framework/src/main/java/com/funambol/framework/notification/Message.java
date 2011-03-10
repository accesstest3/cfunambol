/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.framework.notification;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a message to send.
 *
 * @version $Id: Message.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class Message implements java.io.Serializable {

    // --------------------------------------------------------------- Constants
    public enum Type {
        STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE,
        STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE
    }

    public final static int STANDARD_1_1_TYPE = 1;
    public final static int STANDARD_1_2_TYPE = 2;

    // -------------------------------------------------------------- Properties

    /** The message type */
    private Type messageType;

    /**
     * @deprecated Since v71, use getType()
     */
    public Type getMessageType() {
        return messageType;
    }

    /**
     * @deprecated Since v71, use setType(int type)
     */
    public void setMessageType(Type messageType) {
        this.messageType = messageType;

        if (this.messageType != null) {
            switch (this.messageType) {
                case STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE:
                    setType(STANDARD_1_2_TYPE);
                    break;
                case STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE:
                    setType(STANDARD_1_1_TYPE);
                    break;
            }
        }
    }

    /**
     * The 'type' member is used as substitute for messageType when messageType
     * has to be set to null in order to send a Message via Web Services, since
     * there is an issue if the messageType is not null.
     * So in order to send a Message via Web Service the messageType is set to
     * null, and client side when the Message is received, the messageType is
     * set again to its original value in according to the value of 'type'.
     */
    private int type = STANDARD_1_2_TYPE;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;

        restoreMessageTypeToType();
    }

    /**
     * If the messageType is null then set it to the correct value in according
     * to the value of 'type'.
     */
    public void setMessageTypeToType() {
        if (messageType == null) {
            restoreMessageTypeToType();
        }
    }

    /**
     * Restore the messageType value in according to the value of 'type'.
     */
    private void restoreMessageTypeToType() {
        switch (this.type) {
            case STANDARD_1_2_TYPE:
                messageType = Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE;
                break;
            case STANDARD_1_1_TYPE:
                messageType = Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE;
                break;
        }
    }

    /** The message content */
    private byte[] messageContent;

    public byte[] getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(byte[] messageContent) {
        this.messageContent = messageContent;
    }

    /** The array of SyncSources to notify */
    private String[] syncSources;

    public String[] getSyncSources() {
        return syncSources;
    }

    public void setSyncSources(String[] syncSources) {
        this.syncSources = syncSources;
    }

    /**
     * Creates a new instance of Message.
     *
     * Note that this constructor is required to use this class via WS (on some
     * platform without this default constructor, WS doesn't work)
     */
    public Message() {
        this(STANDARD_1_2_TYPE, null, null);
    }

    /** Creates a new instance of Message
     * @param type The type of the message
     * @param message The message
     */
    public Message(int type, byte[] message) {
        this(type, message, null);
    }

    /** Creates a new instance of Message
     *
     * @param messageType The type of the message
     * @param message The message
     * @deprecated Since v71, use constructor Message(int type, byte[] message)
     */
    public Message(Type messageType, byte[] message) {
        this(messageType, message, null);
    }

    /** Creates a new instance of Message
     *
     * @param type the type of the message
     * @param message the message
     * @param syncSources the array of SyncSources to notify
     */
    public Message(int type, byte[] message, String[] syncSources) {
        setType(type);
        this.messageContent = message;
        this.syncSources    = syncSources;
    }

    /** Creates a new instance of Message
     *
     * @param messageType the type of the message
     * @param message the message
     * @param syncSources the array of SyncSources to notify
     * @deprecated Since v71, use constructor
     *             Message(int type, byte[] message, String[] syncSources)
     */
    public Message(Type messageType, byte[] message, String[] syncSources) {
        setMessageType(messageType);
        this.messageContent = message;
        this.syncSources    = syncSources;
    }

    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("messageType"   , messageType);
        sb.append("type"          , type);
        sb.append("messageContent", messageContent);
        sb.append("syncSources"   , syncSources);
        return sb.toString();
    }
}
