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

/**
 *  This class provides some facility methods to handle events.
 *
 * @version $Id: EventHelper.java 34824 2010-06-24 17:30:39Z luigiafassina $
 */

public class EventHelper {

     
    /**
     * This method is used to match events.
     * Input events must be of the same class and any field
     * must match.
     *
     * Note: timestamp field is ignored
     */
    
    public static boolean compareEvent(Event lastEvent, Event expectedEvent) {
        // handling null case
        if(lastEvent==null)
            return expectedEvent==null;

        // checking event types
        if(!lastEvent.getClass().equals(expectedEvent.getClass())) {
            System.out.println("Event are of different type: expected ["+expectedEvent.getClass()+"] result is ["+lastEvent.getClass()+"].");
            return false;
        }

        // checking event fields
        if(areFieldsDifferent(lastEvent.getDeviceId(),expectedEvent.getDeviceId(),"Device id"))
            return false;
        if(areFieldsDifferent(lastEvent.getEventType(),expectedEvent.getEventType(),"Event type"))
            return false;
        if(areFieldsDifferent(lastEvent.getStatusCode(),expectedEvent.getStatusCode(),"Status code"))
            return false;
        if(areFieldsDifferent(lastEvent.getLoggerName(),expectedEvent.getLoggerName(),"Logger name"))
            return false;
        if(areFieldsDifferent(lastEvent.getMessage(),expectedEvent.getMessage(),"Message"))
            return false;
        if(areFieldsDifferent(lastEvent.getNumAddedItems(),expectedEvent.getNumAddedItems(),"Number of added items"))
            return false;
        if(areFieldsDifferent(lastEvent.getNumDeletedItems(),expectedEvent.getNumDeletedItems(),"Number of deleted items"))
            return false;
        if(areFieldsDifferent(lastEvent.getNumTransferredItems(),expectedEvent.getNumTransferredItems(),"Number of transferred items"))
            return false;
        if(areFieldsDifferent(lastEvent.getNumUpdatedItems(),expectedEvent.getNumUpdatedItems(),"Number of updated items"))
            return false;
        if(areFieldsDifferent(lastEvent.getOriginator(),expectedEvent.getOriginator(),"Originator"))
            return false;
        if(areFieldsDifferent(lastEvent.getSessionId(),expectedEvent.getSessionId(),"Session id"))
            return false;
        if(areFieldsDifferent(lastEvent.getSource(),expectedEvent.getSource(),"Source"))
            return false;
        if(areFieldsDifferent(lastEvent.getSyncType(),expectedEvent.getSyncType(),"Sync type"))
            return false;
        if(areFieldsDifferent(lastEvent.getUserName(),expectedEvent.getUserName(),"User name"))
            return false;

        return true;
    }

    private static boolean areFieldsDifferent(String lastEventField, String expectedEventField, String fieldName) {
        if(lastEventField==null) {
            if(expectedEventField!=null) {
                System.out.println("Field "+fieldName+" is null for last event while expected value is ["+expectedEventField+"].");
                return true;
            } else
                return false;
        }


        if(!lastEventField.equals(expectedEventField)) {
            System.out.println("Field "+fieldName+" has value ["+lastEventField+"] while expected value is ["+expectedEventField+"].");
            return true;
        }

        return false;
    }

    private static boolean areFieldsDifferent(int lastEventField, int expectedEventField, String fieldName) {
            if(lastEventField!=expectedEventField) {
                System.out.println("Field "+fieldName+" has value ["+lastEventField+"] while expected value is ["+expectedEventField+"].");
                return true;
            }

        return false;
    }


}
