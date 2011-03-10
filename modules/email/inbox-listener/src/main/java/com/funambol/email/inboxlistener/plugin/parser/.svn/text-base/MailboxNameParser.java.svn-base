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
package com.funambol.email.inboxlistener.plugin.parser;

import com.funambol.email.util.Def;
import org.apache.log4j.Logger;


/**
 * @version $Id: MailboxNameParser.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */
public class MailboxNameParser implements IUDPMessageParser {

    // --------------------------------------------------------------- Constants

    /** The logger */
    private Logger log = Logger.getLogger(Def.LOGGER_NAME);

    // -------------------------------------------------------------- Properties

    /**  */
    private String prefix;

    private String postfix;

    /** Byte that terminates useful byte sequence wihtin data. */
    private byte terminator = Def.MESSAGE_TERMINATOR;

    // ---------------------------------------------------- Properties accessors

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPostfix(String postfix) {
        this.postfix = postfix;
    }


// ------------------------------------------------------------ Constructors

    /**
     *
     */
    public MailboxNameParser(){
    }

    // ---------------------------------------------------------- Public methods

    /**
     * the parser depends on the specification of the UDP message
     * this parser return the mailboxname.
     * the input message has the format prefix + mailboxname + postfix
     * prefix and postfix are defined in the '....Plugin.xml'
     */
    public KeyAccount parse(byte[] message) throws UDPMessageParserException {

        //
        // recognizes prefix
        //
        byte[] prefixBytes = prefix.getBytes();

        byte current;
        for (int i = 0; i < prefix.length(); i++) {
            current = message[i];
            if (current != prefixBytes[i]) {
                String errmsg = "Invalid prefix: " + new String(message);
                throw new UDPMessageParserException(errmsg);
            }
        }

        //
        // Extracts data from the message.
        //

        // index within message from which data starts
        int dataStartIndex = prefix.length();

        // data within packet
        byte[] data = new byte[Def.PACKET_SIZE];
        int dataLength = 0;

        for (int i = dataStartIndex; i < Def.PACKET_SIZE; i++) {
            current = message[i];
            if (current == terminator){
                break;
            }
            data[dataLength] = current;
            dataLength++;
        }

        // build the KeyAccount object
        String mailboxname = new String(data, 0, dataLength);
        
        KeyAccount key = new KeyAccount();
        if (dataLength >= 1) {
            key.setMsMailboxname(mailboxname);
        }
        
        return key;
    }
}