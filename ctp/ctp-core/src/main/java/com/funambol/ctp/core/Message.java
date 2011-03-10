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
package com.funambol.ctp.core;


/**
 * This represents a CTP Message.
 *
 * @version $Id: Message.java,v 1.2 2007-11-28 11:26:14 nichele Exp $
 */
public class Message {

    // ------------------------------------------------------------ Private data
    private String         protocolVersion;
    private GenericCommand command        ;

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new instance of Message.
     */
    public Message() {
    }

    /**
     * Creates a new instance of Message with the protocol version and a
     * command.
     *
     * @param protocolVersion the protocol version
     * @param command the command
     */
    public Message(String protocolVersion, GenericCommand command) {
        this.protocolVersion = protocolVersion;
        this.command         = command        ;
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Sets the protocol version.
     *
     * @param protocolVersion the protocol version
     */
    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /**
     * Returns the protocol version.
     *
     * @return the protocol version
     */
    public String getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Sets the command.
     *
     * @param command the command
     */
    public void setCommand(GenericCommand command) {
        this.command = command;
    }

    /**
     * Returns the command.
     *
     * @return the command
     */
    public GenericCommand getCommand() {
        return command;
    }

    /**
     * Compares <code>this</code> object with input object to establish if are
     * the same object.
     *
     * @param obj the object to compare
     * @return true if the objects are equals, false otherwise
     */
    public boolean deepequals(Object obj) {
         if( this == obj ) {
             return true;
         }

         if( obj instanceof Message) {
             Message msg = (Message)obj;

             if ( (msg.getProtocolVersion() == null && this.getProtocolVersion() != null) ||
                  (msg.getProtocolVersion() != null && this.getProtocolVersion() == null) ) {
                 return false;
             }
             if (!msg.getProtocolVersion().equals(this.getProtocolVersion())) {
                 return false;
             }

             if (msg.getCommand() != null && this.getCommand() != null) {

                 return msg.getCommand().deepequals(this.getCommand());

             } else if (msg.getCommand() == null && this.getCommand() == null) {
                 return true;
             }
         }
         return false;
     }


    public String toString() {
        return getProtocolVersion() + " " + getCommand().toString();
    }
}
