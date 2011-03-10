/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.email.transport  ;

import com.funambol.email.util.Def;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;



/**
 *
 * <p>
 *    This class implemets Semaphore in order to permit
 *    to manager layer to retreive the UID of the added
 *    message.
 * </p>
 *
 * @version $Id: ImapSemaphore.java,v 1.2 2008-05-13 10:09:53 gbmiglia Exp $
 */
public final class ImapSemaphore
{

    /**     */
    protected static FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    private boolean isGreen;


     /**
      * Create a new condition variable in a known state.
      *
      * @param _isGreen boolean
      */
     public ImapSemaphore(boolean _isGreen){
        isGreen = _isGreen;
     }

     /**
      * See if the condition variable is true (without releasing).
      *
      * @return boolean
      */
     public synchronized boolean isTrue()  {
        return isGreen;
     }

     /**
      * Set the condition to false. Waiting threads are not affected.
      */
     public synchronized void setFalse()   {
         isGreen = false;
     }

     /**
      * Set the condition to true. Waiting threads are not released.
      */
     public synchronized void setTrue() {
       isGreen = true;
       notifyAll();
     }

     /**
      * Release all waiting threads without setting the condition true
      */
     public synchronized void releaseAll(){
       notifyAll();
     }

     /**
      * Release one waiting thread without setting the condition true
      */
     public synchronized void releaseOne(){
        notify();
     }

     /**
      * Wait for the condition to become true.
      * @param timeout Timeout in milliseconds
      */
     public synchronized void waitForTrue( long timeout ) {
         try {
           if( !isGreen ){
               wait( timeout );
           }
         } catch (InterruptedException ie){
            log.error("Error in method waitForTrue", ie);
         }
     }

}
