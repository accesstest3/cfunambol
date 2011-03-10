/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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
package com.funambol.server.session;

import com.funambol.framework.server.Event;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 *  This class is a fake appender. It's only used to keep
 *  the last traced event in order to verify that the logged
 *  event is equal to the expected event.
 *
 * @version $Id: TestAppender.java 30675 2009-04-01 14:39:23Z filmac $
 */
public class TestAppender extends  AppenderSkeleton {

    private Event lastEvent;

    /**
     * Create a new TestAppender object.
     */
    public TestAppender() {
        this.lastEvent = null;
    }

    @Override
    protected void append(LoggingEvent event) {
        lastEvent = null;
        if(event!=null) {
            Object obj = event.getMessage();
            if(obj!=null && obj instanceof Event) {
                lastEvent = (Event) obj;
            } else
                System.out.println("Logged object is null or is not an event ["+obj+"].");
        } else
            System.out.println("Logging event is null. ");

    }

    @Override
    public boolean requiresLayout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    


    /**
     * This method allows to access to the last event
     * handled by this appender.
     *
     * @return the last event.
     */

    public Event getLastEvent() {
        return this.lastEvent;
    }


    /**
     * This method allows to clears data about the last event
     * handled by this appender.
     */
    
    public void clearLastEvent() {
        this.lastEvent=null;
    }

}
