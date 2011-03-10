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

import java.nio.BufferUnderflowException;

import org.apache.mina.common.BufferDataException;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.funambol.ctp.core.Message;
import com.funambol.ctp.core.MessageException;
import com.funambol.ctp.util.MessageParser;

/**
 * Decodes incoming byte array into Message.
 * Collaborates with MessageParser.
 * @version $Id: CTPProtocolDecoder.java,v 1.2 2007-11-28 11:26:15 nichele Exp $
 */
public class CTPProtocolDecoder extends CumulativeProtocolDecoder {

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor
     */
    public CTPProtocolDecoder() {
    }

    // ------------------------------------------------------- Protected Methods

    /**
     * Called by Mina framework when a byte array is readed from socket channel
     * @param ioSession the session associated to the connection
     * @param in the ByteBuffer readed from SocketChannel
     * @param out where to write the decoded message
     * @return true if the ByteBuffer is correctly decoded,
     *         false if the bytes are not completely received
     */
    protected boolean doDecode(IoSession ioSession,
                               ByteBuffer in,
                               ProtocolDecoderOutput out) {

        if (!in.prefixedDataAvailable(CTPProtocolConstants.PREFIX_LENGTH, CTPProtocolConstants.MAX_MESSAGE_SIZE)) {
            return false;
        }
        try {

            Message message = MessageParser.getMessageFromBytes(getByteArray(CTPProtocolConstants.PREFIX_LENGTH, in));
            out.write(message);
        } catch (IllegalArgumentException ex) {
            throw new ProtocolCodecException("Couldn't decode message", ex);
        } catch (BufferDataException ex) {
            throw new ProtocolCodecException("Couldn't decode message", ex);
        } catch (BufferUnderflowException ex) {
            throw new ProtocolCodecException("Couldn't decode message", ex);
        } catch (MessageException ex) {
            throw new ProtocolCodecException("Couldn't decode message", ex);
        }

        return true;
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Extract from the ByteBuffer the subset of bytes corresponding to a message
     * @param prefixLength the number of bytes used as prefix of a message containing the message length
     * @param byteBuffer the ByteBuffer readed by the Mina framework
     * @return the byte array corresponding to a complete message
     * @throws java.nio.BufferUnderflowException 
     * @throws java.lang.IllegalArgumentException 
     * @throws org.apache.mina.common.BufferDataException 
     */
    private byte[] getByteArray(int prefixLength, ByteBuffer byteBuffer)
    throws BufferUnderflowException, IllegalArgumentException, BufferDataException {

        if (!byteBuffer.prefixedDataAvailable(prefixLength)) {
            throw new BufferUnderflowException();
        }

        int length = 0;
        switch (prefixLength) {
            case 1:
                length = byteBuffer.getUnsigned();
                break;
            case 2:
                length = byteBuffer.getUnsignedShort();
                break;
            case 4:
                length = byteBuffer.getInt();
                break;
            default:
                throw new IllegalArgumentException("prefixLength is not valid: " +
                        prefixLength);
        }
        if (length <= CTPProtocolConstants.MIN_MESSAGE_SIZE) {
            throw new BufferDataException("Object length should be greater than " +
                    CTPProtocolConstants.MIN_MESSAGE_SIZE + " but is " + length);
        }

        int oldLimit = byteBuffer.limit();
        byteBuffer.limit(byteBuffer.position() + length);

        java.nio.ByteBuffer result = java.nio.ByteBuffer.allocate(length);
        result.put(byteBuffer.buf());

        byteBuffer.limit(oldLimit);
        return result.array();
    }
}
