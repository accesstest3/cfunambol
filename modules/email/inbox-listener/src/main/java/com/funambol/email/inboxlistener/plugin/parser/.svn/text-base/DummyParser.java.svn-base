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
 * <code>parse</code> method of this parser does nothing on the incoming message.
 * It simply waits for some time and then exits.
 *
 * @version $Id: DummyParser.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */
public class DummyParser implements IUDPMessageParser {
    
    // --------------------------------------------------------------- Constants
    
    /** The logger */
    private Logger log = Logger.getLogger(Def.LOGGER_NAME);
    
    // -------------------------------------------------------------- Properties
    
    /** Execution time in ms. */
    private long executionTime;
    
    // ---------------------------------------------------- Properties accessors
    
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
    
    // ------------------------------------------------------------ Constructors
    
    /** Creates a new instance of a <code>DummyParser</code>.*/
    public DummyParser(){
    }
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * This method does nothing on the incoming message. It simply waits for some
     * time and then exits.
     *
     * @param message incoming message
     * @throws com.funambol.email.inboxlistener.plugin.parser.UDPMessageParserException
     * @return a null <code>KayAccount</code> object (that is: a <code>KayAccount</code>
     * object with properties set to <code>null</code>)
     */
    public KeyAccount parse(byte[] message) throws UDPMessageParserException {
        
        long currentTime = System.currentTimeMillis();
        long startTime   = currentTime;
        long endTime     = currentTime + executionTime;
        
        while (currentTime <= endTime) {
            currentTime = System.currentTimeMillis();
        }
        
        if (log.isTraceEnabled()) {
            log.trace("execution time: " + (currentTime - startTime));
        }
        
        return new KeyAccount();
    }
}












