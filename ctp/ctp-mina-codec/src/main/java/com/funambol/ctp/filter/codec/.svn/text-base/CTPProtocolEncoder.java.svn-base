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

package com.funambol.ctp.filter.codec;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.funambol.ctp.core.Message;
import com.funambol.ctp.core.MessageException;
import com.funambol.ctp.util.MessageConverter;

/**
 * Encodes the outgoing message into a ByteBuffer
 * Collaborates with MessageConverter.
 * @version $Id: CTPProtocolEncoder.java,v 1.2 2007-11-28 11:26:15 nichele Exp $
 */
public class CTPProtocolEncoder extends ProtocolEncoderAdapter {

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor
     */
    public CTPProtocolEncoder() {
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * encode the Object into a ByteBuffer
     * @param ioSession The session associated to the socket connection
     * @param object the objet to encode
     * @param out where to write the encoded ByteBuffer
     */
    public void encode(IoSession ioSession, Object object,
            ProtocolEncoderOutput out) {

        if (!(object instanceof Message)) {
            throw new ProtocolCodecException("Couldn't encode message: " +
                    "Sent object is not a valid Message: " + object.getClass().toString());
        }
        Message message = (Message)object;
        byte[] msg;
        try {
            msg = MessageConverter.getBytesFromMessage(message);
        } catch (MessageException ex) {
            throw new ProtocolCodecException("Couldn't encode message", ex);
        }

        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.setAutoExpand(true); // Enable auto-expand for easier encoding

        buf.putShort((short) msg.length);
        buf.put(msg);
        buf.flip();
        out.write(buf);
    }
}
