/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.email.util.token;

import com.funambol.framework.tools.Base64;
import java.security.SecureRandom;

/**
 * A generator of tokens used by attachment system.
 * @version $Id: TokenGenerator.java,v 1.1 2008-06-03 16:18:59 testa Exp $
 */
public class TokenGenerator {

    // --------------------------------------------------------------- Constants
    private static final char SEPARATOR = ':';
    // ------------------------------------------------------------ Private data
    /** Sequence used in token creation to generate the unique_index. */
    private static TokenSequence sequence;
    /** SecureRandom object used in token creation to generate the ticket part of */
    private static SecureRandom random;

    static {
        try {            
            random = SecureRandom.getInstance("SHA1PRNG");            
        } catch (Exception e) {
            throw new RuntimeException("Unable to create a SecureRandom", e);
        }
    }
    
    // ------------------------------------------------------------ Constructors
    public TokenGenerator(TokenSequence sequence){        
        this.sequence = sequence;
    }
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Creates a token.
     * <p/>
     * A token is formed as follows: Base64([ticket]:[unique_index])
     * where ticket is a random 16 bytes string and unique_index is a long number
     * taken from a unique sequence.
     * 
     * @return a token
     * @throws com.funambol.email.util.token.TokenException 
     */
    public byte[] getToken() throws TokenException {

        byte[] token = new byte[16 + 1 + 8];
        
        //
        // fill the ticket part 
        //
        byte[] ticket = getRandomTicket(16);

        for (int i = 0; i < ticket.length; i++) {
            token[i] = ticket[i];
        }
        
        //
        // fill the separator
        //
        token[ticket.length] = SEPARATOR;
        
        //
        // fill the sequence part
        //
        long seq = sequence.next();
        
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            b[7 - i] = (byte) (seq >>> (i * 8));
        }
        
        for (int i = 0; i < b.length; i++) {
            token[ticket.length + 1 + i] = b[i]; 
        }

        return Base64.encode(token);
    }

    /**
     * Returns a random byte array ticket. The tikets bytes
     * are guaranteed to be in the printable range from ascii 32 to 128.
     *
     * @param length The length of the returned byte array
     * @return a new n bytes long random array
     */
    public static byte[] getRandomTicket(int length) {
        byte[] ticket = new byte[length];
        random.nextBytes(ticket);

        int i;
        for (int j = 0; j < ticket.length; ++j) {
            i = ticket[j] & 0x000000ff;
            if ((i < 32) || (i > 128)) {
                ticket[j] = (byte) (32 + (i % 64));
            }
        }

        return ticket;
    }
}
